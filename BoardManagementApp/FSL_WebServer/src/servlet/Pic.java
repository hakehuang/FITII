package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

/**
 * Servlet implementation class Pic
 */
@WebServlet("/Pic")
public class Pic extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Pic() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		System.out.println(request.getContentType());
		String temp = request.getSession().getServletContext().getRealPath("/")
				+ "temp";
		System.out.println("temp=" + temp);
		// the directory to store files
		String loadpath = request.getSession().getServletContext()
				.getRealPath("/")
				+ "Image";
		System.out.println("loadpath=" + loadpath);
		FileItemFactory factory = new DiskFileItemFactory(4096,new File(temp));
		ServletFileUpload fu = new ServletFileUpload(factory);
		fu.setSizeMax(1 * 1024 * 1024); // The maximum size of file

		//
		int index = 0;
		List fileItems = null;

		try {
			fileItems = fu.parseRequest(new ServletRequestContext(request));
			System.out.println("fileItems=" + fileItems);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Iterator iter = fileItems.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (!item.isFormField()) {
				String name = item.getName();
				name = name.substring(name.lastIndexOf("\\") + 1);
				long size = item.getSize();
				if ((name == null || name.equals("")) && size == 0)
					continue;
				int point = name.indexOf(".");
				name = (new Date()).getTime()
						+ name.substring(point, name.length()) ;
				index++;
				File fNew = new File(loadpath, name);
				String picPath = "http://10.192.244.114:8080/FSL_WebServer/Image/"+name;
				pw.write(picPath);
				try {
					item.write(fNew);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
		pw.flush();
		pw.close();

	}

}
