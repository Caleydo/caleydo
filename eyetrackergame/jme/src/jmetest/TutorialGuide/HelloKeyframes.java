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

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jme.app.SimpleGame;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.MaterialState;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.animation.KeyframeController;

/**
 * Started Date: Jul 23, 2004<br><br>
 *
 * Demonstrates making your own keyframe animations.
 *
 * @author Jack Lindamood
 */
public class HelloKeyframes extends SimpleGame {
    public static void main(String[] args) {
        HelloKeyframes app = new HelloKeyframes();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        // The box we start off looking like
        TriMesh startBox=new Sphere("begining box",15,15,3);
        // Null colors,normals,textures because they aren't being updated
        startBox.setColorBuffer(null);
        startBox.setNormalBuffer(null);
        startBox.setTextureCoords((ArrayList<TexCoords>)null);

        // The middle animation sphere
        TriMesh middleSphere=new Sphere("middleSphere sphere",15,15,3);
        middleSphere.setColorBuffer(null);
        middleSphere.setNormalBuffer(null);
        middleSphere.setTextureCoords((ArrayList<TexCoords>)null);

        // The end animation pyramid
        TriMesh endPyramid=new Sphere("End sphere",15,15,3);
        endPyramid.setColorBuffer(null);
        endPyramid.setNormalBuffer(null);
        endPyramid.setTextureCoords((ArrayList<TexCoords>)null);

        FloatBuffer boxVerts=startBox.getVertexBuffer();
        FloatBuffer sphereVerts=middleSphere.getVertexBuffer();
        FloatBuffer pyramidVerts=endPyramid.getVertexBuffer();

        Vector3f boxPos = new Vector3f(), spherePos = new Vector3f(), pyramidPos = new Vector3f();
        for (int i=0, len = sphereVerts.capacity()/3; i<len; i++){
            BufferUtils.populateFromBuffer(boxPos, boxVerts, i);
            BufferUtils.populateFromBuffer(spherePos, sphereVerts, i);
            BufferUtils.populateFromBuffer(pyramidPos, pyramidVerts, i);

            // The box is the sign of the sphere coords * 4
            boxPos.x =FastMath.sign(spherePos.x)*4;
            boxPos.y =FastMath.sign(spherePos.y)*4;
            boxPos.z =FastMath.sign(spherePos.z)*4;

            if (boxPos.y<0){    // The bottom of the pyramid
                pyramidPos.x=boxPos.x;
                pyramidPos.y=-4;
                pyramidPos.z=boxPos.z;
            }
            else    // The top of the pyramid
                pyramidPos.set(0,4,0);

            BufferUtils.setInBuffer(boxPos, boxVerts, i);
            BufferUtils.setInBuffer(spherePos, sphereVerts, i);
            BufferUtils.setInBuffer(pyramidPos, pyramidVerts, i);
        }

        // The object that will actually be rendered
        TriMesh renderedObject=new Sphere("Rendered Object",15,15,3);
        renderedObject.setLocalScale(2);

        // Create my KeyframeController
        KeyframeController kc=new KeyframeController();
        // Assign the object I'll be changing
        kc.setMorphingMesh(renderedObject);
        // Assign for a time, what my renderedObject will look like
        kc.setKeyframe(0,startBox);
        kc.setKeyframe(.5f,startBox);
        kc.setKeyframe(2.75f,middleSphere);
        kc.setKeyframe(3.25f,middleSphere);
        kc.setKeyframe(5.5f,endPyramid);
        kc.setKeyframe(6,endPyramid);
        kc.setRepeatType(Controller.RT_CYCLE);

        // Give it a red material with a green tint
        MaterialState redgreen=display.getRenderer().createMaterialState();
        redgreen.setDiffuse(ColorRGBA.red.clone());
        redgreen.setSpecular(ColorRGBA.green.clone());
        // Make it very affected by the Specular color.
        redgreen.setShininess(10f);
        renderedObject.setRenderState(redgreen);

        // Add the controller to my object
        renderedObject.addController(kc);
        rootNode.attachChild(renderedObject);
    }
}