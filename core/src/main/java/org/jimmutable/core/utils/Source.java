package org.jimmutable.core.utils;

abstract public interface Source<T>
{
	public T getNext(T default_value);
}
