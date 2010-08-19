package org.caleydo.view.treemap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLTreeMapView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLTreeMapView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedTreeMapView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedTreeMapView serializedView = new SerializedTreeMapView(dataDomainType);
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLTreeMap.VIEW_ID;
	}

}