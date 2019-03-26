package org.jimmutable.cloud.objects;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.IndexId;
import org.jimmutable.cloud.elasticsearch.IndexVersion;
import org.jimmutable.cloud.elasticsearch.Indexable;
import org.jimmutable.cloud.elasticsearch.SearchDocumentId;
import org.jimmutable.cloud.elasticsearch.SearchDocumentWriter;
import org.jimmutable.cloud.elasticsearch.SearchIndexDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldDefinition;
import org.jimmutable.cloud.elasticsearch.SearchIndexFieldType;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldCollection;
import org.jimmutable.core.fields.FieldHashSet;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.objects.common.ObjectReference;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

/**
 * This class is designed to be handle the changes to our standardobjects.
 * 
 * @author andrew.towe
 *
 */
public class StandardChangeLogEntry extends StandardImmutableObject<StandardChangeLogEntry> implements Indexable, Storable
{
	static public final TypeName TYPE_NAME = new TypeName("change_log_entry");

	static public final Kind KIND = new Kind("change-log-entry");

	static public final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
	static public final FieldDefinition.Stringable<ObjectReference> FIELD_SUBJECT = new FieldDefinition.Stringable<ObjectReference>("subject", null, ObjectReference.CONVERTER);
	static public final FieldDefinition.Stringable<ObjectId> FIELD_CHANGE_MADE_BY_USER_ID = new FieldDefinition.Stringable<ObjectId>("changemadebyuserid", null, ObjectId.CONVERTER);
	static public final FieldDefinition.Long FIELD_TIMESTAMP = new FieldDefinition.Long("timestamp", null);
	static public final FieldDefinition.String FIELD_SHORT_DESCRIPTION = new FieldDefinition.String("short_description", "");
	static public final FieldDefinition.String FIELD_COMMENTS = new FieldDefinition.String("comments", null);
	static public final FieldDefinition.Collection FIELD_ATTACHMENTS = new FieldDefinition.Collection("attachments", new FieldArrayList<ObjectId>());
	static public final FieldDefinition.StandardObject FIELD_BEFORE = new FieldDefinition.StandardObject("before_object", null);
	static public final FieldDefinition.StandardObject FIELD_AFTER = new FieldDefinition.StandardObject("after_object", null);

	private ObjectId id;// required
	private ObjectReference subject;// ??maybe
	private long timestamp;// required
	private ObjectId change_made_by_user_id;// required
	private String short_description;// required
	private String comments;// optional
	private FieldList<ObjectId> attachments = new FieldArrayList<>();
	private StandardImmutableObject<?> before;// optional
	private StandardImmutableObject<?> after;// optional

