import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * This is the popular subject screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class PopularSubReportScreen extends JPanel {
	public PopularSubReportScreen() {
		Border raisedbevel, loweredbevel;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compound = BorderFactory.createCompoundBorder(
                raisedbevel, loweredbevel);
		ArrayList<String> subList = new ArrayList<>();
		ArrayList<Integer> countList = new ArrayList<>();
		//count for how mnay books are in jan
		int count = 0;
		String query = "SELECT SubjectName, Count(SubjectName), MONTH(DateOfIssue) " +
						"FROM (SUBJECT , BOOK , ISSUE) " +	
						"WHERE ((MONTH(DateOfIssue) = 1) OR (MONTH(DateOfIssue) = 2)) " +
						"AND (SubjectName = BSubjectName) " +
						"AND (IISBN = ISBN) " +
						"GROUP BY SubjectName, MONTH(DateOfIssue) " +
						"ORDER BY MONTH(DateOfIssue), COUNT(SubjectName) DESC";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			while (rs.next()) {
				subList.add(rs.getString("SubjectName"));
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
		//month
		JLabel k = new JLabel("Month");
		k.setBorder(compound);
		tabPanel.add(k);
		//top subject
		k = new JLabel("Top Subject");
		k.setBorder(compound);
		tabPanel.add(k);
		//# of check outs
		k = new JLabel("#checkouts");
		k.setBorder(compound);
		tabPanel.add(k);
		if (subList.size() != 0) {
			for (int i = 0; i < subList.size(); ++i) {
				if (i < count) {
					k = new JLabel("Jan");
					k.setBorder(compound);
					tabPanel.add(k);
				} else {
					k = new JLabel("Feb");
					k.setBorder(compound);
					tabPanel.add(k);
				}
				k = new JLabel(subList.get(i));
				k.setBorder(compound);
				tabPanel.add(k);
				k = new JLabel(String.valueOf(countList.get(i)));
				k.setBorder(compound);
				tabPanel.add(k);
			}
		} else {
			tabPanel.add(new JLabel("We are sorry no results found"));
		}
		add(tabPanel);
	}
}
