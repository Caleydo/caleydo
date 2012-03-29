package org.caleydo.view.bookmark;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpBookmarkView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpBookmarkView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedBookmarkView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();

		view = new GLBookmarkView(glCanvas, parent, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(
					((ASerializedTopLevelDataView) serializedView).getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
		}

		view.initialize();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedBookmarkView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLBookmarkView.VIEW_TYPE;
	}

}