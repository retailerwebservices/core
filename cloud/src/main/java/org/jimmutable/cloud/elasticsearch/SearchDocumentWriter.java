package org.jimmutable.cloud.elasticsearch;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldCollection;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.core.objects.common.ObjectId;
import java.util.Collection;
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
	 * Add a String array to a text field within the document. These fields are
	 * analyzed, that is they are passed through an analyzer to convert the strings
	 * into a list of individual terms before being indexed.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param elements
	 *            The text of the field. Must not contain any null elements. Blanks
	 *            are ignored.
	 */
	public void writeTextArray(SearchIndexFieldDefinition search_index_definition, FieldCollection<String> elements)
	{

		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.TEXT))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.TEXT, search_index_definition.getSimpleFieldName()));
		}
		Validator.notNull(elements);
		Validator.containsNoNulls(elements);

		if (elements == null)
			return;
		if (elements.size() == 0)
			return;
		for (String s : elements)
		{
			s = s.trim();
		}
		fields.put(search_index_definition.getSimpleFieldName().getSimpleName(), elements);
	}

	/**
	 * Add a String array to a text field within the document. These fields are not
	 * analyzed, that is they are not passed through an analyzer to convert the
	 * strings into a list of individual terms before being indexed.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param elements
	 *            The text of the field. Must not contain any null elements. Blanks
	 *            are ignored.
	 */
	public void writeAtomArray(SearchIndexFieldDefinition search_index_definition, FieldCollection<String> elements)
	{

		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.ATOM))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.ATOM, search_index_definition.getSimpleFieldName()));
		}
		Validator.notNull(elements);
		Validator.containsNoNulls(elements);

		if (elements == null)
			return;
		if (elements.size() == 0)
			return;
		for (String s : elements)
		{
			s = s.trim();
		}
		fields.put(search_index_definition.getSimpleFieldName().getSimpleName(), elements);
	}

	/**
	 * Add a ObjectId array to a text field within the document. These fields are
	 * not analyzed, that is they are not passed through an analyzer to convert the
	 * strings into a list of individual terms before being indexed.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param elements
	 *            The text of the field. Must not contain any null elements. Blanks
	 *            are ignored.
	 */
	public void writeAtomObjectIdArray(SearchIndexFieldDefinition search_index_definition, FieldCollection<ObjectId> elements)
	{

		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.ATOM))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.ATOM, search_index_definition.getSimpleFieldName()));
		}
		Validator.notNull(elements);
		Validator.containsNoNulls(elements);

		if (elements == null)
			return;
		if (elements.size() == 0)
			return;

		FieldCollection<String> ids = new FieldArrayList<String>();
		for (ObjectId id : elements)
		{
			ids.add(id.getSimpleValue());
		}
		fields.put(search_index_definition.getSimpleFieldName().getSimpleName(), ids);
	}

	/**
	 * Add an array of Longs to a LONG field within the document.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param elements
	 *            The Long elements. Must not contain any null elements.
	 */
	public void writeLongArray(SearchIndexFieldDefinition search_index_definition, FieldCollection<Long> elements)
	{

		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.LONG))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.LONG, search_index_definition.getSimpleFieldName()));
		}
		Validator.notNull(elements);
		Validator.containsNoNulls(elements);

		if (elements == null)
			return;
		if (elements.size() == 0)
			return;

		fields.put(search_index_definition.getSimpleFieldName().getSimpleName(), elements);
	}

	/**
	 * Add an array of Longs to a DAY field within the document.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param elements
	 *            The Long elements. Must not contain any null elements.
	 */
	public void writeTimestampArray(SearchIndexFieldDefinition search_index_definition, FieldCollection<Long> elements)
	{

		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.DAY))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.DAY, search_index_definition.getSimpleFieldName()));
		}
		Validator.notNull(elements);
		Validator.containsNoNulls(elements);

		if (elements == null)
			return;
		if (elements.size() == 0)
			return;

		fields.put(search_index_definition.getSimpleFieldName().getSimpleName(), elements);
	}

	/**
	 * Add an array of Floats to a FLOAT field within the document.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param elements
	 *            The Float elements. Must not contain any null elements.
	 */
	public void writeFloatArray(SearchIndexFieldDefinition search_index_definition, FieldCollection<Float> elements)
	{

		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.FLOAT))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.FLOAT, search_index_definition.getSimpleFieldName()));
		}
		Validator.notNull(elements);
		Validator.containsNoNulls(elements);

		if (elements == null)
			return;
		if (elements.size() == 0)
			return;

		fields.put(search_index_definition.getSimpleFieldName().getSimpleName(), elements);
	}

	/**
	 * Add an array of Boolean to a BOOLEAN field within the document.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param elements
	 *            The Boolean elements. Must not contain any null elements.
	 */
	public void writeBooleanArray(SearchIndexFieldDefinition search_index_definition, FieldCollection<Boolean> elements)
	{

		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.BOOLEAN))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.BOOLEAN, search_index_definition.getSimpleFieldName()));
		}
		Validator.notNull(elements);
		Validator.containsNoNulls(elements);

		if (elements == null)
			return;
		if (elements.size() == 0)
			return;

		fields.put(search_index_definition.getSimpleFieldName().getSimpleName(), elements);
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

		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.TEXT))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.TEXT, search_index_definition.getSimpleFieldName()));
		}

		writeText(search_index_definition.getSimpleFieldName(), text);
	}

	/**
	 * Add a text field to the document. These fields are analyzed, that is they are
	 * passed through an analyzer to convert the string into a list of individual
	 * terms before being indexed.
	 * 
	 * @param field_definition
	 *            The FieldDefinition
	 * @param text
	 *            The text of the field. Nulls and blanks are ignored.
	 */
	public void writeText(FieldDefinition<?> field_definition, String text)
	{
		Validator.notNull(field_definition.getSimpleFieldName());
		writeText(field_definition.getSimpleFieldName(), text);
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
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.ATOM))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.ATOM, search_index_definition.getSimpleFieldName()));
		}
		writeAtom(search_index_definition.getSimpleFieldName(), text);
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
	 * @param field_definition
	 *            The FieldDefinition
	 * @param text
	 *            The (atomic) text. Nulls and blanks are ignored.
	 */
	public void writeAtom(FieldDefinition<?> field_definition, String text)
	{
		Validator.notNull(field_definition.getSimpleFieldName());
		writeAtom(field_definition.getSimpleFieldName(), text);
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
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param value
	 *            true or false
	 */
	public void writeBoolean(SearchIndexFieldDefinition search_index_definition, boolean value)
	{
		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.BOOLEAN))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s for field %s", search_index_definition.getTypeName(), SearchIndexFieldType.BOOLEAN, search_index_definition.getSimpleFieldName()));
		}
		writeBoolean(search_index_definition.getSimpleFieldName(), value);
	}

	/**
	 * Add a true or false datatype to a document
	 * 
	 * @param field_definition
	 *            The FieldDefinition
	 * @param value
	 *            true or false
	 */
	public void writeBoolean(FieldDefinition<?> field_definition, boolean value)
	{
		Validator.notNull(field_definition);
		writeBoolean(field_definition.getSimpleFieldName(), value);
	}

	/**
	 * Writes a timestamp (unix epoch) of type long to a date field type.
	 * 
	 * If the field type is not set to DAY in the SearchIndexFieldDefinition a
	 * runtime exception is thrown
	 * 
	 * You can get the timestamp from the helpful function
	 * System.currentTimeMillis().
	 * 
	 * </br>
	 * Example kibana search:</br>
	 * GET integration:placement-fb-image:v1/default/_search</br>
	 * {</br>
	 * "query": {</br>
	 * "query_string" : {</br>
	 * "query" : "start:[2012-01-01 TO 2017-12-31]"</br>
	 * }</br>
	 * }</br>
	 * }</br>
	 * 
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param value
	 *
	 */
	public void writeTimestamp(SearchIndexFieldDefinition search_index_definition, long value)
	{

		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.DAY))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s", search_index_definition.getTypeName(), SearchIndexFieldType.DAY));
		}
		writeLong(search_index_definition.getSimpleFieldName(), value);
	}

	/**
	 * Add a signed 64-bit integer with a minimum value of -2^63 and a maximum value
	 * of 2^63-1 to a document.
	 * 
	 * 
	 * @param name
	 *            The name of the field
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
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * 
	 * @param value
	 *            the long
	 */
	public void writeLong(SearchIndexFieldDefinition search_index_definition, long value)
	{
		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.LONG))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s", search_index_definition.getTypeName(), SearchIndexFieldType.LONG));
		}
		writeLong(search_index_definition.getSimpleFieldName(), value);
	}

	/**
	 * Add a signed 64-bit integer with a minimum value of -2^63 and a maximum value
	 * of 2^63-1 to a document.
	 * 
	 * 
	 * @param field_definition
	 *            The FieldDefinition
	 * 
	 * @param value
	 *            the long
	 */
	public void writeLong(FieldDefinition<?> field_definition, long value)
	{
		Validator.notNull(field_definition);
		writeLong(field_definition.getSimpleFieldName(), value);
	}

	/**
	 * Add a a single-precision 32-bit IEEE 754 floating point number to a document
	 * 
	 * @param name
	 *            The name of the field
	 * @param value
	 *            the float value
	 */
	public void writeFloat(FieldName name, float value)
	{
		Validator.notNull(name);
		fields.put(name.getSimpleName(), value);
	}

	/**
	 * Add a a single-precision 32-bit IEEE 754 floating point number to a document
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param value
	 *            the float value
	 */
	public void writeFloat(SearchIndexFieldDefinition search_index_definition, float value)
	{
		Validator.notNull(search_index_definition);

		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.FLOAT))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s", search_index_definition.getTypeName(), SearchIndexFieldType.FLOAT));
		}

		writeFloat(search_index_definition.getSimpleFieldName(), value);
	}

	/**
	 * Add a a single-precision 32-bit IEEE 754 floating point number to a document
	 * 
	 * @param field_definition
	 *            The FieldDefinition
	 * @param value
	 *            the float value
	 */
	public void writeFloat(FieldDefinition<?> field_definition, float value)
	{
		Validator.notNull(field_definition);
		writeFloat(field_definition.getSimpleFieldName(), value);
	}

	/**
	 * Add a date datatype to a document. Written in the form yyyy-MM-dd.
	 * 
	 * @param name
	 *            The name of the field
	 * @param day
	 *            the Day value
	 */
	public void writeDay(FieldName name, Day day)
	{
		Validator.notNull(name);

		Validator.notNull(day);

		fields.put(name.getSimpleName(), String.format("%d-%02d-%02d", day.getSimpleYear(), day.getSimpleMonthOfYear(), day.getSimpleDayOfMonth()));

	}

	/**
	 * Add a date datatype to a document. Written in the form yyyy-MM-dd.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param day
	 *            the Day value
	 */
	public void writeDay(SearchIndexFieldDefinition search_index_definition, Day day)
	{
		Validator.notNull(search_index_definition);
		Validator.notNull(day);

		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.DAY))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s", search_index_definition.getTypeName(), SearchIndexFieldType.DAY));
		}

		writeDay(search_index_definition.getSimpleFieldName(), day);
	}

	/**
	 * Add a date datatype to a document. Written in the form yyyy-MM-dd.
	 * 
	 * @param field_definition
	 *            The FieldDefinition
	 * @param day
	 *            the Day value
	 */
	public void writeDay(FieldDefinition<?> field_definition, Day day)
	{
		Validator.notNull(field_definition);
		Validator.notNull(day);

		writeDay(field_definition.getSimpleFieldName(), day);
	}

	// /**
	// * Only writes an ObjectId's simple value. Note: The datatype is controlled at
	// * index creation. If you need ObjectId to be written as an Atom and Text
	// value
	// * then you should have two separate field names already mapped upon index
	// * creation.
	// *
	// * @param name
	// * The name of the field to write
	// * @param id
	// * The ObjectId
	// */
	// public void writeObjectId(FieldName name, ObjectId id)
	// {
	// Validator.notNull(name);
	// Validator.notNull(id);
	//
	// fields.put(name.getSimpleName(), id.getSimpleValue());
	// }

	// /**
	// * Only writes an ObjectId's simple value. Note: The datatype is controlled at
	// * index creation. If you need ObjectId to be written as an Atom and Text
	// value
	// * then you should have two separate field names already mapped upon index
	// * creation.
	// *
	// * @param search_index_definition
	// * The SearchIndexFieldDefinition
	// * @param id
	// * The ObjectId
	// */
	// public void writeObjectId(SearchIndexFieldDefinition search_index_definition,
	// ObjectId id)
	// {
	// Validator.notNull(search_index_definition);
	// Validator.notNull(id);
	//
	// if
	// (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.OBJECTID))
	// {
	// throw new RuntimeException(String.format("Invalid type %s, expected %s",
	// search_index_definition.getTypeName(), SearchIndexFieldType.OBJECTID));
	// }
	//
	// writeObjectId(search_index_definition.getSimpleFieldName(), id);
	// }

	// /**
	// * Only writes an ObjectId's simple value. Note: The datatype is controlled at
	// * index creation. If you need ObjectId to be written as an Atom and Text
	// value
	// * then you should have two separate field names already mapped upon index
	// * creation.
	// *
	// * @param field_definition
	// * The FieldDefinition
	// * @param id
	// * The ObjectId
	// */
	// public void writeObjectId(FieldDefinition<?> field_definition, ObjectId id)
	// {
	// Validator.notNull(field_definition);
	// Validator.notNull(id);
	//
	// writeObjectId(field_definition.getSimpleFieldName(), id);
	// }

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
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param text
	 *            The text to write. Limited to 50 characters. Nulls and blanks are
	 *            simply ignored.
	 */
	public void writeTextWithPrefixMatchingSupport(SearchIndexFieldDefinition search_index_definition, String text)
	{
		Validator.notNull(search_index_definition);
		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.TEXT))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s", search_index_definition.getTypeName(), SearchIndexFieldType.TEXT));
		}
		writeTextWithPrefixMatchingSupport(search_index_definition.getSimpleFieldName(), text);
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
	 * @param field_definition
	 *            The FieldDefinition
	 * @param text
	 *            The text to write. Limited to 50 characters. Nulls and blanks are
	 *            simply ignored.
	 */
	public void writeTextWithPrefixMatchingSupport(FieldDefinition<?> field_definition, String text)
	{
		Validator.notNull(field_definition);
		writeTextWithPrefixMatchingSupport(field_definition.getSimpleFieldName(), text);
	}

	/**
	 * * Write a field witch contains un-tokenized text in such a way that it is
	 * suitable for substring matching.
	 * 
	 * USE THIS FIELD SPARINGLY, IT IS *VERY VERY* EXPENSIVE. Try everything you can
	 * to just use writeText or writeTextWithPrefixMatchingSupport.
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
	 * * Write a field witch contains un-tokenized text in such a way that it is
	 * suitable for substring matching.
	 * 
	 * USE THIS FIELD SPARINGLY, IT IS *VERY VERY* EXPENSIVE. Try everything you can
	 * to just use writeText or writeTextWithPrefixMatchingSupport.
	 * 
	 * Values for this field are limited to 50 characters. Any larger values will be
	 * trimmed to 50 characters.
	 * 
	 * @param search_index_definition
	 *            The SearchIndexFieldDefinition
	 * @param text
	 *            The text to write. Limited to 50 characters. Nulls and blanks are
	 *            simply ignored.
	 */
	public void writeTextWithSubstringMatchingSupport(SearchIndexFieldDefinition search_index_definition, String text)
	{
		Validator.notNull(search_index_definition);

		if (!search_index_definition.getSimpleType().equals(SearchIndexFieldType.TEXT))
		{
			throw new RuntimeException(String.format("Invalid type %s, expected %s", search_index_definition.getTypeName(), SearchIndexFieldType.TEXT));
		}

		writeTextWithSubstringMatchingSupport(search_index_definition.getSimpleFieldName(), text);
	}

	/**
	 * * Write a field witch contains un-tokenized text in such a way that it is
	 * suitable for substring matching.
	 * 
	 * USE THIS FIELD SPARINGLY, IT IS *VERY VERY* EXPENSIVE. Try everything you can
	 * to just use writeText or writeTextWithPrefixMatchingSupport.
	 * 
	 * Values for this field are limited to 50 characters. Any larger values will be
	 * trimmed to 50 characters.
	 * 
	 * @param field_definition
	 *            The FieldDefinition
	 * @param text
	 *            The text to write. Limited to 50 characters. Nulls and blanks are
	 *            simply ignored.
	 */
	public void writeTextWithSubstringMatchingSupport(FieldDefinition<?> field_definition, String text)
	{
		Validator.notNull(field_definition);
		writeTextWithSubstringMatchingSupport(field_definition.getSimpleFieldName(), text);
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
