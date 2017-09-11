
package org.jimmutable.aws.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jimmutable.aws.StartupSingleton;
import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.Before;
import org.junit.Test;

public class SearchIndexDefinitionTest
{
	@Before
	public void before()
	{

		StartupSingleton.setupOnce();

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
	}

	@Test
	public void valid()
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		
		// CODE REVEIW: one line
		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("spaghetti"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("meatballs"), SearchIndexFieldType.TEXT));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("foo:BAR:v4"));

		SearchIndexDefinition def = (SearchIndexDefinition) b.create(null);

		assertNotNull(def);
		assertEquals(2, def.getSimpleFields().size());
		
		// CODE REVEIW: one line
		assertTrue(def.getSimpleFields()
				.contains(new SearchIndexFieldDefinition(new FieldName("spaghetti"), SearchIndexFieldType.BOOLEAN)));
		assertEquals("foo:bar:v4", def.getSimpleIndex().getSimpleValue());

	}

	@Test(expected = ValidationException.class)
	public void nullSearchIndexFeildDefintion()
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		// CODE REVEIW: one line
		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("spaghetti"), SearchIndexFieldType.BOOLEAN));
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

		// CODE REVEIW: one line
		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("spaghetti"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("meatballs"), SearchIndexFieldType.TEXT));

		b.create(null);

	}

	@Test
	public void serialize()
	{

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"org.jimmutable.aws.elasticsearch.SearchIndexDefinition\","
			     , "  \"index\" : \"foo:bar:v60\","
			     , "  \"fields\" : [ {"
			     , "    \"type_hint\" : \"org.jimmutable.aws.elasticsearch.SearchIndexFieldDefinition\","
			     , "    \"name\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"uno\""
			     , "    },"
			     , "    \"type\" : \"date\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.aws.elasticsearch.SearchIndexFieldDefinition\","
			     , "    \"name\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"dos\""
			     , "    },"
			     , "    \"type\" : \"keyword\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.aws.elasticsearch.SearchIndexFieldDefinition\","
			     , "    \"name\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"tres\""
			     , "    },"
			     , "    \"type\" : \"text\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.aws.elasticsearch.SearchIndexFieldDefinition\","
			     , "    \"name\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"quatro\""
			     , "    },"
			     , "    \"type\" : \"boolean\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.aws.elasticsearch.SearchIndexFieldDefinition\","
			     , "    \"name\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"cinco\""
			     , "    },"
			     , "    \"type\" : \"float\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.aws.elasticsearch.SearchIndexFieldDefinition\","
			     , "    \"name\" : {"
			     , "      \"type_hint\" : \"jimmutable.FieldName\","
			     , "      \"name\" : \"seis\""
			     , "    },"
			     , "    \"type\" : \"long\""
			     , "  } ]"
			     , "}"
			);

		SearchIndexDefinition obj = (SearchIndexDefinition) StandardObject.deserialize(obj_string);

		assertNotNull(obj);
		assertEquals("foo:bar:v60", obj.getSimpleIndex().getSimpleValue());
		assertEquals(6, obj.getSimpleFields().size());
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("uno"), SearchIndexFieldType.DAY)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("dos"), SearchIndexFieldType.ATOM)));	
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("tres"), SearchIndexFieldType.TEXT)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("quatro"), SearchIndexFieldType.BOOLEAN)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("cinco"), SearchIndexFieldType.FLOAT)));
		assertTrue(obj.getSimpleFields().contains(new SearchIndexFieldDefinition(new FieldName("seis"), SearchIndexFieldType.LONG)));
	}

}