package org.jimmutable.core.objects.common;

import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

import junit.framework.TestCase;

public class ObjectReferenceTest extends TestCase
{
	private StringableTester<ObjectReference> tester = new StringableTester<ObjectReference>(new ObjectReference.MyConverter());
	@Test
	public void testValid()
	{
		tester.assertValid("kind:0000-0000-0000-0000", "kind:0000-0000-0000-0000");
		tester.assertValid("Kind:0000-0000-0000-0001", "kind:0000-0000-0000-0001");
		assertEquals(new ObjectReference(new Kind("kind"), new ObjectId("0000-0000-0000-0002")).getSimpleValue(), "kind:0000-0000-0000-0002");
		
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("&&!(#$");

		tester.assertInvalid(":0000-0000-0000-a123");
		tester.assertInvalid("abc:");
		tester.assertInvalid("ab/0000-0000-00A0-a123:txt");
	}

}
