package org.jimmutable.cloud.elasticsearch;

import java.io.IOException;
import java.net.UnknownHostException;

<<<<<<< HEAD:cloud/src/test/java/org/jimmutable/cloud/elasticsearch/SearchIndexConfigurationUtilsTest.java
import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.cloud.StartupSingleton;
import org.jimmutable.cloud.elasticsearch.SearchIndexConfigurationUtils;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
=======
import org.jimmutable.aws.CloudExecutionEnvironment;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.storage.ApplicationId;
>>>>>>> origin/dev_tjb_refactor:aws/src/test/java/org/jimmutable/aws/elasticsearch/SearchIndexConfigurationUtilsTest.java
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchIndexConfigurationUtilsTest
{
	private static SearchIndexDefinition def;

	// Uncomment this to run unit test with elasticsearch running
	// @BeforeClass
	public static void setup() throws UnknownHostException
	{

		JimmutableTypeNameRegister.registerAllTypes();
		JimmutableCloudTypeNameRegister.registerAllTypes();

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("boolean"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("text"), SearchIndexFieldType.TEXT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("atom"), SearchIndexFieldType.ATOM));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("day"), SearchIndexFieldType.DAY));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("float"), SearchIndexFieldType.FLOAT));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("long"), SearchIndexFieldType.LONG));
		b.add(SearchIndexDefinition.FIELD_FIELDS, new SearchIndexFieldDefinition(new FieldName("objectid"), SearchIndexFieldType.OBJECTID));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("trevorApp:trevorIndex:v2"));

		def = (SearchIndexDefinition) b.create(null);

	}

	// Uncomment this to run unit test with elasticsearch running
	// @Test
	public void upsert() throws IOException
	{

		CloudExecutionEnvironment.startup(new ApplicationId("trevorApp"));

		SearchIndexConfigurationUtils util = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearchIndexConfigurationUtils();

		// SearchIndexConfigurationUtils util = new
		// SearchIndexConfigurationUtils(ElasticSearchEndpoint.CURRENT);
		util.upsertIndex(def);
	}

}
