package org.caleydo.view.grouper;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLGrouperView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLGrouperView() {
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
		SerializedGrouperView serializedView = new SerializedGrouperView(
				dataDomain.getDataDomainType());
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLGrouper.VIEW_ID;
	}

}