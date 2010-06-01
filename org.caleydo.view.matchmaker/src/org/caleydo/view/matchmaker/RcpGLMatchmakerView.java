package org.caleydo.view.matchmaker;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLMatchmakerView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLMatchmakerView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		AGLView view = createGLView(initSerializedView, glCanvas.getID());
		minSizeComposite.setView(view);
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedCompareView serializedView = new SerializedCompareView(
				dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLMatchmaker.VIEW_ID;
	}

}