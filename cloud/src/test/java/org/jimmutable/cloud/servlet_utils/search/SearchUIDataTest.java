package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.Before;
import org.junit.Test;

public class SearchUIDataTest extends StubTest
{
	

//	List<AdvancedSearchField> search_field = Collections.singletonList(new AdvancedSearchField("Label", new SearchFieldId("SearchFieldId"), AdvancedSearchFieldType.TEXT, Collections.emptyList()));
//	List<IncludeFieldInView> fields_in_view = Collections.singletonList(new IncludeFieldInView("fieldLabel", new SearchFieldId("SearchFieldId"), false));

	
	@Test
	public void testUserSerialization()
	{
		
		List<AdvancedSearchField> search_field = Collections.singletonList(new AdvancedSearchField("Label", new SearchFieldId("SearchFieldId"), AdvancedSearchFieldType.TEXT, Collections.emptyList()));
		List<IncludeFieldInView> fields_in_view = Collections.singletonList(new IncludeFieldInView("fieldLabel", new SearchFieldId("SearchFieldId"), false));
		
		SearchUIData field = new SearchUIData(search_field, fields_in_view);
		String serialized_value = field.serialize(Format.JSON_PRETTY_PRINT);

		System.out.println(field.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		assertEquals("{\n" + "  \"type_hint\" : \"searchuidata\",\n" + "  \"advancedsearchfields\" : [ {\n" + "    \"type_hint\" : \"advancedsearchfield\",\n" + "    \"label\" : \"Label\",\n" + "    \"searchdocumentfield\" : \"searchfieldid\",\n" + "    \"type\" : \"text\",\n" + "    \"combo_box_choices\" : [ ]\n" + "  } ],\n" + "  \"fieldsinview\" : [ {\n" + "    \"type_hint\" : \"includefieldinview\",\n" + "    \"label\" : \"fieldLabel\",\n" + "    \"search_document_field\" : \"searchfieldid\",\n" + "    \"included_by_default\" : false\n" + "  } ]\n" + "}", serialized_value);

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"searchuidata\",", "  \"advancedsearchfields\" : [ {", "    \"type_hint\" : \"advancedsearchfield\",", "    \"label\" : \"Label\",", "    \"searchdocumentfield\" : \"searchfieldid\",", "    \"type\" : \"text\",", "    \"combo_box_choices\" : [ ]", "  } ],", "  \"fieldsinview\" : [ {", "    \"type_hint\" : \"includefieldinview\",", "    \"label\" : \"fieldLabel\",", "    \"search_document_field\" : \"searchfieldid\",", "    \"included_by_default\" : false", "  } ]", "}");

		SearchUIData obj = (SearchUIData) StandardObject.deserialize(obj_string);

		assertEquals("Label", obj.getSimpleAdvancedSearchFields().get(0).getSimpleLabel());
	}

	@Test
	public void testImmutableCreation()
	{
		List<AdvancedSearchField> search_field2 = new ArrayList<AdvancedSearchField>();
		search_field2.add(new AdvancedSearchField("Label", new SearchFieldId("SearchFieldId"), AdvancedSearchFieldType.TEXT, Collections.emptyList()));
		List<IncludeFieldInView> fields_in_view2 = Collections.singletonList(new IncludeFieldInView("fieldLabel", new SearchFieldId("SearchFieldId"), false));

		SearchUIData field = new SearchUIData(search_field2, fields_in_view2);
		assertTrue(field.getSimpleAdvancedSearchFields().size() == 1);
		search_field2.add(new AdvancedSearchField("Label2", new SearchFieldId("SearchFieldId2"), AdvancedSearchFieldType.TEXT, Collections.emptyList()));
		assertTrue(field.getSimpleAdvancedSearchFields().size() == 1);
	}

	@Test
	public void testUserComparisonAndEquals()
	{
		List<AdvancedSearchField> search_field = Collections.singletonList(new AdvancedSearchField("Label", new SearchFieldId("SearchFieldId"), AdvancedSearchFieldType.TEXT, Collections.emptyList()));
		List<IncludeFieldInView> fields_in_view = Collections.singletonList(new IncludeFieldInView("fieldLabel", new SearchFieldId("SearchFieldId"), false));
		SearchUIData field = new SearchUIData(search_field, fields_in_view);
		SearchUIData field_modified = new SearchUIData(search_field, fields_in_view);

		assertTrue(field.equals(field_modified));
		assertEquals(0, field.compareTo(field_modified));

		List<IncludeFieldInView> fields_in_view2 = Arrays.asList(new IncludeFieldInView("fieldLabel", new SearchFieldId("SearchFieldId"), false), new IncludeFieldInView("fieldLabel", new SearchFieldId("SearchFieldId"), false));
		field_modified = new SearchUIData(search_field, fields_in_view2);
		assertFalse(field.equals(field_modified));
		assertTrue(0 > field.compareTo(field_modified));

		fields_in_view2 = Collections.EMPTY_LIST;
		field_modified = new SearchUIData(search_field, fields_in_view2);
		assertFalse(field.equals(field_modified));
		assertTrue(0 < field.compareTo(field_modified));
	}

}
