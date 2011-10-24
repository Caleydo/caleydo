package org.caleydo.view.parcoords;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLParCoordsView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLParCoordsView() {
		super();

		try {
			viewContext = JAXBContext
					.newInstance(SerializedParallelCoordinatesView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		createGLCanvas();
		view = new GLParallelCoordinates(glCanvas, parentComposite,
				serializedView.getViewFrustum());
		initializeData();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedParallelCoordinatesView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLParallelCoordinates.VIEW_TYPE;
	}

}