package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.StringableTester;
import org.jimmutable.util.StringableTest;
import org.junit.Test;

import junit.framework.TestCase;

import static org.junit.Assert.assertEquals;

public class IndexIdTest extends TestCase
{
	private StringableTester<ApplicationId> tester = new StringableTester(new ApplicationId.MyConverter());
	@Test
	public void testConverter()
	{
		IndexId defaulted = IndexId.CONVERTER.fromString("no.no.no", new IndexId("yes"));
		assertEquals(defaulted.getSimpleValue(), "yes");
	}

	@Test
	public void inValid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("foo/bar");
		tester.assertInvalid("foo:bar");
		tester.assertInvalid("");
		tester.assertInvalid("foo!");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++) {
			sb.append('a');
		}

		tester.assertInvalid(sb.toString());

		tester.assertInvalid("12");

	}

	@Test
	public void valid()
	{
		tester.assertValid("ABB1924", "abb1924");
		tester.assertValid("abb1924", "abb1924");
		tester.assertValid("aBb1924", "abb1924");
	}

}
