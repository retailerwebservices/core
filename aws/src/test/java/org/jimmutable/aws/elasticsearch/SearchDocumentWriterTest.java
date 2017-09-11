package org.jimmutable.aws.elasticsearch;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchDocumentWriterTest
{

	@BeforeClass
	public static void beforeClass()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
	}

	@Test
	public void sanityTest()
	{
		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		SearchIndexFieldDefinition theBoolean = new SearchIndexFieldDefinition(new FieldName("boolean"),
				SearchIndexFieldType.BOOLEAN);
		SearchIndexFieldDefinition theText1 = new SearchIndexFieldDefinition(new FieldName("text1"),
				SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theText2 = new SearchIndexFieldDefinition(new FieldName("text2"),
				SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theText3 = new SearchIndexFieldDefinition(new FieldName("text3"),
				SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theAtom = new SearchIndexFieldDefinition(new FieldName("atom"),
				SearchIndexFieldType.ATOM);
		SearchIndexFieldDefinition theDay = new SearchIndexFieldDefinition(new FieldName("day"),
				SearchIndexFieldType.DAY);
		SearchIndexFieldDefinition theFloat = new SearchIndexFieldDefinition(new FieldName("float"),
				SearchIndexFieldType.FLOAT);
		SearchIndexFieldDefinition theLong = new SearchIndexFieldDefinition(new FieldName("long"),
				SearchIndexFieldType.LONG);

		b.add(SearchIndexDefinition.FIELD_FIELDS, theBoolean);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theText1);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theText2);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theText3);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theAtom);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theDay);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theFloat);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theLong);

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("sanity:test:v0"));

		SearchIndexDefinition def = (SearchIndexDefinition) b.create(null);

		SearchDocumentId id = new SearchDocumentId("trevor");

		SearchDocumentWriter writer = new SearchDocumentWriter();
		writer.writeBoolean(theBoolean.getSimpleFieldName(), true);
		writer.writeText(theText1.getSimpleFieldName(), "abc");
		writer.writeTextWithPrefixMatchingSupport(theText2.getSimpleFieldName(), "abc");
		writer.writeTextWithSubstringMatchingSupport(theText3.getSimpleFieldName(), "abc");
		writer.writeAtom(theAtom.getSimpleFieldName(), "my atom");
		writer.writeDay(theDay.getSimpleFieldName(), new Day(new DateTime("1972-1-1")));
		writer.writeFloat(theFloat.getSimpleFieldName(), 0.1f);
		writer.writeLong(theLong.getSimpleFieldName(), 100L);

		Map<String, Object> expected = new HashMap<String, Object>();

		expected.put("boolean", true);
		expected.put("text1", "abc");
		expected.put("text2", "abc ab a");
		expected.put("text3", "abc a b c ab bc abc");
		expected.put("atom", "my atom");
		expected.put("day", "1972-01-01");
		expected.put("float", 0.1f);
		expected.put("long", 100L);

		assertEquals(expected, writer.getSimpleFieldsMap());

//		SearchIndexConfigurationUtils util = new SearchIndexConfigurationUtils(new ElasticSearchEndpoint());
//
//		util.upsertIndex(def);
//		try {
//			TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
//					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
//			client.prepareIndex(def.getSimpleIndex().getSimpleValue(), "default", id.getSimpleValue())
//					.setSource(writer.getSimpleFieldsMap()).get();
//
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
