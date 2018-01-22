package org.jimmutable.cloud.attachments;

import java.util.Objects;

import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.IndexDefinition;
import org.jimmutable.cloud.elasticsearch.IndexId;
import org.jimmutable.cloud.elasticsearch.IndexVersion;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class AttachmentMetaData extends StandardImmutableObject<AttachmentMetaData> implements Storable
{
	public static final Kind KIND = new Kind("attachmentmetadata");
	static public final TypeName TYPE_NAME = new TypeName("com.digitalpanda.objects.attatchments.attachmentmetadata");
	static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId("attachmentmetadata"), new IndexVersion("v1"));

	static public final FieldDefinition.Stringable<ObjectId> FIELD_OBJECT_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
	static public final FieldDefinition.String FIELD_DESCRIPTION = new FieldDefinition.String("description", null);
	static public final FieldDefinition.Stringable<DownloadFileName> FIELD_FILE_NAME = new FieldDefinition.Stringable<DownloadFileName>("filename", null, DownloadFileName.CONVERTER);
	static public final FieldDefinition.String FIELD_MIME_TYPE = new FieldDefinition.String("mimetype", null);
	static public final FieldDefinition.Long FIELD_LAST_MODIFIED_TIME = new FieldDefinition.Long("lastmodifiedtime", 0l);
	static public final FieldDefinition.Long FIELD_SIZE = new FieldDefinition.Long("size", 0l);

	private ObjectId id;// id (ObjectId, required)
	private String description;// description (String, required, can not be the empty string)
	private DownloadFileName file_name;// file_name (DownloadFileName, required)
	private String mime_type;// mime_type (String, required)
	private long last_modified_time;// last_modified_time (long, required)
	private long size;// size (long, required, the size of the of the attachment in bytes)

	public AttachmentMetaData( ObjectId id, String description, DownloadFileName file_name, String mime_type, long last_modified_time, long size )
	{
		this.id = id;
		this.description = description;
		this.file_name = file_name;
		this.mime_type = mime_type;
		this.last_modified_time = last_modified_time;
		this.size = size;
		complete();
	}

	public AttachmentMetaData( ObjectParseTree t )
	{
		this.id = t.getStringable(FIELD_OBJECT_ID);
		this.description = t.getString(FIELD_DESCRIPTION);
		this.file_name = t.getStringable(FIELD_FILE_NAME);
		this.mime_type = t.getString(FIELD_MIME_TYPE);
		this.last_modified_time = t.getLong(FIELD_LAST_MODIFIED_TIME);
		this.size = t.getLong(FIELD_SIZE);
	}

	@Override
	public int compareTo( AttachmentMetaData other )
	{
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleObjectId(), other.getSimpleObjectId());
		ret = Comparison.continueCompare(ret, getSimpleDescription(), other.getSimpleDescription());
		ret = Comparison.continueCompare(ret, getSimpleFileName(), other.getSimpleFileName());
		ret = Comparison.continueCompare(ret, getSimpleMimeType(), other.getSimpleMimeType());
		ret = Comparison.continueCompare(ret, getSimpleLastModifiedTime(), other.getSimpleLastModifiedTime());
		ret = Comparison.continueCompare(ret, getSimpleSize(), other.getSimpleSize());
		return ret;
		
		/*
		 * CODEREVIEW
		 * Question: What's the usual pattern for comparison? In general, "comparison" means "sorting",
		 * which generally has semantic meaning to humans. For example, when you open File Explorer (whatever it's called in MacOS),
		 * files are listed by file name. So, do you generally spend any time thinking about what the proper "natural"
		 * ordering is for objects, or do you just make sure the method creates a stable, unique order? 
		 * 
		 * Answer: We usually sort things by objectId first (There are exceptions but those we want to specifically in a different order). 
		 * The reason we sort by objectId is because that is how the file is stored in our filing system. Therefore things are usually listed 
		 * in the same order they are in our file system. After that we just try to get some order out of the data we have available. 
		 */
	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeStringable(FIELD_OBJECT_ID.getSimpleFieldName(), getSimpleObjectId());
		writer.writeString(FIELD_DESCRIPTION.getSimpleFieldName(), getSimpleDescription());
		writer.writeObject(FIELD_FILE_NAME.getSimpleFieldName(), getSimpleFileName().getSimpleValue());
		writer.writeString(FIELD_MIME_TYPE.getSimpleFieldName(), getSimpleMimeType());
		writer.writeLong(FIELD_LAST_MODIFIED_TIME.getSimpleFieldName(), getSimpleLastModifiedTime());
		writer.writeLong(FIELD_SIZE.getSimpleFieldName(), getSimpleSize());

	}

	@Override
	public void freeze()
	{

	}

	@Override
	public void normalize()
	{

	}

	@Override
	public void validate()
	{
		Validator.notNull(getSimpleObjectId(), getSimpleDescription(), getSimpleFileName(), getSimpleMimeType(), getSimpleLastModifiedTime(), getSimpleSize());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleObjectId(), getSimpleDescription(), getSimpleFileName(), getSimpleMimeType(), getSimpleLastModifiedTime(), getSimpleSize());
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof AttachmentMetaData) )
			return false;

		AttachmentMetaData other = (AttachmentMetaData) obj;

		if ( !Objects.equals(getSimpleObjectId(), other.getSimpleObjectId()) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleDescription(), other.getSimpleDescription()) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleFileName(), other.getSimpleFileName()) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleMimeType(), other.getSimpleMimeType()) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleLastModifiedTime(), other.getSimpleLastModifiedTime()) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleSize(), other.getSimpleSize()))
		{
			return false;
		}
		
		return true;
	}

	public ObjectId getSimpleObjectId()
	{
		return id;
	}

	public String getSimpleDescription()
	{
		return description;
	}

	public DownloadFileName getSimpleFileName()
	{
		return file_name;
	}

	public String getSimpleMimeType()
	{
		return mime_type;
	}

	public long getSimpleLastModifiedTime()
	{
		return last_modified_time;
	}

	public long getSimpleSize()
	{
		return size;
	}

	@Override
	public Kind getSimpleKind()
	{
		return KIND;
	}

}
