package org.jimmutable.gcloud;

import org.jimmutable.core.exceptions.ValidationException;
import org.jimmutable.core.objects.Stringable;
import org.jimmutable.core.utils.Validator;

public class StorageKeyExtension extends Stringable{

	private static final String APPLICATION = "APPLICATION/";
	private static final String HTML="html";
	private static final String HTM="htm";
	private static final String CSS="css";
	private static final String JS="js";
	private static final String JSON="json";
	private static final String XML="xml";
	private static final String JPEG="jpeg";
	private static final String JPG="jpg";
	private static final String GIF="gif";
	private static final String PNG="png";
	private static final String PDF="pdf";
	private static final String XSLX="xslx";
	private static final String CSV="csv";
	private static final String TXT="txt";
	private static final String UNKNOWN="OCTET-STREAM";
	public StorageKeyExtension(String value){
		super(value);
	}

	public String getSimpleMimeType() {
		switch(getSimpleValue()){
		case HTML: return APPLICATION+HTML;
		case HTM: return APPLICATION+HTM;
		case CSS: return APPLICATION+CSS;
		case JS: return APPLICATION+JS;
		case JSON: return APPLICATION+JSON;
		case XML: return APPLICATION+XML;
		case JPEG: return APPLICATION+JPEG;
		case JPG: return APPLICATION+JPG;
		case GIF: return APPLICATION+GIF;
		case PNG: return APPLICATION+PNG;
		case PDF: return APPLICATION+PDF;
		case XSLX: return APPLICATION+XSLX;
		case CSV: return APPLICATION+CSV;
		case TXT: return APPLICATION+TXT;
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
