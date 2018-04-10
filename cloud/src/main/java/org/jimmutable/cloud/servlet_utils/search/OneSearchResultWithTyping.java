package org.jimmutable.cloud.servlet_utils.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jimmutable.core.fields.FieldHashMap;
import org.jimmutable.core.fields.FieldMap;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Day;
import org.jimmutable.core.objects.common.time.Instant;
import org.jimmutable.core.objects.common.time.TimeOfDay;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.reader.ReadAs;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.serialization.writer.WriteAs;
import org.jimmutable.core.utils.Validator;

/**
 * OneSearchResultWithTyping Represents a single search result. An iteration on the former OneSearchResult,
 * this class allows for readAs functionality that supports typing.
 * 
 * Stores FieldName, String pairs in a HashMap. HashMap can be empty.
 * 
 * @author jon.toy
 *
 */
public class OneSearchResultWithTyping extends StandardImmutableObject<OneSearchResultWithTyping>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.aws.servlet_utils.search.OneSearchResultWithTyping");

	@Override
	public TypeName getTypeName()
	{
		return TYPE_NAME;
	}

	static public final FieldDefinition.Map FIELD_RESULT = new FieldDefinition.Map("result", new FieldHashMap<FieldName, String[]>());

	private FieldMap<FieldName, String[]> result; // required

	public OneSearchResultWithTyping()
	{
		this(Collections.emptyMap());
	}

	public OneSearchResultWithTyping( Map<FieldName, String[]> result )
	{
		super();

		this.result = new FieldHashMap<>();

		if ( result != null )
		{
			this.result.putAll(result);
		}
		complete();
	}

	public OneSearchResultWithTyping( ObjectParseTree t )
	{
		FieldHashMap<FieldName, String[]> map = new FieldHashMap<FieldName, String[]>();
		
		for ( ObjectParseTree entry : t )
		{
			if ( entry.getSimpleFieldName().equals(FIELD_RESULT.getSimpleFieldName()) ) 
			{
				ObjectParseTree key_tree = entry.findChild(FieldName.FIELD_KEY, null);
				ObjectParseTree value_tree = entry.findChild(FieldName.FIELD_VALUE, null);
				
				if ( key_tree == null || value_tree == null ) continue;
				
				FieldName key = (FieldName) ReadAs.OBJECT.readAs(key_tree);
				String value = (String) ReadAs.STRING.readAs(value_tree);
				
				if ( key == null || value == null ) continue;
				
				String[] array_val = map.get(key);
				if ( array_val == null ) array_val = new String[0];
				String[] new_array_val = new String[array_val.length+1];
				
				int i = 0;
				for ( i = 0; i < array_val.length; i++ )
				{
					new_array_val[i] = array_val[i];
				}
				
				new_array_val[i] = String.valueOf(value);
				
				map.put(key, new_array_val);
			}
		}
		
		this.result = map;
	}

	/**
	 * Unlike version 1, the underlying map result is not accessible
	 * 
	 * @return
	 */
	private FieldMap<FieldName, String[]> getSimpleContents()
	{
		return result;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		writer.writeMap(FIELD_RESULT, result, WriteAs.OBJECT, WriteAs.STRING);
	}

	@Override
	public void normalize()
	{
	}

	@Override
	public void validate()
	{
		Validator.containsOnlyInstancesOfValueStringArray(FieldName.class, String.class, result);
	}
	
	@Override
    public int compareTo(OneSearchResultWithTyping other)
    {
        return Integer.compare(getSimpleContents().size(), other.getSimpleContents().size());
    }

    @Override
    public void freeze()
    {
        getSimpleContents().freeze();
    }

    @Override
    public int hashCode()
    {
        return getSimpleContents().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (! getClass().isInstance(obj)) return false;
        
        OneSearchResultWithTyping other = (OneSearchResultWithTyping) obj;
        
        return getSimpleContents().equals(other.getSimpleContents());
    }
    
    /**
     * Used when we expect an array of one value (readAsFoo)
     * @param name
     * @param default_value
     * @return
     */
    private String getFirstValueOfArrayAsString(FieldName name, String default_value)
    {
    	String[] array = result.get(name);
    	
    	if ( array == null || array.length <= 0 ) return default_value;
    	
    	return array[0];
    }
    
    /**
     * Get a representation of the array as a string array
     * @param name
     * @param default_value
     * @return
     */
    private String[] getArrayAsString(FieldName name, String[] default_value)
    {
    	String[] array = result.get(name);
    	
    	if ( array == null ) return default_value;
    	
    	return array;
    }
    
    /**
     * Get a field as text from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public String readAsText(FieldName name, String default_value)
    {
    	return getFirstValueOfArrayAsString(name, default_value);
    }
    
    /**
     * Get a field as a text array from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public String[] readAsTextArray(FieldName name, String[] default_value)
    {
    	return getArrayAsString(name, default_value);
    }
    
    /**
     * Get a field as atom from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public String readAsAtom(FieldName name, String default_value)
    {
    	return getFirstValueOfArrayAsString(name, default_value);
    }
    
    /**
     * Get a field as a atom array from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public String[] readAsAtomArray(FieldName name, String[] default_value)
    {
    	return getArrayAsString(name, default_value);
    }
    
    /**
     * Get a field as long from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public long readAsLong(FieldName name, long default_value)
    {
    	String value_as_string = getFirstValueOfArrayAsString(name, null);
    	
    	if ( value_as_string == null ) return default_value;
    	
    	try
		{
			return Long.parseLong(value_as_string);
		}
		catch(Exception e)
		{
			return default_value;
		}
    }
    
    /**
     * Get a field as a long array from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public long[] readAsLongArray(FieldName name, long[] default_value)
    {
    	String[] value_as_string_array = getArrayAsString(name, null);
    	
    	if ( value_as_string_array == null ) return default_value;
    	
    	List<Long> valid_values = new ArrayList<>();    	
    	for ( int i = 0; i < value_as_string_array.length; i++ )
    	{
    		try
    		{
    			valid_values.add(Long.parseLong(value_as_string_array[i]));
    		}
    		catch ( Exception e )
    		{
    			continue;
    		}
    	}
    	
    	long[] ret = new long[valid_values.size()];
    	int i = 0;
    	for ( Long value : valid_values )
    	{
    		ret[i] = value;
    		i++;
    	}
    	
    	return ret;
    }
    
    /**
     * Get a field as float from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public float readAsFloat(FieldName name, float default_value)
    {
    	String value_as_string = getFirstValueOfArrayAsString(name, null);
    	
    	if ( value_as_string == null ) return default_value;
    	
    	try
		{
			return Float.parseFloat(value_as_string);
		}
		catch(Exception e)
		{
			return default_value;
		}
    }
    
    /**
     * Get a field as a float array from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public float[] readAsFloatArray(FieldName name, float[] default_value)
    {
    	String[] value_as_string_array = getArrayAsString(name, null);
    	
    	if ( value_as_string_array == null ) return default_value;
    	
    	List<Float> valid_values = new ArrayList<>();    	
    	for ( int i = 0; i < value_as_string_array.length; i++ )
    	{
    		try
    		{
    			valid_values.add(Float.parseFloat(value_as_string_array[i]));
    		}
    		catch ( Exception e )
    		{
    			continue;
    		}
    	}
    	
    	float[] ret = new float[valid_values.size()];
    	int i = 0;
    	for ( float value : valid_values )
    	{
    		ret[i] = value;
    		i++;
    	}
    	
    	return ret;
    }
    
    /**
     * Get a field as boolean from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public boolean readAsBoolean(FieldName name, boolean default_value)
    {
    	String value_as_string = getFirstValueOfArrayAsString(name, null);
    	
    	if ( value_as_string == null ) return default_value;
    	
    	try
		{
			return Boolean.parseBoolean(value_as_string);
		}
		catch(Exception e)
		{
			return default_value;
		}
    }
    
    /**
     * Get a field as a boolean array from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public boolean[] readAsBooleanArray(FieldName name, boolean[] default_value)
    {
    	String[] value_as_string_array = getArrayAsString(name, null);
    	
    	if ( value_as_string_array == null ) return default_value;
    	
    	List<Boolean> valid_values = new ArrayList<>();    	
    	for ( int i = 0; i < value_as_string_array.length; i++ )
    	{
    		try
    		{
    			valid_values.add(Boolean.parseBoolean(value_as_string_array[i]));
    		}
    		catch ( Exception e )
    		{
    			continue;
    		}
    	}
    	
    	boolean[] ret = new boolean[valid_values.size()];
    	int i = 0;
    	for ( boolean value : valid_values )
    	{
    		ret[i] = value;
    		i++;
    	}
    	
    	return ret;
    }
    
    /**
     * Get a field as Day from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public Day readAsDay(FieldName name, Day default_value)
    {
    	String value_as_string = getFirstValueOfArrayAsString(name, null);
    	
    	if ( value_as_string == null ) return default_value;    	
    	
    	try
		{
			return getDayFromString(value_as_string, default_value);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return default_value;
		}
    }
    
    /**
     * Get a field as a Day array from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public Day[] readAsDayArray(FieldName name, Day[] default_value)
    {
    	String[] value_as_string_array = getArrayAsString(name, null);
    	
    	if ( value_as_string_array == null ) return default_value;
    	
    	List<Day> valid_values = new ArrayList<>();    	
    	for ( int i = 0; i < value_as_string_array.length; i++ )
    	{
    		try
    		{
    			Day day = getDayFromString(value_as_string_array[i], null);    			
    			if ( day == null ) continue;
    			valid_values.add(day);
    		}
    		catch ( Exception e )
    		{
    			continue;
    		}
    	}
    	
    	Day[] ret = new Day[valid_values.size()];
    	int i = 0;
    	for ( Day value : valid_values )
    	{
    		ret[i] = value;
    		i++;
    	}
    	
    	return ret;
    }
    
    /**
     * Helper method used to get a Day object from a string formatted yyyy-mm-dd (The format used to store the document in ElasticSearch)
     * @param in
     * @param default_value
     * @return
     */
    private Day getDayFromString(String in, Day default_value)
    {
    	try
    	{
	    	// Format is yyyy-mm-dd for sorting. Get the values to properly assemble a date
	    	String[] pieces = in.split("-");
	    	
	    	if ( pieces.length != 3 ) return default_value;
	    	int month = Integer.parseInt(pieces[1]);
	    	int day = Integer.parseInt(pieces[2]);
	    	int year = Integer.parseInt(pieces[0]);
	    	
	    	return new Day(month, day, year);
    	}
    	catch ( Exception e )
    	{
    		e.printStackTrace();
    		return default_value;
    	}
    }
    
    /**
     * Get a field as Instant from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public Instant readAsInstant(FieldName name, Instant default_value)
    {
    	String value_as_string = getFirstValueOfArrayAsString(name, null);
    	
    	if ( value_as_string == null ) return default_value;
    	
    	try
		{
			return (Instant)StandardObject.deserialize(value_as_string);
		}
		catch(Exception e)
		{
			return default_value;
		}
    }
    
    /**
     * Get a field as an Instant array from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public Instant[] readAsInstantArray(FieldName name, Instant[] default_value)
    {
    	String[] value_as_string_array = getArrayAsString(name, null);
    	
    	if ( value_as_string_array == null ) return default_value;
    	
    	List<Instant> valid_values = new ArrayList<>();    	
    	for ( int i = 0; i < value_as_string_array.length; i++ )
    	{
    		try
    		{
    			Instant instant = (Instant)StandardObject.deserialize(value_as_string_array[i]);	
    			if ( instant == null ) continue;
    			valid_values.add(instant);
    		}
    		catch ( Exception e )
    		{
    			continue;
    		}
    	}
    	
    	Instant[] ret = new Instant[valid_values.size()];
    	int i = 0;
    	for ( Instant value : valid_values )
    	{
    		ret[i] = value;
    		i++;
    	}
    	
    	return ret;
    }
    
    /**
     * Get a field as TimeOfDay from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public TimeOfDay readAsTimeOfDay(FieldName name, TimeOfDay default_value)
    {
    	String value_as_string = getFirstValueOfArrayAsString(name, null);
    	
    	if ( value_as_string == null ) return default_value;
    	
    	try
		{
			return (TimeOfDay)StandardObject.deserialize(value_as_string);
		}
		catch(Exception e)
		{
			return default_value;
		}
    }
    
    /**
     * Get a field as an TimeOfDay array from this search result
     * 
     * @param name
     * @param default_value
     * @return
     */
    public TimeOfDay[] readAsTimeOfDayArray(FieldName name, TimeOfDay[] default_value)
    {
    	String[] value_as_string_array = getArrayAsString(name, null);
    	
    	if ( value_as_string_array == null ) return default_value;
    	
    	List<TimeOfDay> valid_values = new ArrayList<>();    	
    	for ( int i = 0; i < value_as_string_array.length; i++ )
    	{
    		try
    		{
    			TimeOfDay instant = (TimeOfDay)StandardObject.deserialize(value_as_string_array[i]);	
    			if ( instant == null ) continue;
    			valid_values.add(instant);
    		}
    		catch ( Exception e )
    		{
    			continue;
    		}
    	}
    	
    	TimeOfDay[] ret = new TimeOfDay[valid_values.size()];
    	int i = 0;
    	for ( TimeOfDay value : valid_values )
    	{
    		ret[i] = value;
    		i++;
    	}
    	
    	return ret;
    }
}
