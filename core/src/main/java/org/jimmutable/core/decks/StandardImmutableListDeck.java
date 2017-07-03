package org.jimmutable.core.decks;

import org.jimmutable.core.fields.FieldList;

abstract public class StandardImmutableListDeck<T extends StandardImmutableListDeck<T, E>, E> extends StandardImmutableDeck<T, E>
{
	@Override
    abstract public FieldList<E> getSimpleContents();
}
