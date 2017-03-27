package org.jimmutable.core.fields;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * An implementation of a {@link FieldConcurrentHashMap} that begins life as mutable
 * but can, at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link FieldConcurrentHashMap} that implements {@link Field}.
 * 
 * <p>Why does one need a thread safe immutable list? Well... if your
 * <em>construction</em> code (when the object is mutable) is threaded then it is
 * <em>much</em> safer to use a concurrent backing store. In Java, it is nigh
 * impossible to guarantee the mutable -> immutable transition in a multi-threaded
 * context absent a concurrent backing store.
 * 
 * <p>In general, however (when the field is being created in single threaded code),
 * just use {@link FieldHashMap}, which is <em>far</em> faster to construct.
 * 
 * @author Jim Kane
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * 
 * @see FieldMap
 * @see FieldHashMap
 */
final public class FieldConcurrentHashMap <K,V> extends FieldMap<K,V>
{
	/**
	 * Default constructor (for an empty map)
	 */
	public FieldConcurrentHashMap()
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
	public FieldConcurrentHashMap(Map<K,V> initial_values)
	{
		super(initial_values);
	}
	
	@Override
	protected Map<K, V> createNewMutableInstance() 
	{
		return new ConcurrentHashMap<>();
	}
}
