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

import com.jme.animation.SpatialTransformer;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.light.PointLight;
import com.jme.light.SimpleLightNode;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;

/**
 * Started Date: Jul 21, 2004<br><br>
 *
 * This class demonstrates animation via a controller, as well as LightNode.
 *
 * @author Jack Lindamood
 */
public class HelloAnimation extends SimpleGame {
    public static void main(String[] args) {
        HelloAnimation app = new HelloAnimation();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        // Make my sphere and give it some bounds
        Sphere s=new Sphere("My sphere",30,30,5);
        s.setModelBound(new BoundingSphere());
        s.updateModelBound();
        // I will rotate this pivot to move my light
        Node pivot=new Node("Pivot node");

        // This light will rotate around my sphere.  Notice I don't give it a position
        PointLight pl=new PointLight();
        // Color the light red
        pl.setDiffuse(ColorRGBA.red.clone());
        // Enable the light
        pl.setEnabled(true);
        // Remove the default light and attach this one
        lightState.detachAll();
        lightState.attach(pl);

        // This node will hold my light
        SimpleLightNode ln=new SimpleLightNode("A node for my pointLight",pl);
        // I set the light's position thru the node
        ln.setLocalTranslation(new Vector3f(0,10,0));
        // I attach the light's node to my pivot
        pivot.attachChild(ln);

        // I create a box and attach it too my lightnode.  This lets me see where my light is
        Box b=new Box("Blarg",new Vector3f(-.3f,-.3f,-.3f),new Vector3f(.3f,.3f,.3f));
        // Give the box bounds
        b.setModelBound(new BoundingBox());
        b.updateModelBound();
        ln.attachChild(b);

        // I create a controller to rotate my pivot
        SpatialTransformer st=new SpatialTransformer(1);
        // I tell my spatial controller to change pivot
        st.setObject(pivot,0,-1);

        // Assign a rotation for object 0 at time 0 to rotate 0 degrees around the z axis
        Quaternion x0=new Quaternion();
        x0.fromAngleAxis(0,new Vector3f(0,0,1));
        st.setRotation(0,0,x0);

        // Assign a rotation for object 0 at time 2 to rotate 180 degrees around the z axis
        Quaternion x180=new Quaternion();
        x180.fromAngleAxis(FastMath.DEG_TO_RAD*180,new Vector3f(0,0,1));
        st.setRotation(0,2,x180);

        // Assign a rotation for object 0 at time 4 to rotate 360 degrees around the z axis
        Quaternion x360=new Quaternion();
        x360.fromAngleAxis(FastMath.DEG_TO_RAD*360,new Vector3f(0,0,1));
        st.setRotation(0,4,x360);

        // Prepare my controller to start moving around
        st.interpolateMissing();
        // Tell my pivot it is controlled by st
        pivot.addController(st);

        // Attach pivot and sphere to graph
        rootNode.attachChild(pivot);
        rootNode.attachChild(s);
    }
}