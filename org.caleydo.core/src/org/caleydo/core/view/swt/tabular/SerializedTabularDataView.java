package org.caleydo.core.view.swt.tabular;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * Serialized form of a tabular-browser view. 
 * @author Werner Puff
 */
public class SerializedTabularDataView 
	extends ASerializedView {

	public static final String GUI_ID = "org.caleydo.rcp.views.swt.TabularDataView";
	
	@Override
	public ECommandType getCreationCommandType() {
		return null;
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
