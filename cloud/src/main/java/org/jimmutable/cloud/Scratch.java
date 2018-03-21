package org.jimmutable.cloud;

import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose;
import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehoseClientBuilder;

public class Scratch {

	public static void main(String[] args)
	{
//		com.amazonaws.services.kinesis.
		
		/**
		 * PutRecordRequest putRecordRequest = new PutRecordRequest();
putRecordRequest.setDeliveryStreamName(deliveryStreamName);

String data = line + "\n";

Record record = createRecord(data);
putRecordRequest.setRecord(record);

// Put record into the DeliveryStream
firehoseClient.putRecord(putRecordRequest);
		 */
		
//		AmazonKinesisFirehoseClient client = new AmazonKinesisFirehoseClient((AWSCredentials) null);
		AmazonKinesisFirehose client = AmazonKinesisFirehoseClientBuilder.standard().withCredentials(null).build();
		
//		client.
		
		
	}

}
