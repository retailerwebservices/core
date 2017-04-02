package org.jimmutable.core.objects;

import org.jimmutable.core.exceptions.ImmutableException;
import org.jimmutable.core.fields.Field;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;

/**
 * An abstract base class for {@link StandardObject standard} objects
 * that want to guarantee immutability.
 * 
 * <p>A standard immutable object may only be modified <em>before</em> it is
 * {@link #complete() complete}.
 * 
 * <p><b>Note:</b> It is important that the normalization code goes into
 * {@link #normalize()}, the validation code into {@link #validate()}, and the
 * freezing code into {@link #freeze()}. The latter is particularly important as
 * deep cloning (and hence building) rely upon the ability to control the timing
 * of these operations. 
 * 
 * @author Jim Kane
 */
abstract public class StandardImmutableObject<T extends StandardImmutableObject<T>> extends StandardObject<T>
{
	transient volatile private boolean is_complete = false;
	
	/**
	 * Make any changes to this object required to make this object
	 * immutable. Frequently the only job of {@code freeze} is to call
	 * {@code freeze()} on any non-primative fields.
	 * 
	 * @see Field#freeze()
	 */
	abstract public void freeze();
	
	/**
	 * Declare that an object is ready to use. In addition to normal
	 * {@link super#complete() completion}, {@link #freeze()} is
	 * called to toggle immutability.
	 * 
	 * <p>{@link #isComplete()} is guaranteed to return {@code true}
	 * after {@code complete} has returned. 
	 * 
	 * <p>{@code complete} can only be called once for
	 * immutable objects. Future invocations will result in an
	 * {@link ImmutableException}.
	 * 
	 * @throws ImmutableException if the object is {@link #isComplete() complete}
	 */
	@Override
	synchronized public void complete()
	{
		assertNotComplete();
		
		super.complete();
		freeze();
		
		is_complete = true;
	}
	
	/**
	 * Test to see if the object is {@link #complete() complete}
	 * 
	 * @throws ImmutableException if the object is {@link #isComplete() complete}
	 */
	public void assertNotComplete()
	{
		if ( is_complete ) 
			throw new ImmutableException("Attempt to modify an object after construction is complete");
	}
	
    /**
     * Returns {@code true} if this object is {@link #complete() complete}.
     *
     * @return {@code true} if this object is {@link #complete() complete}
     */
	public boolean isComplete() { return is_complete; }
	
	public T deepClone()
	{
		if ( this.is_complete )
			return (T)this; // immutable, so, no need to clone...
		
		return super.deepClone();
	}
}
