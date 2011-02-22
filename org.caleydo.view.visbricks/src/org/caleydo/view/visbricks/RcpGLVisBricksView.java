package org.caleydo.view.visbricks;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 * 
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLVisBricksView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLVisBricksView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedVisBricksView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLVisBricks(glCanvas, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedVisBricksView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLVisBricks.VIEW_ID;
	}

}