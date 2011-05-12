package org.caleydo.view.dataflipper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataFlipperView extends ARcpGLViewPart {

	// private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public RcpGLDataFlipperView() {
		super();

		try {
			viewContext = JAXBContext
					.newInstance(SerializedDataFlipperView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
		
		// iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		createGLCanvas();
		view = new GLDataFlipper(glCanvas, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedDataFlipperView();
		determineDataDomain(serializedView);
	}

	@Override
	public void dispose() {
		// GLDataFlipper glDataFlipperView =
		// (GLDataFlipper)
		// GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID);

		// glRemoteView.clearAll();

		// TODO
		// for (Integer iContainedViewID : iAlContainedViewIDs) {
		// glDataFlipperView.removeView(GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
		// iContainedViewID));
		// }

		super.dispose();
	}

	@Override
	public String getViewGUIID() {
		return GLDataFlipper.VIEW_ID;
	}
}
