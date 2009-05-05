package org.caleydo.core.view.opengl.canvas.storagebased;


import org.caleydo.core.view.opengl.keyboard.GLKeyListener;
import org.eclipse.swt.events.KeyEvent;

public class GLHeatMapKeyListener
	extends GLKeyListener {

	private GLHeatMap glHeatMap;
	
	public GLHeatMapKeyListener(GLHeatMap glHeatMap) {
		
		this.glHeatMap = glHeatMap;
	}
	
	@Override
	protected void handleKeyPressedEvent(KeyEvent event) {

		System.out.println("Key pressed in " +glHeatMap);
	}

}
