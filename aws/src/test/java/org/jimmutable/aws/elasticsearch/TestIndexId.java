package org.jimmutable.aws.elasticsearch;

import static org.junit.Assert.assertEquals;

import org.jimmutable.cloud.elasticsearch.IndexId;
import org.jimmutable.core.exceptions.ValidationException;
import org.junit.Test;

public class TestIndexId
{

	@Test
	public void testConverter()
	{
		IndexId defaulted = IndexId.CONVERTER.fromString("no.no.no", new IndexId("yes"));
		assertEquals(defaulted.getSimpleValue(), "yes");

	}

	@Test(expected = ValidationException.class)
	public void invalidMaxLength()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++)
		{
			sb.append('a');
		}
		new IndexId(sb.toString());
	}

	@Test(expected = ValidationException.class)
	public void invalidMinLength()
	{
		new IndexId("12");
	}

	@Test(expected = ValidationException.class)
	public void invalidChar()
	{
		new IndexId("1.23456");
	}

	@Test
	public void valid()
	{
		IndexId i = new IndexId("123456");
		assertEquals("123456", i.getSimpleValue());
	}

}
