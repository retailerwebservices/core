package org.jimmutable.core.fields;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of a {@link HashMap} that begins life as mutable but can,
 * at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link HashMap} that implements {@link Field}.
 * 
 * <p><b>Note:</b> {@link HashMap}, and consequently {@code FieldHashMap} is
 * not thread safe. This is generally not a concern once "{@link #freeze() frozen}"
 * but if the construction process is multi-threaded, consider
 * {@link FieldConcurrentHashMap}.
 * 
 * <p>In the standard case (construction in one thread), {@code FieldHashMap} will
 * work well.
 * 
 * @author Jim Kane
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * 
 * @see FieldMap
 * @see FieldConcurrentHashMap
 */
final public class FieldHashMap<K,V> extends FieldMap<K,V>
{
	/**
	 * Default constructor (for an empty map)
	 */
	public FieldHashMap()
	{
		super();
	}
	
	/**
     * Constructs a collection containing the elements of the specified {@link Map},
     * in the order they are returned by the {@link Iterable#iterator() iterator}.
     *
     * @param objs The {@code Map} whose elements are to be placed into this map
     * 
     * @throws NullPointerException if the specified {@code Map} is {@code null}
	 */
	public FieldHashMap(Map<K,V> initial_values)
	{
		super(initial_values);
	}
	
	@Override
	protected Map<K, V> createNewMutableInstance() 
	{
		return new HashMap<>();
	}
}
