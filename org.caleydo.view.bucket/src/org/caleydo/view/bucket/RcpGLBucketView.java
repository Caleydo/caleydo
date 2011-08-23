package org.caleydo.view.bucket;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.heatmap.heatmap.SerializedHeatMapView;
import org.caleydo.view.parcoords.SerializedParallelCoordinatesView;
import org.caleydo.view.pathway.SerializedPathwayView;
import org.eclipse.swt.widgets.Composite;

public class RcpGLBucketView extends ARcpGLViewPart {

	private ArrayList<Integer> iAlContainedViewIDs;

	/**
	 * Constructor.
	 */
	public RcpGLBucketView() {
		super();

		try {
			Collection<Class<? extends ASerializedView>> viewTypes = new ArrayList<Class<? extends ASerializedView>>();
			viewTypes.add(SerializedBucketView.class);
			viewTypes.add(SerializedHeatMapView.class);
			viewTypes.add(SerializedParallelCoordinatesView.class);
			viewTypes.add(SerializedPathwayView.class);
			
			Class<?>[] classes = new Class<?>[viewTypes.size()];
			classes = viewTypes.toArray(classes);
			viewContext = JAXBContext.newInstance(classes);

		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}

		iAlContainedViewIDs = new ArrayList<Integer>();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		createGLCanvas();
		
		view = new GLBucket(glCanvas, parentComposite, serializedView.getViewFrustum());
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(serializedView.getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView =
				(IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}
		view.initFromSerializableRepresentation(serializedView);
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedBucketView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void dispose() {
		GLBucket glRemoteView = (GLBucket) GeneralManager.get().getViewManager()
				.getGLView(view.getID());

		for (Integer iContainedViewID : iAlContainedViewIDs) {
			glRemoteView.removeView(GeneralManager.get().getViewManager()
					.getGLView(iContainedViewID));
		}

		super.dispose();

		GeneralManager.get().getViewManager()
				.getConnectedElementRepresentationManager().clearByView(view.getID());

		PathwayManager.get().resetPathwayVisiblityState();
	}

	@Override
	public String getViewGUIID() {
		return GLBucket.VIEW_TYPE;
	}
}
