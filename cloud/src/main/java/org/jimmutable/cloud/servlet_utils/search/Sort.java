package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.jimmutable.core.fields.FieldArrayList;
import org.jimmutable.core.fields.FieldList;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * An object that represents the sort for a search. Contains a list of different SortBy objects
 * to iterate over to ultimately sort
 * 
 * @author jon.toy
 *
 */
public class Sort extends StandardImmutableObject<Sort>
{	
	// Used to handle default sorts (an empty sort)
	public static final Sort DEFAULT_SORT = new Sort(new ArrayList<SortBy>());
	
	public static final FieldDefinition.Collection FIELD_SORT_ORDER = new FieldDefinition.Collection("sort_order", new FieldArrayList<SortBy>());

	public static final TypeName TYPE_NAME = new TypeName(Sort.class.getName());
	
	private FieldList<SortBy> sort_order;
	
	public Sort(ObjectParseTree t)
	{
		sort_order = t.getCollection(FIELD_SORT_ORDER, new FieldArrayList<SortBy>(), ReadAs.OBJECT, ObjectParseTree.OnError.SKIP);
	}

	public Sort(Collection<SortBy> fields)
	{
		super();
		this.sort_order = new FieldArrayList<SortBy>(fields);
		complete();
	}
	
	/**
	 * The list of SortBy objects to sort by. Order is important.
	 * 
	 * @return A list of SortBy
	 */
	public List<SortBy> getSimpleSortOrder() 
	{ 
		return sort_order; 
	}

	@Override
	public int compareTo(Sort o) 
	{
		int ret = Comparison.startCompare();

		Comparison.continueCompare(ret, this.getSimpleSortOrder().size(), o.getSimpleSortOrder().size());

		return ret;
	}

	@Override
	public TypeName getTypeName() { return TYPE_NAME; }

	@Override
	public void write(ObjectWriter writer) 
	{
		writer.writeCollection(FIELD_SORT_ORDER, this.getSimpleSortOrder(), WriteAs.OBJECT);		
	}

	@Override
	public void freeze() 
	{
		sort_order.freeze();		
	}

	@Override
	public void normalize() 
	{	
	}

	@Override
	public void validate() 
	{
		Validator.notNull(this.getSimpleSortOrder());
		Validator.containsNoNulls(this.getSimpleSortOrder());
	}

	@Override
	public int hashCode() 
	{
		return Objects.hash(sort_order);
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (!(obj instanceof Sort))
			return false;

		Sort other = (Sort) obj;

		if (!this.getSimpleSortOrder().equals(other.getSimpleSortOrder()))
			return false;

		return true;
	}
}