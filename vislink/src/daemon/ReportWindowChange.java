package daemon;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ReportWindowChange extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     * Default constructor. 
     */
    public ReportWindowChange() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml");
		
		// I've no idea what's going on... 

		VisLinkManager visLinkManager = (VisLinkManager) getApplicationContext().getBean("visLinkManager"); 
		
		String appName = request.getParameter("name");
		// todo if (appName == null)
		
		visLinkManager.reportWindowChange(appName); 
		
		response.getWriter().print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><reportWindowChange />");
	}

	private WebApplicationContext getApplicationContext() {
		ServletContext servletContext = getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return wac;
	}

}
