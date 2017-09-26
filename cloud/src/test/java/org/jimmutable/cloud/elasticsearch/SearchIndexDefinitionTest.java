
package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.Before;
import org.junit.Test;

public class SearchIndexDefinitionTest
{
	@Before
	public void before()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
	}

	@Test
	public void valid()
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("ftext"), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("fatom"), SearchIndexFieldType.ATOM));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("fday"), SearchIndexFieldType.DAY));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("ffloat"), SearchIndexFieldType.FLOAT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("flong"), SearchIndexFieldType.LONG));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("fobjectid"), SearchIndexFieldType.OBJECTID));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("foo:BAR:v4"));

		SearchIndexDefinition def = (SearchIndexDefinition) b.create(null);

		assertNotNull(def);
		assertEquals(7, def.getSimpleFields().size());

		assertTrue(def.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN)));
		assertTrue(def.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("ftext"), SearchIndexFieldType.TEXT)));
		assertTrue(def.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("fatom"), SearchIndexFieldType.ATOM)));
		assertTrue(def.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("fday"), SearchIndexFieldType.DAY)));
		assertTrue(def.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("ffloat"), SearchIndexFieldType.FLOAT)));
		assertTrue(def.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("flong"), SearchIndexFieldType.LONG)));
		assertTrue(def.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("fobjectid"), SearchIndexFieldType.OBJECTID)));
		assertEquals("foo:bar:v4", def.getSimpleIndex().getSimpleValue());

	}

	@Test(expected = ValidationException.class)
	public void nullSearchIndexFeildDefintion()
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("spaghetti"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(null, SearchIndexFieldType.TEXT));

	}

	@Test
	public void emptyFields()
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("foo:bar:v2"));

		SearchIndexDefinition def = (SearchIndexDefinition) b.create(null);

		assertNotNull(def);
		assertEquals(0, def.getSimpleFields().size());

		assertEquals("foo:bar:v2", def.getSimpleIndex().getSimpleValue());

	}

	@Test(expected = SerializeException.class)
	public void serializationException()
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("spaghetti"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("meatballs"), SearchIndexFieldType.TEXT));

		b.create(null);

	}

	@Test
	public void serialize()
	{

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexDefinition\",", "  \"index\" : \"foo:bar:v4\",", "  \"fields\" : [ {", "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\",", "    \"name\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"fboolean\"", "    },", "    \"type\" : \"boolean\"", "  }, {", "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\",", "    \"name\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"ftext\"", "    },", "    \"type\" : \"text\"", "  }, {", "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\",", "    \"name\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"fatom\"", "    },", "    \"type\" : \"keyword\"", "  }, {", "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\",", "    \"name\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"fday\"", "    },", "    \"type\" : \"date\"", "  }, {", "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\",", "    \"name\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"ffloat\"", "    },", "    \"type\" : \"float\"", "  }, {", "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\",", "    \"name\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"flong\"", "    },", "    \"type\" : \"long\"", "  }, {", "    \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\",", "    \"name\" : {", "      \"type_hint\" : \"jimmutable.FieldName\",", "      \"name\" : \"fobjectid\"", "    },", "    \"type\" : \"text:keyword\"", "  } ]", "}");

		SearchIndexDefinition obj = (SearchIndexDefinition) StandardObject.deserialize(obj_string);

		assertNotNull(obj);
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("ftext"), SearchIndexFieldType.TEXT)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("fatom"), SearchIndexFieldType.ATOM)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("fday"), SearchIndexFieldType.DAY)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("ffloat"), SearchIndexFieldType.FLOAT)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("flong"), SearchIndexFieldType.LONG)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("fobjectid"), SearchIndexFieldType.OBJECTID)));
		assertEquals("foo:bar:v4", obj.getSimpleIndex().getSimpleValue());
	}

}