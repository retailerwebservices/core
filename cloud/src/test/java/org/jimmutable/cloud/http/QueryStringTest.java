package org.jimmutable.cloud.http;

import java.util.Objects;

import org.jimmutable.cloud.http.QueryString;
import org.jimmutable.cloud.http.QueryStringFragment;
import org.jimmutable.cloud.http.QueryStringKey;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class QueryStringTest
{
	StringableTester<QueryString> tester = new StringableTester(new QueryString.MyConverter());
	
	@Test
	public void testSimple()
	{
		QueryString qs = tester.assertValid("foo=bar&baz=quz");
		
		assert(qs.containsKey("foo"));
		assert(qs.containsKey("baz"));
		assert(qs.containsKey("FOO"));
		assert(qs.containsKey("BAZ"));
		
		
		assert(!qs.containsKey((QueryStringKey)null));
		assert(!qs.containsKey((String)null));
		assert(!qs.containsKey("bar"));
		assert(!qs.containsKey("quz"));
		
	
		assert(Objects.equals(qs.getOptionalValue("foo", null),"bar"));
		assert(Objects.equals(qs.getOptionalValue("baz", null),"quz"));
		
		assert(Objects.equals(qs.getOptionalValue("FOO", null),"bar"));
		assert(Objects.equals(qs.getOptionalValue("BAZ", null),"quz"));
		
		assert(Objects.equals(qs.getOptionalValue("zztop", null),null));
		assert(Objects.equals(qs.getOptionalValue("zztop", "foo"),"foo"));
		
		assert(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 1,null),null));
	}
	
	@Test
	public void testMultipleValues()
	{
		QueryString qs = tester.assertValid("foo=bar&foo=baz&foo=quz");
		
		assert(qs.containsKey("foo"));
		
		
		assert(!qs.containsKey((QueryStringKey)null));
		assert(!qs.containsKey((String)null));
		assert(!qs.containsKey("baz"));
		assert(!qs.containsKey("quz"));
		
	
		assert(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 0,null),"bar"));
		assert(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 1,null),"baz")); 
		assert(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 2,null),"quz"));
	}
	
	
	@Test
	public void testCaseSensativity()
	{
		QueryString qs = tester.assertValid("FOO=BAR&BAZ=QUZ");
		
		assert(qs.containsKey("foo"));
		assert(qs.containsKey("baz"));
		assert(qs.containsKey("FOO"));
		assert(qs.containsKey("BAZ"));
		
		
		assert(!qs.containsKey((QueryStringKey)null));
		assert(!qs.containsKey((String)null));
		assert(!qs.containsKey("bar"));
		assert(!qs.containsKey("quz"));
		
	
		assert(Objects.equals(qs.getOptionalValue("foo", null),"BAR"));
		assert(Objects.equals(qs.getOptionalValue("baz", null),"QUZ"));
		
		assert(Objects.equals(qs.getOptionalValue("FOO", null),"BAR"));
		assert(Objects.equals(qs.getOptionalValue("BAZ", null),"QUZ"));
		
		assert(Objects.equals(qs.getOptionalValue("zztop", null),null));
		assert(Objects.equals(qs.getOptionalValue("zztop", "foo"),"foo"));
		
		assert(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 1,null),null));
	}
	
	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
	}
	
	@Test
	public void testEmptyString()
	{
		QueryString qs = tester.assertValid("");
		
		int count = 0;
		
		for ( QueryStringFragment f : qs.getSimpleQueryStringFragments() )
		{
			count++;
		}
		
		assert(count == 0);
	}
	
}
