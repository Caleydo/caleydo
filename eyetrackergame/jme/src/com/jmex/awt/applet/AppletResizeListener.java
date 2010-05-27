package com.jmex.awt.applet;

import java.applet.Applet;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.Callable;

import com.jme.system.DisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;

/**
 * ComponentListener for use with the newer lwjgl2 Applets.<br>
 * Listens for resize events from an Applet and reinitializes the Renderer.
 */
public class AppletResizeListener implements ComponentListener {
	private Applet applet;
	
	public AppletResizeListener(Applet applet) {
		this.applet = applet;
	}
	
	public void componentHidden(ComponentEvent ce) {

	}

	public void componentMoved(ComponentEvent ce) {

	}

	/**
	 * Reinitializes the renderer based on the applets new size.<br>
	 * Sets the new width and height in the displaysystem.
	 */
	public void componentResized(final ComponentEvent ce) {
		Callable<?> exe = new Callable<Object>() {
			int w = applet.getWidth();
			int h = applet.getHeight();
			
		    public Object call() {
    			DisplaySystem display = DisplaySystem.getDisplaySystem();
    			display.getRenderer().reinit(w, h);
    			display.getRenderer().getCamera().setFrustumPerspective(45.0f,
    					(float) applet.getWidth() / (float)applet.getHeight(), 1, 1000);
    			display.setWidth(w);
    			display.setHeight(h);
    			return null;
		    }
		};
		GameTaskQueueManager.getManager()
			.getQueue(GameTaskQueue.RENDER).enqueue(exe);
	}

	public void componentShown(ComponentEvent ce) {

	}

}
