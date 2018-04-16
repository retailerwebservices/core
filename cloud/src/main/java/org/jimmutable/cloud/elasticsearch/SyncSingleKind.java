package org.jimmutable.cloud.elasticsearch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.ObjectIdStorageKey;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyHandler;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

/**
 * This class is used to re-index a single Storable & Indexable Kind by doing
 * the following for the Kind passed in:
 * 
 * 1.) Go through every Object in Storage and upsert its matching search
 * document.
 * 
 * 2.) Go through all of the Kind's search index and delete any search document
 * that doesn't have a matching version in Storage.
 * 
 * This is our first pass at re-indexing, it's not perfect. In the near future
 * we hope to take advantage of the protocol elasticsearch lays out here for
 * re-indexing:
 * https://www.elastic.co/guide/en/elasticsearch/guide/current/index-aliases.html
 * 
 * @author avery.gonzales
 */
public class SyncSingleKind implements Runnable
{
	private static final Logger logger = LogManager.getLogger(SyncSingleKind.class);
	public static final int MAX_ELASTIC_SEARCH_RESULTS = 10_000;
	private static final double MAX_UNSYNCED_SEARCH_DOCUMENT_THRESHOLD = .90; //Our search should be within 10% sync of our Storage

	private Kind kind; //Required and must be both Indexable and Storable for any actions to be taken
	private SearchIndexDefinition index_definition;

	public SyncSingleKind(Kind kind) throws ValidationException
	{
		this.kind = kind;
		this.index_definition = SearchSync.getSimpleAllRegisteredIndexableKindsMap().getOrDefault(kind, null);
		validate();
	}
	
	private void validate()
	{
		if(this.kind == null) throw new ValidationException("No Kind was passed in, kind is required");
		if(!SearchSync.getSimpleAllRegisteredIndexableKinds().contains(kind)) throw new ValidationException("Kind %s was not registered with SearchSync.registerIndexableKind()");
		if(this.index_definition == null) throw new ValidationException("No IndexDefinition was passed in, IndexDefinition is required");
	}

	/**
	 * This will kick off the process of syncing the Kind's Storage with Search
	 */
	@Override
	public void run()
	{
		logger.info("Reindexing of Kind " + kind + " started");
		boolean success = syncSearchAndStorage();
		if (success)
		{
			try
			{
				deleteSearchDocumentsThatAreNotInStorage();
			}
			catch (Exception e)
			{
				logger.fatal("FATALITY: Unable to reindex Kind " + kind + " because Search was greater than 10% out of sync with Storage", e);
				logger.fatal("In able to preserve data integrity this process will now shutdown");
				System.exit(1);
			}
		}
		logger.info("Reindexing of Kind " + kind + " finished");
	}

