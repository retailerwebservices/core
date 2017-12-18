package org.jimmutable.core.objects.common;

import java.util.Objects;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Optional;
import org.jimmutable.core.utils.Validator;

public class Address extends StandardImmutableObject<Address>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.common.Address"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.String FIELD_NAME = new FieldDefinition.String("name",null);
	static public final FieldDefinition.String FIELD_LINE1 = new FieldDefinition.String("line1",null);
	static public final FieldDefinition.String FIELD_LINE2 = new FieldDefinition.String("line2",null);
	static public final FieldDefinition.String FIELD_LINE3 = new FieldDefinition.String("line3",null);
	static public final FieldDefinition.String FIELD_CITY = new FieldDefinition.String("city",null);
	static public final FieldDefinition.String FIELD_STATE = new FieldDefinition.String("state",null);
	static public final FieldDefinition.Stringable<PostalCode> FIELD_POSTAL_CODE = new FieldDefinition.Stringable<PostalCode>("postal_code", null, PostalCode.CONVERTER);
	static public final FieldDefinition.Enum<CountryCode> FIELD_COUNTRY = new FieldDefinition.Enum<>("country", null, CountryCode.CONVERTER);
	static public final FieldDefinition.Float FIELD_LATITUDE = new FieldDefinition.Float("latitude", (float)0);
	static public final FieldDefinition.Float FIELD_LONGITUDE = new FieldDefinition.Float("longitude", (float)0);
	
	private String name; // optional
	private String line1; // required
	private String line2; // optional
	private String line3; // optional
	private String city; // required
	private String state; // required
	private PostalCode postal_code; // required
	private CountryCode country; // required
	private float latitude; // optional
	private float longitude; // optional
	
	public String getOptionalName(String default_value) { return name; }
	public String getSimpleLine1() { return line1; }
	public String getOptionalLine2(String default_value) { return line2; }
	public String getOptionalLine3(String default_value) { return line3; }
	public String getSimpleCity() { return city; }
	public String getSimpleState() { return state; }
	public PostalCode getSimplePostalCode() { return postal_code; }
	public CountryCode getSimpleCountry() { return country; }
	
	public float getOptionalLatitude(float default_value) {
		return Optional.getOptional(latitude, (float)0, default_value);
	}
	
	public float getOptionalLongitude(float default_value) {
		return Optional.getOptional(longitude, (float)0, default_value);
	}
	
	public Address(String line1, String city, String state, PostalCode postal_code, CountryCode country) 
	{
		this.line1 = line1;
		this.city = city;
		this.state = state;
		this.postal_code = postal_code;
		this.country = country;
		complete();
	}
	
	public Address(ObjectParseTree t)
	{
		name = t.getString(FIELD_NAME);
		line1 = t.getString(FIELD_LINE1);
		line2 = t.getString(FIELD_LINE2);
		line3 = t.getString(FIELD_LINE3);
		city = t.getString(FIELD_CITY);
		state = t.getString(FIELD_STATE);
		postal_code = t.getStringable(FIELD_POSTAL_CODE);
		country = t.getEnum(FIELD_COUNTRY);
		latitude = t.getFloat(FIELD_LATITUDE);
		longitude = t.getFloat(FIELD_LONGITUDE);
		
	}
	
	@Override
	public void write(ObjectWriter writer)
	{
		writer.writeString(FIELD_NAME, getOptionalName(null));
		writer.writeString(FIELD_LINE1, getSimpleLine1());
		writer.writeString(FIELD_LINE2, getOptionalLine2(null));
		writer.writeString(FIELD_LINE3, getOptionalLine3(null));
		writer.writeString(FIELD_CITY, getSimpleCity());
		writer.writeString(FIELD_STATE, getSimpleState());
		
		writer.writeStringable(FIELD_POSTAL_CODE, postal_code);
		writer.writeEnum(FIELD_COUNTRY, country);
		writer.writeFloat(FIELD_LATITUDE, latitude);
		writer.writeFloat(FIELD_LONGITUDE, longitude);
	}

	@Override
	public int compareTo(Address other) 
	{
		int ret = Comparison.startCompare();
		
		ret = Comparison.continueCompare(ret, getOptionalName(null), other.getOptionalName(null));
		ret = Comparison.continueCompare(ret, getSimpleLine1(), other.getSimpleLine1());
		ret = Comparison.continueCompare(ret, getOptionalLine2(null), other.getOptionalLine2(null));
		ret = Comparison.continueCompare(ret, getOptionalLine3(null), other.getOptionalLine3(null));
		ret = Comparison.continueCompare(ret, getSimpleCity(), other.getSimpleCity());
		ret = Comparison.continueCompare(ret, getSimpleState(), other.getSimpleState());
		ret = Comparison.continueCompare(ret, getSimplePostalCode(), other.getSimplePostalCode());
		ret = Comparison.continueCompare(ret, getSimpleCountry(), other.getSimpleCountry());
		ret = Comparison.continueCompare(ret, getOptionalLatitude(0), other.getOptionalLatitude(0));
		ret = Comparison.continueCompare(ret, getOptionalLongitude(0), other.getOptionalLongitude(0));
		
		return ret;
	}

	@Override
	public void validate()
	{
		Validator.notNull(line1, city, state, postal_code, country);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getOptionalName(null), getSimpleLine1(), getSimpleCity(), getSimpleState(), getSimplePostalCode());
	}

	@Override
	public boolean equals(Object obj)
	{
		if ( !(obj instanceof Address) ) return false;
		
		Address other = (Address)obj;
		
		if ( !getOptionalName(null).equals((other.getOptionalName(null))) ) return false;
		if ( !getSimpleLine1().equals(other.getSimpleLine1()) ) return false;
		if ( !getOptionalLine2(null).equals((other.getOptionalLine2(null))) ) return false;
		if ( !getOptionalLine3(null).equals((other.getOptionalLine3(null))) ) return false;
		if ( !getSimpleCity().equals((other.getSimpleCity())) ) return false;
		if ( !getSimpleState().equals((other.getSimpleState())) ) return false;
		if ( !getSimplePostalCode().equals(other.getSimplePostalCode()) ) return false;
		if ( !getSimpleCountry().equals(other.getSimpleCountry())) return false;
		if ( getOptionalLatitude(0) != other.getOptionalLatitude(0)) return false;
		if ( getOptionalLongitude(0) != other.getOptionalLongitude(0)) return false;
		
		return true;
	}
	@Override
	public void freeze()
	{
		// Nothing to do
		
	}
	@Override
	public void normalize()
	{
		// Nothing to do
		
	}

}
