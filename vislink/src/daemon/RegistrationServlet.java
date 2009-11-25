package daemon;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import VIS.Color4f;
import VIS.SelectionContainer;
import VIS.VisRendererIPrx;

/**
 * Registration for applications
 */
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	JAXBContext jaxbContext;
	
    /**
     * Default constructor. 
     */
    public RegistrationServlet() {
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
		try {
			jaxbContext = JAXBContext.newInstance(Application.class, BoundingBox.class);
		} catch (JAXBException e) {
			throw new ServletException(e);
		}
    }

    public void destroy() {
    	jaxbContext = null;
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

		Document doc = createDocument();

		Application app = readParams(request);
		register(request, doc, app);

		writeDocument(out, doc);
	}
		
	private void register(HttpServletRequest request, Document doc, Application app) throws ServletException {
		ApplicationManager applicationManager = (ApplicationManager) getApplicationContext().getBean("applicationManager");
		applicationManager.registerApplication(app);
		
		Element root = doc.createElement("registration");
		doc.appendChild(root);
		BoundingBox windowBoundingBox = app.getWindows().get(0);
		SelectionContainer selectionContainer = new SelectionContainer(
				app.getId(),
				windowBoundingBox.getX(),
				windowBoundingBox.getY(),
				windowBoundingBox.getWidth(),
				windowBoundingBox.getHeight(),
				new Color4f(-1, 0, 0, 0));
		VisRendererIPrx rendererPrx = (VisRendererIPrx) getServletContext().getAttribute("visRenderer");
		rendererPrx.registerSelectionContainer(selectionContainer);
		
//		try {
//			Marshaller marshaller = jaxbContext.createMarshaller();
//			marshaller.marshal(app, root);
//		} catch (JAXBException e) {
//			throw new ServletException(e);
//		}
	}

	private Application readParams(HttpServletRequest request) throws ServletException {
		String name = (String) request.getParameter("name");
		if (name == null) {
			throw new ServletException("Registration without application name is not allowed.");
		}

		ApplicationManager applicationManager = (ApplicationManager) getApplicationContext().getBean("applicationManager");
		Application app = applicationManager.getApplications().get(name);
		if (app != null) {
			System.out.println("re-registering " + name);
		} else {
			BoundingBox windowBoundingBox = getWindowBoundingBox(request);
			app = new Application();
			app.setDate(new Date());
			app.setName(name);
			app.getWindows().add(windowBoundingBox);
		}
		return app;
	}

	private BoundingBox getWindowBoundingBox(HttpServletRequest request) 
	throws ServletException {
		String xml = request.getParameter("xml");
		System.out.println(xml);

		BoundingBox bb = null;
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			
			StringReader sr = new StringReader(xml);
			bb = (BoundingBox) unmarshaller.unmarshal(sr);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		return bb;
	}

	protected Document createDocument() throws ServletException {
		Document doc; 
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		}
		return doc;
	}

	protected void writeDocument(PrintWriter out, Document doc) throws ServletException {
		try {
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            // trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            StreamResult result = new StreamResult(out);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);

		} catch (TransformerConfigurationException e) {
			throw new ServletException(e);
		} catch (TransformerException e) {
			throw new ServletException(e);
		}
	}

	private WebApplicationContext getApplicationContext() {
		ServletContext servletContext = getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return wac;
	}
	
}
