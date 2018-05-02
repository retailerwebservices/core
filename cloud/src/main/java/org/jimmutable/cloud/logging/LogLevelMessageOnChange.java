package org.jimmutable.cloud.logging;

import java.util.Objects;

import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.serialization.FieldDefinition;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.reader.ObjectParseTree;
import org.jimmutable.core.serialization.writer.ObjectWriter;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * A standard message used to indicate that a log level has changed in the
 * system.
 * 
 * @author avery.gonzales
 *
 */
public class LogLevelMessageOnChange extends StandardImmutableObject<LogLevelMessageOnChange> 
{
	static public final TypeName TYPE_NAME = new TypeName("jimmutable.gcloud.LogLevelMessageOnChange"); public TypeName getTypeName() { return TYPE_NAME; }
	
	static public final FieldDefinition.Stringable<LogLevel> FIELD_LOG_LEVEL = new FieldDefinition.Stringable("log_level",null, LogLevel.CONVERTER);
	
	private LogLevel log_level; // required
	
	public LogLevelMessageOnChange(ObjectParseTree t)
	{
		log_level = t.getStringable(FIELD_LOG_LEVEL);
	}
	
	public LogLevelMessageOnChange(LogLevel log_level)
	{
		this.log_level = log_level;
		complete();
	}
	
	public LogLevel getSimpleLogLevel() { return log_level; }

	public void write(ObjectWriter writer) 
	{
		writer.writeStringable(FIELD_LOG_LEVEL, getSimpleLogLevel());
	}

	public int compareTo(LogLevelMessageOnChange o) 
	{
		int ret = Comparison.startCompare();
		
		ret = Comparison.continueCompare(ret, getSimpleLogLevel(), o.getSimpleLogLevel());
		
		return ret;
	}
	
	public void freeze() {}
	public void normalize() {}
	public void validate() { Validator.notNull(log_level);	}

	public int hashCode() 
	{
		return Objects.hash(log_level);
	}
	
	public boolean equals(Object obj) 
	{
		if ( !(obj instanceof LogLevelMessageOnChange) ) return false;
		
		LogLevelMessageOnChange other = (LogLevelMessageOnChange)obj;
		
		return compareTo(other) == 0;
	}
}
