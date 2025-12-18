package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class RequestExportCSVTest extends StubTest
{

	static private final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
	static private final FieldDefinition.String FIELD_FIRST_NAME = new FieldDefinition.String("first_name", null);
	
	static private final SearchFieldId SEARCH_FIELD_ID = new SearchFieldId(FIELD_ID.getSimpleFieldName().getSimpleName());
	static private final SearchFieldId SEARCH_FIELD_ID_ATOM = new SearchFieldId(String.format("%s_atom", FIELD_ID.getSimpleFieldName().getSimpleName()));
	static private final SearchFieldId SEARCH_FIELD_FIRST_NAME = new SearchFieldId(FIELD_FIRST_NAME.getSimpleFieldName().getSimpleName());
	@Test
	public void deserializeEquals()
	{

		Set<SearchFieldId> set = new HashSet<SearchFieldId>();
		set.add(SEARCH_FIELD_ID);
		set.add(SEARCH_FIELD_ID_ATOM);

		RequestExportCSV export = new RequestExportCSV(new IndexDefinition("dev_dev_v1"), false, "blaa", set);

//		System.out.println(export.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
				, "{"
				, "  \"type_hint\" : \"request_export_csv\","
				, "  \"index\" : \"dev_dev_v1\","
				, "  \"export_all_documents\" : false,"
				, "  \"field_to_include_in_export\" : [ \"id\", \"id_atom\" ],"
				, "  \"query_string\" : \"blaa\","
				, "  \"id_field\" : {"
				, "    \"type_hint\" : \"jimmutable.FieldName\","
				, "    \"name\" : \"id\""
				, "  }"
				, "}"
		);


		RequestExportCSV obj = (RequestExportCSV)StandardObject.deserialize(obj_string);

		assertEquals(obj, export);
	}

	@Test
	public void sanityTestStringable()
	{

		Set<SearchFieldId> set = new HashSet<SearchFieldId>();

		set.add(SEARCH_FIELD_ID_ATOM);
		set.add(SEARCH_FIELD_FIRST_NAME);

		assertTrue(set.contains(SEARCH_FIELD_ID_ATOM));
		assertTrue(set.contains(SEARCH_FIELD_FIRST_NAME));

		assertEquals(SEARCH_FIELD_FIRST_NAME, SEARCH_FIELD_FIRST_NAME);
	}

	@Test
	public void deserializeEmptyCollection()
	{
		RequestExportCSV export = new RequestExportCSV(new IndexDefinition("dev_dev_v1"), false, "blaa",null);

//		System.out.println(export.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
				, "{"
				, "  \"type_hint\" : \"request_export_csv\","
				, "  \"index\" : \"dev_dev_v1\","
				, "  \"export_all_documents\" : false,"
				, "  \"field_to_include_in_export\" : [ ],"
				, "  \"query_string\" : \"blaa\","
				, "  \"id_field\" : {"
				, "    \"type_hint\" : \"jimmutable.FieldName\","
				, "    \"name\" : \"id\""
				, "  }"
				, "}"
		);

		RequestExportCSV obj = (RequestExportCSV)StandardObject.deserialize(obj_string);

		assertEquals(obj, export);

	}

}
