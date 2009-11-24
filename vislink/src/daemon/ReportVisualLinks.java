package daemon;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

import VIS.Color4f;
import VIS.VisRendererIPrx;
import VIS.VisRendererIPrxHelper;
import VIS.adapterName;
import VIS.adapterPort;
import VIS.Selection;
import VIS.SelectionGroup;

import Ice.Communicator;

/**
 * Servlet implementation class hello
 */
public class ReportVisualLinks extends HttpServlet {
	private static final long serialVersionUID = 1L;

//	VisLinkRenderMockup visLinks = null;

	/** Ice communication object. */
	private Communicator communicator;
	
	/** Proxy object of VisRenderer for remote method invocation. */
	private VisRendererIPrx rendererPrx; 

	/**
     * Default constructor. 
     */
    public ReportVisualLinks() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() {
//    	visLinks = new VisLinkRenderMockup();
    	connect();
    }

    @Override 
    public void destroy() {
//    	visLinks = null;
    	disconnect();
    }

    private void renderVisualLinks(BoundingBoxList bbl) {
//		visLinks.drawVisualLinks(list);
    	renderWithIce(bbl);
    }
    
    private void renderWithIce(BoundingBoxList bbl) {
    	SelectionGroup selectionGroup = new SelectionGroup();
    	selectionGroup.selections = new Selection[bbl.getList().size()];
    	boolean source = true;
    	ArrayList<Selection> selectionList = new ArrayList<Selection>();
    	for (BoundingBox bb : bbl.getList()) {
    		Selection selection = new Selection(bb.getX(), bb.getY(), bb.getWidth(), bb.getHeight(),
    				new Color4f(-1, 0, 0, 0), source);
    		selectionList.add(selection);
    		source = false;
    	}
    	selectionGroup.selections = selectionList.toArray(selectionGroup.selections);
    	SelectionGroup[] groups  = new SelectionGroup[1];
    	groups[0] = selectionGroup;
    	rendererPrx.renderAllLinks(groups);
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
			renderVisualLinks(list);
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

    
	/**
	 * Establishes a connection to the VisRenderer und creates 
	 * a proxy object for remote method invocation. 
	 */
	public void connect() {
		System.out.println("Connect to VisRenderer"); 
		
		if(rendererPrx == null) {

			// init communication channel 
			communicator = Ice.Util.initialize();

			// get local host name 
			String hostname = ""; 
			try {
				InetAddress addr = InetAddress.getLocalHost();
				hostname = addr.getHostName(); 
				System.out.println("hostname="+hostname); 
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} 

			// get server port, name, and end point 
			int serverPort = adapterPort.value; 
			String serverName = adapterName.value; 
			String serverEndPoint = "tcp -h " + hostname + " -p " + serverPort;
			
			System.out.println("Server name: " + serverName); 
			System.out.println("Server end point: " + serverEndPoint); 

			try {

				// if no renderer system is running, this operation
				// will throw an exception 
				Ice.ObjectPrx proxy = communicator.stringToProxy(serverName + ":" 
						+ serverEndPoint);
				rendererPrx = VisRendererIPrxHelper.checkedCast(proxy);
			} catch(Ice.ConnectionRefusedException e){
				System.out.println("Connection refused - VisRenderer not found"); 
			}
		} else {
			System.out.println("Already established connection");
		}
	}

	/**
	 * Clears all elements from the VisRenderer and closes the 
	 * network connection. 
	 */
	public void disconnect() {
		System.out.println("disconnect"); 
		if(this.rendererPrx != null){
			this.rendererPrx.clearAll(); 
		}
		if (communicator != null) {
			try {
				System.out.println("Destroy Ice communicator"); 
				communicator.destroy();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
