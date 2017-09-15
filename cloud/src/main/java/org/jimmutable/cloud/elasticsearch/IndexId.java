package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * The search index identifier
 * 
 * @author trevorbox
 *
 */
public class IndexId extends Stringable
{

	public static final MyConverter CONVERTER = new MyConverter();

	public IndexId(String value)
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
		Validator.min(super.getSimpleValue().length(), 3);
		Validator.max(super.getSimpleValue().length(), 64);

		Validator.containsOnlyValidCharacters(super.getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.DASH, Validator.NUMBERS);

	}

	/**
	 * Convert a String to an IndexId
	 * 
	 * @author trevorbox
	 *
	 */
	static public class MyConverter extends Stringable.Converter<IndexId>
	{
		public IndexId fromString(String str, IndexId default_value)
		{
			try {
				return new IndexId(str);
			} catch (Exception e) {
				return default_value;
			}
		}
	}

}