package org.jimmutable.core.objects;

import java.util.Map;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.Parser;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Validator;

import com.fasterxml.jackson.databind.util.TokenBuffer;

public class Builder 
{
	private ObjectParseTree under_construction;
	
	public Builder(TypeName type_name)
	{
		Validator.notNull(type_name);
		
		under_construction = new ObjectParseTree(FieldName.FIELD_DOCUMENT_ROOT);
		
		ObjectParseTree type_hint = new ObjectParseTree(FieldName.FIELD_NAME_TYPE_HINT);
		type_hint.setValue(type_name.getSimpleName());
		
		under_construction.add(type_hint);
	}
	
	public Builder(StandardObject src)
	{
		Validator.notNull(src);
		TokenBuffer token_buffer = ObjectWriter.serializeToTokenBuffer(src);
		
		under_construction = Parser.parse(token_buffer);
	}
	
	public void unset(FieldDefinition field)
	{
		under_construction.removeAll(field.getSimpleFieldName());
	}
	
	public void set(FieldDefinition.Boolean field, boolean value) { insertNonNullPrimative(field, Boolean.toString(value),true); }
	public void set(FieldDefinition.Character field, char value) { insertNonNullPrimative(field,Character.toString(value),true); }
	public void set(FieldDefinition.Byte field, byte value) { insertNonNullPrimative(field,Byte.toString(value),true); }
	public void set(FieldDefinition.Short field, short value) { insertNonNullPrimative(field,Short.toString(value),true); }
	public void set(FieldDefinition.Integer field, int value) { insertNonNullPrimative(field,Integer.toString(value),true); }
	public void set(FieldDefinition.Long field, long value) { insertNonNullPrimative(field,Long.toString(value),true); }
	public void set(FieldDefinition.Float field, float value) { insertNonNullPrimative(field,Float.toString(value),true); }
	public void set(FieldDefinition.Double field, double value) { insertNonNullPrimative(field,Double.toString(value),true); }
	
	private void insertNonNullPrimative(FieldDefinition field, String primative_value_as_string, boolean is_set)
	{
		Validator.notNull(field, primative_value_as_string);
		
		ObjectParseTree new_child = new ObjectParseTree(field.getSimpleFieldName());
		new_child.setValue(primative_value_as_string);
		
		if ( is_set )
		{
			under_construction.setOrAdd(new_child);
		}
		else
		{
			under_construction.add(new_child);
		}
	}
	
	
	public void set(FieldDefinition.String field, String value)
	{
		Validator.notNull(field);
		
		if ( value == null )
		{
			under_construction.removeAll(field.getSimpleFieldName());
			return;
		}
		
		insertNonNullPrimative(field, value,true);
	}
	
	public <E extends StandardEnum> void set(FieldDefinition.Enum<E> field, E value)
	{
		Validator.notNull(field);
		
		if ( value == null )
		{
			under_construction.removeAll(field.getSimpleFieldName());
			return;
		}
		
		insertNonNullPrimative(field, value.getSimpleCode(), true);
	}
	
	public <S extends Stringable> void set(FieldDefinition.Stringable<S> field, S value)
	{
		Validator.notNull(field);
		
		if ( value == null )
		{
			under_construction.removeAll(field.getSimpleFieldName());
			return;
		}
		
		insertNonNullPrimative(field, value.getSimpleValue(), true);
	}
	
	public void set(FieldDefinition.StandardObject field, StandardObject value)
	{
		Validator.notNull(field);
		
		if ( value == null )
		{
			under_construction.removeAll(field.getSimpleFieldName());
			return;
		}
		
		TokenBuffer token_buffer = ObjectWriter.serializeToTokenBuffer(value);
		
		ObjectParseTree new_child = Parser.parse(token_buffer);
		new_child.setFieldName(field.getSimpleFieldName());
		
		under_construction.setOrAdd(new_child);
	}
	
	public void printUnderConstruction()
	{
		System.out.println(under_construction.toString());
	}
	
	public <U extends StandardObject> U create(U default_value)
	{
		return (U) under_construction.asObject(default_value);
	}
	
