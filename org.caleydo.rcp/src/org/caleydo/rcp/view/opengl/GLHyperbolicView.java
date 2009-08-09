package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.hyperbolic.SerializedHyperbolicView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLHyperbolicView
	extends AGLViewPart {

	public static final String ID = SerializedHyperbolicView.GUI_ID;

	/**
	 * Constructor.
	 */
	public GLHyperbolicView() {
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
		SerializedHyperbolicView serializedView = new SerializedHyperbolicView();
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}
