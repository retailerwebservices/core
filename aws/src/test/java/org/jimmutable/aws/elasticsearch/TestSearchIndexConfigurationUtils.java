package org.jimmutable.aws.elasticsearch;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jimmutable.aws.StartupSingleton;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestSearchIndexConfigurationUtils
{
	private static SearchIndexDefinition def;

	@BeforeClass
	public static void setup() throws UnknownHostException
	{

		
		System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "INFO");
		StartupSingleton.setupOnce();

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);

		StartupSingleton.setupOnce();

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("spaghetti"), SearchIndexFieldType.BOOLEAN));
		b.add(SearchIndexDefinition.FIELD_FIELDS,
				new SearchIndexFieldDefinition(new FieldName("meatballs"), SearchIndexFieldType.TEXT));

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, new IndexDefinition("application_my-index"));

		def = (SearchIndexDefinition) b.create(null);

	}

	@Test
	public void upsert() throws IOException
	{
		SearchIndexConfigurationUtils util = new SearchIndexConfigurationUtils(new ElasticSearchEndpoint());

		util.upsertIndex(def);
		util.indexProperlyConfigured(def);

	}

	@Test
	public void properlyConfigured() throws UnknownHostException
	{
		
		
		
		
		

	}

}
