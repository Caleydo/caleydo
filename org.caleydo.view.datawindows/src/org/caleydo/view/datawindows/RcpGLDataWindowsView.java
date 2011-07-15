package org.caleydo.view.datawindows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataWindowsView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDataWindowsView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedDataWindowsView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		
		view = new GLDataWindows(glCanvas, serializedView.getViewFrustum());
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedDataWindowsView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLDataWindows.VIEW_TYPE;
	}

}