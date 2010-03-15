package org.caleydo.view.heatmap.dendrogram;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDendrogramVerticalView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDendrogramVerticalView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {

		super.createPartControl(parent);
		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedDendogramVerticalView serializedView = new SerializedDendogramVerticalView(
				dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLDendrogram.VIEW_ID + ".vertical";
	}

}