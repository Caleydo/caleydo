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

package jmetest.TutorialGuide;

import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;


/**
 * Started Date: Jul 20, 2004<br><br>
 *
 * Demonstrates using RenderStates with jME.
 * 
 * @author Jack Lindamood
 */
public class HelloStates extends SimpleGame {
    public static void main(String[] args) {
        HelloStates app = new HelloStates();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {

        // Create our objects.  Nothing new here.
        Box b=new Box("my box",new Vector3f(1,1,1),new Vector3f(2,2,2));
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        Sphere s=new Sphere("My sphere",15,15,1);
        s.setModelBound(new BoundingSphere());
        s.updateModelBound();
        Node n=new Node("My root node");

        // Get a URL that points to the texture we're going to load
        URL monkeyLoc;
        monkeyLoc=HelloStates.class.getClassLoader().getResource("jmetest/data/images/Monkey.jpg");

        // Get a TextureState
        TextureState ts=display.getRenderer().createTextureState();
        // Use the TextureManager to load a texture
        Texture t=TextureManager.loadTexture(monkeyLoc,Texture.MinificationFilter.BilinearNearestMipMap,Texture.MagnificationFilter.Bilinear);
        // Assign the texture to the TextureState
        ts.setTexture(t);

        // Get a MaterialState
        MaterialState ms=display.getRenderer().createMaterialState();
        // Give the MaterialState an emissive tint
        ms.setEmissive(new ColorRGBA(0f,.2f,0f,1));

        // Create a point light
        PointLight l=new PointLight();
        // Give it a location
        l.setLocation(new Vector3f(0,10,5));
        // Make it a red light
        l.setDiffuse(ColorRGBA.red.clone());
        // Enable it
        l.setEnabled(true);

        // Create a LightState to put my light in
        LightState ls=display.getRenderer().createLightState();
        // Attach the light
        ls.attach(l);


        // Signal that b should use renderstate ts
        b.setRenderState(ts);
        // Signal that n should use renderstate ms
        n.setRenderState(ms);
        // Detach all the default lights made by SimpleGame
        lightState.detachAll();
        // Make my light effect everything below node n
        n.setRenderState(ls);

        // Attach b and s to n, and n to rootNode.
        n.attachChild(b);
        n.attachChild(s);
        rootNode.attachChild(n);
    }
}