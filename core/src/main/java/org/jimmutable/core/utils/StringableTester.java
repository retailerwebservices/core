package org.jimmutable.core.utils;

import java.util.Objects;

import org.jimmutable.core.objects.Stringable;

/**
 * This class should not be used as it utilizes Java's assert method. Java's
 * assert by default will not throw an AssertionException on failure unless the
 * vm argument "-ea" is passed in. So when utilizing this utility it was very
 * easy to get a false positive on unit test that is actually failing.
 * 
 * The solution we came to in order to avoid the headache of having to remember
 * turning on a vm flag every time we want to utilize this class was to instead
 * make StringableTestingUtils. The new class acts the same but only returns
 * boolean values for failing and passing. Which allows us to avoid using Java
 * asserts as well as avoids creating a dependency of JUnit on our core classes.
 */
@Deprecated
public class StringableTester<S extends Stringable> 
{
	private Stringable.Converter<S> converter = null;
	
	public StringableTester(Stringable.Converter<S> converter)
	{
		Validator.notNull(converter);
		this.converter = converter;
	}

	public S assertValid(String value, String normalized_value)
	{
		S ret = converter.fromString(value, null);
		
		assert(ret != null);
		
		
		assert(Objects.equals(ret.getSimpleValue(), normalized_value));
		
		return ret;
	}
	
	public S assertValid(String value)
	{
		S ret = converter.fromString(value, null);
		
		assert(ret != null);
		
		return ret;
	}
	
	public void assertInvalid(String value)
	{
		assert(null == converter.fromString(value, null));
	}
}
