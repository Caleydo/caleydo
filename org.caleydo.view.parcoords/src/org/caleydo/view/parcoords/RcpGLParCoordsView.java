package org.caleydo.view.parcoords;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

public class RcpGLParCoordsView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLParCoordsView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedParallelCoordinatesView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		if (memento == null) {
			SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView(
					dataDomainType);
			initSerializedView = serializedView;
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

		SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView();
		serializedView.setDataDomainType(determineDataDomain(serializedView));
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLParallelCoordinates.VIEW_ID;
	}

}