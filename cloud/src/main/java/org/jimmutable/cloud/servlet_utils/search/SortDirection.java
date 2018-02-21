package org.jimmutable.cloud.servlet_utils.search;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

/**
 * The direction used for sorting search results (Ascending, Descending)
 * 
 * @author jon.toy
 *
 */
public enum SortDirection implements StandardEnum
{
	ASCENDING("ascending"),
	DESCENDING("descending");

	static public final MyConverter CONVERTER = new MyConverter();

	private String code;

	private SortDirection(String code)
	{
		Validator.notNull(code);
		this.code = Normalizer.lowerCase(code);
	}

	/**
	 * The enum String representation
	 */
	public String getSimpleCode()
	{
		return code;
	}

	/**
	 * The enum String representation
	 */
	public String toString()
	{
		return code;
	}

	/**
	 * Used for converting Strings to SortDirection
	 * 
	 * @author jon.toy
	 *
	 */
	static public class MyConverter extends StandardEnum.Converter<SortDirection>
	{
		public SortDirection fromCode(String code, SortDirection default_value)
		{
			if (code == null)
				return default_value;

			for (SortDirection t : SortDirection.values())
			{
				if (t.getSimpleCode().equalsIgnoreCase(code))
					return t;
			}

			return default_value;
		}
	}
}