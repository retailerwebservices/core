package org.jimmutable.aws.elasticsearch;

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
	// CODE REVEIW: each on seperate line
	ATOM("keyword"), TEXT("text"), LONG("long"), BOOLEAN("boolean"), FLOAT("float"), DAY("date");

	static public final MyConverter CONVERTER = new MyConverter();

	private String code;

	private SearchIndexFieldType(String code)
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

	static public class MyConverter extends StandardEnum.Converter<SearchIndexFieldType>
	{
		public SearchIndexFieldType fromCode(String code, SearchIndexFieldType default_value)
		{
			if (code == null)
				return default_value;

			for (SearchIndexFieldType t : SearchIndexFieldType.values()) {
				if (t.getSimpleCode().equalsIgnoreCase(code))
					return t;
			}

			return default_value;
		}
	}
}
