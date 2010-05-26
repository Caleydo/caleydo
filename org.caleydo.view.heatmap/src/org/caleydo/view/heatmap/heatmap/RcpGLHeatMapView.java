package org.caleydo.view.heatmap.heatmap;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.datadomain.genetic.GeneticUseCase;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RcpGLHeatMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLHeatMapView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IUseCase usecase = GeneralManager.get().getUseCase(dataDomain);
		if (usecase != null && usecase instanceof GeneticUseCase
				&& ((GeneticUseCase) usecase).isPathwayViewerMode()) {
			MessageBox alert = new MessageBox(new Shell(), SWT.OK);
			alert.setMessage("Cannot create heat map in pathway viewer mode!");
			alert.open();

			dispose();
			return;
		}

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedHeatMapView serializedView = new SerializedHeatMapView(
				dataDomain);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLHeatMap.VIEW_ID;
	}

}