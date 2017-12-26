import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the main window for our MySQL frontend.
 * 
 * @author CS4400 Team 74
 * @version 1.0
 */
public class CS4400T74Frontend extends JFrame {
	
	/** Global constants used by the application */
	private static final Dimension SCREEN_SIZE = new Dimension(850, 600);
	private static final String APPLICATION_TITLE = "CS4400 Group 74 MySQL Frontend";
	private static final String URL  = "jdbc:mysql://academic-mysql.cc.gatech.edu/cs4400_Group_74";
	private static final String USER = "";
	private static final String PASS = "";
	
	/** Global variables used by the application */
	private static CardLayout layout;
	private static JPanel contentPanel;
	private static Connection conn = null;
	
	public static String CurrentUser;
	public static String CreatingUser;
	public static HashMap<String, ArrayList<Book>> bookMap;
	public static HashMap<String, HashMap<Integer, Integer>> availabilityMap;
	
	/** This enum describes the different screens accessible to the user */
	public static enum ScreenType {
		LOGIN_SCREEN,
		REGISTER_SCREEN,
		FUTURE_HOLD_SCREEN,
		CREATEPROFILE_SCREEN,
		STUDENTVIEW_SCREEN,
		STAFFVIEW_SCREEN
	};
	
	/**
	 * Create a new instance of the frontend window.
	 * 
	 * @param args Unused.
	 */
	public static void main(String[] args) {
		
		// Attempt to establish a connection with the MySQL server
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(URL, USER, PASS);
			if(conn.isClosed()) {
				System.out.println("Error connecting to " + URL);
			}
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to connect to database. Are you connected to the internet?", "Error connecting to database", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		
		// Main application window
		JFrame frame = new CS4400T74Frontend();
		frame.setVisible(true);

	}
	
	/**
	 * Construct this frontend application with a CardLayout panel
	 */
	public CS4400T74Frontend() {
		super(APPLICATION_TITLE);
		
		// Create the main content panel
		contentPanel = new JPanel();
		layout = new CardLayout();
		contentPanel.setLayout(layout);
		contentPanel.setPreferredSize(SCREEN_SIZE);
		
		// Add panels to the content panel
		contentPanel.add(new LoginScreen(), ScreenType.LOGIN_SCREEN.toString());
		contentPanel.add(new RegisterScreen(), ScreenType.REGISTER_SCREEN.toString());
		contentPanel.add(new CreateProfileScreen(), ScreenType.CREATEPROFILE_SCREEN.toString());
		contentPanel.add(new FutureHoldRequestScreen(), ScreenType.FUTURE_HOLD_SCREEN.toString());
		contentPanel.add(new StudentViewScreen(), ScreenType.STUDENTVIEW_SCREEN.toString());
		contentPanel.add(new StaffViewScreen(), ScreenType.STAFFVIEW_SCREEN.toString());
		// Add the content panel
		add(contentPanel);
		pack();
		
		// Finish up and set JFrame parameters
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	/**
	* Closes the screen
	**/
	public static void CloseScreen() {
		System.exit(0);
	}
	
	/**
	 * Change the screen that's being shown
	 */
	public static void ChangeScreen(ScreenType t) {
		layout.show(contentPanel, t.toString());
	}
	
	/**
	 * Execute a SQL query to the database, and return the result
	 */
	public static ResultSet ExecuteReadQuery(String query) {
		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if(st.execute(query)) {
				rs = st.getResultSet();
				return rs;
			} else {
				System.out.println("Error executing SQL statement!");
				return null;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Execute a SQL query to the database, and return the result
	 */
	public static void ExecuteWriteQuery(String query) {
		try {
			Statement st = conn.createStatement();
			st.executeUpdate(query);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
