package org.jimmutable.core.serialization.reader;

/**
 * The ReadAs system allows code to specify the type information while reading
 * collections and maps from previously serialized data.
 * 
 * Where does this problem come up? Well, imagine that we are reading a List
 * from JSON:
 * 
 * my_list : [ 1, 2, 3 ]
 * 
 * Is the contents of my_list bytes, integers, longs? You can't tell by just
 * looking at the data. You need to say something like, read my_list *as*
 * Integers. To do this you use ReadAs.INTEGER (easy, right?)
 * 
 * @author jim.kane
 *
 */
abstract public class ReadAs 
{
	static public ReadAs OBJECT = new ReadAsObject();
	static public ReadAs STRING = new ReadAsString();
	static public ReadAs BOOELAN = new ReadAsBoolean();
	static public ReadAs CHARACTER = new ReadAsCharacter();
	static public ReadAs BYTE = new ReadAsByte();
	static public ReadAs SHORT = new ReadAsShort();
	static public ReadAs INTEGER = new ReadAsInt();
	static public ReadAs LONG = new ReadAsLong();
	static public ReadAs FLOAT = new ReadAsFloat();
	static public ReadAs DOUBLE = new ReadAsDouble();
	
	/**
	 * Read a read tree (t) and convert into a given type
	 * 
	 * @param t
	 *            The read tree to construct the object from
	 * @return A value in a uniform type, or null if the read tree t could not
	 *         be converted into the target type.
	 */
	abstract public Object readAs(ObjectParseTree t);
	
	
	static private class ReadAsObject extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asObject(null);
		}
		
	}
	
	static private class ReadAsString extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asString(null);
		}
	}
	
	static private class ReadAsBoolean extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asBoolean(null);
		}
	}
	
	static private class ReadAsCharacter extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asCharacter(null);
		}
	}
	
	static private class ReadAsByte extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asByte(null);
		}
	}
	
	static private class ReadAsShort extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asShort(null);
		}
	}
	
	static private class ReadAsInt extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asInteger(null);
		}
	}
	
	static private class ReadAsLong extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asLong(null);
		}
	}
	
	static private class ReadAsFloat extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asFloat(null);
		}
	}
	
	static private class ReadAsDouble extends ReadAs
	{
		public Object readAs(ObjectParseTree t) 
		{
			return t.asDouble(null);
		}
	}
}
