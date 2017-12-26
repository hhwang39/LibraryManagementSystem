import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

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
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * This is the check out screen for staff for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class CheckOutScreen extends JPanel {

	/** Instance variables used by the checkout screen */
	private JTextField Issue_ID;
	private JTextField UserName;
	private JTextField ISBN;
	private JTextField Copy_Number;
	private JTextField Check_Out_Date;
	private JTextField Estimate_Date;
	private JLabel errorLabel;
	/**
	 * Create a new login screen
	 */
	public CheckOutScreen() {
		errorLabel = new JLabel("");
		// Initialize the properties and instance variables
		Issue_ID = new JTextField(20);
		UserName = new JTextField(10);
		ISBN = new JTextField(15);
		Copy_Number = new JTextField(10);
		Check_Out_Date = new JTextField(10);
		Estimate_Date = new JTextField(10);
		UserName.setEditable(false);
		ISBN.setEditable(false);
		Copy_Number.setEditable(false);
		Check_Out_Date.setEditable(false);
		Estimate_Date.setEditable(false);

		// inputPanel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Check Out"),
				BorderFactory.createEmptyBorder(5,5,5,5)));
		//inputPanel.setPreferredSize(new Dimension(500, 500));
		// Request Button
		JButton confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.REGISTER_SCREEN);
				issueCheckOut();
			}
		});

		//creating panel for issue id and user name
		JPanel fieldPanel = new JPanel();
		//fieldPanel.setAlignmentX(LEFT_ALIGNMENT);
		fieldPanel.add(new JLabel("Issue Id"));
		fieldPanel.add(Issue_ID);

		//panel for ISBN and copy number
		JPanel ISBNPanel = new JPanel();
		ISBNPanel.add(new JLabel("ISBN"));
		ISBNPanel.add(ISBN);
		ISBNPanel.add(new JLabel("Copy #"));
		ISBNPanel.add(Copy_Number);

		//panel for check out date
		JPanel datePanel = new JPanel();
		//datePanel.setAlignmentX(LEFT_ALIGNMENT);
		datePanel.add(new JLabel("Check out Date"));
		datePanel.add(Check_Out_Date);
		datePanel.add(new JLabel("Estimated Return Date"));
		datePanel.add(Estimate_Date);

		//panel for button
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(confirmButton);

		//adding all the panels
		inputPanel.add(fieldPanel);
		inputPanel.add(ISBNPanel);
		inputPanel.add(datePanel);
		inputPanel.add(buttonPanel);
		inputPanel.add(errorLabel);
		//inputPanel.setAlignmentX(LEFT_ALIGNMENT);
		add(inputPanel);
		//add(okButton);
	}
	/**
	 * Check out
	 **/
	private void issueCheckOut() {
		
		// Sanitize input
		if(!Pattern.matches("\\d+", Issue_ID.getText())) {
			errorLabel.setText("Invalid issue ID, not a number");
			return;
		}
		int issueID = Integer.parseInt(Issue_ID.getText());
		
		String query = "SELECT DATEDIFF(ReturnDate, CURDATE()), ICopyNumber, IISBN" +
				" FROM ISSUE WHERE IssueID = " + issueID;
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		int difference = 0;
		//get the difference in date differnece
		String IISBN = null;
		int ICopyNumber = -1;
		try {
			if (rs.next()) {
				difference = rs.getInt(1);
				IISBN = rs.getString("IISBN");
				ICopyNumber = rs.getInt("ICopyNumber");
			} else {
				errorLabel.setText("Invalid issue ID, doesn't exist");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (difference < 14) {
			
			// Delete issue
			query = "DELETE FROM ISSUE WHERE IssueID = " + issueID;
			CS4400T74Frontend.ExecuteWriteQuery(query);
			
			// Update the checked out status to N
			query = "UPDATE BOOK_COPY SET IsOnHold = \'N\' WHERE (BISBN = \'" + IISBN + "\')" +
					"AND (CopyNumber = " + ICopyNumber + ")";
			CS4400T74Frontend.ExecuteWriteQuery(query);
			
			// Show message of status to user
			errorLabel.setText("3 days passed removing issue");
			
		} else {
			query = "SELECT IUserName, IISBN, ICopyNumber, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY) " +
					"FROM ISSUE WHERE IssueID = " + issueID;
			rs = CS4400T74Frontend.ExecuteReadQuery(query);
			String userName;
			String isbn_str;
			int icopy;
			Date curDate;
			Date retDate;
			try {
				if (rs.next()) {
					//get datas from sql
					userName = rs.getString("IUserName");
					isbn_str = rs.getString("IISBN");
					icopy = rs.getInt("ICopyNumber");
					curDate = rs.getDate(4);
					retDate = rs.getDate(5);
					UserName.setText(userName);
					ISBN.setText(isbn_str);
					Copy_Number.setText(String.valueOf(icopy));
					Check_Out_Date.setText(curDate.toString());
					Estimate_Date.setText(retDate.toString());
					//get second column (count of subjectName);
					query = "UPDATE ISSUE SET DateOfIssue = CURDATE(), " +
							"ReturnDate = DATE_ADD(CURDATE(), INTERVAL 14 DAY) " +
							"WHERE IssueID = " + issueID;
					CS4400T74Frontend.ExecuteWriteQuery(query);
					query = "UPDATE BOOK_COPY SET IsOnHold = \'N\', " +
							"IsCheckedOut = \'Y\' WHERE BISBN = \'" + isbn_str + "\'" +
							" AND CopyNumber = " + String.valueOf(icopy);
					CS4400T74Frontend.ExecuteWriteQuery(query);
					errorLabel.setText("Checked Out");
					//System.out.println(String.valueOf(rs.getInt(2)));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
