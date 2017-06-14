package org.jimmutable.core.serialization.reader;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jimmutable.core.exceptions.SerializeException;
import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.utils.Validator;

import com.fasterxml.jackson.databind.util.TokenBuffer;

/**
 * ObjectParseTree is created by Parser and used by StandardObject(s) to
 * instantiate themselves from serialized data.
 * 
 * ObjectParseTree are mutable (they are modified by Parser and Builder). That
 * being said, one should not modify a parse tree without a really good reason
 * (generally, use builder to do this)
 * 
 * @author jim.kane
 *
 */

final public class ObjectParseTree implements Iterable<ObjectParseTree>
{
	static private Map<TypeName,Class> standard_object_types = new ConcurrentHashMap(); 
	
	private FieldName field_name; // required
	private String value; // optional
	private TypeName type_hint; // optional
	private LinkedList<ObjectParseTree> children; // optional
	
	/**
	 * Construct an ObjectParseTree.
	 * 
	 * ObjectParseTree(s) are only made via Parser and Builder
	 * 
	 * @param field_name
	 *            The field name of this node in the parse tree. The root of a
	 *            document has the special field name FieldName
	 *            FIELD_DOCUMENT_ROOT
	 */
	public ObjectParseTree(FieldName field_name)
	{
		Validator.notNull(field_name);
		
		this.field_name = field_name;
	}
	
	/**
	 * Set the value
	 * 
	 * @param value The value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
		
		// We always intern the type hint, for faster comparison later on
		if ( field_name.equals(FieldName.FIELD_NAME_TYPE_HINT) )
		{
			this.type_hint = new TypeName(value);
		}
		
		// The base 64 primative values are handled quite gently...
		if ( field_name.equals(FieldName.FIELD_NAME_PRIMITIVE_VALUE_BASE64) )
		{
			field_name = FieldName.FIELD_NAME_PRIMITIVE_VALUE;
			this.value = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
		}
	}
	
	/**
	 * Get the field name of this node in the parse tree
	 * 
	 * @return The field name
	 */
	public FieldName getSimpleFieldName() 
	{ 
		return field_name; 
	}
	
	/**
	 * Set the field name, used by Builder only
	 */
	public void setFieldName(FieldName field_name)
	{
		Validator.notNull(field_name);
		this.field_name = field_name;
	}

	/**
	 * Does this node in the parse tree have any children?
	 * 
	 * @return True if this node has children, false otherwise
	 */
	public boolean hasChildren() 
	{ 
		return children != null && !children.isEmpty(); 
	}
	
	/**
	 * Does this node have a value?
	 * 
	 * Use of this method is not recommended: use the various asXXX or getXXX
	 * methods
	 */
	public boolean hasValue() 
	{ 
		return value != null; 
	}
	
	/**
	 * Get value associated with this node in the parse tree
	 * 
	 * Use of this method is not recommended: use the various asXXX or getXXX
	 * methods
	 * 
	 * @param default_value
	 *            The value to return if this node has no value
	 * @return The contents of the value field, or default_value if this node
	 *         has no value
	 */
	private String getOptionalValue(String default_value) 
	{ 
		if ( !hasValue() ) return default_value;
		return value;
	}
	
	private String getPrimativeValueAsString(String default_value)
	{
		if ( hasValue() )
		{
			return value;
		}
		
		if ( isTypeHint(TypeName.TYPE_NAME_NULL) )
		{
			return default_value;
		}
		
		if ( isPrimativeObject() )
		{
			ObjectParseTree value = findChild(FieldName.FIELD_NAME_PRIMITIVE_VALUE, null);
			
			if ( value == null ) return default_value;
			
			if ( isTypeHint(TypeName.TYPE_NAME_STRING) && !value.hasValue() ) // a null value in a String primitive object means the empty string...
				return "";
			
			return value.getOptionalValue(default_value);
		}
		
		return default_value;
	}
	
