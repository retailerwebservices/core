package org.jimmutable.cloud.elasticsearch;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import org.junit.BeforeClass;
import org.junit.Test;

public class SearchIndexConfigurationUtilsTest
{
	private static SearchIndexDefinition def;

	// Uncomment this to run unit test with elasticsearch running
	// @BeforeClass
	public static void setup()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("boolean"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("text"), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("atom"), SearchIndexFieldType.ATOM));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("day"), SearchIndexFieldType.DAY));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("float"), SearchIndexFieldType.FLOAT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("long"), SearchIndexFieldType.LONG));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("objectid"), SearchIndexFieldType.OBJECTID));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("juan:two:v2"));

		def = (SearchIndexDefinition) b.create(null);

	}

	// Uncomment this to run unit test with elasticsearch running
	// @Test
	public void upsert()
	{

		CloudExecutionEnvironment.startup(new ApplicationId("trevorApp"));

		SearchIndexConfigurationUtils util = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearchIndexConfigurationUtils();

		// SearchIndexConfigurationUtils util = new
		// SearchIndexConfigurationUtils(ElasticSearchEndpoint.CURRENT);
		util.upsertIndex(def);
	}

}
