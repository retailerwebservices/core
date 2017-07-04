package org.jimmutable.aws.s3;

import java.util.List;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * A nice class for working with S3 object paths.
 * 
 * Characters are limited to a-z, 0-9, dashes (-) and dots (.) and slashes /
 * 
 * Names may not start or end with a . or -
 * Names may not contain //, --, or ..
 * 
 * Names may not be longer than 1024 characters
 * 
 * @author jim.kane
 *
 */
public class S3Path extends Stringable
{
	static public S3Path PATH_BUCKET_ROOT = new S3Path("");
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	transient private FieldList<String> cached_parts = null;
	
	public S3Path(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
		
		if ( super.getSimpleValue() != null )
		{
			while(true)
			{
				if ( getSimpleValue().length() == 0 ) break;
				
				char first_ch = getSimpleValue().charAt(0);
				
				if ( first_ch == '/' || Character.isWhitespace(first_ch) )
				{
					setValue(getSimpleValue().substring(1));
					continue;
				}
				
				char last_ch = getSimpleValue().charAt(getSimpleValue().length()-1);
				
				if ( last_ch == '/' || Character.isWhitespace(last_ch) )
				{
					setValue(getSimpleValue().substring(0,getSimpleValue().length()-1));
					continue;
				}
				
				break;
			}
		}
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH, Validator.DOT, Validator.FORWARD_SLASH);
		
		
		if ( getSimpleValue().startsWith(".") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not start with a dot (.) ", getSimpleValue()));
		if ( getSimpleValue().startsWith("-") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not start with a dash (-) ", getSimpleValue()));
		if ( getSimpleValue().startsWith("/") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not start with a slash (/) ", getSimpleValue()));
		
	
		if ( getSimpleValue().endsWith(".") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not end with a dot (.) ", getSimpleValue()));
		if ( getSimpleValue().endsWith("-") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not end with a dash (-) ", getSimpleValue()));
		if ( getSimpleValue().endsWith("/") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not end with a slash (/) ", getSimpleValue()));
		
		if ( getSimpleValue().contains("..") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not contain two dots in a row (..) ", getSimpleValue()));
		if ( getSimpleValue().contains("//") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not contain two forward slashes in a row (//) ", getSimpleValue()));
		if ( getSimpleValue().contains("--") ) throw new ValidationException(String.format("S3ObjectPath %s is invalid, object names may not contain two dashes in a row (--) ", getSimpleValue()));
		
		if ( getSimpleValue().length() > 1024 ) throw new ValidationException(String.format("S3ObjectPath %s is too long, object names are limited to 1024 characters", getSimpleValue()));
	}
	
	
	/**
	 * Get the parts of a path. For example, the path foo/bar/baz.txt has the
	 * parts [foo, bar, baz.txt]
	 * 
	 * @return An immutable list containing the parts of the path
	 */
	public List<String> getSimpleParts()
	{
		if ( cached_parts != null ) return cached_parts;
		
		
		FieldArrayList tmp_parts = new FieldArrayList();
		
		for ( String part : getSimpleValue().split("/") )
		{
			if ( part.length() == 0 ) continue;
			tmp_parts.add(part);
		}
		
		tmp_parts.freeze();
		
		cached_parts = tmp_parts;
		
		return cached_parts;
	}
	
	/**
	 * Get the number of parts of a path. For example, the path foo/bar/baz.txt
	 * would return 3. PATH_BUCKET_ROOT.getSimpleNumberOfParts() returns 0
	 * 
	 * @return The number of parts in the path
	 */
	public int getSimpleNumberOfParts()
	{
		return getSimpleParts().size();
	}
	
	/**
	 * Get the part at a given index
	 * 
	 * @param idx
	 *            The index of the part to get
	 * @param default_value
	 *            The value to return if no part exists at the specified index
	 * @return The part at the specified index, or default_value if that part
	 *         does not exist
	 */
	public String getOptionalPart(int idx, String default_value)
	{
		if ( idx < 0 ) return default_value;
		
		List<String> parts = getSimpleParts();
		if ( idx >= parts.size() ) return default_value;
		
		return parts.get(idx);
	}
	
	/**
	 * Does this path have a "last" part?
	 * 
	 * @return True if the path has a last part, false otherwise
	 */
	public boolean hasLastPart() 
	{
		return !getSimpleParts().isEmpty();
	}
	
	/**
	 * Get the last part of a path. For example, the path foo/bar/baz.txt has
	 * the last part baz.txt
	 * 
	 * @param default_value
	 *            The value to return if the path does not have a last part
	 * 
	 * @return The last part of the path (if the path has a last part) or
	 *         default_value if it does not
	 */
	public String getOptionalLastPart(String default_value)
	{
		List<String> parts = getSimpleParts();
		
		if ( parts.isEmpty() ) return default_value;
		
		return parts.get(parts.size()-1);
	}
	
	/**
	 * Does this path have an extension?
	 * 
	 * foo/bar/baz.txt returns true foo returns false
	 * 
	 * @return True if this path has a filename extension, false otherwise
	 */
	public boolean hasExtension()
	{
		return getOptionalExtension(null) != null;
	}
	
	/**
	 * Get the "extension" of the last part of the path. For example, the path
	 * foo/bar/baz.html.jpg.txt has the extension.txt
	 * 
	 * @param default_value The value to return if the path does not have an extension
	 * 
	 * @return the extension of the path (if the path has an extension,
	 *         default_value otherwise)
	 */
	public String getOptionalExtension(String default_value)
	{
		String last_part = getOptionalLastPart(null);
		if ( last_part == null ) return default_value;
		
		int idx = last_part.lastIndexOf('.');
		if ( idx == -1 ) return default_value;
		
		return last_part.substring(idx+1);
	}
	
	/**
	 * Does this path have a parent?
	 * 
	 * @return True if this path has a parent, false otherwise
	 */
	public boolean hasParent()
	{
		return getSimpleNumberOfParts() > 0;
	}
	
	/**
	 * Get the parent of this path
	 * 
	 * @param default_value
	 *            The value to return if this path does not have a parent
	 * @return The parent of this path, or defaul_value if the path does not
	 *         have a parent
	 */
	public S3Path getOptionalParent(S3Path default_value)
	{
		if ( !hasParent() ) return default_value;
		
		StringBuilder parent_path = new StringBuilder();
		
		List<String> parts = getSimpleParts();
		
		for ( int i = 0; i < parts.size()-1; i++ )
		{
			if ( parent_path.length() != 0 )
				parent_path.append("/");
			
			parent_path.append(parts.get(i));
		} 
		
		return new S3Path(parent_path.toString());
	}
	
	
	static public class MyConverter extends Stringable.Converter<S3Path>
	{
		public S3Path fromString(String str, S3Path default_value)
		{
			try
			{
				return new S3Path(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
