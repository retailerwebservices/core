package org.jimmutable.simple_object_store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jimmutable.aws.environment.ApplicationEnvironment;
import org.jimmutable.aws.environment.CloudName;
import org.jimmutable.aws.s3.S3Path;
import org.jimmutable.aws.simple_object_store.SimpleObjectStore;
import org.jimmutable.aws.simple_object_store.SimpleStoreName;
import org.jimmutable.aws.simple_object_store.scan.OperationScan;
import org.jimmutable.aws.simple_object_store.scan.ScanListener;
import org.jimmutable.aws.utils.PropertiesReader;
import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectID;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import com.amazonaws.services.s3.model.S3ObjectSummary;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SimpleObjectStoreTest extends TestCase
{
	private UnitTestEnvironment env;
	
	
	static private class UnitTestEnvironment extends ApplicationEnvironment
	{ 
		private SimpleObjectStore store;
		private SimpleObjectStore read_only_store;
	
		public UnitTestEnvironment()
		{
			super();
			
			store = new SimpleObjectStore(this,new SimpleStoreName("unit-test-store"), false);
			read_only_store = new SimpleObjectStore(this,new SimpleStoreName("unit-test-store"), true);
		}
		
		
		public CloudName loadLoadNameFromPropertiesFile(PropertiesReader r, CloudName default_value) 
		{
			return CloudName.UNIT_TEST_CLOUD;
		}
		
		public SimpleObjectStore getSimpleStore() { return store; } 
		public SimpleObjectStore getSimpleReadOnlyStore() { return read_only_store; }
	}
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SimpleObjectStoreTest( String testName )
    {
        super( testName );
        
        JimmutableTypeNameRegister.registerAllTypes();
        ObjectParseTree.registerTypeName(BookDBObject.class);
        
        env = new UnitTestEnvironment();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( SimpleObjectStoreTest.class );
    }
    
    public List<BookDBObject> createPaperbackBooks()
    {
    	List<BookDBObject> ret = new ArrayList();
    	
    	ret.add(new BookDBObject(new ObjectID(1), "A Heartbreaking Work of Staggering Genius", 437, "0375725784", BindingType.PAPER_BACK, "Dave Eggers"));
    	ret.add(new BookDBObject(new ObjectID(2), "A Brief History of Time", 212, "0553380168", BindingType.PAPER_BACK, "Stephen Hawking"));
    	ret.add(new BookDBObject(new ObjectID(3), "Kings of Broken Things", 336, "1503941469", BindingType.PAPER_BACK, "Theodore Wheeler"));
    	ret.add(new BookDBObject(new ObjectID(4), "The Elegant Universe: Superstrings, Hidden Dimensions, and the Quest for the Ultimate Theory", 464, "0393338101", BindingType.PAPER_BACK, "Brian Greene"));
    	ret.add(new BookDBObject(new ObjectID(5), "And Then She Was GONE", 262, "23471901", BindingType.PAPER_BACK, "Christopher Greyson"));
    	
    	return ret;
    }
    
    public List<BookDBObject> createHardCoverBooks()
    {
    	List<BookDBObject> ret = new ArrayList();
    	
    	ret.add(new BookDBObject(new ObjectID(1), "A Heartbreaking Work of Staggering Genius", 437, "0375725784", BindingType.HARD_COVER, "Dave Eggers"));
    	ret.add(new BookDBObject(new ObjectID(2), "A Brief History of Time", 212, "0553380168", BindingType.HARD_COVER, "Stephen Hawking"));
    	ret.add(new BookDBObject(new ObjectID(3), "Kings of Broken Things", 336, "1503941469", BindingType.HARD_COVER, "Theodore Wheeler"));
    	ret.add(new BookDBObject(new ObjectID(4), "The Elegant Universe: Superstrings, Hidden Dimensions, and the Quest for the Ultimate Theory", 464, "0393338101", BindingType.HARD_COVER, "Brian Greene"));
    	ret.add(new BookDBObject(new ObjectID(5), "And Then She Was GONE", 262, "23471901", BindingType.HARD_COVER, "Christopher Greyson"));
    	
    	return ret;
    }

  
    public void testStore()
    {
    	List<BookDBObject> soft_cover = createPaperbackBooks();
    	List<BookDBObject> hard_cover = createHardCoverBooks();
    	
    	// First off, delete any objects that may exist in the store...
    	for ( BookDBObject book : soft_cover )
    	{
    		env.store.delete(book.getStorableS3Path());
    	}
    	
    	// Now, upsert all the soft cover books
    	for ( BookDBObject book : soft_cover )
    	{
    		assert(env.store.upsert(book));
    	}
    	
    	// Now get all books and verify they match...
    	{
    		for ( BookDBObject book : soft_cover )
        	{
    			BookDBObject from_store = (BookDBObject)env.store.get(book.getStorableS3Path(), null);
    			
    			assertEquals(from_store, book);
        	}
    	}
    	
    	// Now upsert to hard-cover
    	for ( BookDBObject book : hard_cover )
    	{
    		assert(env.store.upsert(book));
    	}
    	
    	// Now get all books and verify they match the hard cover versions... 
    	{
    		for ( BookDBObject book : hard_cover )
        	{
    			BookDBObject from_store = (BookDBObject)env.store.get(book.getStorableS3Path(), null);
    			
    			assertEquals(from_store, book);
        	}
    	}
    	
    	// Now verify that all the object exists...
    	for ( BookDBObject book : hard_cover )
    	{
    		assert(env.store.objectExists(book.getStorableS3Path(),false));
    	}
    	
    	// Now test that read only store can not delete anything...
    	{ 
    		for ( BookDBObject book : hard_cover )
        	{
        		assertEquals(false,env.read_only_store.delete(book.getStorableS3Path()));
        	}
    		
    		// Now verify that all the object still exist...
        	for ( BookDBObject book : hard_cover )
        	{
        		assert(env.read_only_store.objectExists(book.getStorableS3Path(),false));
        	}
    	}
    	
    	// Now test that the read only store can't upsert anything
    	{
    		for ( BookDBObject book : soft_cover )
        	{
        		assertEquals(false, env.read_only_store.upsert(book));
        	}
    		
    		{
        		for ( BookDBObject book : hard_cover )
            	{
        			BookDBObject from_store = (BookDBObject)env.store.get(book.getStorableS3Path(), null);
        			
        			assertEquals(from_store, book);
            	}
        	}
    	}
    	
    	// Now test out scan
    	{
	    	ScanResults results = new ScanResults();
	    	
	    	env.store.scan(S3Path.PATH_BUCKET_ROOT, results, 2);
	    	
	    	assertEquals(5, results.objects.size());
	    	
	    	for ( BookDBObject book : hard_cover )
	    	{
	    		assertEquals(book, results.objects.get(book.getSimpleObjectID()));
	    	}
    	}
    	
    	// Final test, delete everything, ensure nothing exists
    	{
    	
	    	// Now delete everything
	    	for ( BookDBObject book : hard_cover )
	    	{
	    		env.store.delete(book.getStorableS3Path());
	    	}
	    	
	    	// Now test that nothing exists
	    	for ( BookDBObject book : hard_cover )
	    	{
	    		assertEquals(false,env.store.objectExists(book.getStorableS3Path(),false));
	    	}
    	}
    }
    
    private class ScanResults implements ScanListener
    {
    	private Map<ObjectID, BookDBObject> objects = new ConcurrentHashMap();

		public boolean shouldLoadObject(OperationScan scan, S3Path path, S3ObjectSummary object_summary) 
		{
			for ( BookDBObject book : createPaperbackBooks() )
			{
				if ( book.getStorableS3Path().equals(path) ) return true;
			}
			
			return false;
		}

		public void onLoadObject(OperationScan scan, S3Path path, S3ObjectSummary object_summary, StandardObject obj) 
		{
			BookDBObject book = (BookDBObject)obj;
			objects.put(book.getSimpleObjectID(), book);
		}

		public void onScanComplete(OperationScan scan) 
		{
		}

		public void onScanHearbeat(OperationScan scan) 
		{	
		}
    }
}
