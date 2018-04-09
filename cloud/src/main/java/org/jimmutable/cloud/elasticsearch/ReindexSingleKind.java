package org.jimmutable.cloud.elasticsearch;

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
 * @author avery.gonzales
 */
public class ReindexSingleKind implements Runnable
{
	private static final Logger logger = LogManager.getLogger(ReindexSingleKind.class);
	public static final int MAX_ELASTIC_SEARCH_RESULTS = 10_000;

	Kind kind; //Required and must be both Indexable and Storable for any actions to be taken

	public ReindexSingleKind(Kind kind) throws ValidationException
	{
		this.kind = kind;
		validate();
	}
	
	private void validate()
	{
		if(this.kind == null) throw new ValidationException("No Kind was passed in, kind is required");
	}

	/**
	 * This will kick off the process of syncing the Kind's Storage with Search
	 */
	@Override
	public void run()
	{
		logger.info("Reindexing of Kind " + kind + " started");
		IndexDefinition kinds_index_definition = syncSearchAndStorage(kind, null);
		if (kinds_index_definition != null)
		{
			deleteSearchDocumentsThatAreNotInStorage(kind, kinds_index_definition);
		}
		logger.info("Reindexing of Kind " + kind + " finished");
	}

	/**
	 * This will scan all of Storage for the Kind passed in and for each item
	 * found it will attempt to upsert its matching search document
	 */
	private static IndexDefinition syncSearchAndStorage(Kind kind, IndexDefinition default_value)
	{
		
		UpsertDataHandler data_handler = new UpsertDataHandler();
		if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().scan(kind, data_handler, 10))
		{
			logger.error("Storage scanner for Kind " + kind + " was unable to successfully run. This Kind may not be fully re-indexed.");
			return default_value;
		}

		return data_handler.getOptionalKindIndexDefinition(default_value);
	}

	/**
	 * This handles checking an individual file in Storage. It will check that
	 * the item is able to be deserialized as a Storable and Indexable. If it is
	 * we will set our Kind's Indexable to match that of the Object deserialized
	 * if not yet set. Then attempt to upsert the single Object's search
	 * document into Search.
	 */
	private static class UpsertDataHandler implements StorageKeyHandler
	{
		private IndexDefinition kinds_index_definition;
		private UpsertDataHandler(){}

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
				logger.error("This object was unable to be deserialized as a Storable and Indexable object...");
				e.printStackTrace();
				return;
			}

			//We get the kind's index definition at this point because Kind's don't have relation to a definition until paired in an Object
			if (this.kinds_index_definition == null) this.kinds_index_definition = obj.getSimpleIndexDefinition();

			//If we don't have search index for this Indexable Object we shouldn't be the ones creating it.
			//This script is to only deal with what has already been made.
			if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(obj.getSimpleIndexDefinition()))
			{
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument((Indexable) obj.getObject());
			}
			else
			{
				logger.error("The index for Kind " + obj.getSimpleKind() + " had not been previously created. No re-indexing done on it.");
			}
		}

		public IndexDefinition getOptionalKindIndexDefinition(IndexDefinition default_value)
		{
			if (this.kinds_index_definition == null) return default_value;

			return this.kinds_index_definition;
		}
	}

	/**
	 * This method handles the logic of now iterating through search and
	 * ensuring that each Search document has a matching item in Storage. If it
	 * doesn't, it must be delete from Search.
	 */
	private static void deleteSearchDocumentsThatAreNotInStorage(Kind kind, IndexDefinition definition)
	{
		//This was broken out so it'd be easy to iterate over searches with more than 10k results however
		//We still need a solution for getting searches of over 10,000 obj, currently if you have over 10k results and try to start at any result that is above 10k it throws the following exception 
		//Caused by: org.elasticsearch.search.query.QueryPhaseExecutionException: Result window is too large, from + size must be less than or equal to: [10000] but was [10001]. See the scroll api for a more efficient way to request large data sets. This limit can be set by changing the [index.max_result_window] index level setting.
		StandardSearchRequest search_request = new StandardSearchRequest("*", MAX_ELASTIC_SEARCH_RESULTS, 0);
		JSONServletResponse json_servlet_response = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(definition, search_request);
		if (json_servlet_response instanceof SearchResponseOK)
		{
			deleteSearchDoc(kind, definition, (SearchResponseOK) json_servlet_response);
		}
		else
		{
			logger.error("Invalid query given for Kind: " + kind + ". Unable to finish checking if deletion is need.");
		}
	}

	/**
	 * For all the results given from our current search we will check Storage
	 * to ensure each one has a matching file and delete from search if it does
	 * not.
	 */
	private static void deleteSearchDoc(Kind kind, IndexDefinition definition, SearchResponseOK results)
	{
		for (OneSearchResult result : results.getSimpleResults())
		{
			//Code Review: Thoughts on this?
			//TODO This needs to be implemented long term otherwise we may have indices that don't have the object id set as id
			String cur_object_id = result.getSimpleContents().getOrDefault(ObjectId.FIELD_OBJECT_ID.getSimpleFieldName(), null);
			//Try catch fail here
			ObjectId id = new ObjectId(cur_object_id);

			StorageKey key = new ObjectIdStorageKey(kind, id, Storable.STORABLE_EXTENSION);

			//If it doesn't exist in storage, we should remove it from search
			if (!CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().exists(key, false))
			{
				logger.info("Key: " + key + " existed in search but not in storage for Kind " + kind + ". Deleting result from search.");
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(definition, new SearchDocumentId(id.getSimpleValue()));
			}
		}
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

		public IndexDefinition getSimpleIndexDefinition()
		{
			return ((Indexable) object).getSimpleSearchIndexDefinition();
		}

		public Kind getSimpleKind()
		{
			return ((Storable) object).getSimpleKind();
		}
	}
}
