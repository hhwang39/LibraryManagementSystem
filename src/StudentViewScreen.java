import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class StudentViewScreen extends JPanel {
	
	/** Instance variables used by the student view screen */
	public static JTabbedPane tabs;
	public static SearchBookScreen searchBookScreen;
	public static HoldRequestScreen holdRequestScreen;
	
	public StudentViewScreen() {
		super(new GridLayout(1, 1));
		
		// Create the tabbed pane
		tabs = new JTabbedPane();
		searchBookScreen = new SearchBookScreen();
		tabs.addTab("Search Books",
				null,
				searchBookScreen,
				"Search for a book by title, author or ISBN");
		holdRequestScreen = new HoldRequestScreen();
		tabs.addTab("Hold Request",
				null,
				holdRequestScreen,
				"Request a hold on an available book");
		tabs.addTab("Request Extension",
				null,
				new RequestExtensionScreen(),
				"Request an extension for a borrowed book");
		tabs.addTab("Future Hold Request",
				null,
				new FutureHoldRequestScreen(),
				"Request a hold on a book for when it becomes available");
		tabs.addTab("Track Location",
				null,
				new TrackBookScreen(),
				"Track a book's location");
		add(tabs);
	}
}
