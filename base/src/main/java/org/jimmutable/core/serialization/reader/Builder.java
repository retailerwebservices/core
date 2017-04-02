package org.jimmutable.core.serialization.reader;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.examples.book.Book;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JimmutableTypeNameRegister;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Validator;

import com.fasterxml.jackson.databind.util.TokenBuffer;

public class Builder 
{
	private TypeName under_construction_type_name;
	private ObjectParseTree under_construction;
	
	public Builder(TypeName type_name)
	{
		Validator.notNull(type_name);
		
		under_construction_type_name = type_name;
		
		under_construction = new ObjectParseTree(FieldName.FIELD_DOCUMENT_ROOT);
		
		ObjectParseTree type_hint = new ObjectParseTree(FieldName.FIELD_NAME_TYPE_HINT);
		type_hint.setValue(type_name.getSimpleName());
		
		under_construction.add(type_hint);
	}
	
	public void unset(FieldDefinition field)
	{
		under_construction.removeAll(field.getSimpleFieldName());
	}
	
	public void set(FieldDefinition.Boolean field, boolean value) { insertNonNullPrimative(field, value ? "true" : "false",true); }
	public void set(FieldDefinition.Character field, char value) { insertNonNullPrimative(field,String.format("%c", value),true); }
	public void set(FieldDefinition.Byte field, byte value) { insertNonNullPrimative(field,String.format("%d", value),true); }
	public void set(FieldDefinition.Short field, short value) { insertNonNullPrimative(field,String.format("%d", value),true); }
	public void set(FieldDefinition.Integer field, int value) { insertNonNullPrimative(field,String.format("%d", value),true); }
	public void set(FieldDefinition.Long field, long value) { insertNonNullPrimative(field,String.format("%d", value),true); }
	public void set(FieldDefinition.Float field, float value) { insertNonNullPrimative(field,String.format("%f", value),true); }
	public void set(FieldDefinition.Double field, double value) { insertNonNullPrimative(field,String.format("%f", value),true); }
	
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
	
	public StandardObject create(StandardObject default_value)
	{
		return (StandardObject)under_construction.asObject(default_value);
	}
	
	public void add(FieldDefinition.Collection field, boolean value) { insertNonNullPrimative(field, value ? "true" : "false",false); }
	public void add(FieldDefinition.Collection field, char value) { insertNonNullPrimative(field,String.format("%c", value),false); }
	public void add(FieldDefinition.Collection field, byte value) { insertNonNullPrimative(field,String.format("%d", value),false); }
	public void add(FieldDefinition.Collection field, short value) { insertNonNullPrimative(field,String.format("%d", value),false); }
	public void add(FieldDefinition.Collection field, int value) { insertNonNullPrimative(field,String.format("%d", value),false); }
	public void add(FieldDefinition.Collection field, long value) { insertNonNullPrimative(field,String.format("%d", value),false); }
	public void add(FieldDefinition.Collection field, float value) { insertNonNullPrimative(field,String.format("%f", value),false); }
	public void add(FieldDefinition.Collection field, double value) { insertNonNullPrimative(field,String.format("%f", value),false); }
	
	public void add(FieldDefinition.Collection field, String value)
	{
		Validator.notNull(field);
		
		if ( value == null ) return;
		
		insertNonNullPrimative(field, value,false);
	}
	
	static public void main(String args[])
	{
		JimmutableTypeNameRegister.registerAllTypes();
		
		Builder test = new Builder(Book.TYPE_NAME);
		test.set(Book.FIELD_TITLE, "The Screwtape Letters");
		test.set(Book.FIELD_BINDING, BindingType.HARD_COVER.getSimpleCode());
		test.set(Book.FIELD_PAGE_COUNT, 123);
		test.add(Book.FIELD_AUTHORS, "C.S. Lewis");
		
		System.out.println(test.create(null).toJavaCode(Format.XML_PRETTY_PRINT, "book"));
	}
}
