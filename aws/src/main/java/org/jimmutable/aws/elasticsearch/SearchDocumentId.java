package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

/**
 * Search document identifier
 * 
 * @author trevorbox
 *
 */
public class SearchDocumentId extends Stringable
{
	public static final MyConverter CONVERTER = new MyConverter();

	public SearchDocumentId(String value)
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
		Validator.min(super.getSimpleValue().length(), 1);
		Validator.max(super.getSimpleValue().length(), 256);
		Validator.containsOnlyValidCharacters(super.getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.DASH,
				Validator.NUMBERS);

	}

	static public class MyConverter extends Stringable.Converter<SearchDocumentId>
	{
		public SearchDocumentId fromString(String str, SearchDocumentId default_value)
		{
			try {
				return new SearchDocumentId(str);
			} catch (Exception e) {
				return default_value;
			}
		}
	}
}
