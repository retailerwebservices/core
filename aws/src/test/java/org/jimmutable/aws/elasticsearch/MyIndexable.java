package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.serialization.FieldName;
import org.joda.time.DateTime;

public class MyIndexable implements Indexable
{

	SearchIndexFieldDefinition theBoolean = new SearchIndexFieldDefinition(new FieldName("boolean"), SearchIndexFieldType.BOOLEAN);
	SearchIndexFieldDefinition theText1 = new SearchIndexFieldDefinition(new FieldName("text1"), SearchIndexFieldType.TEXT);
	SearchIndexFieldDefinition theText2 = new SearchIndexFieldDefinition(new FieldName("text2"), SearchIndexFieldType.TEXT);
	SearchIndexFieldDefinition theText3 = new SearchIndexFieldDefinition(new FieldName("text3"), SearchIndexFieldType.TEXT);
	SearchIndexFieldDefinition theAtom = new SearchIndexFieldDefinition(new FieldName("atom"), SearchIndexFieldType.ATOM);
	SearchIndexFieldDefinition theDay = new SearchIndexFieldDefinition(new FieldName("day"), SearchIndexFieldType.DAY);
	SearchIndexFieldDefinition theFloat = new SearchIndexFieldDefinition(new FieldName("float"), SearchIndexFieldType.FLOAT);
	SearchIndexFieldDefinition theLong = new SearchIndexFieldDefinition(new FieldName("long"), SearchIndexFieldType.LONG);

	private SearchIndexDefinition index_definition;
	private SearchDocumentId document_id;

	public MyIndexable()
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theBoolean);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theText1);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theText2);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theText3);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theAtom);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theDay);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theFloat);
		b.add(SearchIndexDefinition.FIELD_FIELDS, theLong);
		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("sanity:test:v0"));

		index_definition = (SearchIndexDefinition) b.create(null);

		document_id = new SearchDocumentId("amigo");

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
		writer.writeText(theText1.getSimpleFieldName(), "abc");
		writer.writeTextWithPrefixMatchingSupport(theText2.getSimpleFieldName(), "abc");
		writer.writeTextWithSubstringMatchingSupport(theText3.getSimpleFieldName(), "abc");
		writer.writeAtom(theAtom.getSimpleFieldName(), "my atom");
		writer.writeDay(theDay.getSimpleFieldName(), new Day(new DateTime("1972-1-1")));
		writer.writeFloat(theFloat.getSimpleFieldName(), 0.1f);
		writer.writeLong(theLong.getSimpleFieldName(), 100L);

	}

	@Override
	public SearchIndexDefinition getSimpleSearchIndexDefinition()
	{
		return index_definition;
	}

}
