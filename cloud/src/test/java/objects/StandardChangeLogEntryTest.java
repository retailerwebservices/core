package objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.attachments.DownloadFileName;
import org.jimmutable.cloud.objects.StandardChangeLogEntry;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.ObjectReference;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;

public class StandardChangeLogEntryTest extends StubTest
{
	@Test
	public void serializationNulls()
	{
		ObjectId id = new ObjectId("0000-0000-0000-0000");
		StandardChangeLogEntry standard_change_log_entry = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), 0, new ObjectId("0000-0000-0000-0001"), "Short",null, new FieldArrayList<ObjectId>(), null,null);
		String serialized_value = standard_change_log_entry.serialize(Format.JSON_PRETTY_PRINT);
		
		//System.out.println(standard_change_log_entry.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"change_log_entry\","
			     , "  \"id\" : \"0000-0000-0000-0000\","
			     , "  \"subject\" : \"thing:0000-0000-0000-0000\","
			     , "  \"timestamp\" : 0,"
			     , "  \"changemadebyuserid\" : \"0000-0000-0000-0001\","
			     , "  \"short_description\" : \"Short\","
			     , "  \"attachments\" : [ ]"
			     , "}"
		);
		StandardChangeLogEntry obj = (StandardChangeLogEntry)StandardObject.deserialize(obj_string);
		
		assertEquals(serialized_value, obj_string);
		assertEquals(standard_change_log_entry, obj);
		

		assertEquals(obj.getSimpleObjectId(), id);

		assertEquals(obj.getSimpleAttachments().size(), 0);
	}
	
	@Test
	public void serializationAll()
	{
		ObjectId id = new ObjectId("0000-0000-0000-0000");
		
		FieldList<ObjectId> attachments = new FieldArrayList<ObjectId>();
		attachments.add(new ObjectId(5));
		attachments.add(new ObjectId(6));
		attachments.add(new ObjectId(7));
		
		AttachmentMetaData meta_data = new AttachmentMetaData(new ObjectId(123), "Description", new DownloadFileName("downloadfilename1"), "mimetype", 1L, 1);
		AttachmentMetaData meta_data_after = new AttachmentMetaData(new ObjectId(123), "Description", new DownloadFileName("downloadfilename2"), "mimetype", 1L, 2);
		
		StandardChangeLogEntry standard_change_log_entry = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), 0, new ObjectId("0000-0000-0000-0001"), "Short","my comment", attachments, meta_data.toString(),meta_data_after.toString());
		
		System.out.println(standard_change_log_entry.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"change_log_entry\","
			     , "  \"id\" : \"0000-0000-0000-0000\","
			     , "  \"subject\" : \"thing:0000-0000-0000-0000\","
			     , "  \"timestamp\" : 0,"
			     , "  \"changemadebyuserid\" : \"0000-0000-0000-0001\","
			     , "  \"short_description\" : \"Short\","
			     , "  \"comments\" : \"my comment\","
			     , "  \"attachments\" : [ \"0000-0000-0000-0005\", \"0000-0000-0000-0006\", \"0000-0000-0000-0007\" ],"
			     , "  \"before_object\" : {"
			     , "    \"type_hint\" : \"com.digitalpanda.objects.attatchments.attachmentmetadata\","
			     , "    \"id\" : \"0000-0000-0000-007b\","
			     , "    \"description\" : \"Description\","
			     , "    \"filename\" : {"
			     , "      \"type_hint\" : \"string\","
			     , "      \"primitive_value\" : \"downloadfilename1\""
			     , "    },"
			     , "    \"mimetype\" : \"mimetype\","
			     , "    \"lastmodifiedtime\" : 1,"
			     , "    \"size\" : 1"
			     , "  },"
			     , "  \"after_object\" : {"
			     , "    \"type_hint\" : \"com.digitalpanda.objects.attatchments.attachmentmetadata\","
			     , "    \"id\" : \"0000-0000-0000-007b\","
			     , "    \"description\" : \"Description\","
			     , "    \"filename\" : {"
			     , "      \"type_hint\" : \"string\","
			     , "      \"primitive_value\" : \"downloadfilename2\""
			     , "    },"
			     , "    \"mimetype\" : \"mimetype\","
			     , "    \"lastmodifiedtime\" : 1,"
			     , "    \"size\" : 2"
			     , "  }"
			     , "}"
			);

		StandardChangeLogEntry obj = (StandardChangeLogEntry)StandardObject.deserialize(obj_string);
		
		
		assertEquals(standard_change_log_entry, obj);
		

		assertEquals(obj.getSimpleObjectId(), id);

		assertEquals(obj.getSimpleAttachments().size(), 3);
	}

	@Test
	public void testComparisonAndEquals()
	{
		ObjectId id = new ObjectId("0000-0000-0000-0000");
		StandardChangeLogEntry standard_change_log_entry = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), 0, new ObjectId("0000-0000-0000-0001"), "Short","comments", new FieldArrayList<ObjectId>(), null,"");
		StandardChangeLogEntry standard_change_log_entry2 = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), 0, new ObjectId("0000-0000-0000-0001"), "Short","comments", new FieldArrayList<ObjectId>(), null,"");
			assertTrue(standard_change_log_entry.equals(standard_change_log_entry2));
		assertEquals(0, standard_change_log_entry.compareTo(standard_change_log_entry2));
		
		standard_change_log_entry2 = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), -1, new ObjectId("0000-0000-0000-0001"), "Short","comments", new FieldArrayList<ObjectId>(), null,"");
		assertFalse(standard_change_log_entry.equals(standard_change_log_entry2));
		assertEquals(1, standard_change_log_entry.compareTo(standard_change_log_entry2));
		
		standard_change_log_entry2 = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), 1, new ObjectId("0000-0000-0000-0001"), "Short","comments", new FieldArrayList<ObjectId>(), null,"");
		assertFalse(standard_change_log_entry.equals(standard_change_log_entry2));
		assertEquals(-1, standard_change_log_entry.compareTo(standard_change_log_entry2));
	}
	
	@Test
	public void testOptionalFields()
	{
		ObjectId id = new ObjectId("0000-0000-0000-0000");
		StandardChangeLogEntry standard_change_log_entry = new StandardChangeLogEntry(id, new ObjectReference(new Kind("thing"), id), 0, new ObjectId("0000-0000-0000-0001"), "Short",null, new FieldArrayList<ObjectId>(), null,"");

		assertEquals("hello", standard_change_log_entry.getOptionalComments("hello"));
		
	}
}
