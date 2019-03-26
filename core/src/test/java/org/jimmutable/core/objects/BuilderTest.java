package org.jimmutable.core.objects;

import java.util.Objects;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.examples.product_data.BrandCode;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.TestingUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BuilderTest extends TestCase
{
	static public class TestObject extends StandardImmutableObject
	{
		static public final TypeName TYPE_NAME = new TypeName("jimmutable.test.BuilderTestObject");

		public TypeName getTypeName()
		{
			return TYPE_NAME;
		}

		static public final FieldDefinition.String FIELD_MY_STRING = new FieldDefinition.String("my_string", null);

		static public final FieldDefinition.Character FIELD_MY_CHAR = new FieldDefinition.Character("my_char", (char) 0);
		static public final FieldDefinition.Boolean FIELD_MY_BOOLEAN = new FieldDefinition.Boolean("my_boolean", false);
		static public final FieldDefinition.Byte FIELD_MY_BYTE = new FieldDefinition.Byte("my_byte", (byte) 0);
		static public final FieldDefinition.Short FIELD_MY_SHORT = new FieldDefinition.Short("my_short", (short) 0);
		static public final FieldDefinition.Integer FIELD_MY_INT = new FieldDefinition.Integer("my_int", 0);
		static public final FieldDefinition.Long FIELD_MY_LONG = new FieldDefinition.Long("my_long", 0l);
		static public final FieldDefinition.Float FIELD_MY_FLOAT = new FieldDefinition.Float("my_float", 0.0f);
		static public final FieldDefinition.Double FIELD_MY_DOUBLE = new FieldDefinition.Double("my_double", 0.0);

		static public final FieldDefinition.Collection FIELD_MY_LIST_OF_STRINGS = new FieldDefinition.Collection("my_list_of_strings", new FieldArrayList());

		static public final FieldDefinition.StandardObject FIELD_MY_BOOK = new FieldDefinition.StandardObject("my_book", null);

		static public final FieldDefinition.Map FIELD_MY_STRING_INT_MAP = new FieldDefinition.Map("my_string_int_map", new FieldHashMap());

		static public final FieldDefinition.Map FIELD_MY_INT_BOOK_MAP = new FieldDefinition.Map("my_int_book_map", new FieldHashMap());

		static public final FieldDefinition.Enum<BindingType> FIELD_MY_ENUM = new FieldDefinition.Enum<BindingType>("my_enum", null, BindingType.CONVERTER);

		static public final FieldDefinition.Stringable<BrandCode> FIELD_MY_STRINGABLE = new FieldDefinition.Stringable("my_stringable", null, BrandCode.CONVERTER);

		private String my_string;

		private char my_char;
		private boolean my_boolean;
		private byte my_byte;
		private short my_short;
		private int my_int;
		private long my_long;
		private float my_float;
		private double my_double;

		private FieldList<String> my_list_of_strings;

		private FieldMap<String, Integer> my_string_int_map;
		private FieldMap<Integer, Book> my_int_book_map;

		private Book my_book;

		private BindingType my_enum;
		private BrandCode my_stringable;

		public TestObject()
		{
			my_list_of_strings = new FieldArrayList();
			my_string_int_map = new FieldHashMap();
		}

		public TestObject( ObjectParseTree t )
		{
			my_string = t.getString(FIELD_MY_STRING);

			my_char = t.getCharacter(FIELD_MY_CHAR);
			my_boolean = t.getBoolean(FIELD_MY_BOOLEAN);
			my_byte = t.getByte(FIELD_MY_BYTE);
			my_short = t.getShort(FIELD_MY_SHORT);
			my_int = t.getInt(FIELD_MY_INT);
			my_long = t.getLong(FIELD_MY_LONG);
			my_float = t.getFloat(FIELD_MY_FLOAT);
			my_double = t.getDouble(FIELD_MY_DOUBLE);

			my_list_of_strings = t.getCollection(FIELD_MY_LIST_OF_STRINGS, new FieldArrayList(), ReadAs.STRING, OnError.SKIP);

			my_book = (Book) t.getObject(FIELD_MY_BOOK);

			my_string_int_map = t.getMap(FIELD_MY_STRING_INT_MAP, new FieldHashMap(), ReadAs.STRING, ReadAs.INTEGER, OnError.SKIP);
			my_int_book_map = t.getMap(FIELD_MY_INT_BOOK_MAP, new FieldHashMap(), ReadAs.INTEGER, ReadAs.OBJECT, OnError.SKIP);

			my_enum = t.getEnum(FIELD_MY_ENUM);

			my_stringable = t.getStringable(FIELD_MY_STRINGABLE);
		}

		public void write( ObjectWriter writer )
		{
			writer.writeString(FIELD_MY_STRING, my_string);

			writer.writeChar(FIELD_MY_CHAR, my_char);
			writer.writeBoolean(FIELD_MY_BOOLEAN, my_boolean);
			writer.writeByte(FIELD_MY_BYTE, my_byte);
			writer.writeShort(FIELD_MY_SHORT, my_short);
			writer.writeInt(FIELD_MY_INT, my_int);
			writer.writeLong(FIELD_MY_LONG, my_long);
			writer.writeFloat(FIELD_MY_FLOAT, my_float);
			writer.writeDouble(FIELD_MY_DOUBLE, my_double);

			writer.writeCollection(FIELD_MY_LIST_OF_STRINGS, my_list_of_strings, WriteAs.STRING);

			writer.writeObject(FIELD_MY_BOOK, my_book);
			writer.writeMap(FIELD_MY_INT_BOOK_MAP, my_int_book_map, WriteAs.NUMBER, WriteAs.OBJECT);

			writer.writeEnum(FIELD_MY_ENUM, my_enum);

			writer.writeStringable(FIELD_MY_STRINGABLE, my_stringable);
		}

		public int compareTo( Object o )
		{
			throw new UnsupportedOperationException();
		}

		public void freeze()
		{
			my_list_of_strings.freeze();
		}

		public void normalize()
		{
		}

		public void validate()
		{
		}

		public int hashCode()
		{
			return Objects.hash(my_string, my_boolean, my_byte, my_short, my_int, my_long, my_float, my_double, my_list_of_strings);
		}

		public boolean equals( Object obj )
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public BuilderTest( String testName )
	{
		super(testName);

		JimmutableTypeNameRegister.registerAllTypes();
		ObjectParseTree.registerTypeName(BuilderTest.TestObject.class);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite(BuilderTest.class);
	}

	public void testListOfStrings()
	{
		Builder builder;
		TestObject obj;

		// Empty list
		{
			builder = new Builder(TestObject.TYPE_NAME);
			obj = (TestObject) builder.create(null);

			assertTrue(obj != null);
			assertEquals(obj.my_list_of_strings.size(), 0);
		}

		// One element
		{
			builder = new Builder(TestObject.TYPE_NAME);
			builder.add(TestObject.FIELD_MY_LIST_OF_STRINGS, "foo");

			obj = (TestObject) builder.create(null);

			assertTrue(obj != null);
			assertEquals(obj.my_list_of_strings.size(), 1);
			assertEquals(obj.my_list_of_strings.get(0), "foo");
		}

		// Multiple element(s)
		{
			builder = new Builder(TestObject.TYPE_NAME);
			builder.add(TestObject.FIELD_MY_LIST_OF_STRINGS, "foo");
			builder.add(TestObject.FIELD_MY_LIST_OF_STRINGS, "foo");
			builder.add(TestObject.FIELD_MY_LIST_OF_STRINGS, "bar");

			obj = (TestObject) builder.create(null);

			assertTrue(obj != null);
			assertEquals(obj.my_list_of_strings.size(), 3);
			assertEquals(obj.my_list_of_strings.get(0), "foo");
			assertEquals(obj.my_list_of_strings.get(1), "foo");
			assertEquals(obj.my_list_of_strings.get(2), "bar");
		}

		// Null test
		{
			builder = new Builder(TestObject.TYPE_NAME);
			builder.add(TestObject.FIELD_MY_LIST_OF_STRINGS, "foo");
			builder.add(TestObject.FIELD_MY_LIST_OF_STRINGS, (String) null);
			builder.add(TestObject.FIELD_MY_LIST_OF_STRINGS, "bar");

			obj = (TestObject) builder.create(null);

			assertTrue(obj != null);
			assertEquals(obj.my_list_of_strings.size(), 2);
			assertEquals(obj.my_list_of_strings.get(0), "foo");
			assertEquals(obj.my_list_of_strings.get(1), "bar");
		}
	}

	public void testModifyExistingObject()
	{
		// test a builder's create a modified version of an existing object
		// functionality...

		Builder builder;

		builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_FLOAT, 3.14159f);

		TestObject first = (TestObject) builder.create(null);

		assertTrue(first != null);
		assertEquals(first.my_float, 3.14159f);

		builder = new Builder(first);
		builder.set(TestObject.FIELD_MY_STRING, "foo");

		TestObject second = (TestObject) builder.create(null);
		assertTrue(second != null);

		assertEquals(second.my_float, 3.14159f);
		assertEquals(second.my_string, "foo");
	}

	public void testString()
	{
		testOneString("foo");
		testOneString(null);
		testOneString(TestingUtils.createAcidString());
		testOneString(TestingUtils.createNonBase64AcidString());
	}

	public void testEnum()
	{
		testOneEnum(null);
		testOneEnum(BindingType.HARD_COVER);
		testOneEnum(BindingType.PAPER_BACK);
		testOneEnum(BindingType.TRADE_PAPER_BACK);
		testOneEnum(BindingType.UNKNOWN);
	}

	public void testStringable()
	{
		testOneStringable(null);
		testOneStringable(new BrandCode("GE"));
		testOneStringable(new BrandCode("FRIG"));
	}

	public void testDouble()
	{
		testOneDouble(0.2);
		testOneDouble(Double.NaN);
		testOneDouble(Double.MAX_VALUE);
		testOneDouble(Double.MIN_VALUE);
		testOneDouble(Double.NEGATIVE_INFINITY);
		testOneDouble(Double.POSITIVE_INFINITY);
	}

	public void testFloat()
	{
		testOneFloat(0.2f);
		testOneFloat(Float.NaN);
		testOneFloat(Float.MAX_VALUE);
		testOneFloat(Float.MIN_VALUE);
		testOneFloat(Float.NEGATIVE_INFINITY);
		testOneFloat(Float.POSITIVE_INFINITY);
	}

	public void testLong()
	{
		testOneLong(17);
		testOneLong(0);
		testOneLong(Long.MAX_VALUE);
		testOneLong(Long.MIN_VALUE);
	}

	public void testInt()
	{
		testOneInt(17);
		testOneInt(0);
		testOneInt(Integer.MAX_VALUE);
		testOneInt(Integer.MIN_VALUE);
	}

	public void testShort()
	{
		testOneShort((short) 17);
		testOneShort((short) 0);
		testOneShort(Short.MAX_VALUE);
		testOneShort(Short.MIN_VALUE);
	}

	public void testByte()
	{
		testOneByte((byte) 17);
		testOneByte((byte) 0);
		testOneByte(Byte.MAX_VALUE);
		testOneByte(Byte.MIN_VALUE);
	}

	public void testChar()
	{
		testOneChar((char) 17);
		testOneChar((char) 0);
		testOneChar('a');
		testOneChar('Z');
		testOneChar('\n');
		testOneChar(Character.MAX_VALUE);
		testOneChar(Character.MIN_VALUE);

		// really pound on it...
		String str = TestingUtils.createAcidString();

		for ( char ch : str.toCharArray() )
		{
			testOneChar(ch);
		}
	}

	public void testBoolean()
	{
		testOneBoolean(true);
		testOneBoolean(false);
	}

	public void testBook()
	{
		testOneBook(null);

		{
			Builder builder = new Builder(Book.TYPE_NAME);
			builder.set(Book.FIELD_TITLE, "Of Mice and Men");
			builder.add(Book.FIELD_AUTHORS, "John Steinbeck");
			builder.set(Book.FIELD_BINDING, BindingType.TRADE_PAPER_BACK);
			builder.set(Book.FIELD_ISBN, "0139438452");
			builder.set(Book.FIELD_PAGE_COUNT, 821);

			Book book = (Book) builder.create(null);

			assertTrue(book != null);

			testOneBook(book);
		}
	}

	public void testStringIntMap()
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);

		builder.addMapEntry(TestObject.FIELD_MY_STRING_INT_MAP, "foo", 2);
		builder.addMapEntry(TestObject.FIELD_MY_STRING_INT_MAP, "bar", 17);
		builder.addMapEntry(TestObject.FIELD_MY_STRING_INT_MAP, "foo", 3);
		builder.addMapEntry(TestObject.FIELD_MY_STRING_INT_MAP, null, 3);
		builder.addMapEntry(TestObject.FIELD_MY_STRING_INT_MAP, "baz", null);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);

		assertEquals(obj.my_string_int_map.size(), 2);

		assertTrue(obj.my_string_int_map.containsKey("foo"));
		assertTrue(obj.my_string_int_map.containsKey("bar"));

		assertEquals(obj.my_string_int_map.get("foo"), (Integer) 3);
		assertEquals(obj.my_string_int_map.get("bar"), (Integer) 17);
	}

	public void testIntBookMap()
	{
		testOneBook(null);

		Book of_mice_and_men, the_great_divorce, the_screwtape_letters;

		{
			Builder builder = new Builder(Book.TYPE_NAME);
			builder.set(Book.FIELD_TITLE, "Of Mice and Men");
			builder.add(Book.FIELD_AUTHORS, "John Steinbeck");
			builder.set(Book.FIELD_BINDING, BindingType.TRADE_PAPER_BACK);
			builder.set(Book.FIELD_ISBN, "0139438452");
			builder.set(Book.FIELD_PAGE_COUNT, 821);

			of_mice_and_men = (Book) builder.create(null);

			assertTrue(of_mice_and_men != null);
		}

		{
			Builder builder = new Builder(Book.TYPE_NAME);
			builder.set(Book.FIELD_TITLE, "The Great Divorce");
			builder.add(Book.FIELD_AUTHORS, "C.S. Lewis");
			builder.set(Book.FIELD_BINDING, BindingType.TRADE_PAPER_BACK);
			builder.set(Book.FIELD_ISBN, "274645102");
			builder.set(Book.FIELD_PAGE_COUNT, 261);

			the_great_divorce = (Book) builder.create(null);

			assertTrue(the_great_divorce != null);
		}

		{
			Builder builder = new Builder(Book.TYPE_NAME);
			builder.set(Book.FIELD_TITLE, "The Screwtape Letters");
			builder.add(Book.FIELD_AUTHORS, "C.S. Lewis");
			builder.set(Book.FIELD_BINDING, BindingType.TRADE_PAPER_BACK);
			builder.set(Book.FIELD_ISBN, "174645102");
			builder.set(Book.FIELD_PAGE_COUNT, 601);

			the_screwtape_letters = (Book) builder.create(null);

			assertTrue(the_screwtape_letters != null);
		}

		Builder builder = new Builder(TestObject.TYPE_NAME);

		builder.addMapEntry(TestObject.FIELD_MY_INT_BOOK_MAP, 17, of_mice_and_men);

		// Verify that it works like a "put"
		builder.addMapEntry(TestObject.FIELD_MY_INT_BOOK_MAP, 32, the_great_divorce);
		builder.addMapEntry(TestObject.FIELD_MY_INT_BOOK_MAP, 32, the_screwtape_letters);

		// Entries with a null should be skipped
		builder.addMapEntry(TestObject.FIELD_MY_INT_BOOK_MAP, null, the_screwtape_letters);
		builder.addMapEntry(TestObject.FIELD_MY_INT_BOOK_MAP, 48, null);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);

		assertEquals(obj.my_int_book_map.size(), 2);

		assertTrue(obj.my_int_book_map.containsKey(17));
		assertTrue(obj.my_int_book_map.containsKey(32));

		assertEquals(obj.my_int_book_map.get(17), of_mice_and_men);
		assertEquals(obj.my_int_book_map.get(32), the_screwtape_letters);
	}

	public void testOneString( String value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_STRING, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_string, value);
	}

	public void testOneEnum( BindingType value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_ENUM, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_enum, value);
	}

	public void testOneStringable( BrandCode value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_STRINGABLE, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_stringable, value);
	}

	public void testOneDouble( double value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_DOUBLE, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_double, value);
	}

	public void testOneFloat( float value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_FLOAT, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_float, value);
	}

	public void testOneLong( long value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_LONG, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_long, value);
	}

	public void testOneInt( int value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_INT, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_int, value);
	}

	public void testOneShort( short value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_SHORT, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_short, value);
	}

	public void testOneByte( byte value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_BYTE, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_byte, value);
	}

	public void testOneChar( char value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_CHAR, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_char, value);
	}

	public void testOneBoolean( boolean value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_BOOLEAN, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_boolean, value);
	}

	public void testOneBook( Book value )
	{
		Builder builder = new Builder(TestObject.TYPE_NAME);
		builder.set(TestObject.FIELD_MY_BOOK, value);

		TestObject obj = (TestObject) builder.create(null);

		assertTrue(obj != null);
		assertEquals(obj.my_book, value);
	}
}