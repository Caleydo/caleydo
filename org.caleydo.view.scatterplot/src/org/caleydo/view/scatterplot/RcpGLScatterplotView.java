package org.caleydo.view.scatterplot;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.rcp.view.rcp.ARcpGLViewPart;
import org.eclipse.swt.widgets.Composite;

public class RcpGLScatterplotView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLScatterplotView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		createGLCanvas();
		createGLView(initSerializedView, glCanvas.getID());
	}

	@Override
	public ASerializedView createDefaultSerializedView() {
		SerializedScatterplotView serializedView = new SerializedScatterplotView(
				dataDomain.getDataDomainType());
		return serializedView;
	}

	@Override
	public String getViewGUIID() {
		return GLScatterPlot.VIEW_ID;
	}

}