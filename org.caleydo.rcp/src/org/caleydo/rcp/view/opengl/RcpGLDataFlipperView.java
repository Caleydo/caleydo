package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.remote.dataflipper.GLDataFlipper;
import org.caleydo.core.view.opengl.canvas.remote.dataflipper.SerializedDataFlipperView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataFlipperView
	extends ARcpGLViewPart {

	public static final String ID = SerializedDataFlipperView.GUI_ID;

	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public RcpGLDataFlipperView() {
		super();

		iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedDataFlipperView serializedView = new SerializedDataFlipperView();
		return serializedView;
	}
	
	@Override
	public void dispose() {
		GLDataFlipper glDataFlipperView =
			(GLDataFlipper) GeneralManager.get().getViewGLCanvasManager().getGLEventListener(iViewID);

		// glRemoteView.clearAll();

		//TODO
//		for (Integer iContainedViewID : iAlContainedViewIDs) {
//			glDataFlipperView.removeView(GeneralManager.get().getViewGLCanvasManager().getGLEventListener(
//				iContainedViewID));
//		}

		super.dispose();
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}
}
