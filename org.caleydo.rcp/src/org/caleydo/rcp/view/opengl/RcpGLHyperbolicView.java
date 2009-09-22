package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.hyperbolic.SerializedHyperbolicView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLHyperbolicView
	extends ARcpGLViewPart {

	public static final String ID = SerializedHyperbolicView.GUI_ID;

	/**
	 * Constructor.
	 */
	public RcpGLHyperbolicView() {
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
		SerializedHyperbolicView serializedView = new SerializedHyperbolicView(dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}
