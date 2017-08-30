package org.jimmutable.gcloud.examples;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jimmutable.core.examples.book.BindingType;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Normalizer;
import org.jimmutable.core.utils.Validator;
import org.jimmutable.gcloud.search.DocumentId;
import org.jimmutable.gcloud.search.DocumentWriter;
import org.jimmutable.gcloud.search.IndexId;
import org.jimmutable.gcloud.search.Indexable;

final public class SearchExampleLibraryPatron extends StandardImmutableObject<SearchExampleLibraryPatron>
		implements Indexable {
	static public final TypeName TYPE_NAME = new TypeName("org.jimmutable.gcloud.examples.SearchExampleLibraryPatron");

	static public final FieldDefinition.Stringable<ObjectId> FIELD_OBJECT_ID = new FieldDefinition.Stringable("id",
			null, ObjectId.CONVERTER);
	static public final FieldDefinition.String FIELD_FIRST_NAME = new FieldDefinition.String("first_name", null);
	static public final FieldDefinition.String FIELD_LAST_NAME = new FieldDefinition.String("last_name", null);
	static public final FieldDefinition.Stringable<Day> FIELD_BIRTH_DATE = new FieldDefinition.Stringable("birth_date",
			null, Day.CONVERTER);
	static public final FieldDefinition.String FIELD_EMAIL_ADDRESS = new FieldDefinition.String("email_address", null);
	static public final FieldDefinition.String FIELD_SSN = new FieldDefinition.String("ssn", null);

	static public final FieldDefinition.Integer FIELD_NUM_BOOKS = new FieldDefinition.Integer(
			"number_of_books_checked_out", -1);

	static public final FieldDefinition.Stringable<IndexId> FIELD_INDEX_ID = new FieldDefinition.Stringable("index_id",
			null, IndexId.CONVERTER);

	static public final FieldDefinition.Stringable<DocumentId> FIELD_DOCUMENT_ID = new FieldDefinition.Stringable(
			"document_id", null, DocumentId.CONVERTER);

	private ObjectId id; // required
	private String first_name; // required
	private String last_name; // required
	private Day birth_date; // optional

	private String email_address; // optional
	private String ssn; // optional
	private Integer number_of_books_checked_out; // required and > 0

	private IndexId index_id; // required
	private DocumentId document_id; // required

	public SearchExampleLibraryPatron(ObjectParseTree t) {
		this.id = t.getStringable(this.FIELD_OBJECT_ID);
		this.first_name = t.getString(this.FIELD_FIRST_NAME);
		this.last_name = t.getString(this.FIELD_LAST_NAME);
		this.email_address = t.getString(this.FIELD_EMAIL_ADDRESS);
		this.ssn = t.getString(this.FIELD_SSN);
		this.number_of_books_checked_out = t.getInt(this.FIELD_NUM_BOOKS);
		this.index_id = t.getStringable(this.FIELD_INDEX_ID);
		this.document_id = t.getStringable(this.FIELD_DOCUMENT_ID);

		this.birth_date = t.getStringable(this.FIELD_BIRTH_DATE);

	}

	@Override
	public IndexId getSimpleSearchIndexId() {
		return index_id;
	}

	@Override
	public DocumentId getSimpleSearchDocumentId() {
		return document_id;
	}

	@Override
	public void writeSearchDocument(DocumentWriter writer) {
		writer.writeText(this.FIELD_FIRST_NAME, this.first_name);
		writer.writeText(this.FIELD_LAST_NAME, this.last_name);
		writer.writeTextWithPrefixMatchingSupport(this.FIELD_EMAIL_ADDRESS, this.email_address);
		writer.writeTextWithSubstringMatchingSupport(this.FIELD_SSN, this.ssn);
		writer.writeNumber(this.FIELD_NUM_BOOKS, this.number_of_books_checked_out);

	}

	@Override
	public int compareTo(SearchExampleLibraryPatron other) {

		int ret = Comparison.startCompare();

		Comparison.continueCompare(ret, this.birth_date, other.getOptionalBirth_date());
		Comparison.continueCompare(ret, this.email_address, other.getOptionalEmail_address());
		Comparison.continueCompare(ret, this.first_name, other.getSimpleFirst_name());
		Comparison.continueCompare(ret, this.id, other.getSimpleId());
		Comparison.continueCompare(ret, this.last_name, other.getSimpleLast_name());
		Comparison.continueCompare(ret, this.number_of_books_checked_out, other.getSimpleNumber_of_books_checked_out());
		Comparison.continueCompare(ret, this.ssn, other.getOptionalSsn());

		Comparison.continueCompare(ret, this.index_id, other.getSimpleSearchIndexId());
		Comparison.continueCompare(ret, this.document_id, other.getSimpleSearchDocumentId());

		return ret;

	}

	@Override
	public TypeName getTypeName() {
		return TYPE_NAME;
	}

	@Override
	public void write(ObjectWriter writer) {
		writer.writeString(this.FIELD_FIRST_NAME, this.first_name);
		writer.writeString(this.FIELD_LAST_NAME, this.last_name);
		writer.writeString(this.FIELD_EMAIL_ADDRESS, this.email_address);
		writer.writeString(this.FIELD_SSN, this.ssn);
		writer.writeInt(this.FIELD_NUM_BOOKS, this.number_of_books_checked_out);
		writer.writeStringable(this.FIELD_BIRTH_DATE, this.birth_date);
		writer.writeStringable(this.FIELD_OBJECT_ID, this.id);
		writer.writeStringable(this.FIELD_DOCUMENT_ID, this.document_id);
		writer.writeStringable(this.FIELD_INDEX_ID, this.index_id);
	}

	@Override
	public void freeze() {
		// no collection to freeze
	}

	@Override
	public void normalize() {
		this.email_address = Normalizer.trim(this.email_address);
		this.first_name = Normalizer.trim(this.first_name);
		this.last_name = Normalizer.trim(this.last_name);
		this.ssn = Normalizer.trim(this.ssn);
	}

	@Override
	public void validate() {

		Validator.notNull(this.id);
		Validator.notNull(this.first_name);
		Validator.notNull(this.last_name);
		Validator.min(this.number_of_books_checked_out, 0);

		Validator.notNull(this.index_id);
		Validator.notNull(this.document_id);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((birth_date == null) ? 0 : birth_date.hashCode());
		result = prime * result + ((document_id == null) ? 0 : document_id.hashCode());
		result = prime * result + ((email_address == null) ? 0 : email_address.hashCode());
		result = prime * result + ((first_name == null) ? 0 : first_name.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((index_id == null) ? 0 : index_id.hashCode());
		result = prime * result + ((last_name == null) ? 0 : last_name.hashCode());
		result = prime * result + ((number_of_books_checked_out == null) ? 0 : number_of_books_checked_out.hashCode());
		result = prime * result + ((ssn == null) ? 0 : ssn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchExampleLibraryPatron other = (SearchExampleLibraryPatron) obj;
		if (birth_date == null) {
			if (other.birth_date != null)
				return false;
		} else if (!birth_date.equals(other.birth_date))
			return false;
		if (document_id == null) {
			if (other.document_id != null)
				return false;
		} else if (!document_id.equals(other.document_id))
			return false;
		if (email_address == null) {
			if (other.email_address != null)
				return false;
		} else if (!email_address.equals(other.email_address))
			return false;
		if (first_name == null) {
			if (other.first_name != null)
				return false;
		} else if (!first_name.equals(other.first_name))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (index_id == null) {
			if (other.index_id != null)
				return false;
		} else if (!index_id.equals(other.index_id))
			return false;
		if (last_name == null) {
			if (other.last_name != null)
				return false;
		} else if (!last_name.equals(other.last_name))
			return false;
		if (number_of_books_checked_out == null) {
			if (other.number_of_books_checked_out != null)
				return false;
		} else if (!number_of_books_checked_out.equals(other.number_of_books_checked_out))
			return false;
		if (ssn == null) {
			if (other.ssn != null)
				return false;
		} else if (!ssn.equals(other.ssn))
			return false;
		return true;
	}

	public ObjectId getSimpleId() {
		return id;
	}

	public String getSimpleFirst_name() {
		return first_name;
	}

	public String getSimpleLast_name() {
		return last_name;
	}

	public Day getOptionalBirth_date() {
		return birth_date;
	}

	public String getOptionalEmail_address() {
		return email_address;
	}

	public String getOptionalSsn() {
		return ssn;
	}

	public Integer getSimpleNumber_of_books_checked_out() {
		return number_of_books_checked_out;
	}
}
