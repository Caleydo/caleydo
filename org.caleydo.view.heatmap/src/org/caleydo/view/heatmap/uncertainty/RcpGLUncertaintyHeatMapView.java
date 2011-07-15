package org.caleydo.view.heatmap.uncertainty;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLUncertaintyHeatMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLUncertaintyHeatMapView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedUncertaintyHeatMapView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		
		view = new GLUncertaintyHeatMap(glCanvas, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);

		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(serializedView.getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView =
				(IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}

		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedUncertaintyHeatMapView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLUncertaintyHeatMap.VIEW_TYPE;
	}

}