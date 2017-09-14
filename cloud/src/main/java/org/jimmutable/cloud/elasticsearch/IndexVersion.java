package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * Searchindex version
 * 
 * @author trevorbox
 *
 */
public class IndexVersion extends Stringable
{

	public static final MyConverter CONVERTER = new MyConverter();

	public IndexVersion(int version_no)
	{
		super(String.format("v%d", version_no));
	}

	public IndexVersion(String value)
	{
		super(value);
	}

	@Override
	public void normalize()
	{
		super.normalizeTrim();
		super.normalizeLowerCase();
	}

	@Override
	public void validate()
	{
		Validator.notNull(super.getSimpleValue());

		if (!super.getSimpleValue().matches("^v\\d+")) {
			throw new ValidationException(String.format("Version does not match syntax ^v\\d+", super.getSimpleValue()));
		}
	}

	/**
	 * Convert a string to an Index Version
	 * 
	 * @author trevorbox
	 *
	 */
	static public class MyConverter extends Stringable.Converter<IndexVersion>
	{
		public IndexVersion fromString(String str, IndexVersion default_value)
		{
			try {
				return new IndexVersion(str);
			} catch (Exception e) {
				return default_value;
			}
		}
	}

}
