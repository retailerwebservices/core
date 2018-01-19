package org.jimmutable.core.utils;

public interface Sink<T>
{
	public void onEmit(T value);
}
