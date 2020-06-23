package org.jimmutable.cloud.servlets.attachments;

import org.jimmutable.cloud.attachments.AttachmentMetaData;
import org.jimmutable.cloud.servlets.common.DoGetGeneric;
import org.jimmutable.core.objects.common.Kind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoGetMetaData extends DoGetGeneric<AttachmentMetaData>
{

	/**
	 *
	 */
	private static final long serialVersionUID = 5792636588695267792L;

	private static final Logger logger = LoggerFactory.getLogger(DoGetMetaData.class);

	@Override
	protected Logger getLogger()
	{
		return logger;
	}

	@Override
	protected Kind getKind()
	{
		return AttachmentMetaData.KIND;
	}

	@Override
	protected String getId()
	{
		return "objectid";
	}

}
