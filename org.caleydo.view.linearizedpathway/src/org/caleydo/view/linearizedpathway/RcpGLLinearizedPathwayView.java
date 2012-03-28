package org.caleydo.view.linearizedpathway;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 * 
 * @author Christian
 */
public class RcpGLLinearizedPathwayView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLLinearizedPathwayView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedLinearizedPathwayView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLLinearizedPathway(glCanvas, parentComposite, serializedView.getViewFrustum());
		initializeViewWithData();
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedLinearizedPathwayView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLLinearizedPathway.VIEW_TYPE;
	}

}