package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.junit.Test;

public class RequestExportCSVTest extends StubTest
{

	static private final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
	static private final FieldDefinition.String FIELD_FIRST_NAME = new FieldDefinition.String("first_name", null);

	static private final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static private final SearchIndexFieldDefinition SEARCH_FIELD_ID_ATOM = new SearchIndexFieldDefinition(new FieldName(String.format("%s_atom", FIELD_ID.getSimpleFieldName().getSimpleName())), SearchIndexFieldType.ATOM);
	static private final SearchIndexFieldDefinition SEARCH_FIELD_FIRST_NAME = new SearchIndexFieldDefinition(FIELD_FIRST_NAME.getSimpleFieldName(), SearchIndexFieldType.TEXT);

	@Test
	public void deserializeEquals()
	{

		Set<SearchIndexFieldDefinition> set = new HashSet<SearchIndexFieldDefinition>();
		set.add(SEARCH_FIELD_ID);
		set.add(SEARCH_FIELD_ID_ATOM);

		RequestExportCSV export = new RequestExportCSV(new IndexDefinition("dev:dev:v1"), false, "blaa", set);

		//System.out.println(export.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
		     , "{"
		     , "  \"type_hint\" : \"request_export_csv\","
		     , "  \"index\" : \"dev:dev:v1\","
		     , "  \"export_all_documents\" : false,"
		     , "  \"field_to_include_in_export\" : [ {"
		     , "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
		     , "    \"name\" : {"
		     , "      \"type_hint\" : \"jimmutable.FieldName\","
		     , "      \"name\" : \"id_atom\""
		     , "    },"
		     , "    \"type\" : \"keyword\""
		     , "  }, {"
		     , "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
		     , "    \"name\" : {"
		     , "      \"type_hint\" : \"jimmutable.FieldName\","
		     , "      \"name\" : \"id\""
		     , "    },"
		     , "    \"type\" : \"text\""
		     , "  } ],"
		     , "  \"query_string\" : \"blaa\""
		     , "}"
		);
		RequestExportCSV obj = (RequestExportCSV) StandardObject.deserialize(obj_string);

		assertEquals(obj, export);
	}

	@Test
	public void sanityTestStringable()
	{

		Set<SearchIndexFieldDefinition> set = new HashSet<SearchIndexFieldDefinition>();

		set.add(SEARCH_FIELD_ID_ATOM);
		set.add(SEARCH_FIELD_FIRST_NAME);

		assertTrue(set.contains(SEARCH_FIELD_ID_ATOM));
		assertTrue(set.contains(SEARCH_FIELD_FIRST_NAME));

		assertEquals(SEARCH_FIELD_FIRST_NAME, SEARCH_FIELD_FIRST_NAME);
	}

	@Test
	public void deserializeEmptyCollection()
	{
		RequestExportCSV export = new RequestExportCSV(new IndexDefinition("dev:dev:v1"), false, "blaa", null);

		//System.out.println(export.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"request_export_csv\","
			     , "  \"index\" : \"dev:dev:v1\","
			     , "  \"export_all_documents\" : false,"
			     , "  \"field_to_include_in_export\" : [ ],"
			     , "  \"query_string\" : \"blaa\""
			     , "}"
			);

		RequestExportCSV obj = (RequestExportCSV)StandardObject.deserialize(obj_string);

		assertEquals(obj, export);

	}

}
