import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is the login screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class LoginScreen extends JPanel {
	
	/** Instance variables used by the login screen */
	private JTextField userField;
	private JPasswordField passwordField;
	private JLabel errorLabel;
	
	/**
	 * Create a new login screen
	 */
	public LoginScreen() {
		
		// Initialize the properties and instance variables
		userField = new JTextField(20);
		passwordField = new JPasswordField(20);
		errorLabel = new JLabel("");
		
		// Title
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Login"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// Username field
		JPanel fieldPanel = new JPanel();
		fieldPanel.add(new JLabel("UserName:"));
		fieldPanel.add(userField);
		//userField.setText("fruit");
		userField.setText("aStaff1");
		inputPanel.add(fieldPanel);
		
		// Password field
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(new JLabel("Password:"));
		passwordPanel.add(passwordField);
		//passwordField.setText("fruit");
		passwordField.setText("staff123");
		inputPanel.add(passwordPanel);
		inputPanel.add(errorLabel);
		add(inputPanel);
		
		// Login button
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				attemptLogin();
			}
		});
		add(loginButton);
		
		// Register button
		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.REGISTER_SCREEN);
			}
		});
		add(registerButton);
	}
	
	/**
	 * Attempt to log in, given the credentials
	 */
	private void attemptLogin() {
		
		// Get password for the user
		String password = "", username = userField.getText();
		String query = "SELECT Password FROM USER WHERE UserName=\'" + username + "\'";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			if(rs.next()) {
				password = rs.getString("Password");
			} else {
				// User doesn't exist
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		// Does this user exist?
		if("".equals(password)) {
			errorLabel.setText("Invalid user!");
			return;
		}
		
		// Does password match typed password?
		if(!passwordField.getText().equals(password)) {
			errorLabel.setText("Incorrect password!");
			return;
		}
		errorLabel.setText("");
		
		// Set current user logged in
		CS4400T74Frontend.CurrentUser = username;
		
		// Determine if user is staff
		boolean isStaff = false;
		query = "SELECT UserName FROM STAFF WHERE UserName=\'" + username + "\'";
		rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			if(rs.next()) {
				isStaff = true;
			} else {
				// This is not staff
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		// Advance to the appropriate page
		if(isStaff) {
			// This is a staff, go to the staff tab view
			CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.STAFFVIEW_SCREEN);
		} else {
			// This is a student, go to book search page
			CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.STUDENTVIEW_SCREEN);
		}
	}
}
