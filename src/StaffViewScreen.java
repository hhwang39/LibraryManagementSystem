import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class StaffViewScreen extends JPanel {
	
	/** Instance variables used by the staff view screen */
	//public static JTabbedPane tabs;
	//public static SearchBookScreen searchBookScreen;
	//public static HoldRequestScreen holdRequestScreen;
	
	public StaffViewScreen() {
		super(new GridLayout(1, 1));
		
		// Create the tabbed pane
		JTabbedPane tabs = new JTabbedPane();
		
		tabs.addTab("Checkout Book",
				null,
				new CheckOutScreen(),
				"Check out a book");
		tabs.addTab("Return Book/Charge Penalty",
				null,
				new ReturnBookScreen(),
				"Return a book and mark damages");
		//TODO: ADD PENTALY CHARGE SCEEN!!
		tabs.addTab("Damaged Report",
				null,
				new DamagedBookReportScreen(),
				"Pick a month to see the damaged book report for 3 subjects");
		tabs.addTab("Popular Books",
				null,
				new PopularBookReportScreen(),
				"Report of 3 most checked out books grouped by month");
		//TODO: FIX FREQUENT USERS!! 
		tabs.addTab("Freq. Users",
				null,
				new FrequentUserReportScreen(),
				"Report shows the top 5 users by checkouts per month");
		tabs.addTab("Pop Subject",
				null,
				new PopularSubReportScreen(),
				"Shows the most popular subject of checked out books per month");
		add(tabs);
	}
}
