package org.jimmutable.gcloud.pubsub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.gcloud.ProjectId;
import org.jimmutable.gcloud.logging.LogSupplier;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

/**
 * Easy to use class for sending StandardObject(s) as messages
 * 
 * The code is optimized to be able to send at any rate.
 * 
 * @author kanej
 *
 */
public class StandardObjectPublisher {
	private static Logger logger = Logger.getLogger(StandardObjectPublisher.class.getName());

	static private Map<TopicId, Publisher> my_publishers = new ConcurrentHashMap();

	/**
	 * Publish an object to a topic.
	 * 
	 * Assuming the topic is setup (you can make sure of this by making a prior call
	 * to ensureTopicSetup) this method is fully asynchronous and returns
	 * immediately. This method is also thread safe.
	 * 
	 * This method is designed to be able to support very high frequency sending
	 * (several thousand objects per second if multiple calling threads are used).
	 * Uses batching etc. as well.
	 * 
	 * @param topic
	 *            The topic id (topic must be in ProjectId.CURRENT_PROJECT)
	 * @param object
	 *            The object to publish, may not be null
	 * 
	 * @return true if the object was published, false otherwise
	 */
	static public boolean publishObject(TopicId topic, StandardObject object) {
		if (topic == null)
			return false; // can't send anything to a null topic
		if (object == null)
			return false; // can't send a null object

		if (!my_publishers.containsKey(topic)) {
			ensureTopicSetup(topic);
		}

		try {
			Publisher publisher = my_publishers.get(topic);
			if (publisher == null)
				return false; // A serious error has occurred, unable to send the message

			String message = object.serialize(Format.JSON);

			// Note: during development, compressing the messages was tested. On small JSON
			// formatted messages (like StandardMessageOnUpsert) gzipping the data made it
			// larger (as opposed to smaller). As a result, the decision was made *not* to
			// compress message data

			ByteString data = ByteString.copyFromUtf8(message);
			PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
			publisher.publish(pubsubMessage);

			return true;
		} catch (Exception e) {
			logger.severe(new LogSupplier(e).get());
			return false;
		}
	}

	/**
	 * Make sure that a topic is setup (exists) and is ready for publishing.
	 * 
	 * If you need to guarantee immediate returns from publishObject on the first
	 * call for a given topic, call ensureTopicSetup prior to making that first call
	 * to make sure the topic is setup etc.
	 * 
	 * @param topic_id
	 *            The topic id (in the current project)
	 * @return true if the topic is "good to go" false if an error occurs
	 * 
	 */
	synchronized static public boolean ensureTopicSetup(TopicId topic_id) {
		if (topic_id == null)
			return false; // can't setup a null topic
		if (my_publishers.containsKey(topic_id))
			return true; // topic is already setup and ready to go

		boolean topic_ready = PubSubConfigurationUtils.createTopicIfNeeded(ProjectId.CURRENT_PROJECT, topic_id);
		if (!topic_ready)
			return false;

		TopicName topic_name = PubSubConfigurationUtils.createTopicName(ProjectId.CURRENT_PROJECT, topic_id);

		try {
			Publisher publisher = Publisher.defaultBuilder(topic_name).build();
			my_publishers.put(topic_id, publisher);

			return true;
		} catch (Exception e) {
			logger.severe(new LogSupplier(e).get());
			try {
				Thread.currentThread().sleep(1000);
			} catch (Exception e2) {
			} // the 1 second delay is to give the google cloud some breathing room before we
				// try again to setup the publisher
			return false;
		}
	}

	/**
	 * Shutdown and close all publishers. Outside of certain specialized situations,
	 * you should not need to call this method.
	 * 
	 * This method is needed when you are about to hard terminate a process and have
	 * a need to make sure than any messages that are batched "make it out" before
	 * the process is hard terminated. This most frequently comes up in batch (cron)
	 * jobs you are about to call System.exit
	 * 
	 * This method may take some time to execute. (Several seconds)
	 * 
	 * If you call shutdown() in one thread, other threads will still (later) be
	 * able to send messages, but no guarantee is made about their messages being
	 * published.
	 * 
	 * Creating a new topic while simultaneously shutting down the publisher in
	 * another thread will have unpredictable results. That being said, that is,
	 * seemingly, an unbelievably rare occurrence.
	 * 
	 * @return true if the shutdown was accomplished without error, false otherwise
	 * 
	 */
	static public boolean shutdown() {
		logger.info("Shutting down standard object publisher");

		Map<TopicId, Publisher> old_publishers = new HashMap(my_publishers);
		my_publishers.clear();

		boolean shutdown_ok = true;

		for (Publisher publisher : old_publishers.values()) {
			try {
				publisher.shutdown();
			} catch (Exception e) {
				logger.severe(new LogSupplier(e).get());
				shutdown_ok = false;
			}
		}

		return shutdown_ok;
	}
}
