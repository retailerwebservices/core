package org.jimmutable.cloud;

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
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.UNDERSCORE, Validator.LOWERCASE_LETTERS, Validator.NUMBERS);

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
