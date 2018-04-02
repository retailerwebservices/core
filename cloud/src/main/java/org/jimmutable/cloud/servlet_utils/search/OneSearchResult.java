package org.jimmutable.cloud.servlet_utils.search;

import java.util.Collections;
import java.util.Map;

import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.decks.StandardImmutableMapDeck;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ObjectParseTree.OnError;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Validator;

/**
 * This class has been deprecated in favor of OneSearchResultWithTyping
 * 
 * OneSearchResult Represents a single search result
 * 
 * Stores FieldName, String pairs in a HashMap. HashMap can be empty.
 * 
 * @author Preston McCumber
 */
@Deprecated
public class OneSearchResult extends StandardImmutableMapDeck<OneSearchResult, FieldName, String>
{

	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.search.OneSearchResult");

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	static public final FieldDefinition.Map FIELD_RESULT = new FieldDefinition.Map("result", new FieldHashMap<FieldName, String>());

	private FieldMap<FieldName, String> result; // required

	public OneSearchResult()
	{
		this(Collections.emptyMap());
	}

	public OneSearchResult( Map<FieldName, String> result )
	{
		super();

		this.result = new FieldHashMap<>();

		if ( result != null )
		{
			this.result.putAll(result);
		}
		complete();
	}

	public OneSearchResult( ObjectParseTree t )
	{
		this.result = t.getMap(FIELD_RESULT, new FieldHashMap<FieldName, String>(), ReadAs.OBJECT, ReadAs.STRING, OnError.SKIP);
	}

	@Override
	public FieldMap<FieldName, String> getSimpleContents()
	{
		return result;
	}

	public void write( ObjectWriter writer )
	{
		writer.writeMap(FIELD_RESULT, result, WriteAs.OBJECT, WriteAs.STRING);
	}

	public void normalize()
	{
	}

	public void validate()
	{
		Validator.containsOnlyInstancesOf(FieldName.class, String.class, result);
	}
}
