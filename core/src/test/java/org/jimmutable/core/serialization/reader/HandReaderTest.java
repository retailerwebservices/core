package org.jimmutable.core.serialization.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Address;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.junit.Test;

public class HandReaderTest
{

	static final String json = "{\n" + "  \"nonsense\": \"\\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\\b\\t\\n\\u000b\\f\\r\\u000e\\u000f\\u0010\\u0011\\u0012\\u0013\\u0014\\u0015\\u0016\\u0017\\u0018\\u0019\\u001a\\u001b\\u001c\\u001d\\u001e\\u001f !\\\"#$%&'()*+,-.\\/0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\\u0080\\u0081\\u0082\\u0083\\u0084\\u0085\\u0086\\u0087\\u0088\\u0089\\u008a\\u008b\\u008c\\u008d\\u008e\\u008f\\u0090\\u0091\\u0092\\u0093\\u0094\\u0095\\u0096\\u0097\\u0098\\u0099\\u009a\\u009b\\u009c\\u009d\\u009e\\u009f\\u00a0\\u00a1\\u00a2\\u00a3\\u00a4\\u00a5\\u00a6\\u00a7\\u00a8\\u00a9\\u00aa\\u00ab\\u00ac\\u00ad\\u00ae\\u00af\\u00b0\\u00b1\\u00b2\\u00b3\\u00b4\\u00b5\\u00b6\\u00b7\\u00b8\\u00b9\\u00ba\\u00bb\\u00bc\\u00bd\\u00be\\u00bf\\u00c0\\u00c1\\u00c2\\u00c3\\u00c4\\u00c5\\u00c6\\u00c7\\u00c8\\u00c9\\u00ca\\u00cb\\u00cc\\u00cd\\u00ce\\u00cf\\u00d0\\u00d1\\u00d2\\u00d3\\u00d4\\u00d5\\u00d6\\u00d7\\u00d8\\u00d9\\u00da\\u00db\\u00dc\\u00dd\\u00de\\u00df\\u00e0\\u00e1\\u00e2\\u00e3\\u00e4\\u00e5\\u00e6\\u00e7\\u00e8\\u00e9\\u00ea\\u00eb\\u00ec\\u00ed\\u00ee\\u00ef\\u00f0\\u00f1\\u00f2\\u00f3\\u00f4\\u00f5\\u00f6\\u00f7\\u00f8\\u00f9\\u00fa\\u00fb\\u00fc\\u00fd\\u00fe\",\n" + "  \"person\": {\n" + "    \"firstname\": \"trevor\",\n" + "    \"lastname\": \"box\",\n" + "    \"hobby\": \"writing code\",\n" + "    \"char\": \"X\",\n" + "    \"monies\": [\n" + "      1.5,\n" + "      5.1\n" + "    ]\n" + "  },\n" + "  \"id\": 123,\n" + "  \"is_awesone\": true,\n" + "  \"favoritecolors\": [\n" + "    \"blue\",\n" + "    \"red\",\n" + "    \"moave\"\n" + "  ],\n" + "  \"favoritenumbers\": [\n" + "    1,\n" + "    2,\n" + "    3,\n" + "    4,\n" + "    555\n" + "  ]\n" + "}";

	@Test
	public void testAllReadTypes()
	{
		HandReader r = new HandReader(json);
		// String
		assertEquals("trevor", r.readString("person/firstname", null));
		// boolean
		assertEquals(true, r.readBoolean("is_awesone", null));
		// byte
		assertEquals((int) new Integer(1).byteValue(), (int) r.readByte("favoritenumbers", null));
		// char
		assertEquals(new Character('X'), r.readCharacter("person/char", null));
		// double
		assertEquals(new Double(2d), r.readDouble("favoritenumbers", null));
		// float
		assertEquals(new Float(1.5f), r.readFloat("person/monies", null));
		// int
		assertEquals(new Integer(3), r.readInt("favoritenumbers", null));
		// long
		assertEquals(new Long(4l), r.readLong("favoritenumbers", null));
		// short
		assertEquals(new Short((short) 555), r.readShort("favoritenumbers", null));
	}

