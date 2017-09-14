package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchTest
{

	// @BeforeClass
	public static void setup()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);

		CloudExecutionEnvironment.startup(new ApplicationId("searchtest"));
	}

	// @Test
	public void test1() throws InterruptedException
	{
		MyIndexable indexable = new MyIndexable();

		for (int i = 0; i < 20; i++) {
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(indexable);
		}

		for (int i = 0; i < 200; i++) {
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(indexable);
		}

	}

	// @Test
	public void testSearch()
	{
		MyIndexable indexable = new MyIndexable();
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(indexable.getSimpleSearchIndexDefinition(), "abc");
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(indexable.getSimpleSearchIndexDefinition(), "day:>2012-01-01");

		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(indexable.getSimpleSearchIndexDefinition(), "NINE");
	}

	// @AfterClass
	public static void shutdown()
	{
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdownThreadPool();

	}

}
