package org.jimmutable.gcloud.search;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.gcloud.ProjectId;

import com.google.cloud.ServiceOptions;

public class DocumentId extends Stringable
{
	static public ProjectId CURRENT_PROJECT = new ProjectId(ServiceOptions.getDefaultProjectId());
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	public DocumentId(String code)
	{
		super(code);
	}

	
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
	}

	
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		Validator.max(getSimpleValue().length(), 64);
		
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
	}
	
	static public class MyConverter extends Stringable.Converter<DocumentId>
	{
		public DocumentId fromString(String str, DocumentId default_value)
		{
			try
			{
				return new DocumentId(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}

