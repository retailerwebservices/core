package org.jimmutable.cloud;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

/**
 * Deployment environment types
 * 
 * @author trevorbox
 *
 */
public enum EnvironmentType implements StandardEnum
{

	DEV("dev"),
	STAGING("staging"),
	PRODUCTION("production"),
	UNKNOWN("unknown");

	static public final MyConverter CONVERTER = new MyConverter();

	private String code;

	private EnvironmentType(String code)
	{
		Validator.notNull(code);
		this.code = Normalizer.lowerCase(code);
	}

	public String getSimpleCode()
	{
		return code;
	}

	public String toString()
	{
		return code;
	}

	static public class MyConverter extends StandardEnum.Converter<EnvironmentType>
	{
		public EnvironmentType fromCode(String code, EnvironmentType default_value)
		{
			if (code == null)
				return default_value;

			for (EnvironmentType t : EnvironmentType.values()) {
				if (t.getSimpleCode().equalsIgnoreCase(code))
					return t;
			}

			return default_value;
		}
	}
}
