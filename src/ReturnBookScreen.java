import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * This is the return book screen for staff for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class ReturnBookScreen extends JPanel {
	
	//Instance Variables
	private JTextField Issue_ID, userNameTextField, ISBNTextField, copyNumberTextField;
	private JLabel errorLabel;
	private JComboBox<String> isDamagedComboBox;
	private static String[] damagedSelector = {"N","Y"};
	
	// Fields for the charge pane;
	private JPanel chargePanel;
	private JTextField chargeAmountTextField;
	private JButton chargeUserButton;
	private JLabel chargeVerificationLabel;
	
	public ReturnBookScreen()
	{
		//Setting up components
		Issue_ID = new JTextField(20);
		userNameTextField = new JTextField(20);
		ISBNTextField = new JTextField(20);
		copyNumberTextField = new JTextField(20);
		isDamagedComboBox = new JComboBox<String>(damagedSelector);
		errorLabel = new JLabel();
		
		userNameTextField.setEditable(false);
		ISBNTextField.setEditable(false);
		copyNumberTextField.setEditable(false);
		
		//Main Panel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Return Book"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		inputPanel.add(new JLabel("Issue ID: "));
		inputPanel.add(Issue_ID);
		
		//panel for ISBN and copy number
		JPanel ISBNPanel = new JPanel();
		ISBNPanel.add(new JLabel("ISBN"));
		ISBNPanel.add(ISBNTextField);
		ISBNPanel.add(new JLabel("Copy #"));
		ISBNPanel.add(copyNumberTextField);
		
		//USERNAME
		JPanel userPanel = new JPanel();
		userPanel.add(new JLabel("User Name: "));
		userPanel.add(userNameTextField);
		
		//Damaged Selector
		JPanel damagePanel = new JPanel();
		damagePanel.add(new JLabel("Damaged?"));
		damagePanel.add(isDamagedComboBox);
		
		//Submit Button
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executeReturn();
			}
		});
		
		//Adding the components
		inputPanel.add(ISBNPanel);
		inputPanel.add(userPanel);
		inputPanel.add(damagePanel);
		inputPanel.add(submitButton);
		inputPanel.add(errorLabel);
		add(inputPanel);
		
		// Set up the charge panel
		chargePanel = new JPanel();
		chargePanel.setLayout(new BoxLayout(chargePanel, BoxLayout.Y_AXIS));
		chargePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Charge User"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// Add the input fields to the charge panel
		JPanel chargeIDPanel = new JPanel();
		chargeIDPanel.add(new JLabel("Charge amount:"));
		chargeAmountTextField = new JTextField(10);
		chargeIDPanel.add(chargeAmountTextField);
		chargeUserButton = new JButton("Charge");
		chargeUserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chargeUser(userNameTextField.getText(), chargeAmountTextField.getText());
			}
		});
		chargeIDPanel.add(chargeUserButton);
		chargePanel.add(chargeIDPanel);
		chargeVerificationLabel = new JLabel("");
		chargePanel.add(chargeVerificationLabel);
	}
	
	/**
	 * This is what happens when you press the button to return a book.
	 */
	private void executeReturn() {
		
		// Sanitization
		if (!Pattern.matches("\\d+", Issue_ID.getText())) {
			errorLabel.setText("Invalid Issue ID");
			return;
		} else {
			errorLabel.setText("");
		}
		int issueID = Integer.parseInt(Issue_ID.getText());
		boolean penalty = "Y".equals(isDamagedComboBox.getSelectedItem());
		
		// Query information about the issue
		String IISBN = null;
		int ICopyNumber = -1;
		String IUserName = null;
		String query = "SELECT IISBN, ICopyNumber, IUserName FROM ISSUE WHERE (IssueID = " + issueID + ")";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			if(rs.next()) {
				errorLabel.setText("");
				IISBN = rs.getString("IISBN");
				ICopyNumber = rs.getInt("ICopyNumber");
				IUserName = rs.getString("IUserName");
			} else {
				errorLabel.setText("Issue ID does not exist");
				return;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		// Show the information about the query to the user
		userNameTextField.setText(IUserName);
		ISBNTextField.setText(IISBN);
		copyNumberTextField.setText("" + ICopyNumber);
		
		// Handle returning the book
		query = "UPDATE BOOK_COPY SET IsCheckedOut = \'N\', IsDamaged = \'" + (penalty ? "Y" : "N") +
				"\' WHERE (BISBN = \'" + IISBN + "\') AND (CopyNumber = " + ICopyNumber + ")";
		//System.out.println(query);
		CS4400T74Frontend.ExecuteWriteQuery(query);
		query = "SELECT DATEDIFF(CURDATE(), ReturnDate) AS DaysPastDue FROM ISSUE WHERE (IssueID = " + issueID + ")";
		rs = CS4400T74Frontend.ExecuteReadQuery(query);
		int daysPastDue = -1;
		try {
			if(rs.next()) {
				daysPastDue = rs.getInt("DaysPastDue");
			} else {
				throw new SQLException("Could not query date diff for transaction");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		// Assign penalty to user
		double newPenalty = 0;
		if(daysPastDue > 0) {
			newPenalty = 0.5 * daysPastDue;
		}
		
		// Update penalty amount
		query = "UPDATE STUDENT_FACULTY SET Penalty = Penalty + " + newPenalty +
				"WHERE UserName = \'" + IUserName + "\'";
		CS4400T74Frontend.ExecuteWriteQuery(query);
		query = "UPDATE STUDENT_FACULTY SET IsDebarred = \'Y\'" +
				"WHERE (Penalty > 100) AND (Username = \'" + IUserName + "\')";
		CS4400T74Frontend.ExecuteWriteQuery(query);
		
		// Show the screen for charge penalty
		if(penalty) {
			if(chargePanel.getParent() == null) {
				chargeVerificationLabel.setText("");
				add(chargePanel);
			}
		} else {
			remove(chargePanel);
		}
		errorLabel.setText("Successfully returned book");
		
		revalidate();
		repaint();
	}
	
	/**
	 * Charge a user some amount of money
	 * @param username The user to charge
	 * @param amount The amount to charge, in cents
	 */
	private void chargeUser(String username, String amount) {
		
		// Validate input
		if(!Pattern.matches("\\d+(\\.\\d+)?", amount)) {
			errorLabel.setText("Invalid charge amount, must be float");
			return;
		}
		double chargeAmount = Double.parseDouble(amount);
		
		// Update penalty
		String query = "UPDATE STUDENT_FACULTY SET Penalty = Penalty + " + chargeAmount +
				" WHERE Username = \'" + username + "\'";
		CS4400T74Frontend.ExecuteWriteQuery(query);
		query = "UPDATE STUDENT_FACULTY SET IsDebarred = \'Y\'" +
				" WHERE (Penalty > 100) AND (Username = \'" + username + "\')";
		CS4400T74Frontend.ExecuteWriteQuery(query);
		
		chargeVerificationLabel.setText("Charged " + username + " $" + amount);
	}
}
