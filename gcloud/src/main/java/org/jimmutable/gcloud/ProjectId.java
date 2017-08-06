package org.jimmutable.gcloud;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

import com.google.cloud.ServiceOptions;

public class ProjectId extends Stringable
{
	static public ProjectId CURRENT_PROJECT = new ProjectId(ServiceOptions.getDefaultProjectId());
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	public ProjectId(String code)
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
	
	static public class MyConverter extends Stringable.Converter<ProjectId>
	{
		public ProjectId fromString(String str, ProjectId default_value)
		{
			try
			{
				return new ProjectId(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}
