package org.caleydo.core.bridge.gui.standalone;

import org.caleydo.core.bridge.gui.IGUIBridge;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.eclipse.swt.widgets.Display;

// TODO: doku
public class SWTStandaloneBridge
	implements IGUIBridge {
	@Override
	public void closeApplication() {
		// TODO Auto-generated method stub
	}

	@Override
	public void setShortInfo(String sMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFileNameCurrentDataSet(String fileName) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileNameCurrentDataSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Display getDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createView(ASerializedView serializedView) {
		// TODO Auto-generated method stub
	}

	@Override
	public void closeView(String viewGUIID) {
		// TODO Auto-generated method stub

	}

	@Override
	public AGLView createGLEventListener(ECommandType type, GLCaleydoCanvas glCanvas, String label,
		IViewFrustum viewFrustum) {
		// TODO Auto-generated method stub
		return null;
	}

}
