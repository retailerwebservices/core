package org.jimmutable.cloud;

import org.jimmutable.cloud.http.QueryStringKey;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * @author andrew.towe This class exists to help us with the handling of
 *         Developer vs. Production application. Any Developer application will
 *         use the OptionalDevApplicationId
 */
public class ApplicationId extends Stringable
{

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
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.DASH, Validator.LOWERCASE_LETTERS, Validator.NUMBERS);

	}

	/**
	 * @param default_value
	 * @return either the current Application Id for Development or the
	 *         default_value passed in
	 */

	public static ApplicationId getOptionalDevApplicationId(ApplicationId default_value)
	{
		// TODO verify if we just need this
		String devEnvironment = System.getProperty("DEV_APPLICATION_ID");
		if (devEnvironment == null)
		{
			return default_value;
		}
		return new ApplicationId(devEnvironment);
	}

	static public class MyConverter extends Stringable.Converter<ApplicationId>
	{
		public ApplicationId fromString(String str, ApplicationId default_value)
		{
			try
			{
				return new ApplicationId(str);
			} catch (Exception e)
			{
				return default_value;
			}
		}
	}
}
