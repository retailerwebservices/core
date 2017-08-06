package org.jimmutable.gcloud;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

import com.google.cloud.ServiceOptions;

public class ProjectID extends Stringable
{
	static public ProjectID CURRENT_PROJECT = new ProjectID(ServiceOptions.getDefaultProjectId());
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	public ProjectID(String code)
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
		
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH);
	}
	
	static public class MyConverter extends Stringable.Converter<ProjectID>
	{
		public ProjectID fromString(String str, ProjectID default_value)
		{
			try
			{
				return new ProjectID(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
