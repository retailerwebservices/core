package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.time.Instant;
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

		SearchIndexFieldDefinition theBoolean = new SearchIndexFieldDefinition(new FieldName("boolean"), SearchIndexFieldType.BOOLEAN);
		SearchIndexFieldDefinition theText1 = new SearchIndexFieldDefinition(new FieldName("text1"), SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theText2 = new SearchIndexFieldDefinition(new FieldName("text2"), SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theText3 = new SearchIndexFieldDefinition(new FieldName("text3"), SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theAtom = new SearchIndexFieldDefinition(new FieldName("atom"), SearchIndexFieldType.ATOM);
		SearchIndexFieldDefinition theDay = new SearchIndexFieldDefinition(new FieldName("day"), SearchIndexFieldType.DAY);
		SearchIndexFieldDefinition theFloat = new SearchIndexFieldDefinition(new FieldName("float"), SearchIndexFieldType.FLOAT);
		SearchIndexFieldDefinition theLong = new SearchIndexFieldDefinition(new FieldName("long"), SearchIndexFieldType.LONG);
		SearchIndexFieldDefinition theInstant = new SearchIndexFieldDefinition(new FieldName("instant"), SearchIndexFieldType.INSTANT);

		SearchIndexFieldDefinition theTextArray = new SearchIndexFieldDefinition(new FieldName("text_array"), SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theAtomArray = new SearchIndexFieldDefinition(new FieldName("atom_array"), SearchIndexFieldType.ATOM);

		SearchIndexFieldDefinition theLongArray = new SearchIndexFieldDefinition(new FieldName("long_array"), SearchIndexFieldType.LONG);
		SearchIndexFieldDefinition theFloatArray = new SearchIndexFieldDefinition(new FieldName("float_array"), SearchIndexFieldType.FLOAT);
		SearchIndexFieldDefinition theBooleanArray = new SearchIndexFieldDefinition(new FieldName("boolean_array"), SearchIndexFieldType.BOOLEAN);
		SearchIndexFieldDefinition theDayArray = new SearchIndexFieldDefinition(new FieldName("day_array"), SearchIndexFieldType.DAY);
		SearchIndexFieldDefinition theInstantArray = new SearchIndexFieldDefinition(new FieldName("instant_array"), SearchIndexFieldType.INSTANT);
		

		SearchDocumentWriter writer = new SearchDocumentWriter();
		writer.writeBoolean(theBoolean, true);
		writer.writeText(theText1, "abc");
		writer.writeTextWithPrefixMatchingSupport(theText2, "abc");
		writer.writeTextWithSubstringMatchingSupport(theText3, "abc");
		writer.writeAtom(theAtom, "my atom");
		writer.writeDay(theDay, new Day(new DateTime("1972-1-1")));
		writer.writeFloat(theFloat, 0.1f);
		writer.writeLong(theLong, 100L);
		writer.writeInstant(theInstant, new Instant(1420070400001L));

		FieldList<String> list = new FieldArrayList<String>();
		list.add("a");
		list.add("b");
		writer.writeTextArray(theTextArray, list);
		writer.writeAtomArray(theAtomArray, list);

		FieldList<Long> longs = new FieldArrayList<Long>();
		longs.add(0L);
		longs.add(1L);
		writer.writeLongArray(theLongArray, longs);
		
		FieldList<Day> days = new FieldArrayList<>();
		days.add(new Day(1, 1, 1980));
		days.add(new Day(2, 29, 2000));
		writer.writeDayArray(theDayArray, days);
		
		FieldList<Instant> instants = new FieldArrayList<>();
		instants.add(new Instant(0L));
		instants.add(new Instant(1L));
		writer.writeInstantArray(theInstantArray, instants);

		FieldList<Float> floats = new FieldArrayList<Float>();
		floats.add(0.0f);
		floats.add(0.1f);
		writer.writeFloatArray(theFloatArray, floats);

		FieldList<Boolean> booleans = new FieldArrayList<Boolean>();
		booleans.add(true);
		booleans.add(false);
		writer.writeBooleanArray(theBooleanArray, booleans);

		Map<String, Object> expected = new HashMap<String, Object>();

		expected.put("boolean", true);
		expected.put("text1", "abc");
		expected.put(ElasticSearch.getSortFieldNameText("text1"), "abc");
		expected.put("text2", "abc ab a");
		expected.put(ElasticSearch.getSortFieldNameText("text2"), "abc ab a");
		
		expected.put("text3", "abc a b c ab bc abc");
		expected.put(ElasticSearch.getSortFieldNameText("text3"), "abc a b c ab bc abc");
		expected.put("atom", "my atom");
		expected.put("day", "1972-01-01");
		expected.put("float", 0.1f);
		expected.put("long", 100L);
		expected.put("instant", new Instant(1420070400001L));
		expected.put(ElasticSearch.getSortFieldNameInstant("instant"), 1420070400001L);
//		expected.put("timestamp", 1420070400001L);

		expected.put("text_array", list);
		expected.put("atom_array", list);

		expected.put("long_array", longs);
		expected.put("float_array", floats);
		List<String> day_array_formatted = new ArrayList<>();
		day_array_formatted.add("1980-01-01");
		day_array_formatted.add("2000-02-29");
		expected.put("day_array", day_array_formatted);
		expected.put("boolean_array", booleans);
		expected.put("instant_array", instants);

		// Below is only for troubleshooting
//		Map<String, Object> test = writer.getSimpleFieldsMap();
//		System.out.println("Actual:" + test.toString());
//		System.out.println("Expected:" + expected.toString());
//		
//		for (Map.Entry<String, Object> entry : writer.getSimpleFieldsMap().entrySet() )
//		{
//			Object comparison_value = expected.get(entry.getKey());
//			if (!comparison_value.equals(entry.getValue()))
//			{
//				System.out.println("Not Equal: " + entry.getKey());
//				System.out.println("Expected: \n" + comparison_value);
//				System.out.println("Actual: \n" + entry.getValue());
//			};
//		}
		
		assertEquals(expected, writer.getSimpleFieldsMap());

	}

}
