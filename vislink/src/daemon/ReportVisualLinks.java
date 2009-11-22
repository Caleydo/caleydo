package daemon;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Servlet implementation class hello
 */
public class ReportVisualLinks extends HttpServlet {
	private static final long serialVersionUID = 1L;

	VisLinkRenderMockup visLinks = null;
	
    /**
     * Default constructor. 
     */
    public ReportVisualLinks() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() {
    	visLinks = new VisLinkRenderMockup();
    }

    @Override 
    public void destroy() {
    	visLinks = null;
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
		ApplicationManager applicationManager = (ApplicationManager) getApplicationContext().getBean("applicationManager");
		Application app = applicationManager.getApplications().get(appName);

		System.out.println("receiving vislinks from " + app);
		System.out.println(xml);

		BoundingBoxList bbl = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(BoundingBoxList.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			StringReader sr = new StringReader(xml);
			bbl = (BoundingBoxList) unmarshaller.unmarshal(sr);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		HashMap<String, BoundingBoxList> app2bbs = (HashMap<String, BoundingBoxList>) getServletContext().getAttribute("boundingBoxes");
		if (app2bbs == null) {
			app2bbs = new HashMap<String, BoundingBoxList>();
			getServletContext().setAttribute("boundingBoxes", app2bbs);
		}
		app2bbs.put(app.getName(), bbl);
		
		if (applicationManager.getApplications().size() == app2bbs.size()) {
			BoundingBoxList list = new BoundingBoxList();
			for (BoundingBoxList elem : app2bbs.values()) {
				list.getList().addAll(elem.getList());
			}
			visLinks.drawVisualLinks(list);
			app2bbs.clear();
		} else {
			System.out.println("waiting for more reports, " + app2bbs.size() + " / " + applicationManager.getApplications().size());
		}
		
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
