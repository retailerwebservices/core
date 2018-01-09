package org.jimmutable.cloud.elasticsearch;

import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class IndexDefinitionTest
{
	private StringableTestingUtils<IndexDefinition> tester = new StringableTestingUtils(new IndexDefinition.MyConverter());
	
	@Test
	public void inValid()
	{
		assertTrue(tester.assertInvalid(null));
		assertTrue(tester.assertInvalid(""));
		assertTrue(tester.assertInvalid("foo!bar:v"));
		assertTrue(tester.assertInvalid("foo!bar"));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++)
		{
			sb.append('a');
		}

		assertTrue(tester.assertInvalid(sb.append("/").append("theindexId").toString()));

		assertTrue(tester.assertInvalid("12"));
		
		assertTrue(tester.assertInvalid("foo:bar"));
		assertTrue(tester.assertInvalid("foo:bar:"));
		assertTrue(tester.assertInvalid("foo:bar:v"));
		assertTrue(tester.assertInvalid("foo:bar:2"));
	}

	@Test
	public void valid()
	{
		assertTrue(tester.assertValid("foo:bar:v2", "foo:bar:v2"));
		assertTrue(tester.assertValid("FOO:BAR:V2", "foo:bar:v2"));
	}
}
