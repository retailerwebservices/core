package org.jimmutable.core.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * An implementation of a {@link List} that begins life as mutable but can, at
 * any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link List} that implements {@link Field}.
 * 
 * @author Jim Kane
 *
 * @param <E> The type of elements in this list
 * 
 * @see FieldCollection
 */
abstract public class FieldList<E> extends FieldCollection<E> implements List<E>
{
	protected final List<E> getContentsAsList() { return (List<E>)getContents(); }
	
	/**
	 * Default constructor (for an empty list)
	 */
	public FieldList()
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
	public FieldList(Iterable<E> objs)
	{
		super(objs);
	}
	
	@Override
	public E get(int index)
	{
		return getContentsAsList().get(index);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) 
	{
		assertNotFrozen();
		return getContentsAsList().addAll(index,c);
	}
	
	@Override
	public E set(int index, E element) 
	{
		assertNotFrozen();
		return getContentsAsList().set(index,element);
	}
	
	@Override
	public void add(int index, E element) 
	{
		assertNotFrozen();
		getContentsAsList().add(index,element);
	}

	@Override
	public E remove(int index) 
	{
		assertNotFrozen();
		return getContentsAsList().remove(index);
	}

	@Override
	public int indexOf(Object o) { return getContentsAsList().indexOf(o); }
	
	@Override
	public int lastIndexOf(Object o) { return getContentsAsList().lastIndexOf(o); }

	@Override
	public ListIterator<E> listIterator() { return new MyListIterator(getContentsAsList().listIterator()); }
	
	@Override
	public ListIterator<E> listIterator(int index) { return new MyListIterator(getContentsAsList().listIterator(index)); }

	@Override
	public FieldList<E> subList(int from_index, int to_index)
	{
		return new FieldArrayList<>(getContentsAsList().subList(from_index, to_index));
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (! (obj instanceof List)) return false;
		
		// (Copied from AbstractList.equals)
		// Using ListIterator behaves will for both sequential and
		// random access lists (where other methods such as get may not)
        ListIterator<E> my_it = listIterator();
        ListIterator<?> other_it = ((List<?>) obj).listIterator();
        
		while (my_it.hasNext() && other_it.hasNext())
		{
			E my_elem = my_it.next();
			Object other_elem = other_it.next();
			
			if (! (my_elem == null ? other_elem == null : my_elem.equals(other_elem)))
			{
				return false;
			}
		}
		
		// Check that the sizes are the same
		return ! (my_it.hasNext() || other_it.hasNext());
	}

	/**
	 * An {@link ListIterator} that enforces {@link FieldCollection#freeze() freeze()}
	 * 
	 * @author Jim Kane
	 */
	private class MyListIterator implements ListIterator<E>
	{
		private ListIterator<E> itr;
		
		public MyListIterator(ListIterator<E> itr)
		{
			this.itr = itr;
		}
		
		public boolean hasNext() { return itr.hasNext(); }
		public E next() { return itr.next(); }

		public boolean hasPrevious() { return itr.hasPrevious(); }
		public E previous() { return itr.previous(); }
		public int nextIndex() { return itr.nextIndex(); }
		public int previousIndex() { return itr.previousIndex(); }

		public void remove() 
		{
			assertNotFrozen();
			itr.remove();
		}

		public void set(E e) 
		{
			assertNotFrozen();
			itr.set(e);
		}

		public void add(E e) 
		{
			assertNotFrozen();
			itr.add(e);
		}
	}
}