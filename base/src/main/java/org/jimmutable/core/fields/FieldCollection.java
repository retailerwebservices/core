package org.jimmutable.core.fields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;



/**
 * An implementation of a {@link Collection} that begins life as mutable but can,
 * at any time, be "{@link #freeze() frozen}" (made immutable). In other
 * words, a wrapper for a {@link Collection} that implements {@link Field}.
 * 
 * <p>What's hard about that? If you have to ask... ;)
 * 
 * <p>Java has a number of really quirky mutability issues... For example, think
 * about the following code:
 * <pre><code>
 * List<String> my_list = new ArrayList();
 * Iterator<String> itr = my_list.iterator();
 *  
 * my_list.add("foo");
 * my_list.add("bar");
 *  
 * my_list = Collections.unmodifiableList(my_list);
 *  
 * itr.remove();
 * </code></pre>
 * 
 * <p>Believe it or not, this code <b>removes {@code foo}</b>.
 *  
 * <p>So, immutable coder beware... and use {@code FieldCollection} (or one of
 * its sub-classes)!
 * 
 * <p>This class is designed to be extended. Most of the <em>collection hierarchy</em>
 * is already wrapped as part of the standard <em>jimmutable</em> library, but further
 * extensions should go quickly as the base class does nearly all of the work. However,
 * extension implementors should take time to carefully understand the immutability
 * principles involved and to write careful unit tests to make sure that the implementations
 * are as strictly immutable as possible.
 * 
 * Null(s) are not allowed in fields (they are skipped when added)
 * 
 * @author Jim Kane
 *
 * @param <E> The type of elements in this collection
 */
abstract public class FieldCollection<E> implements Field, Collection<E> 
{
	transient volatile private boolean is_frozen;
	
	/*
	 * Never access _contents_ directly.
	 */
	private Collection<E> contents;
	
	/**
	 * Default constructor (for an empty collection)
	 */
	public FieldCollection()
	{
		contents = createNewMutableInstance();
		is_frozen = false;
	}
	
	/**
     * Constructs a collection containing the elements of the specified {@link Iterable},
     * in the order they are returned by the {@link Iterable#iterator() iterator}.
     *
     * @param objs The {code Iterable} whose elements are to be placed into this collection
     * 
     * @throws NullPointerException if the specified {@code Iterable} is {@code null}
	 */
	public FieldCollection(Iterable<E> objs)
	{
		this();
		
		if ( objs != null )
		{
			for ( E obj : objs )
			{
				add(obj);
			}
		}
	}
	
	@Override
	public void freeze() { is_frozen = true; }
	
	@Override
	public boolean isFrozen()  { return is_frozen; }

	/**
	 * Get the mutable contents of the {@link Collection} that this object
	 * wraps.
	 * 
	 * <p>This is the main interface between the {@link Field} specification
	 * that this implementation enforces and the {@link Collection} that
	 * it wraps.
	 * 
	 * @return The <em>mutable</em> collection that this object wraps
	 */
	final protected Collection<E> getContents() { return contents; }
	
	@Override
	public int size() { return getContents().size(); }
	
	@Override
	public boolean isEmpty() { return getContents().isEmpty(); }
	
	@Override
	public boolean contains(Object o) { return getContents().contains(o); }
	
	@Override
	public Object[] toArray() { return getContents().toArray(); }
	
	@Override
	public <T> T[] toArray(T[] a) { return getContents().toArray(a); }
	
	@Override
	public boolean containsAll(Collection<?> c) { return getContents().containsAll(c); }
	
	@Override
	public boolean add(E e)
	{
		assertNotFrozen();
		if ( e == null ) return false;
		return getContents().add(e);
	}
	
	@Override
	public boolean remove(Object o)
	{
		assertNotFrozen();
		return getContents().remove(o);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c)
	{
		assertNotFrozen();
		
		for ( E obj : c )
		{
			add(obj);
		}
		
		return true;
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		assertNotFrozen();
		return getContents().retainAll(c);
	}
	
	@Override
	public boolean removeAll(Collection<?> c)
	{
		assertNotFrozen();
		return getContents().removeAll(c);
	}
	
	@Override
	public void clear()
	{
		assertNotFrozen();
		getContents().clear();
	}
	
	@Override
	public int hashCode() 
	{
		return getContents().hashCode();
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (!(obj instanceof Collection) ) return false;
		
		Collection<?> other = (Collection<?>)obj;
		
		if ( size() != other.size() ) return false;
		
		return containsAll(other);
	}

	@Override
	public String toString() 
	{
		return contents.toString();
	}

	@Override
	public Iterator<E> iterator()
	{
		return new MyIterator();
	}
	
	/**
	 * An {@link Iterator} that enforces {@link FieldCollection#freeze() freeze()}
	 * 
	 * @author Jim Kane
	 */
	private class MyIterator implements Iterator<E>
	{
		private Iterator<E> itr;
		
		public MyIterator()
		{
			itr = getContents().iterator();
		}

		public boolean hasNext()
		{
			return itr.hasNext();
		}

		public E next()
		{
			return itr.next();
		}

		public void remove()
		{
			assertNotFrozen();
			itr.remove();
		}
	}
	
	
	
	/**
	 * Instantiate a <em>new</em>, <em>mutable</em> {@link Collection}.
	 * This allows sub-classes to control the {@link Collection} implementation
	 * that is used (e.g. {@link ArrayList}, {@link LinkedList}, {@link HashSet}, etc.).
	 *  
	 * @return The new {@link Collection} instance
	 */
	abstract protected Collection<E> createNewMutableInstance();
}

