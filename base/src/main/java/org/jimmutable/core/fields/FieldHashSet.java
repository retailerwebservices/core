package org.jimmutable.core.fields;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of a {@link HashSet} that begins life as mutable but can,
 * at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link HashSet} that implements {@link Field}.
 * 
 * <p><b>Note:</b> {@link HashSet}, and consequently {@code FieldHashSet} is
 * not thread safe. This is generally not a concern once "{@link #freeze() frozen}"
 * but if the construction process is multi-threaded, consider
 * {@link FieldConcurrentHashSet}.
 * 
 * <p>In the standard case (construction in one thread), {@code FieldHashSet} will
 * work well.
 * 
 * @author Jim Kane
 *
 * @param <E> The type of elements in this set
 * 
 * @see FieldSet
 * @see FieldConcurrentHashSet
 */
final public class FieldHashSet<E> extends FieldSet<E>
{
	/**
	 * Default constructor (for an empty set)
	 */
	public FieldHashSet()
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
	public FieldHashSet(Iterable<E> objs)
	{
		super(objs);
	}
	
	@Override
	protected Set<E> createNewMutableInstance()
	{
		return new HashSet<>();
	}
}
