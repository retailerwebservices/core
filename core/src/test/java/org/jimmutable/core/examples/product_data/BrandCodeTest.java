package org.jimmutable.core.examples.product_data;

import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class BrandCodeTest
{
	private StringableTestingUtils<BrandCode> tester = new StringableTestingUtils(new BrandCode.MyConverter());

	@Test
	public void testBrandCode()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid("foo-bar"));
		assertTrue(tester.isInvalid("foo:bar"));
		assertTrue(tester.isInvalid(""));

		assertTrue(tester.isValid("AMN", "AMN"));
		assertTrue(tester.isValid("amn", "AMN"));
		assertTrue(tester.isValid("gE", "GE"));

		assertTrue(tester.isValid("ca_sny", "CA_SNY"));
	}
}
