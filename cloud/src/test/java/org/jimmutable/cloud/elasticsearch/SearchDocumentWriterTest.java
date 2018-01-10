package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.Map;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
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

		SearchIndexFieldDefinition theBoolean = new SearchIndexFieldDefinition(new FieldName("boolean"), SearchIndexFieldType.BOOLEAN);
		SearchIndexFieldDefinition theText1 = new SearchIndexFieldDefinition(new FieldName("text1"), SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theText2 = new SearchIndexFieldDefinition(new FieldName("text2"), SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theText3 = new SearchIndexFieldDefinition(new FieldName("text3"), SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theAtom = new SearchIndexFieldDefinition(new FieldName("atom"), SearchIndexFieldType.ATOM);
		SearchIndexFieldDefinition theDay = new SearchIndexFieldDefinition(new FieldName("day"), SearchIndexFieldType.DAY);
		SearchIndexFieldDefinition theFloat = new SearchIndexFieldDefinition(new FieldName("float"), SearchIndexFieldType.FLOAT);
		SearchIndexFieldDefinition theLong = new SearchIndexFieldDefinition(new FieldName("long"), SearchIndexFieldType.LONG);

		SearchIndexFieldDefinition theTimestamp = new SearchIndexFieldDefinition(new FieldName("timestamp"), SearchIndexFieldType.DAY);

		SearchIndexFieldDefinition theTextArray = new SearchIndexFieldDefinition(new FieldName("text_array"), SearchIndexFieldType.TEXT);
		SearchIndexFieldDefinition theAtomArray = new SearchIndexFieldDefinition(new FieldName("atom_array"), SearchIndexFieldType.ATOM);

		SearchIndexFieldDefinition theLongArray = new SearchIndexFieldDefinition(new FieldName("long_array"), SearchIndexFieldType.LONG);
		SearchIndexFieldDefinition theFloatArray = new SearchIndexFieldDefinition(new FieldName("float_array"), SearchIndexFieldType.FLOAT);
		SearchIndexFieldDefinition theBooleanArray = new SearchIndexFieldDefinition(new FieldName("boolean_array"), SearchIndexFieldType.BOOLEAN);
		SearchIndexFieldDefinition theTimestampArray = new SearchIndexFieldDefinition(new FieldName("timestamp_array"), SearchIndexFieldType.DAY);

		SearchDocumentWriter writer = new SearchDocumentWriter();
		writer.writeBoolean(theBoolean.getSimpleFieldName(), true);
		writer.writeText(theText1.getSimpleFieldName(), "abc");
		writer.writeTextWithPrefixMatchingSupport(theText2.getSimpleFieldName(), "abc");
		writer.writeTextWithSubstringMatchingSupport(theText3.getSimpleFieldName(), "abc");
		writer.writeAtom(theAtom.getSimpleFieldName(), "my atom");
		writer.writeDay(theDay.getSimpleFieldName(), new Day(new DateTime("1972-1-1")));
		writer.writeFloat(theFloat.getSimpleFieldName(), 0.1f);
		writer.writeLong(theLong.getSimpleFieldName(), 100L);
		writer.writeTimestamp(theTimestamp, 1420070400001L);

		FieldList<String> list = new FieldArrayList<String>();
		list.add("a");
		list.add("b");
		writer.writeTextArray(theTextArray, list);
		writer.writeAtomArray(theAtomArray, list);

		FieldList<Long> longs = new FieldArrayList<Long>();
		longs.add(0L);
		longs.add(1L);
		writer.writeLongArray(theLongArray, longs);
		writer.writeTimestampArray(theTimestampArray, longs);

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
		expected.put("text2", "abc ab a");
		expected.put("text3", "abc a b c ab bc abc");
		expected.put("atom", "my atom");
		expected.put("day", "1972-01-01");
		expected.put("float", 0.1f);
		expected.put("long", 100L);
		expected.put("timestamp", 1420070400001L);

		expected.put("text_array", list);
		expected.put("atom_array", list);

		expected.put("long_array", longs);
		expected.put("float_array", floats);
		expected.put("boolean_array", booleans);
		expected.put("timestamp_array", longs);

		assertEquals(expected, writer.getSimpleFieldsMap());

	}

}
