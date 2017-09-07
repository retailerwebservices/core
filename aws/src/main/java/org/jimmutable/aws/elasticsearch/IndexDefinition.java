package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.storage.ApplicationId;

/**
 * Search index definition applicaitonid/indexid
 * 
 * @author trevorbox
 *
 */
public class IndexDefinition extends Stringable
{

	public static final MyConverter CONVERTER = new MyConverter();

	private static final String SEPARATOR = "_";

	public IndexDefinition(ApplicationId applicationId, IndexId indexId)
	{
		super(String.format("%s%s%s", applicationId.getSimpleValue(), SEPARATOR, indexId.getSimpleValue()));
	}

	public IndexDefinition(String value)
	{
		super(value);
	}

	@Override
	public void normalize()
	{

	}

	@Override
	public void validate()
	{

		Validator.notNull(super.getSimpleValue());

		String[] values = super.getSimpleValue().split(SEPARATOR);

		if (values.length != 2) {
			throw new ValidationException(
					String.format("Expected the format applicationId_indexId but the definition was set to %s",
							super.getSimpleValue()));
		}

		// run the validation
		ApplicationId.getOptionalDevApplicationId(new ApplicationId(values[0]));
		new IndexId(values[1]);

	}

	static public class MyConverter extends Stringable.Converter<IndexDefinition>
	{
		public IndexDefinition fromString(String str, IndexDefinition default_value)
		{
			try {
				return new IndexDefinition(str);
			} catch (Exception e) {
				return default_value;
			}
		}
	}

}