	@Test
	public void testStandardObjectRead()
	{

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(Address.class);

		String json = "{\n" + "  \"address\": {\n" + "    \"type_hint\": \"jimmutable.common.Address\",\n" + "    \"name\": \"Bob Smith\",\n" + "    \"line1\": \"2713 Bridgeport Rd.\",\n" + "    \"line2\": \"test line 2\",\n" + "    \"line3\": \"test line 3\",\n" + "    \"city\": \"Milton\",\n" + "    \"state\": \"Ontario\",\n" + "    \"postal_code\": \"L9T 2Y2\",\n" + "    \"country\": \"CA\",\n" + "    \"latitude\": 2.0,\n" + "    \"longitude\": 5.0\n" + "  }\n" + "}";
		HandReader r = new HandReader(json);

		String obj_string = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s", "{", "  \"type_hint\" : \"jimmutable.common.Address\",", "  \"name\" : \"Bob Smith\",", "  \"line1\" : \"2713 Bridgeport Rd.\",", "  \"line2\" : \"test line 2\",", "  \"line3\" : \"test line 3\",", "  \"city\" : \"Milton\",", "  \"state\" : \"Ontario\",", "  \"postal_code\" : \"L9T 2Y2\",", "  \"country\" : \"CA\",", "  \"latitude\" : 2.0,", "  \"longitude\" : 5.0", "}");

		Address obj = (Address) StandardObject.deserialize(obj_string);

		assertEquals(obj, (Address) r.readObject("address", null));

		json = "{\n" + "  \"type_hint\" : \"jimmutable.common.Address\",\n" + "  \"name\" : \"Bob Smith\",\n" + "  \"line1\" : \"2713 Bridgeport Rd.\",\n" + "  \"line2\" : \"test line 2\",\n" + "  \"line3\" : \"test line 3\",\n" + "  \"city\" : \"Milton\",\n" + "  \"state\" : \"Ontario\",\n" + "  \"postal_code\" : \"L9T 2Y2\",\n" + "  \"country\" : \"CA\",\n" + "  \"latitude\" : 2.0,\n" + "  \"longitude\" : 5.0\n" + "}";

		r = new HandReader(json);
		assertEquals(obj, (Address) r.asObject(null));

		json = "{\n" + "  \"address\": {\n" + "    \"type_hint\": \"jimmutable.common.Address\",\n" + "    \"name\": \"Bob Smith\",\n" + "    \"line1\": \"2713 Bridgeport Rd.\",\n" + "    \"line2\": \"test line 2\",\n" + "    \"line3\": \"test line 3\",\n" + "    \"city\": \"Milton\",\n" + "    \"state\": \"Ontario\",\n" + "    \"postal_code\": \"L9T 2Y2\",\n" + "    \"country\": \"CA\",\n" + "    \"latitude\": 2.0,\n" + "    \"longitude\": 5.0\n" + "  }\n" + "}";
		r = new HandReader(json).read("address", null);

		assertEquals(obj, (Address) r.asObject(null));
	}

	@Test
	public void testNestedRead()
	{

		HandReader r = new HandReader(json);

		String str = r.readString("person/firstname", null);

		assertEquals("trevor", str);

		Float f = r.readFloat("person/monies", null);

		List<Float> my_monies = new LinkedList<Float>();

		while ( f != null )
		{
			my_monies.add(f);
			f = r.readFloat("person/monies", null);
		}

		assertTrue(my_monies.contains(1.5f));
		assertTrue(my_monies.contains(5.1f));

	}

	@Test
	public void testArrays()
	{
		HandReader r = new HandReader(json);

		List<Integer> my_ints = new LinkedList<Integer>();

		Integer i = r.readInt("favoritenumbers", null);

		while ( i != null )
		{
			my_ints.add(i);
			i = r.readInt("favoritenumbers", null);
		}

		assertTrue(my_ints.contains(1));
		assertTrue(my_ints.contains(2));
		assertTrue(my_ints.contains(3));
		assertTrue(my_ints.contains(4));
		assertTrue(my_ints.contains(555));
		assertTrue(my_ints.size() == 5);

		r = new HandReader(json);

		List<String> my_Strings = new LinkedList<String>();

		String s = r.readString("favoritecolors", null);

		while ( s != null )
		{
			my_Strings.add(s);
			s = r.readString("favoritecolors", null);
		}

		assertTrue(my_Strings.contains("red"));
		assertTrue(my_Strings.contains("blue"));
		assertTrue(my_Strings.contains("moave"));

	}

	@Test
	public void testSpecialCharacterNonSense()
	{

		HandReader r = new HandReader(json);

		StringBuilder sb = new StringBuilder();

		for ( int i = 0; i < 255; i++ )
		{
			sb.append((char) i);
		}

		assertEquals(sb.toString(), r.readString("nonsense", null));

	}

	@Test
	public void testNull()
	{

		HandReader r = new HandReader(json);

		assertEquals(null, r.readLong("nonsense", null));

	}

