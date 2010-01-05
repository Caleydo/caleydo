package org.caleydo.view.base.rcp;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.radial.SerializedRadialHierarchyView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLRadialHierarchyView
	extends ARcpGLViewPart {

	public static final String ID = SerializedRadialHierarchyView.GUI_ID;

	/**
	 * Constructor.
	 */
	public RcpGLRadialHierarchyView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		AGLView view = createGLEventListener(initSerializedView, glCanvas.getID());
		minSizeComposite.setView(view);
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedRadialHierarchyView serializedView = new SerializedRadialHierarchyView(dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}