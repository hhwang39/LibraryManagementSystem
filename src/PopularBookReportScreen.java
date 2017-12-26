import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * This is the popular book report screen for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class PopularBookReportScreen extends JPanel {
	public PopularBookReportScreen() {
		ArrayList<String> titleList = new ArrayList<>();
		ArrayList<Integer> countList = new ArrayList<>();
		Border raisedbevel, loweredbevel;
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		Border compound = BorderFactory.createCompoundBorder(
                raisedbevel, loweredbevel);
		//count for how mnay books are in jan
		int count = 0;
		JLabel jL = new JLabel();
		String query = "SELECT Title, MONTH(DateOfIssue), Count(Title) " +
						"FROM(BOOK , BOOK_COPY , ISSUE) " +	
						"WHERE ((CopyNumber = ICopyNumber) " +
						"AND (BISBN = ISBN) AND (IISBN = ISBN)) " +
						"AND ((MONTH(DateOfIssue) = 1) " +
						"OR (MONTH(DateOfIssue) = 2)) " +
						"GROUP BY Title, MONTH(DateOfIssue) " +
						"ORDER BY MONTH(DateOfIssue), COUNT(Title) DESC";
		ResultSet rs = CS4400T74Frontend.ExecuteReadQuery(query);
		try {
			while (rs.next()) {
				titleList.add(rs.getString("Title"));
				countList.add(rs.getInt(3));
				if (rs.getInt(2) == 1) {
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
		jL = new JLabel("Month");
		jL.setBorder(compound);
		tabPanel.add(jL);
		//title
		jL = new JLabel("Title");
		jL.setBorder(compound);
		tabPanel.add(jL);
		//check out
		jL = new JLabel("#checkouts");
		jL.setBorder(compound);
		tabPanel.add(jL);
		int c1 = 0;
		int c2 = 0;
		if (count > 3) {
			c1 = 3;
			c2 = titleList.size() - count;
		}
		if (c2 > 3) {
			c2 = 3;
		}
		if (titleList.size() != 0) {
			for (int i = 0; i < c1; ++i) {
				jL = new JLabel("Jan");
				jL.setBorder(compound);
				tabPanel.add(jL);
				String titleStr = "<html>";
				String left = titleList.get(i);
				while(left.length() > 18) {
					titleStr += left.substring(0, 18) + "<br>";
					left = left.substring(18);
				}
				titleStr += left + "</html>";
				jL = new JLabel(titleStr);
				jL.setBorder(compound);
				tabPanel.add(jL);
				jL = new JLabel(String.valueOf(countList.get(i)));
				jL.setBorder(compound);
				tabPanel.add(jL);
			}
			for (int j = 0; j < c2; ++j) {
				jL = new JLabel("Feb");
				jL.setBorder(compound);
				tabPanel.add(jL);
				String titleStr = "<html>";
				String left = titleList.get(j + count);
				while(left.length() > 18) {
					titleStr += left.substring(0, 18) + "<br>";
					left = left.substring(18);
				}
				titleStr += left + "</html>";
				jL = new JLabel(titleStr);
				jL.setBorder(compound);
				tabPanel.add(jL);
				jL = new JLabel(String.valueOf(countList.get(j + count)));
				jL.setBorder(compound);
				tabPanel.add(jL);
			}
		} else {
			tabPanel.add(new JLabel("We are sorry no results found"));
		}
		add(tabPanel);
	}
}
