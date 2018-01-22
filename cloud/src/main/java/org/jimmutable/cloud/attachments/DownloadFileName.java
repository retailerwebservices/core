package org.jimmutable.cloud.attachments;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class DownloadFileName extends Stringable
{
	static public final MyConverter CONVERTER = new MyConverter();

	public DownloadFileName(String value)
	{
		super(value);
	}

	@Override
	public void normalize()
	{
		normalizeTrim();
		normalizeLowerCase();
		super.setValue(super.getSimpleValue().replaceAll(" ", "_"));
	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.LOWERCASE_LETTERS, Validator.NUMBERS, Validator.DASH, Validator.UNDERSCORE, Validator.DOT);

	}

	static public class MyConverter extends Stringable.Converter<DownloadFileName>
	{
		public DownloadFileName fromString(String str, DownloadFileName default_value)
		{
			try
			{
				return new DownloadFileName(str);
			} catch (Exception e)
			{
				return default_value;
			}
		}
	}

}
