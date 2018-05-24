package org.jimmutable.cloud.email;

public class EmailStub implements IEmail
{
	private static final String ERROR_MESSAGE = "This should have never been called for unit testing, use a different implementation for integration testing!";

	@Override
	public boolean sendEmail(Email email)
	{
		throw new RuntimeException(ERROR_MESSAGE);
	}

}
