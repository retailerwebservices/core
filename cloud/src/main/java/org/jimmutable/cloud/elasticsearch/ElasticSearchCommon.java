package org.jimmutable.cloud.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.EnvironmentType;
import org.jimmutable.cloud.elasticsearch.ElasticSearchTransportClient.GenericStorableAndIndexable;
import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResult;
import org.jimmutable.cloud.servlet_utils.search.OneSearchResultWithTyping;
import org.jimmutable.cloud.servlet_utils.search.SearchFieldId;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseError;
import org.jimmutable.cloud.servlet_utils.search.SearchResponseOK;
import org.jimmutable.cloud.servlet_utils.search.SortBy;
import org.jimmutable.cloud.servlet_utils.search.SortDirection;
import org.jimmutable.cloud.servlet_utils.search.StandardSearchRequest;
import org.jimmutable.cloud.storage.IStorage;
import org.jimmutable.cloud.storage.StorageKey;
import org.jimmutable.cloud.storage.StorageKeyHandler;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.time.Instant;
import org.jimmutable.core.objects.common.time.TimeOfDay;
import org.jimmutable.core.serialization.FieldName;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvListWriter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO Once we're ready to fully deprecate the TransportClient used in Dev, the
 * methods in this class can replace the methods in the standard ElasticSearch
 * 
 * @author salvador.salazar
 *
 */
public class ElasticSearchCommon
{
	private static final Logger logger = LogManager.getLogger(ElasticSearchCommon.class);

	protected static final ExecutorService document_upsert_pool = (ExecutorService) new ThreadPoolExecutor(8, 8, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());

	public static final String ELASTICSEARCH_DEFAULT_TYPE = "default";
	public static final String SORT_FIELD_NAME_JIMMUTABLE = "jimmutable_sort_field";

	public static XContentBuilder getMappingBuilder( SearchIndexDefinition index, XContentBuilder default_value )
	{
		XContentBuilder mappingBuilder;
		try
		{
			mappingBuilder = jsonBuilder();

			mappingBuilder.startObject().startObject(ELASTICSEARCH_DEFAULT_TYPE).startObject("properties");
			for ( SearchIndexFieldDefinition field : index.getSimpleFields() )
			{
				mappingBuilder.startObject(field.getSimpleFieldName().getSimpleName());
				{
					mappingBuilder.field("type", field.getSimpleType().getSimpleSearchType());
				}
				mappingBuilder.endObject();

			}
			mappingBuilder.endObject().endObject().endObject();
			mappingBuilder.close();
			return mappingBuilder;
		}
		catch ( Exception e )
		{
			logger.log(Level.ERROR, String.format("Failed to generate mapping json for index %s", index.getSimpleIndex().getSimpleValue()), e);
			return default_value;
		}
	}
	
	
	protected static boolean shutdownDocumentUpsertThreadPool( int timeout_seconds )
	{
		long start = System.currentTimeMillis();

		document_upsert_pool.shutdown();

		boolean terminated = true;

		try
		{
			terminated = document_upsert_pool.awaitTermination(timeout_seconds, TimeUnit.SECONDS);
		}
		catch ( InterruptedException e )
		{
			logger.log(Level.FATAL, "Shutdown of runnable pool was interrupted!", e);
		}

		if ( !terminated )
		{
			logger.error(String.format("Failed to terminate in %s seconds. Calling shutdownNow...", timeout_seconds));
			document_upsert_pool.shutdownNow();
		}

		boolean success = document_upsert_pool.isTerminated();

		if ( success )
		{
			logger.warn(String.format("Successfully terminated pool in %s milliseconds", (System.currentTimeMillis() - start)));
		}
		else
		{
			logger.warn(String.format("Unsuccessful termination of pool in %s milliseconds", (System.currentTimeMillis() - start)));
		}
		return success;
	}

