package org.jimmutable.cloud.elasticsearch;

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

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("trevor:isawesome:v1"));

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

		writer.writeBoolean(theBoolean.getSimpleFieldName(), true);
		writer.writeText(theText.getSimpleFieldName(), "abc");
		writer.writeAtom(theAtom.getSimpleFieldName(), "my atom");
		writer.writeDay(theDay.getSimpleFieldName(), new Day(new DateTime("1972-1-1")));
		writer.writeFloat(theFloat.getSimpleFieldName(), 0.1f);
		writer.writeLong(theLong.getSimpleFieldName(), 100L);

		writer.writeTimestamp(theTimestamp, 1420070400001L);
	}

	@Override
	public IndexDefinition getSimpleSearchIndexDefinition()
	{
		return index_definition;
	}

}
