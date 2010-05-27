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

import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Cylinder;

/**
 * Started Date: Jun 26, 2004<br><br>
 *
 * This class test the ability to use OBB.
 *
 * @author Jack Lindamood
 */
public class TestOrientedBox extends SimpleGame{
    private static final Logger logger = Logger.getLogger(TestOrientedBox.class
            .getName());
    
    public static void main(String[] args) {

        TestOrientedBox app=new TestOrientedBox();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    Node AABBnode=new Node("AABBNode");
    Node OBBnode=new Node("OBBNode");

    Quaternion smallrotationx;
    Quaternion smallrotationy;
    Quaternion tempQ=new Quaternion();

    protected void simpleInitGame() {
        {
            Cylinder c=new Cylinder("cylinder",20,20,1,10);
            c.setModelBound(new BoundingBox());
//            c.setModelBound(new OrientedBoundingBox());
            c.updateModelBound();
            AABBnode.attachChild(c);
        }
        {
            Cylinder c2=new Cylinder("cylinder2",20,20,1,10);
            c2.setLocalTranslation(new Vector3f(5,10,0));
//            c2.setModelBound(new BoundingSphere());
//            c2.setModelBound(new BoundingBox());
            c2.setModelBound(new OrientedBoundingBox());
            c2.updateModelBound();
            OBBnode.attachChild(c2);
        }
        AABBnode.updateGeometricState(0,true);
        AABBnode.updateRenderState();
        OBBnode.updateGeometricState(0,true);
        OBBnode.updateRenderState();

        smallrotationy=new Quaternion();
        smallrotationy.fromAngleNormalAxis(FastMath.PI/2,new Vector3f(0,1,0));

        smallrotationx=new Quaternion();
        smallrotationx.fromAngleNormalAxis(FastMath.PI/2,new Vector3f(0,0,1));

        Quaternion upright=new Quaternion();
        upright.fromAngleNormalAxis(FastMath.PI/2,new Vector3f(1,0,0));
        OBBnode.setLocalRotation(new Quaternion(upright));
        AABBnode.setLocalRotation(new Quaternion(upright));


        rootNode.attachChild(AABBnode);
        rootNode.attachChild(OBBnode);
    }
    int frames;
    float totalTime;
    protected void simpleUpdate(){
        tempQ.set(0,0,0,1);
        tempQ.slerp(smallrotationx,tpf);
        AABBnode.getLocalRotation().multLocal(
                tempQ);
        OBBnode.getLocalRotation().multLocal(
                tempQ);

        tempQ.set(0,0,0,1);
        tempQ.slerp(smallrotationy,tpf);
        AABBnode.getLocalRotation().multLocal(
                tempQ);

        tempQ.set(0,0,0,1);
        tempQ.slerp(smallrotationy,tpf/2);
        OBBnode.getLocalRotation().multLocal(
                tempQ);
        frames++;
        totalTime+=tpf;
        if (totalTime>2.5f){
            logger.info("FPS:" + (frames/totalTime));
            totalTime=0;
            frames=0;
        }
    }
}