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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.light.LightControllerManager;
import com.jme.light.PointLight;
import com.jme.light.SimpleLightNode;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.LightState;

/**
 * 
 * @author Badmi
 */
public class TestLightStateController extends SimpleGame {

    Node colornode; //The node that stores the lights.

    static final float worldsize = 20;//The size of the world

    public static void main(String[] args) {
        TestLightStateController app = new TestLightStateController();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    /**
     * Creates the lights for the demo
     */

    void randomLight(int i) {
        //Chose the color for the lights.
        ColorRGBA LightColor = ColorRGBA.randomColor();

        //Create a sphere to show where the light is in the demo.
        Sphere LightSphere = new Sphere("lp" + i, 10, 10, .1f);
        LightSphere.setModelBound(new BoundingSphere());
        LightSphere.updateModelBound();
        LightSphere.setLightCombineMode(Spatial.LightCombineMode.Off);
        LightSphere.setDefaultColor(LightColor);

        //Create a new point light and fill out the properties
        PointLight pointLight = new PointLight();
        pointLight.setAttenuate(true);
        pointLight.setConstant(/* FastMath.rand.nextFloat() */.1f);
        pointLight.setLinear(/* FastMath.rand.nextFloat()* */.01f);
        pointLight.setQuadratic(/* FastMath.rand.nextFloat() */.1f);
        pointLight.setEnabled(true);
        pointLight.setDiffuse(LightColor);
        //pointLight.setSpecular(LightColor);
        pointLight
                .setAmbient(new com.jme.renderer.ColorRGBA(.1f, .1f, .1f, .1f));

        //Add the light to the world part 1:
        //Add the light to the state creator.
        LightControllerManager.addLight(pointLight);

        //Create a node to hold the light and add a light node with this light.
        final Node mnod = new Node("P" + i + " Light pos");
        SimpleLightNode ln = new SimpleLightNode("ln" + i, pointLight);
        mnod.setLocalTranslation(new Vector3f(FastMath.rand.nextFloat()
                * worldsize * 2 - worldsize, FastMath.rand.nextFloat()
                * worldsize * 2 - worldsize, FastMath.rand.nextFloat()
                * worldsize * 2 - worldsize));
        mnod.attachChild(LightSphere);
        
        mnod.addController(new Controller() {

            private static final long serialVersionUID = 1L;

            float timeX = FastMath.rand.nextFloat() * FastMath.PI * 8;
            float timeY = FastMath.rand.nextFloat() * FastMath.PI * 8;
            float timeZ = FastMath.rand.nextFloat() * FastMath.PI * 8;

            @Override
            public void update(float tpf) {
                this.timeX += tpf;
                this.timeY += tpf;
                this.timeZ += tpf;
                
                mnod.getLocalTranslation().set(FastMath.sin(this.timeX*0.4f) * worldsize * 1, 
                        FastMath.cos(this.timeY*0.5f) * worldsize * 1, 
                        FastMath.sin(this.timeZ*0.6f) * worldsize * 1);                
            }
        });

        mnod.attachChild(ln);

        //Add the light to the world part 2.
        colornode.attachChild(mnod);
    }

    void randomSphere(int i) {
        //Crate a sphere and position it.
        Sphere newSphere = new Sphere("sp" + i, 10, 10, 2);
        newSphere.setModelBound(new BoundingSphere());
        newSphere.updateModelBound();
        newSphere.setLocalTranslation(new Vector3f(FastMath.rand.nextFloat()
                * worldsize * 2 - worldsize, FastMath.rand.nextFloat()
                * worldsize * 2 - worldsize, FastMath.rand.nextFloat()
                * worldsize * 2 - worldsize));
        //Add a new state to the controller. We do not use createLightState
        // because it would require that the world bounds be updated first.
        LightState ls = com.jme.system.DisplaySystem.getDisplaySystem()
                .getRenderer().createLightState();
        ls.setEnabled(true);
        newSphere.setRenderState(ls);//StateCreator.createLightState(newSphere));

        //Create a controller to update the lighting and set the combine modes
        // to REPLACE.
        //!!All other combine modes will not work!!
        LightControllerManager.addSpatial(newSphere);
        newSphere.setLightCombineMode(Spatial.LightCombineMode.Replace);

        rootNode.attachChild(newSphere);
    }

    protected void simpleInitGame() {
        //First we remove all the lights from the light state. And create a
        // light state controller.
        this.lightState.detachAll();

        FastMath.rand.setSeed(1337);

        //Now add all the lights.
        colornode = new Node("LightNode");
        for (int i = 0; i < 40; i++) {
            this.randomLight(i);
        }
        //Add the spheres.
        for (int i = 0; i < 30; i++) {
            this.randomSphere(i);
        }
        //We do not want to use lighting on the spears that represent lights so
        // we add a disabled render state.
        LightState nl = com.jme.system.DisplaySystem.getDisplaySystem()
                .getRenderer().createLightState();
        nl.setEnabled(false);
        colornode.setRenderState(nl);
        rootNode.attachChild(colornode);
        rootNode.updateRenderState();

    }

}

