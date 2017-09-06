package org.jimmutable.aws.messaging;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.TypeName;
import org.jimmutable.core.serialization.writer.ObjectWriter;

import com.amazonaws.services.glacier.model.GetDataRetrievalPolicyRequest;

public class MessageStandardObject extends StandardObject<MessageStandardObject>
{
	private byte[] data;

	public MessageStandardObject( byte[] bytesArray )
	{
		this.data = bytesArray;
	}

	@Override
	public int compareTo( MessageStandardObject o )
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TypeName getTypeName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write( ObjectWriter writer )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void normalize()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void validate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean equals( Object obj )
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	public byte[] getData() {
		return data;
	}

}
