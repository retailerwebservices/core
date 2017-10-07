package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.servlet_utils.search.AdvancedSearchComboBoxChoice;
import org.jimmutable.cloud.servlet_utils.search.IncludeFieldInView;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.Before;
import org.junit.Test;

public class AdvancedSearchComboBoxChoiceTest extends StubTest
{
	@Before
	public void before()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(IncludeFieldInView.class);
		ObjectParseTree.registerTypeName(AdvancedSearchComboBoxChoice.class);

	}

	@Test
	public void testUserSerialization()
	{
		AdvancedSearchComboBoxChoice field = new AdvancedSearchComboBoxChoice("Name", "Jim");
		String serialized_value = field.serialize(Format.JSON_PRETTY_PRINT);

		System.out.println(field.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		assertEquals("{\n" + "  \"type_hint\" : \"advancedsearchcomboboxchoice\",\n" + "  \"label\" : \"Name\",\n" + "  \"value\" : \"Jim\"\n" + "}", serialized_value);

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"advancedsearchcomboboxchoice\",", "  \"label\" : \"Name\",", "  \"value\" : \"Jim\"", "}");

		AdvancedSearchComboBoxChoice obj = (AdvancedSearchComboBoxChoice) StandardObject.deserialize(obj_string);

		assertEquals("Name", obj.getSimpleLabel());
	}

	@Test
	public void testUserComparisonAndEquals()
	{
		AdvancedSearchComboBoxChoice field = new AdvancedSearchComboBoxChoice("Name", "Andrew");
		AdvancedSearchComboBoxChoice field_modified = new AdvancedSearchComboBoxChoice("Name", "Andrew");

		assertTrue(field.equals(field_modified));
		assertEquals(0, field.compareTo(field_modified));

		field_modified = new AdvancedSearchComboBoxChoice("Name", "Trevor");
		assertFalse(field.equals(field_modified));
		assertTrue(0>field.compareTo(field_modified));

		field_modified = new AdvancedSearchComboBoxChoice("Name", "Aaron");
		assertFalse(field.equals(field_modified));
		assertTrue(0<field.compareTo(field_modified));
	}

}
