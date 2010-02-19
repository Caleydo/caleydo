package org.caleydo.view.datawindows;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RcpGLDataWindowsView extends ARcpGLViewPart {

//	// FIXME: check if it is ok to overwrite
//	private EDataDomain dataDomain;

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