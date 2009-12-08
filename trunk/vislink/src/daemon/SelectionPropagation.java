package daemon;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class hello
 */
public class SelectionPropagation extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public SelectionPropagation() {
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
		PrintWriter out = response.getWriter();
		
		VisLinkManager visLinkManager = (VisLinkManager) getApplicationContext().getBean("visLinkManager");
		String appName = request.getParameter("name");
		String filter = visLinkManager.retrieveSelectionId(appName);
		
		String empty = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
			"<vislink>" + 
			"</vislink>";

		if (filter != null) {
			getServletContext().setAttribute("recentFilter", filter);
			String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
					"<vislink>" + 
					"	<element>" +
					"		<id>" + filter + "</id>" +
					"	</element>" +
					"</vislink>";
			out.print(xml);
			System.out.println("sending to " + appName + ", xml=" + xml);
		} else {
			out.println(empty);
			// System.out.println("sending empty links to " + appName);
		}
	}

	private WebApplicationContext getApplicationContext() {
		ServletContext servletContext = getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return wac;
	}
	
}
