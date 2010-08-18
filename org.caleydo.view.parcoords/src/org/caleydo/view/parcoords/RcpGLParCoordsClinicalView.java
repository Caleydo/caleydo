package org.caleydo.view.parcoords;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLParCoordsClinicalView extends ARcpGLViewPart {

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
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView(
				dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}