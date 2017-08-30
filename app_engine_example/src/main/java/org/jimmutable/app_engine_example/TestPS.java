package org.jimmutable.app_engine_example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jimmutable.gcloud.logging.LogSupplier;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.iam.v1.TestIamPermissionsResponse;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

public class TestPS extends HttpServlet {

	private static final Logger logger = Logger.getLogger(TestPS.class.getName());

	/**
	 * 
	 */
	// private static final long serialVersionUID = 6893627755083611557L;
	//
	// static private final TopicId TREVOR_TOPIC = new TopicId("trevortopic");
	// static private final SubscriptionId TREVOR_SUBSCRIPTION = new
	// SubscriptionId("trevorsubscription");
	//
	// @Override
	// public void doGet(HttpServletRequest request, HttpServletResponse response)
	// throws IOException, ServletException {
	//
	// logger.info("Starting pub sub example");
	//
	// ExampleUtils.setupExample();
	//
	// PullSubscriptionDefinition def;
	//
	// def = new PullSubscriptionDefinition(ProjectId.CURRENT_PROJECT,
	// TREVOR_SUBSCRIPTION, ProjectId.CURRENT_PROJECT,
	// TREVOR_TOPIC);
	//
	// StandardObjectReceiver.startListening(def, new SimpleListener());
	//
	// StandardMessageOnUpsert message = new StandardMessageOnUpsert(new
	// Kind("one-per-sec-test"),
	// new ObjectId("single message"));
	//
	// if (StandardObjectPublisher.publishObject(TREVOR_TOPIC, message)) {
	// logger.severe("Publish successful");
	// } else {
	// logger.severe("Publish failed");
	// }
	//
	// boolean shutdown_result = StandardObjectPublisher.shutdown();
	// logger.info("Shutdown publisher result = " + shutdown_result);
	//
	// }

	/**
	 * This is a simple StandardObjectListener implementation. It will only respond
	 * to StandardMessageOnUpsert messages and it does so simply by printing the
	 * kind and object id out System.out
	 *
	 * @author kanej
	 *
	 */
	// static private class SimpleListener implements StandardObjectListener {
	// public void onMessageReceived(StandardObject message) {
	// if (message instanceof StandardMessageOnUpsert) {
	// StandardMessageOnUpsert upsert_message = (StandardMessageOnUpsert) message;
	//
	// logger.info(new LogSupplier("Upsert Message Rec %s:%s\n",
	// upsert_message.getSimpleKind(),
	// upsert_message.getSimpleObjectId()));
	// }
	// }
	// }

	// @Override
	// public void doPost(HttpServletRequest request, HttpServletResponse response)
	// throws IOException, ServletException {
	//
	// }
	//
	// private Publisher publisher;
	//
	// @Override
	// public void doGet(HttpServletRequest request, HttpServletResponse response) {
	//
	// logger.info("Do Get!!!");
	//
	// Publisher publisher = this.publisher;
	//
	// String topicId = "trevortopic";
	// // create a publisher on the topic
	// if (publisher == null) {
	// try {
	// publisher = Publisher.defaultBuilder(TopicName.create("platform-test-174921",
	// topicId)).build();
	// } catch (IOException e) {
	// logger.severe(new LogSupplier(e));
	// }
	// }
	// // construct a pubsub message from the payload
	// final String payload = "mypayload";
	// PubsubMessage pubsubMessage =
	// PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(payload)).build();
	//
	// try {
	// logger.info(publisher.publish(pubsubMessage).get());
	// } catch (InterruptedException | ExecutionException e) {
	// try {
	// response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
	// } catch (IOException e1) {
	// logger.severe(new LogSupplier(e));
	// }
	// logger.severe(new LogSupplier(e));
	// }
	//
	// }

	static final int MESSAGE_COUNT = 5;

	// use the default project id
	private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

	// schedule a message to be published, messages are automatically batched
	private static ApiFuture<String> publishMessage(Publisher publisher, String message) throws Exception {
		// convert message to bytes
		ByteString data = ByteString.copyFromUtf8(message);
		PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
		return publisher.publish(pubsubMessage);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		TopicName topicName = TopicName.create(PROJECT_ID, "trevortopic");

		try (TopicAdminClient topicAdminClient = TopicAdminClient.create()) {
			List<String> permissions = new LinkedList<>();
			permissions.add("pubsub.topics.get");
			TestIamPermissionsResponse testedPermissions = topicAdminClient.testIamPermissions(topicName.toString(),
					permissions);
			logger.info(testedPermissions.toString());

			// logger.info("Creating topic " + topicName.toString());

			// topicAdminClient.createTopic(topicName);

		} catch (Exception e) {
			logger.severe(new LogSupplier(e));
		}

		// https://github.com/GoogleCloudPlatform/google-cloud-java/issues/2275
		final Credentials pubSubCredentials = TopicAdminSettings.defaultCredentialsProviderBuilder().build()
				.getCredentials();
		pubSubCredentials.refresh(); // Force immediate retrieval of credentials from within request thread so that
										// AppEngine automatic authentication works
		Publisher publisher = null;
		List<ApiFuture<String>> apiFutures = new ArrayList<>();
		try {
			// Create a publisher instance with default settings bound to the topic
			publisher = Publisher.defaultBuilder(topicName)
					.setCredentialsProvider(FixedCredentialsProvider.create(pubSubCredentials)).build();
			for (int i = 0; i < MESSAGE_COUNT; i++) {
				String message = "message-" + i;
				ApiFuture<String> messageId = publishMessage(publisher, message);
				apiFutures.add(messageId);
			}
		} catch (Exception e) {
			logger.severe(new LogSupplier(e));
		} finally {
			// Once published, returns server-assigned message ids (unique within the topic)
			List<String> messageIds = null;
			try {
				messageIds = ApiFutures.allAsList(apiFutures).get();
			} catch (InterruptedException | ExecutionException e) {
				logger.severe(new LogSupplier(e));
			}

			if (messageIds != null) {
				for (String messageId : messageIds) {
					logger.info(messageId);
				}
			}
			if (publisher != null) {
				// When finished with the publisher, shutdown to free up resources.
				try {
					publisher.shutdown();
				} catch (Exception e) {
					logger.severe(new LogSupplier(e));
				}
			}
		}

		try {
			response.getWriter().println("Done - check the logs");
		} catch (IOException e) {
			logger.severe(new LogSupplier(e));
		}

	}

}
