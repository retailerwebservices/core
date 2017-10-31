package org.jimmutable.core.objects.common;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;

public class ObjectReference extends Stringable
{
	private Kind kind;
	private ObjectId id;

	public ObjectReference( String value )
	{
		super(value);
		// TODO Auto-generated constructor stub
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
		super.normalizeTrim();
		super.normalizeLowerCase();
		int kind_delim_index = super.getSimpleValue().indexOf(":");
		kind = new Kind(super.getSimpleValue().substring(0, kind_delim_index));
		id = new ObjectId(super.getSimpleValue().substring(kind_delim_index + 1, super.getSimpleValue().length()));
	}

	@Override
	public void validate()
	{
		Validator.notNull(super.getSimpleValue(), getSimpleKind(), getSimpleObjectId());

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
