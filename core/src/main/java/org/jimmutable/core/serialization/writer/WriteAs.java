package org.jimmutable.core.serialization.writer;

import java.util.Map;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.utils.Validator;

/**
 * A "WriteAs" in an instruction write an Object in a certain way
 * 
 * For example, imagine that you are serializing a collection, List&lt;Float&gt; foo.
 * Do you want to write:
 * 
 * foo : [0.2, 1.7, 0.8]
 * 
 * or
 * 
 * foo : ["0.2", "1.7", "0.8"]
 * 
 * or
 * 
 * foo : [ { type_hint: "float", primitive_value: 0.2 }, { type_hint: "float",
 * primitive_value: 1.7 }, { type_hint: "float", primitive_value: 0.8 } ]
 * 
 * (The above are WriteAs.NUMBER, WriteAs.STRING, and WriteAs.OBJECT,
 * respectively)
 * 
 * The issue of the write as type comes to the for when trying to make the
 * output easy to work with in JavaScript, and when dealing with Collections and
 * Maps of mixed primitive types.
 * 
 * @author jim.kane
 *
 */
abstract public class WriteAs 
{
	/**
	 * Write as an object (complete object form, with type_hint etc.)
	 * 
	 * This is the WriteAs to use for StandardObject(s) and, by extension,
	 * StandardImmutableObject(s)
	 */
	static public final WriteAs OBJECT = new WriteAsObject();
	
	/**
	 * Write a string (primitive, when possible)
	 */
	static public final WriteAs STRING = new WriteAsString();
	
	/**
	 * Write as number (will be read by JavaScript code as a Number)
	 */
	static public final WriteAs NUMBER = new WriteAsNumber();
	
	/**
	 * Write as a boolean (will be read by JavaScritp code as a boolean)
	 */
	static public final WriteAs BOOLEAN = new WriteAsBoolean();

	/**
	 * Write an object in the proper format
	 * 
	 * @param writer The writer to write obj to
	 * @param field_name The field name to write obj to
	 * @param obj The object to write
	 */
	abstract public void writeObject(ObjectWriter writer, FieldName field_name, Object obj);
	
	static private class WriteAsObject extends WriteAs
	{
		public void writeObject(ObjectWriter writer, FieldName field_name, Object obj)
		{
			writer.writeObject(field_name, obj);
		}
	}
	
	static private class WriteAsString extends WriteAs
	{
		public void writeObject(ObjectWriter writer, FieldName field_name, Object obj)
		{
			if ( obj == null ) 
			{
				writer.writeNull(field_name);
				return;
			}
			
			if ( obj instanceof Stringable ) obj = obj.toString();
			
			if ( obj instanceof StandardObject ) throw new SerializeException("Attempt to write a standard object as a string");
			if ( obj instanceof Map.Entry ) throw new SerializeException("Attempt to write a map entry as a string");
			
			writer.writeString(field_name, obj.toString());
		}
	}
	
	static private class WriteAsNumber extends WriteAs
	{
		public void writeObject(ObjectWriter writer, FieldName field_name, Object obj)
		{
			if ( obj == null ) throw new SerializeException("Attempt to write null as a number");
			
			if ( obj instanceof Boolean ) { writer.writeInt(field_name, ((Boolean)obj).booleanValue() ? 1 : 0); return; }
			if ( obj instanceof Character ) { writer.writeInt(field_name, (int)((Character)obj).charValue()); return; }
			if ( obj instanceof Byte ) { writer.writeByte(field_name, (Byte)obj); return; }
			if ( obj instanceof Short ) { writer.writeShort(field_name, (Short)obj); return; }
			if ( obj instanceof Integer ) { writer.writeInt(field_name, (Integer)obj); return; }
			if ( obj instanceof Long ) { writer.writeLong(field_name, (Long)obj); return; }
			if ( obj instanceof Float ) { writer.writeFloat(field_name, (Float)obj); return; }
			if ( obj instanceof Double ) { writer.writeDouble(field_name, (Double)obj); return; }
			
			throw new SerializeException(String.format("Unable to write %s as a number", obj.getClass().getSimpleName()));
		}
	}
	
	static private class WriteAsBoolean extends WriteAs
	{
		public void writeObject(ObjectWriter writer, FieldName field_name, Object obj)
		{
			if ( obj == null ) { writer.writeBoolean(field_name, false); }
			
			if ( obj instanceof Boolean ) { writer.writeBoolean(field_name, (Boolean)obj); return;}
			
			if ( obj instanceof Byte ) { writer.writeBoolean(field_name, (Byte)obj != 0); return ;}
			if ( obj instanceof Short ) { writer.writeBoolean(field_name, (Short)obj != 0); return;}
			if ( obj instanceof Integer ) { writer.writeBoolean(field_name, (Integer)obj != 0); return;}
			if ( obj instanceof Long ) { writer.writeBoolean(field_name, (Long)obj != 0); return;}
			
			
			throw new SerializeException(String.format("Unable to write %s as a boolean", obj.getClass().getSimpleName()));
		}
	}
	
	static public class MapWriteAs extends WriteAs
	{
		private WriteAs key_type;
		private WriteAs value_type;
		
		public MapWriteAs(WriteAs key_type, WriteAs value_type)
		{
			Validator.notNull(key_type, value_type);
			
			this.key_type = key_type;
			this.value_type = value_type;
		}
		
		public void writeObject(ObjectWriter writer, FieldName field_name, Object obj)
		{
			if ( !(obj instanceof Map.Entry) ) return;
			
			writer.openObject(field_name, TypeName.TYPE_NAME_MAP_ENTRY);
			{
				Map.Entry entry = (Map.Entry)obj;
				
				key_type.writeObject(writer, FieldName.FIELD_KEY, entry.getKey());
				value_type.writeObject(writer, FieldName.FIELD_VALUE, entry.getValue());
			
				writer.closeObject();
			}
		}
	}
	
}
