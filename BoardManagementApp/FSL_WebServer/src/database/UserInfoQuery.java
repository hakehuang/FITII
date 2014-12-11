package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import struct.Person;

public class UserInfoQuery {

	/**
	 * Get person information from table by CoreID
	 * 
	 * @param CoreID
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Person getPersonInfo(String CoreID) throws SQLException,
			ClassNotFoundException {
		String name, DeptCode, Location = "";
		Person p = null;
		Statement stmt = connection.conn();
		String command = "select * from userinfo where `CoreID` ='" + CoreID + "'";

		// System.out.println(command);

		ResultSet rs = stmt.executeQuery(command);
		while (rs.next()) {
			System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t"
					+ rs.getString(3) + "\t" + rs.getString(4) + "\t"
					+ rs.getString(5));
			name = rs.getString(2);
			DeptCode = rs.getString(3);
			Location = rs.getString(4);
			p = new Person(CoreID, name, DeptCode, Location);
		}
		connection.close();
		return p;
	}

	/*
	 * public static Person modifyPersonInfo(String name, String mAge) throws
	 * ClassNotFoundException, SQLException { String a, b, allString = "";
	 * Person p = null; int c = Integer.parseInt(mAge); conn(); String command =
	 * "Update people SET age ='"+c+"'WHERE name='" + name+"'";
	 * System.out.println(command); stmt.executeUpdate(command); p =
	 * getPersonInfo(name); return p; }
	 */
	/**
	 * add new user into table
	 * 
	 * @param p
	 * @param password
	 */
	public static void Register(Person p, String password) throws SQLException {
		Statement stmt;
		try {
			stmt = connection.conn();
			String command = "Insert INTO userinfo VALUES('" + p.getCoreID()
					+ "','" + p.getName() + "','" + p.getDeptCode() + "','"
					+ p.getLocation() + "','" + password + "')";

			System.out.println(command);
			stmt.executeUpdate(command);
			connection.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Verify the password of user
	 * 
	 * @param CoreID
	 * @param Password
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean SignInVerification(String CoreID, String Password)
			throws SQLException {
		String pw = "";
		Statement stmt;
		try {
			stmt = connection.conn();
			String command = "select password from userinfo where CoreID='"
					+ CoreID + "'";
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				pw = rs.getString(1);
			}
			connection.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(command);

		System.out.println("get Pw:" + pw + "   password:" + Password);
		if (pw.equals(Password))
			return true;
		else
			return false;
	}

	/**
	 * Put users' information into a list
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static List<Person> getUserList() throws SQLException {

		List<Person> UserList = new ArrayList<Person>();
		System.out.println(UserList);
		// build person list
		try {
			Statement stmt = connection.conn();
			ResultSet rs = stmt.executeQuery("select * from userinfo");
			Person p = null;
			while (rs.next()) {
				String CoreID, name, DeptCode, Location = "";
				System.out.println(rs.getString(1) + "\t" + rs.getString(2)
						+ "\t" + rs.getString(3) + "\t" + rs.getString(4)
						+ "\t" + rs.getString(5));
				CoreID = rs.getString(1);
				name = rs.getString(2);
				DeptCode = rs.getString(3);
				Location = rs.getString(4);
				p = new Person(CoreID, name, DeptCode, Location);
				UserList.add(p);
			}
			System.out.println(UserList);

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return UserList;
		}
		connection.close();
		return UserList;

	}
}
