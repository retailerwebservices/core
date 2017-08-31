package org.jimmutable.aws.dev.messaging;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class TopicId extends Stringable{

	public TopicId(String value) {
		super(value);
	}

	@Override
	public void normalize() 
	{
		normalizeTrim();
		normalizeLowerCase();
	}
	
	@Override
	public void validate() 
	{
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.UNDERSCORE,Validator.LOWERCASE_LETTERS,Validator.NUMBERS);

	}

}
