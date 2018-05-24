package org.jimmutable.cloud.email;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.EmailAddress;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

public class Email extends StandardObject<Email>
{
	static public final TypeName TYPE_NAME = new TypeName("email");

	static public final FieldDefinition.Stringable<EmailAddress> FIELD_FROM = new FieldDefinition.Stringable<EmailAddress>("from", null, EmailAddress.CONVERTER);
	static public final FieldDefinition.String FIELD_FROM_NAME = new FieldDefinition.String("from_name", null);
	static public final FieldDefinition.Collection FIELD_TO = new FieldDefinition.Collection("to", null);
	static public final FieldDefinition.Collection FIELD_CC = new FieldDefinition.Collection("cc", null);
	static public final FieldDefinition.Collection FIELD_BCC = new FieldDefinition.Collection("bcc", null);
	static public final FieldDefinition.Collection FIELD_REPLY_TO = new FieldDefinition.Collection("reply_to", null);
	static public final FieldDefinition.String FIELD_SUBJECT = new FieldDefinition.String("subject", null);
	static public final FieldDefinition.String FIELD_TEXT_BODY = new FieldDefinition.String("text_body", null);
	static public final FieldDefinition.String FIELD_HTML_BODY = new FieldDefinition.String("html_body", null);

	private EmailAddress from; // required
	private String from_name; // required
	private String subject; // required
	private Set<EmailAddress> to; // required, atleast 1
	private Set<EmailAddress> cc; // optional
	private Set<EmailAddress> bcc; // optional
	private Set<EmailAddress> reply_to; // optional
	private String text_body; // optional
	private String html_body; // optional

	public Email(ObjectParseTree t)
	{
		this.from = t.getStringable(FIELD_FROM);
		this.from_name = t.getString(FIELD_FROM_NAME);
		this.subject = t.getString(FIELD_SUBJECT);
		this.to = t.getCollection(FIELD_TO, new HashSet<EmailAddress>(), EmailAddress.CONVERTER, ObjectParseTree.OnError.THROW_EXCEPTION);
		this.cc = t.getCollection(FIELD_CC, new HashSet<EmailAddress>(), EmailAddress.CONVERTER, ObjectParseTree.OnError.THROW_EXCEPTION);
		this.bcc = t.getCollection(FIELD_BCC, new HashSet<EmailAddress>(), EmailAddress.CONVERTER, ObjectParseTree.OnError.THROW_EXCEPTION);
		this.reply_to = t.getCollection(FIELD_REPLY_TO, new HashSet<EmailAddress>(), EmailAddress.CONVERTER, ObjectParseTree.OnError.THROW_EXCEPTION);
		this.text_body = t.getString(FIELD_TEXT_BODY);
		this.html_body = t.getString(FIELD_HTML_BODY);
	}

	@Override
	public int compareTo(Email other)
	{
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleFrom(), other.getSimpleFrom());
		ret = Comparison.continueCompare(ret, getSimpleFromName(), other.getSimpleFromName());
		ret = Comparison.continueCompare(ret, getSimpleSubject(), other.getSimpleSubject());
		ret = Comparison.continueCompare(ret, getSimpleTo().size(), other.getSimpleTo().size());
		ret = Comparison.continueCompare(ret, getOptionalHtmlBody(null), other.getOptionalHtmlBody(null));
		ret = Comparison.continueCompare(ret, getOptionalTextBody(null), other.getOptionalTextBody(null));
		ret = Comparison.continueCompare(ret, other.getOptionalBcc(null).size(), other.getOptionalBcc(null).size());
		ret = Comparison.continueCompare(ret, other.getOptionalCc(null).size(), other.getOptionalCc(null).size());
		ret = Comparison.continueCompare(ret, other.getOptionalReplyTo(null).size(), other.getOptionalReplyTo(null).size());
		return ret;
	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeStringable(FIELD_FROM, getSimpleFrom());
		writer.writeString(FIELD_FROM_NAME, getSimpleFromName());
		writer.writeString(FIELD_SUBJECT, getSimpleSubject());
		writer.writeCollection(FIELD_TO, getSimpleTo(), WriteAs.STRING);

