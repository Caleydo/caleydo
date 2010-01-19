package org.caleydo.view.base.rcp;

import java.util.ArrayList;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.glyph.sliderview.SerializedGlyphSliderView;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLGlyphSliderView extends ARcpGLViewPart {

	public static final String ID = SerializedGlyphSliderView.GUI_ID;

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
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
		return;

	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedGlyphSliderView serializedView = new SerializedGlyphSliderView(
				dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}