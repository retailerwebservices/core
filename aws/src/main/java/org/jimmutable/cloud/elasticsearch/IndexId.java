package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

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

		char chars[] = getSimpleValue().toCharArray();
		for (char ch : chars)
		{
			if (ch >= 'a' && ch <= 'z')
				continue;
			if (ch >= '0' && ch <= '9')
				continue;
			if (ch == '-')
				continue;

			throw new ValidationException(String.format(
					"Illegal character \'%c\' in %s.  Only lower case letters, numbers, and dashed are allowed", ch,
					super.getSimpleValue()));
		}

	}

	static public class MyConverter extends Stringable.Converter<IndexId>
	{
		public IndexId fromString(String str, IndexId default_value)
		{
			try
			{
				return new IndexId(str);
			} catch (Exception e)
			{
				return default_value;
			}
		}
	}

}