	public void add(FieldDefinition.Collection field, boolean value) { insertNonNullPrimative(field, Boolean.toString(value),false); }
	public void add(FieldDefinition.Collection field, char value) { insertNonNullPrimative(field,Character.toString(value),false); }
	public void add(FieldDefinition.Collection field, byte value) { insertNonNullPrimative(field,Byte.toString(value),false); }
	public void add(FieldDefinition.Collection field, short value) { insertNonNullPrimative(field,Short.toString(value),false); }
	public void add(FieldDefinition.Collection field, int value) { insertNonNullPrimative(field,Integer.toString(value),false); }
	public void add(FieldDefinition.Collection field, long value) { insertNonNullPrimative(field,Long.toString(value),false); }
	public void add(FieldDefinition.Collection field, float value) { insertNonNullPrimative(field,Float.toString(value),false); }
	public void add(FieldDefinition.Collection field, double value) { insertNonNullPrimative(field,Double.toString(value),false); }
	
	public void add(FieldDefinition.Collection field, String value)
	{
		Validator.notNull(field);
		
		if ( value == null ) return;
		
		insertNonNullPrimative(field, value,false);
	}
	
	public void add(FieldDefinition.Collection field, Stringable value)
	{
		Validator.notNull(field);
		
		if ( value == null ) return;
		
		insertNonNullPrimative(field, value.getSimpleValue(),false);
	}
	
	public void add(FieldDefinition.Collection field, StandardObject value)
	{
		Validator.notNull(field);
		
		if ( value == null ) return;
		
		TokenBuffer token_buffer = ObjectWriter.serializeToTokenBuffer(value);
		
		ObjectParseTree new_child = Parser.parse(token_buffer);
		new_child.setFieldName(field.getSimpleFieldName());
		
		under_construction.add(new_child);
	}
	
	public void add(FieldDefinition.Collection field, StandardEnum value)
	{
		Validator.notNull(field);
		
		if ( value == null ) return;
		
		TokenBuffer token_buffer = ObjectWriter.serializeToTokenBuffer(value.toString());
		
		ObjectParseTree new_child = Parser.parse(token_buffer);
		new_child.setFieldName(field.getSimpleFieldName());
		
		under_construction.add(new_child);
	}
	
	private ObjectParseTree mapKeyOrValueToObjectParseTree(FieldName field_name, Object value)
	{
		Validator.notNull(field_name, value);
		
		// See if this is a primitive...
		String primative_value = null;
		
		if ( value instanceof Character ) primative_value = value.toString();
		if ( value instanceof Boolean ) primative_value = value.toString();
		if ( value instanceof Byte ) primative_value = value.toString();
		if ( value instanceof Short ) primative_value = value.toString();
		if ( value instanceof Integer ) primative_value = value.toString();
		if ( value instanceof Long ) primative_value = value.toString();
		if ( value instanceof Float ) primative_value = value.toString();
		if ( value instanceof Double ) primative_value = value.toString();
		if ( value instanceof String ) primative_value = (String)value;
		if ( value instanceof Stringable ) primative_value = ((Stringable)value).getSimpleValue();
		
		if ( primative_value != null )
		{
			ObjectParseTree ret = new ObjectParseTree(field_name);
			ret.setValue(primative_value);
			return ret;
		}
		
		// We have an object of some sort...
		
		TokenBuffer token_buffer = ObjectWriter.serializeToTokenBuffer(value);
		
		ObjectParseTree ret = Parser.parse(token_buffer);
		ret.setFieldName(field_name);
		
		return ret;
	}
	
	public void addMapEntry(FieldDefinition.Map field, Object key, Object value)
	{
		Validator.notNull(field);
		
		if ( key == null || value == null ) return; // nulls not allowed, skip...
		
		ObjectParseTree entry = new ObjectParseTree(field.getSimpleFieldName());
		
		entry.add(mapKeyOrValueToObjectParseTree(FieldName.FIELD_KEY, key));
		entry.add(mapKeyOrValueToObjectParseTree(FieldName.FIELD_VALUE, value));
		
		under_construction.add(entry);
	}
}
