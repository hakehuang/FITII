package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class connection {
	public static Statement stmt;
	public static Connection conn;

	/**
	 * Connect to database
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Statement conn() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/board_app",
				"root", "Happy123");
		stmt = conn.createStatement();
		return stmt;
	}

	/**
	 * Close the connection
	 * 
	 * @throws SQLException
	 */
	public static void close() throws SQLException {
		conn.close();
	}
}
