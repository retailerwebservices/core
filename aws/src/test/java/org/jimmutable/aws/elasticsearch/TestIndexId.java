package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.objects.Stringable;

import org.jimmutable.util.StringableTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

//CODE REVEIW: Should be IndexIdTest to match our convention
public class TestIndexId extends StringableTest
{

	@Test
	public void testConverter()
	{
		IndexId defaulted = IndexId.CONVERTER.fromString("no.no.no", new IndexId("yes"));
		assertEquals(defaulted.getSimpleValue(), "yes");
	}

	@Test
	public void inValid()
	{
		assertNotValid(null);
		assertNotValid("foo/bar");
		assertNotValid("foo:bar");
		assertNotValid("");
		assertNotValid("foo!");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++) {
			sb.append('a');
		}

		assertNotValid(sb.toString());

		assertNotValid("12");

	}

	@Test
	public void valid()
	{
		assertValid("ABB1924", "abb1924");
		assertValid("abb1924", "abb1924");
		assertValid("aBb1924", "abb1924");
	}

	@Override
	public Stringable fromString(String src)
	{
		return new IndexId(src);
	}

}
