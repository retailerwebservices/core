package org.jimmutable.cloud.attachments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.ObjectId;
import org.junit.Test;

public class AttachmentMetaDataTest extends StubTest
{
	@Test
	public void testAttatchmentMetaDataSerialization()
	{
		AttachmentMetaData meta_data = new AttachmentMetaData(new ObjectId(123), "Description", new DownloadFileName("downloadfilename"), "mimetype", 1L, 1);
		//System.out.println(meta_data.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		
		
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"com.digitalpanda.objects.attatchments.attachmentmetadata\","
			     , "  \"id\" : \"0000-0000-0000-007b\","
			     , "  \"description\" : \"Description\","
			     , "  \"filename\" : {"
			     , "    \"type_hint\" : \"string\","
			     , "    \"primitive_value\" : \"downloadfilename\""
			     , "  },"
			     , "  \"mimetype\" : \"mimetype\","
			     , "  \"lastmodifiedtime\" : 1,"
			     , "  \"size\" : 1"
			     , "}"
			);
		
		AttachmentMetaData obj = (AttachmentMetaData)StandardObject.deserialize(obj_string);
		
		assertEquals(meta_data, obj);

		assertEquals(obj.getSimpleMimeType(), "mimetype");

		assertEquals(obj.getSimpleSize(), 1);
	}

	@Test
	public void testComparisonAndEquals()
	{
		AttachmentMetaData meta_data = new AttachmentMetaData(new ObjectId(123), "Description", new DownloadFileName("downloadfilename"), "mimetype", 1, 1);
		AttachmentMetaData meta_data2 = new AttachmentMetaData(new ObjectId(123), "Description", new DownloadFileName("downloadfilename"), "mimetype", 1, 1);
		assertTrue(meta_data.equals(meta_data2));
		assertEquals(0, meta_data.compareTo(meta_data2));

		meta_data2 = new AttachmentMetaData(new ObjectId(122), "Description", new DownloadFileName("downloadfilename"), "mimetype", 1, 1);
		assertFalse(meta_data.equals(meta_data2));
		assertEquals(1, meta_data.compareTo(meta_data2));

		meta_data2 = new AttachmentMetaData(new ObjectId(124), "Description", new DownloadFileName("downloadfilename"), "mimetype", 1, 1);
		assertFalse(meta_data.equals(meta_data2));
		assertEquals(-1, meta_data.compareTo(meta_data2));
	}
}
