package org.jimmutable.core.examples.product_data;

import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class ItemAttributeTest
{
	private StringableTestingUtils<ItemAttribute> tester = new StringableTestingUtils(new ItemAttribute.MyConverter());

	@Test
	public void testAttribute()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid("foo-bar"));
		assertTrue(tester.isInvalid("foo:bar"));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("foo!"));

		assertTrue(tester.isValid("IMG_SRC_URL0","IMG_SRC_URL0"));
		assertTrue(tester.isValid("NEW_LONG_DESCRIPTION","NEW_LONG_DESCRIPTION"));
		assertTrue(tester.isValid("New_LONG_DeSCRIPTION","NEW_LONG_DESCRIPTION"));
	}
}
