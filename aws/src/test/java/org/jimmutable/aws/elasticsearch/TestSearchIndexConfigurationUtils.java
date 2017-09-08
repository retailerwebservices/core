package org.jimmutable.aws.elasticsearch;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.jimmutable.aws.StartupSingleton;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestSearchIndexConfigurationUtils
{
	private static SearchIndexDefinition def;

	@BeforeClass
	public static void setup() throws UnknownHostException
	{

		System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "INFO");
		StartupSingleton.setupOnce();

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);

		StartupSingleton.setupOnce();

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("spaghetti"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("meatballs"), SearchIndexFieldType.TEXT));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("application:my-index:v1"));

		def = (SearchIndexDefinition) b.create(null);

	}

	@Test
	public void parseJson() throws JsonProcessingException, IOException
	{
		String JSON = "{\"tweet\":{\"properties\":{\"message\":{\"type\":\"text\"}}}}";

		// String JSON =
		// "{\"tweet\":{\"propderties\":{\"message\":{\"type\":\"text\"}}}}";



		Map<String, String> map = new HashMap<String, String>();

		try {
			new ObjectMapper().readTree(JSON).get("tweet").get("properties").fields().forEachRemaining(action -> {
				map.put(action.getKey(), action.getValue().get("type").asText());
			});
		} catch (Exception e) {
			// do nothing
		}

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("message"), SearchIndexFieldType.TEXT));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("foo:BAR:v4"));

		SearchIndexDefinition def = (SearchIndexDefinition) b.create(null);

		Map<String, String> comparedTo = new HashMap<String, String>();
		def.getSimpleFields().forEach(action -> {
			comparedTo.put(action.getSimpleFieldName().getSimpleName(), action.getSimpleType().getSimpleCode());
		});

		System.out.println(map.toString());

		assertEquals(map, comparedTo);

		// System.out.println(root.toString());
		//
		// JsonNode propNode = root.path("tweet").path("properties");
		//
		// System.out.println(propNode.toString());
		//
		// JsonParser parser = propNode.traverse();
		//
		// String fieldName;
		// while (!parser.isClosed()) {
		// JsonToken jsonToken = parser.nextToken();
		// if (JsonToken.FIELD_NAME.equals(jsonToken)) {
		// fieldName = parser.getCurrentName();
		// System.out.println(fieldName);
		// }
		// }

		// JsonFactory factory = new JsonFactory();
		// JsonParser parser = factory.createParser(JSON);
		//
		// String fieldName;
		// while (!parser.isClosed()) {
		//
		// JsonToken jsonToken = parser.nextToken();
		//
		// if (JsonToken.FIELD_NAME.equals(jsonToken)) {
		// fieldName = parser.getCurrentName();
		// System.out.println("tweet?" + fieldName);
		//
		// if (fieldName.equals("tweet")) {
		//
		// jsonToken = parser.nextToken();
		// if (JsonToken.FIELD_NAME.equals(jsonToken)) {
		// fieldName = parser.getCurrentName();
		// System.out.println("properties?" + fieldName);
		//
		// if (fieldName.equals("properties")) {
		// jsonToken = parser.nextToken();
		// if (JsonToken.FIELD_NAME.equals(jsonToken)) {
		// fieldName = parser.getCurrentName();
		//
		// System.out.println(fieldName);
		// jsonToken = parser.nextToken();
		//
		// if (JsonToken.FIELD_NAME.equals(jsonToken)) {
		//
		// // System.out.println(parser.toString());
		// }
		// }
		//
		// }
		// }
		// }
		// }
		// }

		// fieldName = parser.getCurrentName();
		// System.out.println(fieldName);
		//
		// jsonToken = parser.nextToken();
		// if (fieldName.equals("properties")) {
		// fieldName = parser.getCurrentName();
		// System.out.println(fieldName);
		//
		// jsonToken = parser.nextToken();
		// fieldName = parser.getCurrentName();
		// System.out.println(fieldName);
		// if (fieldName.equals("type")) {
		// System.out.println(parser.getValueAsString());
		// }
		//
		// }

	}

	// @Test
	public void upsert() throws IOException
	{
		SearchIndexConfigurationUtils util = new SearchIndexConfigurationUtils(new ElasticSearchEndpoint());

		util.upsertIndex(def);
		util.indexProperlyConfigured(def);

	}

	// @Test
	public void properlyConfigured() throws UnknownHostException
	{

	}

}
