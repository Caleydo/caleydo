package org.caleydo.view.tagclouds;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 * 
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLTagCloudView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLTagCloudView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedTagCloudView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLTagCloud(glCanvas, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomain(
					serializedView.getDataDomainType());
			if (dataDomain == null)
				throw new IllegalStateException("DataDomain null");
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}
		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedTagCloudView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLTagCloud.VIEW_ID;
	}

}