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

package jmetest.input.action;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.BaseGame;
import com.jme.bounding.BoundingSphere;
import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.RelativeMouse;
import com.jme.input.action.MouseLook;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TestBackwardAction</code>
 * @author Mark Powell
 * @version
 */
public class TestMouseLook extends BaseGame {
    private static final Logger logger = Logger.getLogger(TestMouseLook.class
            .getName());
    
    private Node scene;
    private Camera cam;
    private Line l;
    private Point p;
    private TriMesh t;
    private TriMesh t2;
    private InputHandler input;
    private Text text;

    /**
     * @see com.jme.app.SimpleGame#update
     */
    protected void update(float interpolation) {
        input.update(1);
    }

    /**
     * Render the scene
     * @see com.jme.app.SimpleGame#render
     */
    protected void render(float interpolation) {
        display.getRenderer().clearBuffers();
        display.getRenderer().draw(scene);
    }

    /**
     * set up the display system and camera.
     * @see com.jme.app.BaseGame#initSystem()
     */
    protected void initSystem() {
        try {
            display = DisplaySystem.getDisplaySystem(settings.getRenderer());
            display.createWindow(
                settings.getWidth(),
                settings.getHeight(),
                settings.getDepth(),
                settings.getFrequency(),
                settings.isFullscreen());
            cam =
                display.getRenderer().createCamera(
                    settings.getWidth(),
                    settings.getHeight());

        } catch (JmeException e) {
            logger.log(Level.SEVERE, "Could not create displaySystem", e);
            System.exit(1);
        }
        ColorRGBA blackColor = new ColorRGBA();
        blackColor.r = 0;
        blackColor.g = 0;
        blackColor.b = 0;
        display.getRenderer().setBackgroundColor(blackColor);
        cam.setFrustum(1.0f, 1000.0f, -0.55f, 0.55f, 0.4125f, -0.4125f);
        Vector3f loc = new Vector3f(4.0f, 0.0f, 0.0f);
        Vector3f left = new Vector3f(0.0f, -1.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 0.0f, 1.0f);
        Vector3f dir = new Vector3f(-1.0f, 0f, 0.0f);
        cam.setFrame(loc, left, up, dir);

        display.getRenderer().setCamera(cam);

        input = new InputHandler();

        RelativeMouse mouse = new RelativeMouse("Mouse Input");
        mouse.registerWithInputHandler( input );
        MouseLook mouseLook = new MouseLook(mouse, cam, 0.1f);
        mouseLook.setLockAxis(up);
        input.addAction(mouseLook);
    }

    /**
     * set up the scene
     * @see com.jme.app.BaseGame#initGame()
     */
    protected void initGame() {
        Vector3f[] vertex = new Vector3f[1000];
        ColorRGBA[] color = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex[i] = new Vector3f();
            vertex[i].x = (float) Math.random() * 50;
            vertex[i].y = (float) Math.random() * 50;
            vertex[i].z = (float) Math.random() * 50;
            color[i] = new ColorRGBA();
            color[i].r = (float) Math.random();
            color[i].g = (float) Math.random();
            color[i].b = (float) Math.random();
            color[i].a = 1.0f;
        }

        l = new Line("Line Group", vertex, null, color, null);
        l.setLocalTranslation(new Vector3f(-200.0f, -25, -25));
        l.setModelBound(new BoundingSphere());
        l.updateModelBound();

        Vector3f[] vertex2 = new Vector3f[1000];
        ColorRGBA[] color2 = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex2[i] = new Vector3f();
            vertex2[i].x = (float) Math.random() * -100 - 50;
            vertex2[i].y = (float) Math.random() * 50 - 25;
            vertex2[i].z = (float) Math.random() * 50 - 25;

