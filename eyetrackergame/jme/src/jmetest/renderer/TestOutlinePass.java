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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimplePassGame;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.OutlinePass;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.util.export.binary.BinaryImporter;
import com.jmex.model.converters.Md2ToJme;

/**
 * Started Date: Jan 21, 2006<br>
 * 
 * This class test the Outline RenderPass.
 * 
 * @author Beskid Lucian Cristian
 * @version $Id: TestOutlinePass.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestOutlinePass extends SimplePassGame {
    private static final Logger logger = Logger.getLogger(TestOutlinePass.class
            .getName());
    
	private Node model = null;

	protected void simpleInitGame() {
		display.setTitle("Outline render pass test");
		display.getRenderer().setBackgroundColor(new ColorRGBA(0.5f, 0.7f, 1f, 1f));

		cam.setFrustumPerspective(55.0f, (float) display.getWidth() / (float) display.getHeight(), 1, 1000);
		cam.setLocation(new Vector3f(50, 0, 0));
		cam.lookAt(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));

		PointLight light = new PointLight();
		light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		light.setLocation(new Vector3f(0, 30, 0));
		light.setEnabled(true);
		lightState.attach(light);

		Node outlinedObjects = new Node("outlined");

		OutlinePass outlineRenderPass = new OutlinePass();
		outlineRenderPass.add(outlinedObjects);
		outlineRenderPass.setEnabled(true);

		pManager.add(outlineRenderPass);

		rootNode.attachChild(outlinedObjects);

		try {
			// load/convert the model
			Md2ToJme converter = new Md2ToJme();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			converter
					.convert(getClass().getClassLoader().getResourceAsStream("jmetest/data/model/drfreak.md2"), stream);
			model = (Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(stream.toByteArray()));

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load Md2 file", e);
		}

		if (model != null) {
			SharedNode outlinedModel = new SharedNode("outlined.model", model);

			outlinedObjects.attachChild(outlinedModel);
		}
		rootNode.updateGeometricState(0, true);
		rootNode.updateRenderState();
	}

	public static void main(String[] args) {
		TestOutlinePass app = new TestOutlinePass();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}
	
	public TestOutlinePass() {
		/* un-comment the line below line to enable AA */
		// this.samples = 2;
	}
}
