package org.jimmutable.core.examples.book;

import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

/**
 * A class used to represent the "binding" of a book (i.e. paperback, hard
 * cover, etc.)
 * 
 * This class is used to demonstrate the how enums are used in the
 * StandardObject pattern
 * 
 * @author jim.kane
 *
 */
public enum BindingType 
{
	HARD_COVER("hard-cover"),
	PAPER_BACK("paper-back"),
	TRADE_PAPER_BACK("trade-paper-back"),
	UNKNOWN("unknown");
	
	private String code;
	
	private BindingType(String code)
	{
		Validator.notNull(code);
		this.code = Normalizer.lowerCase(code);
	}
	
	static public BindingType fromCode(String code, BindingType default_value)
	{
		if ( code == null ) return default_value;
		
		for ( BindingType t : BindingType.values() )
		{
			if ( t.getSimpleCode().equalsIgnoreCase(code) ) 
				return t;
		}
		
		return default_value;
	}
	
	public String getSimpleCode() { return code; }
	public String toString() { return code; }
}

