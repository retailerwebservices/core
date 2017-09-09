package org.jimmutable.aws.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Utility class for search Index maintenance. Should be used on startup of
 * application that cares about properly configured search indices.
 * 
 * @author trevorbox
 *
 */
public class SearchIndexConfigurationUtils
{

	private static final Logger logger = Logger.getLogger(SearchIndexConfigurationUtils.class.getName());

	private TransportClient client;

	private static final String ELASTICSEARCH_DEFAULT_TYPE = "default";

	@SuppressWarnings("resource")
	public SearchIndexConfigurationUtils(ElasticSearchEndpoint endpoint)
	{
		// set cluster name?

		Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();

		try {

			// long start = System.currentTimeMillis();
			client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(
					InetAddress.getByName(endpoint.getSimpleHost()), endpoint.getSimplePort()));

			// System.out.println(String.format("TransportClient took %s seconds to start",
			// (System.currentTimeMillis() - start) * 0.001));

		} catch (UnknownHostException e) {
			String errorMessage = String.format("Failed to create a TransportClient from endpoint %s:%d",
					endpoint.getSimpleHost(), endpoint.getSimplePort());
			logger.log(Level.SEVERE, errorMessage, e);
			throw new RuntimeException(errorMessage);
		}

	}

	/**
	 * 
	 * @param index
	 *            IndexDefinition
	 * @return boolean if the index exists or not
	 */
	public boolean indexExists(IndexDefinition index)
	{
		if (index == null) {
			logger.severe("Cannot check the existence of a null Index");
			return false;
		}
		return client.admin().indices().prepareExists(index.getSimpleValue()).get().isExists();
	}

	/**
	 * 
	 * @param index
	 *            SearchIndexDefinition
	 * @return boolean if the index exists or not
	 */
	public boolean indexExists(SearchIndexDefinition index)
	{
		if (index == null) {
			logger.severe("Cannot check the existence of a null Index");
			return false;
		}
		return client.admin().indices().prepareExists(index.getSimpleIndex().getSimpleValue()).get().isExists();
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

			GetMappingsResponse response = client.admin().indices()
					.prepareGetMappings(index.getSimpleIndex().getSimpleValue()).get();

			String json = null;
			try {
				json = response.getMappings().get(index.getSimpleIndex().getSimpleValue())
						.get(ELASTICSEARCH_DEFAULT_TYPE).source().string();
			} catch (IOException e) {

				logger.log(Level.SEVERE, String.format("Failed to get the _mapping json for index %s",
						index.getSimpleIndex().getSimpleValue()), e);
				return false;
			}

			Map<String, String> actual = new HashMap<String, String>();

			try {
				new ObjectMapper().readTree(json).get(ELASTICSEARCH_DEFAULT_TYPE).get("properties").fields()
						.forEachRemaining(action -> {
							actual.put(action.getKey(), action.getValue().get("type").asText());
						});
			} catch (Exception e) {
				// do nothing
			}

			return expected.equals(actual);

		} else {
			return false;
		}
	}

	private boolean createIndex(SearchIndexDefinition index)
	{
		if (index == null) {
			logger.severe("Cannot create a null Index");
			return false;
		}

		try {
			XContentBuilder mappingBuilder = jsonBuilder().startObject().startObject(ELASTICSEARCH_DEFAULT_TYPE)
					.startObject("properties");

			for (SearchIndexFieldDefinition field : index.getSimpleFields()) {
				mappingBuilder.startObject(field.getSimpleFieldName().getSimpleName())
						.field("type", field.getSimpleType().getSimpleCode()).endObject();
			}

			mappingBuilder.endObject().endObject().endObject();

			CreateIndexResponse createResponse = client.admin().indices()
					.prepareCreate(index.getSimpleIndex().getSimpleValue())
					.addMapping(ELASTICSEARCH_DEFAULT_TYPE, mappingBuilder).get();

			if (!createResponse.isAcknowledged()) {
				logger.severe(String.format("Index Creation not acknowledged for index %s",
						index.getSimpleIndex().getSimpleValue()));
				return false;
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format("Failed to generate mapping json for index %s",
					index.getSimpleIndex().getSimpleValue()), e);
		}

		return true;
	}

	public boolean deleteIndex(SearchIndexDefinition index)
	{
		if (index == null) {
			logger.severe("Cannot delete a null Index");
			return false;
		}

		DeleteIndexResponse deleteResponse = client.admin().indices()
				.prepareDelete(index.getSimpleIndex().getSimpleValue()).get();
		if (!deleteResponse.isAcknowledged()) {
			logger.severe(String.format("Index Deletion not acknowledged for index %s",
					index.getSimpleIndex().getSimpleValue()));
			return false;
		}
		return true;

	}

	// on shutdown
	public void closeClient()
	{
		client.close();
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
			logger.severe("Cannot upsert a null Index");
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
		logger.info("No upsert needed");
		return true;
	}

}
