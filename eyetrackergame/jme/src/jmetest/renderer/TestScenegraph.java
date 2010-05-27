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

package jmetest.renderer;

import java.nio.FloatBuffer;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.NodeHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TestScenegraph</code>
 *
 * @author Mark Powell
 * @version $Id: TestScenegraph.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestScenegraph extends SimpleGame {

    private Node scene;

    private NodeHandler nc1, nc2, nc3, nc4, nc5, nc6;

    private Box box1, box2, box3, box4, box5, box6;

    private Box selectionBox;

    private Node node1, node2, node3, node4, node5, node6;

    private Text text;

    private Node selectedNode;

    private TextureState ts, ts2, ts3;

    private Line line;

    /**
     * Entry point for the test,
     *
     * @param args
     */
    public static void main(String[] args) {
        TestScenegraph app = new TestScenegraph();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        input.update(timer.getTimePerFrame());
        updateLines();

        selectionBox.setLocalTranslation(selectedNode.getWorldTranslation());
        selectionBox.setLocalRotation(selectedNode.getWorldRotation());

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("tex1",
                false)) {
            selectedNode.setRenderState(ts);
            selectedNode.updateRenderState();
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("tex2",
                false)) {
            selectedNode.setRenderState(ts2);
            rootNode.updateRenderState();
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("tex3",
                false)) {
            selectedNode.setRenderState(ts3);
            rootNode.updateRenderState();
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand("notex",
                false)) {
            selectedNode.clearRenderState(RenderState.StateType.Texture);
            rootNode.updateRenderState();
        }

    }

    private void updateLines() {
        scene.updateGeometricState(0, true);
        FloatBuffer lineVerts = line.getVertexBuffer();
        lineVerts.rewind();
        BufferUtils.setInBuffer(node1.getWorldTranslation(), lineVerts, 0);
        BufferUtils.setInBuffer(node2.getWorldTranslation(), lineVerts, 1);
        BufferUtils.setInBuffer(node1.getWorldTranslation(), lineVerts, 2);
        BufferUtils.setInBuffer(node3.getWorldTranslation(), lineVerts, 3);
        BufferUtils.setInBuffer(node2.getWorldTranslation(), lineVerts, 4);
        BufferUtils.setInBuffer(node4.getWorldTranslation(), lineVerts, 5);
        BufferUtils.setInBuffer(node2.getWorldTranslation(), lineVerts, 6);
        BufferUtils.setInBuffer(node5.getWorldTranslation(), lineVerts, 7);
        BufferUtils.setInBuffer(node3.getWorldTranslation(), lineVerts, 8);
        BufferUtils.setInBuffer(node6.getWorldTranslation(), lineVerts, 9);
    }

    /**
     * builds the trimesh.
     *
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        Vector3f loc = new Vector3f(0.0f, 0.0f, -100.0f);
        Vector3f left = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f dir = new Vector3f(0.0f, 0f, 1.0f);
        cam.setFrame(loc, left, up, dir);
        cam.update();

        display.setTitle("Test Scene Graph");

        lightState.setEnabled(false);

        KeyBindingManager.getKeyBindingManager().set("notex", KeyInput.KEY_7);

        KeyBindingManager.getKeyBindingManager().set("tex1", KeyInput.KEY_8);

        KeyBindingManager.getKeyBindingManager().set("tex2", KeyInput.KEY_9);

        KeyBindingManager.getKeyBindingManager().set("tex3", KeyInput.KEY_0);

        KeyBindingManager.getKeyBindingManager().set("tog_bounds",
                KeyInput.KEY_B);

        Vector3f min = new Vector3f(-5, -5, -5);
        Vector3f max = new Vector3f(5, 5, 5);
        BlendState as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as1.setDestinationFunction(BlendState.DestinationFunction.One);
        as1.setTestEnabled(true);
        as1.setTestFunction(BlendState.TestFunction.GreaterThan);
        as1.setEnabled(true);

        DirectionalLight dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setDirection(new Vector3f(0, 0, 150));
        dr.setEnabled(true);
        lightState.detachAll();
        lightState.attach(dr);

        text = Text.createDefaultTextLabel("Selected Node", "Selected Node: Node 1");
        text.setLocalTranslation(new Vector3f(0, 20, 0));
        statNode.attachChild(text);

        scene = new Node("3D Scene Node");

        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        cs.setEnabled(true);
        rootNode.setRenderState(cs);

        selectionBox = new Box("Selection", min.mult(1.25f), max.mult(1.25f));
        selectionBox.setDefaultColor(new ColorRGBA(0, .6f, 0, 0.3f));
        selectionBox.setRenderState(as1);
        selectionBox.setModelBound(new BoundingSphere());
        selectionBox.updateModelBound();
        selectionBox.setLightCombineMode(LightCombineMode.Off);
        selectionBox.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);

        node1 = new Node("Node 1");
        box1 = new Box("Box 1", min, max);
        node1.attachChild(box1);
        node1.setLocalTranslation(new Vector3f(0, 30, 0));
        selectedNode = node1;
        box1.setModelBound(new BoundingSphere());
        box1.updateModelBound();

        node2 = new Node("Node 2");
        box2 = new Box("Box 2", min, max);
        node2.attachChild(box2);
        node1.attachChild(node2);
        node2.setLocalTranslation(new Vector3f(-20, -20, 0));
        box2.setModelBound(new BoundingSphere());
        box2.updateModelBound();

        node3 = new Node("Node 3");
        box3 = new Box("Box 3", min, max);
        node3.attachChild(box3);
        node1.attachChild(node3);
        node3.setLocalTranslation(new Vector3f(20, -20, 0));
        box3.setModelBound(new BoundingSphere());
        box3.updateModelBound();

        node4 = new Node("Node 4");
        box4 = new Box("Box 4", min, max);
        node4.attachChild(box4);
        node2.attachChild(node4);
        node4.setLocalTranslation(new Vector3f(-20, -20, 0));
        box4.setModelBound(new BoundingSphere());
        box4.updateModelBound();

        node5 = new Node("Node 5");
        box5 = new Box("Box 5", min, max);
        node5.attachChild(box5);
        node2.attachChild(node5);
        node5.setLocalTranslation(new Vector3f(20, -20, 0));
        box5.setModelBound(new BoundingSphere());
        box5.updateModelBound();

        node6 = new Node("Node 6");
        box6 = new Box("Box 6", min, max);
        node6.attachChild(box6);
        node3.attachChild(node6);
        node6.setLocalTranslation(new Vector3f(0, -20, 0));
        box6.setModelBound(new BoundingSphere());
        box6.updateModelBound();

        FloatBuffer verts = BufferUtils.createVector3Buffer(10); // 5 lines, 2 endpoints each
        line = new Line("Connection", verts, null, null, null);
        line.setLightCombineMode(LightCombineMode.Off);
        line.setLineWidth(2.5f);
        line.setStipplePattern((short)0xAAAA);
        line.setStippleFactor(5);

        ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        Texture t1 = TextureManager.loadTexture(
                TestScenegraph.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"), Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        ts.setTexture(t1);

        ts2 = display.getRenderer().createTextureState();
        ts2.setEnabled(true);
        Texture t2 = TextureManager.loadTexture(TestScenegraph.class
                .getClassLoader().getResource("jmetest/data/texture/dirt.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
        ts2.setTexture(t2);

        ts3 = display.getRenderer().createTextureState();
        ts3.setEnabled(true);
        Texture t3 = TextureManager.loadTexture(TestScenegraph.class
                .getClassLoader().getResource(
                        "jmetest/data/texture/snowflake.png"),
                Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
        ts3.setTexture(t3);

        node1.setRenderState(ts);

        scene.attachChild(node1);
        rootNode.attachChild(line);
        rootNode.attachChild(scene);
        scene.attachChild(selectionBox);

        nc1 = new NodeHandler(node1, 5, 1 );
        nc2 = new NodeHandler(node2, 5, 1 );
        nc3 = new NodeHandler(node3, 5, 1 );
        nc4 = new NodeHandler(node4, 5, 1 );
        nc5 = new NodeHandler(node5, 5, 1 );
        nc6 = new NodeHandler(node6, 5, 1 );

        input = new InputHandler();
        input.addToAttachedHandlers( nc1 );
        input.addToAttachedHandlers( nc2 );
        input.addToAttachedHandlers( nc3 );
        input.addToAttachedHandlers( nc4 );
        input.addToAttachedHandlers( nc5 );
        input.addToAttachedHandlers( nc6 );
        input.setEnabledOfAttachedHandlers( false );
        nc1.setEnabled( true );

        KeyBindingManager keyboard = KeyBindingManager.getKeyBindingManager();
        keyboard.set("node1", KeyInput.KEY_1);
        keyboard.set("node2", KeyInput.KEY_2);
        keyboard.set("node3", KeyInput.KEY_3);
        keyboard.set("node4", KeyInput.KEY_4);
        keyboard.set("node5", KeyInput.KEY_5);
        keyboard.set("node6", KeyInput.KEY_6);
        input.addAction( new TestNodeSelectionAction(this, 1), "node1", false );
        input.addAction( new TestNodeSelectionAction(this, 2), "node2", false );
        input.addAction( new TestNodeSelectionAction(this, 3), "node3", false );
        input.addAction( new TestNodeSelectionAction(this, 4), "node4", false );
        input.addAction( new TestNodeSelectionAction(this, 5), "node5", false );
        input.addAction( new TestNodeSelectionAction(this, 6), "node6", false );
    }

    public void setSelectedNode(int node) {
        input.setEnabledOfAttachedHandlers( false );
        switch (node) {
            case 1:
                nc1.setEnabled( true );
                selectedNode = node1;
            break;
            case 2:
                nc2.setEnabled( true );
                selectedNode = node2;
                break;
            case 3:
                nc3.setEnabled( true );
                selectedNode = node3;
                break;
            case 4:
                nc4.setEnabled( true );
                selectedNode = node4;
                break;
            case 5:
                nc5.setEnabled( true );
                selectedNode = node5;
                break;
            case 6:
                nc6.setEnabled( true );
                selectedNode = node6;
                break;
        }
        text.print("Selected Node: " + selectedNode.getName() );
    }
}
