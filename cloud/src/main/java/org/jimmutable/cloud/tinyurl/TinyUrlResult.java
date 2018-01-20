package org.jimmutable.cloud.tinyurl;

import java.util.Objects;

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
import org.jimmutable.core.objects.Builder;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class TinyUrlResult extends StandardImmutableObject<TinyUrlResult> implements Storable, Indexable
{
	static public final Kind KIND = new Kind("TinyUrlResult");
	static public final TypeName TYPE_NAME = new TypeName("com.digitalpanda.objects.tinyurl.TinyUrlResult");
	static public final IndexDefinition INDEX_DEFINITION = new IndexDefinition(CloudExecutionEnvironment.getSimpleCurrent().getSimpleApplicationId(), new IndexId(KIND.getSimpleValue()), new IndexVersion("v1"));
	static public final FieldDefinition.Stringable<ObjectId> FIELD_ID = new FieldDefinition.Stringable<ObjectId>("id", null, ObjectId.CONVERTER);
	static public final FieldDefinition.String FIELD_URL = new FieldDefinition.String("url", null);
	static public final FieldDefinition.String FIELD_TINY_URL = new FieldDefinition.String("tiny_url", null);

	static public final SearchIndexFieldDefinition SEARCH_FIELD_ID = new SearchIndexFieldDefinition(FIELD_ID.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static public final SearchIndexFieldDefinition SEARCH_FIELD_URL = new SearchIndexFieldDefinition(FIELD_URL.getSimpleFieldName(), SearchIndexFieldType.TEXT);
	static public final SearchIndexFieldDefinition SEARCH_FIELD_TINY_URL = new SearchIndexFieldDefinition(FIELD_TINY_URL.getSimpleFieldName(), SearchIndexFieldType.TEXT);

	static public final SearchIndexDefinition INDEX_MAPPING;

	static
	{

		Builder b = new Builder(SearchIndexDefinition.TYPE_NAME);

		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_ID);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_URL);
		b.add(SearchIndexDefinition.FIELD_FIELDS, SEARCH_FIELD_TINY_URL);

		b.set(SearchIndexDefinition.FIELD_INDEX_DEFINITION, INDEX_DEFINITION);

		INDEX_MAPPING = (SearchIndexDefinition) b.create(null);

	}

	private ObjectId id;// (ObjectId, required)
	private String url;// (String, required)
	private String tiny_url; // (String, required)

	public TinyUrlResult( ObjectId id, String url, String tiny_url )
	{
		this.id = id;
		this.url = url;
		this.tiny_url = tiny_url;
		complete();
	}

	public TinyUrlResult( ObjectParseTree o )
	{
		this.id = o.getStringable(FIELD_ID);
		this.url = o.getString(FIELD_URL);
		this.tiny_url = o.getString(FIELD_TINY_URL);
	}

	@Override
	public int compareTo( TinyUrlResult other )
	{
		int ret = Comparison.startCompare();

		ret = Comparison.continueCompare(ret, getSimpleObjectId(), other.getSimpleObjectId());
		ret = Comparison.continueCompare(ret, getSimpleUrl(), other.getSimpleUrl());
		ret = Comparison.continueCompare(ret, getSimpleTinyUrl(), other.getSimpleTinyUrl());

		return ret;
	}

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeStringable(FIELD_ID, getSimpleObjectId());
		writer.writeString(FIELD_URL, getSimpleUrl());
		writer.writeString(FIELD_TINY_URL, getSimpleTinyUrl());
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
		Validator.notNull(id, url, tiny_url);

	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, url, tiny_url);
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof TinyUrlResult) )
			return false;

		TinyUrlResult other = (TinyUrlResult) obj;

		if ( !Objects.equals(getSimpleObjectId(), (other.getSimpleObjectId())) )
		{
			return false;
		}
		if ( !Objects.equals(getSimpleUrl(), other.getSimpleUrl()) )
		{
			return false;
		}

		if ( !Objects.equals(getSimpleTinyUrl(), other.getSimpleTinyUrl()) )
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
	public void writeSearchDocument( SearchDocumentWriter writer )
	{
		writer.writeText(FIELD_ID.getSimpleFieldName(), getSimpleObjectId().getSimpleValue());
		writer.writeText(FIELD_URL.getSimpleFieldName(), getSimpleUrl());
		writer.writeText(FIELD_TINY_URL.getSimpleFieldName(), getSimpleTinyUrl());

	}

	@Override
	public Kind getSimpleKind()
	{
		return KIND;
	}

	@Override
	public ObjectId getSimpleObjectId()
	{
		return id;
	}

	public String getSimpleUrl()
	{
		return url;
	}

	public String getSimpleTinyUrl()
	{
		return tiny_url;
	}
}