	/**
	 * Using a SortBy object, construct a SortBuilder used by ElasticSearch. This
	 * method handles the unique sorting cases for Text, Time of Day, and Instant.
	 * 
	 * @param sort_by
	 * @param default_value
	 * @return
	 */
	static public FieldSortBuilder getSort(SortBy sort_by, FieldSortBuilder default_value)
	{
		SortOrder order = null;
		if (sort_by.getSimpleDirection() == SortDirection.ASCENDING)
			order = SortOrder.ASC;
		if (sort_by.getSimpleDirection() == SortDirection.DESCENDING)
			order = SortOrder.DESC;

		if (order == null)
			return default_value;

		FieldName field_name = sort_by.getSimpleField().getSimpleFieldName();
		String sort_on_string = field_name.getSimpleName();

		if (sort_by.getSimpleField().getSimpleType() == SearchIndexFieldType.TEXT)
			sort_on_string = getSortFieldNameText(sort_by.getSimpleField().getSimpleFieldName()) + "." + SearchIndexFieldType.ATOM.getSimpleSearchType();
		if (sort_by.getSimpleField().getSimpleType() == SearchIndexFieldType.TIMEOFDAY)
			sort_on_string = getSortFieldNameTimeOfDay(sort_by.getSimpleField().getSimpleFieldName()) + "." + SearchIndexFieldType.TIMEOFDAY.getSimpleSearchType();;
		if (sort_by.getSimpleField().getSimpleType() == SearchIndexFieldType.INSTANT)
			sort_on_string = getSortFieldNameInstant(sort_by.getSimpleField().getSimpleFieldName()) + "." + SearchIndexFieldType.INSTANT.getSimpleSearchType();;

		return SortBuilders.fieldSort(sort_on_string).order(order).unmappedType(SearchIndexFieldType.ATOM.getSimpleSearchType());
	}

	/**
	 * Sorting on text fields is impossible without enabling fielddata in
	 * ElasticSearch. To get around this, we instead use a keyword field for every
	 * text field written. This solution is recommended by ElasticSearch over
	 * enabling fielddata. For more information, read here:
	 * 
	 * https://www.elastic.co/guide/en/elasticsearch/reference/5.4/fielddata.html#before-enabling-fielddata
	 * https://www.elastic.co/blog/support-in-the-wild-my-biggest-elasticsearch-problem-at-scale
	 * 
	 * @param field
	 * @return
	 */
	static public String getSortFieldNameText(FieldName field)
	{
		return getSortFieldNameText(field.getSimpleName());
	}

	/**
	 * Sorting on text fields is impossible without enabling fielddata in
	 * ElasticSearch. To get around this, we instead use a keyword field for every
	 * text field written. This solution is recommended by ElasticSearch over
	 * enabling fielddata. For more information, read here:
	 * 
	 * https://www.elastic.co/guide/en/elasticsearch/reference/5.4/fielddata.html#before-enabling-fielddata
	 * https://www.elastic.co/blog/support-in-the-wild-my-biggest-elasticsearch-problem-at-scale
	 * 
	 * @param field_name
	 * @return
	 */
	static public String getSortFieldNameText(String field_name)
	{
		return field_name + "_" + SORT_FIELD_NAME_JIMMUTABLE + "_" + SearchIndexFieldType.ATOM.getSimpleSearchType();
	}

	/**
	 * In order to sort TimeOfDay objects, we need to look at the ms_from_midnight
	 * field from within. This method creates a consistent field name to sort by.
	 * 
	 * @param field
	 * @return
	 */
	static public String getSortFieldNameTimeOfDay(FieldName field)
	{
		return getSortFieldNameTimeOfDay(field.getSimpleName());
	}

	/**
	 * In order to sort TimeOfDay objects, we need to look at the ms_from_midnight
	 * field from within. This method creates a consistent field name to sort by.
	 * 
	 * @param field_name
	 * @return
	 */
	static public String getSortFieldNameTimeOfDay(String field_name)
	{
		return field_name + "_" + SORT_FIELD_NAME_JIMMUTABLE + "_" + TimeOfDay.FIELD_MS_FROM_MIDNIGHT.getSimpleFieldName().getSimpleName();
	}

	/**
	 * In order to sort Instant objects, we need to look at the ms_from_epoch field
	 * from within. This method creates a consistent field name to sort by.
	 * 
	 * @param field
	 * @return
	 */
	static public String getSortFieldNameInstant(FieldName field)
	{
		return getSortFieldNameInstant(field.getSimpleName());
	}

	/**
	 * In order to sort Instant objects, we need to look at the ms_from_epoch field
	 * from within. This method creates a consistent field name to sort by.
	 * 
	 * @param field_name
	 * @return
	 */
	static public String getSortFieldNameInstant(String field_name)
	{
		return field_name + "_" + SORT_FIELD_NAME_JIMMUTABLE + "_" + Instant.FIELD_MS_FROM_EPOCH.getSimpleFieldName().getSimpleName();
	}
}