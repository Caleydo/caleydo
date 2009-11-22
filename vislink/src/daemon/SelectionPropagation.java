package daemon;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import vis.TestBean;

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
		BlockingQueue<String> queue = (BlockingQueue<String>) getServletContext().getAttribute("filterQueue");
		if (queue == null) {
			queue = new ArrayBlockingQueue<String>(10);
			getServletContext().setAttribute("filterQueue", queue);
		}
		String empty = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
		"<vislink>" + 
		"</vislink>";
		String filter = null;; 
		if (queue.size() == 0) {
			filter = (String) getServletContext().getAttribute("recentFilter");
		} else {
			try {
				filter = queue.take();
			} catch (Exception e) {
				out.println(empty);
				// System.out.println("sending empty links");
			}
		}
		if (filter != null) {
			getServletContext().setAttribute("recentFilter", filter);
			String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
					"<vislink>" + 
					"	<element>" +
					"		<id>" + filter + "</id>" +
					"	</element>" +
					"</vislink>";
			System.out.println("sending xml=" + xml);
			out.print(xml);
		} else {
			out.println(empty);
			ApplicationManager applicationManager = (ApplicationManager) getApplicationContext().getBean("applicationManager");
			Application app = applicationManager.getApplications().get(request.getParameter("name"));
			System.out.println("sending empty links to " + app);
		}
	}

	private WebApplicationContext getApplicationContext() {
		ServletContext servletContext = getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return wac;
	}
	
}
