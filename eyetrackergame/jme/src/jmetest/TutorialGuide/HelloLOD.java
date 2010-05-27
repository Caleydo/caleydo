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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.curve.BezierCurve;
import com.jme.curve.CurveController;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.KeyExitAction;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.CameraNode;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.state.RenderState;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;

/**
 * Started Date: Aug 16, 2004<br><br>
 *
 * This program teaches Complex Level of Detail mesh objects.  To use this program, move
 * the camera backwards and watch the model disappear.
 * 
 * @author Jack Lindamood
 */
public class HelloLOD extends SimpleGame {
    private static final Logger logger = Logger.getLogger(HelloLOD.class
            .getName());

    CameraNode cn;

    public static void main(String[] args) {
        HelloLOD app = new HelloLOD();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        // Point to a URL of my model
        URL model=HelloLOD.class.getClassLoader().getResource("jmetest/data/model/maggie.obj");

        // Create something to convert .obj format to .jme
        FormatConverter converter=new ObjToJme();
        // Point the converter to where it will find the .mtl file from
        converter.setProperty("mtllib",model);

        // This byte array will hold my .jme file
        ByteArrayOutputStream BO=new ByteArrayOutputStream();
        Spatial maggie = null;
        try {
            // Use the format converter to convert .obj to .jme
            converter.convert(model.openStream(), BO);

            // Load the binary .jme format into a scene graph
            maggie=(Spatial)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
            

        } catch (IOException e) {   // Just in case anything happens
            logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
            System.exit(0);
        }

        // Create a clod duplicate of meshParent.
        Node clodNode=getClodNodeFromParent((Node)maggie);

        // Attach the clod mesh at the origin.
        clodNode.setLocalScale(.1f);
        rootNode.attachChild(clodNode);

        // Attach the original at -15,0,0
        maggie.setLocalScale(.1f);
        maggie.setLocalTranslation(new Vector3f(-15,0,0));

        rootNode.attachChild(maggie);

        // Clear the keyboard commands that can move the camera.
        input = new InputHandler();
        // Insert a keyboard command that can exit the application.
        input.addAction( new KeyExitAction(this), "exit", KeyInput.KEY_ESCAPE, false );

        // The path the camera will take.
        Vector3f[]cameraPoints=new Vector3f[]{
            new Vector3f(0,5,20),
            new Vector3f(0,20,90),
            new Vector3f(0,30,200),
            new Vector3f(0,100,300),
            new Vector3f(0,150,400),
        };
        // Create a path for the camera.
        BezierCurve bc=new BezierCurve("camera path",cameraPoints);

        // Create a camera node to move along that path.
        cn=new CameraNode("camera node",cam);

        // Create a curve controller to move the CameraNode along the path
        CurveController cc=new CurveController(bc,cn);

        // Cycle the animation.
        cc.setRepeatType(Controller.RT_CYCLE);

        // Slow down the curve controller a bit
        cc.setSpeed(.25f);

        // Add the controller to the node.
        cn.addController(cc);

        // Attach the node to rootNode
        rootNode.attachChild(cn);
    }

    private Node getClodNodeFromParent(Node meshParent) {
        // Create a node to hold my cLOD mesh objects
        Node clodNode=new Node("Clod node");
        // For each mesh in maggie
        for (int i=0;i<meshParent.getQuantity();i++){
            final Spatial child = meshParent.getChild(i);
            if ( child instanceof Node )
            {
                clodNode.attachChild( getClodNodeFromParent( (Node) child ) );
            }
            else if ( child instanceof TriMesh )
            {
                // Create an AreaClodMesh for that mesh.  Let it compute records automatically
                AreaClodMesh acm=new AreaClodMesh("part"+i,(TriMesh) child,null);
                acm.setModelBound(new BoundingSphere());
                acm.updateModelBound();

                // Allow 1/2 of a triangle in every pixel on the screen in the bounds.
                acm.setTrisPerPixel(.5f);

                // Force a move of 2 units before updating the mesh geometry
                acm.setDistanceTolerance(2);

                // Give the clodMesh node the material state that the original had.
                acm.setRenderState(child.getRenderState(RenderState.StateType.Material));

                // Attach clod node.
                clodNode.attachChild(acm);
            }
            else
            {
                logger.warning("Unhandled Spatial type: " + child.getClass());
            }
        }
        return clodNode;
    }

    Vector3f up=new Vector3f(0,1,0);
    Vector3f left=new Vector3f(1,0,0);

    private static Vector3f tempVa=new Vector3f();
    private static Vector3f tempVb=new Vector3f();
    private static Vector3f tempVc=new Vector3f();
    private static Vector3f tempVd=new Vector3f();
    private static Matrix3f tempMa=new Matrix3f();

    protected void simpleUpdate(){
        // Get the center of root's bound.
        Vector3f objectCenter=rootNode.getWorldBound().getCenter(tempVa);

        // My direction is the place I want to look minus the location of the camera.
        Vector3f lookAtObject=tempVb.set(objectCenter).subtractLocal(cam.getLocation()).normalizeLocal();

        // Left vector
        tempMa.setColumn(0,up.cross(lookAtObject,tempVc).normalizeLocal());
        // Up vector
        tempMa.setColumn(1,left.cross(lookAtObject,tempVd).normalizeLocal());
        // Direction vector
        tempMa.setColumn(2,lookAtObject);

        cn.setLocalRotation(tempMa);
    }
}