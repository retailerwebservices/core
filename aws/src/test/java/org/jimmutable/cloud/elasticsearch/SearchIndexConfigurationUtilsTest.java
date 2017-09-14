package org.jimmutable.cloud.elasticsearch;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.cloud.StartupSingleton;
import org.jimmutable.cloud.elasticsearch.SearchIndexConfigurationUtils;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchIndexConfigurationUtilsTest
{
	private static SearchIndexDefinition def;

	// Uncomment this to run unit test with elasticsearch running
	@BeforeClass
	public static void setup() throws UnknownHostException
	{

		StartupSingleton.setupOnce();

		JimmutableTypeNameRegister.registerAllTypes();
		JimmutableCloudTypeNameRegister.registerAllTypes();

		StartupSingleton.setupOnce();

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("boolean"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("text"), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("atom"), SearchIndexFieldType.ATOM));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("day"), SearchIndexFieldType.DAY));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("float"), SearchIndexFieldType.FLOAT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("long"), SearchIndexFieldType.LONG));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("trevorApp:trevorIndex:v1"));

		def = (SearchIndexDefinition) b.create(null);

	}

	// Uncomment this to run unit test with elasticsearch running
	// @Test
	public void upsert() throws IOException
	{
		SearchIndexConfigurationUtils util = new SearchIndexConfigurationUtils(ElasticSearchEndpoint.CURRENT);

		util.upsertIndex(def);

	}

}
