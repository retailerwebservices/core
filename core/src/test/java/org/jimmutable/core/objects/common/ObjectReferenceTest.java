package org.jimmutable.core.objects.common;

import org.jimmutable.core.utils.StringableTestingUtils;
import org.junit.Test;

import junit.framework.TestCase;

public class ObjectReferenceTest extends TestCase
{
	private StringableTestingUtils<ObjectReference> tester = new StringableTestingUtils<ObjectReference>(new ObjectReference.MyConverter());
	
	@Test
	public void testValid()
	{
		assertTrue(tester.isValid("kind:0000-0000-0000-0000", "kind:0000-0000-0000-0000"));
		assertTrue(tester.isValid("Kind:0000-0000-0000-0001", "kind:0000-0000-0000-0001"));
		assertEquals(new ObjectReference(new Kind("kind"), new ObjectId("0000-0000-0000-0002")).getSimpleValue(), "kind:0000-0000-0000-0002");
	}

	@Test
	public void testInvalid()
	{
		assertTrue(tester.isInvalid(null));
		assertTrue(tester.isInvalid(""));
		assertTrue(tester.isInvalid("&&!(#$"));

		assertTrue(tester.isInvalid(":0000-0000-0000-a123"));
		assertTrue(tester.isInvalid("abc:"));
		assertTrue(tester.isInvalid("ab/0000-0000-00A0-a123:txt"));
	}

}
