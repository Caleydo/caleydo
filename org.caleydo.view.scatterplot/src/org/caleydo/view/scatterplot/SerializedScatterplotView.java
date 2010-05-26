package org.caleydo.view.scatterplot;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.datadomain.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a scatterplot-view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedScatterplotView extends ASerializedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedScatterplotView() {
	}

	public SerializedScatterplotView(EDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC,
				0, 8, 0, 8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLScatterPlot.VIEW_ID;
	}
}
