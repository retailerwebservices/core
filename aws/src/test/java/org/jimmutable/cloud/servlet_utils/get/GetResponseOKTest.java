package org.jimmutable.cloud.servlet_utils.get;

import org.jimmutable.cloud.JimmutableCloudTypeNameRegister;
import org.jimmutable.cloud.servlet_utils.get.GetResponseOK;
import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.reader.ObjectParseTree;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GetResponseOKTest extends TestCase
{
	public GetResponseOKTest( String testName )
	{
		super(testName);
	}

	public static Test suite()
	{
		JimmutableTypeNameRegister.registerAllTypes();
//		ObjectParseTree.registerTypeName(GetResponseOK.class);
		JimmutableCloudTypeNameRegister.registerAllTypes();
		return new TestSuite(GetResponseOKTest.class);
	}

	public void testGetResponseOKTest()
	{
		GetResponseOK result = null;
		try
		{
			result = new GetResponseOK(new Book("test title", 50, "100", BindingType.HARD_COVER, "test author"));
			System.out.println(result.toJavaCode(Format.JSON_PRETTY_PRINT, "obj"));
		} catch ( Exception e )
		{
			assert (false);
		}

		assert (result.getSimpleHTTPResponseCode() == GetResponseOK.HTTP_STATUS_CODE_OK);
		assert (result.getSimpleObject() != null);

		StandardImmutableObject<Book> data_object = new Book("test title", 50, "100", BindingType.HARD_COVER, "test author");
		result = new GetResponseOK(data_object);
		assert (result.getSimpleObject() == data_object);
	}

	public void testSerialization()
	{
		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s"
			     , "{"
			     , "  \"type_hint\" : \"jimmutable.aws.servlet_utils.get.GetResponseOK\","
			     , "  \"object\" : {"
			     , "    \"type_hint\" : \"jimmutable.examples.Book\","
			     , "    \"title\" : \"TEST TITLE\","
			     , "    \"page_count\" : 50,"
			     , "    \"isbn\" : \"100\","
			     , "    \"binding\" : \"hard-cover\","
			     , "    \"authors\" : [ \"test author\" ]"
			     , "  }"
			     , "}"
			);

		GetResponseOK obj = (GetResponseOK)StandardObject.deserialize(obj_string);

		Book requiredObject = (Book) obj.getSimpleObject();
		assert (requiredObject.getSimpleTitle().equals("TEST TITLE"));
	}
}
