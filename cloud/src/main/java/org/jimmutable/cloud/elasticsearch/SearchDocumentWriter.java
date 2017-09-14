package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.utils.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * Create a HashMap<String, Object> of fields for search document upsert
 * 
 * @author trevorbox
 *
 */
public class SearchDocumentWriter
{

	private Map<String, Object> fields;

	public SearchDocumentWriter()
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
	 * Add a true or false datatype to a document
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            true or false
	 */
	public void writeBoolean(SearchIndexFieldDefinition search_index_definition, boolean value)
	{
		Validator.notNull(search_index_definition);
		writeBoolean(search_index_definition.getSimpleFieldName(), value);
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
	 * Add a signed 64-bit integer with a minimum value of -2^63 and a maximum value
	 * of 2^63-1 to a document.
	 * 
	 * 
	 * * @param name The name of the field
	 * 
	 * @param value
	 *            the long
	 */
	public void writeLong(SearchIndexFieldDefinition search_index_definition, long value)
	{
		Validator.notNull(search_index_definition);
		writeLong(search_index_definition.getSimpleFieldName(), value);
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
	 * Add a a single-precision 32-bit IEEE 754 floating point number to a document
	 * 
	 * @param name
	 * @param value
	 */
	public void writeFloat(SearchIndexFieldDefinition search_index_definition, float value)
	{
		Validator.notNull(search_index_definition);
		writeFloat(search_index_definition.getSimpleFieldName(), value);
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

		// fields.put(name.getSimpleName(),
		// DAY_FORMAT.format(day.createSimpleDate(DateTimeZone.forTimeZone(TimeZone.getTimeZone(TIMEZONE)))));
		fields.put(name.getSimpleName(), String.format("%d-%02d-%02d", day.getSimpleYear(), day.getSimpleDayOfMonth(),
				day.getSimpleDayOfMonth()));

	}

	/**
	 * Add a date datatype to a document. Written in the form yyyy-MM-dd.
	 * 
	 * @param name
	 * @param day
	 */
	public void writeDay(SearchIndexFieldDefinition search_index_definition, Day day)
	{
		Validator.notNull(search_index_definition);
		Validator.notNull(day);
		writeDay(search_index_definition.getSimpleFieldName(), day);
	}

	/**
	 * Only writes an ObjectId's simple value. Note: The datatype is controlled at
	 * index creation. If you need ObjectId to be written as an Atom and Text value
	 * then you should have two separate field names already mapped upon index
	 * creation.
	 * 
	 * @param name
	 *            The name of the field to write
	 * @param id
	 *            The ObjectId
	 */
	public void writeObjectId(FieldName name, ObjectId id)
	{
		Validator.notNull(name);
		Validator.notNull(id);

		fields.put(name.getSimpleName(), id.getSimpleValue());
	}

	/**
	 * Only writes an ObjectId's simple value. Note: The datatype is controlled at
	 * index creation. If you need ObjectId to be written as an Atom and Text value
	 * then you should have two separate field names already mapped upon index
	 * creation.
	 * 
	 * @param name
	 *            The name of the field to write
	 * @param id
	 *            The ObjectId
	 */
	public void writeObjectId(SearchIndexFieldDefinition search_index_definition, ObjectId id)
	{
		Validator.notNull(search_index_definition);
		Validator.notNull(id);

		writeObjectId(search_index_definition.getSimpleFieldName(), id);
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

		if (text == null)
			return;
		text = text.trim();
		if (text.length() == 0)
			return;

		writeText(name, SubstringUtils.writeTextWithPrefixMatchingSupport(text, 50, null));

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
	public void writeTextWithPrefixMatchingSupport(SearchIndexFieldDefinition search_index_definition, String text)
	{
		Validator.notNull(search_index_definition);
		writeTextWithPrefixMatchingSupport(search_index_definition.getSimpleFieldName(), text);
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
		Validator.notNull(name);

		if (text == null)
			return;
		text = text.toLowerCase().trim();

		if (text.length() == 0)
			return;
		if (text.length() > 50)
			text = text.substring(0, 50);

		// TODO don't we want up to 50 chars substring?
		writeText(name, SubstringUtils.createSubstringMatchingText(text, 1, 50, null));
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
	public void writeTextWithSubstringMatchingSupport(SearchIndexFieldDefinition search_index_definition, String text)
	{
		Validator.notNull(search_index_definition);
		writeTextWithSubstringMatchingSupport(search_index_definition.getSimpleFieldName(), text);
	}

	/**
	 * Get the written fields map
	 * 
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getSimpleFieldsMap()
	{
		return fields;
	}

}
