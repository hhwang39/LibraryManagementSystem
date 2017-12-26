import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This is the requestextension screen
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class RequestExtensionScreen extends JPanel {
	//instances
	private JTextField Issue_ID;
	private JTextField OriCheckDate;
	private JTextField CurExtensionDate;
	private JTextField NewExtensionDate;
	private JTextField CurRetDate;
	private JTextField NewEstimatedRetDate;
	private Date newExtenDate;
	private Date newRetDate;
	private int count;
	private JLabel errorLabel;
	public RequestExtensionScreen() {
		errorLabel = new JLabel("");
		newExtenDate = new Date(2);
		newRetDate = new Date(1);
		//initializing all textfield;
		Issue_ID = new JTextField(20);
		OriCheckDate = new JTextField(15);
		CurExtensionDate = new JTextField(15);
		NewExtensionDate = new JTextField(15);
		CurRetDate = new JTextField(15);
		NewEstimatedRetDate = new JTextField(15);
		//disable edits
		OriCheckDate.setEditable(false);
		CurExtensionDate.setEditable(false);
		NewExtensionDate.setEditable(false);
		CurRetDate.setEditable(false);
		NewEstimatedRetDate.setEditable(false);
		//buttons
		// submit button for issue id
		JButton submit_id_Button = new JButton("Submit");
		submit_id_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				issueIDcheck();
			}
		});
		// submit button for update
		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateRetDate();
			}
		});
		// Title
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("RequestExtension"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
		//panel for issue id
		JPanel issuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//issuePanel.setLayout(new FlowLayout());
		issuePanel.add(new JLabel("Enter your issue_id"));
		issuePanel.add(Issue_ID);
		issuePanel.add(submit_id_Button);
		//create Panel for original check out date
		JPanel oriPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oriPanel.add(new JLabel("Original Checkout Date"));
		oriPanel.add(OriCheckDate);
		//panel for extension + current return date
		JPanel extcurPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		extcurPanel.add(new JLabel("Current Extension Date"));
		extcurPanel.add(CurExtensionDate);
		extcurPanel.add(new JLabel("Current Return Date"));
		extcurPanel.add(CurRetDate);
		//panel new extension + new estimated date
		JPanel newextcurPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		newextcurPanel.add(new JLabel("New Extension Date"));
		newextcurPanel.add(NewExtensionDate);
		newextcurPanel.add(new JLabel("New Estimated Return Date"));
		newextcurPanel.add(NewEstimatedRetDate);
		//add all panels
		inputPanel.add(issuePanel);
		inputPanel.add(oriPanel);
		inputPanel.add(extcurPanel);
		inputPanel.add(newextcurPanel);
		inputPanel.add(submitButton);
		inputPanel.add(errorLabel);
		add(inputPanel);
		//add(submitButton);
	}
	/**
	 * given issue id print all the dates needed
	 */
	private void issueIDcheck() {
		String query = "SELECT DateofIssue, ReturnDate, ExtensionDate, IISBN, ICopyNumber, CountOfExtension FROM " +
				"ISSUE, BOOK_COPY WHERE (IssueID = " + Issue_ID.getText() + ") AND " +
				"(DATEDIFF(ReturnDate, CURDATE()) > 0) " + 
				"AND (IISBN = BISBN) AND (ICopyNumber = CopyNumber) " +
				"AND IUserName = \'" + CS4400T74Frontend.CurrentUser + "\'" +
				" AND IsCheckedOut = \'Y\';";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		//arbitrary Date set. Just setting Date to some random 
		Date cDate = new Date(1);
		Date rDate = new Date(2);
		Date eDate = new Date(3);
		String isbn = "";
		int copyNum = 0;
		Calendar c = Calendar.getInstance();
		//get data
		try {
			if(rs.next()) {
				cDate = rs.getDate("DateofIssue");
				rDate = rs.getDate("ReturnDate");
				eDate = rs.getDate("ExtensionDate");
				isbn = rs.getString("IISBN");
				copyNum = rs.getInt("ICopyNumber");
				count = rs.getInt("CountOfExtension");
				System.out.println(isbn);
				if (checkifValidBook(isbn, copyNum) && count != 2) {				
					newExtenDate.setTime(Calendar.getInstance().getTimeInMillis());
					c.add(Calendar.DATE, 14);
					newRetDate.setTime(c.getTimeInMillis());
					if (eDate == null) {
						CurExtensionDate.setText("none");
					} else {
						CurExtensionDate.setText(eDate.toString());
					}
					OriCheckDate.setText(cDate.toString());
					CurRetDate.setText(rDate.toString());
					NewExtensionDate.setText(newExtenDate.toString());
					NewEstimatedRetDate.setText(newRetDate.toString());
				} else {
					errorLabel.setText("Transaction ID not found in database");
					revalidate();
				}
			} else {
				errorLabel.setText("This book is not checked out by you");
				// User doesn't exist
			}
			//setting fields from getting values
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * update the query to reflect new return date
	 */
	private void updateRetDate() {
		if (!NewEstimatedRetDate.getText().isEmpty()) {
			count++;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String d = sdf.format(newRetDate);
			String d2 = sdf.format(newExtenDate);
			String query = "UPDATE ISSUE SET ReturnDate = \'" + d +
						"\', ExtensionDate = \'" +  d2 + "\', CountOfExtension = " + String.valueOf(count) +
						" WHERE " + "IssueID = " + Issue_ID.getText() + ";";
			CS4400T74Frontend.ExecuteWriteQuery(query);
		}
		StudentViewScreen.tabs.setSelectedComponent(StudentViewScreen.searchBookScreen);
		JOptionPane.showMessageDialog(SearchBookScreen.this_instance,
				"Extension request successful",
				"Extension Request",
				JOptionPane.INFORMATION_MESSAGE);
	}
	/**
	 * check if the book user tries to extend does not have future requester
	 * @param isbn, copynumber
	 * @return if it is valid
	 */
	private boolean checkifValidBook(String ISBN, int CopyNumber) {
		String query = "SELECT FutureRequester FROM BOOK_COPY WHERE (BISBN = " +
						"\'" + ISBN + "\') AND (CopyNumber = " + String.valueOf(CopyNumber) + ") AND +" +
								"(FutureRequester Is NULL);";
		//System.out.println(query);
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			if (rs.next()) {
				//System.out.println("returning");	
				return true;
			} else {
				//String FutureRequester = rs.getString("FutureRequester");
				//System.out.println(FutureRequester);
				errorLabel.setText("Can't find it or someone else future holds it");
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		errorLabel.setText("Sorry boy someone else future hold it");
		return false;
	}
}
