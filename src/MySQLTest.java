import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Test MySQL commands with a remote server
 * @author andrew
 * @version 1.0
 */
public class MySQLTest {
	
	/** Login parameters */
	private static final String URL  = "jdbc:mysql://academic-mysql.cc.gatech.edu/cs4400_Group_74";
	private static final String USER = "cs4400_Group_74";
	private static final String PASS = "lI3v6bx2";
	
	private static final String TEST_SQL = "SELECT * FROM STUDENT_FACULTY;";
	
	/**
	 * Tester for the MySQL connection
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Attempt to establish a connection with the MySQL server
		Connection conn = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(URL, USER, PASS);
			if(!conn.isClosed()) {
				
				// Established a connection!
				Statement st = conn.createStatement();
				rs = st.executeQuery(TEST_SQL);
				if(st.execute(TEST_SQL)) {
					rs = st.getResultSet();
					while(rs.next()) {
						String s = rs.getString("UserName");
						Date d = rs.getDate("DOB");
						System.out.println(s + " : " + d);
					}
				} else {
					System.out.println("There was an error executing the SQL statement");
				}
			} else {
				System.out.println("Error connecting to " + URL);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(conn != null) {
					conn.close();
				}
				if(rs != null) {
					rs.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
}
