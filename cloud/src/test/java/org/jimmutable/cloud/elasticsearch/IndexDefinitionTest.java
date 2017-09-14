package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class IndexDefinitionTest extends TestCase
{
	private StringableTester<ApplicationId> tester = new StringableTester(new ApplicationId.MyConverter());
	@Test
	public void inValid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("foo!bar:v");
		tester.assertInvalid("foo/bar");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++) {
			sb.append('a');
		}

		tester.assertInvalid(sb.append("/").append("theindexId").toString());

		tester.assertInvalid("12");

		tester.assertInvalid("foo:bar");
		tester.assertInvalid("foo:bar:");
		tester.assertInvalid("foo:bar:v");
		tester.assertInvalid("foo:bar:2");

	}

	@Test
	public void valid()
	{
		tester.assertValid("foo:bar:v2", "foo:bar:v2");
		tester.assertValid("FOO:BAR:V2", "foo:bar:v2");
	}

}
