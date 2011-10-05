package org.caleydo.view.radial;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.ARcpGLViewPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLRadialHierarchyView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLRadialHierarchyView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedRadialHierarchyView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// minSizeComposite.setView(view);

		createGLCanvas();

		view = new GLRadialHierarchy(glCanvas, parentComposite,
				serializedView.getViewFrustum());
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

	public static void createToolBarItems(int viewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedRadialHierarchyView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLRadialHierarchy.VIEW_TYPE;
	}

}