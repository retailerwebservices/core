package org.jimmutable.core.fields;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * An implementation of a {@link Set} wrapper around {@link ConcurrentHashMap}
 * ({@link Collections#newSetFromMap(java.util.Map)}) that begins life as mutable
 * but can, at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link ConcurrentHashMap concurrent}
 * {@link Collections#newSetFromMap(java.util.Map) Set} that implements {@link Field}.
 * 
 * <p>Why does one need a thread safe immutable set? Well... if your
 * <em>construction</em> code (when the object is mutable) is threaded then it is
 * <em>much</em> safer to use a concurrent backing store. In Java, it is nigh
 * impossible to guarantee the mutable -> immutable transition in a multi-threaded
 * context absent a concurrent backing store.
 * 
 * <p>In general, however (when the field is being created in single threaded code),
 * just use {@link FieldHashSet}, which is <em>far</em> faster to construct.
 * 
 * @author Jim Kane
 *
 * @param <E> The type of elements in this list
 * 
 * @see FieldSet
 * @see FieldHashSet
 */
final public class FieldConcurrentHashSet<E> extends FieldSet<E>
{
	/**
	 * Default constructor (for an empty set)
	 */
	public FieldConcurrentHashSet()
	{
		super();
	}
	
	/**
     * Constructs a set containing the elements of the specified {@link Iterable}
     *
     * @param objs The {code Iterable} whose elements are to be placed into this set
     * 
     * @throws NullPointerException if the specified {@code Iterable} is {@code null}
	 */
	public FieldConcurrentHashSet(Iterable<E> objs)
	{
		super(objs);
	}
	
	@Override
	protected Set<E> createNewMutableInstance()
	{
		return Collections.newSetFromMap(new ConcurrentHashMap<>());
	}
	
	
}