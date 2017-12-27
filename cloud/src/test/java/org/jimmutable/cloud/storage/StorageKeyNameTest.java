package org.jimmutable.cloud.storage;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;

import junit.framework.TestCase;

public class StorageKeyNameTest extends TestCase
{
	public StorageKeyNameTest(String testName)
	{
		super(testName);
	}

	public Stringable fromString(String src)
	{
		return new StorageKeyName(src);
	}
	
	public void testStorageKeyName()
	{
    	assertNotValid(null);
    	assertNotValid("aaa!");
    	assertNotValid("11-222@---335");
    	assertNotValid("1234#567");
    	assertNotValid("");
    	assertNotValid("1---456-90-*");
    	
    	// don't accept keys longer than 255 chars
    	assertNotValid("ascdefghijklmnopqrstuvwxyzascdefghijklmnopqrstuvwxyzascdefghijklmnopqrstuvwxyz"
    			+ "ascdefghijklmnopqrstuvwxyzascdefghijklmnopqrstuvwxyzascdefghijklmnopqrstuvwxyz"
    			+ "ascdefghijklmnopqrstuvwxyzascdefghijklmnopqrstuvwxyzascdefghijklmnopqrstuvwxyz"
    			+ "ascdefghijklmnopqrstuv");

    	assertValid("this-is-valid", "this-is-valid");
    	assertValid("sHoulD-AUTO-lowerCase", "should-auto-lowercase");
    	assertValid("T415-1S_V41id", "t415-1s_v41id");
	}
	
	private void assertNotValid(String src_code)
	{
		Stringable ret = null;
		
		try
		{
			// failure, should have not been valid
			ret = fromString(src_code);
		}
		catch (ValidationException e)
		{
			assert (ret == null);
		}
	}

	private void assertValid(String src_code, String expected_value)
	{
		try {
			Stringable test = fromString(src_code);
			assertEquals(test.getSimpleValue(), expected_value);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}
	}

}
