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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.NodeHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TestNodeController</code> provides a test for control of a node, in
 * this case a camera node.
 * 
 * @author Mark Powell
 * @version $Id: TestNodeController.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestNodeController extends SimpleGame {

    private CameraNode cameraNode;

    public static void main(String[] args) {
        TestNodeController app = new TestNodeController();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * set up the scene
     * 
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {

        cameraNode = new CameraNode("Camera Node", cam);
        cameraNode.setLocalTranslation(new Vector3f(0, 0, -250));
        cameraNode.updateWorldData(0);

        input = new NodeHandler(cameraNode, 50f, .5f);

        lightState.setEnabled(false);

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

        final Line l = new Line("Line Group", vertex, null, color, null);
        l.setLocalTranslation(new Vector3f(-100.0f, -25, -25));

        Vector3f[] vertex2 = new Vector3f[1000];
        ColorRGBA[] color2 = new ColorRGBA[1000];
        for (int i = 0; i < 1000; i++) {
            vertex2[i] = new Vector3f();
            vertex2[i].x = (float) Math.random() * -50 + 25;
            vertex2[i].y = (float) Math.random() * 50 - 25;
            vertex2[i].z = (float) Math.random() * 50 - 25;

            color2[i] = new ColorRGBA();
            color2[i].r = (float) Math.random();
            color2[i].g = (float) Math.random();
            color2[i].b = (float) Math.random();
            color2[i].a = 1.0f;
        }

        final Point p = new Point("Point Group", vertex2, null, color2, null);
        p.setLocalTranslation(new Vector3f(100f, 10, 10));
        p.setPointSize(5);
        p.setAntialiased(true);
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

        final TriMesh t = new TriMesh("Triangle 1", BufferUtils.createFloatBuffer(verts),
                null, BufferUtils.createFloatBuffer(color3), null, BufferUtils
                        .createIntBuffer(indices));
        t.setLocalTranslation(new Vector3f(-100, 0, 0));

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

        final TriMesh t2 = new TriMesh("Triangle 2", BufferUtils.createFloatBuffer(verts2),
                null, BufferUtils.createFloatBuffer(color4), null, BufferUtils
                        .createIntBuffer(indices2));
        t2.setLocalTranslation(new Vector3f(100, 0, 0));

        rootNode.attachChild(l);
        rootNode.attachChild(pointNode);
        rootNode.attachChild(t);
        rootNode.attachChild(t2);
        rootNode.attachChild(cameraNode);
        
        // Setup bounding boxes on all scene elements.
        rootNode.setModelBound(new BoundingBox());
        rootNode.updateModelBound();
    }
}
