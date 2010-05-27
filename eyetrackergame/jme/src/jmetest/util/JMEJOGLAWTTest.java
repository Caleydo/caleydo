/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jmetest.util;

import java.awt.Component;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.media.opengl.GLAutoDrawable;
import javax.swing.JFrame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
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
 * Test for JOGL AWT Canvas implementation. Based upon {@link JMESWTTest}.
 * 
 * @author Joshua Slack
 * @author Steve Vaughan
 * @see JMESWTTest
 */

public class JMEJOGLAWTTest {

    private static final Logger logger = Logger.getLogger(JMEJOGLAWTTest.class
            .getName());
    
    private static Animator animator;

    static int width = 640, height = 480;

    public static void main(String[] args) {
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
        final JMECanvas jmeCanvas = ds
                .createCanvas(width, height, "AWT", props);
        jmeCanvas.setUpdateInput(true);
        jmeCanvas.setTargetRate(60);

        // XXX Note that the canvas can be added to the frame without any prior
        // interaction (such as parameter passing to createCanvas).
        final JFrame frame = new JFrame("jMonkey Engine JOGL AWT Canvas Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add((Component) jmeCanvas);
        frame.pack();

        // TODO Are we required to use the JMonkey Engine input methods?
        // KeyInput.setProvider(AWTKeyInput.class.getCanonicalName());
        // canvas.addKeyListener((SWTKeyInput) KeyInput.get());

        // TODO Are we required to use the JMonkey Engine input methods?
        // SWTMouseInput.setup(canvas, true);

        // Important! Here is where we add the guts to the panel:
        MyImplementor impl = new MyImplementor(width, height);
        jmeCanvas.setImplementor(impl);

        // TODO Remove when complete (original SWT code).
        // shell.setText("SWT/JME Example");
        // shell.setSize(width, height);
        // shell.open();

        // TODO Remove when complete (original SWT code).
        // canvas.init();
        // canvas.render();
        
        frame.setVisible(true);

        // FIXME Encapsulate this within the canvas in some fashion?
        animator = new Animator((GLAutoDrawable) jmeCanvas);
        animator.start();

        // TODO Remove when complete (original SWT code).
        // while (!shell.isDisposed()) {
        // if (!display.readAndDispatch())
        // display.sleep();
        // }

        // FIXME Where does this go?
        // display.dispose();
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

        @Override
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
            input.addAction(new InputAction() {
                public void performAction(InputActionEvent evt) {
                    DisplaySystem.getDisplaySystem().getRenderer().cleanup();
                    TextureManager.doTextureCleanup();
                    TextureManager.clearCache();
                    // Run this on another thread than the opengl thread to
                    // make sure the call to Animator.stop() completes before
                    // exiting
                    new Thread(new Runnable() {
                        public void run() {
                            animator.stop();
                            System.exit(0);
                        }
                    }).start();
                }
            }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_ESCAPE,
              InputHandler.AXIS_NONE, false );
        }

        @Override
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