            color2[i] = new ColorRGBA();
            color2[i].r = (float) Math.random();
            color2[i].g = (float) Math.random();
            color2[i].b = (float) Math.random();
            color2[i].a = 1.0f;
        }

        p = new Point("Point Group", vertex2, null, color2, null);
        p.setLocalTranslation(new Vector3f(0.0f, 25, 0));
        p.setModelBound(new BoundingSphere());
        p.updateModelBound();
        Node pointNode = new Node("Point Node");
        pointNode.attachChild(p);

        Vector3f[] verts = new Vector3f[3];
        ColorRGBA[] color3 = new ColorRGBA[3];

        verts[0] = new Vector3f();
        verts[0].x = -50;
        verts[0].y = 0;
        verts[0].z = 0;
        verts[1] = new Vector3f();
        verts[1].x = -50;
        verts[1].y = 25;
        verts[1].z = 25;
        verts[2] = new Vector3f();
        verts[2].x = -50;
        verts[2].y = 25;
        verts[2].z = 0;

        color3[0] = new ColorRGBA();
        color3[0].r = 1;
        color3[0].g = 0;
        color3[0].b = 0;
        color3[0].a = 1;
        color3[1] = new ColorRGBA();
        color3[1].r = 0;
        color3[1].g = 1;
        color3[1].b = 0;
        color3[1].a = 1;
        color3[2] = new ColorRGBA();
        color3[2].r = 0;
        color3[2].g = 0;
        color3[2].b = 1;
        color3[2].a = 1;
        int[] indices = { 0, 1, 2 };

        t = new TriMesh("Triangle 1", BufferUtils.createFloatBuffer(verts), null, BufferUtils.createFloatBuffer(color3), null, BufferUtils.createIntBuffer(indices));
        t.setLocalTranslation(new Vector3f(-150, 0, 0));
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        pointNode.attachChild(t);
        pointNode.setLocalTranslation(new Vector3f(0, -50, 0));

        //should be culled:

        Vector3f[] verts2 = new Vector3f[3];
        ColorRGBA[] color4 = new ColorRGBA[3];

        verts2[0] = new Vector3f();
        verts2[0].x = -50;
        verts2[0].y = 0;
        verts2[0].z = 0;
        verts2[1] = new Vector3f();
        verts2[1].x = -50;
        verts2[1].y = 25;
        verts2[1].z = 25;
        verts2[2] = new Vector3f();
        verts2[2].x = -50;
        verts2[2].y = 25;
        verts2[2].z = 0;

        color4[0] = new ColorRGBA();
        color4[0].r = 1;
        color4[0].g = 0;
        color4[0].b = 0;
        color4[0].a = 1;
        color4[1] = new ColorRGBA();
        color4[1].r = 0;
        color4[1].g = 1;
        color4[1].b = 0;
        color4[1].a = 1;
        color4[2] = new ColorRGBA();
        color4[2].r = 0;
        color4[2].g = 0;
        color4[2].b = 1;
        color4[2].a = 1;
        int[] indices2 = { 0, 1, 2 };

        t2 = new TriMesh("Triangle 2", BufferUtils.createFloatBuffer(verts2), null, BufferUtils.createFloatBuffer(color4), null, BufferUtils.createIntBuffer(indices2));
        t2.setLocalTranslation(new Vector3f(150, 0, 0));
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        scene = new Node("Scene graph Node");
        scene.attachChild(l);
        scene.attachChild(pointNode);
        scene.attachChild(t2);
        cam.update();

        text = new Text("Text Label", "");
        text.setLocalTranslation(new Vector3f(1, 60, 0));
        scene.attachChild(text);
        scene.updateRenderState();

        MouseInput.get().addListener( new MouseInputListener() {
            public void onButton( int button, boolean pressed, int x, int y ) {
                text.print( "button " + button + " " + (pressed?"pressed":"released") );
            }

            public void onWheel( int wheelDelta, int x, int y ) {
                text.print( "wheel scrolled " + wheelDelta );
            }

            public void onMove( int xDelta, int yDelta, int newX, int newY ) {
                text.print( "mouse moved by ("+xDelta+";"+yDelta+")");
            }
        } );

        scene.updateGeometricState(0.0f, true);
    }

    /**
     * not used.
     * @see com.jme.app.BaseGame#reinit()
     */
    protected void reinit() {
    }

    /**
     * not used.
     * @see com.jme.app.BaseGame#cleanup()
     */
    protected void cleanup() {
    }

    public static void main(String[] args) {
        TestMouseLook app = new TestMouseLook();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }
}
