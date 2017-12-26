import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the search screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class SearchBookScreen extends JPanel {
	
	/** Definitions */
	private final static String[] searchTypes = {"Title", "ISBN", "Author"};
	
	/** Instance variables used by the search screen */
	private JComboBox<String> searchTypeComboBox;
	private JTextField searchField;
	private JLabel errorLabel;
	public static JLabel searchScreenToast = new JLabel("");
	public static SearchBookScreen this_instance;
	
	/**
	 * Create a new search screen
	 */
	public SearchBookScreen() {
		
		// Initialize the properties and instance variables
		searchTypeComboBox = new JComboBox<String>(searchTypes);
		searchField = new JTextField(20);
		this_instance = this;
		errorLabel = new JLabel("");
		
		// Title
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Search Books"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// Creating panel for drop down box
		JPanel dropdownPanel = new JPanel();
		dropdownPanel.add(new JLabel("Search type:"));
		dropdownPanel.add(searchTypeComboBox);
		
		// Panel for search box
		JPanel searchPanel = new JPanel();
		searchPanel.add(searchField);
		//searchField.setText("Comp");
		
		// Back Button
		JButton logoutButton = new JButton("Logout");
		logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				backPressed();
			}
		});
		//add(backButton);
		//searchButton
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(searchBook()) {
					StudentViewScreen.tabs.setSelectedComponent(StudentViewScreen.holdRequestScreen);
				}
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(logoutButton);
		buttonPanel.add(searchButton);
		
		//add(closeButton);
		inputPanel.add(dropdownPanel);
		inputPanel.add(searchPanel);
		inputPanel.add(buttonPanel);
		inputPanel.add(errorLabel);
		add(inputPanel);
		add(searchScreenToast);
	}
	/**
	* search the book and show it on Screen
	**/
	public boolean searchBook() {
		
		String searchString = searchField.getText();
		
		// Validate input
		if("".equals(searchString)) {
			errorLabel.setText("Must type something to search!");
			return false;
		}
		errorLabel.setText("");
		
		// Do different search query depending on user selection
		String query = "";
		switch((String)searchTypeComboBox.getSelectedItem()) {
		case "Title":
			query = "SELECT ISBN, Title, isBookOnReserve, CopyNumber, isCheckedOut, isOnHold, Edition FROM (BOOK, BOOK_COPY) WHERE (Title LIKE \'%" +
					searchString + "%\') AND (BISBN = ISBN) AND (isDamaged = \'N\')";
			break;
		case "ISBN":
			query = "SELECT ISBN, Title, isBookOnReserve, CopyNumber, isCheckedOut, isOnHold, Edition FROM (BOOK, BOOK_COPY) WHERE (ISBN=\'" +
					searchString + "\') AND (BISBN = ISBN) AND (isDamaged = \'N\')";;
			break;
		case "Author":
			query = "SELECT ISBN, Title, isBookOnReserve, CopyNumber, isCheckedOut, isOnHold, Edition FROM (BOOK, BOOK_COPY, AUTHOR) WHERE (AuthorName LIKE \'%" +
					searchString + "%\') AND (BISBN = ISBN) AND (ISBN = AISBN) AND (isDamaged = \'N\')";
			break;
		}
		
		// Perform book search
		CS4400T74Frontend.bookMap = new HashMap<String, ArrayList<Book>>();
		CS4400T74Frontend.availabilityMap = new HashMap<String, HashMap<Integer, Integer>>();
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			while(rs.next()) {
				String title = rs.getString("Title");
				String isbn = rs.getString("ISBN");
				String onReserve = rs.getString("isBookOnReserve");
				int copyNumber = rs.getInt("CopyNumber");
				String checkedOut = rs.getString("isCheckedOut");
				String onHold = rs.getString("isOnHold");
				int edition = rs.getInt("Edition");
				Book b = new Book(title, isbn, "Y".equals(onReserve), copyNumber, "Y".equals(checkedOut), "Y".equals(onHold));
				b.edition = edition;
				if(CS4400T74Frontend.bookMap.get(title) == null) {
					CS4400T74Frontend.bookMap.put(title, new ArrayList<Book>());
				}
				CS4400T74Frontend.bookMap.get(title).add(b);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		// Determine availability
		for(String s : CS4400T74Frontend.bookMap.keySet()) {
			HashMap<Integer, Integer> availablePerEdition = new HashMap<Integer, Integer>();
			for(Book b : CS4400T74Frontend.bookMap.get(s)) {
				if(!b.isCheckedOut && !b.isOnHold) {
					if(!availablePerEdition.containsKey(b.edition)) {
						availablePerEdition.put(b.edition, 1);
					} else {
						availablePerEdition.put(b.edition, availablePerEdition.get(b.edition) + 1);
					}
				}
			}
			CS4400T74Frontend.availabilityMap.put(s, availablePerEdition);
		}
		
		// Show hold request screen
		HoldRequestScreen.populateTable();
		return true;
	}

	/**
	 * go back to login screen?
	 */
	private void backPressed() {
		CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.LOGIN_SCREEN);
	}
}
