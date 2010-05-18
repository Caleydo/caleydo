package org.caleydo.view.datawindows;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataWindowsView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDataWindowsView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedDataWindowsView serializedView = new SerializedDataWindowsView(
				GeneralManager.get().getMasterUseCase().getDataDomain());
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLDataWindows.VIEW_ID;
	}

}