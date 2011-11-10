package org.caleydo.view.heatmap.uncertainty;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLUncertaintyHeatMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLUncertaintyHeatMapView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedUncertaintyHeatMapView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();

		view = new GLUncertaintyHeatMap(glCanvas, parentComposite,
				serializedView.getViewFrustum());
		initializeViewWithData();
		view.initFromSerializableRepresentation(serializedView);
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedUncertaintyHeatMapView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLUncertaintyHeatMap.VIEW_TYPE;
	}

}