		if (hasBcc())
		{
			writer.writeCollection(FIELD_BCC, getOptionalBcc(null), WriteAs.STRING);
		}
		if (hasCc())
		{
			writer.writeCollection(FIELD_CC, getOptionalCc(null), WriteAs.STRING);
		}
		if (hasReplyTo())
		{
			writer.writeCollection(FIELD_REPLY_TO, getOptionalReplyTo(null), WriteAs.STRING);
		}

		if (hasHtmlBody())
		{
			writer.writeString(FIELD_HTML_BODY, getOptionalHtmlBody(null));
		}

		if (hasTextBody())
		{
			writer.writeString(FIELD_TEXT_BODY, getOptionalTextBody(null));
		}
	}

	public String getSimpleSource()
	{
		return String.format("%s <%s>", from_name, from.getSimpleValue());
	}

	@Override
	public void normalize()
	{

	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleFrom(), "From");
		Validator.notNull(getSimpleFromName(), "From Name");
		Validator.notNull(getSimpleSubject(), "Subject");
		Validator.notNull(getSimpleTo(), "To");
		Validator.containsNoNulls(getSimpleTo(), "To");
		Validator.min(getSimpleTo().size(), 1, "To");
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getOptionalBcc(null), getOptionalCc(null), getOptionalHtmlBody(null), getOptionalReplyTo(null), getOptionalTextBody(null), getSimpleFrom(), getSimpleFromName(), getSimpleSubject(), getSimpleTo());
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Email))
		{
			return false;
		}

		Email other = (Email) obj;

		if (!Objects.equals(getSimpleFrom(), other.getSimpleFrom()))
		{
			return false;
		}
		if (!Objects.equals(getSimpleFromName(), other.getSimpleFromName()))
		{
			return false;
		}
		if (!Objects.equals(getSimpleSubject(), other.getSimpleSubject()))
		{
			return false;
		}
		if (!Objects.equals(getSimpleTo(), other.getSimpleTo()))
		{
			return false;
		}

		if (!Objects.equals(this.getOptionalBcc(null), other.getOptionalBcc(null)))
		{
			return false;
		}
		if (!Objects.equals(this.getOptionalCc(null), other.getOptionalCc(null)))
		{
			return false;
		}
		if (!Objects.equals(getOptionalHtmlBody(null), other.getOptionalHtmlBody(null)))
		{
			return false;
		}
		if (!Objects.equals(getOptionalReplyTo(null), other.getOptionalReplyTo(null)))
		{
			return false;
		}
		if (!Objects.equals(getOptionalTextBody(null), other.getOptionalTextBody(null)))
		{
			return false;
		}

		return true;
	}

	public EmailAddress getSimpleFrom()
	{
		return from;
	}

	public String getSimpleFromName()
	{
		return from_name;
	}

	public Set<EmailAddress> getSimpleTo()
	{
		return to;
	}

	public boolean hasCc()
	{
		if (getOptionalCc(null) != null)
		{
			if (!getOptionalCc(null).isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	public Set<EmailAddress> getOptionalCc(Set<EmailAddress> default_value)
	{
		return Optional.getOptional(cc, null, default_value);
	}

	public boolean hasBcc()
	{
		if (getOptionalBcc(null) != null)
		{
			if (!getOptionalBcc(null).isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	public Set<EmailAddress> getOptionalBcc(Set<EmailAddress> default_value)
	{
		return Optional.getOptional(bcc, null, default_value);
	}

	public boolean hasReplyTo()
	{
		if (getOptionalReplyTo(null) != null)
		{
			if (!getOptionalReplyTo(null).isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	public Set<EmailAddress> getOptionalReplyTo(Set<EmailAddress> default_value)
	{
		return Optional.getOptional(reply_to, null, default_value);
	}

	public String getSimpleSubject()
	{
		return subject;
	}

	public boolean hasTextBody()
	{
		return getOptionalTextBody(null) != null;
	}

	public String getOptionalTextBody(String default_value)
	{
		return Optional.getOptional(text_body, null, default_value);
	}

	public boolean hasHtmlBody()
	{
		return getOptionalHtmlBody(null) != null;
	}

	public String getOptionalHtmlBody(String default_value)
	{
		return Optional.getOptional(html_body, null, default_value);
	}

}
