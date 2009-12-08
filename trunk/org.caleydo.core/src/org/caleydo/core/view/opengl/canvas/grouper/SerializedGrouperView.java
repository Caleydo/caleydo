package org.caleydo.core.view.opengl.canvas.grouper;

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
 * @author Alexander Lex
 */
@XmlRootElement
@XmlType
public class SerializedGrouperView
	extends ASerializedView {

	public SerializedGrouperView() {
	}

	public SerializedGrouperView(EDataDomain dataDomain) {
		super(dataDomain);
	}

	public static final String GUI_ID = "org.caleydo.rcp.views.opengl.GLGrouperView";

	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CREATE_GL_GROUPER;
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
