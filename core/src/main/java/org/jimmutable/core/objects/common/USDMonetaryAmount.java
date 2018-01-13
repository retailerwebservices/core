package org.jimmutable.core.objects.common;

import java.util.Objects;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class USDMonetaryAmount extends StandardImmutableObject<USDMonetaryAmount>
{
	static public final TypeName TYPE_NAME = new TypeName("com.digitalpanda.objects.facebookads.USDMonetaryAmount");

	static public final FieldDefinition.Long FIELD_AMOUNT_IN_CENTS = new FieldDefinition.Long("amount_in_cents", 0l);
	static public final FieldDefinition.String FIELD_TEXT = new FieldDefinition.String("text", "");

	private long amount_in_cents;

	public USDMonetaryAmount( long amount_in_cents )
	{
		this.amount_in_cents = amount_in_cents;
		complete();
	}

	public USDMonetaryAmount( String dollar_value )
	{
		Validator.containsOnlyValidCharacters(dollar_value, Validator.MONEY_SYMBOLS);
		this.amount_in_cents = USDMonetaryAmount.convertFromStringToLong(dollar_value);
		complete();
	}

	public USDMonetaryAmount( ObjectParseTree t )
	{
		this.amount_in_cents = t.getLong(FIELD_AMOUNT_IN_CENTS);
	}

	@Override
	public int compareTo( USDMonetaryAmount other )
	{
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleAmountInCents(), other.getSimpleAmountInCents());
		return ret;
	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		/*
		 * CODEREVIEW Why do you serialize both versions of the amount? The only stored
		 * field is amount_in_cents... so FIELD_TEXT is completely interchangeable,
		 * right? Am I missing something? -JMD
		 * 
		 * When you show the value on the app, it is really nice to have already done this calculation and simply make a 
		 */
		writer.writeLong(FIELD_AMOUNT_IN_CENTS, getSimpleAmountInCents());
		writer.writeString(FIELD_TEXT, USDMonetaryAmount.convertFromLongToString(getSimpleAmountInCents()));
	}

	@Override
	public void freeze()
	{
	}


	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{
		
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(amount_in_cents);
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof USDMonetaryAmount) )
			return false;

		USDMonetaryAmount other = (USDMonetaryAmount) obj;

		if ( !Objects.equals(getSimpleAmountInCents(), other.getSimpleAmountInCents()) )
		{
			return false;
		}
		return true;
	}

	public long getSimpleAmountInCents()
	{
		return amount_in_cents;
	}

	public String toString()
	{
		return convertFromLongToString(amount_in_cents);
	}

	public static String convertFromLongToString( long l )
	{
		String negative = "";
		if(l<0l) {
			negative ="-";
			l = Math.abs(l);
		}
		if ( l < 10l )
		{
			return negative +"$.0" + l;
		}
		String s = "" +l;
		String dollars = s.substring(0, s.length() - 2);
		String cents = s.substring(s.length() - 2, s.length());

		StringBuilder str = new StringBuilder(dollars);
		int idx = str.length() - 3;

		while ( idx > 0 )
		{
			str.insert(idx, ",");
			idx = idx - 3;
		}

		return negative +"$" + str.toString() + "." + cents;
	}

	public static Long convertFromStringToLong( String s )
	{
		s = s.replace("$", "");

		s = s.replaceAll(",", "");
		String[] parts = s.split("\\.");
		if ( parts.length>1 )
		{
			String otherpart = (parts[1] + "00").substring(0, 2);
			s = parts[0] + otherpart;
		}
		else
		{
			s = s + "00";
		}

		return Long.valueOf(s);
	}
}
