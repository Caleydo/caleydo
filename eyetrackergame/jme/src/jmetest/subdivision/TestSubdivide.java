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
package jmetest.subdivision;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

import jmetest.renderer.TestBoxColor;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.NodeHandler;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.jme.scene.Point;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.subdivision.Subdivision;
import com.jmex.subdivision.SubdivisionButterfly;

/**
 * <code>TestSubdivision</code> demonstrates the usage of
 * <code>Subdivision</code> and <code>SubdivisionButterfly</code>. 
 * 
 * @author Tobias Andersson
 */
public class TestSubdivide extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestSubdivide.class
            .getName());
	
	TriMesh mesh;
	Subdivision subdivision;
	Point point = null;
	int pressed = 0;
	
	FloatBuffer vb1, vb2, vb3, vb4, vb5;
	IntBuffer ib1, ib2, ib3, ib4, ib5;
	FloatBuffer tb5;
	
	TextureState ts;
	
	/**
	 * Entry point for the test
	 * 
	 * @param args
	 *            arguments passed to the program
	 */
	public static void main(String[] args) {
		TestSubdivide app = new TestSubdivide();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}
	
	/* (non-Javadoc)
	 * @see com.jme.app.BaseSimpleGame#simpleUpdate()
	 */
	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		/** If subdivide is a valid command (via key z), then subdivide. */
		if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
				"subdivide", false ) ) {
			subdivide();
		}
		
	}
	
	public void subdivide() {
		float time;
		pressed++;
		if (pressed > 25) return;
		if (pressed == 3) {
			mesh.setVertexBuffer(vb1);
            mesh.setIndexBuffer(ib1);
            mesh.clearTextureBuffers();
			ts.setEnabled(false);
			mesh.updateModelBound();
			
			subdivision = new SubdivisionButterfly(mesh);
			subdivision.computeNormals();
			
		} else if (pressed == 7) {
            mesh.setVertexBuffer(vb2);
            mesh.setIndexBuffer(ib2);
            mesh.clearTextureBuffers();
			ts.setEnabled(false);
			mesh.updateModelBound();

			subdivision = new SubdivisionButterfly(mesh);
			subdivision.computeNormals();
			
		} else if (pressed == 11) {
            mesh.setVertexBuffer(vb3);
            mesh.setIndexBuffer(ib3);
            mesh.clearTextureBuffers();
			ts.setEnabled(false);
			mesh.updateModelBound();
			
			
			subdivision = new SubdivisionButterfly(mesh);
			subdivision.computeNormals();
			
		} else if (pressed == 15) {
            mesh.setVertexBuffer(vb4);
            mesh.setIndexBuffer(ib4);
            mesh.clearTextureBuffers();
            mesh.setRandomColors();
			ts.setEnabled(false);
			mesh.updateModelBound();

			subdivision = new SubdivisionButterfly(mesh);
			subdivision.computeNormals();
			
		} else if (pressed == 19) {
            mesh.setVertexBuffer(vb5);
            mesh.setIndexBuffer(ib5);
            mesh.setTextureCoords(new TexCoords(tb5), 0);
            mesh.setColorBuffer(null);
            mesh.setRandomColors();
			ts.setEnabled(true);
			mesh.updateModelBound();

			subdivision = new SubdivisionButterfly(mesh);
			subdivision.computeNormals();
			
		} else if (pressed == 22) {
            mesh.setVertexBuffer(vb2);
            mesh.setIndexBuffer(ib2);
            mesh.setColorBuffer(null);
            mesh.setSolidColor(new ColorRGBA(1f,1f,1f,1f));
            mesh.clearTextureBuffers();
			ts.setEnabled(false);
			lightState.setEnabled(true);
			mesh.updateModelBound();

			subdivision = new SubdivisionButterfly(mesh);
			subdivision.computeNormals();
			
		} else {
			time = DisplaySystem.getSystemProvider().getTimer().getTimeInSeconds();
			subdivision.subdivide();
			subdivision.apply();
			subdivision.computeNormals();
			logger.info("time for subdivision: " + (DisplaySystem.getSystemProvider().getTimer().getTimeInSeconds() - time));
		}
	}
	
	/**
	 * Sets up the test.
	 */
	protected void simpleInitGame() {
		display.setTitle("Hill Heightmap");
		
		CameraNode camNode = new CameraNode("Camera Node", cam);
		camNode.setLocalTranslation(new Vector3f(0, 0, 0));
		camNode.updateWorldData(0);
		input = new NodeHandler(camNode, 150, 1);
		rootNode.attachChild(camNode);
		
		// Set basic render states
		CullState cs = display.getRenderer().createCullState();
		cs.setCullFace(CullState.Face.Back);
		cs.setEnabled(true);
		rootNode.setRenderState(cs);
		
		
		// the meshes
		
		// Eight sided pyramid
		vb1 = BufferUtils.createVector3Buffer(10);
		ib1 = BufferUtils.createIntBuffer(16*3);
		vb1.put(10).put(15).put(20); // 0 
		vb1.put(15).put(10).put(20); // 1 
		vb1.put(20).put(10).put(20); // 2 
		vb1.put(25).put(15).put(20); // 3 
		vb1.put(25).put(20).put(20); // 4 
		vb1.put(20).put(25).put(20); // 5 
		vb1.put(15).put(25).put(20); // 6 
		vb1.put(10).put(20).put(20); // 7 
		vb1.put(17.5f).put(17.5f).put(40); // 8 
		vb1.put(17.5f).put(17.5f).put(0); // 9
		
		ib1.put(0).put(1).put(8);  
		ib1.put(1).put(2).put(8);  
		ib1.put(2).put(3).put(8);  
		ib1.put(3).put(4).put(8);  
		ib1.put(4).put(5).put(8);  
		ib1.put(5).put(6).put(8);  
		ib1.put(6).put(7).put(8);  
		ib1.put(7).put(0).put(8);  
		ib1.put(0).put(7).put(9);  
		ib1.put(7).put(6).put(9);  
		ib1.put(6).put(5).put(9);  
		ib1.put(5).put(4).put(9);  
		ib1.put(4).put(3).put(9);  
		ib1.put(3).put(2).put(9);  
		ib1.put(2).put(1).put(9);  
		ib1.put(1).put(0).put(9);  
		
		// Tetrahedron
		vb2 = BufferUtils.createVector3Buffer(4);
		ib2 = BufferUtils.createIntBuffer(4*3);
		vb2.put(10+0).put(10+0).put(10+0); // 0 
		vb2.put(10+25).put(10+0).put(10+0); // 1 
		vb2.put(10+12).put(10+0).put(10+18); // 2 
		vb2.put(10+12).put(10+18).put(10+12); // 3 
		
		ib2.put(0).put(1).put(2);  // botten
		ib2.put(0).put(2).put(3);  
		ib2.put(0).put(3).put(1);  
		ib2.put(1).put(3).put(2);
		
		// Star
		ib3 = BufferUtils.createIntBuffer(24*3);
		vb3 = BufferUtils.createVector3Buffer(14);
		
		vb3.put(25).put(25).put(25); // 0 
		vb3.put(25).put(35).put(25); // 1 
		vb3.put(35).put(25).put(25); // 2
		vb3.put(35).put(35).put(25); // 3
		vb3.put(25).put(25).put(35); // 4
		vb3.put(25).put(35).put(35); // 5
		vb3.put(35).put(25).put(35); // 6
		vb3.put(35).put(35).put(35); // 7
		vb3.put(30).put(30).put(0);  // 8
		vb3.put(30).put(30).put(60); // 9
		vb3.put(30).put(0).put(30);  // 10
		vb3.put(30).put(60).put(30); // 11
		vb3.put(0).put(30).put(30);  // 12
		vb3.put(60).put(30).put(30); // 13
		
		ib3.put(0).put(8).put(2);
		ib3.put(2).put(8).put(3);
		ib3.put(1).put(3).put(8);
		ib3.put(0).put(1).put(8);
		
		ib3.put(4).put(6).put(9);
		ib3.put(4).put(9).put(5);
		ib3.put(6).put(7).put(9);
		ib3.put(5).put(9).put(7);
		
		ib3.put(0).put(12).put(1);
		ib3.put(0).put(4).put(12);
		ib3.put(4).put(5).put(12);
		ib3.put(5).put(1).put(12);
		
		ib3.put(0).put(2).put(10);
		ib3.put(4).put(10).put(6);
		ib3.put(0).put(10).put(4);
		ib3.put(2).put(6).put(10);
		
		ib3.put(1).put(5).put(11);
		ib3.put(3).put(11).put(7);
		ib3.put(5).put(7).put(11);
		ib3.put(1).put(11).put(3);
		
		ib3.put(2).put(13).put(6);
		ib3.put(3).put(7).put(13);
		ib3.put(2).put(3).put(13);
		ib3.put(6).put(13).put(7);
		
		
		// Disc
		int sections = 10;
		float size = 30f;
		ib4 = BufferUtils.createIntBuffer(sections*3*2);
		vb4 = BufferUtils.createVector3Buffer(2*sections);
		
		double theta;
		for (int x = 0; x<sections; x++) {
			theta = 2*Math.PI*(double)x/(double)sections;
			vb4.put(size*(float)Math.cos(theta)).put(0f).put(size*(float)Math.sin(theta));
			vb4.put(size/2f*(float)Math.cos(theta)).put(0f).put(size/2f*(float)Math.sin(theta));
			
			if (x != sections-1) { 
				ib4.put(2*x).put(2*x+1).put(2*x+2);
				ib4.put(2*x+2).put(2*x+1).put(2*x+3);
			} else {
				ib4.put(2*x).put(2*x+1).put(0);
				ib4.put(0).put(2*x+1).put(1);
			}
		}
		
		// floor with a bump
		ib5 = BufferUtils.createIntBuffer(9*9*2*3); // 9 rows, 9 cols, 2 tris, 3 indices
		tb5 = BufferUtils.createVector2Buffer(100);
		vb5 = BufferUtils.createVector3Buffer(100);
		for (int x=0; x<10; x++) 
			for (int z=0; z<10; z++) {
				if ((x>3 && x<7) && (z>3))
					vb5.put(x*10f).put(10f).put(z*10f);
				else
					vb5.put(x*10f).put(0f).put(z*10f);
				if (x!=9 && z!=9) {
					ib5.put(z*10 + x).put((z+1)*10 + x + 1).put((z+1)*10 + x);
					ib5.put(z*10 + x).put(z*10 + x + 1).put((z+1)*10 + x + 1);
				}
				
				tb5.put(x % 2).put(z % 2);
			}
		
        mesh = new TriMesh("test");
        mesh.setVertexBuffer(vb5);
        mesh.setIndexBuffer(ib5);
        mesh.setTextureCoords(new TexCoords(tb5), 0);
		mesh.setModelBound(new BoundingBox()); 
		mesh.updateModelBound(); 
		mesh.getLocalTranslation().x = -20f;
		mesh.getLocalTranslation().y = -20f;
		mesh.getLocalTranslation().z = 120f;
		
		rootNode.attachChild(mesh);
		
		subdivision = new SubdivisionButterfly(mesh);
		subdivision.computeNormals(mesh);
		
		ts = display.getRenderer().createTextureState();
		ts.setEnabled(true);
		ts.setTexture(
				TextureManager.loadTexture(
						TestBoxColor.class.getClassLoader().getResource(
						"jmetest/data/texture/wall.jpg"),
						Texture.MinificationFilter.Trilinear,
						Texture.MagnificationFilter.Bilinear));
		
		mesh.setRenderState(ts);
		
		lightState.setEnabled(true);
		
		
		/** Assign key Z to action "subdivide". */
		KeyBindingManager.getKeyBindingManager().set( "subdivide",
				KeyInput.KEY_Z );
		
	}
	
}