	@Test
	public void duplicateFieldsJson()
	{
		String json = "{\n" + "  \"nonsense\": \"\\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\\b\\t\\n\\u000b\\f\\r\\u000e\\u000f\\u0010\\u0011\\u0012\\u0013\\u0014\\u0015\\u0016\\u0017\\u0018\\u0019\\u001a\\u001b\\u001c\\u001d\\u001e\\u001f !\\\"#$%&'()*+,-.\\/0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\\u0080\\u0081\\u0082\\u0083\\u0084\\u0085\\u0086\\u0087\\u0088\\u0089\\u008a\\u008b\\u008c\\u008d\\u008e\\u008f\\u0090\\u0091\\u0092\\u0093\\u0094\\u0095\\u0096\\u0097\\u0098\\u0099\\u009a\\u009b\\u009c\\u009d\\u009e\\u009f\\u00a0\\u00a1\\u00a2\\u00a3\\u00a4\\u00a5\\u00a6\\u00a7\\u00a8\\u00a9\\u00aa\\u00ab\\u00ac\\u00ad\\u00ae\\u00af\\u00b0\\u00b1\\u00b2\\u00b3\\u00b4\\u00b5\\u00b6\\u00b7\\u00b8\\u00b9\\u00ba\\u00bb\\u00bc\\u00bd\\u00be\\u00bf\\u00c0\\u00c1\\u00c2\\u00c3\\u00c4\\u00c5\\u00c6\\u00c7\\u00c8\\u00c9\\u00ca\\u00cb\\u00cc\\u00cd\\u00ce\\u00cf\\u00d0\\u00d1\\u00d2\\u00d3\\u00d4\\u00d5\\u00d6\\u00d7\\u00d8\\u00d9\\u00da\\u00db\\u00dc\\u00dd\\u00de\\u00df\\u00e0\\u00e1\\u00e2\\u00e3\\u00e4\\u00e5\\u00e6\\u00e7\\u00e8\\u00e9\\u00ea\\u00eb\\u00ec\\u00ed\\u00ee\\u00ef\\u00f0\\u00f1\\u00f2\\u00f3\\u00f4\\u00f5\\u00f6\\u00f7\\u00f8\\u00f9\\u00fa\\u00fb\\u00fc\\u00fd\\u00fe\",\n" + "  \"person\": {\n" + "    \"firstname\": \"trevor2\",\n" + "    \"firstname\": \"trevor\",\n" + "    \"lastname\": \"box\",\n" + "    \"hobby\": \"writing code\",\n" + "    \"monies\": [\n" + "      1.5,\n" + "      5.1\n" + "    ]\n" + "  },\n" + "  \"id\": 123,\n" + "  \"is_awesone\": true,\n" + "  \"favoritecolors\": [\n" + "    \"blue\",\n" + "    \"red\",\n" + "    \"moave\"\n" + "  ],\n" + "  \"favoritenumbers\": [\n" + "    1,\n" + "    2,\n" + "    3,\n" + "    4,\n" + "    555\n" + "  ]\n" + "}";
		HandReader r = new HandReader(json);

		String str = r.readString("person/firstname", null);

		List<String> strings = new LinkedList<>();
		while ( str != null )
		{

			strings.add(str);
			str = r.readString("person/firstname", null);

		}

		assertEquals(2, strings.size());

	}

	@Test
	public void complex()
	{
		String json = "{\n" + "  \"medications\": [\n" + "    {\n" + "      \"aceinhibitors\": [\n" + "        {\n" + "          \"name\": \"lisinopril\",\n" + "          \"strength\": \"10 mg Tab\",\n" + "          \"dose\": \"1 tab\",\n" + "          \"route\": \"PO\",\n" + "          \"sig\": \"daily\",\n" + "          \"pillcount\": \"#90\",\n" + "          \"refills\": \"Refill 3\"\n" + "        },\n" + "        {\n" + "          \"name\": \"foggle\",\n" + "          \"strength\": \"10 mg Tab\",\n" + "          \"dose\": \"1 tab\",\n" + "          \"route\": \"PO\",\n" + "          \"sig\": \"daily\",\n" + "          \"pillcount\": \"#90\",\n" + "          \"refills\": \"Refill 3\"\n" + "        }\n" + "      ]\n" + "    }\n" + "  ]\n" + "}";

		HandReader root = new HandReader(json);

		// System.out.println(root.toString());

		List<String> aceInhibitorNames = new LinkedList<>();

		while ( true )
		{
			HandReader medications = root.read("medications", null);
			if ( medications == null )
			{
				break;
			}
			// System.out.println(medications.toString());
			while ( true )
			{
				HandReader aceinhibitors = medications.read("aceinhibitors", null);

				if ( aceinhibitors == null )
				{
					break;
				}

				while ( true )
				{
					String str = aceinhibitors.readString("name", null);

					if ( str == null )
					{
						break;
					}

					aceInhibitorNames.add(str);

				}

			}

		}

		assertEquals(2, aceInhibitorNames.size());
		assertTrue(aceInhibitorNames.contains("lisinopril"));
		assertTrue(aceInhibitorNames.contains("foggle"));

	}

	@Test(expected = SerializeException.class)
	public void badJSON()
	{
		String json = "{\n" + "  \"medications\": [\n" + "    \n" + "      \"aceinhibitors\": [\n" + "        {\n" + "          \"name\": \"lisinopril\",\n" + "          \"strength\": \"10 mg Tab\",\n" + "          \"dose\": \"1 tab\",\n" + "          \"route\": \"PO\",\n" + "          \"sig\": \"daily\",\n" + "          \"pillcount\": \"#90\",\n" + "          \"refills\": \"Refill 3\"\n" + "        },\n" + "        {\n" + "          \"name\": \"foggle\",\n" + "          \"strength\": \"10 mg Tab\",\n" + "          \"dose\": \"1 tab\",\n" + "          \"route\": \"PO\",\n" + "          \"sig\": \"daily\",\n" + "          \"pillcount\": \"#90\",\n" + "          \"refills\": \"Refill 3\"\n" + "        }\n" + "      ]\n" + "    }\n" + "  ]\n" + "}";
		new HandReader(json);
	}

}
