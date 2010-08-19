package org.caleydo.view.heatmap.heatmap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLHeatMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLHeatMapView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedHeatMapView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		//
		// if (dataDomain != null && dataDomain instanceof GeneticDataDomain
		// && ((GeneticDataDomain) dataDomain).isPathwayViewerMode()) {
		// MessageBox alert = new MessageBox(new Shell(), SWT.OK);
		// alert.setMessage("Cannot create heat map in pathway viewer mode!");
		// alert.open();
		//
		// dispose();
		// return;
		// }

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedHeatMapView serializedView = new SerializedHeatMapView(dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLHeatMap.VIEW_ID;
	}

}