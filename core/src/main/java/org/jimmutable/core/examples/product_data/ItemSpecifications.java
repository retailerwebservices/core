package org.jimmutable.core.examples.product_data;

import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Validator;

/**
 * An example StandardImmutableObject (roughly an abstraction for PKV)
 * 
 * @author jim.kane
 *
 */
public class ItemSpecifications extends StandardImmutableObject<ItemSpecifications>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.examples.ItemSpecifications"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.StandardObject FIELD_ITEM_KEY = new FieldDefinition.StandardObject("item_key", null);
	static public final FieldDefinition.Map FIELD_ATTRIBUTES = new FieldDefinition.Map("attributes",new FieldHashMap<>());
	
	private ItemKey item_key; // required
	private FieldMap<ItemAttribute,String> attributes;
	
	public ItemSpecifications(ObjectParseTree reader)
	{
		item_key = (ItemKey)reader.getObject(FIELD_ITEM_KEY);
//		attributes = reader.getMap(FIELD_ATTRIBUTES, new FieldHashMap<>(), ItemAttribute.CONVERTER, ReadAs.STRING, ObjectParseTree.OnError.SKIP);
		//TODO Trevor
		attributes = reader.getMap(FIELD_ATTRIBUTES, new FieldHashMap<ItemAttribute,String>(), ItemAttribute.CONVERTER, ReadAs.STRING, ObjectParseTree.OnError.SKIP);
	}

	public void write(ObjectWriter writer) 
	{
		writer.writeObject(FIELD_ITEM_KEY, item_key);
		writer.writeMap(FIELD_ATTRIBUTES, attributes, WriteAs.STRING, WriteAs.STRING);
	}

	public int compareTo(ItemSpecifications o) 
	{
		return getSimpleItemKey().compareTo(o.getSimpleItemKey());
	}

	public void freeze() 
	{
		attributes.freeze();
	}

	
	public void normalize() 
	{
		// These are attributes in the PKV, but in the object they are expressed in the item key
		// If they happen to ahve been set, remove them
		
		attributes.remove(ItemAttribute.ATTRIBUTE_BRAND);
		attributes.remove(ItemAttribute.ATTRIBUTE_PN);
	}

	public void validate() 
	{
		Validator.notNull(item_key);		
	}

	public int hashCode()
	{
		return getSimpleItemKey().hashCode();
	}

	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof ItemSpecifications) ) return false;
		
		ItemSpecifications other = (ItemSpecifications)obj;
		
		if ( !getSimpleItemKey().equals(other.getSimpleItemKey()) ) return false;
		
		return attributes.equals(other.attributes);
	}
	
	public ItemKey getSimpleItemKey() { return item_key; }
	
	public FieldMap<ItemAttribute,String> getSimpleAttributes() { return attributes; }
}
