package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.util.StringableTest;
import org.junit.Test;

public class IndexVersionTest extends StringableTest
{

	@Override
	public Stringable fromString(String src)
	{
		return new IndexVersion(src);
	}

	@Test
	public void valid()
	{
		super.assertValid("v0192837465", "v0192837465");
		super.assertValid(" V2 ", "v2");
	}

	@Test
	public void inValid()
	{
		super.assertNotValid("");
		super.assertNotValid("v");
		super.assertNotValid("vv");
		super.assertNotValid("v 2");
	}

}
