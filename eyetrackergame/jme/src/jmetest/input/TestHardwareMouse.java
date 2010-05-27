package jmetest.input;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Mouse;

import jmetest.renderer.TestEnvMap;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * <code>TestHardwareMouse</code>
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class TestHardwareMouse extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestHardwareMouse.class.getName());
    
	private AbsoluteMouse mouse;
	private Box spinningBox;
	private boolean useHardwareCursor;
	private Text currentTypeText;
	private final long SLEEPING_TIME = 20;

	public static void main(String[] args) {
		TestHardwareMouse app = new TestHardwareMouse();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	protected void simpleUpdate() {
		//Switch between software/hardware mousecursor
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("g", false)) {
			useHardwareCursor = !useHardwareCursor;
			if (useHardwareCursor) {
				currentTypeText.print("Current cursor type: [HARDWARE]");
				mouse.setCullHint(Spatial.CullHint.Always);
                Mouse.setGrabbed(false);
//                MouseInput.get().setCursorVisible(true);
				MouseInput.get().setHardwareCursor(TestHardwareMouse.class.getClassLoader().getResource("jmetest/data/cursor/cursor1.png"));
				//correction due to different hotspot positions between hardware/software. not needed when using only hardware etc.
				MouseInput.get().setCursorPosition((int) mouse.getLocalTranslation().x - mouse.getImageWidth() / 2,
												   (int) mouse.getLocalTranslation().y + mouse.getImageHeight() / 2);
			}
			else {
				currentTypeText.print("Current cursor type: [SOFTWARE]");
				mouse.setCullHint(Spatial.CullHint.Never);
				Mouse.setGrabbed(true);
//				MouseInput.get().setCursorVisible(false);
			}
		}

		//Sleep to simulate game fps
		try {
			Thread.sleep(SLEEPING_TIME);
		} catch (InterruptedException e) {
			logger.logp(Level.SEVERE, this.getClass().toString(), "simpleUpdate()", "Exception",
                    e);
		}
	}

	protected void simpleInitGame() {
		input = new InputHandler();

		cam.getLocation().set(0, 0, 150);
		cam.update();

		//Setup software mouse
		mouse = new AbsoluteMouse("Mouse Input", display.getWidth(), display.getHeight());
		mouse.registerWithInputHandler(input);
		TextureState cursor = display.getRenderer().createTextureState();
		cursor.setTexture(TextureManager.loadTexture(
				TestHardwareMouse.class.getClassLoader().getResource("jmetest/data/cursor/cursor1.png"),
				Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear));
		mouse.setRenderState(cursor);
		BlendState as1 = display.getRenderer().createBlendState();
		as1.setBlendEnabled(true);
		as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		as1.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		as1.setTestEnabled(true);
		as1.setTestFunction(BlendState.TestFunction.GreaterThan);
		mouse.setRenderState(as1);
		rootNode.attachChild(mouse);

		//Setup dummybox to show that we are running
		spinningBox = new Box("SpinBox", new Vector3f(), 10, 10, 10);
		spinningBox.setModelBound(new BoundingBox());
		spinningBox.updateModelBound();
		TextureState ts = display.getRenderer().createTextureState();
		Texture t0 = TextureManager.loadTexture(
				TestEnvMap.class.getClassLoader().getResource(
						"jmetest/data/images/Monkey.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t0.setWrap(Texture.WrapMode.Repeat);
		ts.setTexture(t0);
		spinningBox.setRenderState(ts);
		rootNode.attachChild(spinningBox);
		//Make the box spin around
		spinningBox.addController(new Controller() {
            private static final long serialVersionUID = 1L;
            float spinTime = 0;
			public void update(float time) {
				spinTime += time;
				spinningBox.getLocalRotation().fromAngles(spinTime, spinTime, spinTime);
			}
		});

		//Setup keybinding and help text
		KeyBindingManager.getKeyBindingManager().set("g", KeyInput.KEY_G);

		Text helpText = Text.createDefaultTextLabel("Text", "Using forced sleep(" + SLEEPING_TIME + ") to clearly show the difference...");
		helpText.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		helpText.setLightCombineMode(Spatial.LightCombineMode.Off);
		helpText.setLocalTranslation(new Vector3f(0, display.getHeight() - 20, 1));
		statNode.attachChild(helpText);

		Text helpText2 = Text.createDefaultTextLabel("Text", "Key 'G': Switch between software/hardware mouse cursor");
		helpText2.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		helpText2.setLightCombineMode(Spatial.LightCombineMode.Off);
		helpText2.setLocalTranslation(new Vector3f(0, 60, 1));
		statNode.attachChild(helpText2);

		currentTypeText = Text.createDefaultTextLabel("Text", "Current cursor type: [SOFTWARE]");
		currentTypeText.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		currentTypeText.setLightCombineMode(Spatial.LightCombineMode.Off);
		currentTypeText.setLocalTranslation(new Vector3f(0, 40, 1));
		statNode.attachChild(currentTypeText);
	}
}
