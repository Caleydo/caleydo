package org.caleydo.view.datagraph;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDataGraphView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDataGraphView() {
		super();

		try {
			viewContext = JAXBContext
					.newInstance(SerializedDataGraphView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLDataGraph(glCanvas, serializedView.getViewFrustum());
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
		serializedView = new SerializedDataGraphView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLDataGraph.VIEW_ID;
	}

}
