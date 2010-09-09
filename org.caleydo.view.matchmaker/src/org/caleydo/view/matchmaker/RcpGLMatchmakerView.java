package org.caleydo.view.matchmaker;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLMatchmakerView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLMatchmakerView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedMatchmakerView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLMatchmaker(glCanvas, serializedView.getViewFrustum());
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedMatchmakerView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLMatchmaker.VIEW_ID;
	}

}