	/**
	 * get an iterator over all children
	 */
	public Iterator<ObjectParseTree> iterator() 
	{
		if ( children == null ) return Collections.emptyIterator();
		
		return children.iterator();
	}
	
	/**
	 * Add a child.  Only called from Parser
	 * 
	 * @param child The child to add
	 */
	public void add(ObjectParseTree child)
	{
		Validator.notNull(child);
		if ( children == null ) children = new LinkedList();
		children.add(child);
	}
	
	/**
	 * Set or add a child.  Only called from Builder
	 * 
	 * This function removes any children with the same field name as new_child, then adds new_child
	 * 
	 * @param new_child The object parse tree to "set or add"
	 */
	public void setOrAdd(ObjectParseTree new_child)
	{
		Validator.notNull(new_child);
		
		removeAll(new_child.getSimpleFieldName());
		add(new_child); 
	}
	
	/**
	 * Remove all children with the specified field name
	 * 
	 * @param field_name The field name to search for/remove
	 */
	public void removeAll(FieldName field_name)
	{
		Validator.notNull(field_name);
		
		if ( children != null )
		{ 
			Iterator<ObjectParseTree> itr = children.iterator();
			
			while(itr.hasNext())
			{
				ObjectParseTree cur = itr.next();
				
				if ( cur.getSimpleFieldName().equals(field_name) )
					itr.remove();
			}
		}
	}
	
	/**
	 * Find a child with the specified field_name
	 * 
	 * @param field_name
	 *            The field name to search for
	 * @param default_value
	 *            The ObjectParseTree to return if no child with the field name
	 *            specified could be found
	 * @return The first child with the specified field name, or default_value
	 *         if no such child exists
	 */
	public ObjectParseTree findChild(FieldName field_name, ObjectParseTree default_value)
	{
		if ( field_name == null ) return default_value;
		
		for ( ObjectParseTree child : this )
		{
			if ( child.getSimpleFieldName().equals(field_name) )
			{
				return child;
			}
		}
		
		return default_value;
	}
	
	/**
	 * Does this tree have a type hint?
	 * 
	 * @return True if this reader has a type hint, false otherwise
	 */
	public boolean hasTypeHint() 
	{
		return getOptionalTypeHint(null) != null;
	}
	
	/**
	 * Get the type hint
	 * 
	 * @param default_value
	 *            The value to return if this node does not have a type hint
	 * @return The type hint associated with this node, or default_value if no
	 *         type hint is present
	 */
	public TypeName getOptionalTypeHint(TypeName default_value)
	{
		if ( type_hint != null ) return type_hint;
		
		ObjectParseTree t = findChild(FieldName.FIELD_NAME_TYPE_HINT,null);
		
		if ( t == null || t.type_hint == null ) return default_value;
		
		type_hint = t.type_hint;
		return type_hint;
	}

	/**
	 * Is this a primitive object? (i.e. String, Float, etc.) in either full
	 * object or primitive form
	 * 
	 */
	public boolean isPrimativeObject()
	{
		TypeName tn = getOptionalTypeHint(null);
		if ( tn == null ) return false;
		return tn.isPrimative();
	}
	
	/**
	 * Test to see if the type hint is equal to the specified value
	 * 
	 * @param name_to_test
	 *            The type name to test
	 * @return True if the type hint for this reader equals the name_to_test,
	 *         false otherwise
	 */
	public boolean isTypeHint(TypeName name_to_test)
	{
		Validator.notNull(name_to_test);
		
		TypeName tn = getOptionalTypeHint(null);
		if ( tn == null ) return false;
		return tn.equals(name_to_test);
	}
	
	/**
	 * Called by Parser only
	 */
	public void removeLast()
	{
		if ( children == null ) return;
		
		children.removeLast();
	}
	
	/**
	 * Perform a diagnostic printout of this parse tree
	 */
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		diagnosticPrint(b, 0);
		
