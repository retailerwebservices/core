package org.jimmutable.gcloud.logging;

import static org.junit.Assert.assertEquals;

import org.jimmutable.gcloud.logging.LogSupplier;
import org.junit.Test;

public class TestLogSupplier {

	@Test
	public void testFormat() {
		LogSupplier lf = new LogSupplier("I am the #%d %s donut.", 1, "jelly");
		assertEquals(lf.get(), "I am the #1 jelly donut.");
	}

	@Test
	public void invalidFormat() {
		LogSupplier lf = new LogSupplier("I am the #%d %s donut.", 1);
		assertEquals("I am the #%d %s donut.", lf.get());
	}

	@Test
	public void exception() {
		LogSupplier lf = new LogSupplier(new Exception("this is my exception"));
		assertEquals("java.lang.Exception: this is my exception", lf.get().split(System.lineSeparator(), -1)[0]);
	}

	@Test
	public void nulls() {
		LogSupplier lf = new LogSupplier(null, null);
		assertEquals("null", lf.get());
		lf = new LogSupplier(null);
		assertEquals("null", lf.get());
	}
}
