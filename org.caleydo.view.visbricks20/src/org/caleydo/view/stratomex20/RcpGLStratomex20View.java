package org.caleydo.view.stratomex20;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 * 
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLStratomex20View extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLStratomex20View() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedStratomex20View.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLStratomex20(glCanvas, parentComposite,
				serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedStratomex20View();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLStratomex20.VIEW_TYPE;
	}

}