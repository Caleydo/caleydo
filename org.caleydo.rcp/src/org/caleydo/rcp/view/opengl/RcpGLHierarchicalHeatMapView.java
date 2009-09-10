package org.caleydo.rcp.view.opengl;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genetic.GeneticUseCase;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.storagebased.SerializedHierarchicalHeatMapView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RcpGLHierarchicalHeatMapView
	extends ARcpGLViewPart {

	public static final String ID = SerializedHierarchicalHeatMapView.GUI_ID;

	/**
	 * Constructor.
	 */
	public RcpGLHierarchicalHeatMapView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IUseCase usecase = GeneralManager.get().getUseCase();
		if (usecase instanceof GeneticUseCase && ((GeneticUseCase) usecase).isPathwayViewerMode()) {
			MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			alert.setMessage("Cannot create heat map in pathway viewer mode!");
			alert.open();

			dispose();
			return;
		}

		createGLCanvas();
		createGLEventListener(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedHierarchicalHeatMapView serializedView = new SerializedHierarchicalHeatMapView();
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return ID;
	}

}