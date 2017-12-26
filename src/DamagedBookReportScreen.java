import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is the damaged book report screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class DamagedBookReportScreen extends JPanel {
	private String[] months = {"Jan", "Feb", "Mar"};
	private ArrayList<String> subjectList;
	private JPanel tabPanel;
	private JPanel inputPanel;
	private JComboBox<String> subject1ComboBox;
	private JComboBox<String> subject2ComboBox;
	private JComboBox<String> subject3ComboBox;
	private JComboBox<String> monthComboBox;
	/*
	 * Constructor
	 */
	public DamagedBookReportScreen() {
		subjectList = new ArrayList<>();
		tabPanel = new JPanel();
		//combo box for month
		monthComboBox = new JComboBox<String>(months);
		
		// show report Button
		JButton reportButton = new JButton("Show Report");
		reportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showReport();
			}
		});
		String query = "SELECT SubjectName FROM SUBJECT";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		String sub;
		try {
			while (rs.next()) {
				sub = rs.getString("SubjectName");
				subjectList.add(sub);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// List of subjects
		String[] subjectListArray = (String[]) subjectList.toArray(new String[subjectList.size()]);
		subject1ComboBox = new JComboBox<String>(subjectListArray);
		subject2ComboBox = new JComboBox<String>(subjectListArray);
		subject2ComboBox.setSelectedIndex(1);
		subject3ComboBox = new JComboBox<String>(subjectListArray);
		subject3ComboBox.setSelectedIndex(2);
		
		// inputPanel
		inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Damaged Book Report"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		//Month Panel
		JPanel monthPanel = new JPanel();
		monthPanel.add(new JLabel("Month"));
		monthPanel.add(monthComboBox);
		monthPanel.add(new JLabel("Subject 1:"));
		monthPanel.add(subject1ComboBox);
		
		//second month panel
		JPanel month2Panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		month2Panel.add(new JLabel("Subject 2:"));
		month2Panel.add(subject2ComboBox);
		
		//third month panel
		JPanel month3Panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		month3Panel.add(new JLabel("Subject 3:"));
		month3Panel.add(subject3ComboBox);
		
		//adding to inputPanel
		inputPanel.add(monthPanel);
		inputPanel.add(month2Panel);
		inputPanel.add(month3Panel);
		inputPanel.add(reportButton);
		inputPanel.add(tabPanel);
		add(inputPanel);
	}
	/*
	 * shows damaged book for chosen months and subjects
	 */
	private void showReport() {
		
		// This is the number of books in each subject
		HashMap<String, ArrayList<Book>> damagedBookMap = new HashMap<String, ArrayList<Book>>();
		HashMap<String, Integer> damagedPerSelectedMonth = new HashMap<String, Integer>();
		GridLayout layout = new GridLayout(0, 3);
		layout.setHgap(10);
		layout.setVgap(10);
		tabPanel.setLayout(layout);
		tabPanel.removeAll();
		tabPanel.add(new JLabel("Month"));
		tabPanel.add(new JLabel("Subject"));
		tabPanel.add(new JLabel("#damaged books"));
		String selectedMonth = (String) monthComboBox.getSelectedItem();
		
		// The subjects that will be queried
		ArrayList<String> subjects = new ArrayList<String>();
		subjects.add((String) subject1ComboBox.getSelectedItem());
		subjects.add((String) subject2ComboBox.getSelectedItem());
		subjects.add((String) subject3ComboBox.getSelectedItem());
		
		// Get damaged book counts per subject
		for(String subject : subjects) {
			String query = "SELECT CopyNumber, ISBN FROM BOOK, BOOK_COPY WHERE (BSubjectName = \'" + subject +
					"\') AND (IsDamaged = \'Y\') AND (BISBN = ISBN)";
			ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
			try {
				ArrayList<Book> damagedBooks = new ArrayList<Book>();
				while (rs.next()) {
					int copyNum = rs.getInt("CopyNumber");
					String ISBN = rs.getString("ISBN");
					Book b = new Book(null, ISBN, false, copyNum, false, false);
					damagedBooks.add(b);
				}
				damagedBookMap.put(subject, damagedBooks);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// Count damaged books per subject in the selected month
		for(String subject : subjects) {
			damagedPerSelectedMonth.put(subject, 0);
			for(Book b : damagedBookMap.get(subject)) {
				int damagedDate = -1;
				String query = "SELECT MAX(MONTH(ReturnDate)) AS DamageDate FROM ISSUE WHERE (YEAR(ReturnDate) = \'2015\') AND (ICopyNumber = " +
						b.copyNumber + ") AND (IISBN = \'" + b.ISBN + "\')";
				ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
				try {
					if(rs.next()) {
						damagedDate = rs.getInt("DamageDate");
					} else {
						throw new SQLException("This book was not checked out");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					continue;
				}
				
				// Is this book damaged in the selected month?
				String damageMonth = null;
				switch(damagedDate) {
				case 1:
					damageMonth = "Jan";
					break;
				case 2:
					damageMonth = "Feb";
					break;
				case 3:
					damageMonth = "Mar";
					break;
				default:
					damageMonth = "Other";
				}
				if(damageMonth.equals(selectedMonth)) {
					damagedPerSelectedMonth.put(subject, damagedPerSelectedMonth.get(subject) + 1);
				}
			}
		}
		
		// Create the table of books
		boolean printMonth = true;
		for(String subject : subjects) {
			
			// Only print the month once
			tabPanel.add(new JLabel(printMonth ? selectedMonth : ""));
			if(printMonth) {
				printMonth = false;
			}
			
			// Print the data
			tabPanel.add(new JLabel(subject));
			tabPanel.add(new JLabel("" + damagedPerSelectedMonth.get(subject)));
		}
		
		// Update the screen
		if(tabPanel.getParent() != null) {
			inputPanel.remove(tabPanel);
		}
		inputPanel.add(tabPanel);
		revalidate();
		repaint();
	}
}
