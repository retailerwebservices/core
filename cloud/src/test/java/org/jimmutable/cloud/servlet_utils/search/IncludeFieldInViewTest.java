package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.*;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.servlet_utils.search.IncludeFieldInView;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;



public class IncludeFieldInViewTest extends StubTest
{
	@Before
	public void before()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(IncludeFieldInView.class);
		
	}


	@Test
	public void testUserSerialization()
	{
			IncludeFieldInView test_page = new IncludeFieldInView("Hello", new SearchFieldId("123"),true);
		String serialized_value = test_page.serialize(Format.JSON_PRETTY_PRINT);
	
		System.out.println(test_page.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		assertEquals("{\n" + 
				"  \"type_hint\" : \"includefieldinview\",\n" + 
				"  \"label\" : \"Hello\",\n" + 
				"  \"search_document_field\" : \"123\",\n" + 
				"  \"included_by_default\" : true\n" + 
				"}", serialized_value);
		
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"includefieldinview\","
			     , "  \"label\" : \"Hello\","
			     , "  \"search_document_field\" : \"123\","
			     , "  \"included_by_default\" : true"
			     , "}"
			);

			IncludeFieldInView obj = (IncludeFieldInView)StandardObject.deserialize(obj_string);

			assertEquals(true, obj.isSimpleIncludedByDefault());
	}

	@Test
	public void testUserComparisonAndEquals()
	{
		IncludeFieldInView field = new IncludeFieldInView("Hello", new SearchFieldId("123"),true);
		IncludeFieldInView field_modified = new IncludeFieldInView("Hello", new SearchFieldId("123"),true);

		assertTrue(field.equals(field_modified));
		assertEquals(0, field.compareTo(field_modified));

		field_modified = new IncludeFieldInView("Hello", new SearchFieldId("124"),true);
		assertFalse(field.equals(field_modified));
		assertEquals(-1, field.compareTo(field_modified));

		field_modified = new IncludeFieldInView("Hello", new SearchFieldId("122"),true);
		assertFalse(field.equals(field_modified));
		assertEquals(1, field.compareTo(field_modified));
	}

}
