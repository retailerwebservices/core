package org.jimmutable.core.utils;

import java.util.Objects;

/**
 * Implementation of various convenience methods for working with Optional
 * values
 * 
 * @author jim.kane
 *
 */
public class Optional
{
	static public <T extends Object> T getOptional(T value, T unset_value, T default_value)
	{
		if ( Objects.equals(value, unset_value) ) return default_value;
		return value;
	}
	
	static public byte getOptional(byte value, byte unset_value, byte default_value) { return value != unset_value ? value : default_value; } 
	static public short getOptional(short value, short unset_value, short default_value) { return value != unset_value ? value : default_value; } 
	static public int getOptional(int value, int unset_value, int default_value) { return value != unset_value ? value : default_value; } 
	static public long getOptional(long value, long unset_value, long default_value) { return value != unset_value ? value : default_value; } 
	static public char getOptional(char value, char unset_value, char default_value) { return value != unset_value ? value : default_value; } 
	
	
	static public <T extends Object> boolean has(T value, T unset_value)
	{
		return !Objects.equals(value, unset_value);
	}
	
	static public boolean has(byte value, byte unset_value) { return value != unset_value; }
	static public boolean has(short value, short unset_value) { return value != unset_value; }
	static public boolean has(int value, int unset_value) { return value != unset_value; }
	static public boolean has(long value, long unset_value) { return value != unset_value; }
	static public boolean has(char value, char unset_value) { return value != unset_value; }
}
