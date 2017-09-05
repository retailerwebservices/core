package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.objects.Stringable;

import org.jimmutable.util.StringableTestOld;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestIndexId extends StringableTestOld {

	public TestIndexId(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(TestIndexId.class);
	}

	public void testConverter() {
		IndexId defaulted = IndexId.CONVERTER.fromString("no.no.no", new IndexId("yes"));
		assertEquals(defaulted.getSimpleValue(), "yes");
	}

	public void inValid() {
		assertNotValid(null);
		assertNotValid("foo/bar");
		assertNotValid("foo:bar");
		assertNotValid("");
		assertNotValid("foo!");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 65; i++) {
			sb.append('a');
		}

		assertNotValid(sb.toString());

		assertNotValid("12");

	}

	public void valid() {
		assertValid("ABB1924", "abb1924");
		assertValid("abb1924", "abb1924");
		assertValid("aBb1924", "abb1924");
	}

	@Override
	public Stringable fromString(String src) {
		return new IndexId(src);
	}

}
