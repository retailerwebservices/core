package org.jimmutable.cloud.servlet_utils.upsert;

import java.util.Objects;

import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Optional;

/**
 * UpsertResponseOK Used to indicate successful response to update/insert
 * request
 * 
 * @author Preston McCumber Sep 1, 2017
 */
public class UpsertResponseOK extends JSONServletResponse
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.upsert.UpsertResponseOK");

	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	static public final FieldDefinition.StandardObject FIELD_OBJECT = new FieldDefinition.StandardObject("object", null);
	static public final FieldDefinition.String FIELD_MESSAGE = new FieldDefinition.String("message", null);

	static public final int HTTP_STATUS_CODE_OK = 200;

	private String message; // optional
	private StandardImmutableObject object; // optional

	public UpsertResponseOK()
	{
	};

	public UpsertResponseOK(String message, StandardImmutableObject object)
	{
		this.message = message;
		this.object = object;
		complete();
	}

	public UpsertResponseOK(ObjectParseTree t)
	{
		this.message = t.getString(FIELD_MESSAGE);
		this.object = (StandardImmutableObject) t.getObject(FIELD_OBJECT);
	}

	@Override
	public int compareTo(JSONServletResponse obj)
	{
		if (!(obj instanceof UpsertResponseOK))
			return 0;

		UpsertResponseOK other = (UpsertResponseOK) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getOptionalMessage(null), other.getOptionalMessage(null));
		ret = Comparison.continueCompare(ret, getOptionalObject(null), other.getOptionalObject(null));
		return ret;
	}

	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeString(FIELD_MESSAGE, getOptionalMessage(null));

		if (getOptionalObject(null) instanceof Stringable)
		{

			Stringable s = (Stringable) getOptionalObject(null);
			writer.writeStringable(FIELD_OBJECT, s);
		} else
		{

			writer.writeObject(FIELD_OBJECT, getOptionalObject(null));
		}
	}

	@Override
	public int getSimpleHTTPResponseCode()
	{
		return HTTP_STATUS_CODE_OK;
	}

	public String getOptionalMessage(String default_value)
	{
		return Optional.getOptional(message, null, default_value);
	}

	public StandardImmutableObject getOptionalObject(String default_value)
	{
		return (StandardImmutableObject) Optional.getOptional(object, null, default_value);
	}

	@Override
	public void freeze()
	{
	}

	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getOptionalMessage(null), getOptionalObject(null));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof UpsertResponseOK))
			return false;

		UpsertResponseOK other = (UpsertResponseOK) obj;
		if (!Objects.equals(getOptionalMessage(null), other.getOptionalMessage(null)))
			return false;
		if (!Objects.equals(getOptionalObject(null), other.getOptionalObject(null)))
			return false;

		return true;
	}

}
