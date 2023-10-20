package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.EnvironmentType;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.IndexId;
import org.jimmutable.cloud.elasticsearch.IndexVersion;
import org.jimmutable.cloud.elasticsearch.Indexable;
import org.jimmutable.cloud.elasticsearch.SearchDocumentId;
import org.jimmutable.cloud.elasticsearch.SearchDocumentWriter;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectText;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldCollection;
import org.jimmutable.core.objects.JimmutableBuilder;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;

/**
 * The purpose of this class is to test our
 * Search implementation, specifically Elasticsearch.
 * 
 * This is a manually executed Integration Test.
 * 
 * @author preston.mccumber
 *
 */
class SearchSortIntegrationTestV2
{
	private static final Logger logger = LoggerFactory.getLogger(SearchSortIntegrationTestV2.class);
	public static final String APPLICATION_ID = "SearchSortIntegrationTest";
	public static final String APPLICATION_SERVICE_ID = "SearchSortIntegrationTest-service";
	private static final Random RANDOM = new Random();
	
	private static boolean test_failed = false;
    

	public static void main(String args[])
	{		
    	
        CloudExecutionEnvironment.startup(new ApplicationId("SearchSortIntegrationTest"),
            new ApplicationId("SearchSortIntegrationTest-service"), EnvironmentType.DEV);

        // Add object types to be registered
        ObjectParseTree.registerTypeName(SearchSortObjectText.class);
        ObjectParseTree.registerTypeName(SearchSortObjectLongV2.class);
        ObjectParseTree.registerTypeName(SearchSortObjectFloatV2.class);
        ObjectParseTree.registerTypeName(SearchSortObjectBooleanV2.class);
        ObjectParseTree.registerTypeName(SearchSortObjectDayV2.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectInstant.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectTimeOfDay.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectTextArray.class);
        ObjectParseTree.registerTypeName(SearchSortObjectLongArray.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectFloatArray.class);
        
        if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleEnvironmentType().equals(EnvironmentType.DEV) )
		{				
			ArrayList<SearchIndexDefinition> classes = new ArrayList<SearchIndexDefinition>();
			Collections.addAll(classes, SearchSortObjectLongV2.INDEX_MAPPING, /* SearchSortObjectLongArray.INDEX_MAPPING), */ SearchSortObjectTextV2.INDEX_MAPPING, //, SearchSortObjectFloat.INDEX_MAPPING, SearchSortObjectBoolean.INDEX_MAPPING, 
					SearchSortObjectDayV2.INDEX_MAPPING); //, SearchSortObjectInstant.INDEX_MAPPING, SearchSortObjectTimeOfDay.INDEX_MAPPING);
			
			for ( SearchIndexDefinition definition : classes )
			{
				if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(definition) )
				{
					logger.warn(String.format("Upserting index:%s", definition.getSimpleIndex().getSimpleValue()));
					CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(definition);
					try
					{
						Thread.sleep(500);
					}
					catch ( InterruptedException e )
					{
					}
				}
			}	
			try
			{
				// Make sure indices are empty before starting
				cleanup();
				
				if (!testSearchSortObjectText())
				{
					test_failed = true;
				}
				
				if (!testSearchSortObjectLong())
				{
					test_failed = true;
				}
				
				if (!testSearchSortObjectDay())
				{
					test_failed = true;
				}
				
				if (!testSearchSortObjectLongArray())
				{
					test_failed = true;
				}

				if (!testSearchSortObjectFloat())
				{
					test_failed = true;
				}

				if (!testSearchSortObjectBoolean())
				{
					test_failed = true;
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace();
				
				test_failed = true;
			}
			
		}
		
        cleanup();
        
        if (test_failed)
        {
        	logger.error("========================= FAILURE =========================");
        	logger.error("=====     FAILURE: ERROR RUNNING INTEGRATION TEST     =====");
        	logger.error("========================= FAILURE =========================");
        	System.exit(1);
        }
        
