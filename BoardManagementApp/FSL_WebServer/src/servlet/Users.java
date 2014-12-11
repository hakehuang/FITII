package servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.UserInfoQuery;
import struct.Person;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Users")
public class Users extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Users() {
		super();
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
		//pw.write("==========User Servlet=========");
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
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Check whether there is a JSON object send by client side
		if (result.length() > 1) {
			JSONObject jsonObject = JSONObject.fromObject(result);
			//Sign Up
			JSONObject jsonReply = new JSONObject();
			if(jsonObject.containsKey("Name")&&jsonObject.size()==6){
				 jsonReply = SignUp(jsonObject);	
			}else{//Sign in
				 jsonReply = Verify(jsonObject);	
			}			
			
			System.out.println(jsonReply);
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
	private JSONObject SignUp(JSONObject jsonObject){
		String CoreID = jsonObject.getString("CoreID");
		JSONObject jsonReply = new JSONObject();
		Person p = null;
		String status ="";
		boolean verify = false;
		try {
			p = UserInfoQuery.getPersonInfo(CoreID);
			if(p!=null){// if the CoreID already exist
				status = "Failed! The user has already exist.";
			}else{// put user into table
				p = new Person(CoreID, jsonObject.getString("Name"), jsonObject.getString("DeptID"), jsonObject.getString("Location"),jsonObject.getString("Phone"));
				UserInfoQuery.Register(p,jsonObject.getString("Password"));
				status = "Succeed";
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonReply = p.getJSON();
		jsonReply.put("Status", status);
		return jsonReply;
	}
	private JSONObject Verify(JSONObject jsonObject){
		String CoreID = jsonObject.getString("CoreID");
		JSONObject jsonReply = new JSONObject();
		Person p = null;
		boolean verify = false;
		try {
			p = UserInfoQuery.getPersonInfo(CoreID);
			//Verify the sign in request.
			if(jsonObject.containsKey("Password")){
				verify = UserInfoQuery.SignInVerification(CoreID, jsonObject.getString("Password"));
				//System.out.println("Verify:  "+verify);
				//System.out.println(jsonObject.getString("Password"));
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (p != null) {
			// build Json
			jsonReply = p.getJSON();
			jsonReply.put("SignIn", String.valueOf(verify));
			
		} else {
			jsonReply.put("name", "Cannot found");
			jsonReply.put("DeptCode", "Cannot found");
			jsonReply.put("Location", "Cannot found");

		}
		return jsonReply;
	}

}
