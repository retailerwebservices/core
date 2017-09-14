package org.jimmutable.aws.elasticsearch;

import org.jimmutable.aws.CloudExecutionEnvironment;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.storage.ApplicationId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchTest
{

	@BeforeClass
	public static void setup()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(SearchIndexFieldDefinition.class);
		ObjectParseTree.registerTypeName(SearchIndexDefinition.class);

		CloudExecutionEnvironment.startup(new ApplicationId("searchtest"));
	}

	@Test
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

	@AfterClass
	public static void shutdown()
	{
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().shutdown();
	}

}
