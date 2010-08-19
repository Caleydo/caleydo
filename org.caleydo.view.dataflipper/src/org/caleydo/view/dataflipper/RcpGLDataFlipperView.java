package org.caleydo.view.dataflipper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
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
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {

		SerializedDataFlipperView serializedView = new SerializedDataFlipperView();
		return serializedView;
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
