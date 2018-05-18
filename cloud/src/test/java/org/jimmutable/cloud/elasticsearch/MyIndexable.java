package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.serialization.FieldName;
import org.joda.time.DateTime;

/**
 * testclass
 * 
 * @author trevorbox
 *
 */
public class MyIndexable implements Indexable
{

	public static final SearchIndexFieldDefinition theBoolean = new SearchIndexFieldDefinition(new FieldName("boolean"), SearchIndexFieldType.BOOLEAN);
	public static final SearchIndexFieldDefinition theText = new SearchIndexFieldDefinition(new FieldName("text"), SearchIndexFieldType.TEXT);
	public static final SearchIndexFieldDefinition theAtom = new SearchIndexFieldDefinition(new FieldName("atom"), SearchIndexFieldType.ATOM);
	public static final SearchIndexFieldDefinition theDay = new SearchIndexFieldDefinition(new FieldName("day"), SearchIndexFieldType.DAY);
	public static final SearchIndexFieldDefinition theFloat = new SearchIndexFieldDefinition(new FieldName("float"), SearchIndexFieldType.FLOAT);
	public static final SearchIndexFieldDefinition theLong = new SearchIndexFieldDefinition(new FieldName("long"), SearchIndexFieldType.LONG);

	public static final SearchIndexFieldDefinition theTimestamp = new SearchIndexFieldDefinition(new FieldName("timestamp"), SearchIndexFieldType.DAY);

	public static final SearchIndexFieldDefinition theTextArray = new SearchIndexFieldDefinition(new FieldName("text_array"), SearchIndexFieldType.TEXT);
	public static final SearchIndexFieldDefinition theAtomArray = new SearchIndexFieldDefinition(new FieldName("atom_array"), SearchIndexFieldType.ATOM);
	public static final SearchIndexFieldDefinition theLongArray = new SearchIndexFieldDefinition(new FieldName("long_array"), SearchIndexFieldType.LONG);
	public static final SearchIndexFieldDefinition theFloatArray = new SearchIndexFieldDefinition(new FieldName("float_array"), SearchIndexFieldType.FLOAT);
	public static final SearchIndexFieldDefinition theBooleanArray = new SearchIndexFieldDefinition(new FieldName("boolean_array"), SearchIndexFieldType.BOOLEAN);
	public static final SearchIndexFieldDefinition theTimestampArray = new SearchIndexFieldDefinition(new FieldName("timestamp_array"), SearchIndexFieldType.DAY);

	private IndexDefinition index_definition;
	private SearchDocumentId document_id;

	public static final SearchIndexDefinition SEARCH_INDEX_DEFINITION;

	static
	{
		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, theBoolean);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theText);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theAtom);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theDay);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theFloat);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theLong);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theTimestamp);

		b.add(SearchIndexDefinition.FIELD_FIELDS, theTextArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theAtomArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theLongArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theFloatArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theBooleanArray);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theTimestampArray);

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("trevor_isawesome_v1"));

		SEARCH_INDEX_DEFINITION = (SearchIndexDefinition) b.create(null);
	}

	public MyIndexable(IndexDefinition index_definition, SearchDocumentId document_id)
	{
		this.index_definition = index_definition;
		this.document_id = document_id;
	}

	@Override
	public SearchDocumentId getSimpleSearchDocumentId()
	{
		return document_id;
	}

	@Override
	public void writeSearchDocument(SearchDocumentWriter writer)
	{

		writer.writeBoolean(theBoolean, true);
		writer.writeText(theText, "abc");
		writer.writeAtom(theAtom, "my atom");
		writer.writeDay(theDay, new Day(new DateTime("1972-1-1")));
		writer.writeFloat(theFloat, 0.1f);
		writer.writeLong(theLong, 100L);

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
	}

	@Override
	public IndexDefinition getSimpleSearchIndexDefinition()
	{
		return index_definition;
	}

}
