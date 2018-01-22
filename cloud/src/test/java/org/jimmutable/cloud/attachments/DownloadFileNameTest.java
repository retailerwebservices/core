package org.jimmutable.cloud.attachments;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.cloud.attachments.DownloadFileName;
import org.jimmutable.core.utils.StringableTester;
import org.junit.Test;

public class DownloadFileNameTest
{
	private StringableTester<DownloadFileName> tester = new StringableTester(new DownloadFileName.MyConverter());

	@Test
	public void testValid()
	{
		tester.assertValid("foo", "foo");
		tester.assertValid("FOO", "foo");

		tester.assertValid("abcdefghijklmnopqrstuvwxyz0123456789-_.", "abcdefghijklmnopqrstuvwxyz0123456789-_.");

		tester.assertValid("FoO-BAR", "foo-bar");
	}

	@Test
	public void testInvalid()
	{
		tester.assertInvalid(null);
		tester.assertInvalid("");
		tester.assertInvalid("&&!(#$");
		// Removed this test in favor of replacement of spaces rather than invalidation.
		// This is assuming the only use of this class is to upload attachments anyways
		// tester.assertInvalid("foo bar");
	}
}