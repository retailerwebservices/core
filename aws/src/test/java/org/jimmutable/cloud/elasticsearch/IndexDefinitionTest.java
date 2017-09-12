package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.util.StringableTest;
import org.junit.Test;

public class IndexDefinitionTest extends StringableTest
{

	@Test
	public void inValid()
	{
		assertNotValid(null);
		assertNotValid("");
		assertNotValid("foo!bar:v");
		assertNotValid("foo/bar");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++) {
			sb.append('a');
		}

		assertNotValid(sb.append("/").append("theindexId").toString());

		assertNotValid("12");

		assertNotValid("foo:bar");
		assertNotValid("foo:bar:");
		assertNotValid("foo:bar:v");
		assertNotValid("foo:bar:2");

	}

	@Test
	public void valid()
	{
		assertValid("foo:bar:v2", "foo:bar:v2");
		assertValid("FOO:BAR:V2", "foo:bar:v2");
	}

	@Override
	public Stringable fromString(String src)
	{
		return new IndexDefinition(src);
	}

}
