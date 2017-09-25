package org.jimmutable.cloud.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class MimeTypes 
{
	static private Map<String, String> mime_types = null;
	
	static private Map<String, String> createMimeTypes()
	{
	    Map<String, String> types = new HashMap<String, String>();
	    
		types.put("", "content/unknown");
		types.put("uu", "application/octet-stream");
		types.put("exe", "application/octet-stream");
		types.put("ps", "application/postscript");
		types.put("zip", "application/zip");
		types.put("sh", "application/x-shar");
		types.put("tar", "application/x-tar");
		types.put("snd", "audio/basic");
		types.put("au", "audio/basic");
		types.put("wav", "audio/x-wav");
		types.put("gif", "image/gif");
		types.put("png", "image/png");
		types.put("jpg", "image/jpeg");
		types.put("jpeg", "image/jpeg");
		types.put("htm", "text/html");
		types.put("html", "text/html");
		types.put("php", "text/html");
		types.put("cfm", "text/html");
		types.put("aspx", "text/html");
		types.put("asp", "text/html");
		types.put("jsp", "text/html");
		types.put("text", "text/plain");
		types.put("c", "text/plain");
		types.put("cc", "text/plain");
		types.put("c++", "text/plain");
		types.put("h", "text/plain");
		types.put("pl", "text/plain");
		types.put("txt", "text/plain");
		types.put("java", "text/plain");
		types.put("xml", "application/xml");
		types.put("pdf", "application/pdf");
		types.put("svg", "image/svg+xml");
		types.put("csv","application/csv");
		types.put("css","text/css");
		types.put("mer", "application/octet-stream");
		types.put("flv", "flv-application/octet-stream");
		types.put("swf", "application/x-shockwave-flash");
		types.put("ogg", "video/ogg");
		types.put("ico", "image/x-icon");
		types.put("js", "text/javascript");
		types.put("xlsx","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");		
		types.put("xltx","application/vnd.openxmlformats-officedocument.spreadsheetml.template");
		types.put("potx","application/vnd.openxmlformats-officedocument.presentationml.template");
		types.put("ppsx","application/vnd.openxmlformats-officedocument.presentationml.slideshow");
		types.put("pptx","application/vnd.openxmlformats-officedocument.presentationml.presentation");
		types.put("sldx","application/vnd.openxmlformats-officedocument.presentationml.slide");
		types.put("docx","application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		types.put("dotx","application/vnd.openxmlformats-officedocument.wordprocessingml.template");		
		types.put("xlam","application/vnd.ms-excel.addin.macroEnabled.12");
		types.put("xlsb","application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        types.put("mobi", "application/x-mobipocket-ebook");
        types.put("epub", "application/epub+zip");
		
		return Collections.unmodifiableMap(types);
	}
	
	static public String getMimeType(String extension)
	{
		if ( extension == null ) return "content/unknown";
		
		extension = extension.trim().toLowerCase();
		
		while( extension.startsWith(".") ) 
			extension = extension.substring(1);
		
		if ( mime_types == null ) 
			mime_types = createMimeTypes();
		
		String ret = mime_types.get(extension);
		if ( ret == null ) return "content/unknown";
		
		return ret;
	}
	
//	static public String getMimeType(S3Path path)
//	{
//		if ( path == null ) return "content/unknown";
//		return getMimeType(path.getOptionalExtension(""));
//	}
}
