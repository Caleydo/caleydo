package jmetest.awt.applet;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jmex.awt.applet.SimpleApplet;

/**
 * Displaying a Box inside a Applet.<br>
 * This used the lwjgl2 kind of Applets.<br>
 * Press F to toggle between fullscreen and windowed mode.
 */
public class TestSimpleApplet extends SimpleApplet {
	private static final long serialVersionUID = 1L;

	/** 
	 * sets the desired size of the applet.
	 */
	@Override
	public void init() {
		System.setProperty("jme.stats", "set");
		setSize(640, 480);
		super.init();
	}
	/**
	 * create a simple Box.
	 */
	@Override
    protected void simpleInitGame() {
		Box b = new Box("A test box", Vector3f.ZERO, 5,5,5);
		b.setModelBound(new BoundingBox());
		b.updateModelBound();
    	rootNode.attachChild(b);
    }
	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
	}
}