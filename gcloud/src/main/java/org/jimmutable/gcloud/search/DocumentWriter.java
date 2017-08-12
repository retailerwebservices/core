package org.jimmutable.gcloud.search;

import java.util.HashSet;
import java.util.Set;

import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.utils.Validator;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;

/**
 * A nice utility class for easily creating Google Search API documents
 * 
 * @author kanej
 *
 */
public class DocumentWriter 
{
	private Document.Builder builder;
	
	/**
	 * Create a new DocumentWriter
	 * @param id The document identifier for the new document
	 */
	public DocumentWriter(DocumentId id)
	{
		Validator.notNull(id);
		builder = Document.newBuilder().setId(id.getSimpleValue());
	}
	
	/**
	 * Add a text field to the document. Text fields are tokenized, but do not
	 * support substring searches.
	 * 
	 * @param name
	 *            The name of the field to set
	 * @param text
	 *            The text of the field. Nulls and blanks are ignored.
	 */
	public void writeText(FieldName name, String text)
	{
		Validator.notNull(name);
		
		if ( text == null ) return;
		text = text.trim();
		if (text.length() == 0 ) return;
		
		builder.addField(Field.newBuilder().setName(name.getSimpleName()).setText(text));
	}
	
	/**
	 * Convenience method, equivalent to writeText(def.getSimpleFieldName(), text);
	 * 
	 * @param def
	 *            A field definition
	 * @param text
	 *            The text to write
	 */
	public void writeText(FieldDefinition def, String text)
	{
		Validator.notNull(def);
		writeText(def.getSimpleFieldName(), text);
	}
	
	/**
	 * Add an atomic field to the document. Atoms are either matched (case
	 * insensitive) or not match (no tokenezation, no partial matching). These are
	 * useful for things like enum codes etc.
	 * 
	 * @param name
	 *            The name of the field
	 * @param text
	 *            The (atomic) text. Null and empty strings are simply ignored.
	 */
	public void writeAtom(FieldName name, String text)
	{
		Validator.notNull(name);
		
		if ( text == null ) return;
		text = text.trim();
		if (text.length() == 0 ) return;
		
		builder.addField(Field.newBuilder().setName(name.getSimpleName()).setAtom(text));
	}
	
	/**
	 * Write a number to the search document. DO NOT USE SEARCH DOCUMENTS FOR
	 * PRECISE NUMERICAL STORAGE. View all numerics as "approximate"
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            The value of the field
	 */
	public void writeNumber(FieldName name, double value)
	{
		Validator.notNull(name);
		
		if ( Double.isInfinite(value) || Double.isNaN(value) ) return;
		
		builder.addField(Field.newBuilder().setName(name.getSimpleName()).setNumber(value));
	}
	
	/**
	 * Utility method, equivalent to writeNumber(def.getSimpleFieldName(), value)
	 * 
	 * @param def
	 *            The field definition
	 * @param value
	 *            The value to write
	 */
	public void writeNumber(FieldDefinition def, double value)
	{
		Validator.notNull(def);
		writeNumber(def.getSimpleFieldName(), value);
	}
	
	/**
	 * Write a field which contains tokenized text in such a way as to be suitable
	 * for prefix matching. This means, for example, that if you write ABCDEFG as a
	 * tokenized prefix string, searches like ABC will match (any start to the
	 * string will match).
	 * 
	 * USE THIS FIELD TYPE SPARINGLY, IT IS VERY EXPENSIVE.
	 * 
	 * Use text fields unless absolutely required.
	 * 
	 * Values for this field are limited to 50 characters. Any larger values will be
	 * trimmed to 50 characters.
	 * 
	 * @param name
	 *            The name of the field to write
	 * @param text
	 *            The text to write. Limited to 50 characters. Nulls and blanks are
	 *            simply ignored.
	 */
	public void writeTextWithPrefixMatchingSupport(FieldName name, String text)
	{
		Validator.notNull(name);
		
		if ( text == null ) return;
		text = text.trim();
		if (text.length() == 0 ) return;
		if ( text.length() > 50 ) text = text.substring(0, 50);
		
		builder.addField(Field.newBuilder().setName(name.getSimpleName()).setTokenizedPrefix(text));
	}
	
	/**
	 * Utility method, equivalent to writeTextWithPrefixMatchingSupport(def.getSimpleFieldName(), text)
	 * 
	 * USE THIS FIELD TYPE SPARINGLY, IT IS VERY EXPENSIVE.
	 * 
	 * @param def The name of the field to write
	 * @param text The text to write
	 */
	public void writeTextWithPrefixMatchingSupport(FieldDefinition def, String text)
	{
		Validator.notNull(def);
		writeTextWithPrefixMatchingSupport(def.getSimpleFieldName(), text);
	}
	
	/**
	 * Write a field witch contains un-tokenized text in such a way that it is
	 * suitable for substring matching.
	 * 
	 * USE THIS FIELD SPARINGLY, IT IS *VERY VERY* EXPENSIVE. Try everything you can
	 * to just use writeText or writeTextWithPrefixMatchingSupport.
	 * 
	 * Values for this field are limited to 50 characters. Any larger values will be
	 * trimmed to 50 characters.
	 * 
	 */
	public void writeTextWithSubstringMatchingSupport(FieldName name, String text)
	{
		if ( text == null ) return;
		text = text.toLowerCase().trim();
		
		if ( text.length() == 0 ) return;
		if ( text.length() > 50 ) text = text.substring(0, 50);
		
		writeText(name, SubstringUtils.createSubstringMatchingText(text, 1, 10, null));
	}
	
	/**
	 * Utility method, equivalent to writeTextWithSubstringMatchingSupport(def.getSimpleFieldName(), text)
	 * 
	 * USE THIS FIELD TYPE SPARINGLY, IT IS VERY EXPENSIVE.
	 * 
	 * @param def The name of the field to write
	 * @param text The text to write
	 */
	public void writeTextWithSubstringMatchingSupport(FieldDefinition def, String text)
	{
		Validator.notNull(def);
		writeTextWithSubstringMatchingSupport(def.getSimpleFieldName(), text);
	} 
	
	public Document createDocument()
	{
		return builder.build();
	}
}
