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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;

/**
 * Started Date: Jul 20, 2004<br><br>
 *
 * Simple Node object with a few Geometry manipulators.
 * 
 * @author Jack Lindamood
 */
public class HelloNode extends SimpleGame {
    public static void main(String[] args) {
        HelloNode app = new HelloNode();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        Box b=new Box("My Box",new Vector3f(0,0,0),new Vector3f(1,1,1));
        // Give the box a bounds object to allow it to be culled
        b.setModelBound(new BoundingSphere());
        // Calculate the best bounds for the object you gave it
        b.updateModelBound();
        // Move the box 2 in the y direction up
        b.setLocalTranslation(new Vector3f(0,2,0));
        // Give the box a solid color of blue.
        b.setDefaultColor(ColorRGBA.blue.clone());

        Sphere s=new Sphere("My sphere",10,10,1f);
        // Do bounds for the sphere, but we'll use a BoundingBox this time
        s.setModelBound(new BoundingBox());
        s.updateModelBound();
        // Give the sphere random colors
        s.setRandomColors();

        // Make a node and give it children
        Node n=new Node("My Node");
        n.attachChild(b);
        n.attachChild(s);
        // Make the node and all its children 5 times larger.
        n.setLocalScale(5);

        // Remove  lighting for rootNode so that it will use our basic colors.
        rootNode.setLightCombineMode(Spatial.LightCombineMode.Off);
        rootNode.attachChild(n);
    }
}