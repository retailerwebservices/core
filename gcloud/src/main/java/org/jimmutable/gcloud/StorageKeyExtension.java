package org.jimmutable.gcloud;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class StorageKeyExtension extends Stringable{

	public static final String APPLICATION = "application/";
	public static final String TEXT = "text";
	public static final String IMAGE = "image";
	public static final String AUDIO = "audio";
	public static final String VIDEO = "video";
	public static final String HTML="html";
	public static final String HTM="htm";
	public static final String CSS="css";
	public static final String JS="js";
	public static final String JSON="json";
	public static final String XML="xml";
	public static final String JPEG="jpeg";
	public static final String JPG="jpg";
	public static final String GIF="gif";
	public static final String PNG="png";
	public static final String PDF="pdf";
	public static final String XSLX="xslx";
	public static final String CSV="csv";
	public static final String TXT="txt";
	public static final String UNKNOWN="OCTET-STREAM";
	public StorageKeyExtension(String value){
		super(value);
	}

	public String getSimpleMimeType() {
		switch(getSimpleValue()){
		case HTML: return TEXT+HTML;
		case HTM: return TEXT+HTM;
		case CSS: return TEXT+CSS;
		case JS: return APPLICATION+JS;
		case JSON: return APPLICATION+JSON;
		case XML: return APPLICATION+XML;
		case JPEG: return IMAGE+JPEG;
		case JPG: return IMAGE+JPG;
		case GIF: return IMAGE+GIF;
		case PNG: return IMAGE+PNG;
		case PDF: return APPLICATION+PDF;
		case XSLX: return APPLICATION+XSLX;
		case CSV: return TEXT+CSV;
		case TXT: return TEXT+TXT;
		default: return APPLICATION+UNKNOWN;
		}

	}
	
	@Override
	public void normalize() {
		normalizeLowerCase();
	}

	@Override
	public void validate() {
		Validator.notNull(getSimpleValue());
		Validator.min(getSimpleValue().length(), 1);
		
		char chars[] = getSimpleValue().toCharArray();
		for ( char ch : chars )
		{
			if ( ch >= 'a' && ch <= 'z' ) continue;
			if ( ch >= '0' && ch <= '9' ) continue;
			
			throw new ValidationException(String.format("Illegal character \'%c\' in item attribute %s.  Only lower case letters and numbers are allowed", ch, getSimpleValue()));
		}
		
	}

}
