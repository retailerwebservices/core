package org.jimmutable.aws.elasticsearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Utility class for search Index maintenance. Should be used on startup of
 * application to make sure indices are correct.
 *
 * 
 * @author trevorbox
 *
 */

// TODO we need to come to a consensus on how to manage everything. Creating a
// single client takes 16 seconds so it would be better to simply have a
// centrally managed solution separated from this class i think

// CODE REVEIW: Generally, I think the lifecycle management (opening and closing
// clients) needs to be managed *for* the user. i.e. its bad that if you forget
// to call closeClient resources are not freed etc. I suggest making the
// constructor private and having public static methods to perform the logical
// operations needed for others...

public class SearchIndexConfigurationUtils
{

	private static final Logger logger = LogManager.getLogger(SearchIndexConfigurationUtils.class);

	private TransportClient client;

	private static final String ELASTICSEARCH_DEFAULT_TYPE = Indexable.DEFAULT_TYPE;

	/**
	 *
	 * @param client
	 */
	public SearchIndexConfigurationUtils(TransportClient client)
	{
		this.client = client;
	}

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            IndexDefinition
	 * @return boolean if the index exists or not
	 */
	public boolean indexExists(IndexDefinition index)
	{
		if (index == null) {
			logger.fatal("Cannot check the existence of a null Index");
			return false;
		}
		try {
			return client.admin().indices().prepareExists(index.getSimpleValue()).get().isExists();
		} catch (Exception e) {
			logger.log(Level.FATAL, "Failed to check if index exists", e);
			return false;
		}
	}

	/**
	 * Test if the index exists or not
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index exists or not
	 */
	public boolean indexExists(SearchIndexDefinition index)
	{
		if (index == null) {
			logger.fatal("Cannot check the existence of a null Index");
			return false;
		}
		try {
			return client.admin().indices().prepareExists(index.getSimpleIndex().getSimpleValue()).get().isExists();
		} catch (Exception e) {
			logger.log(Level.FATAL, "Failed to check if index exists", e);
			return false;
		}
	}

	/**
	 * An index is properly configured if it exists and its field names and
	 * datatypes match
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index is properly configured or not
	 */
	public boolean indexProperlyConfigured(SearchIndexDefinition index)
	{

		if (index == null) {
			return false;
		}

		if (indexExists(index)) {

			// compare the expected index fields to the actual index fields
			Map<String, String> expected = new HashMap<String, String>();
			index.getSimpleFields().forEach(fields -> {
				expected.put(fields.getSimpleFieldName().getSimpleName(), fields.getSimpleType().getSimpleCode());
			});

			try {
				GetMappingsResponse response = client.admin().indices().prepareGetMappings(index.getSimpleIndex().getSimpleValue()).get();

				String json = response.getMappings().get(index.getSimpleIndex().getSimpleValue()).get(ELASTICSEARCH_DEFAULT_TYPE).source().string();

				Map<String, String> actual = new HashMap<String, String>();

				new ObjectMapper().readTree(json).get(ELASTICSEARCH_DEFAULT_TYPE).get("properties").fields().forEachRemaining(fieldMapping -> {
					actual.put(fieldMapping.getKey(), fieldMapping.getValue().get("type").asText());
				});
				return expected.equals(actual);

			} catch (Exception e) {
				logger.log(Level.FATAL, String.format("Failed to get the index mapping for index %s", index.getSimpleIndex().getSimpleValue()), e);
			}
		}

		return false;

	}

	private boolean createIndex(SearchIndexDefinition index)
	{
		if (index == null) {
			logger.fatal("Cannot create a null Index");
			return false;
		}

		try {

			XContentBuilder mappingBuilder = jsonBuilder();
			mappingBuilder.startObject().startObject(ELASTICSEARCH_DEFAULT_TYPE).startObject("properties");
			for (SearchIndexFieldDefinition field : index.getSimpleFields()) {
				mappingBuilder.startObject(field.getSimpleFieldName().getSimpleName());
				mappingBuilder.field("type", field.getSimpleType().getSimpleCode());
				mappingBuilder.endObject();
				// https://www.elastic.co/blog/strings-are-dead-long-live-strings
				if (field.getSimpleType().equals(SearchIndexFieldType.OBJECTID)) {
					mappingBuilder.startObject("fields").startObject("keyword");
					mappingBuilder.field("type", "keyword");
					mappingBuilder.field("ignore_above", 256);
					mappingBuilder.endObject().endObject();
				}
			}
			mappingBuilder.endObject().endObject().endObject();

			CreateIndexResponse createResponse = client.admin().indices().prepareCreate(index.getSimpleIndex().getSimpleValue()).addMapping(ELASTICSEARCH_DEFAULT_TYPE, mappingBuilder).get();

			if (!createResponse.isAcknowledged()) {
				logger.fatal(String.format("Index Creation not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		} catch (IOException e) {
			logger.log(Level.FATAL, String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			return false;
		}

		logger.info("Created index %s", index.getSimpleIndex().getSimpleValue());
		return true;
	}

	private boolean deleteIndex(SearchIndexDefinition index)
	{
		if (index == null) {
			logger.fatal("Cannot delete a null Index");
			return false;
		}

		try {
			DeleteIndexResponse deleteResponse = client.admin().indices().prepareDelete(index.getSimpleIndex().getSimpleValue()).get();
			if (!deleteResponse.isAcknowledged()) {
				logger.fatal(String.format("Index Deletion not acknowledged for index %s", index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		} catch (Exception e) {
			logger.fatal(String.format("Index Deletion failed for index %s", index.getSimpleIndex().getSimpleValue()));
			return false;
		}
		logger.info("Deleted index %s", index.getSimpleIndex().getSimpleValue());
		return true;

	}

	/**
	 * Upsert if the index doesnt exist or is not properly configured already
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the upsert was successful or not
	 */
	public boolean upsertIndex(SearchIndexDefinition index)
	{

		if (index == null) {
			logger.fatal("Cannot upsert a null Index");
			return false;
		}

		// if it exists and is not configured correctly delete and add
		if (indexExists(index)) {
			if (!indexProperlyConfigured(index)) {
				if (deleteIndex(index)) {
					return createIndex(index);
				} else {
					// deletion failed
					return false;
				}
			}
		} else {
			// index is new
			return createIndex(index);
		}

		// index exists and already configured correctly
		logger.info(String.format("No upsert needed for index %s", index.getSimpleIndex().getSimpleValue()));
		return true;
	}

}
