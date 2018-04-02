package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

/**
 * Search index field datatypes
 * 
 * @author trevorbox
 *
 */
public enum SearchIndexFieldType implements StandardEnum
{
	ATOM("keyword"),
	TEXT("text"),
	LONG("long"),
	BOOLEAN("boolean"),
	FLOAT("float"),
	DAY("date"),
	INSTANT("text"),
	TIMEOFDAY("text");

	static public final MyConverter CONVERTER = new MyConverter();

	private String code;

	private SearchIndexFieldType(String code)
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
	 * Used for converting Strings to SearchIndexFieldType
	 * 
	 * @author trevorbox
	 *
	 */
	static public class MyConverter extends StandardEnum.Converter<SearchIndexFieldType>
	{
		public SearchIndexFieldType fromCode(String code, SearchIndexFieldType default_value)
		{
			if (code == null)
				return default_value;

			for (SearchIndexFieldType t : SearchIndexFieldType.values())
			{
				if (t.getSimpleCode().equalsIgnoreCase(code))
					return t;
			}

			return default_value;
		}
	}
}
