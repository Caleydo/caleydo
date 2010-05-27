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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.jmex.model.animation.JointController;
import com.jmex.model.converters.MilkToJme;

/**
 * <code>TestPick</code>
 * 
 * @author Mark Powell
 * @version $Id: TestPick.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestPick extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestPick.class
            .getName());

	private Node model;

	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestPick app = new TestPick();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	/**
	 * builds the trimesh.
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
        try {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(TestPick.class
                            .getClassLoader().getResource(
                                    "jmetest/data/model/msascii/")));
        } catch (URISyntaxException e1) {
            logger.log(Level.WARNING, "unable to setup texture directory.", e1);
        }

        display.setTitle("Mouse Pick");
		cam.setLocation(new Vector3f(0.0f, 50.0f, 100.0f));
		cam.update();
		
        Text text = Text.createDefaultTextLabel("Test Label", "Hits: 0 Shots: 0");
        text.setCullHint(Spatial.CullHint.Never);
        text.setTextureCombineMode(TextureCombineMode.Replace);
        text.setLocalTranslation(new Vector3f(1, 60, 0));
		
        Text cross = Text.createDefaultTextLabel("Cross hairs", "+");
        cross.setCullHint(Spatial.CullHint.Never);
        cross.setTextureCombineMode(TextureCombineMode.Replace);
        cross.setLocalTranslation(new Vector3f(
				display.getWidth() / 2f - 8f, // 8 is half the width
														// of a font char
				display.getHeight() / 2f - 8f, 0));

        statNode.attachChild(text);
        statNode.attachChild(cross);

		MilkToJme converter = new MilkToJme();
		URL MSFile = TestPick.class.getClassLoader().getResource(
				"jmetest/data/model/msascii/run.ms3d");
		ByteArrayOutputStream BO = new ByteArrayOutputStream();

		try {
			converter.convert(MSFile.openStream(), BO);
		} catch (IOException e) {
			logger.info("IO problem writting the file!!!");
			logger.info(e.getMessage());
			System.exit(0);
		}
		model = null;
		try {
			model = (Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO
					.toByteArray()));
            model.setModelBound(new BoundingCapsule());
            model.updateModelBound();
		} catch (IOException e) {
			logger.info("darn exceptions:" + e.getMessage());
		}
		((JointController) model.getController(0)).setActive(false);
        

		Vector3f[] vertex = new Vector3f[1000];
		ColorRGBA[] color = new ColorRGBA[1000];
		for (int i = 0; i < 1000; i++) {
			vertex[i] = new Vector3f();
			vertex[i].x = FastMath.nextRandomFloat() * -100 - 50;
			vertex[i].y = FastMath.nextRandomFloat() * 50 - 25;
			vertex[i].z = FastMath.nextRandomFloat() * 50 - 25;
			color[i] = ColorRGBA.randomColor();
		}

		Line l = new Line("Line Group", vertex, null, color, null);
		l.setModelBound(new BoundingBox());
		l.updateModelBound();
		l.setLightCombineMode(Spatial.LightCombineMode.Off);

		rootNode.attachChild(l);
		rootNode.attachChild(model);

		MousePick pick = new MousePick(cam, rootNode, text);
		input.addAction(pick);
	}

}