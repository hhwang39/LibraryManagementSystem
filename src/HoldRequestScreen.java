import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * This is the hold request screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class HoldRequestScreen extends JPanel {
	
	/** Instance variables used by the hold request screen */
	private static HoldRequestScreen this_instance;
	private static ButtonGroup buttons;
	private static JPanel contentPanel;
	private static JTextField holdRequestDateTextField, returnDateTextField;
	private static JButton submitButton;
	private static JLabel errorLabel;
	private static HashMap<AbstractButton, Book> selectionMap;
	private static JScrollPane scrollPane;
	
	/**
	 * Create a new hold request screen
	 */
	public HoldRequestScreen() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// Instantiate variables
		holdRequestDateTextField = new JTextField(10);
		returnDateTextField = new JTextField(10);
		errorLabel = new JLabel();
		submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				submitButtonPressed();
			}
		});
		add(new JLabel("You must first search for a book."));
		
		// Make a link to this instance
		this_instance = this;
	}
	
	/**
	 * Populate the table of available books
	 */
	public static void populateTable() {
		
		// Instantiate private vars
		buttons = new ButtonGroup();
		JPanel scrollView = new JPanel();
		scrollView.setLayout(new BoxLayout(scrollView, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(scrollView);
		selectionMap = new HashMap<AbstractButton, Book>();
		boolean firstButton = true;
		this_instance.removeAll();
		errorLabel.setText("");
		java.util.Date currDate = new java.util.Date();
		String currDateStr = new SimpleDateFormat("yyyy-MM-dd").format(currDate);
		holdRequestDateTextField.setText(currDateStr);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currDate);
		cal.add(Calendar.DATE, 17);
		String returnDateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		returnDateTextField.setText(returnDateStr);
		
		// Populate the table
		ArrayList<Object[]> rowData = new ArrayList<Object[]>();
		ArrayList<BookTuple> addedBookEditions = new ArrayList<BookTuple>();
		ArrayList<Book> reservedBooks = new ArrayList<Book>();
		for(String s : CS4400T74Frontend.bookMap.keySet()) {
			ArrayList<Book> books = CS4400T74Frontend.bookMap.get(s);
			HashMap<Integer, Integer> availablePerEdition = CS4400T74Frontend.availabilityMap.get(s);
			for(Book b : books) {
				BookTuple newTuple = new BookTuple(b.title, b.edition);
				if(!addedBookEditions.contains(newTuple) && !b.isCheckedOut && !b.isOnHold && !b.isReserved) {
					addedBookEditions.add(newTuple);
					JRadioButton rb = new JRadioButton();
					rb.setSelected(firstButton);
					firstButton = false;
					buttons.add(rb);
					selectionMap.put(rb, b);
					Object[] newRow = {
						rb, b.ISBN, b.title, b.edition, availablePerEdition.get(b.edition), ""
					};
					rowData.add(newRow);
				}
			}
			for(Book b : books) {
				BookTuple newTuple = new BookTuple(b.title, b.edition);
				//if(!addedBookEditions.contains(newTuple) && b.isCheckedOut) {
				if(!addedBookEditions.contains(newTuple) && !b.isReserved) {
					addedBookEditions.add(newTuple);
					Date date = new Date(2);
					String query = "SELECT (DATEDIFF(ReturnDate, CURDATE())), ReturnDate, CopyNumber " +
							"FROM ISSUE, BOOK_COPY WHERE (BISBN = IISBN) AND (IsDamaged = \'N\')" + 
							"AND (ICopyNumber = CopyNumber) AND (BISBN =\'" + 
							b.ISBN + "\')" +
							"AND ((IsCheckedOut = \'Y\') OR (IsOnHold = \'Y\')) AND " +
							"(DATEDIFF(ReturnDate, CURDATE()) > 0)  AND (FutureRequester Is NULL) GROUP BY " +
							"DATEDIFF(ReturnDate, CURDATE()) ORDER BY " +
							"DATEDIFF(ReturnDate, CURDATE()) LIMIT 1";
					ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
					try {
						if(rs.next()) {
							date = rs.getDate("ReturnDate");
						} else {
							//throw new SQLException("Unable to query the correct unavailable book: " + b.title + " Edition " + b.edition + " Copy #" + b.copyNumber);
							date = null;
						}
					} catch(SQLException e) {
						e.printStackTrace();
					}
					Object[] newRow = {
						"", b.ISBN, b.title, b.edition, availablePerEdition.get(b.edition), date != null ? date.toString() : "Unknown"
					};
					rowData.add(newRow);
				} else if(!addedBookEditions.contains(newTuple) && b.isReserved) {
					addedBookEditions.add(newTuple);
					reservedBooks.add(b);
				}
			}
		}
		
		// Create the JPanel for the table
		GridLayout layout = new GridLayout(0, 6);
		layout.setHgap(10);
		layout.setVgap(10);
		contentPanel = new JPanel(layout);
		contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Books Available Summary"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// Add data to the table
		contentPanel.add(new JLabel("<html><u>Select</html>"));
		contentPanel.add(new JLabel("<html><u>ISBN</html>"));
		contentPanel.add(new JLabel("<html><u>Title</html>"));
		contentPanel.add(new JLabel("<html><u>Edition</html>"));
		contentPanel.add(new JLabel("<html><u># Available</html>"));
		contentPanel.add(new JLabel("<html><u>When Available</html>"));
		for(Object[] objs : rowData) {
			contentPanel.add(objs[0] instanceof String ? new JLabel("") : (JRadioButton) objs[0]);
			contentPanel.add(new JLabel((String) objs[1]));
			// Create formatted string for title
			String titleStr = "<html>";
			String left = (String) objs[2];
			while(left.length() > 14) {
				titleStr += left.substring(0, 14);
				left = left.substring(14);
			}
			titleStr += left + "</html>";
			contentPanel.add(new JLabel(titleStr));
			contentPanel.add(new JLabel((Integer) objs[3] + ""));
			contentPanel.add(new JLabel(objs[4] == null ? "0" : (Integer) objs[4] + ""));
			JPanel lastColPanel = new JPanel();
			lastColPanel.add(new JLabel((String) objs[5]));
			if("Unknown".equals(objs[5])) {
				JButton infoButton = new JButton("?");
				infoButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JOptionPane.showMessageDialog(HoldRequestScreen.this_instance,
								"The time that this book is unavailable cannot be determined.\n" +
								"This is because the book is currently unavailable, and\n" +
								"someone currently has a future hold request for this book.\n" +
								"That person may borrow it at any time, so the date this\n" +
								"book becomes available depends on when they borrow the book.");
					}
				});
				lastColPanel.add(infoButton);
			}
			contentPanel.add(lastColPanel);
		}
		
		// Add the table panel
		Dimension d = this_instance.getSize();
		d.setSize(d.width - 50, 50 * (rowData.size() + 1));
		contentPanel.setPreferredSize(d);
		scrollView.add(contentPanel);
		
		// Input UI Elements
		JPanel controlPanel = new JPanel();
		controlPanel.add(new JLabel("Hold Request Date:"));
		controlPanel.add(holdRequestDateTextField);
		controlPanel.add(new JLabel("Estimated Return Date:"));
		controlPanel.add(returnDateTextField);
		scrollView.add(controlPanel);
		
		// Button UI elements
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(submitButton);
		scrollView.add(buttonPanel);
		scrollView.add(errorLabel);
		
		// Books on reserve panel
		GridLayout layout2 = new GridLayout(0, 4);
		layout.setHgap(10);
		layout.setVgap(10);
		JPanel reservePanel = new JPanel(layout2);
		reservePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Books on Reserve"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// Add data to the second table
		reservePanel.add(new JLabel("<html><u>ISBN</html>"));
		reservePanel.add(new JLabel("<html><u>Title</html>"));
		reservePanel.add(new JLabel("<html><u>Edition</html>"));
		reservePanel.add(new JLabel("<html><u># Available</html>"));
		for(Book b : reservedBooks) {
			reservePanel.add(new JLabel("" + b.ISBN));
			// Create formatted string for title
			String titleStr = "<html>";
			String left = b.title;
			while(left.length() > 14) {
				titleStr += left.substring(0, 14) + "<br>";
				left = left.substring(14);
			}
			titleStr += left + "</html>";
			reservePanel.add(new JLabel(titleStr));
			reservePanel.add(new JLabel("" + b.edition));
			reservePanel.add(new JLabel("" + CS4400T74Frontend.availabilityMap.get(b.title).get(b.edition)));
		}
		scrollView.add(reservePanel);
		this_instance.add(scrollPane);
	}
	
	/**
	 * What to do when you press the submit button
	 */
	private static void submitButtonPressed() {
		
		// Get input data
		String holdRequestDateStr = holdRequestDateTextField.getText();
		String returnDateStr = returnDateTextField.getText();
		
		// Validate input fields
		if(buttons.getButtonCount() == 0) {
			errorLabel.setText("No selection");
			return;
		}
		if(!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", holdRequestDateStr)) {
			errorLabel.setText("Invalid request date string");
			return;
		}
		if(!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", returnDateStr)) {
			errorLabel.setText("Invalid return date string");
			return;
		}
		errorLabel.setText("");
		
		// Is the student debarred?
		String query = "SELECT IsDebarred FROM STUDENT_FACULTY WHERE Username = \'" +
				CS4400T74Frontend.CurrentUser + "\'";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		boolean isDebarred = false;
		try {
			if(rs.next()) {
				isDebarred = "Y".equals(rs.getString("IsDebarred").toUpperCase());
			} else {
				throw new SQLException("Unable to query user for debarred status");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		if(isDebarred) {
			errorLabel.setText("You cannot reserve a book because you are debarred");
			return;
			
		}
		
		// Get selected book
		Book b = null;
		for(AbstractButton ab : selectionMap.keySet()) {
			if(ab.isSelected()) {
				b = selectionMap.get(ab);
				break;
			}
		}
		
		// Update the book copy to be on hold
		query = "UPDATE BOOK_COPY SET isOnHold = \'Y\' WHERE (BISBN = \'" +
				b.ISBN + "\') AND (CopyNumber = " + b.copyNumber + ")";
		CS4400T74Frontend.ExecuteWriteQuery(query);
		
		// Get largest issue value
		query = "SELECT IssueId from ISSUE ORDER BY IssueId DESC LIMIT 1";
		int issueID = -1;
		rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			if(rs.next()) {
				issueID = rs.getInt("IssueID");
			} else {
				throw new SQLException("Unable to query largest issue number");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		++issueID;
		
		// Update the issue table
		query = "INSERT INTO ISSUE (IUserName, ICopyNumber, IISBN, IssueID, DateOfIssue, ReturnDate, CountOfExtension) VALUES (\'" +
				CS4400T74Frontend.CurrentUser + "\', " +
				b.copyNumber + ", \'" +
				b.ISBN + "\', " +
				issueID + ", CURDATE(), DATE_ADD(CURDATE(), INTERVAL 17 DAY), 0)";
		CS4400T74Frontend.ExecuteWriteQuery(query);
		
		// Update the toast on the search book screen and return to it
		SearchBookScreen.this_instance.searchBook();
		StudentViewScreen.tabs.setSelectedComponent(StudentViewScreen.searchBookScreen);
		new MakeSearchToastThread().run();
		JOptionPane.showMessageDialog(this_instance,
				"Here is your issue ID.\nSave it for your records:\n\n" + issueID,
				"Hold Request Receipt",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * This class represents a controller for displaying toast on the search screen.
	 * @author andrew
	 *
	 */
	private static class MakeSearchToastThread extends Thread {
		private Timer tm;
		@Override
		public void run() {
			SearchBookScreen.searchScreenToast.setText("Successfully requested hold");
			SearchBookScreen.this_instance.revalidate();
			tm = new Timer(1750, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					SearchBookScreen.searchScreenToast.setText("");
					SearchBookScreen.this_instance.revalidate();
					tm.stop();
				}
			});
			tm.start();
		}
	}
	
	/**
	 * This class represents a tuple of book title with edition for determining what was added to the table
	 * @author andrew
	 */
	private static class BookTuple {
		
		/** Defining feilds for a book tuple */
		private String title;
		private int edition;
		
		/**
		 * Create a new book tuple
		 * @param title Title of the book
		 * @param edition Edition of the book
		 */
		public BookTuple(String title, int edition) {
			this.title = title;
			this.edition = edition;
		}
		
		/**
		 * Determine if this BookTuple is equal to another object
		 */
		@Override
		public boolean equals(Object o) {
			return o instanceof BookTuple ? (title.equals(((BookTuple)o).title) && edition == ((BookTuple)o).edition) : false;
		}
		
		/**
		 * Collision-resistance hashcode function
		 */
		@Override
		public int hashCode() {
			return (edition << 24) | title.hashCode();
		}
		
		/**
		 * Print the relevant informatino about this book tuple
		 */
		@Override
		public String toString() {
			return title + " (" + edition + ")";
		}
	}
}
