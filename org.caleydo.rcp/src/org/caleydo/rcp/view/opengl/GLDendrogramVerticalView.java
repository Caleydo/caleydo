package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedDendogramVerticalView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLDendrogramVerticalView
	extends AGLViewPart {
	public static final String ID = "org.caleydo.rcp.views.opengl.GLDendrogramVerticalView";

	/**
	 * Constructor.
	 */
	public GLDendrogramVerticalView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {

		super.createPartControl(parent);
		createGLCanvas();
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedDendogramVerticalView serializedView = new SerializedDendogramVerticalView();
		serializedView.setViewGUIID(getViewGUIID());
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}