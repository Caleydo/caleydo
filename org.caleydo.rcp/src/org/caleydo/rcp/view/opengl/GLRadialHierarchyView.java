package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.radial.SerializedRadialHierarchyView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLRadialHierarchyView
	extends AGLViewPart {

	public static final String ID = SerializedRadialHierarchyView.GUI_ID;

	/**
	 * Constructor.
	 */
	public GLRadialHierarchyView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		AGLEventListener view = createGLEventListener(initSerializedView, glCanvas.getID());
		minSizeComposite.setView(view);
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedRadialHierarchyView serializedView = new SerializedRadialHierarchyView();
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}