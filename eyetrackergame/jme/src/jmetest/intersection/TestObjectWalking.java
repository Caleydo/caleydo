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

package jmetest.intersection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.CollisionTree;
import com.jme.bounding.CollisionTreeManager;
import com.jme.input.KeyInput;
import com.jme.intersection.PickData;
import com.jme.intersection.TrianglePickResults;
import com.jme.light.PointLight;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Point;
import com.jme.scene.SharedMesh;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Dodecahedron;
import com.jme.scene.shape.Octahedron;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.ZBufferState;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.MidPointHeightMap;

/**
 * Started Date: Jul 22, 2004 <br>
 * <br>
 * 
 * Demonstrates picking with the mouse.
 * 
 * @author Jack Lindamood
 */
public class TestObjectWalking extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestObjectWalking.class.getName());

	Node pickNode;

	private Line walkSelection;
	private Point pointWalk;
	
	private Vector3f oldCamLoc;
	public static void main(String[] args) {
		TestObjectWalking app = new TestObjectWalking();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	protected void simpleInitGame() {
		
		PointLight pl = new PointLight();
		pl.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 1));
		pl.setEnabled(true);
		pl.setLocation(new Vector3f(500,500,500));
		lightState.attach(pl);
		
		walkSelection = new Line("selected triangle", new Vector3f[4], null, new ColorRGBA[4], null);
		walkSelection.setSolidColor(new ColorRGBA(0,0,1,1));
		walkSelection.setLineWidth(5);
		walkSelection.setAntialiased(true);
		walkSelection.setMode(Line.Mode.Connected);
		
		ZBufferState zbs = display.getRenderer().createZBufferState();
		zbs.setFunction(ZBufferState.TestFunction.Always);
		walkSelection.setRenderState(zbs);
		walkSelection.setLightCombineMode(Spatial.LightCombineMode.Off);
		
		rootNode.attachChild(walkSelection);
		
		pointWalk = new Point("selected triangle", new Vector3f[1], null, new ColorRGBA[1], null);
		pointWalk.setSolidColor(new ColorRGBA(1,0,0,1));
		pointWalk.setPointSize(10);
		pointWalk.setAntialiased(true);
		
		pointWalk.setRenderState(zbs);
		pointWalk.setLightCombineMode(Spatial.LightCombineMode.Off);
		
		rootNode.attachChild(pointWalk);

        CollisionTreeManager.getInstance().setTreeType(CollisionTree.Type.AABB);
        
        MidPointHeightMap heightMap = new MidPointHeightMap(128, 1.9f);
        Vector3f terrainScale = new Vector3f(5,1,5);
        TerrainBlock terrain = new TerrainBlock("Terrain", heightMap.getSize(), terrainScale,
                                           heightMap.getHeightMap(),
                                           new Vector3f(0, 0, 0));
        terrain.setModelBound(new BoundingBox());
        terrain.updateModelBound();
        //test protection
        CollisionTreeManager.getInstance().generateCollisionTree(CollisionTree.Type.AABB, terrain, true);
        
        Box b = new Box("b", new Vector3f(0,0,0), 10, 10 ,10);
        
        SharedMesh sb = new SharedMesh("Shared box", b);
        sb.setModelBound(new BoundingBox());
        sb.updateModelBound();
        sb.setLocalTranslation(new Vector3f(100,terrain.getHeight(100, 200),200));
        
        SharedMesh sb2 = new SharedMesh("Shared box2", b);
        sb2.setModelBound(new BoundingBox());
        sb2.updateModelBound();
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(3, new Vector3f(1,0,0));
        sb2.setLocalRotation(quat);
        sb2.setLocalTranslation(new Vector3f(150,terrain.getHeight(150, 100),100));
        
        
        Octahedron o = new Octahedron("o", 10);
        o.setModelBound(new BoundingBox());
        o.updateModelBound();
        o.setLocalTranslation(new Vector3f(100,terrain.getHeight(100, 150),150));
        Dodecahedron d = new Dodecahedron("d", 10);
        Sphere s = new Sphere("sphere", 50, 50, 10);
        
        SharedMesh sm3 = new SharedMesh("Shared d1", d);
        sm3.setModelBound(new BoundingBox());
        sm3.updateModelBound();
        sm3.setLocalTranslation(new Vector3f(150,terrain.getHeight(150, 150),150));
        
        SharedMesh sm4 = new SharedMesh("Shared d2", d);
        sm4.setModelBound(new BoundingBox());
        sm4.updateModelBound();
        sm4.setLocalTranslation(new Vector3f(150,terrain.getHeight(150, 200),200));
        Quaternion qt = new Quaternion();
        qt.fromAngleAxis(2, new Vector3f(1,0,1));
        sm4.setLocalRotation(qt);
        
        SharedMesh sm = new SharedMesh("Shared Sphere1", s);
        sm.setModelBound(new BoundingBox());
        sm.updateModelBound();
        sm.setLocalTranslation(new Vector3f(100, terrain.getHeight(100,50), 50));
        
        SharedMesh sm2 = new SharedMesh("Shared Sphere2", s);
        sm2.setModelBound(new BoundingBox());
        sm2.updateModelBound();
        sm2.setLocalTranslation(new Vector3f(50, terrain.getHeight(50,100), 100));
        
        Sphere s2 = new Sphere("sphere2", 50, 50, 10);
        s2.setModelBound(new BoundingBox());
        s2.updateModelBound();
        s2.setLocalTranslation(new Vector3f(200, terrain.getHeight(200,50), 50));
        s2.setLocalScale(2);
        
        bridge = new Box("Bridge", new Vector3f(0,0,0), 100, 5, 10);
        bridge.setModelBound(new BoundingBox());
        bridge.updateModelBound();
        bridge.setLocalTranslation(new Vector3f(100, terrain.getHeight(100,50) + 5, 50));
        
        
        
		// Create the box in the middle. Give it a bounds
		URL model = TestObjectWalking.class.getClassLoader().getResource(
				"jmetest/data/model/maggie.obj");
		Spatial maggie = null;
		try {
			FormatConverter converter = new ObjToJme();
			converter.setProperty("mtllib", model);
			ByteArrayOutputStream BO = new ByteArrayOutputStream();
			converter.convert(model.openStream(), BO);
			maggie = (Spatial)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO
					.toByteArray()));
			//scale rotate and translate to confirm that world transforms are handled
			//correctly.
			maggie.setLocalScale(.1f);
			maggie.setLocalTranslation(new Vector3f(100,terrain.getHeight(100, 100),100));
			Quaternion q = new Quaternion();
			q.fromAngleAxis(0.5f, new Vector3f(0,1,0));
			maggie.setLocalRotation(q);
		} catch (IOException e) { // Just in case anything happens
			logger.logp(Level.SEVERE, this.getClass().toString(),
                    "simpleInitGame()", "Exception", e);
			System.exit(0);
		}
		
		maggie.setModelBound(new BoundingBox());
        maggie.updateModelBound();
        
        // Attach Children
        
        pickNode = new Node("Pick");
        pickNode.attachChild(maggie);
        pickNode.attachChild(terrain);
        pickNode.attachChild(sb);
        pickNode.attachChild(sb2);
        pickNode.attachChild(o);
        pickNode.attachChild(sm3);
        pickNode.attachChild(sm4);
        pickNode.attachChild(sm2);
        pickNode.attachChild(sm);
        pickNode.attachChild(s2);
        pickNode.attachChild(bridge);
        pickNode.updateGeometricState(0, true);
        
		rootNode.attachChild(pickNode);
		
		camResults.setCheckDistance(true);
        
        cam.setLocation(new Vector3f(50, terrain.getHeight(50,50)+10, 50));
        cam.setDirection(new Vector3f(0.5f,0,0.5f));
        cam.setLeft(new Vector3f(0.5f,0,-0.5f));
        cam.update();
        
        oldCamLoc = new Vector3f(cam.getLocation());
        
        pickNode.lockBounds();
		pickNode.lockTransforms();
    }

	TrianglePickResults camResults = new TrianglePickResults() {

		public void processPick() {
			//initialize selection triangles, this can go across multiple target
			//meshes.
			int total = 0;
			for(int i = 0; i < getNumber(); i++) {
				total += getPickData(i).getTargetTris().size();
			}
			if (getNumber() > 0) {
					PickData pData = getPickData(0);
					ArrayList<Integer> tris = pData.getTargetTris();
	                TriMesh mesh = (TriMesh) pData.getTargetMesh();
					if(tris.size() > 0) {	
						int triIndex = ((Integer) tris.get(0)).intValue();
						Vector3f[] vec = new Vector3f[3];
						mesh.getTriangle(triIndex, vec);
						for(int x = 0; x < vec.length; x++) {
							vec[x].multLocal(mesh.getWorldScale());
							mesh.getWorldRotation().mult(vec[x], vec[x]);
							vec[x].addLocal(mesh.getWorldTranslation());
						}
						
						Vector3f loc = new Vector3f();
						pData.getRay().intersectWhere(vec[0], vec[1], vec[2], loc);
						
						loc.y += 10;
//						if((loc.y - oldCamLoc.y) > 5f) {
//							logger.info("too big");
//							cam.getLocation().set(oldCamLoc);
//							cam.update();
//						} else {
							cam.getLocation().set(loc);
							cam.update();
							oldCamLoc.set(cam.getLocation());
						
							BufferUtils.setInBuffer(loc, pointWalk.getVertexBuffer(), 0);
							
							FloatBuffer buff = walkSelection.getVertexBuffer();
							BufferUtils.setInBuffer(vec[0], buff, 0);
							BufferUtils.setInBuffer(vec[1], buff, 1);
							BufferUtils.setInBuffer(vec[2], buff, 2);
							BufferUtils.setInBuffer(vec[0], buff, 3);
						//}
					} else {
						logger.info("No triangles");
					}
			} 
		}
	};
	
	Ray camRay = new Ray(new Vector3f(), new Vector3f(0,-1,0));

    private Box bridge;
	
	// This is called every frame. Do changing of values here.
	protected void simpleUpdate() {
		//lock camera to objects
		camRay.getOrigin().set(cam.getLocation());
		camResults.clear();
		pickNode.calculatePick(camRay, camResults);
        
        if(KeyInput.get().isKeyDown(KeyInput.KEY_BACKSLASH)) {
            pickNode.detachChild(bridge);
        }
	}
}