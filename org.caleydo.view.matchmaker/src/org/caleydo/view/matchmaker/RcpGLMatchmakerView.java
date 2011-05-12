package org.caleydo.view.matchmaker;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLMatchmakerView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLMatchmakerView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedMatchmakerView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLMatchmaker(glCanvas, serializedView.getViewFrustum());
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomain(
					serializedView.getDataDomainType());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}

		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedMatchmakerView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLMatchmaker.VIEW_ID;
	}

}