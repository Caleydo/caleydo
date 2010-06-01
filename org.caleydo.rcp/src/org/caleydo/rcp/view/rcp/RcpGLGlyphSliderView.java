package org.caleydo.rcp.view.rcp;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.glyph.sliderview.GLGlyphSliderView;
import org.caleydo.core.view.opengl.canvas.glyph.sliderview.SerializedGlyphSliderView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLGlyphSliderView
	extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLGlyphSliderView() {
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
		return;

	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedGlyphSliderView serializedView = new SerializedGlyphSliderView(dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLGlyphSliderView.VIEW_ID;
	}

}