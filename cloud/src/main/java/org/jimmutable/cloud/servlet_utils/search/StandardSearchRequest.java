package org.jimmutable.cloud.servlet_utils.search;

import java.util.Objects;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * StandardSearchRequest Container for passing general search information
 * 
 * @author Preston McCumber Sep 5, 2017
 */
public class StandardSearchRequest extends StandardImmutableObject<StandardSearchRequest>
{

	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.search.StandardSearchRequest");

	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	static public int DEFAULT_MAX_RESULTS = 100;
	static public int DEFAULT_START_RESULTS_AFTER = 0;

	static public final FieldDefinition.String FIELD_QUERY_STRING = new FieldDefinition.String("query", "");
	static public final FieldDefinition.Integer FIELD_MAX_RESULTS = new FieldDefinition.Integer("max_results", DEFAULT_MAX_RESULTS);
	static public final FieldDefinition.Integer FIELD_START_RESULTS_AFTER = new FieldDefinition.Integer("start_results_after", DEFAULT_START_RESULTS_AFTER);
	static public final FieldDefinition.StandardObject FIELD_SORT = new FieldDefinition.StandardObject("sort", Sort.DEFAULT_SORT);

	private String query_string; // required
	private int max_results; // required
	private int start_results_after; // required
	private Sort sort; // required

	public StandardSearchRequest( ObjectParseTree t )
	{
		this.query_string = t.getString(FIELD_QUERY_STRING);
		this.max_results = t.getInt(FIELD_MAX_RESULTS);
		this.start_results_after = t.getInt(FIELD_START_RESULTS_AFTER);
		this.sort = (Sort) t.getObject(FIELD_SORT);
	}

	public StandardSearchRequest( String query_string, int max_results, int start_results_after, Sort sort )
	{
		this.query_string = query_string;
		this.max_results = max_results;
		this.start_results_after = start_results_after;
		this.sort = sort;

		complete();
	}
	
	public StandardSearchRequest( String query_string, int max_results, int start_results_after )
	{
		this(query_string, max_results, start_results_after, Sort.DEFAULT_SORT);
	}

	public StandardSearchRequest( String query_string )
	{
		this(query_string, DEFAULT_MAX_RESULTS, DEFAULT_START_RESULTS_AFTER, Sort.DEFAULT_SORT);
	}

	public String getSimpleQueryString()
	{
		return query_string;
	}

	public int getSimpleMaxResults()
	{
		return max_results;
	}

	public int getSimpleStartResultsAfter()
	{
		return start_results_after;
	}
	
	public Sort getSimpleSort()
	{
		return sort;
	}

	@Override
	public int compareTo( StandardSearchRequest obj )
	{
		StandardSearchRequest other = (StandardSearchRequest) obj;
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, getSimpleQueryString(), other.getSimpleQueryString());
		ret = Comparison.continueCompare(ret, getSimpleMaxResults(), other.getSimpleMaxResults());
		ret = Comparison.continueCompare(ret, getSimpleStartResultsAfter(), getSimpleStartResultsAfter());
		ret = Comparison.continueCompare(ret, getSimpleSort(), getSimpleSort());
		return ret;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeString(FIELD_QUERY_STRING, getSimpleQueryString());
		writer.writeInt(FIELD_MAX_RESULTS, getSimpleMaxResults());
		writer.writeInt(FIELD_START_RESULTS_AFTER, getSimpleStartResultsAfter());
		writer.writeObject(FIELD_SORT, getSimpleSort());
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
		Validator.notNull(query_string);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getSimpleQueryString(), getSimpleMaxResults(), getSimpleStartResultsAfter(), getSimpleSort());
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( !(obj instanceof StandardSearchRequest) )
			return false;

		StandardSearchRequest other = (StandardSearchRequest) obj;
		if ( !Objects.equals(getSimpleQueryString(), other.getSimpleQueryString()) )
			return false;
		if ( getSimpleMaxResults() != other.getSimpleMaxResults() )
			return false;
		if ( getSimpleStartResultsAfter() != other.getSimpleStartResultsAfter() )
			return false;
		if ( getSimpleSort() != other.getSimpleSort() )
			return false;

		return true;
	}

}
