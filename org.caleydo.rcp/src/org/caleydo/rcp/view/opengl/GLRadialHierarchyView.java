package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.radial.EDrawingStateType;
import org.caleydo.core.view.opengl.canvas.radial.EPDDrawingStrategyType;
import org.caleydo.core.view.opengl.canvas.radial.GLRadialHierarchy;
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
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedRadialHierarchyView serializedView = new SerializedRadialHierarchyView();

		serializedView.setMaxDisplayedHierarchyDepth(GLRadialHierarchy.DISP_HIER_DEPTH_DEFAULT);
		serializedView.setDrawingStateType(EDrawingStateType.DRAWING_STATE_FULL_HIERARCHY);
		serializedView.setDefaultDrawingStrategyType(EPDDrawingStrategyType.EXPRESSION_COLOR);
		serializedView.setRootElementID(-1);
		serializedView.setSelectedElementID(-1);
		serializedView.setMouseOverElementID(-1);
		serializedView.setRootElementStartAngle(0);
		serializedView.setSelectedElementStartAngle(0);
		serializedView.setNewSelection(true);

		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}