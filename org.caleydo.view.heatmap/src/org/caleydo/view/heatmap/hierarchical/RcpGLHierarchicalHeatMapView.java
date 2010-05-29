package org.caleydo.view.heatmap.hierarchical;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class RcpGLHierarchicalHeatMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLHierarchicalHeatMapView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		if (dataDomain != null && dataDomain instanceof GeneticDataDomain
				&& ((GeneticDataDomain) dataDomain).isPathwayViewerMode()) {
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
		
		super.createDefaultSerializedView();
		
		SerializedHierarchicalHeatMapView serializedView = new SerializedHierarchicalHeatMapView(
				dataDomain.getDataDomainType());
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLHierarchicalHeatMap.VIEW_ID;
	}

}