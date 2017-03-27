package org.jimmutable.core.examples.product_data;

import java.util.Objects;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldName;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * An example of a simple StandardImmutableObject
 * 
 * Note: This implementation is not a Stringable key patterned class, rather it
 * is coded as a plain old StandardImmutableObject
 * 
 * @author jim.kane
 *
 */
public class ItemKey extends StandardImmutableObject<ItemKey>
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.examples.ItemKey"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static private final FieldName FIELD_BRAND_CODE = new FieldName("brand");
	static private final FieldName FIELD_PN = new FieldName("pn");
	
	private BrandCode brand; // required
	private PartNumber pn; // required
	
	public ItemKey(String brand, String pn)
	{
		this(new BrandCode(brand),new PartNumber(pn));
	}
	
	public ItemKey(BrandCode brand, PartNumber pn)
	{
		this.brand = brand;
		this.pn = pn;
		
		complete();
	}
	
	public ItemKey(ObjectParseTree r)
	{
		brand = new BrandCode(r.getString(FIELD_BRAND_CODE, null));
		pn = new PartNumber(r.getString(FIELD_PN, null));
	}
	
	public void write(ObjectWriter writer) 
	{
		writer.writeStringable(FIELD_BRAND_CODE, brand);
		writer.writeStringable(FIELD_PN, pn);
	}

	
	public int compareTo(ItemKey o) 
	{
		int ret = Comparison.startCompare();
		
		ret = Comparison.continueCompare(ret, getSimpleBrand(), o.getSimpleBrand());
		ret = Comparison.continueCompare(ret, getSimplePN(), o.getSimplePN());
		
		return ret;
	}

	public void freeze() {}
	public void normalize() {}

	
	public void validate() 
	{
		Validator.notNull(brand, pn);
	}

	public int hashCode() 
	{
		return Objects.hash(brand,pn);
	}

	
	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof ItemKey) ) return false;
		
		ItemKey other = (ItemKey)obj;
		
		return getSimpleBrand().equals(other.getSimpleBrand()) && getSimplePN().equals(other.getSimplePN());
	}
	
	public BrandCode getSimpleBrand() { return brand; }
	public PartNumber getSimplePN() { return pn; }
	public String toString() { return String.format("%s:%s", brand.getSimpleValue(), pn.getSimpleValue()); }
}
