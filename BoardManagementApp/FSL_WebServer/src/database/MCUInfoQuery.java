package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;
import struct.MCUnit;

public class MCUInfoQuery {

	/**
	 * Get MCU information by ID
	 * 
	 * @param ID
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static MCUnit getMCUInfo(String ID) throws SQLException,
			ClassNotFoundException {
		MCUnit mcu = null;
		Statement stmt = connection.conn();
		String command = "select * from mcuinfo where `ID` ='" + ID
				+ "' OR `Board Number` = '" + ID + "'";

		// System.out.println(command);

		ResultSet rs = stmt.executeQuery(command);
		while (rs.next()) {

			mcu = new MCUnit(rs.getString(1), rs.getString(2), rs.getString(3),
					rs.getString(4), rs.getString(5), rs.getString(6),
					rs.getString(7), rs.getString(8), rs.getString(9),
					rs.getString(10),rs.getString(11));
		}
		connection.close();
		return mcu;
	}
/**
 * Search for a list of boards
 * @param query
 * @return
 * @throws SQLException
 */
	public static List<MCUnit> searchMCUnitList(String query) throws SQLException {

		List<MCUnit> mList = new ArrayList<MCUnit>();
		query = query.replaceAll(" ", "");
		// build person list
		try {
			Statement stmt = connection.conn();
			ResultSet rs = stmt
					.executeQuery("select * from mcuinfo where `description` LIKE '%"
							+ query + "%' OR `Board Number` LIKE '%" + query
							+ "%' ORDER BY `OwnerRegisterDate` DESC");
			MCUnit mcu = null;
			while (rs.next()) {

				mcu = new MCUnit(rs.getString(1), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8),
						rs.getString(9), rs.getString(10),rs.getString(11));
				mList.add(mcu);
			}

			// System.out.println(mList);

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return mList;
		}
		connection.close();
		return mList;

	}
	/**
	 * Get list of MCUnit relate to CoreID
	 * @return
	 * @throws SQLException
	 */
	public static List<MCUnit> getMCUnitList(String coreID) throws SQLException {

		List<MCUnit> mList = new ArrayList<MCUnit>();
		// build person list
		try {
			Statement stmt = connection.conn();
			ResultSet rs = stmt
					.executeQuery("select * from mcuinfo where `OwnerID` = '"
							+ coreID + "' OR `LastOwner` LIKE '%" + coreID
							+ "%' ORDER BY `OwnerRegisterDate` DESC");
			MCUnit mcu = null;
			while (rs.next()) {

				mcu = new MCUnit(rs.getString(1), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getString(7), rs.getString(8),
						rs.getString(9), rs.getString(10),rs.getString(11));
				mList.add(mcu);
			}

			// System.out.println(mList);

		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return mList;
		}
		connection.close();
		return mList;

	}
/**
 * change owner of the board
 * @param CoreID
 * @param ID
 * @param lastOwner
 * @return
 * @throws SQLException
 * @throws ClassNotFoundException
 */
	public static MCUnit register(String CoreID, String ID, String lastOwner)
			throws SQLException, ClassNotFoundException {
		MCUnit mcu = getMCUInfo(ID);
		String Owners = mcu.getLastOwner();
		if (Owners.contains(lastOwner)) {

			Owners = Owners.replaceAll(";" + lastOwner, "");
		}
		Owners = lastOwner + ";" + Owners;
		Statement stmt = connection.conn();
		String command = "Update mcuinfo SET `OwnerID` ='"
				+ CoreID
				+ "', `LastOwner` = '"
				+ Owners
				+ "', `OwnerRegisterDate` = '"
				+ (new java.text.SimpleDateFormat("yyyy-MM-dd HH/" + ""
						+ ":mm:ss")).format(new Date()) + "' WHERE ( `ID` ='"
				+ ID + "')";
		//System.out.println(command);
		stmt.executeUpdate(command);
		mcu = getMCUInfo(ID);
		return mcu;
	}
/**
 * Modify board infomation or add a new board into database
 * @param jsonObject
 * @return
 * @throws SQLException
 * @throws ClassNotFoundException
 */
	public static String modifyInfo(JSONObject jsonObject) throws SQLException,
			ClassNotFoundException {
		String id = "";
		Statement stmt = connection.conn();
		String command = "";
		// add new unit
		if (jsonObject.get("Mode").equals("add")) {
			command = "INSERT INTO mcuinfo (`description`, `Master chip on board`, `Board Rev`, `Schematic Rev`, `Pic`, `OwnerID`, `OwnerRegisterDate`, `Board Number`,`Last Update`) VALUES ('"
					+ jsonObject.getString("description")
					+ "','"
					+ jsonObject.getString("Master chip on board")
					+ "','"
					+ jsonObject.getString("BoardRev")
					+ "','"
					+ jsonObject.getString("SchematicRev")
					+ "','"
					+ jsonObject.getString("Pic")
					+ "','"
					+ jsonObject.getString("Editor")
					+ "','"
					+ (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
							.format(new Date())
					+ "','"
					+ jsonObject.getString("BoardNumber") 
					+ "','"
					+ (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
							.format(new Date())					
					+ "')";
			stmt.executeUpdate(command);

			id = getMCUInfo(jsonObject.getString("BoardNumber")).getID();

			// check if the mcu has been successfully added; // modify unit
		} else {
			command = "UPDATE mcuinfo SET `description` = '"
					+ jsonObject.getString("description")
					+ "',  `Master chip on board` = '"
					+ jsonObject.getString("Master chip on board")
					+ "',  `Board Rev` = '" + jsonObject.getString("BoardRev")
					+ "',  `Schematic Rev` = '"
					+ jsonObject.getString("SchematicRev") + "',  `Pic` = '"
					+ jsonObject.getString("Pic") +"', `Last Update` = '"
					+ (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
							.format(new Date())	
					+"' WHERE ( `ID` ='"
					+ jsonObject.getString("ID") + "')";
			stmt.executeUpdate(command);

			id = jsonObject.getString("ID");

		}

		return id;
	}
}
