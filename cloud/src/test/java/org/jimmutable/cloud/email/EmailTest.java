package org.jimmutable.cloud.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.objects.JimmutableBuilder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.EmailAddress;
import org.junit.Test;

public class EmailTest extends StubTest
{

	static public final Email base_email;

	static
	{
		JimmutableBuilder b = new JimmutableBuilder(Email.TYPE_NAME);
		b.set(Email.FIELD_FROM_NAME, "Someone");
		b.set(Email.FIELD_FROM, new EmailAddress("someone@retailerwebservices.com"));
		b.set(Email.FIELD_SUBJECT, "subject");
		b.add(Email.FIELD_TO, new EmailAddress("someone.else@retailerwebservices.com"));
		b.set(Email.FIELD_HTML_BODY, "<p>body</p>");
		base_email = b.create();
	}

	@Test
	public void required()
	{
		//System.out.println(base_email.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"email\","
			     , "  \"from\" : \"someone@retailerwebservices.com\","
			     , "  \"from_name\" : \"Someone\","
			     , "  \"subject\" : \"subject\","
			     , "  \"to\" : [ \"someone.else@retailerwebservices.com\" ],"
			     , "  \"html_body\" : \"<p>body</p>\""
			     , "}"
			);

			
		Email obj = (Email)StandardObject.deserialize(obj_string);
		assertEquals(obj,base_email);
		assertFalse(base_email.hasBcc() && base_email.hasCc() && base_email.hasReplyTo() && base_email.hasTextBody());
	}
	
	@Test
	public void optional()
	{
		JimmutableBuilder b = new JimmutableBuilder(base_email);
		b.add(Email.FIELD_BCC, "bcc@retailerwebservices.com");
		b.add(Email.FIELD_CC, "cc@retailerwebservices.com");
		b.add(Email.FIELD_REPLY_TO, "reply_to@retailerwebservices.com");
		
		Email email = b.create();
		
		//System.out.println(email.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"email\","
			     , "  \"from\" : \"someone@retailerwebservices.com\","
			     , "  \"from_name\" : \"Someone\","
			     , "  \"subject\" : \"subject\","
			     , "  \"to\" : [ \"someone.else@retailerwebservices.com\" ],"
			     , "  \"bcc\" : [ \"bcc@retailerwebservices.com\" ],"
			     , "  \"cc\" : [ \"cc@retailerwebservices.com\" ],"
			     , "  \"reply_to\" : [ \"reply_to@retailerwebservices.com\" ],"
			     , "  \"html_body\" : \"<p>body</p>\""
			     , "}"
			);

		Email obj = (Email)StandardObject.deserialize(obj_string);
		assertEquals(obj,email);
		assertTrue(email.hasBcc() && email.hasCc() && email.hasHtmlBody() && email.hasReplyTo());
	}

}
