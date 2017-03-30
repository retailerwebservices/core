package org.jimmutable.core.decks;

import java.util.Collection;

import org.jimmutable.core.fields.FieldList;

abstract public class StandardImmutableListDeck<T extends StandardImmutableListDeck<T, E>, E> extends StandardImmutableDeck<T, E>
{
	@Override
    abstract public FieldList<E> getSimpleContents();
    
    @Override
    abstract public Builder<T, E> getBuilder();
    
    
    public T cloneAddAll(int index, Collection<? extends E> elements)
    {
        Builder<T, E> builder = getBuilder();
        builder.getSimpleContents().addAll(index, elements);
        return builder.create();
    }
    
    public T cloneSet(int index, E element)
    {
        Builder<T, E> builder = getBuilder();
        builder.getSimpleContents().set(index, element);
        return builder.create();
    }
    
    public T cloneAdd(int index, E element)
    {
        Builder<T, E> builder = getBuilder();
        builder.getSimpleContents().add(index, element);
        return builder.create();
    }
    
    public T cloneRemove(int index)
    {
        Builder<T, E> builder = getBuilder();
        builder.getSimpleContents().remove(index);
        return builder.create();
    }
    
    
    abstract static public class Builder<T extends StandardImmutableListDeck<T, E>, E> extends StandardImmutableDeck.Builder<T, E>
    {
        public Builder()
        {
            super();
        }
        
        public Builder(T starting_point)
        {
            super(starting_point);
        }
        
        @Override
        public FieldList<E> getSimpleContents()
        {
            return under_construction.getSimpleContents();
        }
    }
}
