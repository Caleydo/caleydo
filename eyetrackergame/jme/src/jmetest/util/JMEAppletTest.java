package jmetest.util;

import java.applet.Applet;
import java.awt.Component;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.media.opengl.GLAutoDrawable;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.system.jogl.JOGLSystemProvider;
import com.jme.util.TextureManager;
import com.jmex.awt.jogl.JOGLAWTCanvasConstructor;
import com.jmex.swt.lwjgl.LWJGLSWTConstants;
import com.sun.opengl.util.Animator;

/**
 * @author Joshua Slack
 * @author Steve Vaughan
 * @see JMEJOGLAWTTest
 */
public class JMEAppletTest extends Applet {

    /**
     * Generated serial version ID.
     */
    private static final long serialVersionUID = 825690860810038699L;

    private static final Logger logger = Logger.getLogger(JMEAppletTest.class
            .getName());

    private Animator animator;

    @Override
    public void init() {
        DisplaySystem ds = DisplaySystem
                .getDisplaySystem(JOGLSystemProvider.SYSTEM_IDENTIFIER);
        // TODO Shouldn't this be automatic, determined by the SystemProvider?
        ds.registerCanvasConstructor("AWT", JOGLAWTCanvasConstructor.class);

        // TODO Shouldn't DEPTH_BITS be a part of the requested capabilities for
        // the display system? Why would this be specific to the canvas
        // instance?
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(LWJGLSWTConstants.DEPTH_BITS, 8);
        // If I'm asking for a canvas, and canvases can be resized, then why
        // specify the width and height? Note the call to shell.setSize in the
        // JMESWTTest class.
        final JMECanvas jmeCanvas = ds.createCanvas(this.getWidth(), this
                .getHeight(), "AWT", props);

        // XXX Note that the canvas can be added to the frame without any prior
        // interaction (such as parameter passing to createCanvas).
        // final JFrame frame = new
        // JFrame("jMonkey Engine JOGL AWT Canvas Test");
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Canvas canvas = new java.awt.Canvas();
        // frame.add(canvas);
        // canvas.setSize(width, height);
        // frame.add((Component) jmeCanvas);
        this.add((Component) jmeCanvas);
        // ((Component) jmeCanvas).setSize(width, height); // FIXME Does this
        // // belong here?
        // // Refactoring!
        // frame.pack();

        // TODO Are we required to use the JMonkey Engine input methods?
        // KeyInput.setProvider(AWTKeyInput.class.getCanonicalName());
        // canvas.addKeyListener((SWTKeyInput) KeyInput.get());

        // TODO Are we required to use the JMonkey Engine input methods?
        // SWTMouseInput.setup(canvas, true);

        // Important! Here is where we add the guts to the panel:
        MyImplementor impl = new MyImplementor(this.getWidth(), this
                .getHeight());
        jmeCanvas.setImplementor(impl);
        jmeCanvas.setUpdateInput(true);
        jmeCanvas.setTargetRate(60);

        // shell.setText("SWT/JME Example");
        // shell.setSize(width, height);
        // shell.open();

        // canvas.init();
        // canvas.render();

        // frame.setVisible(true);

        // for (int i = 0; i < 100; ++i) {
        // logger.info ("Calling display (" + i + ")...");
        // ((GLAutoDrawable)jmeCanvas).display();
        // try {
        // Thread.sleep(500);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }

        // while (!shell.isDisposed()) {
        // if (!display.readAndDispatch())
        // display.sleep();
        // }

        // FIXME Where does this go?
        // display.dispose();
        animator = new Animator((GLAutoDrawable) jmeCanvas);
    }

    @Override
    public void start() {
        if (!animator.isAnimating())
            animator.start();
    }

    @Override
    public void destroy() {
        if (animator.isAnimating())
            animator.stop();

        DisplaySystem.getDisplaySystem().close();
    }

    static class MyImplementor extends SimpleCanvasImpl {

        private Quaternion rotQuat;

        private float angle = 0;

        private Vector3f axis;

        private Box box;

        long startTime = 0;

        long fps = 0;

        private InputHandler input;

        public MyImplementor(int width, int height) {
            super(width, height);
        }

        public void simpleSetup() {
            // Normal Scene setup stuff...
            rotQuat = new Quaternion();
            axis = new Vector3f(1, 1, 0.5f);
            axis.normalizeLocal();

            Vector3f max = new Vector3f(5, 5, 5);
            Vector3f min = new Vector3f(-5, -5, -5);

            box = new Box("Box", min, max);
            box.setModelBound(new BoundingBox());
            box.updateModelBound();
            box.setLocalTranslation(new Vector3f(0, 0, -10));
            box.setRenderQueueMode(Renderer.QUEUE_SKIP);
            rootNode.attachChild(box);

            box.setRandomColors();

            TextureState ts = renderer.createTextureState();
            ts.setEnabled(true);
            ts.setTexture(TextureManager.loadTexture(JMESwingTest.class
                    .getClassLoader().getResource(
                            "jmetest/data/images/Monkey.jpg"),
                    Texture.MinificationFilter.BilinearNearestMipMap,
                    Texture.MagnificationFilter.Bilinear));

            rootNode.setRenderState(ts);
            startTime = System.currentTimeMillis() + 5000;

            input = new FirstPersonHandler(cam, 50, 1);
        }

        public void simpleUpdate() {
            input.update(tpf);

            // Code for rotating the box... no surprises here.
            if (tpf < 1) {
                angle = angle + (tpf * 25);
                if (angle > 360) {
                    angle = 0;
                }
            }
            rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
            box.setLocalRotation(rotQuat);

            if (startTime > System.currentTimeMillis()) {
                fps++;
            } else {
                long timeUsed = 5000 + (startTime - System.currentTimeMillis());
                startTime = System.currentTimeMillis() + 5000;
                logger.info(fps + " frames in " + (timeUsed / 1000f)
                        + " seconds = " + (fps / (timeUsed / 1000f))
                        + " FPS (average)");
                fps = 0;
            }
        }
    }

}
