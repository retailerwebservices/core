package org.jimmutable.core.objects;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.JavaCodeUtils;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.StandardWritable;
import org.jimmutable.core.utils.Validator;

/**
 * The root for all "transient" objects (essentially a StandardObject without serialization). All data in the framework,
 * whether abstract, final, component, or container, should - directly
 * or indirectly - inherit from {@code TransientObject}.
 * 
 * <p>Transient objects all support:
 * <ul>
 *  <li>Hashing, comparison, equality testing (i.e. can be included in any of the standard Java collection implementations, including {@code Set} and {@code Map})</li>
 *  <li>Standardized normalization of data field values</li>
 *  <li>Standardized validation of data field values</li>
 * </ul>
 * 
 * <p>Extending {@code TransientObject} is kind of like shouting out to the world "hey, I am
 * good, little, normal boy that is well behaved". (But I don't support serialization)
 * 
 * <p>{@code TransientObject}(s) may be mutable. If you wish to create an immutable standard
 * object (by far the most common type) you should extend {@link TransientImmutableObject}.
 * 
 * @author Jim Kane
 *
 * @param <T> The object that is extending {@code TransientObject}. This fixes the typing for
 * 			  serialization, cloning, etc.
 * 
 * @see TransientImmutableObject
 */
abstract public class TransientObject<T extends TransientObject<T>> implements Comparable<T>
{
	/**
	 * Normalize the fields of the object. The idea is to clean up the values of
	 * fields when possible (e.g. all uppercase letters, empty collection).
	 *
	 * <p>It is expected that implementations of {@code normalize} will never throw an
	 * Exception. Work done during normalization should be tolerant of bad vales
	 * (e.g. {@code null}). If there's something wrong with a value, {@link #validate()}
	 * will catch it.
	 * 
	 * <p>In {@link StandardImmutableObject}, the object is guaranteed to be mutable
	 * at the point {@code normalize} is called.
	 * 
	 * <p>{@link #validate()} is called <em>after</em> normalization. The idea is that
	 * you are given a chance to fill in/correct fields before a validation check is made.
	 */
	abstract public void normalize();
	
	/**
	 * Validate the fields of the object. Standard practice is to simply call
	 * the various validation methods in {@link Validator} to do the heavy lifting.
	 * 
	 * @throws ValidationException if something is not "up to snuff"
	 */
	abstract public void validate();
	
	@Override
	abstract public int hashCode();
	
	@Override
	abstract public boolean equals(Object obj);
	
	/**
	 * Declare that an object is ready to use. The practical effect is that
	 * {@link #normalize() normalization} and {@link #validate() validation}
	 * is done on all fields.
	 * 
	 * <p>{@code Complete} must only be called <em>once</em> in an object's life cycle.
	 * Typically, standard object's call {@code complete} at the end of their
	 * standard constructor.
	 * 
	 * <p>When building complicated class hierarchies, the decision of <em>where</em>
	 * to call {@code complete} can become complicated. Why? Well, {@code complete}
	 * can only be called <em>once</em> so you need to call it at the end of the constructor
	 * in the "leaf" classes. Declaring leaf classes {@code final} is a good "heuristic"
	 * way to get started with this in many cases. In more complicated cases, you may
	 * need to add arguments to base class constructors "am I the leaf?" to control
	 * completion.
	 */
	public void complete()
	{
		normalize();
		validate();
	}
	
	abstract public String toString(); // Because serialization is not supported, you need to code your own toString method
}
