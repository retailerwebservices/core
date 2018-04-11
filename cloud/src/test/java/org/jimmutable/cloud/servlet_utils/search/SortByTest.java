package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.Before;
import org.junit.Test;

public class SortByTest
{

	@Before
	public void before()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SortBy.class);
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
	}

	@Test
	public void valid()
	{
		Builder b = new Builder(SortBy.TYPE_NAME);

		b.set(SortBy.FIELD_FIELD, new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN));
		b.set(SortBy.FIELD_DIRECTION,  SortDirection.ASCENDING);

		SortBy sort_by = (SortBy) b.create(null);

		//System.out.println(sort_by.toJavaCode(Format.JSON_PRETTY_PRINT, "sort_by"));

		assertNotNull(sort_by);
		assertEquals(SortDirection.ASCENDING, sort_by.getSimpleDirection());
		assertEquals("fboolean", sort_by.getSimpleField().getSimpleFieldName().getSimpleName());

	}

	@Test(expected = ValidationException.class)
	public void nullField()
	{
		Builder b = new Builder(SortBy.TYPE_NAME);

		b.set(SortBy.FIELD_FIELD, new SearchIndexFieldDefinition(null, SearchIndexFieldType.TEXT));
		b.set(SortBy.FIELD_DIRECTION,  null);
	}

	@Test(expected = SerializeException.class)
	public void serializationException()
	{
		Builder b = new Builder(SortBy.TYPE_NAME);

		b.set(SortBy.FIELD_FIELD, new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN));

		// Intentionally omitted
		// b.set(SortBy.FIELD_DIRECTION,  SortDirection.ASCENDING);

		b.create(null);

	}

	@Test
	public void serialize()
	{
		String sort_by_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.SortBy\","
			     , "  \"field\" : {"
			     , "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
			     , "    \"name\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"fboolean\""
			     , "    },"
			     , "    \"type\" : \"boolean\""
			     , "  },"
			     , "  \"direction\" : \"ascending\""
			     , "}"
			);

		SortBy sort_by = (SortBy)StandardObject.deserialize(sort_by_string);
		assertNotNull(sort_by);
		assertEquals(SortDirection.ASCENDING, sort_by.getSimpleDirection());
		assertEquals("fboolean", sort_by.getSimpleField().getSimpleFieldName().getSimpleName());
	}

}