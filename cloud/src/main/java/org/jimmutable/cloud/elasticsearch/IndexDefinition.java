package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.cloud.ApplicationId;

/**
 * Search index definition applicaitonid:indexid
 * 
 * @author trevorbox
 *
 */
public class IndexDefinition extends Stringable
{

	public static final MyConverter CONVERTER = new MyConverter();

	private static final String SEPARATOR = ":";

	private transient ApplicationId applicationId;
	private transient IndexId indexId;
	private transient IndexVersion indexVersion;

	public IndexDefinition(ApplicationId applicationId, IndexId indexId, IndexVersion indexVersion)
	{
		super(String.format("%s%s%s%s%s", applicationId.getSimpleValue(), SEPARATOR, indexId.getSimpleValue(), SEPARATOR, indexVersion.getSimpleValue()));
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

		if (values.length != 3) {
			throw new ValidationException(String.format("Expected the format applicationId:indexId:indexVersion but the definition was set to %s",	super.getSimpleValue()));
		}

		// run the validation
		this.applicationId = new ApplicationId(values[0]);
		this.indexId = new IndexId(values[1]);
		this.indexVersion = new IndexVersion(values[2]);

		// Set the value (normalizes everything)
		super.setValue(String.format("%s%s%s%s%s", this.applicationId.getSimpleValue(), SEPARATOR, this.indexId.getSimpleValue(), SEPARATOR, this.indexVersion.getSimpleValue()));

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
