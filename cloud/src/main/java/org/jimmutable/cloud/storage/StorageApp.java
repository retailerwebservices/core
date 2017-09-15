package org.jimmutable.cloud.storage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.jimmutable.cloud.ApplicationId;
import org.jimmutable.core.objects.common.Kind;
import org.jimmutable.core.objects.common.ObjectId;

public class StorageApp
{
	static ApplicationId appId;
	Storage storage_system;
	static JFrame frame = new JFrame();

	public static void main( String[] args )
	{
		appId = new ApplicationId("Development");
		Storage storage_system = new StorageDevLocalFileSystem(false);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(750, 350);

		frame.setLayout(new BorderLayout());
		frame.setTitle("Storage Application Trainer");
		
		JTextField textField = new JTextField();
		textField.setEditable(false);

		StorageKey test_file = new StorageKey(new Kind("test"), new ObjectId("123"), new StorageKeyExtension(".txt"));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JButton upsert = new JButton("Upsert File");
		upsert.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				storage_system.upsert(test_file, "This is a test".getBytes(), false);
			}
		});
		
		
		JButton upsert_again = new JButton("Change File");
		upsert_again.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				storage_system.upsert(test_file, "Aha! It's something new".getBytes(), false);
			}
		});

		JButton check_existing = new JButton("Check Exists");
		check_existing.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				textField.setText(storage_system.exists(test_file, false)?"True":"False");
			}
		});
		
		JButton current_version = new JButton("Current Version");
		current_version.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				textField.setText(new String(storage_system.getCurrentVersion(test_file, null)));
			}
		});
		
		JButton delete = new JButton("Delete");
		delete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				storage_system.delete(test_file);
			}
		});
		
		JButton clean_up = new JButton("Clean up");
		clean_up.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				String filePathString = System.getProperty("user.home") + "/jimmutable_dev/" + ApplicationId.getOptionalDevApplicationId(appId);// application id needs to go here
				File f = new File(filePathString);
				if ( f.exists() )
				{
					Path rootPath = Paths.get(filePathString);
					try
					{
						Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
					}
					catch ( IOException e1 )
					{
						e1.printStackTrace();
					}
				}
			}
		});


		panel.add(upsert);
		panel.add(upsert_again);
		panel.add(check_existing);
		panel.add(current_version);
		panel.add(delete);
		panel.add(clean_up);

		JTextPane information = new JTextPane();
		information.setContentType("text/html");
		information.setText(getTrainingInformation());
		information.setEditable(false);
		
		frame.add(textField, BorderLayout.PAGE_START);
		
		frame.add(information, BorderLayout.CENTER);

		frame.add(panel, BorderLayout.LINE_END);
		frame.setVisible(true);
	}

	private static String getTrainingInformation()
	{
		return "<h1>How to use our Storage training application.</h1> "
				+ "<ol type=\"1\">"
				+ "<li> Open either Mac's Finder or Window's File View"
				+ "<li> Click on the 'Upsert File' button."
				+ "<li> Go to your file system and observe that there is now a file under Jimmutable_dev"
				+ "<li> Click on the button that says 'Check Exists', you should see a message saying 'True'"
				+ "<li> Click on the Current Version Button. You should see a message that states that it is a test"
				+ "<li> Click on the 'Change File' button and then click on the 'Current Version' button again. You should see a different message"
				+ "<li> Click the 'Delete' button and then click on the 'Check Exists' button. You should get a message saying 'false'"
				+ "<li> before you exit out, click the 'Clean up' button"
				+ "</o1>";
	}
}