	static public final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.ATOM);
	static public final SearchIndexFieldDefinition SEARCH_FIELD_SUBJECT = new SearchIndexFieldDefinition(FIELD_SUBJECT.getSimpleFieldName(), SearchIndexFieldType.ATOM);

	static public final SearchIndexFieldDefinition SEARCH_FIELD_BEFORE_ID = new SearchIndexFieldDefinition(new FieldName(String.format("%s_id", FIELD_BEFORE.getSimpleFieldName().getSimpleName())), SearchIndexFieldType.ATOM);
	static public final SearchIndexFieldDefinition SEARCH_FIELD_AFTER_ID = new SearchIndexFieldDefinition(new FieldName(String.format("%s_id", FIELD_AFTER.getSimpleFieldName().getSimpleName())), SearchIndexFieldType.ATOM);

	static public final SearchIndexFieldDefinition SEARCH_FIELD_TIMESTAMP = new SearchIndexFieldDefinition(FIELD_TIMESTAMP.getSimpleFieldName(), SearchIndexFieldType.DAY);
	static public final SearchIndexFieldDefinition SEARCH_FIELD_CHANGE_MADE_BY_USER_ID = new SearchIndexFieldDefinition(FIELD_CHANGE_MADE_BY_USER_ID.getSimpleFieldName(), SearchIndexFieldType.ATOM);
	static public final SearchIndexFieldDefinition SEARCH_FIELD_SHORT_DESCRIPTION = new SearchIndexFieldDefinition(FIELD_SHORT_DESCRIPTION.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static public final SearchIndexFieldDefinition SEARCH_FIELD_COMMENTS = new SearchIndexFieldDefinition(FIELD_COMMENTS.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static public final SearchIndexFieldDefinition SEARCH_FIELD_ATTACHMENTS = new SearchIndexFieldDefinition(FIELD_ATTACHMENTS.getSimpleFieldName(), SearchIndexFieldType.ATOM);

	static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("change-log-entry"), new IndexVersion("v1"));

	static public final SearchIndexDefinition INDEX_MAPPING;

	static public final Logger logger = LogManager.getLogger(StandardChangeLogEntry.class);

	static
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ID);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_SUBJECT);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_TIMESTAMP);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_CHANGE_MADE_BY_USER_ID);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_SHORT_DESCRIPTION);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_COMMENTS);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ATTACHMENTS);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_BEFORE_ID);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_AFTER_ID);
		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

		INDEX_MAPPING = (SearchIndexDefinition) b.create(null);

	}

	public StandardChangeLogEntry(ObjectId id, ObjectReference subject, long timestamp, ObjectId change_made_by_user_id, String short_description, String comments, FieldList<ObjectId> attachments, StandardImmutableObject<?> old_object, StandardImmutableObject<?> new_object)
	{
		this.id = id;
		this.subject = subject;
		this.timestamp = timestamp;
		this.change_made_by_user_id = change_made_by_user_id;
		this.short_description = short_description;
		this.comments = comments;
		this.attachments = new FieldArrayList<>(attachments);
		this.before = old_object;
		this.after = new_object;
		complete();
	}

	public StandardChangeLogEntry(ObjectParseTree o)
	{
		this.id = o.getStringable(FIELD_ID);
		this.subject = o.getStringable(FIELD_SUBJECT);
		this.timestamp = o.getLong(FIELD_TIMESTAMP);
		this.change_made_by_user_id = o.getStringable(FIELD_CHANGE_MADE_BY_USER_ID);
		this.short_description = o.getString(FIELD_SHORT_DESCRIPTION);
		this.comments = o.getString(FIELD_COMMENTS);

		// correct
		this.attachments = o.getCollection(FIELD_ATTACHMENTS, new FieldArrayList<ObjectId>(), ObjectId.CONVERTER, ObjectParseTree.OnError.SKIP);

		// wrong, this causes class cast exception when attempting to read it
		// this.attachments = o.getCollection(FIELD_ATTACHMENTS, new
		// FieldArrayList<ObjectId>(), ReadAs.STRING, ObjectParseTree.OnError.SKIP);

		this.before = (StandardImmutableObject<?>) o.getObject(FIELD_BEFORE);
		this.after = (StandardImmutableObject<?>) o.getObject(FIELD_AFTER);
	}

	@Override
	public int compareTo(StandardChangeLogEntry other)
	{
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleTimestamp(), other.getSimpleTimestamp());// want to sort by time
																								// first
		ret = Comparison.continueCompare(ret, getSimpleObjectId(), other.getSimpleObjectId());
		ret = Comparison.continueCompare(ret, getSimpleSubject(), other.getSimpleSubject());
		ret = Comparison.continueCompare(ret, getSimpleShortDescription(), other.getSimpleShortDescription());
		ret = Comparison.continueCompare(ret, getOptionalComments(null), other.getOptionalComments(null));
		ret = Comparison.continueCompare(ret, getSimpleAttachments().size(), other.getSimpleAttachments().size());
		ret = Comparison.continueCompare(ret, getOptionalBefore(null), other.getOptionalBefore(null));
		ret = Comparison.continueCompare(ret, getOptionalAfter(null), other.getOptionalAfter(null));
		return ret;
	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeStringable(FIELD_ID, getSimpleObjectId());
		writer.writeStringable(FIELD_SUBJECT, getSimpleSubject());
		writer.writeLong(FIELD_TIMESTAMP, getSimpleTimestamp());
		writer.writeStringable(FIELD_CHANGE_MADE_BY_USER_ID, getSimpleChangeMadeByUserId());
		writer.writeString(FIELD_SHORT_DESCRIPTION, getSimpleShortDescription());

		if (getOptionalComments(null) != null)
		{
			writer.writeString(FIELD_COMMENTS, getOptionalComments(null));
		}

		writer.writeCollection(FIELD_ATTACHMENTS, getSimpleAttachments(), WriteAs.STRING);

		if (getOptionalBefore(null) != null)
		{
			writer.writeObject(FIELD_BEFORE, getOptionalBefore(null));
		}

		if (getOptionalAfter(null) != null)
		{
			writer.writeObject(FIELD_AFTER, getOptionalAfter(null));
		}

	}

	public String getSimpleShortDescription()
	{
		return short_description;
	}

	public ObjectId getSimpleChangeMadeByUserId()
	{
		return change_made_by_user_id;
	}

	public StandardImmutableObject<?> getOptionalAfter(StandardImmutableObject<?> default_value)
	{
		return Optional.getOptional(after, null, default_value);
	}

	public boolean hasAfter()
	{
		return after == null;
	}

	public StandardImmutableObject<?> getOptionalBefore(StandardImmutableObject<?> default_value)
	{
		return Optional.getOptional(before, null, default_value);
	}

	public boolean hasBefore()
	{
		return before == null;
	}

	public List<ObjectId> getSimpleAttachments()
	{
		return attachments;
	}

	public String getOptionalComments(String default_value)
	{
		return Optional.getOptional(comments, null, default_value);
	}

	public long getSimpleTimestamp()
	{
		return timestamp;
	}

	public ObjectReference getSimpleSubject()
	{
		return subject;
	}

	@Override
	public ObjectId getSimpleObjectId()
	{
		return id;
	}

	@Override
	public void freeze()
	{

		attachments.freeze();

	}

	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{
		Validator.notNull(id, subject, timestamp, change_made_by_user_id, short_description, attachments);
		Validator.containsNoNulls(attachments);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleObjectId(), getSimpleTimestamp(), getSimpleChangeMadeByUserId(), getSimpleShortDescription(), getOptionalComments(null), getSimpleAttachments(), getOptionalBefore(null), getOptionalAfter(null));
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof StandardChangeLogEntry))
			return false;

		StandardChangeLogEntry other = (StandardChangeLogEntry) obj;

		if (!Objects.equals(getSimpleObjectId(), other.getSimpleObjectId()))
		{
			return false;
		}
		if (!Objects.equals(getSimpleSubject(), other.getSimpleSubject()))
		{
			return false;
		}
		if (!Objects.equals(getSimpleTimestamp(), other.getSimpleTimestamp()))
		{
			return false;
		}
		if (!Objects.equals(getSimpleChangeMadeByUserId(), other.getSimpleChangeMadeByUserId()))
		{
			return false;
		}
		if (!Objects.equals(getSimpleShortDescription(), other.getSimpleShortDescription()))
		{
			return false;
		}
		if (!Objects.equals(getOptionalComments(null), other.getOptionalComments(null)))
		{
			return false;
		}

		if (!Objects.equals(getSimpleAttachments().size(), other.getSimpleAttachments().size()))
		{
			return false;
		}

		if (!Objects.equals(getOptionalBefore(null), other.getOptionalBefore(null)))
		{
			return false;
		}
		if (!Objects.equals(getOptionalAfter(null), other.getOptionalAfter(null)))
		{
			return false;
		}

		return true;
	}

	@Override
	public IndexDefinition getSimpleSearchIndexDefinition()
	{
		return INDEX_DEFINITION;
	}

	@Override
	public SearchDocumentId getSimpleSearchDocumentId()
	{
		return new SearchDocumentId(id.getSimpleValue());
	}

	@Override
	public void writeSearchDocument(SearchDocumentWriter writer)
	{
		writer.writeAtom(SEARCH_FIELD_ID, getSimpleObjectId().getSimpleValue());

		writer.writeAtom(SEARCH_FIELD_SUBJECT, getSimpleSubject().getSimpleValue());

		writer.writeTimestamp(SEARCH_FIELD_TIMESTAMP, getSimpleTimestamp());

		writer.writeAtom(SEARCH_FIELD_CHANGE_MADE_BY_USER_ID, getSimpleChangeMadeByUserId().getSimpleValue());

		writer.writeText(SEARCH_FIELD_SHORT_DESCRIPTION, getSimpleShortDescription());

		if (getOptionalComments(null) != null)
		{
			writer.writeText(SEARCH_FIELD_COMMENTS, getOptionalComments(null));
		}

		FieldCollection<ObjectId> attachments = new FieldHashSet<>();
		getSimpleAttachments().forEach(attachment ->
		{
			attachments.add(attachment);
		});

		writer.writeAtomObjectIdArray(SEARCH_FIELD_ATTACHMENTS, attachments);

		StandardImmutableObject<?> before_object = getOptionalBefore(null);

		if (before_object != null)
		{
			if (before_object instanceof Storable)
			{
				writer.writeAtom(SEARCH_FIELD_BEFORE_ID, ((Storable) before_object).getSimpleObjectId().getSimpleValue());
			}
		}

		StandardImmutableObject<?> after_object = getOptionalAfter(null);

		if (after_object != null)
		{
			if (after_object instanceof Storable)
			{
				writer.writeAtom(SEARCH_FIELD_AFTER_ID, ((Storable) after_object).getSimpleObjectId().getSimpleValue());
			}
		}

	}

	@Override
	public Kind getSimpleKind()
	{
		return KIND;
	}

}
