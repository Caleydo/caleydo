package jmetest.shape;

import jmetest.shape.TestTube;

import com.jme.app.SimpleGame;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.math.FastMath;
import com.jme.scene.shape.Disk;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * @author Ahmed Abdelkader
 */
public class DiskAngleTest extends SimpleGame {

	@Override
	protected void simpleInitGame() {
		display.setTitle("Disk-Angle Test");

		Disk myDisk1 = new Disk("",  2f*FastMath.HALF_PI, 4, 30, 5);
		Disk myDisk2 = new Disk("",  3f*FastMath.HALF_PI, 4, 30, 5);
		Disk myDisk3 = new Disk("", -2f*FastMath.HALF_PI, 4, 30, 5);
		Disk myDisk4 = new Disk("", -1f*FastMath.HALF_PI, 4, 30, 5);
		myDisk1.setLocalTranslation(-7, 3, 0);
		myDisk2.setLocalTranslation( 7, 3, 0);
		myDisk3.setLocalTranslation(-7, -3, 0);
		myDisk4.setLocalTranslation( 7, -3, 0);
		rootNode.attachChild(myDisk1);
		rootNode.attachChild(myDisk2);
		rootNode.attachChild(myDisk3);
		rootNode.attachChild(myDisk4);

		TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
		ts.setTexture(TextureManager.loadTexture(
				TestTube.class.getClassLoader().getResource(
						"jmetest/data/images/Monkey.jpg"),
				MinificationFilter.Trilinear, MagnificationFilter.Bilinear));
		ts.getTexture().setWrap(WrapMode.Repeat);
		rootNode.setRenderState(ts);
	}
	
	public static void main(String[] args) {
		new DiskAngleTest().start();
	}
}
