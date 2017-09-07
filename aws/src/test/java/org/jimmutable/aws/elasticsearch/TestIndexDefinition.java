package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.util.StringableTest;
import org.junit.Test;

public class TestIndexDefinition extends StringableTest
{

	@Test
	public void inValid()
	{
		assertNotValid(null);
		assertNotValid("foo:bar");
		assertNotValid("");
		assertNotValid("foo!");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++) {
			sb.append('a');
		}

		assertNotValid(sb.append("/").append("theindexId").toString());

		assertNotValid("12");

	}

	@Test
	public void valid()
	{
		assertValid("foo/bar", "foo/bar");
	}

	@Override
	public Stringable fromString(String src)
	{
		return new IndexDefinition(src);
	}

}
