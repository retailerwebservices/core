package org.jimmutable.core.fields;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jimmutable.core.exceptions.ImmutableException;


/**
 * An implementation of a {@link Collection} that begins life as mutable but
 * can, at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link Collection} that implements {@link Field}.
 * 
 * <p>
 * This class is designed to be extended. Most of the <em>collection
 * hierarchy</em> is already wrapped as part of the standard <em>jimmutable</em>
 * library, but further extensions should go quickly as the base class does
 * nearly all of the work. However, extension implementors should take time to
 * carefully understand the immutability principles involved and to write
 * careful unit tests to make sure that the implementations are as strictly
 * immutable as possible.
 * 
 * Null keys and/or values are not allowed (attempts to put either will result
 * in no action being taken)
 * 
 * @author Jim Kane
 *
 * @param <K>
 *            the type of keys maintained by this map
 * @param <V>
 *            the type of mapped values
 */
abstract public class FieldMap<K,V> implements Map<K,V>, Field
{
	//TODO Trevor
	 public void assertNotFrozen() 
		{
			if (isFrozen())
				throw new ImmutableException();
		}
	transient volatile private boolean is_frozen;
	
	/*
	 * Never access _contents_ directly.
	 * Use getContents so that SubList (and future) inheritance works
	 * correctly.
	 */
	
	private Map<K,V> contents = createNewMutableInstance();
	
	/**
	 * Get the mutable contents of the {@link Map} that this object wraps.
	 * 
	 * <p>This is the main interface between the {@link Field} specification
	 * that this implementation enforces and the {@link Map} that it wraps.
	 * 
	 * @return The <em>mutable</em> map that this object wraps
	 */
	protected Map<K,V> getContents() { return contents; }

	/**
	 * Instantiate a <em>new</em>, <em>mutable</em> {@link Map}.
	 * This allows sub-classes to control the {@link Map} implementation
	 * that is used (e.g. {@link HashMap}, {@link TreeMap}, etc.).
	 *  
	 * @return The new {@link Map} instance
	 */
	abstract protected Map<K,V> createNewMutableInstance();
	
	
	/**
	 * Default constructor (for an empty map)
	 */
	public FieldMap()
	{
		is_frozen = false;
	}
	
	/**
     * Constructs a collection containing the elements of the specified {@link Map},
     * in the order they are returned by the {@link Iterable#iterator() iterator}.
     *
     * @param initial_values The {@code Map} whose elements are to be placed into this map
     * 
     * @throws NullPointerException if the specified {@code Map} is {@code null}
	 */
	public FieldMap(Map<K,V> initial_values)
	{
		this();
		
		if ( initial_values != null )
		{
			for ( Map.Entry<K, V> e : initial_values.entrySet() )
			{
				put(e.getKey(),e.getValue());
			}
		}
	}
	
	@Override
	public void freeze() { is_frozen = true; }
	
	@Override
	public boolean isFrozen()  { return is_frozen; }
	
	@Override
	public int size() { return getContents().size(); }
	
	@Override
	public boolean isEmpty() { return getContents().isEmpty(); }
	
	@Override
	public boolean containsKey(Object key) { return getContents().containsKey(key); }
	
	@Override
	public boolean containsValue(Object value) { return getContents().containsValue(value); }
	
	@Override
	public V get(Object key) { return getContents().get(key); }

	@Override
	public V put(K key, V value)
	{
		assertNotFrozen();
		if ( key == null ) return null;
		if ( value == null ) return null;
		return getContents().put(key, value);
	}
	
	@Override
	public V remove(Object key)
	{
		assertNotFrozen();
		return getContents().remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m)
	{
		assertNotFrozen();
		
		for ( Map.Entry e : m.entrySet() )
		{
			put((K)e.getKey(),(V)e.getValue());
		}
	}
	
	@Override
	public void clear()
	{
		assertNotFrozen();
		getContents().clear();
	}
	
	@Override
	public Set<K> keySet()
	{
		return new InnerKeySet<K>();
	}

	@Override
	public Collection<V> values()
	{
		return new InnerValueCollection<V>();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet()
	{
		return new InnerEntrySet<Map.Entry<K, V>>();
	}

	@Override
	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof Map) ) return false;
		
		Map<?, ?> other = (Map<?, ?>)obj;
		
		if ( size() != other.size() ) return false;
		
		return entrySet().equals(other.entrySet());
	}
	
	@Override
	public String toString() 
	{
		return getContents().toString();
	}
	
	private class InnerKeySet<E> extends FieldSet<E>
	{
		protected Set<E> createNewMutableInstance() 
		{
			return (Set<E>)(FieldMap.this.getContents().keySet());
		}
		
		@Override
		public void freeze()
		{
			FieldMap.this.freeze();
		}

		@Override
		public boolean isFrozen()
		{
			return FieldMap.this.isFrozen();
		}
	}
	
	private class InnerEntrySet<E> extends FieldSet<E>
	{
		protected Set<E> createNewMutableInstance() 
		{
			return (Set<E>)(FieldMap.this.getContents().entrySet());
		}
		
		@Override
		public void freeze()
		{
			FieldMap.this.freeze();
		}

		@Override
		public boolean isFrozen()
		{
			return FieldMap.this.isFrozen();
		}
	}
	
	private class InnerValueCollection<E> extends FieldCollection<E>
	{
		protected Collection<E> createNewMutableInstance() 
		{
			return (Collection<E>)(FieldMap.this.getContents().values());
		}
		
		@Override
		public void freeze()
		{
			FieldMap.this.freeze();
		}

		@Override
		public boolean isFrozen()
		{
			return FieldMap.this.isFrozen();
		}
	}
}
