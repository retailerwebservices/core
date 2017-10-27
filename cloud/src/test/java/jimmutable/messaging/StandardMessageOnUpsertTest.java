package jimmutable.messaging;

import static org.junit.Assert.assertEquals;

import org.jimmutable.cloud.StubTest;
import org.jimmutable.cloud.messaging.StandardMessageOnUpsert;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.Format;
import org.junit.Test;


public class StandardMessageOnUpsertTest extends StubTest
{

	@Test
	public void testBuilder()
	{
		Builder builder = new Builder(StandardMessageOnUpsert.TYPE_NAME);
		
		builder.set(StandardMessageOnUpsert.FIELD_KIND, new Kind("foo"));
		builder.set(StandardMessageOnUpsert.FIELD_OBJECT_ID, new ObjectId(1));
		
		StandardMessageOnUpsert obj = (StandardMessageOnUpsert)builder.create(null);
		
		assertEquals(new StandardMessageOnUpsert(new Kind("foo"),new ObjectId(1)), obj);
		
		StandardMessageOnUpsert not_equal_1 = new StandardMessageOnUpsert(new Kind("bar"),new ObjectId(1));
		StandardMessageOnUpsert not_equal_2 = new StandardMessageOnUpsert(new Kind("foo"),new ObjectId(2));
		
		assert(!obj.equals(not_equal_1));
		assert(!obj.equals(not_equal_2));
		
		System.out.println(obj.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
	}
	
	@Test
	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.gcloud.StandardMessageOnUpsert\","
			     , "  \"kind\" : \"foo\","
			     , "  \"object_id\" : \"0000-0000-0000-0001\""
			     , "}"
			);

		StandardMessageOnUpsert obj = (StandardMessageOnUpsert)StandardObject.deserialize(obj_string);
		
		assertEquals(new StandardMessageOnUpsert(new Kind("foo"),new ObjectId(1)), obj);
	}
	
	@Test
	public void testSerializationXML() {
	
		/*
		 * This is not really necessary. Code already exists to verify the other (XML or JSON) if only one is tested
		 */
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n"
			     , "<?xml version=\'1.0\' encoding=\'UTF-8\'?><object>"
			     , "  <type_hint>jimmutable.gcloud.StandardMessageOnUpsert</type_hint>"
			     , "  <kind>foo</kind>"
			     , "  <object_id>0000-0000-0000-0001</object_id>"
			     , "</object>"
			);

			StandardMessageOnUpsert obj = (StandardMessageOnUpsert)StandardObject.deserialize(obj_string);
			
			assertEquals(new StandardMessageOnUpsert(new Kind("foo"),new ObjectId(1)), obj);
	}
}