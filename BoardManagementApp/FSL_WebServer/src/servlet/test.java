package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import struct.Person;
import database.UserInfoQuery;

/**
 * Servlet implementation class test
 */
@WebServlet("/test")
public class test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer sb = new StringBuffer("");
		String result = "";
		PrintWriter pw = response.getWriter();	
		pw.write("+++++Test+++++ Version:11:04am 17/12</br>");
		pw.write(request.getSession().getServletContext().getRealPath("/")+"</br>");
		try {
			Person p = UserInfoQuery.getPersonInfo("b12345");
			pw.write("person name"+p.getName());
			pw.write("person info:"+p.getLocation());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			pw.write(e.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			pw.write(e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
