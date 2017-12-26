import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

/**
 * This is the register screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class RegisterScreen extends JPanel {
	
	/** Instance variables used by the register screen */
	private JTextField userField;
	private JPasswordField passwordField, confirmField;
	private JLabel errorLabel;
	
	/**
	 * Create a new register screen
	 */
	public RegisterScreen() {
		
		// Initialize the properties and instance variables
		userField = new JTextField(20);
		passwordField = new JPasswordField(20);
		confirmField = new JPasswordField(20);
		errorLabel = new JLabel("");
		
		// Title
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("New User Registration"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// Username field
		JPanel fieldPanel = new JPanel();
		fieldPanel.add(new JLabel("User:"));
		fieldPanel.add(userField);
		inputPanel.add(fieldPanel);
		
		// Password field
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(new JLabel("Password:"));
		passwordPanel.add(passwordField);
		inputPanel.add(passwordPanel);
		
		// Password field
		JPanel confirmPanel = new JPanel();
		confirmPanel.add(new JLabel("Confirm password:"));
		confirmPanel.add(confirmField);
		inputPanel.add(confirmPanel);
		inputPanel.add(errorLabel);
		add(inputPanel);
		
		// Register button
		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewUser();
			}
		});
		add(registerButton);
		
		// Cancel button
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.LOGIN_SCREEN);
			}
		});
		add(cancelButton);
	}
	
	/**
	 * Attempt to log in, given the credentials
	 */
	private void createNewUser() {
		
		String username = userField.getText();
		String password = passwordField.getText();
		
		// Is the username non-empty?
		if("".equals(username)) {
			errorLabel.setText("Must input a user name!");
			return;
		}
		
		// Do the passwords match?
		if(!password.equals(confirmField.getText())) {
			errorLabel.setText("Passwords do not match!");
			return;
		}
		
		// Is the password non-empty?
		if("".equals(password)) {
			errorLabel.setText("Cannot have an empty password!");
			return;
		}
		
		// Does the user already exist?
		boolean userExists = false;
		String query = "SELECT Password FROM USER WHERE UserName=\'" + username + "\'";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			if(rs.next()) {
				userExists = true;
			} else {
				// User doesn't exist
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		if(userExists) {
			errorLabel.setText("User already exists!");
			return;
		}
		
		// Create user
		query = "INSERT INTO USER (UserName, Password) VALUES (\'" + username + "\', \'" + password + "\')";
		CS4400T74Frontend.ExecuteWriteQuery(query);
		
		// Change to the create profile screen
		CS4400T74Frontend.CreatingUser = username;
		CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.CREATEPROFILE_SCREEN);
	}
}
