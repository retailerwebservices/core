package org.jimmutable.aws.elasticsearch;

import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.utils.Validator;

import org.joda.time.DateTimeZone;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class SearchDocumentWriter
{

	private Map<String, Object> fields;

	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private static final String TIMEZONE = "US/Arizona";

	public SearchDocumentWriter(SearchIndexDefinition index, SearchDocumentId id)
	{
		fields = new HashMap<String, Object>();
	}

	/**
	 * Add a text field to the document. These fields are analyzed, that is they are
	 * passed through an analyzer to convert the string into a list of individual
	 * terms before being indexed.
	 * 
	 * @param name
	 *            The name of the field to set
	 * @param text
	 *            The text of the field. Nulls and blanks are ignored.
	 */
	public void writeText(FieldName name, String text)
	{

		Validator.notNull(name);

		if (text == null)
			return;
		text = text.trim();
		if (text.length() == 0)
			return;

		fields.put(name.getSimpleName(), text);
	}

	/**
	 * Add a text field to the document. These fields are analyzed, that is they are
	 * passed through an analyzer to convert the string into a list of individual
	 * terms before being indexed.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param text
	 *            The text of the field. Nulls and blanks are ignored.
	 */
	public void writeText(SearchIndexFieldDefinition search_index_definition, String text)
	{
		Validator.notNull(search_index_definition.getSimpleFieldName());
		writeText(search_index_definition.getSimpleFieldName(), text);
	}

	/**
	 * A field to index structured content such as email addresses, hostnames,
	 * status codes, zip codes or tags. They are typically used for filtering (Find
	 * me all blog posts where status is published), for sorting, and for
	 * aggregations. Keyword fields are only searchable by their exact value.
	 * 
	 * If you need to index full text content such as email bodies or product
	 * descriptions, it is likely that you should rather use a text field.
	 * 
	 * @param name
	 *            The name of the field
	 * @param text
	 *            The (atomic) text. Nulls and blanks are ignored.
	 */
	public void writeAtom(FieldName name, String text)
	{
		Validator.notNull(name);

		if (text == null)
			return;
		text = text.trim();
		if (text.length() == 0)
			return;
		fields.put(name.getSimpleName(), text);
	}

	/**
	 * A field to index structured content such as email addresses, hostnames,
	 * status codes, zip codes or tags. They are typically used for filtering (Find
	 * me all blog posts where status is published), for sorting, and for
	 * aggregations. Keyword fields are only searchable by their exact value.
	 * 
	 * If you need to index full text content such as email bodies or product
	 * descriptions, it is likely that you should rather use a text field.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param text
	 *            The (atomic) text. Nulls and blanks are ignored.
	 */
	public void writeAtom(SearchIndexFieldDefinition search_index_definition, String text)
	{
		Validator.notNull(search_index_definition.getSimpleFieldName());
		writeAtom(search_index_definition.getSimpleFieldName(), text);
	}

	/**
	 * Add a true or false datatype to a document
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            true or false
	 */
	public void writeBoolean(FieldName name, boolean value)
	{
		Validator.notNull(name);
		fields.put(name.getSimpleName(), value);
	}

	/**
	 * Add a signed 64-bit integer with a minimum value of -2^63 and a maximum value
	 * of 2^63-1 to a document.
	 * 
	 * 
	 * * @param name The name of the field
	 * 
	 * @param value
	 *            the long
	 */
	public void writeLong(FieldName name, long value)
	{
		Validator.notNull(name);
		fields.put(name.getSimpleName(), value);
	}

	/**
	 * Add a a single-precision 32-bit IEEE 754 floating point number to a document
	 * 
	 * @param name
	 * @param value
	 */
	public void writeFloat(FieldName name, float value)
	{
		Validator.notNull(name);
		fields.put(name.getSimpleName(), value);
	}

	/**
	 * Add a date datatype to a document. Written in the form yyyy-MM-dd.
	 * 
	 * @param name
	 * @param day
	 */
	public void writeDay(FieldName name, Day day)
	{
		Validator.notNull(name);

		Validator.notNull(day);

		fields.put(name.getSimpleName(),
				DAY_FORMAT.format(day.createSimpleDate(DateTimeZone.forTimeZone(TimeZone.getTimeZone(TIMEZONE)))));
	}

	/**
	 * Add a
	 * 
	 * @param name
	 * @param id
	 */
	public void writeObjectId(FieldName name, ObjectId id)
	{
		Validator.notNull(name);
		Validator.notNull(id);

		fields.put(name.getSimpleName(), id.getSimpleValue());
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
	// public void writeTextWithPrefixMatchingSupport(FieldName name, String text)
	// {
	// Validator.notNull(name);
	//
	// if ( text == null ) return;
	// text = text.trim();
	// if (text.length() == 0 ) return;
	// if ( text.length() > 50 ) text = text.substring(0, 50);
	// //TODO
	// builder.addField(Field.newBuilder().setName(name.getSimpleName()).setTokenizedPrefix(text));
	// }

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
	// public void writeTextWithSubstringMatchingSupport(FieldName name, String
	// text)
	// {
	// if ( text == null ) return;
	// text = text.toLowerCase().trim();
	//
	// if ( text.length() == 0 ) return;
	// if ( text.length() > 50 ) text = text.substring(0, 50);
	//
	// writeText(name, SubstringUtils.createSubstringMatchingText(text, 1, 10,
	// null));
	// }

}
