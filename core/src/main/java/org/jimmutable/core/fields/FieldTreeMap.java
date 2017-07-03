package org.jimmutable.core.fields;

import java.util.Map;
import java.util.TreeMap;

/**
 * An implementation of a {@link TreeMap} that begins life as mutable but can,
 * at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link TreeMap} that implements {@link Field}.
 * 
 * <p><b>Note:</b> {@link TreeMap}, and consequently {@code FieldTreeMap} is
 * not thread safe. This is generally not a concern once "{@link #freeze() frozen}"
 * but if the construction process is multi-threaded, consider
 * {@link FieldConcurrentHashMap} (although you will loose the ordering property).
 * 
 * <p>In the standard case (construction in one thread), {@code FieldTreeMap} will
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
final public class FieldTreeMap<K,V> extends FieldMap<K,V>
{
	/**
	 * Default constructor (for an empty map)
	 */
	public FieldTreeMap()
	{
		super();
	}
	
	/**
     * Constructs a collection containing the elements of the specified {@link Map},
     * in the order they are returned by the {@link Iterable#iterator() iterator}.
     *
     * @param initial_values The {@code Map} whose elements are to be placed into this map
     * 
     * @throws NullPointerException if the specified {@code Map} is {@code null}
	 */
	public FieldTreeMap(Map<K,V> initial_values)
	{
		super(initial_values);
	}
	
	@Override
	protected Map<K, V> createNewMutableInstance() 
	{
		return new TreeMap<>();
	}
}

