package org.caleydo.view.visbricks;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * RCP view container for {@link GLVisBricks}
 * 
 * @author <Alexander Lex
 */
public class RcpGLVisBricksView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLVisBricksView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedVisBricksView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLVisBricks(glCanvas, parentComposite, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(
					serializedView.getDataDomainID());
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
		serializedView = new SerializedVisBricksView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLVisBricks.VIEW_TYPE;
	}

}