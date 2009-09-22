package org.caleydo.rcp.view.opengl;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLParCoordsClinicalView
	extends ARcpGLViewPart {

	public static final String ID = SerializedParallelCoordinatesView.GUI_ID;

	/**
	 * Constructor.
	 */
	public RcpGLParCoordsClinicalView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView(dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}