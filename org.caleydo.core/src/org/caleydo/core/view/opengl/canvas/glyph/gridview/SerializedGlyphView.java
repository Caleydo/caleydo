package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.manager.datadomain.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a glyph-view.
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedGlyphView
	extends ASerializedView {

	public SerializedGlyphView() {
	}

	public SerializedGlyphView(EDataDomain dataDomain) {
		super(dataDomain);
	}

	@Override
	public ViewFrustum getViewFrustum() {
		ViewFrustum viewFrustum = new ViewFrustum(EProjectionMode.ORTHOGRAPHIC, 0, 2, 0, 2, -20, 20);
		return viewFrustum;
	}

	@Override
	public String getViewType() {
		return GLGlyph.VIEW_ID;
	}
}
