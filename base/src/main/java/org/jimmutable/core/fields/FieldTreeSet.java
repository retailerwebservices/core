package org.jimmutable.core.fields;

import java.util.Set;
import java.util.TreeSet;


/**
 * An implementation of a {@link TreeSet} that begins life as mutable but can,
 * at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link TreeSet} that implements {@link Field}.
 * 
 * <p><b>Note:</b> {@link TreeSet}, and consequently {@code FieldTreeSet} is
 * not thread safe. This is generally not a concern once "{@link #freeze() frozen}"
 * but if the construction process is multi-threaded, consider
 * {@link FieldConcurrentSkipListSet}.
 * 
 * <p>In the standard case (construction in one thread), {@code FieldTreeSet} will
 * work well.
 * 
 * @author Jim Kane
 *
 * @param <E> The type of elements in this set
 * 
 * @see FieldSet
 * @see FieldConcurrentSkipListSet
 */
final public class FieldTreeSet<E> extends FieldSet<E>
{
	/**
	 * Default constructor (for an empty set)
	 */
	public FieldTreeSet()
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
	public FieldTreeSet(Iterable<E> objs)
	{
		super(objs);
	}
	
	@Override
	protected Set<E> createNewMutableInstance()
	{
		return new TreeSet<>();
	}
}
