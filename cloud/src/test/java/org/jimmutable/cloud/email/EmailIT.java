package org.jimmutable.cloud.email;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.common.EmailAddress;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmailIT extends IntegrationTest
{

	@BeforeClass
	public static void start()
	{
		setupEnvironment();
	}

	@Test
	public void sendEmail()
	{

		Builder b = new Builder(Email.TYPE_NAME);
		b.set(Email.FIELD_FROM_NAME, "AdRocket");
		b.set(Email.FIELD_FROM, new EmailAddress("no-reply@retailerwebservices.com"));
		b.set(Email.FIELD_SUBJECT, "subject");
		b.add(Email.FIELD_TO, new EmailAddress("trevor.box@retailerwebservices.com"));
		b.set(Email.FIELD_HTML_BODY, "<i>body</i>");

		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleEmailService().sendEmail(b.create(null)));
	}

}
