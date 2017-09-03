package jimmutable_aws.messaging.common_messages;

import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.utils.Validator;
/**
 * 
 * @author andrew.towe
 *	This Class is to help us handle all of our queue items
 */

// CODE REVIEW: The javadoc comment for this class is not correct.  This class is a stringable that enforces our limitations on queue id(s)... namely, min of 3 characters, max of 64 characters, a-z, 0-9 and dashes

public class QueueId extends Stringable
{

	public QueueId( String value )
	{
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
		Validator.min(getSimpleValue().length(), 3);
		Validator.max(getSimpleValue().length(), 64);
		Validator.containsOnlyValidCharacters(getSimpleValue(), Validator.UNDERSCORE, Validator.LOWERCASE_LETTERS,
				Validator.NUMBERS); // CODE REVEIW: This is not right.  - are allowed, not _

	}
}
