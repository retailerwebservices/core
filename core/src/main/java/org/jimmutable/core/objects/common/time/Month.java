package org.jimmutable.core.objects.common.time;

import java.time.DateTimeException;

import org.jimmutable.core.objects.StandardEnum;
import org.jimmutable.core.utils.Validator;

public enum Month implements StandardEnum
{

	JANUARY("January", 1),

	FEBRUARY("February", 2),

	MARCH("March", 3),

	APRIL("April", 4),

	MAY("May", 5),

	JUNE("June", 6),

	JULY("July", 7),

	AUGUST("August", 8),

	SEPTEMBER("September", 9),

	OCTOBER("October", 10),

	NOVEMBER("November", 11),

	DECEMBER("December", 12);

	static public final MyConverter CONVERTER = new MyConverter();

	private String code;
	private int month_id;

	private Month(String code, int month_id)
	{
		Validator.notNull(code);
		this.code = code;
		this.month_id = month_id;
	}

	public String toString()
	{
		return code;
	}

	public int getSimpleMonthId()
	{
		return month_id;
	}

	@Override
	public String getSimpleCode()
	{
		return code;
	}

	public java.time.Month getComplexJavaMonth(java.time.Month default_value)
	{
		try
		{
			return java.time.Month.of(getSimpleMonthId());
		} catch (DateTimeException e)
		{
			e.printStackTrace();
			return default_value;
		}
	}

	static public class MyConverter extends StandardEnum.Converter<Month>
	{
		public Month fromCode(String code, Month default_value)
		{
			if (code == null)
			{
				return default_value;
			}
			code = code.trim();

			for (Month t : Month.values())
			{
				if (t.getSimpleCode().equalsIgnoreCase(code))
					return t;
			}
			return default_value;
		}

		public Month fromMonthId(int month_id, Month default_value)
		{

			for (Month month : Month.values())
			{
				if (month.getSimpleMonthId() == month_id)
					return month;
			}
			return default_value;
		}
	}

}
