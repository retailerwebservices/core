package org.jimmutable.core.fields;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * An implementation of a {@link Set} that begins life as mutable but can, at
 * any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link Set} that implements {@link Field}.
 * 
 * @author Jim Kane
 *
 * @param <E> The type of elements in this set
 * 
 * @see FieldCollection
 */
abstract public class FieldSet<E> extends FieldCollection<E> implements Set<E>
{
	/**
	 * Default constructor (for an empty set)
	 */
	public FieldSet()
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
	public FieldSet(Iterable<E> objs)
	{
		super(objs);
	}
}
