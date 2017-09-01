package org.jimmutable.storage;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
/**
 * @author andrew.towe
 * This class exists to help us with the handling of Developer vs. Production application. 
 * Any Developer application will use the OptionalDevApplicationId
 */
public class ApplicationId extends Stringable
{
	private static final ApplicationId DEV_CURRENT_APPLICATION_ID = createDevCurrentApplicationId();

	public ApplicationId(String value) 
	{
		super(value);
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
		Validator.max(getSimpleValue().length(), 64);
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.UNDERSCORE,Validator.LOWERCASE_LETTERS,Validator.NUMBERS);

	}
	/**
	 * @param default_value
	 * @return either the current Application Id for Development or the default_value passed in 
	 */
	
	public static ApplicationId getOptionalDevApplicationId(ApplicationId default_value) 
	{
		if ( DEV_CURRENT_APPLICATION_ID == null ) 
		{
			return default_value;
		}
		return DEV_CURRENT_APPLICATION_ID;
	}

	/**
	 * @return true if there is a current Development Application Id else false
	 */
	
	public static boolean hasOptionalDevApplicationId() 
	{
		return DEV_CURRENT_APPLICATION_ID!=null;
	}
	
	/**
	 * @return either the Dev Application Id Environmental Variable or null
	 */
	public static ApplicationId createDevCurrentApplicationId() 
	{
		String devEnvironment = System.getenv("DEV_APPLICATION_ID");
		return new ApplicationId(devEnvironment);
	}
}
