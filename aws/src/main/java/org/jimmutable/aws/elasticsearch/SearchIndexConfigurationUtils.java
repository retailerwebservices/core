package org.jimmutable.aws.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.ToXContent.Params;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class SearchIndexConfigurationUtils
{

	private static final Logger logger = Logger.getLogger(SearchIndexConfigurationUtils.class.getName());

	private TransportClient client;

	@SuppressWarnings("resource")
	public SearchIndexConfigurationUtils(ElasticSearchEndpoint endpoint) throws UnknownHostException
	{
		// set cluster name

		// Settings settings = Settings.builder().put("cluster.name",
		// "elasticsearch").build();

		client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(
				InetAddress.getByName(endpoint.getSimpleHost()), endpoint.getSimplePort()));

	}

	public boolean indexExists(IndexDefinition index)
	{
		return client.admin().indices().prepareExists(index.getSimpleValue()).get().isExists();
	}

	public boolean indexExists(SearchIndexDefinition index)
	{
		boolean response = client.admin().indices().prepareExists(index.getSimpleIndex().getSimpleValue()).get()
				.isExists();

		System.out.println(response);

		return response;
	}

	public boolean indexProperlyConfigured(SearchIndexDefinition index) throws IOException
	{

		if (indexExists(index)) {
			// GetSettingsResponse response = client.admin().indices()
			// .prepareGetSettings(index.getSimpleIndex().getSimpleValue()).get();

			// GetFieldMappingsResponse response = client.admin().indices()
			// .prepareGetFieldMappings(index.getSimpleIndex().getSimpleValue()).setTypes("tweet").get();

			GetMappingsResponse response2 = client.admin().indices()
					.prepareGetMappings(index.getSimpleIndex().getSimpleValue()).get();

			System.out.println(response2.mappings().size());

			// {"tweet":{"properties":{"message":{"type":"text"}}}}

			String json = response2.getMappings().get(index.getSimpleIndex().getSimpleValue()).get("tweet").source()
					.string();

			Map<String, String> nameType = new HashMap<String, String>();

			System.out.println("current json " + json);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(json);
			
			

			System.out.println("root " + root.textValue());

			JsonNode properties = root.path("tweet").path("properties");

			System.out.println("properties " + properties.textValue());

			// System.out.println(
			// response2.getMappings().get(index.getSimpleIndex().getSimpleValue()).get("tweet").source());
			//
			// System.out.println(response2.getMappings().get(index.getSimpleIndex().getSimpleValue()).get("tweet"));
			//
			// ObjectMapper mapper = new ObjectMapper();

			// DefaultType type = mapper.readValue(
			// response2.getMappings().get(index.getSimpleIndex().getSimpleValue()).get("tweet").source().string(),
			// DefaultType.class);

			// DefaultType type = new DefaultType(new IndexType("blaa"));
			// System.out.println(mapper.writeValueAsString(type));

			response2.mappings().forEach(cursor -> {
				// System.out.println(String.format("%s %s %s %s", cursor.index, cursor.key,
				// cursor.value.toString(), cursor.toString()));

				cursor.value.forEach(unit -> {

					try {
						System.out.println(unit.value.source().toString());

						unit.value.getSourceAsMap().forEach((key, value) -> {
							System.out.println(String.format("%s %s %s ", unit.key, key, value));

						});
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// System.out.println(String.format("%s %s %s %s", unit.index, unit.key,
					// unit.value.toString(), unit.toString()));
				});

			});

			// response2.mappings().forEach((name, map) -> {
			// System.out.print(name);
			// map.forEach((name2, map2) -> {
			// System.out.print(name2);
			// map2.forEach((name3, map3) -> {
			// System.out.print(name3);
			// map3.sourceAsMap().forEach((metaname, meta) -> {
			// System.out.print(metaname);
			// System.out.print(meta.toString());
			// });
			// });
			// });
			// System.out.println();
			// });

			// XContentBuilder builder = jsonBuilder();
			//
			// response.toXContent(builder, ToXContent.EMPTY_PARAMS);
			//
			// response.

			// System.out.println(builder.toString());

			// response.getIndexToSettings().forEach(cursor -> {
			// System.out.println(
			// String.format("%s %s %s %s", cursor.index, cursor.key,
			// cursor.value.getAsMap().toString(), cursor.toString()));
			//
			// });

		}
		return false;
	}

	private String toJson(SearchIndexDefinition index)
	{

		try {
			XContentBuilder builder = jsonBuilder().startObject().startObject("mappings").startObject("properties");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// index.getSimpleFields().forEach(field->{
		// builder.field(field.getSimpleFieldName().getSimpleName(),
		// field.getSimpleType().getSimpleCode());
		// });
		//
		//
		// .field("user", "kimchy")
		// .field("postDate", new Date())
		// .field("message", "trying out Elasticsearch")
		// .endObject()
		//
		// index.getSimpleFields();

		return null;
	}

	// TODO
	public boolean upsertIndex(SearchIndexDefinition index)
	{

		if (!indexExists(index)) {
			CreateIndexResponse response = client.admin().indices()
					.prepareCreate(index.getSimpleIndex().getSimpleValue())
					.addMapping("tweet",
							"{\n" + "    \"tweet\": {\n" + "      \"properties\": {\n" + "        \"message\": {\n"
									+ "          \"type\": \"string\"\n" + "        }\n" + "      }\n" + "    }\n"
									+ "  }")
					.get();
			System.out.println("response is acknowledged? " + response.isAcknowledged());
		} else {
			logger.info(String.format("Index %s already exists", index.getSimpleIndex().getSimpleValue()));
			System.out.println(String.format("Index %s already exists", index.getSimpleIndex().getSimpleValue()));
		}

		return true;
	}

}
