package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.MCUInfoQuery;
import struct.MCUnit;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class MCUs
 */
@WebServlet("/MCUs")
public class MCUs extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MCUs() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		StringBuffer sb = new StringBuffer("");
		String result = "";
		PrintWriter pw = response.getWriter();
		// read the info send by client side
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					request.getInputStream(), "utf-8"));
			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Check whether there is a JSON object send by client side
		if (result.length() > 1) {
			JSONObject jsonObject = JSONObject.fromObject(result);
			MCUnit mcu = null;
			// get list of mcu
			if (jsonObject.containsKey("OwnerID")) {
				JSONArray jsonReply = new JSONArray();
				String CoreID = jsonObject.getString("OwnerID");
				List<MCUnit> mList = new ArrayList<MCUnit>();
				try {
					mList = MCUInfoQuery.getMCUnitList(CoreID);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (MCUnit unit : mList) {
					JSONObject JsonObj = unit.getJSON();
					jsonReply.add(JsonObj);
				}
				pw.write(jsonReply.toString());
			} else if (jsonObject.containsKey("Mode")) { //modify or add new board
				mcu = null;
				try {
					result = MCUInfoQuery.modifyInfo(jsonObject);
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(jsonObject.containsKey("Search")){
				JSONArray jsonReply = new JSONArray();
				String query = jsonObject.getString("Search");
				List<MCUnit> mList = new ArrayList<MCUnit>();
				try {
					mList = MCUInfoQuery.searchMCUnitList(query);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (MCUnit unit : mList) {
					JSONObject JsonObj = unit.getJSON();
					jsonReply.add(JsonObj);
				}
				pw.write(jsonReply.toString());
			} else if (jsonObject.size() == 1) { // read board info
				try {
					String ID = jsonObject.getString("UID");
					mcu = MCUInfoQuery.getMCUInfo(ID);

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (jsonObject.size() == 3) { // register
				String ID = jsonObject.getString("UID");
				String CoreID = jsonObject.getString("CoreID");
				String LastOwner = jsonObject.getString("LastOwner");
				try {
					mcu = MCUInfoQuery.register(CoreID, ID, LastOwner);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JSONObject jsonReply = new JSONObject();

			if (mcu != null) {
				// build Json
				jsonReply = mcu.getJSON();
			}else{
				jsonReply.put("ID", result);
			}
			// System.out.println(jsonReply);
			pw.write(jsonReply.toString());
		}

		pw.flush();
		pw.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
