
package org.jimmutable.cloud.servlet_utils.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.junit.Before;
import org.junit.Test;

public class SortTest
{

	@Before
	public void before()
	{
		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(Sort.class);
		ObjectParseTree.registerTypeName(SortBy.class);
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
	}

	@Test
	public void valid()
	{
		Builder b = new Builder(Sort.TYPE_NAME);
		b.add(Sort.FIELD_SORT_ORDER, new SortBy(new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN), SortDirection.ASCENDING));
		b.add(Sort.FIELD_SORT_ORDER, new SortBy(new SearchIndexFieldDefinition(new FieldName("ftext"), SearchIndexFieldType.TEXT), SortDirection.DESCENDING));
		b.add(Sort.FIELD_SORT_ORDER, new SortBy(new SearchIndexFieldDefinition(new FieldName("fatom"), SearchIndexFieldType.ATOM), SortDirection.ASCENDING));
		b.add(Sort.FIELD_SORT_ORDER, new SortBy(new SearchIndexFieldDefinition(new FieldName("fday"), SearchIndexFieldType.DAY), SortDirection.DESCENDING));
		b.add(Sort.FIELD_SORT_ORDER, new SortBy(new SearchIndexFieldDefinition(new FieldName("ffloat"), SearchIndexFieldType.FLOAT), SortDirection.ASCENDING));
		b.add(Sort.FIELD_SORT_ORDER, new SortBy(new SearchIndexFieldDefinition(new FieldName("flong"), SearchIndexFieldType.LONG), SortDirection.DESCENDING));

		Sort sort = (Sort) b.create(null);

		// System.out.println(sort.toJavaCode(Format.JSON_PRETTY_PRINT, "def"));

		assertNotNull(sort);
		assertEquals(6, sort.getSimpleSortOrder().size());

		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN), SortDirection.ASCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("ftext"), SearchIndexFieldType.TEXT), SortDirection.DESCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("fatom"), SearchIndexFieldType.ATOM), SortDirection.ASCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("fday"), SearchIndexFieldType.DAY), SortDirection.DESCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("ffloat"), SearchIndexFieldType.FLOAT), SortDirection.ASCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("flong"), SearchIndexFieldType.LONG), SortDirection.DESCENDING)));
	}

	@Test(expected = ValidationException.class)
	public void nullSortBy()
	{
		Builder b = new Builder(Sort.TYPE_NAME);

		b.add(Sort.FIELD_SORT_ORDER, new SortBy(new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN), SortDirection.ASCENDING));
		b.add(Sort.FIELD_SORT_ORDER, new SortBy(new SearchIndexFieldDefinition(null, SearchIndexFieldType.TEXT), SortDirection.DESCENDING));
	}

	@Test
	public void emptyFields()
	{
		Builder b = new Builder(Sort.TYPE_NAME);

		Sort def = (Sort) b.create(null);

		assertNotNull(def);
		assertEquals(0, def.getSimpleSortOrder().size());
	}

	@Test
	public void serialize()
	{
		String def_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.Sort\","
			     , "  \"sort_order\" : [ {"
			     , "    \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.SortBy\","
			     , "    \"field\" : {"
			     , "      \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
			     , "      \"name\" : {"
			     , "        \"type_hint\" : \"jimmutable.FieldName\","
			     , "        \"name\" : \"fboolean\""
			     , "      },"
			     , "      \"type\" : \"boolean\""
			     , "    },"
			     , "    \"direction\" : \"ascending\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.SortBy\","
			     , "    \"field\" : {"
			     , "      \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
			     , "      \"name\" : {"
			     , "        \"type_hint\" : \"jimmutable.FieldName\","
			     , "        \"name\" : \"ftext\""
			     , "      },"
			     , "      \"type\" : \"text\""
			     , "    },"
			     , "    \"direction\" : \"descending\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.SortBy\","
			     , "    \"field\" : {"
			     , "      \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
			     , "      \"name\" : {"
			     , "        \"type_hint\" : \"jimmutable.FieldName\","
			     , "        \"name\" : \"fatom\""
			     , "      },"
			     , "      \"type\" : \"keyword\""
			     , "    },"
			     , "    \"direction\" : \"ascending\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.SortBy\","
			     , "    \"field\" : {"
			     , "      \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
			     , "      \"name\" : {"
			     , "        \"type_hint\" : \"jimmutable.FieldName\","
			     , "        \"name\" : \"fday\""
			     , "      },"
			     , "      \"type\" : \"date\""
			     , "    },"
			     , "    \"direction\" : \"descending\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.SortBy\","
			     , "    \"field\" : {"
			     , "      \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
			     , "      \"name\" : {"
			     , "        \"type_hint\" : \"jimmutable.FieldName\","
			     , "        \"name\" : \"ffloat\""
			     , "      },"
			     , "      \"type\" : \"float\""
			     , "    },"
			     , "    \"direction\" : \"ascending\""
			     , "  }, {"
			     , "    \"type_hint\" : \"org.jimmutable.cloud.servlet_utils.search.SortBy\","
			     , "    \"field\" : {"
			     , "      \"type_hint\" : \"org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition\","
			     , "      \"name\" : {"
			     , "        \"type_hint\" : \"jimmutable.FieldName\","
			     , "        \"name\" : \"flong\""
			     , "      },"
			     , "      \"type\" : \"long\""
			     , "    },"
			     , "    \"direction\" : \"descending\""
			     , "  } ]"
			     , "}"
			);

		Sort sort = (Sort) StandardObject.deserialize(def_string);
		assertNotNull(sort);
		assertEquals(6, sort.getSimpleSortOrder().size());

		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("fboolean"), SearchIndexFieldType.BOOLEAN), SortDirection.ASCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("ftext"), SearchIndexFieldType.TEXT), SortDirection.DESCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("fatom"), SearchIndexFieldType.ATOM), SortDirection.ASCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("fday"), SearchIndexFieldType.DAY), SortDirection.DESCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("ffloat"), SearchIndexFieldType.FLOAT), SortDirection.ASCENDING)));
		assertTrue(sort.getSimpleSortOrder().contains(new SortBy(new SearchIndexFieldDefinition(new FieldName("flong"), SearchIndexFieldType.LONG), SortDirection.DESCENDING)));
	}

}