package org.jimmutable.gcloud.search;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.gcloud.ProjectId;
import org.jimmutable.gcloud.search.DocumentId.MyConverter;

import com.google.cloud.ServiceOptions;

public class IndexId extends Stringable
{
	static public ProjectId CURRENT_PROJECT = new ProjectId(ServiceOptions.getDefaultProjectId());
	
	static public final MyConverter CONVERTER = new MyConverter();
	
	public IndexId(String code)
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
	
	static public class MyConverter extends Stringable.Converter<IndexId>
	{
		public IndexId fromString(String str, IndexId default_value)
		{
			try
			{
				return new IndexId(str);
			}
			catch(Exception e)
			{
				return default_value;
			}
		}
	}
}

