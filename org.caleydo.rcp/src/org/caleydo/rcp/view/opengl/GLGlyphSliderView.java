package org.caleydo.rcp.view.opengl;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.glyph.sliderview.SerializedGlyphSliderView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class GLGlyphSliderView
	extends AGLViewPart {

	public static final String ID = SerializedGlyphSliderView.GUI_ID;

	/**
	 * Constructor.
	 */
	public GLGlyphSliderView() {
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
		return;

	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedGlyphSliderView serializedView = new SerializedGlyphSliderView();
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}