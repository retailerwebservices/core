package org.jimmutable.aws.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.amazonaws.util.IOUtils;

public class CompressionUtils 
{
	static private Charset utf8 = Charset.forName("UTF-8");
	
	static public byte[] gzip(byte src[], byte default_value[])
	{
		try
		{
			if ( src == null ) return default_value;
			
			ByteArrayOutputStream out_raw = new ByteArrayOutputStream();
			GZIPOutputStream out = new GZIPOutputStream(out_raw);
			
			ByteArrayInputStream in = new ByteArrayInputStream(src);
			
			IOUtils.copy(in, out);
			
			in.close();
			out.close();
			
			return out_raw.toByteArray();
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	static public byte[] gzipString(String src, byte default_value[])
	{
		if ( src == null ) return default_value;
		return gzip(src.getBytes(utf8),default_value);
	} 
	
	static public byte[] gunzip(byte src[], byte default_value[])
	{
		try
		{
			if ( src == null ) return default_value;
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(src));
			
			
			IOUtils.copy(in, out);
			
			in.close();
			out.close();
			
			return out.toByteArray();
		}
		catch(Exception e)
		{
			return default_value;
		}
	}
	
	static public String gunzipToString(byte src[], String default_value)
	{
		byte uncompressed_data[] = gunzip(src, null);
		if ( uncompressed_data == null ) return default_value;
		
		return new String(uncompressed_data, utf8);
	}
}
