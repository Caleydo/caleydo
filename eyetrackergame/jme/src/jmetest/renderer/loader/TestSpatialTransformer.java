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

package jmetest.renderer.loader;

import com.jme.animation.SpatialTransformer;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;

/**
 * Started Date: Jul 12, 2004<br><br>
 *
 * Test Spatial Transformer animation.
 *
 * @author Jack Lindamood
 */
public class TestSpatialTransformer extends SimpleGame{
    public static void main(String[] args) {
        new TestSpatialTransformer().start();
    }
    protected void simpleInitGame() {
        Box b=new Box("box",new Vector3f(-1,-1,-1),new Vector3f(1,1,1));
        b.setRandomColors();
        b.setLocalTranslation(new Vector3f(0,5,0));
        Sphere s=new Sphere("sphere",new Vector3f(0,0,5),10,10,1);
        s.setRandomColors();
        SpatialTransformer st=new SpatialTransformer(2);

        st.setObject(b,0,-1);
        st.setObject(s,1,0);

        Quaternion x0=new Quaternion();
        x0.fromAngleAxis(0,new Vector3f(0,1,0));
        Quaternion x90=new Quaternion();
        x90.fromAngleAxis((float) (Math.PI/2),new Vector3f(0,1,0));
        Quaternion x180=new Quaternion();
        x180.fromAngleAxis((float) (Math.PI),new Vector3f(0,1,0));
        Quaternion x270=new Quaternion();
        x270.fromAngleAxis((float) (3*Math.PI/2),new Vector3f(0,1,0));

        st.setRotation(0,0,x0);
        st.setRotation(0,1,x90);
        st.setRotation(0,2,x180);
        st.setRotation(0,3,x270);
        st.setRotation(0,4,x0);

        st.setScale(0,0,new Vector3f(.25f,.25f,2));
        st.setScale(0,2,new Vector3f(2,2,2));
        st.setScale(0,4,new Vector3f(.25f,.25f,2));

        st.setPosition(0,0,new Vector3f(0,0,0));
        st.setPosition(0,2,new Vector3f(0,7,0));
        st.setPosition(0,4,new Vector3f(0,0,0));

        st.setPosition(1,0,new Vector3f(0,0,0));
        st.setPosition(1,2,new Vector3f(0,0,-5));
        st.setPosition(1,4,new Vector3f(0,0,0));

        st.interpolateMissing();
        b.addController(st);
        b.setModelBound(new BoundingSphere());
        b.updateModelBound();
        s.setModelBound(new BoundingSphere());
        s.updateModelBound();
        rootNode.attachChild(b);
        rootNode.attachChild(s);
        lightState.detachAll();
    }
}