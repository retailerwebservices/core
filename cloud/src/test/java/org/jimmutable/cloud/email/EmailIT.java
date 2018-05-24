package org.jimmutable.cloud.email;

import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.IntegrationTest;
import org.jimmutable.core.objects.Builder;
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
		Builder b = new Builder(EmailTest.base_email);
		b.set(Email.FIELD_TEXT_BODY, "body");
		assertTrue(CloudExecutionEnvironment.getSimpleCurrent().getSimpleEmailService().sendEmail(b.create(null)));
	}

}
