package org.jimmutable.gcloud;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class ApplicationId extends Stringable{
	private static String DEV_CURRENT_APPLICATION_ID = null;

	public ApplicationId(String value) {
		super(value);
		String devEnvironment = System.getenv("DEV_APPLICATION_ID");
		if ( devEnvironment != null ) 
		{
			DEV_CURRENT_APPLICATION_ID= devEnvironment;
		}
	}

	@Override
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
	}
	
	@Override
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.UNDERSCORE,Validator.LOWERCASE_LETTERS,Validator.NUMBERS);

	}
	
	public static String getOptionalDevApplicationId(ApplicationId default_value) {
		if(DEV_CURRENT_APPLICATION_ID==null) 
		{
			return default_value.getSimpleValue();
		}
		return DEV_CURRENT_APPLICATION_ID;
	}

	public static boolean hasOptionalDevApplicationId() {
		return DEV_CURRENT_APPLICATION_ID!=null;
	}
}
