package org.jimmutable.core.decks;

import org.jimmutable.core.fields.FieldSet;

abstract public class StandardImmutableSetDeck<T extends StandardImmutableSetDeck<T, E>, E> extends StandardImmutableDeck<T, E>
{
	@Override
    abstract public FieldSet<E> getSimpleContents();
}
