package org.jimmutable.aws.servlet_utils.common_objects;

import java.util.Objects;

import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;

/**
 * CODE REVIEW: Needs javadoc comments
 * @author kanej
 *
 */

public class GeneralResponseOK extends JSONServletResponse
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.common_objects.GeneralResponseOK"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.String FIELD_MESSAGE = new FieldDefinition.String("message", null);

	static private final int HTTP_STATUS_CODE_OK = 200;  // CODE REVEIW: Put this in JSONServeletResponse as a static public
	
	private String message;  // optional

	public GeneralResponseOK() {};
	
	public GeneralResponseOK(String message)
	{
		this.message = message;
		complete();
	}
	
	public GeneralResponseOK(ObjectParseTree t)
	{
		this.message = t.getString(FIELD_MESSAGE);
	}

	@Override
	public int compareTo(JSONServletResponse obj)
	{
		if (!(obj instanceof GeneralResponseOK))	return 0;

		GeneralResponseOK other = (GeneralResponseOK) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getOptionalMessage(null), other.getOptionalMessage(null));
		return ret;
	}

	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeString(FIELD_MESSAGE, getOptionalMessage(null));
	}

	@Override
	public int getSimpleHTTPResponseCode() { return HTTP_STATUS_CODE_OK; }
	public String getOptionalMessage(String default_value) { return message; }  // CODE REVIEW: This implementation is wrong.  It won't return default_value if object is unset

	@Override
	public void freeze() {}
	@Override
	public void normalize() {}
	@Override
	public void validate() {}

	@Override
	public int hashCode()
	{
		return Objects.hash(getOptionalMessage(null));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GeneralResponseOK))
			return false;

		GeneralResponseOK other = (GeneralResponseOK) obj;

		// CODE REVIEW: This code can throw a NullPointerException (if, for example, this class does not have  messsage)
		// Suggest you use Objects.equals instead
		
		if (!getOptionalMessage(null).equals(other.getOptionalMessage(null)))
			return false;

		return true;
	}

}
