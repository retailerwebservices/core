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
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("foo!bar:v"));
		assertTrue(tester.isInvalid("foo!bar"));

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++)
		{
			sb.append('a');
		}

		assertTrue(tester.isInvalid(sb.append("/").append("theindexId").toString()));

		assertTrue(tester.isInvalid("12"));
		
		assertTrue(tester.isInvalid("foo_bar"));
		assertTrue(tester.isInvalid("foo_ba_"));
		assertTrue(tester.isInvalid("foo_bar_v"));
		assertTrue(tester.isInvalid("foo_bar_2"));
	}

	@Test
	public void valid()
	{
		assertTrue(tester.isValid("foo_bar_v2", "foo_bar_v2"));
		assertTrue(tester.isValid("FOO_BAR_V2", "foo_bar_v2"));
	}
}
