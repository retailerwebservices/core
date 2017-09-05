package org.jimmutable.aws.servlet_utils.get;

import java.util.Objects;

import org.jimmutable.aws.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;

/**
 * GetResponseOK
 * Used to indicate successful response to get request
 * 
 * @author Preston McCumber
 * @date Sep 1, 2017
 */
public class GetResponseOK extends JSONServletResponse
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.get.GetResponseOK");

	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	static public final FieldDefinition.StandardObject FIELD_OBJECT = new FieldDefinition.StandardObject("object",
			null);

	static public final int HTTP_STATUS_CODE_OK = 200;

	private StandardImmutableObject object; // required

	public GetResponseOK( StandardImmutableObject object )
	{
		this.object = object;
		complete();
	}

	public GetResponseOK( ObjectParseTree t )
	{
		this.object = (StandardImmutableObject) t.getObject(FIELD_OBJECT);
	}

	@Override
	public int compareTo( JSONServletResponse obj )
	{
		if ( !(obj instanceof GetResponseOK) )
			return 0;

		GetResponseOK other = (GetResponseOK) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleObject(), other.getSimpleObject());
		return ret;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeObject(FIELD_OBJECT, getSimpleObject());
	}

	@Override
	public int getSimpleHTTPResponseCode()
	{
		return HTTP_STATUS_CODE_OK;
	}

	public StandardImmutableObject getSimpleObject()
	{
		return (StandardImmutableObject) object;
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
		return Objects.hash(getSimpleObject());
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof GetResponseOK) )
			return false;

		GetResponseOK other = (GetResponseOK) obj;
		if ( !Objects.equals(getSimpleObject(), other.getSimpleObject()) )
			return false;

		return true;
	}

}
