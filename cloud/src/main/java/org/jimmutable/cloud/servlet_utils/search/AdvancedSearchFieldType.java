package org.jimmutable.cloud.servlet_utils.search;


import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;

/**
 * This is how we tell if the advancedsearchfield is either a text field or New
 * 
 * @author andrew.towe
 *
 */
public enum AdvancedSearchFieldType implements StandardEnum
{
	TEXT("text"), COMBO_BOX("combo-box"), CHECK_BOX("checkbox");
	static public final AdvancedSearchFieldType.MyConverter CONVERTER = new MyConverter();

	private String code;

	@Override
	public String getSimpleCode()
	{
		return code;
	}

	public String toString()
	{
		return code;
	}

	private AdvancedSearchFieldType( String code )
	{
		Validator.notNull(code);
		this.code = Normalizer.lowerCase(code);
	}

	static public class MyConverter extends StandardEnum.Converter<AdvancedSearchFieldType>
	{
		public AdvancedSearchFieldType fromCode( String code, AdvancedSearchFieldType default_value )
		{
			if ( code == null )
				return default_value;

			for ( AdvancedSearchFieldType t : AdvancedSearchFieldType.values() )
			{
				if ( t.getSimpleCode().equalsIgnoreCase(code) )
					return t;
			}

			return default_value;
		}
	}
}