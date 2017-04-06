package org.jimmutable.core.serialization;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Validator;

abstract public class FieldDefinition<T>
{
	private FieldName field_name;
	private T unset_value;
	
	public FieldDefinition(java.lang.String field_name, T unset_value)
	{
		this.field_name = new FieldName(field_name);
		this.unset_value = unset_value;
	}
	
	public FieldName getSimpleFieldName() { return field_name; }
	public T getSimpleUnsetValue() { return unset_value; }
	
	static public class Boolean extends FieldDefinition<java.lang.Boolean>
	{
		public Boolean(java.lang.String field_name, java.lang.Boolean unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Character extends FieldDefinition<java.lang.Character>
	{
		public Character(java.lang.String field_name, java.lang.Character unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Byte extends FieldDefinition<java.lang.Byte>
	{
		public Byte(java.lang.String field_name, java.lang.Byte unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Short extends FieldDefinition<java.lang.Short>
	{
		public Short(java.lang.String field_name, java.lang.Short unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Integer extends FieldDefinition<java.lang.Integer>
	{
		public Integer(java.lang.String field_name, java.lang.Integer unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Long extends FieldDefinition<java.lang.Long>
	{
		public Long(java.lang.String field_name, java.lang.Long unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Float extends FieldDefinition<java.lang.Float>
	{
		public Float(java.lang.String field_name, java.lang.Float unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Double extends FieldDefinition<java.lang.Double>
	{
		public Double(java.lang.String field_name, java.lang.Double unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class String extends FieldDefinition<java.lang.String>
	{
		public String(java.lang.String field_name, java.lang.String unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class StandardObject extends FieldDefinition<org.jimmutable.core.objects.StandardObject>
	{
		public StandardObject(java.lang.String field_name, org.jimmutable.core.objects.StandardObject unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Collection extends FieldDefinition<java.util.Collection>
	{
		public Collection(java.lang.String field_name, java.util.Collection unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Map extends FieldDefinition<java.util.Map>
	{
		public Map(java.lang.String field_name, java.util.Map unset_value)
		{
			super(field_name,unset_value);
		}
	}
	
	static public class Enum<E extends StandardEnum> extends FieldDefinition<E>
	{
		private StandardEnum.Converter<E> my_converter;
		
		public Enum(java.lang.String field_name, E unset_value, StandardEnum.Converter<E> converter)
		{
			super(field_name,unset_value);
			
			Validator.notNull(converter);
			
			this.my_converter = converter;
		}
		
		public  StandardEnum.Converter<E> getSimpleConverter() { return my_converter; }
	}
}
