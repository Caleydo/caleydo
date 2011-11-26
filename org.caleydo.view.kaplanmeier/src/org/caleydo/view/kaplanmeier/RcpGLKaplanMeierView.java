package org.caleydo.view.kaplanmeier;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * RCP Kaplan Meier view.
 * 
 * @author Marc Streit
 */
public class RcpGLKaplanMeierView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLKaplanMeierView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedKaplanMeierView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLKaplanMeier(glCanvas, parentComposite, serializedView.getViewFrustum());
		initializeViewWithData();
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedKaplanMeierView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLKaplanMeier.VIEW_TYPE;
	}

}