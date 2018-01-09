package org.jimmutable.core.examples.product_data;

import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class PartNumberTest
{
	private StringableTestingUtils<PartNumber> tester = new StringableTestingUtils(new PartNumber.MyConverter());

   @Test
   public void testBrandCode()
   {
	   assertTrue(tester.isInvalid(null));
	   assertTrue(tester.isInvalid("foo-bar"));
	   assertTrue(tester.isInvalid("foo:bar"));
	   assertTrue(tester.isInvalid(""));
	   assertTrue(tester.isInvalid("foo!"));
   	
	   assertTrue(tester.isValid("ABB1924","ABB1924"));
	   assertTrue(tester.isValid("abb1924","ABB1924"));
	   assertTrue(tester.isValid("aBb1924","ABB1924"));
   }
}

