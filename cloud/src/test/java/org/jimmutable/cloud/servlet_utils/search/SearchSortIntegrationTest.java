package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldCollection;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.time.Instant;
import org.jimmutable.core.objects.common.time.TimeOfDay;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.joda.time.DateTime;

public class SearchSortIntegrationTest
{	
	private static final Logger logger = LogManager.getLogger(SearchSortIntegrationTest.class);
	public static final String APPLICATION_ID = "SearchSortIntegrationTest";
	
	public static final Random RANDOM = new Random();
	
	static
	{
		CloudExecutionEnvironment.startup(new ApplicationId(APPLICATION_ID), EnvironmentType.DEV);
		
		ObjectParseTree.registerTypeName(SearchSortObjectText.class);
		ObjectParseTree.registerTypeName(SearchSortObjectLong.class);
		ObjectParseTree.registerTypeName(SearchSortObjectFloat.class);		
		ObjectParseTree.registerTypeName(SearchSortObjectBoolean.class);	
		ObjectParseTree.registerTypeName(SearchSortObjectDay.class);	
		ObjectParseTree.registerTypeName(SearchSortObjectInstant.class);	
		ObjectParseTree.registerTypeName(SearchSortObjectTimeOfDay.class);	
		
		ObjectParseTree.registerTypeName(SearchSortObjectTextArray.class);
		ObjectParseTree.registerTypeName(SearchSortObjectLongArray.class);
		ObjectParseTree.registerTypeName(SearchSortObjectFloatArray.class);
	}
	
	public static void main(String args[])
	{		
		if ( CloudExecutionEnvironment.getSimpleCurrent().getSimpleEnvironmentType().equals(EnvironmentType.DEV) )
		{				
			ArrayList<SearchIndexDefinition> classes = new ArrayList<SearchIndexDefinition>();
			Collections.addAll(classes, SearchSortObjectLong.INDEX_MAPPING, SearchSortObjectText.INDEX_MAPPING, SearchSortObjectFloat.INDEX_MAPPING, SearchSortObjectBoolean.INDEX_MAPPING, 
					SearchSortObjectDay.INDEX_MAPPING, SearchSortObjectInstant.INDEX_MAPPING, SearchSortObjectTimeOfDay.INDEX_MAPPING);
			
			for ( SearchIndexDefinition definition : classes )
			{
				if ( !CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().indexProperlyConfigured(definition) )
				{
					logger.warn(String.format("Upserting index:%s", definition.getSimpleIndex().getSimpleValue()));
					CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertIndex(definition);
					try
					{
						Thread.sleep(2000);
					}
					catch ( InterruptedException e )
					{
					}
				}
			}
			
			try
			{
				upsertData();
				upsertDataArray();
				doSearch();
				doSearchArray();
			}
			catch ( Exception e )
			{
				e.printStackTrace();
				System.err.println("FAILURE: ERROR RUNNING INTEGRATION TEST");
				System.exit(0);
			}
		}
		
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		System.out.println("Test Complete!");
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		System.exit(0);
	}
	
	public static void upsertData()
	{
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		System.out.println("Upserting Data (singles)");
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		
		SearchSortObjectText.upsertData();
		SearchSortObjectLong.upsertData();
		SearchSortObjectFloat.upsertData();
		SearchSortObjectBoolean.upsertData();
		SearchSortObjectDay.upsertData();
		SearchSortObjectInstant.upsertData();
		SearchSortObjectTimeOfDay.upsertData();
	}
	
	public static void upsertDataArray()
	{
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		System.out.println("Upserting Data (array)");
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		
		SearchSortObjectTextArray.upsertData();
		SearchSortObjectLongArray.upsertData();
		SearchSortObjectFloatArray.upsertData();
		SearchSortObjectBooleanArray.upsertData();
		SearchSortObjectDayArray.upsertData();
		SearchSortObjectInstantArray.upsertData();
		SearchSortObjectTimeOfDayArray.upsertData();
	}
	
	public static void doSearch()
	{
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		System.out.println("Do Search (singles)");
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		
		SearchSortObjectText.doSearch();
		SearchSortObjectLong.doSearch();		
		SearchSortObjectFloat.doSearch();
		SearchSortObjectBoolean.doSearch();
		SearchSortObjectDay.doSearch();
		SearchSortObjectInstant.doSearch();
		SearchSortObjectTimeOfDay.doSearch();
	}
	
