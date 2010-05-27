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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.input.NodeHandler;
import com.jme.light.DirectionalLight;
import com.jme.light.SpotLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.ZBufferState.TestFunction;
import com.jme.util.TextureManager;

/**
 * <code>TestLightState</code>
 * @author Mark Powell
 * @version $Id: TestCameraNode.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestCameraNode extends SimpleGame {
    
    private TriMesh t;
    private CameraNode camNode;
    private Node root;

    /**
     * Entry point for the test,
     * @param args
     */
    public static void main(String[] args) {
        TestCameraNode app = new TestCameraNode();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();

    }

    public void addSpatial(Spatial spatial) {
        rootNode.attachChild(spatial);
        rootNode.updateGeometricState(0.0f, true);
    }

    

    /**
     * builds the trimesh.
     * @see com.jme.app.SimpleGame#initGame()
     */
    protected void simpleInitGame() {
        camNode = new CameraNode( "Camera Node", display.getRenderer().getCamera());
        input = new NodeHandler( camNode, 15f, 1 );
        

        Vector3f max = new Vector3f(5,5,5);
        Vector3f min = new Vector3f(-5,-5,-5);



        t = new Box("Box 1", min,max);
        t.setModelBound(new BoundingSphere());
        t.updateModelBound();

        t.setLocalTranslation(new Vector3f(0,0,0));

        Box t2 = new Box("Box 2", min.divide(4), max.divide(4));
        t2.setLocalTranslation(new Vector3f(-5,0,10));
        t2.setModelBound(new BoundingSphere());
        t2.updateModelBound();

        rootNode.attachChild(t);
        root = new Node("Root Node");
        root.attachChild(rootNode);

        ZBufferState buf = display.getRenderer().createZBufferState();
        buf.setEnabled(true);
        buf.setFunction(TestFunction.LessThanOrEqualTo);

        SpotLight am = new SpotLight();
        am.setDiffuse(new ColorRGBA(0.0f, 1.0f, 0.0f, 1.0f));
        am.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am.setDirection(new Vector3f(0, 0, 0));
        am.setLocation(new Vector3f(25, 10, 0));
        am.setAngle(15);

        SpotLight am2 = new SpotLight();
        am2.setDiffuse(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        am2.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        am2.setDirection(new Vector3f(0, 0, 0));
        am2.setLocation(new Vector3f(-25, 10, 0));
        am2.setAngle(15);


        DirectionalLight dr = new DirectionalLight();
        dr.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        dr.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        dr.setSpecular(new ColorRGBA(1.0f, 0.0f, 0.0f, 1.0f));
        dr.setDirection(new Vector3f(150, 0 , 150));

        LightState state = display.getRenderer().createLightState();
        state.attach(am);
        state.attach(dr);
        state.attach(am2);
        am.setEnabled(true);
        am2.setEnabled(true);
        dr.setEnabled(true);
        rootNode.setRenderState(state);
        rootNode.setRenderState(buf);


        camNode.setLocalTranslation(new Vector3f(0,0,-75));
        camNode.attachChild(t2);
        rootNode.attachChild(camNode);

        //cam.update();

        TextureState ts = display.getRenderer().createTextureState();
                ts.setEnabled(true);
                ts.setTexture(
                    TextureManager.loadTexture(
                        TestCameraNode.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"),
                MinificationFilter.BilinearNoMipMaps,
                MagnificationFilter.Bilinear));

        rootNode.setRenderState(ts);



    }
    /**
     * not used.
     * @see com.jme.app.SimpleGame#reinit()
     */
    protected void reinit() {

    }

    /**
     * Not used.
     * @see com.jme.app.SimpleGame#cleanup()
     */
    protected void cleanup() {

    }

}
