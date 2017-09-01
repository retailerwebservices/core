package org.jimmutable.storage;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
/**
 * 
 * @author andrew.towe
 *	This class exists to help us manage the extensions of each of our storable objects. 
 *	This class handles the mimetypes.
 */
public class StorageKeyExtension extends Stringable
{	
	public StorageKeyExtension(String value)
	{
		super(value);
	}
	/**
	 * @return the mime type of this extension.  
	 */

	public String getSimpleMimeType() 
	{
		switch(getSimpleValue()){
		case "html": return "text/html";
		case "htm": return "text/htm";
		case "css": return "text/css";
		case "js": return "application/js";
		case "json": return "application/json";
		case "xml": return "application/xml";
		case "jpeg": return "image/jpeg";
		case "jpg": return "image/jpg";
		case "gif": return "image/gif";
		case "png": return "image/png";
		case "pdf": return "application/pdf";
		case "xslx": return "application/xslx";
		case "csv": return "text/csv";
		case "txt": return "text/txt";
		default: return "application/octet-stream";
		}
	}
	
	@Override
	public void normalize() 
	{
		Validator.notNull(getSimpleValue());
		
		// CODE REVIEW: You can not do this becuase validate has *not* guaranteed that value is null
		// TRY it out: StorageKeyExtension(null) will throw an NPE here
		// I have fixed it for you, just remember this
		
		if( getSimpleValue() != null )
		{
			// we want to strip out any leading ".". so that we do not confuse ourselves. 
			
			while( getSimpleValue().startsWith(".") )
				setValue(getSimpleValue().substring(1));
		}
		normalizeLowerCase();
	}

	@Override
	public void validate() 
	{
		Validator.min(getSimpleValue().length(), 1);//if we strip out the "." and nothing is left over, it will be caught here. 
		Validator.containsOnlyValidCharacters(getSimpleValue(),Validator.LOWERCASE_LETTERS,Validator.NUMBERS);		
	}

}