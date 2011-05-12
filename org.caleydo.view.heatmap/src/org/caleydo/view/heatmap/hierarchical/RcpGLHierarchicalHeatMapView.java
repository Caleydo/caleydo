package org.caleydo.view.heatmap.hierarchical;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.camera.ECameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.eclipse.swt.widgets.Composite;

public class RcpGLHierarchicalHeatMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLHierarchicalHeatMapView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedHierarchicalHeatMapView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		
		ViewFrustum viewFrustum = new ViewFrustum(ECameraProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20);
		view = new GLHierarchicalHeatMap(glCanvas, viewFrustum);
		view.initFromSerializableRepresentation(serializedView);

		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomain(serializedView.getDataDomainType());
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

		serializedView = new SerializedHierarchicalHeatMapView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLHierarchicalHeatMap.VIEW_ID;
	}

}