package org.jimmutable.util;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;

import static org.junit.Assert.assertEquals;

abstract public class StringableTest
{
	abstract public Stringable fromString(String src);

	public void assertNotValid(String src_code)
	{
		try {
			fromString(src_code);
			assert (false); // failure, should have not been valid
		} catch (ValidationException e) {
			// This is what we expect
		}
	}

	public void assertValid(String src_code, String expected_value)
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
