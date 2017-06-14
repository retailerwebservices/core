package org.jimmutable.core.fields;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of a {@link ArrayList} that begins life as mutable but can,
 * at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link ArrayList} that implements {@link Field}.
 * 
 * <p><b>Note:</b> {@link ArrayList}, and consequently {@code FieldArrayList} is
 * not thread safe. This is generally not a concern once "{@link #freeze() frozen}"
 * but if the construction process is multi-threaded, consider
 * FieldCopyOnWriteArrayList.
 * 
 * <p>In the standard case (construction in one thread), {@code FieldArrayList} will
 * work well.
 * 
 * @author Jim Kane
 *
 * @param <E> The type of elements in this list
 * 
 * @see FieldList
 */
final public class FieldArrayList<E> extends FieldList<E>
{
	/**
	 * Default constructor (for an empty list)
	 */
	public FieldArrayList()
	{
		super();
	}
	
	/**
     * Constructs a list containing the elements of the specified {@link Iterable},
     * in the order they are returned by the {@link Iterable#iterator() iterator}.
     *
     * @param objs The {code Iterable} whose elements are to be placed into this list
     * 
     * @throws NullPointerException if the specified {@code Iterable} is {@code null}
	 */
	public FieldArrayList(Iterable<E> objs)
	{
		super(objs);
	}

	@Override
	protected List<E> createNewMutableInstance() 
	{
		return new ArrayList<>();
	}
}
