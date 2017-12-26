import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This is the create profile screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class CreateProfileScreen extends JPanel {
	
	/** Definitions */
	private final static String[] genders = {"Male", "Female"};
	private static final String[] departments = {"Medical", "Literature", "Engineering", "Physics", "Math"};
	
	/** Instance variables used by the create profile screen */
	private JTextField firstNameField, lastNameField, dobField, emailField, addressField;
	private JComboBox<String> genderComboBox, departmentComboBox;
	private JCheckBox facultyCheckBox;
	private JLabel errorLabel;
	
	/**
	 * Create a new create profile screen
	 */
	public CreateProfileScreen() {
		
		// Initialize the properties and instance variables
		firstNameField = new JTextField(20);
		lastNameField = new JTextField(20);
		dobField = new JTextField(20);
		emailField = new JTextField(20);
		addressField = new JTextField(40);
		genderComboBox = new JComboBox<String>(genders);
		departmentComboBox = new JComboBox<String>(departments);
		departmentComboBox.setEnabled(false);
		facultyCheckBox = new JCheckBox();
		errorLabel = new JLabel("");
		
		// Title
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Create Profile"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		// First name field
		JPanel firstNamePanel = new JPanel();
		firstNamePanel.add(new JLabel("First Name:"));
		firstNamePanel.add(firstNameField);
		inputPanel.add(firstNamePanel);
		
		// Last name field
		JPanel lastNamePanel = new JPanel();
		lastNamePanel.add(new JLabel("Last Name:"));
		lastNamePanel.add(lastNameField);
		inputPanel.add(lastNamePanel);
		
		// Date of birth field
		JPanel dobPanel = new JPanel();
		dobPanel.add(new JLabel("DOB (yyyy-mm-dd):"));
		dobPanel.add(dobField);
		inputPanel.add(dobPanel);
		
		// Gender dropdown menu
		JPanel genderPanel = new JPanel();
		genderPanel.add(new JLabel("Gender:"));
		genderPanel.add(genderComboBox);
		inputPanel.add(genderPanel);
		
		// Email field
		JPanel emailPanel = new JPanel();
		emailPanel.add(new JLabel("Email:"));
		emailPanel.add(emailField);
		inputPanel.add(emailPanel);
		
		// Faculty check box
		JPanel facultyCheckboxPanel = new JPanel();
		facultyCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				departmentComboBox.setEnabled(facultyCheckBox.isSelected());
			}
		});
		facultyCheckboxPanel.add(new JLabel("Are you faculty?"));
		facultyCheckboxPanel.add(facultyCheckBox);
		inputPanel.add(facultyCheckboxPanel);
		
		// Department
		JPanel departmentPanel = new JPanel();
		departmentPanel.add(new JLabel("Department:"));
		departmentPanel.add(departmentComboBox);
		inputPanel.add(departmentPanel);
		
		// Address
		JPanel addressPanel = new JPanel();
		departmentPanel.add(new JLabel("Address:"));
		departmentPanel.add(addressField);
		inputPanel.add(addressPanel);
		
		inputPanel.add(errorLabel);
		add(inputPanel);
		
		// Submit button
		JButton submitButton = new JButton("Login");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				submitProfile();
			}
		});
		add(submitButton);
	}
	
	/**
	 * Attempt to log in, given the credentials
	 */
	private void submitProfile() {
		
		// Get the strings from the UI
		String username = CS4400T74Frontend.CreatingUser;
		String firstName = firstNameField.getText();
		String lastName = lastNameField.getText();
		String dob = dobField.getText();
		String gender = "Male".equals((String)genderComboBox.getSelectedItem()) ? "M" : "F";
		String email = emailField.getText();
		String isFaculty = facultyCheckBox.isSelected() ? "Y" : "N";
		String department = "Y".equals(isFaculty) ? (String)departmentComboBox.getSelectedItem() : "uGRAD";
		String address = addressField.getText();
		
		// Is the first name non-empty?
		if("".equals(firstName)) {
			errorLabel.setText("Must put in a first name!");
			return;
		}
		
		// Is the last name non-empty?
		if("".equals(lastName)) {
			errorLabel.setText("Must put in a last name!");
			return;
		}
		
		// Is the dob valid?
		if(!Pattern.matches("\\d{4}-\\d{2}-\\d{2}", dob)) {
			errorLabel.setText("Incorrect date format!");
			return;
		}
		
		// Is the email non-empty?
		if("".equals(email)) {
			errorLabel.setText("Must put in an email!");
			return;
		}
		
		// Is the address non-empty?
		if("".equals(address)) {
			errorLabel.setText("Must put in an address!");
			return;
		}
		
		// Create user
		String query = "INSERT INTO STUDENT_FACULTY (UserName, Name, DOB, Gender, Email, isFaculty, Address, Department, isDebarred, Penalty) VALUES (\'" +
				username + "\', \'" +
				firstName + " " + lastName + "\', \'" +
				dob + "\', \'" +
				gender + "\', \'" +
				email + "\', \'" +
				isFaculty + "\', \'" +
				address + "\', \'" +
				department + "\', \'N\', 0)";
		CS4400T74Frontend.ExecuteWriteQuery(query);
		
		// Go back to the login screen
		CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.LOGIN_SCREEN);
	}
}