	public static void doSearchArray()
	{
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		System.out.println("Do Search (array)");
		System.out.println();
		System.out.println("*****************************************");
		System.out.println();
		
		SearchSortObjectTextArray.doSearch();
		SearchSortObjectLongArray.doSearch();		
		SearchSortObjectFloatArray.doSearch();
		SearchSortObjectBooleanArray.doSearch();
		SearchSortObjectDayArray.doSearch();
		SearchSortObjectInstantArray.doSearch();
		SearchSortObjectTimeOfDayArray.doSearch();
	}
	
	static public class SearchSortObjectLong extends StandardImmutableObject<SearchSortObjectLong> implements Indexable
	{		
		static public final FieldDefinition.Long FIELD_VALUE = new FieldDefinition.Long("value", -1L);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectLong");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("long"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.LONG);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private long value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectLong(long value)
		{
			this.value = value;
		}
		
		public Long getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(value+"");
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeLong(SEARCH_FIELD_VALUE, value);		
		}

		@Override
		public int compareTo(SearchSortObjectLong o) 
		{
			return Long.compare(value, o.value);
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeLong(FIELD_VALUE, value);		
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
				SearchSortObjectLong obj = new SearchSortObjectLong(RANDOM.nextLong());
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectLong.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectLong.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.println(result.readAsLong(SEARCH_FIELD_VALUE.getSimpleFieldName(), -1L));
			}
		}
	}
	
	static public class SearchSortObjectLongArray extends StandardImmutableObject<SearchSortObjectLongArray> implements Indexable
	{		
		static public final FieldDefinition.Long FIELD_VALUE = new FieldDefinition.Long("value", -1L);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectLongArray");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("long-array"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.LONG);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private FieldCollection<Long> value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectLongArray(Collection<Long> value)
		{
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
		}
		
		public Collection<Long> getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(String.valueOf(this.hashCode()));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeLongArray(SEARCH_FIELD_VALUE, value);		
		}

		@Override
		public int compareTo(SearchSortObjectLongArray o) 
		{
			return Integer.compare(getSimpleValue().size(), o.getSimpleValue().size());
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
				System.out.println("]");
			}
		}
	}
	
	static public class SearchSortObjectText extends StandardImmutableObject<SearchSortObjectText> implements Indexable
	{		
		static public final FieldDefinition.String FIELD_VALUE = new FieldDefinition.String("value", null);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectText");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("text"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.TEXT);		
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private String value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectText(String value)
		{
			this.value = value;
		}
		
		public String getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(String.valueOf(hashCode()));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeText(SEARCH_FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectText o) 
		{
			return getSimpleValue().compareTo(o.getSimpleValue());
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
	
	static public class SearchSortObjectTextArray extends StandardImmutableObject<SearchSortObjectTextArray> implements Indexable
	{		
		static public final FieldDefinition.String FIELD_VALUE = new FieldDefinition.String("value", null);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectTextArray");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("text-array"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.TEXT);		
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private FieldCollection<String> value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectTextArray(Collection<String> value)
		{
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
		}
		
		public Collection<String> getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(String.valueOf(this.hashCode()));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeTextArray(SEARCH_FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectTextArray o) 
		{
			return Integer.compare(getSimpleValue().size(), o.getSimpleValue().size());
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeCollection(FIELD_VALUE, value, WriteAs.STRING);			
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
				List<String> collection = new ArrayList<>();
				
				while(RANDOM.nextInt(26) % 2 == 0)
				{
					String str = "";
					for ( int j =  0; j < 10; j++ )
					{
						str += alphabet.get(RANDOM.nextInt(26));
					}
					
					collection.add(str);
				}
				
				SearchSortObjectTextArray obj = new SearchSortObjectTextArray(collection);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
			
			// Edge cases
			// Acid String
			List<String> collection = new ArrayList<>();
			collection.add(createAcidString());
			SearchSortObjectTextArray obj = new SearchSortObjectTextArray(collection);			
			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectText.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectTextArray.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "a*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.print("[");
				for ( String value : result.readAsTextArray(SEARCH_FIELD_VALUE.getSimpleFieldName(), null) )
				{
					System.out.print(value + ", ");
				}
				System.out.println("]");
			}
		}
	}
	
	static public class SearchSortObjectFloat extends StandardImmutableObject<SearchSortObjectFloat> implements Indexable
	{		
		static public final FieldDefinition.Float FIELD_VALUE = new FieldDefinition.Float("value", -1F);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectFloat");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("float"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.FLOAT);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private float value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectFloat(float value)
		{
			this.value = value;
		}
		
		public Float getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(Float.toString(value).replace('.', '-'));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeFloat(FIELD_VALUE, value);	
		}

		@Override
		public int compareTo(SearchSortObjectFloat o) 
		{
			return Float.compare(value, o.value);
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeFloat(FIELD_VALUE, value);			
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
			if (!(obj instanceof SearchSortObjectFloat))
				return false;

			SearchSortObjectFloat other = (SearchSortObjectFloat) obj; 
			
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
				SearchSortObjectFloat obj = new SearchSortObjectFloat(RANDOM.nextFloat());
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
			
			// Edge cases
			// NaN - ElasticSearch only supports finite values (NumberFieldMapper.java:351)
//			SearchSortObjectFloat obj = new SearchSortObjectFloat(Float.NaN);
//			
//			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectFloat.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectFloat.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.println(result.readAsFloat(SEARCH_FIELD_VALUE.getSimpleFieldName(), -1F));
			}
		}
	}
	
	static public class SearchSortObjectFloatArray extends StandardImmutableObject<SearchSortObjectFloatArray> implements Indexable
	{		
		static public final FieldDefinition.Float FIELD_VALUE = new FieldDefinition.Float("value", -1F);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectFloatArray");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("float-array"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.FLOAT);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private FieldCollection<Float> value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectFloatArray(Collection<Float> value)
		{
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
		}
		
		public Collection<Float> getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(String.valueOf(this.hashCode()));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeFloatArray(SEARCH_FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectFloatArray o) 
		{
			return Integer.compare(getSimpleValue().size(), o.getSimpleValue().size());
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
			if (!(obj instanceof SearchSortObjectFloatArray))
				return false;

			SearchSortObjectFloatArray other = (SearchSortObjectFloatArray) obj; 
			
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
				List<Float> collection = new ArrayList<>();
				
				while(RANDOM.nextInt(26) % 2 == 0)
				{
					collection.add(RANDOM.nextFloat());
				}
				
				SearchSortObjectFloatArray obj = new SearchSortObjectFloatArray(collection);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
			
			// Edge cases
			// NaN - ElasticSearch only supports finite values (NumberFieldMapper.java:351)
//			List<Float> collection = new ArrayList<>();
//			collection.add(Float.NaN);
//			SearchSortObjectFloatArray obj = new SearchSortObjectFloatArray(collection);			
//			CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectFloatArray.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectFloatArray.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.print("[");
				for ( Float value : result.readAsFloatArray(SEARCH_FIELD_VALUE.getSimpleFieldName(), null) )
				{
					System.out.print(value + ", ");
				}
				System.out.println("]");
			}
		}
	}
	
	static public class SearchSortObjectBoolean extends StandardImmutableObject<SearchSortObjectBoolean> implements Indexable
	{		
		static public final FieldDefinition.Boolean FIELD_VALUE = new FieldDefinition.Boolean("value", false);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectBoolean");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("boolean"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.BOOLEAN);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private boolean value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectBoolean(boolean value)
		{
			this.value = value;
		}
		
		public boolean getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(value + Integer.toString(RANDOM.nextInt()) + "");
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeBoolean(FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectBoolean o) 
		{
			return Boolean.compare(value, o.value);
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeBoolean(FIELD_VALUE, value);			
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
			if (!(obj instanceof SearchSortObjectBoolean))
				return false;

			SearchSortObjectBoolean other = (SearchSortObjectBoolean) obj; 
			
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
				int seed = RANDOM.nextInt();
				SearchSortObjectBoolean obj = new SearchSortObjectBoolean(seed % 2 == 0);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectBoolean.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectBoolean.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.println(result.readAsBoolean(SEARCH_FIELD_VALUE.getSimpleFieldName(), false));
			}
		}
	}
	
	static public class SearchSortObjectBooleanArray extends StandardImmutableObject<SearchSortObjectBooleanArray> implements Indexable
	{		
		static public final FieldDefinition.Boolean FIELD_VALUE = new FieldDefinition.Boolean("value", false);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectBooleanArray");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("boolean-array"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.BOOLEAN);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private FieldCollection<Boolean> value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectBooleanArray(Collection<Boolean> value)
		{
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
		}
		
		public Collection<Boolean> getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(String.valueOf(this.hashCode()));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeBooleanArray(SEARCH_FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectBooleanArray o) 
		{
			return Integer.compare(getSimpleValue().size(), o.getSimpleValue().size());
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeCollection(FIELD_VALUE, value, WriteAs.BOOLEAN);			
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
			if (!(obj instanceof SearchSortObjectBooleanArray))
				return false;

			SearchSortObjectBooleanArray other = (SearchSortObjectBooleanArray) obj; 
			
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
				List<Boolean> collection = new ArrayList<>();
				
				while(RANDOM.nextInt(26) % 2 == 0)
				{
					collection.add(RANDOM.nextBoolean());
				}
				
				SearchSortObjectBooleanArray obj = new SearchSortObjectBooleanArray(collection);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectBooleanArray.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectBooleanArray.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.print("[");
				for ( Boolean value : result.readAsBooleanArray(SEARCH_FIELD_VALUE.getSimpleFieldName(), null) )
				{
					System.out.print(value + ", ");
				}
				System.out.println("]");
			}
		}
	}
	
	static public class SearchSortObjectDay extends StandardImmutableObject<SearchSortObjectDay> implements Indexable
	{		
		static public final FieldDefinition.Stringable<Day> FIELD_VALUE = new FieldDefinition.Stringable<Day>("day", null, Day.CONVERTER);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectDay");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("day"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.DAY);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private Day value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectDay(Day value)
		{
			this.value = value;
		}
		
		public Day getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(value.getSimpleValue().replace('/', '-'));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeDay(FIELD_VALUE, value);		
		}

		@Override
		public int compareTo(SearchSortObjectDay o) 
		{
			return value.compareTo(o.getSimpleValue());
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
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
			return Objects.hash(getSimpleValue());
		}

		@Override
		public boolean equals(Object obj) 
		{
			if (!(obj instanceof SearchSortObjectDay))
				return false;

			SearchSortObjectDay other = (SearchSortObjectDay) obj; 
			
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
				DateTime date_time = DateTime.now().minusDays(RANDOM.nextInt(3000));
				Day day = new Day(date_time);
				SearchSortObjectDay obj = new SearchSortObjectDay(day);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectDay.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectDay.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.println(result.readAsDay(SEARCH_FIELD_VALUE.getSimpleFieldName(), null));
			}
		}
	}
	
	static public class SearchSortObjectDayArray extends StandardImmutableObject<SearchSortObjectBooleanArray> implements Indexable
	{		
		static public final FieldDefinition.Stringable<Day> FIELD_VALUE = new FieldDefinition.Stringable<Day>("day", null, Day.CONVERTER);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectDayArray");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("day-array"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.DAY);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private FieldCollection<Day> value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectDayArray(Collection<Day> value)
		{
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
		}
		
		public Collection<Day> getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(String.valueOf(this.hashCode()));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeDayArray(SEARCH_FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectBooleanArray o) 
		{
			return Integer.compare(getSimpleValue().size(), o.getSimpleValue().size());
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeCollection(FIELD_VALUE, value, WriteAs.OBJECT);			
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
			if (!(obj instanceof SearchSortObjectDayArray))
				return false;

			SearchSortObjectDayArray other = (SearchSortObjectDayArray) obj; 
			
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
				List<Day> collection = new ArrayList<>();
				
				while(RANDOM.nextInt(26) % 2 == 0)
				{
					DateTime date_time = DateTime.now().minusDays(RANDOM.nextInt(3000));
					Day day = new Day(date_time);
					collection.add(day);
				}
				
				SearchSortObjectDayArray obj = new SearchSortObjectDayArray(collection);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectDayArray.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectDayArray.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.print("[");
				for ( Day value : result.readAsDayArray(SEARCH_FIELD_VALUE.getSimpleFieldName(), null) )
				{
					System.out.print(value + ", ");
				}
				System.out.println("]");
			}
		}
	}
	
	static public class SearchSortObjectInstant extends StandardImmutableObject<SearchSortObjectInstant> implements Indexable
	{		
		static public final FieldDefinition.StandardObject FIELD_VALUE = new FieldDefinition.StandardObject("instant", null);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectInstant");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("instant"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.INSTANT);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private Instant value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectInstant(Instant value)
		{
			this.value = value;
		}
		
		public Instant getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(value.getSimpleMillisecondsFromEpoch()+"");
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeInstant(SEARCH_FIELD_VALUE, value);			
		}

		@Override
		public int compareTo(SearchSortObjectInstant o) 
		{
			return value.compareTo(o.getSimpleValue());
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeObject(FIELD_VALUE, value);			
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
			if (!(obj instanceof SearchSortObjectInstant))
				return false;

			SearchSortObjectInstant other = (SearchSortObjectInstant) obj; 
			
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
				DateTime date_time = DateTime.now().minusDays(RANDOM.nextInt(3000));
				Instant instant = new Instant(date_time.getMillis());
				SearchSortObjectInstant obj = new SearchSortObjectInstant(instant);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectInstant.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectInstant.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.println(result.readAsInstant(SEARCH_FIELD_VALUE.getSimpleFieldName(), null));
			}
		}
	}
	
	static public class SearchSortObjectInstantArray extends StandardImmutableObject<SearchSortObjectInstantArray> implements Indexable
	{		
		static public final FieldDefinition.StandardObject FIELD_VALUE = new FieldDefinition.StandardObject("instant", null);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectInstant");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("instant-array"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.INSTANT);	
		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private FieldCollection<Instant> value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectInstantArray(Collection<Instant> value)
		{
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
		}
		
		public Collection<Instant> getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(String.valueOf(this.hashCode()));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeInstantArray(SEARCH_FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectInstantArray o) 
		{
			return Integer.compare(getSimpleValue().size(), o.getSimpleValue().size());
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeCollection(FIELD_VALUE, value, WriteAs.OBJECT);			
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
			if (!(obj instanceof SearchSortObjectInstantArray))
				return false;

			SearchSortObjectInstantArray other = (SearchSortObjectInstantArray) obj; 
			
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
				List<Instant> collection = new ArrayList<>();
				
				while(RANDOM.nextInt(26) % 2 == 0)
				{
					DateTime date_time = DateTime.now().minusDays(RANDOM.nextInt(3000));
					Instant instant = new Instant(date_time.getMillis());
					collection.add(instant);
				}
				
				SearchSortObjectInstantArray obj = new SearchSortObjectInstantArray(collection);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectInstantArray.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectInstantArray.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.print("[");
				for ( Instant value : result.readAsInstantArray(SEARCH_FIELD_VALUE.getSimpleFieldName(), null) )
				{
					System.out.print(value + ", ");
				}
				System.out.println("]");
			}
		}
	}
	
	static public class SearchSortObjectTimeOfDay extends StandardImmutableObject<SearchSortObjectTimeOfDay> implements Indexable
	{		
		static public final FieldDefinition.StandardObject FIELD_VALUE = new FieldDefinition.StandardObject("timeofday", null);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectTimeOfDay");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("timeofday"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.TIMEOFDAY);	

		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private TimeOfDay value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectTimeOfDay(TimeOfDay value)
		{
			this.value = value;
		}
		
		public TimeOfDay getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(value.getSimpleMillisecondsFromMidnight()+"");
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeTimeOfDay(SEARCH_FIELD_VALUE, value);			
		}

		@Override
		public int compareTo(SearchSortObjectTimeOfDay o) 
		{
			return value.compareTo(o.getSimpleValue());
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeObject(FIELD_VALUE, value);			
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
			if (!(obj instanceof SearchSortObjectTimeOfDay))
				return false;

			SearchSortObjectTimeOfDay other = (SearchSortObjectTimeOfDay) obj; 
			
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
				TimeOfDay time_of_day = new TimeOfDay(RANDOM.nextInt(86399999));
				SearchSortObjectTimeOfDay obj = new SearchSortObjectTimeOfDay(time_of_day);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectTimeOfDay.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectTimeOfDay.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.println(result.readAsTimeOfDay(SEARCH_FIELD_VALUE.getSimpleFieldName(), null));
			}
		}
	}
	
	static public class SearchSortObjectTimeOfDayArray extends StandardImmutableObject<SearchSortObjectTimeOfDayArray> implements Indexable
	{		
		static public final FieldDefinition.StandardObject FIELD_VALUE = new FieldDefinition.StandardObject("timeofday", null);
		
		static public final TypeName TYPE_NAME = new TypeName("SearchSortObjectTimeOfDayArray");		
		static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("timeofday-array"), new IndexVersion("v1"));
		
		static public final SearchIndexFieldDefinition SEARCH_FIELD_VALUE = new SearchIndexFieldDefinition(FIELD_VALUE.getSimpleFieldName(), SearchIndexFieldType.TIMEOFDAY);	

		static public final SearchIndexDefinition INDEX_MAPPING;
		
		private FieldCollection<TimeOfDay> value;
		
		static
		{
			Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

			b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_VALUE);
			b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

			INDEX_MAPPING = (SearchIndexDefinition) b.create(null);
		}
		
		public SearchSortObjectTimeOfDayArray(Collection<TimeOfDay> value)
		{
			this.value = new FieldArrayList<>();
			this.value.addAll(value);
		}
		
		public Collection<TimeOfDay> getSimpleValue() { return value; }

		@Override
		public IndexDefinition getSimpleSearchIndexDefinition() 
		{
			return INDEX_DEFINITION;
		}

		@Override
		public SearchDocumentId getSimpleSearchDocumentId() 
		{
			return new SearchDocumentId(String.valueOf(this.hashCode()));
		}

		@Override
		public void writeSearchDocument(SearchDocumentWriter writer) 
		{
			writer.writeTimeOfDayArray(SEARCH_FIELD_VALUE, value);
		}

		@Override
		public int compareTo(SearchSortObjectTimeOfDayArray o) 
		{
			return Integer.compare(getSimpleValue().size(), o.getSimpleValue().size());
		}

		@Override
		public TypeName getTypeName() 
		{
			return TYPE_NAME;
		}

		@Override
		public void write(ObjectWriter writer) 
		{
			writer.writeCollection(FIELD_VALUE, value, WriteAs.OBJECT);			
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
			if (!(obj instanceof SearchSortObjectTimeOfDayArray))
				return false;

			SearchSortObjectTimeOfDayArray other = (SearchSortObjectTimeOfDayArray) obj; 
			
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
				List<TimeOfDay> collection = new ArrayList<>();
				
				while(RANDOM.nextInt(26) % 2 == 0)
				{
					TimeOfDay time_of_day = new TimeOfDay(RANDOM.nextInt(86399999));
					collection.add(time_of_day);
				}
				
				SearchSortObjectTimeOfDayArray obj = new SearchSortObjectTimeOfDayArray(collection);
				
				CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(obj);
			}
		}
		
		public static void doSearch()
		{
			SortBy sort_by = new SortBy(SearchSortObjectTimeOfDayArray.SEARCH_FIELD_VALUE, SortDirection.ASCENDING);
			List<OneSearchResultWithTyping> results = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().search(SearchSortObjectTimeOfDayArray.INDEX_DEFINITION, 
					new StandardSearchRequest(String.format("%s:%s", SEARCH_FIELD_VALUE.getSimpleFieldName().getSimpleName(), "*"), 10000, 0, new Sort(sort_by)), null);
			
			for ( OneSearchResultWithTyping result : results )
			{
				System.out.print("[");
				for ( TimeOfDay value : result.readAsTimeOfDayArray(SEARCH_FIELD_VALUE.getSimpleFieldName(), null) )
				{
					System.out.print(value + ", ");
				}
				System.out.println("]");
			}
		}
	}
	
	private static String createAcidString()
	{
		StringBuilder ret = new StringBuilder();
		
		for ( int i = 0; i < 10_000; i++ )
		{
			ret.append((char)i);
		}
		
		return ret.toString();
	}
}