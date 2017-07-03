package org.jimmutable.core.fields;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.jimmutable.core.exceptions.ImmutableException;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldCollection;
import org.jimmutable.core.fields.FieldConcurrentHashSet;
import org.jimmutable.core.fields.FieldConcurrentSkipListSet;
import org.jimmutable.core.fields.FieldHashSet;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.fields.FieldTreeSet;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Validator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FieldCollectionTest extends TestCase
{
	static public class TestObject extends StandardImmutableObject<TestObject>
	{
		static public final TypeName TYPE_NAME = new TypeName("jimmutable.test.field_collection.dummy_object"); public TypeName getTypeName() { return TYPE_NAME; }
		
		static private final FieldName FIELD_CLASS = new FieldName("field_class");
		static private final FieldName FIELD_COLLECTION = new FieldName("field_collection");
		
		private Class field_class;
		private FieldCollection<String> collection;
		
		transient private Iterator old_iterator;
		transient private ListIterator old_list_iterator;
		
		public TestObject(Class field_class)
		{
			Validator.notNull(field_class);
			this.field_class = field_class;
			
			collection = createEmtpyCollection();
			
			verifyMutable();
			
			collection.add("foo");  
			
			old_iterator = collection.iterator();
			if ( collection instanceof List ) old_list_iterator = ((List)collection).listIterator();
			
			complete();
			
			verifyImmutable();
			verifyOldIteratorImmutable();
		}
		
		public TestObject(Class field_class, Builder b)
		{
			Validator.notNull(field_class);
			this.field_class = field_class;
			
			collection = createEmtpyCollection();
		}
		
		public TestObject(ObjectParseTree t)
		{
			try 
			{ 
				String class_name = t.getString(FIELD_CLASS, null);
				field_class = Class.forName(class_name);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				assert(false);
			}
			
			collection = t.getCollection(FIELD_COLLECTION, createEmtpyCollection(), ReadAs.STRING, ObjectParseTree.OnError.SKIP);
		}
		
		public void write(ObjectWriter writer) 
		{
			writer.writeString(FIELD_CLASS, field_class.getName());
			writer.writeCollection(FIELD_COLLECTION, collection, WriteAs.STRING);
		}
		
		private FieldCollection<String> createEmtpyCollection()
		{
			try
			{
				return (FieldCollection<String>)field_class.newInstance();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				assert(false);
				return null;
			}
		}
		
		public int compareTo(TestObject o) { return 0; }
		public void normalize() {}
		public void validate() {}
		public void freeze() { collection.freeze(); }
		public int hashCode() { return collection.hashCode(); }
		public FieldCollection<String> getSimpleCollection() { return collection; } 
		
		public boolean equals(Object obj) 
		{
			if (!(obj instanceof TestObject) ) return false;
			
			TestObject other = (TestObject)obj;
			
			if ( !getSimpleCollection().equals(other.getSimpleCollection()) ) return false;
			
			return true;
		}
		
		
		
		public void verifyMutable()
		{
			try
			{
				collection.add("foo");
				collection.add("bar");
				collection.add("baz");
				collection.add("quz");
				collection.add("quuz");
				
				collection.add(null);  // should just skip

				assertEquals(collection.size(),5);

				collection.remove("quz");
				collection.remove("quuz");

				assertEquals(collection.size(),3);

				Set<String> quz_values = new HashSet();
				quz_values.add("quz");
				quz_values.add("quuz");

				collection.addAll(quz_values);

				assertEquals(collection.size(),5);

				collection.removeAll(quz_values);

				assertEquals(collection.size(),3);

				collection.addAll(quz_values);
				collection.retainAll(quz_values);

				assert(collection.containsAll(quz_values));
				assertEquals(collection.size(), quz_values.size());

				collection.remove("something_not_in_the_set");

				collection.add("blaz");

				Iterator<String> itr = collection.iterator();
				itr.next();

				itr.remove(); // Verify that items can be removed using an iterator...
				
				collection.clear();
				
				if ( collection instanceof List )
				{
					List<String> as_list = (List)collection;
					
					as_list.add(0, "foo");
					as_list.add(0, "bar");
					as_list.add(0, "baz");
					
					assertEquals(as_list.get(0),"baz");
					assertEquals(as_list.get(1),"bar");
					assertEquals(as_list.get(2),"foo");
					
					as_list.remove(0);
					
					assertEquals(as_list.size(),2);
					assertEquals(as_list.get(0),"bar");
					assertEquals(as_list.get(1),"foo");
					 
					as_list.set(0, "quz");
					
					assertEquals(as_list.get(0),"quz");
					
					as_list.clear();
					
					ArrayList<String> tmp = new ArrayList();
					
					tmp.add("one");
					tmp.add("two");
					
					as_list.addAll(0, tmp);
					
					assertEquals(as_list.size(),2);
					assertEquals(as_list.get(0),"one");
					assertEquals(as_list.get(1),"two");
				}
				
				collection.clear();

				assertEquals(collection.size(),0);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				assert(false);
			}
		}



		public void verifyImmutable()
		{
			assert(!collection.isEmpty()); // these tests require list contains at least one element...

			try { collection.add("foo"); assert(false); } catch(ImmutableException e) { }
			try { collection.remove("foo"); assert(false); } catch(ImmutableException e) { }

			Set<String> quz_values = new HashSet();
			quz_values.add("quz");
			quz_values.add("quuz");


			try { collection.addAll(quz_values); assert(false); } catch(ImmutableException e) { }
			try { collection.removeAll(quz_values); assert(false); } catch(ImmutableException e) { }
			try { collection.retainAll(quz_values); assert(false); } catch(ImmutableException e) { }

			
			try { collection.remove(1); assert(false); } catch(ImmutableException e) { }
			
			try
			{
				Iterator<String> itr = collection.iterator();
				itr.next();

				itr.remove(); // Verify that items can be removed using an iterator...
				assert(false);
			}
			catch(ImmutableException e) {}

			try { collection.clear(); assert(false); } catch(ImmutableException e) { }
			
			
			if ( collection instanceof List )
			{
				List<String> as_list = (List)collection;
				
				try { as_list.add(0,"foo"); assert(false); } catch(ImmutableException e) { }
				try { as_list.remove(0); assert(false); } catch(ImmutableException e) { }
				try { as_list.set(0, "quz"); assert(false); } catch(ImmutableException e) { }
				
				
				
				ArrayList<String> tmp = new ArrayList();
				
				tmp.add("one");
				tmp.add("two");
				
				try { as_list.addAll(0, tmp); assert(false); } catch(ImmutableException e) { }
			}
		}
		
		public void verifyOldIteratorImmutable()
		{
			try { old_iterator.remove(); assert(false); } catch(ImmutableException e) { }
			
			if ( old_list_iterator != null )
			{
				try { old_list_iterator.remove(); assert(false); } catch(ImmutableException e) { }
			}
		}
		
		static public class Builder
		{
			private TestObject obj;
			
			public Builder(Class c)
			{
				obj = new TestObject(c, this);
			}
			
			public void add(String el)
			{
				obj.collection.add(el);
			}
			
			public TestObject create()
			{
				return obj.deepClone();
			}
		}
	}
	
	
    public FieldCollectionTest( String testName )
    {
        super( testName );
    }

 
    public static Test suite()
    {
        return new TestSuite( FieldCollectionTest.class );
    }
    
    public void testCollections()
    {
    	ObjectParseTree.registerTypeName(TestObject.class);
    	
    	testCollection(FieldArrayList.class, true);
    	testCollection(FieldConcurrentHashSet.class, true);
    	testCollection(FieldConcurrentSkipListSet.class, true);
    	testCollection(FieldHashSet.class, true);
    	testCollection(FieldTreeSet.class, true);
    }
    
    public void testCollection(Class c, boolean print_output)
    {
    	testImmutability(c);
    	testBuilder(c, print_output);
    }
    
    public void testImmutability(Class c)
    {
    	new TestObject(c);
    }
    
    public void testBuilder(Class c, boolean print_output)
    {
    	TestObject.Builder builder = new TestObject.Builder(c);
    	
    	builder.add("foo");
    	builder.add("bar");
    	builder.add("jimmutable");
    	
    	TestObject obj = builder.create();
    	obj.verifyImmutable();
    	
    	if ( print_output )
    	{
    		System.out.println(obj.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
    	}
    	
    	assert(obj.collection.contains("foo"));
    	assert(obj.collection.contains("bar"));
    	assert(obj.collection.contains("jimmutable"));
    	assertEquals(obj.collection.size(),3);
    	assertEquals(obj.field_class.getName(),c.getName());
    }
    
    public void testBulkAddNulls()
    {
    	List<String> src_with_nulls = new ArrayList();
    	
    	src_with_nulls.add(null);
    	src_with_nulls.add("foo");
    	src_with_nulls.add("bar");
    	src_with_nulls.add(null);
    	
    	FieldList<String> test_list = new FieldArrayList(src_with_nulls);
    	
    	assertEquals(test_list.size(),2);
    	
    	FieldList<String> test_two = new FieldArrayList();
    	
    	test_two.add("a");
    	test_two.add("b");
    	test_two.add(null);
    	test_two.addAll(src_with_nulls);
    	
    	assertEquals(test_two.size(),4);
    	
    }
}


