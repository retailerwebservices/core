package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class AdvancedSearchFieldTest extends StubTest
{

	@Test
	public void testUserSerialization()
	{
		AdvancedSearchField field = new AdvancedSearchField("label", new SearchFieldId("field"), AdvancedSearchFieldType.COMBO_BOX, Collections.EMPTY_LIST); 
		String serialized_value = field.serialize(Format.JSON_PRETTY_PRINT);

		System.out.println(field.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		assertEquals("{\n" + 
				"  \"type_hint\" : \"advancedsearchfield\",\n" + 
				"  \"label\" : \"label\",\n" + 
				"  \"searchdocumentfield\" : \"field\",\n" + 
				"  \"type\" : \"combo-box\",\n" + 
				"  \"combo_box_choices\" : [ ]\n" + 
				"}", serialized_value);

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"advancedsearchfield\","
			     , "  \"label\" : \"label\","
			     , "  \"searchdocumentfield\" : \"field\","
			     , "  \"type\" : \"combo-box\","
			     , "  \"combo_box_choices\" : [ ]"
			     , "}"
			);

			AdvancedSearchField obj = (AdvancedSearchField)StandardObject.deserialize(obj_string);
		assertEquals("label", obj.getSimpleLabel());
	}

	@Test
	public void testUserComparisonAndEquals()
	{
		AdvancedSearchField field = new AdvancedSearchField("label", new SearchFieldId("field"), AdvancedSearchFieldType.COMBO_BOX, Collections.EMPTY_LIST); 
		AdvancedSearchField field_modified = new AdvancedSearchField("label", new SearchFieldId("field"), AdvancedSearchFieldType.COMBO_BOX, Collections.EMPTY_LIST); 

		assertTrue(field.equals(field_modified));
		assertEquals(0, field.compareTo(field_modified));
		
		field_modified = new AdvancedSearchField("label", new SearchFieldId("fielda"), AdvancedSearchFieldType.TEXT, Collections.EMPTY_LIST); 
		assertFalse(field.equals(field_modified));
		assertTrue(0>field.compareTo(field_modified));

		field_modified = new AdvancedSearchField("label", new SearchFieldId("afield"), AdvancedSearchFieldType.COMBO_BOX, Collections.EMPTY_LIST); 
		assertFalse(field.equals(field_modified));
		assertTrue(0<field.compareTo(field_modified));
	}

	@Test
	public void testOptionalFields()
	{
		AdvancedSearchField field = new AdvancedSearchField("label", new SearchFieldId("field"), AdvancedSearchFieldType.COMBO_BOX, Collections.EMPTY_LIST); 
		AdvancedSearchField field_modified = new AdvancedSearchField("label", new SearchFieldId("field"), AdvancedSearchFieldType.COMBO_BOX, Collections.EMPTY_LIST); 

		assertTrue(field.equals(field_modified));
		assertEquals(0, field.compareTo(field_modified));
		
		field_modified = new AdvancedSearchField("label", new SearchFieldId("fielda"), AdvancedSearchFieldType.TEXT, Collections.EMPTY_LIST); 
		assertFalse(field.equals(field_modified));
		assertTrue(0>field.compareTo(field_modified));

		field_modified = new AdvancedSearchField("label", new SearchFieldId("afield"), AdvancedSearchFieldType.COMBO_BOX, Collections.EMPTY_LIST); 
		assertFalse(field.equals(field_modified));
		assertTrue(0<field.compareTo(field_modified));
	}
	
}