	/**
	 * This will scan all of Storage for the Kind passed in and for each item
	 * found it will attempt to upsert its matching search document.
	 * 
	 * @return true on success
	 */
	private boolean syncSearchAndStorage()
	{
		if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().scan(kind, new UpsertDataHandler(), 10))
		{
			logger.warn("Storage scanner for Kind " + kind + " was unable to successfully run. This Kind may not be fully re-indexed or there may currently not be any entries of Kind in Storage.");
			return false;
		}
		return true;
	}

	/**
	 * This handles checking an individual file in Storage. It will check that
	 * the item is able to be deserialized as a Storable and Indexable. If it is
	 * we will set our Kind's Indexable to match that of the Object deserialized
	 * if not yet set. Then attempt to upsert the single Object's search
	 * document into Search.
	 */
	private class UpsertDataHandler implements StorageKeyHandler
	{
		private UpsertDataHandler(){}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void handle(StorageKey key)
		{
			byte[] bytes = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().getCurrentVersion(key, null);

			GenericStorableAndIndexable<?> obj = null;
			try
			{
				obj = new GenericStorableAndIndexable(bytes);
			}
			catch (Exception e)
			{
				logger.error("This object was unable to be deserialized as a Storable and Indexable object...", e);
				return;
			}
			
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument((Indexable) obj.getObject());
		}
	}

	/**
	 * This method handles the logic of now iterating through search and
	 * ensuring that each Search document has a matching item in Storage. If it
	 * doesn't, it must be delete from Search.
	 */
	private void deleteSearchDocumentsThatAreNotInStorage() throws Exception
	{
		//Known deficiency here with our current Search interface only allowing for first 10k results even when search window is smaller than 10k. We are aware and an iteration on Search is coming for it.
		StandardSearchRequest search_request = new StandardSearchRequest("*", MAX_ELASTIC_SEARCH_RESULTS, 0);
		JSONServletResponse json_servlet_response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(index_definition.getSimpleIndex(), search_request);
		if (json_servlet_response instanceof SearchResponseOK)
		{
			//We want to ensure that we never are deleting more than 10% of our search documents in order to match Storage
			//If this happens we want to exit the process completely and send a signal about this failing.
			List<OneSearchResult> results = ((SearchResponseOK) json_servlet_response).getSimpleResults();
			int acceptable_unsyced_documents_threshold = (int) Math.round(results.size() * MAX_UNSYNCED_SEARCH_DOCUMENT_THRESHOLD);
			Set<String> documents_to_delete = extractCountOfDeleteableDocuments(results);
			int number_of_documents_to_delete = documents_to_delete.size();
			int search_results_size_if_documents_deleted = results.size() - number_of_documents_to_delete;
			
			if(search_results_size_if_documents_deleted < acceptable_unsyced_documents_threshold)
			{
				throw new Exception("FATAL ERROR: the kind " + kind + " Storage and Search are more than 10% out of sync. Expected at least " + Math.round(results.size() * .9) + " search documents, but found " + search_results_size_if_documents_deleted);
			}
			
			for(String deletable_key : documents_to_delete)
			{
				logger.info("Key: " + deletable_key + " existed in search but not in storage for Kind " + kind + ". Deleting result from search.");
				//this is not in storage but only in search.
				
				//Problem, we don't have a guarantee that this searchdocumentId will be the ObjectId in search but we need to have that guarantee to check against. However, we don't have another way to get it currently...
				//Okay'd this issue with Jeff for now though since we will very likely be rewriting this logic once our Elasticsearch infastructure allows for re-indexing in a more properly fashion.				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(index_definition.getSimpleIndex(), new SearchDocumentId(deletable_key));
			}
		}
		else
		{
			logger.error("Invalid query given for Kind: " + kind + ". Unable to finish checking if deletion is need.");
		}
	}

	
	/**
	 * This will go through and get the count of documents that would be deleted
	 * to make a given search index match Storage.
	 */
	private Set<String> extractCountOfDeleteableDocuments(List<OneSearchResult> results)
	{
		Set<String> deletable_keys = new HashSet<>();
		for (OneSearchResult result : results)
		{
			//We can't rely on the Indexable.SearchDocumentId because according to the docs this value isn't always guaranteed be the ObjectId
			//So this needs to be implemented long term otherwise we may have indices that don't have the object id set as id
			String cur_object_id = result.getSimpleContents().getOrDefault(ObjectId.FIELD_OBJECT_ID.getSimpleFieldName(), null);

			ObjectId id = null;
			try
			{
				id = new ObjectId(cur_object_id);
			}
			catch(Exception e)
			{
				logger.error("Could not extract ObjectId from results ", e);
				continue;
			}

			StorageKey key = new ObjectIdStorageKey(kind, id, Storable.STORABLE_EXTENSION);

			//If it doesn't exist in storage, we should remove it from search
			if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().exists(key, false))
			{
				deletable_keys.add(id.getSimpleValue());
			}
		}
		
		return deletable_keys;
	}
	
	/**
	 * This is a simple class to handle deserializing any Kind's Object and
	 * ensuring that the Kind's Object is both Indexable and Storable.
	 */
	private static class GenericStorableAndIndexable<T>
	{
		private T object;

		@SuppressWarnings("unchecked")
		public GenericStorableAndIndexable(byte[] bytes) throws ValidationException
		{
			StandardObject<?> obj = null;
			try
			{
				obj = StandardObject.deserialize(new String(bytes));
			}
			catch (Exception e)
			{
				throw new ValidationException("Unable to deserialize object", e);
			}

			//Broken out this way, rather than just deserializing T so that we know exactly what a
			if (!(obj instanceof Storable))
			{
				throw new ValidationException("Object " + obj.getTypeName() + " is unable to be reindexed since it is not a Storable.");
			}
			if (!(obj instanceof Indexable))
			{
				throw new ValidationException("Object " + obj.getTypeName() + " is unable to be reindexed since it is not a Indexable.");
			}

			this.object = (T) obj;
		}

		public T getObject()
		{
			return object;
		}
	}
}
