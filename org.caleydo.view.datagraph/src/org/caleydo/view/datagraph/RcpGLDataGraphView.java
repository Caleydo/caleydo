package org.caleydo.view.datagraph;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataGraphView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDataGraphView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedDataGraphView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLDataGraph(glCanvas, parentComposite, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		minSizeComposite.setView((AGLView) view);
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDataGraphView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLDataGraph.VIEW_TYPE;
	}

}
