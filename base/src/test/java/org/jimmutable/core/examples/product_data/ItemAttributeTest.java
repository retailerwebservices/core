package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.examples.product_data.ItemAttribute;
import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.StringableTest;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ItemAttributeTest extends StringableTest
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public ItemAttributeTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( ItemAttributeTest.class );
	}


	public Stringable fromString(String src) 
	{
		return new ItemAttribute(src);
	}

	public void testAttribute()
	{
		assertNotValid(null);
		assertNotValid("foo-bar");
		assertNotValid("foo:bar");
		assertNotValid("");
		assertNotValid("foo!");

		assertValid("IMG_SRC_URL0","IMG_SRC_URL0");
		assertValid("NEW_LONG_DESCRIPTION","NEW_LONG_DESCRIPTION");
		assertValid("New_LONG_DeSCRIPTION","NEW_LONG_DESCRIPTION");
	}
}
