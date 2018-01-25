package org.jimmutable.core.utils;

abstract public interface Source<T>
{
	public T getNext(T default_value);
	
	static public class CountSource implements Source<Integer>
	{
		private int count = 0;

		@Override
		public Integer getNext( Integer default_value )
		{
			count++;
			return count; 
		}
	}
}
