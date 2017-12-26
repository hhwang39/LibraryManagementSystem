import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is the future hold request screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class FutureHoldRequestScreen extends JPanel {
	
	/** Instance variables used by the future hold request screen */
	private JTextField ISBN;
	private JTextField Copy_Number;
	private JTextField Expected_Date;
	private String cn;
	private Date date;
	private String isbn_str;
	private boolean isSearched;
	/**
	 * Create a new future hold request screen
	 */
	public FutureHoldRequestScreen() {
		
		// Initialize the properties and instance variables
		ISBN = new JTextField(20);
		Copy_Number = new JTextField(10);
		Expected_Date = new JTextField(20);
		Expected_Date.setEditable(false);
		Copy_Number.setEditable(false);
		// inputPanel
		isSearched = false;
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Future Hold Request"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		// Request Button
		JButton requestButton = new JButton("Request");
		requestButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.REGISTER_SCREEN);
				executeHoldRequest();
			}
		});
		//ok Button
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.REGISTER_SCREEN);
				okPressedNextPage();
			}
		});
		
		//creating panel for ISBN
		JPanel fieldPanel = new JPanel();
		fieldPanel.add(new JLabel("ISBN"));
		fieldPanel.add(ISBN);
		fieldPanel.add(requestButton);
		
		//panel for copynumber
		JPanel copyPanel = new JPanel();
		copyPanel.add(new JLabel("Copy Number"));
		copyPanel.add(Copy_Number);
		
		//panel for Date
		JPanel datePanel = new JPanel();
		datePanel.add(new JLabel("Expected Avaiable Date"));
		datePanel.add(Expected_Date);
		
		//panel for ok Button
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		
		//adding all the panels
		inputPanel.add(fieldPanel);
		inputPanel.add(copyPanel);
		inputPanel.add(datePanel);
		inputPanel.add(buttonPanel);
		add(inputPanel);
		//add(okButton);
	}
	/**
	* show copyNumber and expected avaiable date, do not update
	**/
	private void executeHoldRequest() {
		cn = "";
		date = new Date(2); 
		String query = "SELECT (DATEDIFF(ReturnDate, CURDATE())), ReturnDate, CopyNumber " +
						"FROM ISSUE, BOOK_COPY WHERE (BISBN = IISBN) AND (IsDamaged = \'N\')" + 
						"AND (ICopyNumber = CopyNumber) AND (BISBN =\'" + 
						ISBN.getText() + "\')" +
						"AND ((IsCheckedOut = \'Y\') OR (IsOnHold = \'Y\')) AND " +
						"(DATEDIFF(ReturnDate, CURDATE()) > 0)  AND (FutureRequester Is NULL) GROUP BY " +
						"DATEDIFF(ReturnDate, CURDATE()) ORDER BY " +
						"DATEDIFF(ReturnDate, CURDATE()) LIMIT 1;";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			if (rs.next()) {
				cn = rs.getString("CopyNumber");
				date = rs.getDate("ReturnDate");
				Copy_Number.setText(cn);
				Expected_Date.setText(date.toString());
				isbn_str = ISBN.getText();
				isSearched = true;
			} else {
				
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	* ok button is pressed go to next screen and before that update
	**/
	private void okPressedNextPage() {
		if (isSearched) {
			String query = "UPDATE BOOK_COPY SET FutureRequester = \'" + CS4400T74Frontend.CurrentUser + "\'" +
							"WHERE (BISBN = \'" + isbn_str + "\') AND (CopyNumber = " +
							cn + ");";
			CS4400T74Frontend.ExecuteWriteQuery(query);
			StudentViewScreen.tabs.setSelectedComponent(StudentViewScreen.searchBookScreen);
		}
	}

}
