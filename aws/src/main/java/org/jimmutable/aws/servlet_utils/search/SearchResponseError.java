package org.jimmutable.aws.servlet_utils.search;

import java.util.Objects;

import org.jimmutable.aws.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

/**
 * SearchResponseError Used for a standardized error response to a SEARCH
 * request
 * 
 * @author Preston McCumber
 */
public class SearchResponseError extends JSONServletResponse
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.search.SearchResponseError");

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	static public final FieldDefinition.StandardObject FIELD_SEARCH_REQUEST = new FieldDefinition.StandardObject("search_request", new StandardSearchRequest(""));
	static public final FieldDefinition.String FIELD_MESSAGE = new FieldDefinition.String("message", null);

	static public final int HTTP_STATUS_CODE_ERROR = 500;

	private StandardSearchRequest search_request; // required
	private String message; // optional

	public SearchResponseError( StandardSearchRequest search_request, String message )
	{
		this.search_request = search_request;
		this.message = message;
		complete();
	}

	public SearchResponseError( StandardSearchRequest search_request )
	{
		this.search_request = search_request;
		complete();
	}

	public SearchResponseError( ObjectParseTree t )
	{
		this.search_request = (StandardSearchRequest) t.getObject(FIELD_SEARCH_REQUEST);
		this.message = t.getString(FIELD_MESSAGE);
	}

	@Override
	public int getSimpleHTTPResponseCode()
	{
		return HTTP_STATUS_CODE_ERROR;
	}

	public StandardSearchRequest getSimpleSearchRequest()
	{
		return search_request;
	}

	public String getOptionalMessage( String unset_value )
	{
		return Optional.getOptional(message, null, unset_value);
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
		Validator.notNull(search_request);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleSearchRequest(), getOptionalMessage(null));
	}

	@Override
	public int compareTo( JSONServletResponse obj )
	{
		if ( !(obj instanceof SearchResponseError) )
			return 0;

		SearchResponseError other = (SearchResponseError) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleSearchRequest(), other.getSimpleSearchRequest());
		ret = Comparison.continueCompare(ret, getOptionalMessage(null), other.getOptionalMessage(null));
		return ret;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeObject(FIELD_SEARCH_REQUEST, getSimpleSearchRequest());
		writer.writeString(FIELD_MESSAGE, getOptionalMessage(null));
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof SearchResponseError) )
			return false;

		SearchResponseError other = (SearchResponseError) obj;

		if ( !Objects.equals(getSimpleSearchRequest(), other.getSimpleSearchRequest()) )
			return false;

		if ( !Objects.equals(getOptionalMessage(null), other.getOptionalMessage(null)) )
			return false;

		return true;
	}
}
