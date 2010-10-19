package org.caleydo.view.filterpipeline;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 * 
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLFilterPipelineView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLFilterPipelineView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedFilterPipelineView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLFilterPipeline(glCanvas, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedFilterPipelineView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLFilterPipeline.VIEW_ID;
	}

}