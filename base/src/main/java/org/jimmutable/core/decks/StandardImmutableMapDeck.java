package org.jimmutable.core.decks;

import java.util.Map;

import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.objects.StandardImmutableObject;


abstract public class StandardImmutableMapDeck<T extends StandardImmutableMapDeck<T, K, V>, K, V> extends StandardImmutableObject<T>
{
    abstract public FieldMap<K, V> getSimpleContents();
    abstract public Builder<T, K, V> getBuilder();
    
    
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
        
        StandardImmutableMapDeck<?, ?, ?> other = (StandardImmutableMapDeck<?, ?, ?>) obj;
        
        return getSimpleContents().equals(other.getSimpleContents());
    }
    
    
    public T clonePut(K key, V value)
    {
        Builder<T, K, V> builder = getBuilder();
        builder.getSimpleContents().put(key, value);
        return builder.create();
    }
    
    public T cloneRemove(Object key)
    {
        Builder<T, K, V> builder = getBuilder();
        builder.getSimpleContents().remove(key);
        return builder.create();
    }
    
    public T clonePutAll(Map<? extends K, ? extends V> map)
    {
        Builder<T, K, V> builder = getBuilder();
        builder.getSimpleContents().putAll(map);
        return builder.create();
    }
    
    public T cloneClear()
    {
        Builder<T, K, V> builder = getBuilder();
        builder.getSimpleContents().clear();
        return builder.create();
    }
    
    
    abstract static public class Builder<T extends StandardImmutableMapDeck<T, K, V>, K, V>
    {
        protected T under_construction;
        
        public Builder()
        {
        }
        
        public Builder(T starting_point)
        {
            under_construction = starting_point.deepMutableCloneForBuilder();
        }
        
        public FieldMap<K, V> getSimpleContents()
        {
            return under_construction.getSimpleContents();
        }
        
        public T create()
        {
            return under_construction.deepClone();
        }
    }
}
