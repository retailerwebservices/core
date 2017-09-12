package org.jimmutable.cloud.storage;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * 
 * @author andrew.towe This class exists to help us manage the extensions of
 *         each of our storable objects. This class handles the mimetypes.
 */
public class StorageKeyExtension extends Stringable
{
	public static StorageKeyExtension XML = new StorageKeyExtension("xml");
	public static StorageKeyExtension JSON = new StorageKeyExtension("json");

	public StorageKeyExtension( String value )
	{
		super(value);
	}

	/**
	 * @return the mime type of this extension.
	 */

	public String getSimpleMimeType()
	{
		switch ( getSimpleValue() )
		{
		case "html":
			return "text/html";
		case "htm":
			return "text/htm";
		case "css":
			return "text/css";
		case "js":
			return "application/js";
		case "json":
			return "application/json";
		case "xml":
			return "application/xml";
		case "jpeg":
			return "image/jpeg";
		case "jpg":
			return "image/jpg";
		case "gif":
			return "image/gif";
		case "png":
			return "image/png";
		case "pdf":
			return "application/pdf";
		case "xslx":
			return "application/xslx";
		case "csv":
			return "text/csv";
		case "txt":
			return "text/txt";
		default:
			return "application/octet-stream";
		}
	}

	@Override
	public void normalize()
	{
		Validator.notNull(getSimpleValue());

		if ( getSimpleValue() != null )
		{
			while ( getSimpleValue().startsWith(".") )
				setValue(getSimpleValue().substring(1));
		}
		normalizeLowerCase();
	}

	@Override
	public void validate()
	{
		Validator.min(getSimpleValue().length(), 1);// if we strip out the "." and nothing is left over, it will be
													// caught here.
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS);
	}

}