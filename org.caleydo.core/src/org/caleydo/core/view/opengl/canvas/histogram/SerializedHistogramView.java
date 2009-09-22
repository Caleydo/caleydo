package org.caleydo.core.view.opengl.canvas.histogram;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of the remote-rendering view (bucket).
 * 
 * @author Werner Puff
 */
@XmlRootElement
@XmlType
public class SerializedHistogramView
	extends ASerializedView {

	public SerializedHistogramView() {
	}
	
	public SerializedHistogramView(EDataDomain dataDomain) {
		super(dataDomain);
	}

	public static final String GUI_ID = "org.caleydo.rcp.views.opengl.GLHistogramView";

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_HISTOGRAM;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	@Override
	public String getViewGUIID() {
		return GUI_ID;
	}

}
