package org.caleydo.view.visbricks.brick;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

/**
 * RCP View for a single Brick. Mainly intended for development purposes.
 * 
 * @author Alexander Lex
 */
public class RcpGLBrickView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLBrickView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedBrickView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		view = new GLBrick(glCanvas, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);
		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomain(serializedView.getDataDomainType());
			if(dataDomain == null)
				throw new IllegalStateException("DataDomain null");
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
		serializedView = new SerializedBrickView();
		determineDataDomain(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLBrick.VIEW_ID;
	}

}