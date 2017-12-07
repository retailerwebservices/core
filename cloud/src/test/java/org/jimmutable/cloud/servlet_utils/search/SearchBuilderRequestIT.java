package org.jimmutable.cloud.servlet_utils.search;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.cloud.elasticsearch.MyIndexable;
import org.jimmutable.cloud.elasticsearch.SearchDocumentId;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchBuilderRequestIT extends IntegrationTest
{

	@BeforeClass
	public static void setup()
	{

		setupEnvironment();
		CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(MyIndexable.SEARCH_INDEX_DEFINITION);

		for (int i = 0; i < 20; i++)
		{
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocumentAsync(new MyIndexable(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex(), new SearchDocumentId(String.format("doc%s", i))));
		}

		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{

		}

	}

	@Test
	public void testInstantiation()
	{

		SearchRequestBuilder builder = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().getBuilder(MyIndexable.SEARCH_INDEX_DEFINITION.getSimpleIndex());
		builder.addAggregation(AggregationBuilders.max(MyIndexable.theDay.getSimpleFieldName().getSimpleName()));
		
		
		
		SearchBuilderRequest request = new SearchBuilderRequest(builder);

	}

}
