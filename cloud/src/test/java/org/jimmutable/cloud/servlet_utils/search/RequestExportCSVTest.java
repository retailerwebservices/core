package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class RequestExportCSVTest extends StubTest
{

		// TODO Objects dont equal after deserializing

		/*
		 * This test seems to fail because the deserialized object is not read in as a
		 * Stringable, but a String. ContainAll() calls Stringable's equals
		 * method and fails because the deserialized Set contains Strings not Stringable.
		 * 
		 * We cannot however write it as an Object because we cannot register Stringable
		 * object types (an exception is thrown).
		 */
		@Test
		public void deserializeEquals()
		{
			Set<SearchFieldId> set = new HashSet<>();
			set.add(new SearchFieldId("myField"));
			set.add(new SearchFieldId("myOtherField"));

			RequestExportCSV export = new RequestExportCSV(new IndexDefinition("dev:dev:v1"), false, "blaa", set);

			System.out.println(export.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));

			String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s"
				     , "{"
				     , "  \"type_hint\" : \"request_export_csv\","
				     , "  \"index\" : \"dev:dev:v1\","
				     , "  \"export_all_documents\" : false,"
				     , "  \"field_to_include_in_export\" : [ \"myotherfield\", \"myfield\" ],"
				     , "  \"query_string\" : \"blaa\""
				     , "}"
				);

			RequestExportCSV obj = (RequestExportCSV)StandardObject.deserialize(obj_string);


			assertEquals(obj, export);
		}
	
	@Test
	public void sanityTestStringable()
	{
		SearchFieldId one = new SearchFieldId("one");
		SearchFieldId same = new SearchFieldId("one");

		Set<SearchFieldId> set = new HashSet<SearchFieldId>();

		set.add(one);
		set.add(same);

		assertTrue(set.contains(one));
		assertTrue(set.contains(same));

		assertEquals(one, same);
	}

	@Test
	public void deserializeEmptyCollection()
	{
		RequestExportCSV export = new RequestExportCSV(new IndexDefinition("dev:dev:v1"), false, "blaa", null);

		System.out.println(export.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		
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
