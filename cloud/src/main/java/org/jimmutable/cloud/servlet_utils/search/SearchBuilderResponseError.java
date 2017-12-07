package org.jimmutable.cloud.servlet_utils.search;

import java.util.Objects;

import org.jimmutable.cloud.servlet_utils.common_objects.JSONServletResponse;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

public class SearchBuilderResponseError extends JSONServletResponse
{
	static public final TypeName TYPE_NAME = new TypeName("search_builder_response_error");

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	static public final FieldDefinition.String FIELD_SEARCH_REQUEST = new FieldDefinition.String("search_request", null);
	static public final FieldDefinition.String FIELD_MESSAGE = new FieldDefinition.String("message", null);

	static public final int HTTP_STATUS_CODE_ERROR = 500;

	private String search_request; // required
	private String message; // optional

	public SearchBuilderResponseError(String search_request, String message)
	{
		this.search_request = search_request;
		this.message = message;
		complete();
	}

	public SearchBuilderResponseError(String search_request)
	{
		this.search_request = search_request;
		complete();
	}

	public SearchBuilderResponseError(ObjectParseTree t)
	{
		this.search_request = t.getString(FIELD_SEARCH_REQUEST);
		this.message = t.getString(FIELD_MESSAGE);
	}

	@Override
	public int getSimpleHTTPResponseCode()
	{
		return HTTP_STATUS_CODE_ERROR;
	}

	public String getSimpleSearchRequest()
	{
		return search_request;
	}

	public String getOptionalMessage(String default_message)
	{
		return Optional.getOptional(message, null, default_message);
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
	public int compareTo(JSONServletResponse obj)
	{
		if (!(obj instanceof SearchBuilderResponseError))
			return 0;

		SearchBuilderResponseError other = (SearchBuilderResponseError) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleSearchRequest(), other.getSimpleSearchRequest());
		ret = Comparison.continueCompare(ret, getOptionalMessage(null), other.getOptionalMessage(null));
		return ret;
	}

	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeObject(FIELD_SEARCH_REQUEST, getSimpleSearchRequest());
		writer.writeString(FIELD_MESSAGE, getOptionalMessage(null));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SearchBuilderResponseError))
			return false;

		SearchBuilderResponseError other = (SearchBuilderResponseError) obj;

		if (!Objects.equals(getSimpleSearchRequest(), other.getSimpleSearchRequest()))
			return false;

		if (!Objects.equals(getOptionalMessage(null), other.getOptionalMessage(null)))
			return false;

		return true;
	}

}
