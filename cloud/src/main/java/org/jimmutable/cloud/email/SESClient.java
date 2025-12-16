package org.jimmutable.cloud.email;

import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.jimmutable.core.utils.Validator;

import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.regions.Region;

/**
 * AWS Simple Email Service
 * 
 * @author trevorbox
 *
 */
public class SESClient implements IEmail
{

	private static final Logger logger = LoggerFactory.getLogger(SESClient.class);

	private SesClient client;

	/**
	 * Just set the -Daws.accessKeyId and -Daws.secretKey properties when running
	 * the JVM to pass the id and secret for the client to work.
	 * 
	 * @see <a href=
	 *      "https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html">Working
	 *      with AWS Credentials</a>
	 * 
	 * @return
	 */
	public static SesClient getClient()
	{
		return SesClient.builder().region(Region.US_WEST_2).build();
	}

	public SESClient(SesClient client)
	{
		this.client = client;
	}

	@Override
	public boolean sendEmail(Email email)
	{
		try
		{
			Validator.notNull(email, "Email");

			Content subject = Content.builder().data(email.getSimpleSubject()).build();

			Body aws_body = Body.builder().build();

			// email can only have one option, text or html, both cannot be set
			if (email.hasTextBody())
			{
				Content textcontent = Content.builder().data(email.getOptionalTextBody(null)).build();
				aws_body = aws_body.toBuilder().text(textcontent).build();
			}
			if (email.hasHtmlBody())
			{
				Content htmlcontent = Content.builder().data(email.getOptionalHtmlBody(null)).build();
				aws_body = aws_body.toBuilder().html(htmlcontent).build();
			}

			Message aws_message = Message.builder()
					.subject(subject)
					.body(aws_body)
					.build();

			Destination dest = Destination.builder().build();

			SendEmailRequest request = SendEmailRequest.builder()
					.source(email.getSimpleSource())
					.build();

			dest.toBuilder().toAddresses(email.getSimpleTo().stream().map(e -> e.getSimpleValue()).collect(Collectors.toSet())).build();

			if (email.hasCc())
			{
				dest.toBuilder().ccAddresses(email.getOptionalCc(null).stream().map(e -> e.getSimpleValue()).collect(Collectors.toSet())).build();
			}

			if (email.hasBcc())
			{
				dest.toBuilder().bccAddresses(email.getOptionalBcc(null).stream().map(e -> e.getSimpleValue()).collect(Collectors.toSet())).build();
			}

			request.toBuilder().destination(dest).message(aws_message).build();

			if (email.hasReplyTo())
			{
				request.toBuilder().replyToAddresses(email.getOptionalReplyTo(null).stream().map(e -> e.getSimpleValue()).collect(Collectors.toSet())).build();
			}

			// Send the email.
			SendEmailResponse result = client.sendEmail(request);
			logger.info(String.format("Sent an email with id: %s", result.messageId()));
			return true;
		} catch (Exception e)
		{
			logger.error("Message rejected!", e);
			return false;
		}
	}

}
