package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.objects.common.ObjectId.MyConverter;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;

/**
 * A reference to the object we have stored. 
 * The Object's Kind and unique identifier (Object Id) is the reference. 
 * @author andrew.towe
 */

public class ObjectReference extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();
	
	private Kind kind;
	private ObjectId id;

	public ObjectReference( String value )
	{
		super(value);
	}

	public ObjectReference( Kind kind, ObjectId id )
	{
		super(createStringFromComponents(kind, id));
	}

	public ObjectReference( ObjectParseTree tree )
	{
		super(tree);
	}

	@Override
	public void normalize()
	{
		normalizeTrim();
		normalizeLowerCase();
	}

	@Override
	public void validate()
	{
		int kind_delim_index = super.getSimpleValue().indexOf(":");
		kind = new Kind(super.getSimpleValue().substring(0, kind_delim_index));
		id = new ObjectId(super.getSimpleValue().substring(kind_delim_index + 1, super.getSimpleValue().length()));
		Validator.notNull(getSimpleValue(), getSimpleKind(), getSimpleObjectId());

	}

	static private String createStringFromComponents( Kind kind, ObjectId object_id )
	{
		Validator.notNull(kind, object_id);
		return String.format("%s:%s", kind.getSimpleValue(), object_id.getSimpleValue());
	}

	public Kind getSimpleKind()
	{
		return kind;
	}

	public ObjectId getSimpleObjectId()
	{
		return id;
	}

	static public class MyConverter extends Stringable.Converter<ObjectReference>
	{
		public ObjectReference fromString( String str, ObjectReference default_value )
		{
			try
			{
				return new ObjectReference(str);
			}
			catch ( Exception e )
			{
				return default_value;
			}
		}
	}
}
