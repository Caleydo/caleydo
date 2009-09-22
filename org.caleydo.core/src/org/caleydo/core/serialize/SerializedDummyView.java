package org.caleydo.core.serialize;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.ViewFrustum;

/**
 * This class is a serialized form reduced to hold only the view-id.
 * It should only be used until all views have their own serialized form class.   
 * @author Werner Puff
 */
public class SerializedDummyView 
	extends ASerializedView {
	
	public SerializedDummyView() {
	}
	
	public SerializedDummyView(EDataDomain dataDomain) {
		super(dataDomain);
	}
	
	@Override
	public ECommandType getCreationCommandType() {
		return ECommandType.CMD_ID;
	}

	@Override
	public ViewFrustum getViewFrustum() {
		return null;
	}

	@Override
	public String getViewGUIID() {
		return null;
	}

}
