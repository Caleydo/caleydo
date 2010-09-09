package org.caleydo.view.scatterplot;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
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

	public SerializedScatterplotView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public String getViewType() {
		return GLScatterPlot.VIEW_ID;
	}
}