		System.out.println();
		logger.info("*****************************************");
		logger.info("             Test Complete!              ");
		logger.info("*****************************************");
		System.exit(0);
    }

    
    private static void cleanup()
    {
    	if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(SearchSortObjectLongV2.INDEX_MAPPING))
    	{
    		deleteIndexEntries(SearchSortObjectLongV2.SEARCH_FIELD_ID.getSimpleFieldName(), SearchSortObjectLongV2.INDEX_DEFINITION);
    	}
    	
    	if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(SearchSortObjectText.INDEX_MAPPING))
    	{
    		deleteIndexEntries(SearchSortObjectLongV2.SEARCH_FIELD_ID.getSimpleFieldName(), SearchSortObjectText.INDEX_DEFINITION);
    	}
    	
    	if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(SearchSortObjectText.INDEX_MAPPING))
    	{
    		deleteIndexEntries(SearchSortObjectDayV2.SEARCH_FIELD_ID.getSimpleFieldName(), SearchSortObjectDayV2.INDEX_DEFINITION);
    	}

		if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(SearchSortObjectFloatV2.INDEX_MAPPING))
		{
			deleteIndexEntries(SearchSortObjectFloatV2.SEARCH_FIELD_ID.getSimpleFieldName(), SearchSortObjectFloatV2.INDEX_DEFINITION);
		}

		if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(SearchSortObjectBooleanV2.INDEX_MAPPING))
		{
			deleteIndexEntries(SearchSortObjectBooleanV2.SEARCH_FIELD_ID.getSimpleFieldName(), SearchSortObjectBooleanV2.INDEX_DEFINITION);
		}
    }
    

    
    private static boolean testSearchSortObjectLong() throws Exception
    {
    	String test_object = "SearchSortObjectLong";
    	boolean is_successful = true;
    	
    	Map<ObjectId, SearchSortObjectLongV2> test_objects = new HashMap<>();
    	
		for ( long i = 9; i >= 0; i-- )
		{					
			SearchSortObjectLongV2 obj = new SearchSortObjectLongV2(ObjectId.createRandomId(), i);
			test_objects.put(obj.getSimpleObjectId(), obj);
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}
    	
		String query = String.format("%s:%s", SearchSortObjectLongV2.SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*");
		
		// Test Sort LONG Ascending
		SortBy sort_by = new SortBy(SearchSortObjectLongV2.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
		List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLongV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		if (test_objects.size() != results.size())
		{
			outputError(test_object, String.format("Incorrect number of entries retrieved from search. Expected: %s, Actual: %s", test_objects.size(), results.size()));
			is_successful = false;
		}
		
		long prev_long_value = -1;
		for ( OneSearchResultWithTyping result : results )
		{
			ObjectId id = new ObjectId(result.readAsAtom(SearchSortObjectLongV2.SEARCH_FIELD_ID.getSimpleFieldName(), null));
			long long_value = result.readAsLong(SearchSortObjectLongV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), -1L);
			
			if (!(long_value == test_objects.get(id).getSimpleValue()) )
			{
				outputError(test_object, String.format("Value from search doesn't match expected value. Expected: %s, Actual: %s", test_objects.get(id).getSimpleValue(), long_value));
				is_successful = false;
			}
			
			if (!(long_value > prev_long_value))
			{
				outputError(test_object, String.format("Incorrect ascending sort sequence. Prev: %s, Curr: %s", prev_long_value, long_value));
				is_successful = false;
			}
			
			logger.info(String.format("Current value: %s, Previous value: %s", long_value, prev_long_value));

			prev_long_value = long_value;
		}
		
		// Test Sort LONG Descending
		sort_by = new SortBy(SearchSortObjectLongV2.SEARCH_FIELD_VALUE, SortDirection.DESCENDING);
		results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLongV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		prev_long_value = 10;
		for ( OneSearchResultWithTyping result : results )
		{
			long long_value = result.readAsLong(SearchSortObjectLongV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), -1L);

			if (!(long_value < prev_long_value))
			{
				outputError(test_object, String.format("Incorrect descending sort sequence. Prev: %s, Curr: %s", prev_long_value, long_value));
				is_successful = false;
			}

			prev_long_value = long_value;
		}
		
		return is_successful;
    }

	private static boolean testSearchSortObjectFloat() throws Exception
	{
		String test_object = "SearchSortObjectFloat";
		boolean is_successful = true;

		Map<ObjectId, SearchSortObjectFloatV2> test_objects = new HashMap<>();

		for ( float i = 9; i >= 0; i-- )
		{
			SearchSortObjectFloatV2 obj = new SearchSortObjectFloatV2(ObjectId.createRandomId(), i);
			test_objects.put(obj.getSimpleObjectId(), obj);
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}

		String query = String.format("%s:%s", SearchSortObjectFloatV2.SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*");

		// Test Sort FLOAT Ascending
		SortBy sort_by = new SortBy(SearchSortObjectLongV2.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
		List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectFloatV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);

		if (test_objects.size() != results.size())
		{
			outputError(test_object, String.format("Incorrect number of entries retrieved from search. Expected: %s, Actual: %s", test_objects.size(), results.size()));
			is_successful = false;
		}

		logger.info("Sort FLOAT Ascending search results:");
		float prev_float_value = -1;
		for ( OneSearchResultWithTyping result : results )
		{
			ObjectId id = new ObjectId(result.readAsAtom(SearchSortObjectFloatV2.SEARCH_FIELD_ID.getSimpleFieldName(), null));
			float float_value = result.readAsFloat(SearchSortObjectFloatV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), -1F);

			if (!(float_value == test_objects.get(id).getSimpleValue()) )
			{
				outputError(test_object, String.format("Value from search doesn't match expected value. Expected: %s, Actual: %s", test_objects.get(id).getSimpleValue(), float_value));
				is_successful = false;
			}

			if (!(float_value > prev_float_value))
			{
				outputError(test_object, String.format("Incorrect ascending sort sequence. Prev: %s, Curr: %s", prev_float_value, float_value));
				is_successful = false;
			}

			logger.info(String.format("Current value: %s, Previous value: %s", float_value, prev_float_value));

			prev_float_value = float_value;
		}

		// Test Sort FLOAT Descending
		sort_by = new SortBy(SearchSortObjectFloatV2.SEARCH_FIELD_VALUE, SortDirection.DESCENDING);
		results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectFloatV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);

		logger.info("Sort FLOAT Descending search results:");
		prev_float_value = 10;
		for ( OneSearchResultWithTyping result : results )
		{
			float float_value = result.readAsFloat(SearchSortObjectFloatV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), -1F);

			if (!(float_value < prev_float_value))
			{
				outputError(test_object, String.format("Incorrect descending sort sequence. Prev: %s, Curr: %s", prev_float_value, float_value));
				is_successful = false;
			}

			logger.info(String.format("Current value: %s, Previous value: %s", float_value, prev_float_value));

			prev_float_value = float_value;
		}

		return is_successful;
	}

	private static boolean testSearchSortObjectBoolean() throws Exception
	{
		String test_object = "SearchSortObjectBoolean";
		boolean is_successful = true;

		Map<ObjectId, SearchSortObjectBooleanV2> test_objects = new HashMap<>();

		for ( int i = 9; i >= 0; i-- )
		{
			boolean bool_val = ((i % 2) == 0); //Alternate true and false
			SearchSortObjectBooleanV2 obj = new SearchSortObjectBooleanV2(ObjectId.createRandomId(), bool_val);
			test_objects.put(obj.getSimpleObjectId(), obj);
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}

		String query = String.format("%s:%s", SearchSortObjectBooleanV2.SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*");

		// Test Sort Boolean Ascending
		SortBy sort_by = new SortBy(SearchSortObjectBooleanV2.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
		List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectBooleanV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);

		if (test_objects.size() != results.size())
		{
			outputError(test_object, String.format("Incorrect number of entries retrieved from search. Expected: %s, Actual: %s", test_objects.size(), results.size()));
			is_successful = false;
		}

		logger.info("Sort BOOLEAN Ascending search results:");
		boolean prev_bool_value = false;
		for ( OneSearchResultWithTyping result : results )
		{
			ObjectId id = new ObjectId(result.readAsAtom(SearchSortObjectBooleanV2.SEARCH_FIELD_ID.getSimpleFieldName(), null));
			boolean bool_value = result.readAsBoolean(SearchSortObjectBooleanV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), false);

			if (!(bool_value == test_objects.get(id).getSimpleValue()) )
			{
				outputError(test_object, String.format("Value from search doesn't match expected value. Expected: %s, Actual: %s", test_objects.get(id).getSimpleValue(), bool_value));
				is_successful = false;
			}

			if (Boolean.compare(prev_bool_value, bool_value) > 0)
			{
				outputError(test_object, String.format("Incorrect ascending sort sequence. Prev: %s, Curr: %s", prev_bool_value, bool_value));
				is_successful = false;
			}

			logger.info(String.format("Current value: %s, Previous value: %s", bool_value, prev_bool_value));

			prev_bool_value = bool_value;
		}

		// Test Sort BOOLEAN Descending
		sort_by = new SortBy(SearchSortObjectBooleanV2.SEARCH_FIELD_VALUE, SortDirection.DESCENDING);
		results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectBooleanV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);

		logger.info("Sort BOOLEAN Descending search results:");
		prev_bool_value = true;
		for ( OneSearchResultWithTyping result : results )
		{
			boolean bool_value = result.readAsBoolean(SearchSortObjectBooleanV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), false);

			if (Boolean.compare(prev_bool_value, bool_value) < 0)
			{
				outputError(test_object, String.format("Incorrect descending sort sequence. Prev: %s, Curr: %s", prev_bool_value, bool_value));
				is_successful = false;
			}

			logger.info(String.format("Current value: %s, Previous value: %s", bool_value, prev_bool_value));

			prev_bool_value = bool_value;
		}

		return is_successful;
	}

    
    private static boolean testSearchSortObjectLongArray() throws Exception
    {
    	String test_object = "SearchSortObjectLongArray";
    	boolean is_successful = true;
    	
    	Map<ObjectId, SearchSortObjectLongArray> test_objects = new HashMap<>();
    	
    	
		for ( long i = 9; i >= 0; i-- )
		{
			List<Long> collection = new ArrayList<>();
			collection.add(i);
			collection.add(i+10);
			SearchSortObjectLongArray obj = new SearchSortObjectLongArray(ObjectId.createRandomId(), collection);
			test_objects.put(obj.getSimpleObjectId(), obj);
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}
    	
		String query = String.format("%s:%s", SearchSortObjectLongArray.SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*");
		
		// Test Sort LONG Ascending
		SortBy sort_by = new SortBy(SearchSortObjectLongArray.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
		List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLongV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		if (test_objects.size() != results.size())
		{
			outputError(test_object, String.format("Incorrect number of entries retrieved from search. Expected: %s, Actual: %s", test_objects.size(), results.size()));
			is_successful = false;
		}
		
		long prev_long_value = -1;
		for ( OneSearchResultWithTyping result : results )
		{
			ObjectId id = new ObjectId(result.readAsAtom(SearchSortObjectLongArray.SEARCH_FIELD_ID.getSimpleFieldName(), null));
			long[] values = result.readAsLongArray(SearchSortObjectLongArray.SEARCH_FIELD_VALUE.getSimpleFieldName(), null);
			
//			for (long long_value : values)
//			{
//			if (!(long_value == test_objects.get(id).getSimpleValue()) )
//			{
//				outputError(test_object, String.format("Value from search doesn't match expected value. Expected: %s, Actual: %s", test_objects.get(id).getSimpleValue(), long_value));
//				is_successful = false;
//			}
//			
//			if (!(long_value > prev_long_value))
//			{
//				outputError(test_object, String.format("Incorrect ascending sort sequence. Prev: %s, Curr: %s", prev_long_value, long_value));
//				is_successful = false;
//			}
//			}
//
//			prev_long_value = long_value;
		}
		
		// Test Sort LONG Descending
		sort_by = new SortBy(SearchSortObjectLongArray.SEARCH_FIELD_VALUE, SortDirection.DESCENDING);
		results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLongArray.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		prev_long_value = 10;
		for ( OneSearchResultWithTyping result : results )
		{
			ObjectId id = new ObjectId(result.readAsAtom(SearchSortObjectLongArray.SEARCH_FIELD_ID.getSimpleFieldName(), null));
			long[] values = result.readAsLongArray(SearchSortObjectLongArray.SEARCH_FIELD_VALUE.getSimpleFieldName(), null);
		}
		
		return is_successful;
    }
    
    private static boolean testSearchSortObjectText() throws Exception
    {
    	String test_object = "SearchSortObjectText";
    	boolean is_successful = true;
    	
    	// Create test entries
    	Map<ObjectId, SearchSortObjectTextV2> test_objects = new HashMap<>();
    	
		for ( char ch = 'j' ; ch >= 'a' ; ch-- )
		{					
			SearchSortObjectTextV2 obj = new SearchSortObjectTextV2(ObjectId.createRandomId(), String.valueOf(ch));
			test_objects.put(obj.getSimpleObjectId(), obj);
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}
    	
		// Test Sort TEXT Ascending
		String query = String.format("%s:%s", SearchSortObjectText.SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*");
		
		SortBy sort_by = new SortBy(SearchSortObjectText.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
		List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectText.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		if (results == null)
		{
			outputError(test_object, String.format("No results found for search request. Expected: %s, Actual: %s", test_objects.size(), 0));
			is_successful = false;
		}
		else
		{
			if (test_objects.size() != results.size())
			{
				outputError(test_object, String.format("Incorrect number of entries retrieved from search. Expected: %s, Actual: %s", test_objects.size(), results.size()));
				is_successful = false;
			}
			
			logger.info("Sort TEXT Ascending search results:");
			String prev_text_value = "";
			for ( OneSearchResultWithTyping result : results )
			{
				ObjectId id = new ObjectId(result.readAsAtom(SearchSortObjectTextV2.SEARCH_FIELD_ID.getSimpleFieldName(), null));
				String text_value = result.readAsText(SearchSortObjectTextV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), null);
				
				if (!(text_value == null || text_value.equals(test_objects.get(id).getSimpleValue())) )
				{
					outputError(test_object, String.format("Value from search doesn't match expected value. Expected: %s, Actual: %s", test_objects.get(id).getSimpleValue(), text_value));
					is_successful = false;
				}
				
				if (text_value.compareTo(prev_text_value) <= 0)
				{
					outputError(test_object, String.format("Incorrect ascending sort sequence. Prev: %s, Curr: %s", prev_text_value, text_value));
					is_successful = false;
				}
				
				logger.info(String.format("Current value: %s, Previous value: %s", text_value, prev_text_value));
	
				prev_text_value = text_value;
			}
		}
		
		// Test Sort TEXT Descending
		sort_by = new SortBy(SearchSortObjectText.SEARCH_FIELD_VALUE, SortDirection.DESCENDING);
		results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectText.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		if (results == null)
		{
			outputError(test_object, String.format("No results found for search request. Expected: %s, Actual: %s", test_objects.size(), 0));
			is_successful = false;
		}
		else
		{
			logger.info("Sort TEXT Descending search results:");
			
			String prev_text_value = "zzz";
			for ( OneSearchResultWithTyping result : results )
			{
				String text_value = result.readAsText(SearchSortObjectText.SEARCH_FIELD_VALUE.getSimpleFieldName(), null);
	
				logger.info(String.format("Current value: %s, Previous value: %s", text_value, prev_text_value));
				
				if (text_value.compareTo(prev_text_value) >= 0)
				{
					outputError(test_object, String.format("Incorrect descending sort sequence. Prev: %s, Curr: %s", prev_text_value, text_value));
					is_successful = false;
				}
	
				prev_text_value = text_value;
			}
		}
		
		return is_successful;
    }
    
    private static boolean testSearchSortObjectDay() throws Exception
    {
    	String test_object = "SearchSortObjectDay";
    	boolean is_successful = true;
    	
    	// Create test entries
    	Map<ObjectId, SearchSortObjectDayV2> test_objects = new HashMap<>();
    	Day start_day = new Day("12/29/2024");
    	
    	IntStream.range(0, 10).forEach(i -> 
    	{
    		SearchSortObjectDayV2 obj = new SearchSortObjectDayV2(ObjectId.createRandomId(), start_day.createSimpleAddDays(i));
			test_objects.put(obj.getSimpleObjectId(), obj);
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
    	}
    	);
    	
		// Test Sort DAY Ascending
		String query = String.format("%s:%s", SearchSortObjectDayV2.SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*");
		
		SortBy sort_by = new SortBy(SearchSortObjectDayV2.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
		List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectDayV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		if (results == null)
		{
			outputError(test_object, String.format("No results found for search request. Expected: %s, Actual: %s", test_objects.size(), 0));
			is_successful = false;
		}
		else
		{
			if (test_objects.size() != results.size())
			{
				outputError(test_object, String.format("Incorrect number of entries retrieved from search. Expected: %s, Actual: %s", test_objects.size(), results.size()));
				is_successful = false;
			}
			
			logger.info("Sort DAY Ascending search results:");
			Day prev_day_value = new Day("01/01/0001");
			for ( OneSearchResultWithTyping result : results )
			{
				ObjectId id = new ObjectId(result.readAsAtom(SearchSortObjectDayV2.SEARCH_FIELD_ID.getSimpleFieldName(), null));
				Day day_value = result.readAsDay(SearchSortObjectDayV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), null);
				
				if (!(day_value == null || day_value.equals(test_objects.get(id).getSimpleValue())) )
				{
					outputError(test_object, String.format("Value from search doesn't match expected value. Expected: %s, Actual: %s", test_objects.get(id).getSimpleValue(), day_value));
					is_successful = false;
				}
				
				if (!day_value.isAfter(prev_day_value))
				{
					outputError(test_object, String.format("Incorrect ascending sort sequence. Prev: %s, Curr: %s", prev_day_value, day_value));
					is_successful = false;
				}
				
				logger.info(String.format("Current value: %s, Previous value: %s", day_value, prev_day_value));
	
				prev_day_value = day_value;
			}
		}
		
		// Test Sort DAY Descending
		sort_by = new SortBy(SearchSortObjectDayV2.SEARCH_FIELD_VALUE, SortDirection.DESCENDING);
		results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectDayV2.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		if (results == null)
		{
			outputError(test_object, String.format("No results found for search request. Expected: %s, Actual: %s", test_objects.size(), 0));
			is_successful = false;
		}
		else
		{
			logger.info("Sort DAY Descending search results:");
			
			Day prev_day_value = new Day("12/31/9999");
			for ( OneSearchResultWithTyping result : results )
			{
				Day day_value = result.readAsDay(SearchSortObjectDayV2.SEARCH_FIELD_VALUE.getSimpleFieldName(), null);
	
				logger.info(String.format("Current value: %s, Previous value: %s", day_value, prev_day_value));
				
				if (!day_value.isBefore(prev_day_value))
				{
					outputError(test_object, String.format("Incorrect descending sort sequence. Prev: %s, Curr: %s", prev_day_value, day_value));
					is_successful = false;
				}
	
				prev_day_value = day_value;
			}
		}
		
		return is_successful;
    }

    private static void outputError(String test_class, String error_msg)
    {
    	logger.error(String.format("FAILURE - Test Class:%s   %s", test_class, error_msg));
    }
    
    static public class SearchSortObjectTextV2 extends StandardImmutableObject<SearchSortObjectTextV2> implements Indexable
	{	
    	static public final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
		static public final FieldDefinition.String FIELD_VALUE = new FieldDefinition.String("value", null);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectText");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("text"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.ATOM);
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.TEXT);		
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private ObjectId id;
		private String value;
		
		static
		{
			JimmutableBuilder b = new JimmutableBuilder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ID);
			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create();
		}
		
		public SearchSortObjectTextV2(ObjectId id, String value)
		{
			this.id = id;
			this.value = value;
			complete();
		}
		
		public ObjectId getSimpleObjectId() { return id; }
		public String getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(id.getSimpleValue());
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeAtom(SEARCH_FIELD_ID, id.getSimpleValue());
			writer.writeText(SEARCH_FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectTextV2 o) 
		{
			int ret = Comparison.startCompare();
			ret = Comparison.continueCompare(ret, getSimpleObjectId(), o.getSimpleObjectId());
			ret = Comparison.continueCompare(ret, getSimpleValue(), o.getSimpleValue());
			
			return ret;
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeStringable(FIELD_ID, id);
			writer.writeString(FIELD_VALUE, value);			
		}

		@Override
		public void freeze() 
		{
		}

		@Override
		public void normalize() 
		{
		}

		@Override
		public void validate() 
		{
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(id, value);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SearchSortObjectTextV2 other = (SearchSortObjectTextV2) obj;
			return Objects.equals(id, other.id) && Objects.equals(value, other.value);
		}
		
//		public static void upsertData()
//		{
//			List<String> alphabet = new ArrayList<>();
//			for ( char ch = 'a' ; ch <= 'z' ; ch++ )
//			{
//				alphabet.add(String.valueOf(ch));
//			}
//				
//			for ( int i = 0; i < 100; i++ )
//			{
//				String value = "";
//				for ( int j =  0; j < 10; j++ )
//				{
//					value += alphabet.get(RANDOM.nextInt(26));
//				}
//				
//				SearchSortObjectText obj = new SearchSortObjectText(value);
//				
//				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
//			}
//			
//			// Edge cases
//			// Acid String
//			SearchSortObjectText obj = new SearchSortObjectText(createAcidString());			
//			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
//		}
//		
//		public static void doSearch()
//		{
//			SortBy sort_by = new SortBy(SearchSortObjectText.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
//			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectText.INDEX_DEFINITION, 
//					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "a*"), 10000, 0, new Sort(sort_by)), null);
//			
//			for ( OneSearchResultWithTyping result : results )
//			{
//				System.out.println(result.readAsText(SEARCH_FIELD_VALUE.getSimpleFieldName(), null));
//			}
//		}
	}
    
	public static class SearchSortObjectLongV2 extends StandardImmutableObject<SearchSortObjectLongV2> implements Indexable
	{	
		static public final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
		static public final FieldDefinition.Long FIELD_VALUE = new FieldDefinition.Long("value", -1L);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectLong");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("long"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.ATOM);	
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.LONG);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private ObjectId id;
		private long value;
	    
		static
		{
			JimmutableBuilder b = new JimmutableBuilder(SearchIndexDefinition.TYPE_NAME);
			
			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ID);
			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create();
		}
		
		public SearchSortObjectLongV2(ObjectId id, long value)
		{
			this.id = id;
			this.value = value;
			complete();
		}
		
		public Long getSimpleValue() { return value; }
		
		public ObjectId getSimpleObjectId() { return id; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(id.getSimpleValue());
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeAtom(SEARCH_FIELD_ID, id.getSimpleValue());
			writer.writeLong(SEARCH_FIELD_VALUE, value);		
		}


		@Override
		public void freeze()
		{
		}

		@Override
		public void normalize()
		{	
		}

		@Override
		public void validate()
		{	
		}
		
		@Override
		public int compareTo(SearchSortObjectLongV2 o) 
		{
			int ret = Comparison.startCompare();
			ret = Comparison.continueCompare(ret, getSimpleObjectId(), o.getSimpleObjectId());
			ret = Comparison.continueCompare(ret, getSimpleValue(), o.getSimpleValue());
			
			return ret;
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeStringable(FIELD_ID, id);
			writer.writeLong(FIELD_VALUE, value);		
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(id, value);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SearchSortObjectLongV2 other = (SearchSortObjectLongV2) obj;
			return Objects.equals(id, other.id) && value == other.value;
		}

		
		
	}


	public static class SearchSortObjectFloatV2 extends StandardImmutableObject<SearchSortObjectFloatV2> implements Indexable
	{
		static public final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
		static public final FieldDefinition.Float FIELD_VALUE = new FieldDefinition.Float("value", -1F);

		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectFloat");
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("float"), new IndexVersion("v1"));

		static public final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.ATOM);
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.FLOAT);
		static public final SearchIndexDefinition INDEX_MAPPING;

		private ObjectId id;
		private float value;

		static
		{
			JimmutableBuilder b = new JimmutableBuilder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ID);
			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create();
		}

		public SearchSortObjectFloatV2(ObjectId id, float value)
		{
			this.id = id;
			this.value = value;
			complete();
		}

		public Float getSimpleValue() { return value; }

		public ObjectId getSimpleObjectId() { return id; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition()
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId()
		{
			return new SearchDocumentId(id.getSimpleValue());
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer)
		{
			writer.writeAtom(SEARCH_FIELD_ID, id.getSimpleValue());
			writer.writeFloat(SEARCH_FIELD_VALUE, value);
		}


		@Override
		public void freeze()
		{
		}

		@Override
		public void normalize()
		{
		}

		@Override
		public void validate()
		{
		}

		@Override
		public int compareTo(SearchSortObjectFloatV2 o)
		{
			int ret = Comparison.startCompare();
			ret = Comparison.continueCompare(ret, getSimpleObjectId(), o.getSimpleObjectId());
			ret = Comparison.continueCompare(ret, getSimpleValue(), o.getSimpleValue());

			return ret;
		}

		@Override
		public TypeName getTypeName()
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer)
		{
			writer.writeStringable(FIELD_ID, id);
			writer.writeFloat(FIELD_VALUE, value);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(id, value);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SearchSortObjectFloatV2 other = (SearchSortObjectFloatV2) obj;
			return Objects.equals(id, other.id) && value == other.value;
		}



	}

	public static class SearchSortObjectBooleanV2 extends StandardImmutableObject<SearchSortObjectBooleanV2> implements Indexable
	{
		static public final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
		static public final FieldDefinition.Boolean FIELD_VALUE = new FieldDefinition.Boolean("value", false);

		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectBoolean");
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("boolean"), new IndexVersion("v1"));

		static public final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.ATOM);
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.BOOLEAN);
		static public final SearchIndexDefinition INDEX_MAPPING;

		private ObjectId id;
		private boolean value;

		static
		{
			JimmutableBuilder b = new JimmutableBuilder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ID);
			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create();
		}

		public SearchSortObjectBooleanV2(ObjectId id, boolean value)
		{
			this.id = id;
			this.value = value;
			complete();
		}

		public Boolean getSimpleValue() { return value; }

		public ObjectId getSimpleObjectId() { return id; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition()
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId()
		{
			return new SearchDocumentId(id.getSimpleValue());
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer)
		{
			writer.writeAtom(SEARCH_FIELD_ID, id.getSimpleValue());
			writer.writeBoolean(SEARCH_FIELD_VALUE, value);
		}


		@Override
		public void freeze()
		{
		}

		@Override
		public void normalize()
		{
		}

		@Override
		public void validate()
		{
		}

		@Override
		public int compareTo(SearchSortObjectBooleanV2 o)
		{
			int ret = Comparison.startCompare();
			ret = Comparison.continueCompare(ret, getSimpleObjectId(), o.getSimpleObjectId());
			ret = Comparison.continueCompare(ret, getSimpleValue(), o.getSimpleValue());

			return ret;
		}

		@Override
		public TypeName getTypeName()
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer)
		{
			writer.writeStringable(FIELD_ID, id);
			writer.writeBoolean(FIELD_VALUE, value);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(id, value);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SearchSortObjectBooleanV2 other = (SearchSortObjectBooleanV2) obj;
			return Objects.equals(id, other.id) && value == other.value;
		}

	}
	
	public static class SearchSortObjectDayV2 extends StandardImmutableObject<SearchSortObjectDayV2> implements Indexable
	{		
		static public final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
		static public final FieldDefinition.Stringable<Day> FIELD_VALUE = new FieldDefinition.Stringable<Day>("day", null, Day.CONVERTER);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectDay");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("day"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.ATOM);	
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.DAY);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private ObjectId id;
		private Day value;
		
		static
		{
			JimmutableBuilder b = new JimmutableBuilder(SearchIndexDefinition.TYPE_NAME);
			
			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ID);
			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create();
		}
		
		public SearchSortObjectDayV2(ObjectId id, Day value)
		{
			this.id = id;
			this.value = value;
			complete();
		}
		
		public ObjectId getSimpleObjectId() { return id; }
		public Day getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(id.getSimpleValue());
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeAtom(SEARCH_FIELD_ID, id.getSimpleValue());
			writer.writeDay(SEARCH_FIELD_VALUE, value);		
		}

		@Override
		public int compareTo(SearchSortObjectDayV2 o) 
		{
			int ret = Comparison.startCompare();
			ret = Comparison.continueCompare(ret, getSimpleObjectId(), o.getSimpleObjectId());
			ret = Comparison.continueCompare(ret, getSimpleValue(), o.getSimpleValue());
			
			return ret;
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeStringable(FIELD_ID, id);
			writer.writeStringable(FIELD_VALUE, value);			
		}

		@Override
		public void freeze() 
		{
		}

		@Override
		public void normalize() 
		{
		}

		@Override
		public void validate() 
		{
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(id, value);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			SearchSortObjectDayV2 other = (SearchSortObjectDayV2) obj;
			return Objects.equals(id, other.id) && Objects.equals(value, other.value);
		}

		
		
