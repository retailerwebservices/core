package org.jimmutable.cloud.environment;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.jimmutable.core.serialization.JavaCodeUtils;
import org.jimmutable.core.utils.FileUtils;


public class DefaultLoggingSetup 
{
	static public void configureLogging()
	{
		try
		{
			File src = FileUtils.getSimpleFileInHomeDirectory("log4j2_configuration.xml");
			if ( !src.exists() )
			{
				System.out.println(String.format("Unable to logging configuration from %s, creating default", src.getAbsolutePath()));
				
				String default_config = String.format("%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n%s\r\n"
					     , "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					     , "<Configuration status=\"WARN\">"
					     , "  <Appenders>"
					     , "    <Console name=\"Console\" target=\"SYSTEM_OUT\">"
					     , "      <PatternLayout pattern=\"%d{dd MMM HH:mm:ss} [%t] %-5level %logger{36} - %msg%n\"/>"
					     , "    </Console>"
					     , "  </Appenders>"
					     , "  <Loggers>"
					     , "    <Root level=\"debug\">"
					     , "      <AppenderRef ref=\"Console\"/>"
					     , "    </Root>"
					     , "  </Loggers>"
					     , "</Configuration>"
					);
			
				FileUtils.quietWriteFile(src, default_config);
			}
									
			ConfigurationSource source = new ConfigurationSource(new FileInputStream(src));
			Configurator.initialize(null, source);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			System.out.println("Unable to configure logging");
			System.exit(1);
		}
		
	}
	
	static private void printJavaCode()
	{
		File src = FileUtils.getSimpleFileInHomeDirectory("log4j2_configuration.xml");
		String str = FileUtils.getComplexFileContentsAsString(src, "");
		
		System.out.println(JavaCodeUtils.toJavaStringLiteral(str));
	}
	
	/*static public void main(String args[])
	{
		configureLogging();
		
		Logger l = LogManager.getRootLogger();
		l.error("Hello World");
		l.debug("hi");
		l.trace("something");
		
		Exception e = new Exception("Hi there");
	
		
		l.debug("Something bad",e);
		
		//printJavaCode();
	}*/
}
