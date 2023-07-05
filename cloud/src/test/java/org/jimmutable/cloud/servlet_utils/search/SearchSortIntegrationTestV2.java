package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectBoolean;
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectDay;
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectFloat;
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectInstant;
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectLong;
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectLongArray;
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectText;
import org.jimmutable.cloud.servlet_utils.search.SearchSortIntegrationTest.SearchSortObjectTimeOfDay;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldCollection;
import org.jimmutable.core.objects.JimmutableBuilder;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
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
//        ObjectParseTree.registerTypeName(SearchSortObjectText.class);
        ObjectParseTree.registerTypeName(SearchSortObjectLong.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectFloat.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectBoolean.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectDay.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectInstant.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectTimeOfDay.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectTextArray.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectLongArray.class);
//        ObjectParseTree.registerTypeName(SearchSortObjectFloatArray.class);
        
        if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleEnvironmentType().equals(EnvironmentType.DEV) )
		{				
			ArrayList<SearchIndexDefinition> classes = new ArrayList<SearchIndexDefinition>();
			Collections.addAll(classes, SearchSortObjectLong.INDEX_MAPPING); /* SearchSortObjectLongArray.INDEX_MAPPING), */ // SearchSortObjectText.INDEX_MAPPING; //, SearchSortObjectFloat.INDEX_MAPPING, SearchSortObjectBoolean.INDEX_MAPPING, 
//					SearchSortObjectDay.INDEX_MAPPING, SearchSortObjectInstant.INDEX_MAPPING, SearchSortObjectTimeOfDay.INDEX_MAPPING);
			
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
				
				if (!testSearchSortObjectLong())
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
    	if (CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexExists(SearchSortObjectLong.INDEX_MAPPING))
    	{
    		deleteIndexEntries(SearchSortObjectLong.SEARCH_FIELD_ID.getSimpleFieldName(), SearchSortObjectLong.INDEX_DEFINITION);
    	}
    }
    

    
    private static boolean testSearchSortObjectLong() throws Exception
    {
    	String test_object = "SearchSortObjectLong";
    	boolean is_successful = true;
    	
    	Map<ObjectId, SearchSortObjectLong> test_objects = new HashMap<>();
    	
		for ( long i = 9; i >= 0; i-- )
		{					
			SearchSortObjectLong obj = new SearchSortObjectLong(ObjectId.createRandomId(), i);
			test_objects.put(obj.getSimpleObjectId(), obj);
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}
    	
		// TODO:PM - convert to id and OR's to prevent previous incomplete runs from contaminating this result.
		String query = String.format("%s:%s", SearchSortObjectLong.SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*");
		
		SortBy sort_by = new SortBy(SearchSortObjectLong.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
		List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLong.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		if (test_objects.size() != results.size())
		{
			outputError(test_object, String.format("Incorrect number of entries retrieved from search. Expected: %s, Actual: %s", test_objects.size(), results.size()));
			is_successful = false;
		}
		
		long prev_long_value = -1;
		for ( OneSearchResultWithTyping result : results )
		{
			ObjectId id = new ObjectId(result.readAsAtom(SearchSortObjectLong.SEARCH_FIELD_ID.getSimpleFieldName(), null));
			long long_value = result.readAsLong(SearchSortObjectLong.SEARCH_FIELD_VALUE.getSimpleFieldName(), -1L) + 1;
			
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

			prev_long_value = long_value;
		}
		
		sort_by = new SortBy(SearchSortObjectLong.SEARCH_FIELD_VALUE, SortDirection.DESCENDING);
		results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLong.INDEX_DEFINITION, new StandardSearchRequest(query, 10000, 0, new Sort(sort_by)), null);
		
		prev_long_value = 10;
		for ( OneSearchResultWithTyping result : results )
		{
			long long_value = result.readAsLong(SearchSortObjectLong.SEARCH_FIELD_VALUE.getSimpleFieldName(), -1L);

			if (!(long_value < prev_long_value))
			{
				outputError(test_object, String.format("Incorrect descending sort sequence. Prev: %s, Curr: %s", prev_long_value, long_value));
				is_successful = false;
			}

			prev_long_value = long_value;
		}
		
		return is_successful;
    }

    private static void outputError(String test_class, String error_msg)
    {
    	logger.error(String.format("FAILURE - Test Class:%s   %s", test_class, error_msg));
    }
    
    void testSearchSortObjectLongArray() 
    {
    
    }
    
    static public class SearchSortObjectText extends StandardImmutableObject<SearchSortObjectText> implements Indexable
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
		
		public SearchSortObjectText(ObjectId id, String value)
		{
			this.id = id;
			this.value = value;
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
		public int compareTo(SearchSortObjectText o) 
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
			return Objects.hash(getSimpleValue());
		}

		@Override
		public boolean equals(Object obj) 
		{
			if (!(obj instanceof SearchSortObjectText))
				return false;

			SearchSortObjectText other = (SearchSortObjectText) obj; 
			
			if (!Objects.equals(getSimpleValue(), other.getSimpleValue()))
			{
				return false;
			}
			
			return true;
		}
		
		public static void upsertData()
		{
			List<String> alphabet = new ArrayList<>();
			for ( char ch = 'a' ; ch <= 'z' ; ch++ )
			{
				alphabet.add(String.valueOf(ch));
			}
				
			for ( int i = 0; i < 100; i++ )
			{
				String value = "";
				for ( int j =  0; j < 10; j++ )
				{
					value += alphabet.get(RANDOM.nextInt(26));
				}
				
				SearchSortObjectText obj = new SearchSortObjectText(value);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
			
			// Edge cases
			// Acid String
			SearchSortObjectText obj = new SearchSortObjectText(createAcidString());			
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectText.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectText.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "a*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.println(result.readAsText(SEARCH_FIELD_VALUE.getSimpleFieldName(), null));
			}
		}
	}
    
	public static class SearchSortObjectLong extends StandardImmutableObject<SearchSortObjectLong> implements Indexable
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
		
		public SearchSortObjectLong(ObjectId id, long value)
		{
			this.id = id;
			this.value = value;
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
		public int compareTo(SearchSortObjectLong o) 
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
			return Objects.hash(getSimpleValue());
		}

		@Override
		public boolean equals(Object obj) 
		{
			if (!(obj instanceof SearchSortObjectLong))
				return false;

			SearchSortObjectLong other = (SearchSortObjectLong) obj; 
			
			if (!Objects.equals(getSimpleValue(), other.getSimpleValue()))
			{
				return false;
			}
			
			return true;
		}
	}

	private static class SearchSortObjectLongArray extends StandardImmutableObject<SearchSortObjectLongArray> implements Indexable
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

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create();
		}
		
		public SearchSortObjectLongArray(ObjectId id, Collection<Long> value)
		{
			this.id = id;
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
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
			writer.writeCollection(FIELD_VALUE, value, WriteAs.NUMBER);		
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
			return Objects.hash(getSimpleValue());
		}

		@Override
		public boolean equals(Object obj) 
		{
			if (!(obj instanceof SearchSortObjectLong))
				return false;

			SearchSortObjectLong other = (SearchSortObjectLong) obj; 
			
			if (!Objects.equals(getSimpleValue(), other.getSimpleValue()))
			{
				return false;
			}
			
			return true;
		}
		
		public static void upsertData()
		{
			for ( int i = 0; i < 100; i++ )
			{		
				List<Long> collection = new ArrayList<>();
				
				while(RANDOM.nextInt(26) % 2 == 0)
				{
					collection.add(RANDOM.nextLong());
				}
				
				SearchSortObjectLongArray obj = new SearchSortObjectLongArray(collection);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectLong.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLongArray.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.print("[");
				for ( Long value : result.readAsLongArray(SEARCH_FIELD_VALUE.getSimpleFieldName(), null) )
				{
					System.out.print(value + ", ");
				}
				logger.info("]");
			}
		}
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
