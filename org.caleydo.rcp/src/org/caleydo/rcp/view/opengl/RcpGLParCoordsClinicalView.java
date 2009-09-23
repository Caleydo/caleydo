package org.caleydo.rcp.view.opengl;

import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedParallelCoordinatesView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLParCoordsClinicalView
	extends ARcpGLViewPart {

	public static final String ID = "org.caleydo.rcp.views.opengl.ClinicalGLParCoordsView";

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
		SerializedParallelCoordinatesView serializedView =
			new SerializedParallelCoordinatesView(EDataDomain.CLINICAL_DATA);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}