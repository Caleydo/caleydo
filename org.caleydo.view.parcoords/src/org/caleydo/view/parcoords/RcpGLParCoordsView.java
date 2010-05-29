package org.caleydo.view.parcoords;

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
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		if (memento == null) {
			SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView(
					dataDomain.getDataDomainType());
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
		super.createDefaultSerializedView();
		
		SerializedParallelCoordinatesView serializedView = new SerializedParallelCoordinatesView(
				dataDomain.getDataDomainType());
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLParallelCoordinates.VIEW_ID;
	}

}