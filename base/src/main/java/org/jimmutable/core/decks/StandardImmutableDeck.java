package org.jimmutable.core.decks;

import java.util.Iterator;

import org.jimmutable.core.fields.FieldCollection;
import org.jimmutable.core.objects.StandardImmutableObject;


abstract public class StandardImmutableDeck<T extends StandardImmutableDeck<T, E>, E> extends StandardImmutableObject<T> implements Iterable<E>
{
    abstract public FieldCollection<E> getSimpleContents();
    
    
    @Override
    public Iterator<E> iterator()
    {
        return getSimpleContents().iterator();
    }
    
    @Override
    public int compareTo(T other)
    {
        return Integer.compare(getSimpleContents().size(), other.getSimpleContents().size());
    }

    @Override
    public void freeze()
    {
        getSimpleContents().freeze();
    }

    @Override
    public int hashCode()
    {
        return getSimpleContents().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (! getClass().isInstance(obj)) return false;
        
        @SuppressWarnings("unchecked")
        StandardImmutableDeck<T, E> other = (StandardImmutableDeck<T, E>) obj;
        
        return getSimpleContents().equals(other.getSimpleContents());
    }
}
