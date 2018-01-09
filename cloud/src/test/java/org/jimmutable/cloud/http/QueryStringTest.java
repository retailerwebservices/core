package org.jimmutable.cloud.http;

import static org.junit.Assert.assertTrue;

import java.util.Objects;

import org.jimmutable.cloud.http.QueryString;
import org.jimmutable.cloud.http.QueryStringFragment;
import org.jimmutable.cloud.http.QueryStringKey;
import org.jimmutable.core.utils.StringableTester;
import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

public class QueryStringTest
{
	StringableTestingUtils<QueryString> tester = new StringableTestingUtils(new QueryString.MyConverter());
	
	@Test
	public void testSimple()
	{
		QueryString qs = tester.create("foo=bar&baz=quz", null);
		assertTrue(qs != null);
		
		assertTrue(qs.containsKey("foo"));
		assertTrue(qs.containsKey("baz"));
		assertTrue(qs.containsKey("FOO"));
		assertTrue(qs.containsKey("BAZ"));
		
		
		assertTrue(!qs.containsKey((QueryStringKey)null));
		assertTrue(!qs.containsKey((String)null));
		assertTrue(!qs.containsKey("bar"));
		assertTrue(!qs.containsKey("quz"));
		
	
		assertTrue(Objects.equals(qs.getOptionalValue("foo", null),"bar"));
		assertTrue(Objects.equals(qs.getOptionalValue("baz", null),"quz"));
		
		assertTrue(Objects.equals(qs.getOptionalValue("FOO", null),"bar"));
		assertTrue(Objects.equals(qs.getOptionalValue("BAZ", null),"quz"));
		
		assertTrue(Objects.equals(qs.getOptionalValue("zztop", null),null));
		assertTrue(Objects.equals(qs.getOptionalValue("zztop", "foo"),"foo"));
		
		assertTrue(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 1,null),null));
	}
	
	@Test
	public void testMultipleValues()
	{
		QueryString qs = tester.create("foo=bar&foo=baz&foo=quz", null);
		assertTrue(qs != null);

		assertTrue(qs.containsKey("foo"));
		
		
		assertTrue(!qs.containsKey((QueryStringKey)null));
		assertTrue(!qs.containsKey((String)null));
		assertTrue(!qs.containsKey("baz"));
		assertTrue(!qs.containsKey("quz"));
		
	
		assertTrue(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 0,null),"bar"));
		assertTrue(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 1,null),"baz")); 
		assertTrue(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 2,null),"quz"));
	}
	
	
	@Test
	public void testCaseSensativity()
	{
		QueryString qs = tester.create("FOO=BAR&BAZ=QUZ", null);
		assertTrue(qs != null);

		assertTrue(qs.containsKey("foo"));
		assertTrue(qs.containsKey("baz"));
		assertTrue(qs.containsKey("FOO"));
		assertTrue(qs.containsKey("BAZ"));
		
		
		assertTrue(!qs.containsKey((QueryStringKey)null));
		assertTrue(!qs.containsKey((String)null));
		assertTrue(!qs.containsKey("bar"));
		assertTrue(!qs.containsKey("quz"));
		
	
		assertTrue(Objects.equals(qs.getOptionalValue("foo", null),"BAR"));
		assertTrue(Objects.equals(qs.getOptionalValue("baz", null),"QUZ"));
		
		assertTrue(Objects.equals(qs.getOptionalValue("FOO", null),"BAR"));
		assertTrue(Objects.equals(qs.getOptionalValue("BAZ", null),"QUZ"));
		
		assertTrue(Objects.equals(qs.getOptionalValue("zztop", null),null));
		assertTrue(Objects.equals(qs.getOptionalValue("zztop", "foo"),"foo"));
		
		assertTrue(Objects.equals(qs.getOptionalValue(new QueryStringKey("foo"), 1,null),null));
	}
	
	@Test
	public void testInvalid()
	{
		assertTrue(tester.assertInvalid(null));
	}
	
	@Test
	public void testEmptyString()
	{
		QueryString qs = tester.create("", null);
		assertTrue(qs != null);

		int count = 0;
		
		for ( QueryStringFragment f : qs.getSimpleQueryStringFragments() )
		{
			count++;
		}
		
		assertTrue(count == 0);
	}
	
}
