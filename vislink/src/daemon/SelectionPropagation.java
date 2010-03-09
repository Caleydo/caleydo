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
		
		//VisLinkManager visLinkManager = (VisLinkManager) getApplicationContext().getBean("visLinkManager");
		SelectionManager selectionManager = (SelectionManager) getApplicationContext().getBean("selectionManager");
		String appName = request.getParameter("name");
//		if(appName == "googlemaps"){
//			System.out.println("Propagation: " + appName); 
//		}
		
		String empty = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
		"<vislink>" + 
		"</vislink>";
		
		//String filter = visLinkManager.retrieveSelectionId(appName);
		UserSelection selection = selectionManager.getUnreportedSelection(appName); 
		
		if(selection != null){
			String selectionID = selection.getSelectionID(); 
			String pointerID = selection.getPointerID(); 
			//		String selectionID = selectionManager.getSelectionIDFilter(appName); 
			//		String pointerID = selectionManager.getPointerIDFilter(appName); 



			if (selectionID != null && pointerID != null) {

				System.out.println("Not empty: selectionID=" + selectionID + " - pointerID=" + pointerID); 

				// what's that??
				getServletContext().setAttribute("recentFilter", selectionID);

				String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
				"<vislink>" + 
				"	<element>" +
				"		<id>" + selectionID + "</id>" +
				"		<pointer>" + pointerID + "</pointer>" +
				"	</element>" +
				"</vislink>";
				out.print(xml);
				System.out.println("sending to " + appName + ", xml=" + xml);
			} else {
				out.println(empty);
				// System.out.println("sending empty links to " + appName);
			}
		}
		else{
			out.println(empty); 
		}
	}

	private WebApplicationContext getApplicationContext() {
		ServletContext servletContext = getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return wac;
	}
	
}
