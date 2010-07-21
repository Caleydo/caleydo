package org.caleydo.view.texture;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a texture-view.
 * 
 * @author Marc Streit
 */
@XmlRootElement
@XmlType
public class SerializedTextureView extends ASerializedView {

	private String texturePath;

	private String label;

	private int experimentIndex;

	private String info;

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTextureView() {

	}

	public SerializedTextureView(String dataDomainType) {
		super(dataDomainType);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0,
				8, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLTexture.VIEW_ID;
	}

	public void setTexturePath(String texturePath) {
		this.texturePath = texturePath;
	}

	public String getTexturePath() {
		return texturePath;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getExperimentIndex() {
		return experimentIndex;
	}

	public void setExperimentIndex(int experimentIndex) {
		this.experimentIndex = experimentIndex;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}
}
