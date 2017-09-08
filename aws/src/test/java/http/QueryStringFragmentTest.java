package http;

import org.jimmutable.aws.http.QueryStringFragment;
import org.jimmutable.aws.http.QueryStringKey;
import org.jimmutable.core.utils.StringableTester;
import org.jimmutable.core.utils.TestingUtils;
import org.junit.Test;

public class QueryStringFragmentTest 
{
	StringableTester<QueryStringFragment> tester = new StringableTester(new QueryStringFragment.MyConverter());
	
	@Test
	public void testEncodingAndDecoding()
	{
		assert(QueryStringFragment.URLDecode("hello%20world").equals("hello world"));
		assert(QueryStringFragment.URLDecode("hello%20world%3Dhello%2Bworld").equals("hello world=hello+world"));
		
		
		assert(QueryStringFragment.URLEncode("hello world").equals("hello+world"));
		assert(QueryStringFragment.URLEncode("a&b=c+d").equals("a%26b%3Dc%2Bd"));
		
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
			fragment = tester.assertValid("foo=bar");
			
			assert(fragment.getSimpleKey().equals(new QueryStringKey("foo")));
			assert(fragment.getSimpleFragmentDecodedValue().equals("bar"));
		}
		
		{
			fragment = tester.assertValid("FOO=BaR");
			
			assert(fragment.getSimpleKey().equals(new QueryStringKey("foo")));
			assert(fragment.getSimpleFragmentDecodedValue().equals("BaR"));
		}
		
		{
			fragment = tester.assertValid(" FOO!! =BaR");
			
			assert(fragment.getSimpleKey().equals(new QueryStringKey("foo")));
			assert(fragment.getSimpleFragmentDecodedValue().equals("BaR"));
		}
		
		testOneFragmentFromPieces(new QueryStringKey("foo"),"bar");
	}
	
	
	private void testOneStringEncodeDecode(String value)
	{
		String tmp = QueryStringFragment.URLEncode(value);
		tmp = QueryStringFragment.URLDecode(tmp);
		
		assert(tmp.equals(value));
	}
	
	public void testOneFragmentFromPieces(QueryStringKey key, String value)
	{
		QueryStringFragment fragment = new QueryStringFragment(key,value);
		
		QueryStringFragment from_string = new QueryStringFragment(fragment.getSimpleValue());
		
		
		
		assert(from_string.getSimpleKey().equals(key));
		///assert(from_string.getSimpleFragmentDecodedValue());
	}
}