		return b.toString().trim();
	}
	
	private void diagnosticPrint(StringBuilder builder, int indent)
	{
		for ( int i = 0; i < indent; i++ )
		{
			builder.append('\t');
		}
		
		builder.append(field_name.getSimpleName());
		
		if ( hasValue() )
		{
			builder.append(String.format(": [%s]", getOptionalValue(null)));
		}
		
		builder.append("\n");
		
		for ( ObjectParseTree child : this )
		{
			child.diagnosticPrint(builder, indent+1);
		}
	}
	
	/**
	 * Interpret the current node as a String
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            String
	 * @return This node, interpreted as a String, or default_value if this is
	 *         not possible.
	 */
	
	public String asString(String default_value)
	{
		return getPrimativeValueAsString(default_value);
	}
	
	/**
	 * Interpret the current node as a Character
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            Character
	 * @return This node, interpreted as a Character, or default_value if this is
	 *         not possible.
	 */
	public Character asCharacter(Character default_value)
	{
		String primative_value =  getPrimativeValueAsString(null);
		
		if ( primative_value == null ) return default_value;
		if ( primative_value.length() > 1 ) return default_value;
		
		return primative_value.charAt(0);
	}
	
	/**
	 * Interpret the current node as a Boolean
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            Boolean
	 * @return This node, interpreted as a Boolean, or default_value if this is
	 *         not possible.
	 */
	public Boolean asBoolean(Boolean default_value)
	{
		String primative_value =  getPrimativeValueAsString(null);
		
		if ( primative_value == null ) return default_value;
		
		if ( primative_value.equalsIgnoreCase("true") ) return true;
		if ( primative_value.equalsIgnoreCase("t") ) return true;
		if ( primative_value.equals("1") ) return true;
		
		if ( primative_value.equalsIgnoreCase("false") ) return false;
		if ( primative_value.equalsIgnoreCase("f") ) return false;
		if ( primative_value.equals("0") ) return false;
		
		return default_value;
	}
	
	/**
	 * Interpret the current node as a byte
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            byte
	 * @return This node, interpreted as a byte, or default_value if this is
	 *         not possible.
	 */
	public Byte asByte(Byte default_value)
	{
		String primative_value =  getPrimativeValueAsString(null);
		if ( primative_value == null ) return default_value;
		
		try
		{
			return new Byte(primative_value);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	/**
	 * Interpret the current node as a short
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            short
	 * @return This node, interpreted as a short, or default_value if this is
	 *         not possible.
	 */
	public Short asShort(Short default_value)
	{
		String primative_value =  getPrimativeValueAsString(null);
		if ( primative_value == null ) return default_value;
		
		try
		{
			return new Short(primative_value);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	/**
	 * Interpret the current node as a int
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            int
	 * @return This node, interpreted as a int, or default_value if this is
	 *         not possible.
	 */
	public Integer asInteger(Integer default_value)
	{
		String primative_value =  getPrimativeValueAsString(null);
		if ( primative_value == null ) return default_value;
		
		try
		{
			return new Integer(primative_value);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	/**
	 * Interpret the current node as a long
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            long
	 * @return This node, interpreted as a long, or default_value if this is
	 *         not possible.
	 */
	
	public Long asLong(Long default_value)
	{
		String primative_value =  getPrimativeValueAsString(null);
		if ( primative_value == null ) return default_value;
		
		try
		{
			return new Long(primative_value);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	/**
	 * Interpret the current node as a float
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            float
	 * @return This node, interpreted as a float, or default_value if this is
	 *         not possible.
	 */
	
	public Float asFloat(Float default_value)
	{
		String primative_value =  getPrimativeValueAsString(null);
		if ( primative_value == null ) return default_value;
		
		try
		{
			return new Float(primative_value);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	/**
	 * Interpret the current node as a double
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            double
	 * @return This node, interpreted as a double, or default_value if this is
	 *         not possible.
	 */
	
	public Double asDouble(Double default_value)
	{
		String primative_value =  getPrimativeValueAsString(null);
		if ( primative_value == null ) return default_value;
		
		try
		{
			return new Double(primative_value);
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	 
	/**
	 * Interpret the current node as an Object (either StandardObject or a
	 * primative object)
	 * 
	 * @param default_value
	 *            The value to return if this node can not be interpreted as a
	 *            object
	 * @return This node, interpreted as an object, or default_value if this is
	 *         not possible.
	 */
	
	
	public Object asObject(Object default_value) 
	{
		// Special handling for null fields
		if ( !hasChildren() && !hasValue() )
			return default_value;
			
		if ( isPrimativeObject() )
		{
			if ( isTypeHint(TypeName.TYPE_NAME_NULL) ) 
			{
				return default_value;
			}
			
			if ( isTypeHint(TypeName.TYPE_NAME_STRING) )
			{
				String ret = asString(null);
				if ( ret == null ) return default_value;
				return ret;
			}
			
			if ( isTypeHint(TypeName.TYPE_NAME_CHAR) )
			{
				Character ret = asCharacter(null);
				if ( ret == null ) return default_value;
				return ret;
			}
			
			if ( isTypeHint(TypeName.TYPE_NAME_BOOLEAN) )
			{
				Boolean ret = asBoolean(null);
				if ( ret == null ) return default_value;
				return ret;
			}
			
			if ( isTypeHint(TypeName.TYPE_NAME_BYTE) )
			{
				Byte ret = asByte(null);
				if ( ret == null ) return default_value;
				return ret;
			}
			
			if ( isTypeHint(TypeName.TYPE_NAME_SHORT) )
			{
				Short ret = asShort(null);
				if ( ret == null ) return default_value;
				return ret;
			} 
			
			if ( isTypeHint(TypeName.TYPE_NAME_INT) )
			{
				Integer ret = asInteger(null);
				if ( ret == null ) return default_value;
				return ret;
			}
			
			if ( isTypeHint(TypeName.TYPE_NAME_LONG) )
			{
				Long ret = asLong(null);
				if ( ret == null ) return default_value;
				return ret;
			}
			
			if ( isTypeHint(TypeName.TYPE_NAME_FLOAT) )
			{
				Float ret = asFloat(null);
				if ( ret == null ) return default_value;
				return ret;
			}
			
			if ( isTypeHint(TypeName.TYPE_NAME_DOUBLE) )
			{
				Double ret = asDouble(null);
				if ( ret == null ) return default_value;
				return ret;
			}
			
			return default_value; // unknown primitive type? (should not be possible)
		}
		
		TypeName type_name = getOptionalTypeHint(null);
		
		if ( type_name == null )
			throw new SerializeException("Attempt to read object, but not a primitive and no type hint present");
		
		Class c = standard_object_types.get(type_name); 
		
		if ( c == null )
		{
			throw new SerializeException(String.format("The type name %s is not registered.  Register with ObjectReader.registerTypeName",type_name.getSimpleName()));
		}
		
		// Standard object converter...
		if ( c != null )
		{
			try
			{
				Constructor constructor = c.getConstructor(ObjectParseTree.class);
				Object ret = constructor.newInstance(this);
				
				if ( ret instanceof StandardObject )
				{
					((StandardObject)ret).complete();
				}
				
				return ret;
			}
			catch(NoSuchMethodException e)
			{
				throw new SerializeException(String.format("No constructor found %s(ReadTree t)", c.getSimpleName()),e);
			}
			catch(SerializeException e2)
			{
				throw e2;
			}
			catch(Exception e3)
			{
				throw new SerializeException("Error reading object",e3);
			}
		}
		
		return default_value;
	}
	
	/**
	 * Get the value of a given child (field_name) as a String
	 */
	public String getString(FieldName field_name, String default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asString(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a boolean
	 */
	public Boolean getBoolean(FieldName field_name, Boolean default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asBoolean(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a char
	 */
	public Character getCharacter(FieldName field_name, Character default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asCharacter(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a byte
	 */
	public Byte getByte(FieldName field_name, Byte default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asByte(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a short
	 */
	public Short getShort(FieldName field_name, Short default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asShort(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a int
	 */
	public Integer getInt(FieldName field_name, Integer default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asInteger(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a long
	 */
	public Long getLong(FieldName field_name, Long default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asLong(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a float
	 */
	public Float getFloat(FieldName field_name, Float default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asFloat(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a double
	 */
	public Double getDouble(FieldName field_name, Double default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asDouble(default_value);
	}
	
	/**
	 * Get the value of a given child (field_name) as a object
	 */
	public Object getObject(FieldName field_name, Object default_value)
	{
		ObjectParseTree child = findChild(field_name, null);
		if ( child == null ) return default_value;
		return child.asObject(default_value);
	}
	
	
	/**
	 * Get the value of a given child (field) as a String
	 * 
	 * If the string is not readable, then the unset value of the field is returned.
	 */
	public String getString(FieldDefinition.String field)
	{
		Validator.notNull(field);
		return getString(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a StandardNum
	 */
	
	public <E extends StandardEnum> E getEnum(FieldDefinition.Enum<E> field)
	{
		Validator.notNull(field);
		
		String code = getString(field.getSimpleFieldName(),null);
		if ( code == null ) return field.getSimpleUnsetValue();
		
		return field.getSimpleConverter().fromCode(code, field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a Stringable
	 */
	
	public <S extends Stringable> S getStringable(FieldDefinition.Stringable<S> field)
	{
		Validator.notNull(field);
		
		String str = getString(field.getSimpleFieldName(),null);
		if ( str == null ) return field.getSimpleUnsetValue();
		
		return field.getSimpleConverter().fromString(str, field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a boolean
	 */
	public Boolean getBoolean(FieldDefinition.Boolean field)
	{
		Validator.notNull(field);
		return getBoolean(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a char
	 */
	public Character getCharacter(FieldDefinition.Character field)
	{
		Validator.notNull(field);
		return getCharacter(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a byte
	 */
	public Byte getByte(FieldDefinition.Byte field)
	{
		Validator.notNull(field);
		return getByte(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a short
	 */
	public Short getShort(FieldDefinition.Short field)
	{
		Validator.notNull(field);
		return getShort(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a int
	 */
	public Integer getInt(FieldDefinition.Integer field)
	{
		Validator.notNull(field);
		return getInt(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a long
	 */
	public Long getLong(FieldDefinition.Long field)
	{
		Validator.notNull(field);
		return getLong(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a float
	 */
	public Float getFloat(FieldDefinition.Float field)
	{
		Validator.notNull(field);
		return getFloat(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a double
	 */
	public Double getDouble(FieldDefinition.Double field)
	{
		Validator.notNull(field);
		return getDouble(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * Get the value of a given child (field) as a object
	 */
	public Object getObject(FieldDefinition field)
	{
		Validator.notNull(field);
		return getObject(field.getSimpleFieldName(),field.getSimpleUnsetValue());
	}
	
	/**
	 * An enum used by getCollection and getMap as an instruction on what to do
	 * when an error is encountered
	 * 
	 * @author jim.kane
	 *
	 */
	static public enum OnError
	{
		SKIP,
		THROW_EXCEPTION;
	}
	
	/**
	 * Get (read) a collection
	 * 
	 * @param field
	 *            The field that contains the collection
	 * @param empty_collection
	 *            A mutable, empty collection (will be returned "filled", but
	 *            still mutable)
	 * @param type
	 *            A ReadAs object that specifies the type to read as. If you are
	 *            working with a collection of StandardObject(s), use
	 *            ReadAs.OBJECT
	 * @param on_error
	 *            What to do if an error is encountered while reading an element
	 *            of the collection (skip it, throw a SerializeException)
	 * @return empty_collection "filled"
	 */
	
	public <C extends Collection> C getCollection(FieldDefinition field, C empty_collection, ReadAs type, OnError on_error)
	{
		Validator.notNull(field);
		return getCollection(field.getSimpleFieldName(), empty_collection, type, on_error);
	}
	
	/**
	 * Get (read) a collection
	 * 
	 * @param field_name
	 *            The field name that contains the collection
	 * @param empty_collection
	 *            A mutable, empty collection (will be returned "filled", but
	 *            still mutable)
	 * @param type
	 *            A ReadAs object that specifies the type to read as. If you are
	 *            working with a collection of StandardObject(s), use
	 *            ReadAs.OBJECT
	 * @param on_error
	 *            What to do if an error is encountered while reading an element
	 *            of the collection (skip it, throw a SerializeException)
	 * @return empty_collection "filled"
	 */
	public <C extends Collection> C getCollection(FieldName field_name, C empty_collection, ReadAs type, OnError on_error)
	{
		Validator.notNull(field_name);
		Validator.notNull(empty_collection);
		Validator.notNull(type);
		
		C ret = empty_collection;
		
		for ( ObjectParseTree child : children )
		{
			if ( child.getSimpleFieldName().equals(field_name) ) 
			{
				Object obj = type.readAs(child);
				
				if ( obj == null ) 
				{
					if ( on_error == OnError.SKIP ) continue;
					else throw new SerializeException("Could not read object in collection");
				}
				
				ret.add(obj);
			}
		}
		
		return ret;
	}
	
	/**
	 * Get (read) a Map
	 * 
	 * @param field
	 *            The field definition of the field to read the map from
	 * @param empty_map
	 *            A mutable, empty map (will be returned "filled", but still
	 *            mutable)
	 * @param key_type
	 *            A ReadAs object that specifies the type to read keys as
	 * @param value_type
	 *            A ReadAs object that specifies the type read values as
	 * @param on_error
	 *            What to do when an error (reading a key or value) occours
	 *            (skip, throw and exception)
	 * @return empty_map "filled"
	 */
	public <M extends Map> M getMap(FieldDefinition field, M empty_map, ReadAs key_type, ReadAs value_type, OnError on_error)
	{
		Validator.notNull(field_name);
		return getMap(field.getSimpleFieldName(), empty_map, key_type, value_type, on_error);
	}
	
	/**
	 * Get (read) a Map
	 * 
	 * @param field_name
	 *            The field name to read the map from
	 * @param empty_map
	 *            A mutable, empty map (will be returned "filled", but still
	 *            mutable)
	 * @param key_type
	 *            A ReadAs object that specifies the type to read keys as
	 * @param value_type
	 *            A ReadAs object that specifies the type read values as
	 * @param on_error
	 *            What to do when an error (reading a key or value) occours
	 *            (skip, throw and exception)
	 * @return empty_map "filled"
	 */
	public <M extends Map> M getMap(FieldName field_name, M empty_map, ReadAs key_type, ReadAs value_type, OnError on_error)
	{
		Validator.notNull(field_name);
		Validator.notNull(empty_map);
		Validator.notNull(key_type);
		Validator.notNull(value_type);
		Validator.notNull(on_error);
		
		M ret = empty_map;
		
		for ( ObjectParseTree entry : children )
		{
			if ( entry.getSimpleFieldName().equals(field_name) ) 
			{
				ObjectParseTree key_tree = entry.findChild(FieldName.FIELD_KEY, null);
				ObjectParseTree value_tree = entry.findChild(FieldName.FIELD_VALUE, null);
				
				if ( key_tree == null || value_tree == null ) 
				{
					if ( on_error == OnError.SKIP ) continue;
					if ( on_error == OnError.THROW_EXCEPTION ) throw new SerializeException("Could not read key/value pair");
				}
				
				Object key = key_type.readAs(key_tree);
				Object value = value_type.readAs(value_tree);
				
				if ( key == null || value == null ) 
				{
					if ( on_error == OnError.SKIP ) continue;
					if ( on_error == OnError.THROW_EXCEPTION ) throw new SerializeException("Could not read key/value pair");
				}
				
				ret.put(key, value);
			}
		}
		
		return ret;
	}
	
	/**
	 * Construct an object from previously serialized data. The format is
	 * automatically detected. 
	 * 
	 * There are two common exceptions that can occur when constructing an
	 * object from previously serialized data, SerializeException (trouble
	 * reading) and ValidationException (object read was not valid). Both extend
	 * RuntimeException, so you are not required to handle them explicitly. That
	 * being said, most (nee all) well designed readers of serialized data
	 * "trap" errors: thought must be given as to what to do when this happens.
	 * 
	 * @param serialized_data
	 *            The data to read from
	 * @return The object previously serialized
	 * 
	 */
	
	static public Object deserialize(String document)
	{
		ObjectParseTree t = Parser.parse(document);
		
		Object ret = t.asObject(null);
		
		if ( ret == null ) 
			throw new SerializeException("Unable to read document!");
		
		return ret;
	}
	
	/**
	 * Construct an object from previously serialized data.
	 * 
	 * This version of the function reads from a TokenBuffer and is used to
	 * clone objects etc.
	 * 
	 * @return The object previously serialized
	 * 
	 */
	static public Object deserialize(TokenBuffer document) throws SerializeException
	{
		ObjectParseTree t = Parser.parse(document);
		
		Object ret = t.asObject(null);
		
		if ( ret == null ) 
			throw new SerializeException("Unable to read document!");
		
		return ret;
	}
	
	/**
	 * Register a type name. TypeName(s) must be registered *prior* to readign
	 * any instances of the type. It is safe to register a TypeName multiple
	 * times (last registration wins). The function is thread safe.
	 * 
	 * Typically, each Jimmtuable VM implements a register class (See
	 * JimmutableTypeNameRegister). Rember to invoke this (pretty much first
	 * thing) at boot time.
	 * 
	 * Classes must have a static public method named TYPE_NAME to be registered
	 * 
	 * @param c
	 *            The class to register
	 */
	static public void registerTypeName(Class c)
	{
		try
		{
			TypeName type_name = (TypeName)c.getField("TYPE_NAME").get(null);
			if ( type_name.isPrimative() ) throw new SerializeException("Attempt to register a primative type name using registerTypeName.  Did you try to register a Stringable?");
			standard_object_types.put(type_name, c);
		}
		catch(Exception e)
		{
			System.err.println(String.format("Unable to register a type name for %s, could not read static public field %s.TYPE_NAME", c.getSimpleName(),c.getSimpleName()));
			e.printStackTrace();
		}
	}
	
	/**
	 * Check to see if a given TypeName object is registered
	 * 
	 * @param type
	 *            The TypeName to check
	 * @return true if the TypeName is registered, false otherwise
	 */
	static public boolean isTypeRegistered(TypeName type)
	{
		if ( type == null ) return false;
		return standard_object_types.containsKey(type);
	}
	
	/**
	 * Given the Class associated with a TypeName
	 * 
	 * @param type_name
	 *            Any type name, may not be null
	 * @param default_value
	 *            The value to return if type_name is not associated with a
	 *            class
	 * @return The Class associated with type_name or default_value if the type
	 *         name is not bound
	 */
	static public Class getClassForTypeName(TypeName type_name, Class default_value) 
	{
		if ( type_name == null ) return default_value;
		
		Class ret = standard_object_types.get(type_name);
		if ( ret == null ) return default_value;
		
		return ret;
	}
}

