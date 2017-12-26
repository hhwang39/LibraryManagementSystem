import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * This is the frquent user report screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class FrequentUserReportScreen extends JPanel{
	public FrequentUserReportScreen() {
		//counter for how many jan is there
		int count = 0;
		ArrayList<String> nameList = new ArrayList<>();
		ArrayList<Integer> countList = new ArrayList<>();
		Border raisedbevel, loweredbevel;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compound = BorderFactory.createCompoundBorder(
                raisedbevel, loweredbevel);
		String query = "SELECT Name, COUNT(Name), MONTH(DateOfIssue) " +
						"FROM (STUDENT_FACULTY, ISSUE) " +
						"WHERE (IUserName = UserName AND (MONTH(DateOfIssue) = 1 OR MONTH(DateOfIssue) = 2)) " +					
						"GROUP BY Name, MONTH(DateOfIssue) " +
						"HAVING (COUNT(Name) >= 10) " +
						"ORDER BY MONTH(DateOfIssue), COUNT(Name) DESC ";
		
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			while (rs.next()) {
				nameList.add(rs.getString("Name"));
				countList.add(rs.getInt(2));
				if (rs.getInt(3) == 1) {
					count++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		JPanel tabPanel = new JPanel();
		GridLayout layout = new GridLayout(0, 3);
		//layout.setHgap(10);
		//layout.setVgap(10);
		tabPanel.setLayout(layout);
		tabPanel.removeAll();
		//add month
		JLabel jL = new JLabel("Month");
		jL.setBorder(compound);
		tabPanel.add(jL);
		//user name
		jL = new JLabel("User Name");
		jL.setBorder(compound);
		tabPanel.add(jL);
		// # of check outs
		jL = new JLabel("#checkouts");
		jL.setBorder(compound);
		tabPanel.add(jL);
		int c1 = count;
		int c2 = nameList.size() - count;
		if (count > 5) {
			c1 = 5;
		}
		if (c2 > 5) {
			c2 = 5;
		}
		for (int i = 0; i < c1; ++i) {
			//month
			jL = new JLabel("Jan");
			jL.setBorder(compound);
			tabPanel.add(jL);
			//name
			jL = new JLabel(nameList.get(i));
			jL.setBorder(compound);
			tabPanel.add(jL);
			//count
			jL = new JLabel(String.valueOf(countList.get(i)));
			jL.setBorder(compound);
			tabPanel.add(jL);
		}
		for (int i = count; i < count + c2; ++i) {
			//month
			jL = new JLabel("Feb");
			jL.setBorder(compound);
			tabPanel.add(jL);
			//name
			jL = new JLabel(nameList.get(i));
			jL.setBorder(compound);
			tabPanel.add(jL);
			//count
			jL = new JLabel(String.valueOf(countList.get(i)));
			jL.setBorder(compound);
			tabPanel.add(jL);
		}
		add(tabPanel);
	}
}
