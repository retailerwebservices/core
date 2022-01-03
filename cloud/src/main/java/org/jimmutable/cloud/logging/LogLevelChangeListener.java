package org.jimmutable.cloud.logging;

import org.jimmutable.cloud.messaging.signal.SignalListener;
import org.jimmutable.core.objects.StandardObject;

public class LogLevelChangeListener implements SignalListener
{
	@Override
	public void onMessageReceived( StandardObject message )
	{
		if ( message instanceof LogLevelMessageOnChange )
		{
			LogLevelMessageOnChange log_level_message = (LogLevelMessageOnChange) message;
//			Log4jUtil.setAllLoggerLevels(log_level_message.getSimpleLogLevel().getSimpleLevel());
			Slf4jUtil.setAllLoggerLevels(log_level_message.getSimpleLogLevel().getSimpleLevel());
		}
	}

}