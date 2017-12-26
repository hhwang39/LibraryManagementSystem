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
 * This is the track book screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class TrackBookScreen extends JPanel {

	/** Instance variables used by the logic screen */
	private JTextField ISBN;
	private JTextField Floor_Number;
	private JTextField Shelf_Number;
	private JTextField Aisle_Number;
	private JTextField Subject;
	/**
	 * Create a new login screen
	 */
	public TrackBookScreen() {
		
		// Initialize the properties and instance variables
		ISBN = new JTextField(20);
		Floor_Number = new JTextField(10);
		Shelf_Number = new JTextField(8);
		Aisle_Number = new JTextField(10);
		Subject = new JTextField(10);
		Floor_Number.setEditable(false);
		Aisle_Number.setEditable(false);
		Shelf_Number.setEditable(false);
		Subject.setEditable(false);
		// inputPanel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Track Book Location"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		// Request Button
		JButton locateButton = new JButton("Locate");
		locateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//CS4400T74Frontend.ChangeScreen(CS4400T74Frontend.ScreenType.REGISTER_SCREEN);
				locateBooks();
			}
		});
		//creating panel for ISBN
		JPanel fieldPanel = new JPanel();
		fieldPanel.add(new JLabel("ISBN"));
		fieldPanel.add(ISBN);
		fieldPanel.add(locateButton);
		//panel for floor and shelf number
		JPanel floorPanel = new JPanel();
		floorPanel.add(new JLabel("Floor Number"));
		floorPanel.add(Floor_Number);
		floorPanel.add(new JLabel("Shelf Number"));
		floorPanel.add(Shelf_Number);
		//panel for Aisle
		JPanel aislePanel = new JPanel();
		aislePanel.add(new JLabel("Aisle Number"));
		aislePanel.add(Aisle_Number);
		aislePanel.add(new JLabel("Subject"));
		aislePanel.add(Subject);
		//adding all the panels
		inputPanel.add(fieldPanel);
		inputPanel.add(floorPanel);
		inputPanel.add(aislePanel);
		add(inputPanel);
		//add(okButton);
	}
	/**
	* locate books and show floor number, aisle number, shelf and subject
	**/
	private void locateBooks() {
		//show sth
		String bookISBN = ISBN.getText();
		String sf_num = "";
		String ai_num = "";
		String sh_num = "";
		String su_name = "";
		String str = "SELECT Floor_Number, Aisle_Number, Shelf_Number, SubjectName FROM (FLOOR, SHELF, SUBJECT, BOOK)" +
		"WHERE (BShelf_Number = Shelf_Number)  AND (SubjectName = BSubjectName) AND (ISBN = \'" + bookISBN + "\');";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(str);
		try {
			if(rs.next()) {
				sf_num = rs.getString("Floor_Number");
				ai_num = rs.getString("Aisle_Number");
				sh_num = rs.getString("Shelf_Number");
				su_name = rs.getString("SubjectName");
				Floor_Number.setText(sf_num);
				Shelf_Number.setText(sh_num);
				Aisle_Number.setText(ai_num);
				Subject.setText(su_name);
			} else {
				// User doesn't exist
			}
			//setting fields from getting values
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
