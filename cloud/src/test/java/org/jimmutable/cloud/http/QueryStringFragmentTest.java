package org.jimmutable.cloud.http;

import static org.junit.Assert.assertTrue;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.jimmutable.core.utils.TestingUtils;
import org.junit.Test;

public class QueryStringFragmentTest 
{
	StringableTestingUtils<QueryStringFragment> tester = new StringableTestingUtils(new QueryStringFragment.MyConverter());
	
	@Test
	public void testEncodingAndDecoding()
	{
		assertTrue(QueryStringFragment.URLDecode("hello%20world").equals("hello world"));
		assertTrue(QueryStringFragment.URLDecode("hello%20world%3Dhello%2Bworld").equals("hello world=hello+world"));
		
		
		assertTrue(QueryStringFragment.URLEncode("hello world").equals("hello+world"));
		assertTrue(QueryStringFragment.URLEncode("a&b=c+d").equals("a%26b%3Dc%2Bd"));
		
		testOneStringEncodeDecode("a&b=c+d");
		testOneStringEncodeDecode("+=");
		testOneStringEncodeDecode("foo=bar&baz=quz");
		testOneStringEncodeDecode("+=!@#$%^&*()-_|\\\"\';:][{},.<>");
		
		testOneStringEncodeDecode(TestingUtils.createAcidString());
	}
	
	@Test
	public void testQueryStringFragment()
	{
		QueryStringFragment fragment;
		
		{
			fragment = tester.create("foo=bar", null);
			assertTrue(fragment != null);

			assertTrue(fragment.getSimpleKey().equals(new QueryStringKey("foo")));
			assertTrue(fragment.getSimpleFragmentDecodedValue().equals("bar"));
		}
		
		{
			fragment = tester.create("FOO=BaR", null);
			assertTrue(fragment != null);

			assertTrue(fragment.getSimpleKey().equals(new QueryStringKey("foo")));
			assertTrue(fragment.getSimpleFragmentDecodedValue().equals("BaR"));
		}
		
		{
			fragment = tester.create(" FOO!! =BaR", null);
			assertTrue(fragment != null);

			assertTrue(fragment.getSimpleKey().equals(new QueryStringKey("foo")));
			assertTrue(fragment.getSimpleFragmentDecodedValue().equals("BaR"));
		}
		
		{
			fragment = tester.create("foo=bar baz", null);
			
			assertTrue(fragment != null);
			
			assertTrue(fragment.getSimpleKey().equals(new QueryStringKey("foo")));
			assertTrue(fragment.getSimpleFragmentDecodedValue().equals("bar baz"));
		}
		
		testOneFragmentFromPieces(new QueryStringKey("foo"),"bar");
	}
	
	public void testOneStringEncodeDecode(String value)
	{
		String tmp = QueryStringFragment.URLEncode(value);
		tmp = QueryStringFragment.URLDecode(tmp);
		
		assertTrue(tmp.equals(value));
	}
	
	public void testOneFragmentFromPieces(QueryStringKey key, String value)
	{
		QueryStringFragment fragment = new QueryStringFragment(key,value);
		
		QueryStringFragment from_string = new QueryStringFragment(fragment.getSimpleValue());

		assertTrue(from_string.getSimpleKey().equals(key));
		///assertTrue(from_string.getSimpleFragmentDecodedValue());
	}
}