//		public static void upsertData()
//		{
//			for ( int i = 0; i < 100; i++ )
//			{				
//				DateTime date_time = DateTime.now().minusDays(RANDOM.nextInt(3000));
//				Day day = new Day(date_time);
//				SearchSortObjectDay obj = new SearchSortObjectDay(day);
//				
//				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
//			}
//		}
//		
//		public static void doSearch()
//		{
//			SortBy sort_by = new SortBy(SearchSortObjectDay.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
//			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectDay.INDEX_DEFINITION, 
//					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
//			
//			for ( OneSearchResultWithTyping result : results )
//			{
//				System.out.println(result.readAsDay(SEARCH_FIELD_VALUE.getSimpleFieldName(), null));
//			}
//		}
	}
	
	public static class SearchSortObjectLongArray extends StandardImmutableObject<SearchSortObjectLongArray> implements Indexable
	{		
		static public final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
		static public final FieldDefinition.Long FIELD_VALUE = new FieldDefinition.Long("value", -1L);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectLongArray");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("long-array"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.ATOM);
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.LONG);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private ObjectId id;
		private FieldCollection<Long> value;
		
		static
		{
			JimmutableBuilder b = new JimmutableBuilder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ID);
			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create();
		}
		
		public SearchSortObjectLongArray(ObjectId id, Collection<Long> value)
		{
			this.id = id;
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
			complete();
		}
		
		public ObjectId getSimpleObjectId() { return id; }
		public Collection<Long> getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(id.getSimpleValue());
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeAtom(SEARCH_FIELD_ID, id.getSimpleValue());
			writer.writeLongArray(SEARCH_FIELD_VALUE, value);		
		}

		@Override
		public int compareTo(SearchSortObjectLongArray o) 
		{
			int ret = Comparison.startCompare();
			ret = Comparison.continueCompare(ret, getSimpleObjectId(), o.getSimpleObjectId());
			ret = Comparison.continueCompare(ret, getSimpleValue().size(), o.getSimpleValue().size());
			
			return ret;
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeStringable(FIELD_ID, id);
			writer.writeCollection(FIELD_VALUE, value, WriteAs.NUMBER);		
		}

		@Override
		public void freeze() 
		{
			value.freeze();
		}

		@Override
		public void normalize() 
		{
		}

		@Override
		public void validate() 
		{
		}

		@Override
		public int hashCode() 
		{
			return Objects.hash(getSimpleObjectId(), getSimpleValue());
		}

		@Override
		public boolean equals(Object obj) 
		{
			if (!(obj instanceof SearchSortObjectLongV2))
				return false;

			SearchSortObjectLongV2 other = (SearchSortObjectLongV2) obj; 
			
			if (!Objects.equals(getSimpleValue(), other.getSimpleValue()))
			{
				return false;
			}
			
			return true;
		}
		
