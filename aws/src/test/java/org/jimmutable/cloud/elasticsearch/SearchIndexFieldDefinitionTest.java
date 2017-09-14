package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.junit.Before;
import org.junit.Test;

public class SearchIndexFieldDefinitionTest
{

	@Before
	public void before()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		JimmutableCloudTypeNameRegister.registerAllTypes();
	}

	@Test
	public void valid()
	{

		Builder b = new Builder(SearchIndexFieldDefinition.TYPE_NAME);

		b.set(SearchIndexFieldDefinition.FIELD_FIELD_NAME, new FieldName("spaghetti"));
		b.set(SearchIndexFieldDefinition.FIELD_SEARCH_INDEX_FIELD_TYPE, SearchIndexFieldType.ATOM);

		SearchIndexFieldDefinition def = (SearchIndexFieldDefinition) b.create(null);

		assertNotNull(def);
		assertEquals("spaghetti", def.getSimpleFieldName().getSimpleName());
		assertEquals(SearchIndexFieldType.ATOM, def.getSimpleType());

	}

	@Test(expected = SerializeException.class)
	public void inValidNullType()
	{

		Builder b = new Builder(SearchIndexFieldDefinition.TYPE_NAME);

		b.set(SearchIndexFieldDefinition.FIELD_FIELD_NAME, new FieldName("spaghetti"));
		b.set(SearchIndexFieldDefinition.FIELD_SEARCH_INDEX_FIELD_TYPE, null);

		b.create(null);

	}

	@Test(expected = SerializeException.class)
	public void inValidNullFieldName()
	{
		Builder b = new Builder(SearchIndexFieldDefinition.TYPE_NAME);

		b.set(SearchIndexFieldDefinition.FIELD_FIELD_NAME, null);
		b.set(SearchIndexFieldDefinition.FIELD_SEARCH_INDEX_FIELD_TYPE, SearchIndexFieldType.ATOM);
		b.create(null);
	}

	@Test(expected = SerializeException.class)
	public void notSet()
	{
		Builder b = new Builder(SearchIndexFieldDefinition.TYPE_NAME);
		b.create(null);
	}

	@Test
	public void jsonSerialization()
	{

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\",", "  \"name\" : {", "    \"type_hint\" : \"jimmutable.FieldName\",", "    \"name\" : \"spaghetti\"", "  },", "  \"type\" : \"keyword\"", "}");

		SearchIndexFieldDefinition obj = (SearchIndexFieldDefinition) StandardObject.deserialize(obj_string);

		assertNotNull(obj);
		assertEquals("spaghetti", obj.getSimpleFieldName().getSimpleName());
		assertEquals(SearchIndexFieldType.ATOM, obj.getSimpleType());

	}

}
