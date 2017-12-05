package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

public enum SortOrderType implements StandardEnum
{
	ASC("asc"),
	DESC("desc");

	static public final MyConverter CONVERTER = new MyConverter();

	private String code;

	private SortOrderType(String code)
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
	 * Used for converting Strings to SortOrderType
	 * 
	 * @author trevorbox
	 *
	 */
	static public class MyConverter extends StandardEnum.Converter<SortOrderType>
	{
		public SortOrderType fromCode(String code, SortOrderType default_value)
		{
			if (code == null)
				return default_value;

			for (SortOrderType t : SortOrderType.values())
			{
				if (t.getSimpleCode().equalsIgnoreCase(code))
					return t;
			}

			return default_value;
		}
	}
}