//		public static void upsertData()
//		{
//			for ( int i = 0; i < 100; i++ )
//			{		
//				List<Long> collection = new ArrayList<>();
//				
//				while(RANDOM.nextInt(26) % 2 == 0)
//				{
//					collection.add(RANDOM.nextLong());
//				}
//				
//				SearchSortObjectLongArray obj = new SearchSortObjectLongArray(collection);
//				
//				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
//			}
//		}
//		
//		public static void doSearch()
//		{
//			SortBy sort_by = new SortBy(SearchSortObjectLong.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
//			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLongArray.INDEX_DEFINITION, 
//					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
//			
//			for ( OneSearchResultWithTyping result : results )
//			{
//				System.out.print("[");
//				for ( Long value : result.readAsLongArray(SEARCH_FIELD_VALUE.getSimpleFieldName(), null) )
//				{
//					System.out.print(value + ", ");
//				}
//				logger.info("]");
//			}
//		}
	}

    private static void deleteIndexEntries( FieldName key_field_name, IndexDefinition index_definition)
    {
    	String deletion_query = String.format("%s:%s", key_field_name.getSimpleName(), "*");
		List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(index_definition, new StandardSearchRequest(deletion_query, 10000, 0), null);
		for ( OneSearchResultWithTyping result : results )
		{
			SearchDocumentId key = new SearchDocumentId(result.readAsAtom(key_field_name, null));
			if (key != null)
			{
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().deleteDocument(index_definition, key);
			}
		}
    }

}
