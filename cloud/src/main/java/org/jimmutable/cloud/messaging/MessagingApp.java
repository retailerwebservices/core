package org.jimmutable.cloud.messaging;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

public class MessagingApp
{
	static ApplicationId appId;
	static Messaging messagingsystem;
	static JFrame frame = new JFrame();

	public static void main(String[] args)
	{
		appId = new ApplicationId("Development");
		messagingsystem = new MessagingDevLocalFileSystem();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 350);

		frame.setLayout(new BorderLayout());
		frame.setTitle("Messaging Application Trainer");

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JButton setup_button = new JButton("Setup");
		setup_button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String mainpath = System.getProperty("user.home") + "/jimmutable_dev/messaging/development/monty-python-jokes";
				for (String queue_application_id : Arrays.asList("lancelot", "galahad"))
				{
					for (String queue_queue_id : Arrays.asList("queue1", "queue2"))
					{
						String filepath = mainpath + "/" + queue_application_id + "/" + queue_queue_id;
						File f = new File(filepath);
						f.mkdirs();
					}
				}

			}
		});

		JButton send_message = new JButton("Send Message");
		send_message.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				messagingsystem.sendAsync(new TopicDefinition(appId, new TopicId("monty-python-jokes")), new StandardMessageOnUpsert(new Kind("killer-bunny"), new ObjectId(123456789)));
			}
		});

		JButton start_listening = new JButton("Start Listening");
		start_listening.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				messagingsystem.startListening(new SubscriptionDefinition(new TopicDefinition(appId, new TopicId("monty-python-jokes")), new QueueDefinition(appId, new QueueId("the-holy-grail"))), new TestAppMessageListener());
			}
		});

		JButton tear_down_button = new JButton("Tear Down");
		tear_down_button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String filePathString = System.getProperty("user.home") + "/jimmutable_dev/messaging";// application id
																										// needs to go
																										// here
				File f = new File(filePathString);
				if (f.exists())
				{
					Path rootPath = Paths.get(filePathString);
					try
					{
						Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}

			}
		});

		panel.add(setup_button);
		panel.add(send_message);
		panel.add(start_listening);
		panel.add(tear_down_button);

		JTextPane information = new JTextPane();
		information.setContentType("text/html");
		information.setText(getTrainingInformation());
		information.setEditable(false);
		frame.add(information);

		frame.add(panel, BorderLayout.LINE_END);
		frame.setVisible(true);
	}

	private static String getTrainingInformation()
	{
		return "<h1>How to use our messaging training application.</h1> " + "<ol type=\"1\">" + "<li> Open either Mac's Finder or Window's File View" + "<li> Click the setup button on the right hand side" + "<ol type=\"a\">" + "<li> This will setup our file system that will be required for sending messages." + "</ol>" + "<li> Click on the 'Send Message' button." + "<li> Go to your file system and observe that the file has appeared on all subfolders of the 'monty-python-jokes' folder has a .json file underneath it." + "<li> Click on the 'Start Listening' button" + "<ol type=\"a\">" + "<li> You will now see a folder under monty-python-jokes called development" + "</ol>" + "<li> Now click the 'Send Message' button." + "<li> You should see a popup." + "<li> before you exit out, click the 'Tear Down' button" + "</o1>";
	}

}

class TestAppMessageListener implements MessageListener
{
	public String messageContent;

	@Override
	public void onMessageReceived(@SuppressWarnings("rawtypes") StandardObject message)
	{
		messageContent = ((StandardMessageOnUpsert) message).getSimpleKind().toString();
		JOptionPane.showMessageDialog(null, messageContent);
	}
}