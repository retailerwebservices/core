package org.jimmutable.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class provides a number of methods designed to make comparison easier.
 * 
 * Chief among this is a solution to the "chained" comparison problem. Chained
 * comparison is when you have several fields that "cascade" upon equality.
 * 
 * By way of example, imagine you have a simple class, Person, with three
 * fields, first_name, last_name and SSN. One way to compare instances of Person
 * would be to first compare by last name, then first name, then SSN. With the
 * Comparison class this is easy:
 * 
 * int ret = startCompare();
 * 
 * ret = continueCompare(ret, getSimpleLastName(),
 * other_person.getSimpleLastName());
 * 
 * ret = continueCompare(ret, getSimpleFirstName(),
 * other_person.getSimpleFirstName());
 * 
 * ret = continueCompare(ret, getOptionalSSN(null),
 * other_person.getOptionalSSN(null));
 * 
 * return ret;
 * 
 * continueCompare is very fast (no object creation) and supports nulls (nulls
 * sort last by default). Convience methods for primatives (avoiding object
 * creation) are provided.
 * 
 * @author jim.kane
 *
 */
public class Comparison 
{
	static public Comparator NULL_FIRST = new NullFirstComparator();
	static public Comparator NULL_LAST = new NullLastComparator();
	
	/**
	 * Start a chained comparison.  
	 * 
	 * Typically, this is used in a line like: int ret = startCompare();
	 * 
	 * @return 0
	 */
	static public int startCompare() { return 0; }
	
	/**
	 * Continue a chained comparison. If a prior comparison in the chain "broke
	 * the tie" then continuing the comparison does nothing. If, on the other
	 * hand, the comparison is still "tied" then the objects are compared to try
	 * and break the tie.
	 * 
	 * Nulls are fully supported
	 * 
	 * @param ret
	 *            The status of the chain comparison after this comparison
	 * @param one
	 *            The first object to compare
	 * @param two
	 *            The second object to compare 
	 *            
	 * @return The result of the chained comparison
	 */
	static public int continueCompare(int ret, Comparable one, Comparable two)
	{
		return continueCompare(ret, one, two, NULL_LAST);
	}
	
	/**
	 * Continue a chained comparison. If a prior comparison in the chain "broke
	 * the tie" then continuing the comparison does nothing. If, on the other
	 * hand, the comparison is still "tied" then the objects are compared to try
	 * and break the tie.
	 * 
	 * Nulls are fully supported. This version of the function allows you to
	 * control the ordering of nulls (first or last) by passing in
	 * Comparison.NULL_FIRST or Comparison.NULL_LAST
	 * 
	 * @param ret
	 *            The status of the chain comparison after this comparison
	 * @param one
	 *            The first object to compare
	 * @param two
	 *            The second object to compare
	 * 
	 * @return The result of the chained comparison
	 */
	static public int continueCompare(int ret, Comparable one, Comparable two, Comparator null_comparator)
	{
		if ( null_comparator == null ) null_comparator = NULL_LAST;
		
		if ( ret != 0 ) return ret; // only do the compare if a preceding compare has not resolved the whole comparison
		
		// Do we need to use the null comparator?
		if ( one == null || two == null ) return null_comparator.compare(one,two);
		
		// actually do the compare
		return one.compareTo(two);
	}
	
	static public int continueCompare(int ret, boolean one, boolean two)
	{
		if ( ret != 0 ) return ret;
		return Boolean.compare(one, two);
	}
	
	static public int continueCompare(int ret, char one, char two)
	{
		if ( ret != 0 ) return ret;
		return Character.compare(one, two);
	}
	
	static public int continueCompare(int ret, byte one, byte two)
	{
		if ( ret != 0 ) return ret;
		return Byte.compare(one, two);
	}
	
	static public int continueCompare(int ret, short one, short two)
	{
		if ( ret != 0 ) return ret;
		return Short.compare(one, two);
	}
	
	static public int continueCompare(int ret, int one, int two)
	{
		if ( ret != 0 ) return ret;
		return Integer.compare(one, two);
	}
	
	static public int continueCompare(int ret, long one, long two)
	{
		if ( ret != 0 ) return ret;
		return Long.compare(one, two);
	}
	
	static public int continueCompare(int ret, float one, float two)
	{
		if ( ret != 0 ) return ret;
		return Float.compare(one, two);
	}
	
	static public int continueCompare(int ret, double one, double two)
	{
		if ( ret != 0 ) return ret;
		return Double.compare(one, two);
	}
	
	static private class NullFirstComparator implements Comparator
	{
		public int compare(Object o1, Object o2) 
		{
			if ( o1 == null && o2 == null ) return 0;
			return o1 == null ? -1 : 1;
		}
	}
	
	static private class NullLastComparator implements Comparator
	{
		public int compare(Object o1, Object o2) 
		{
			if ( o1 == null && o2 == null ) return 0;
			return o1 == null ? 1 : -1;
		}
	}
}
