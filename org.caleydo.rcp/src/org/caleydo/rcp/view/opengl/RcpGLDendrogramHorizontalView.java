package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedDendogramHorizontalView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDendrogramHorizontalView
	extends ARcpGLViewPart {

	public static final String ID = SerializedDendogramHorizontalView.GUI_ID;

	/**
	 * Constructor.
	 */
	public RcpGLDendrogramHorizontalView() {
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
		SerializedDendogramHorizontalView serializedView = new SerializedDendogramHorizontalView(dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}