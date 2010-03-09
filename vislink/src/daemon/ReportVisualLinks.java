package daemon;

import java.io.IOException;

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
public class ReportVisualLinks extends HttpServlet {
	private static final long serialVersionUID = 1L;

//	VisLinkRenderMockup visLinks = null;

	/**
     * Default constructor. 
     */
    public ReportVisualLinks() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() {
//    	visLinks = new VisLinkRenderMockup();
    }

    @Override 
    public void destroy() {
//    	visLinks = null;
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
		
		String xml = (String) request.getParameter("xml");
		String appName = request.getParameter("name");
		String pointerID = request.getParameter("pointer"); 
		
		System.out.println("Report visual links: name="+appName+", pointer="+pointerID+"xml..."); 
		
		VisLinkManager visLinkManager = (VisLinkManager) getApplicationContext().getBean("visLinkManager");
		
		visLinkManager.reportVisualLinks(appName, pointerID, xml);
		
		String returnXml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
				"<reportVisualLinks>" + 
				"</reportVisualLinks>";
		response.getOutputStream().print(returnXml);
	}

	private WebApplicationContext getApplicationContext() {
		ServletContext servletContext = getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return wac;
	}

}
