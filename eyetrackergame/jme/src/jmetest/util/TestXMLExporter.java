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

package jmetest.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.export.xml.XMLExporter;
import com.jme.util.export.xml.XMLImporter;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;

/**
 * <code>TestExporter</code> (TH 2008-03017: modified to test using special characters during export/import.
 * @version $Id: TestXMLExporter.java 4278 2009-04-15 17:40:08Z christoph.luder $
 */
public class TestXMLExporter extends SimpleGame {
    private static final Logger logger = Logger.getLogger(TestXMLExporter.class
            .getName());

    private Node t;

    /**
     * Entry point for the test,
     * 
     * @param args
     */
    public static void main(String[] args) {
        TestXMLExporter app = new TestXMLExporter();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();

    }

    protected void simpleInitGame() {
        lightState.setEnabled(false);
        
        String torusName = "T\u00D8rus"; // torus with a strike through the O
        torusName += "u\2623"; // add another unicode characters that can not be found in most 1 byte encodings (in this case the BIOHAZARD character)

        Torus torus = new Torus( torusName, 50, 50, 10, 20 );

        Quad background = new Quad("Background");
        background.updateGeometry(150, 120);
        background.setLocalTranslation(new Vector3f(0, 0, -30));

        // set up a ResourceLocators easily load textures
        try {
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
                        new SimpleResourceLocator(
                                TestXMLExporter.class.getResource("/jmetest/data/texture/")));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE,
                    new SimpleResourceLocator(
                            TestXMLExporter.class.getResource("/jmetest/data/images/")));
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
        
        Texture bg = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "clouds.png"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear);
        TextureState bgts = display.getRenderer().createTextureState();
        bgts.setTexture(bg);
        bgts.setEnabled(true);
        background.setRenderState(bgts);

        TextureState ts = display.getRenderer().createTextureState();
        // Base texture, not environmental map.
        Texture t0 = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "Monkey.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        // Environmental Map (reflection of clouds)
        Texture tex = TextureManager.loadTexture(
                ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, "clouds.png"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        tex.setEnvironmentalMapMode(Texture.EnvironmentalMapMode.SphereMap);
        ts.setTexture(t0, 0);
        ts.setTexture(tex, 1);
        ts.setEnabled(true);

        PointLight pl = new PointLight();
        pl.setAmbient(new ColorRGBA(0.75f, 0.75f, 0.75f, 1));
        pl.setDiffuse(new ColorRGBA(1, 0, 0, 1));
        pl.setLocation(new Vector3f(50, 0, 0));
        pl.setEnabled(true);

        lightState.attach(pl);

        torus.setRenderState(ts);
        t = new Node("main");
        t.attachChild(torus);
        t.attachChild(background);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            XMLExporter.getInstance().save(t, bos);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "BinaryExporter failed to save file", e);
        }

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        try {
            t = (Node) XMLImporter.getInstance().load(bis);
            rootNode.attachChild(t);
            if ( t.getChild(torusName) == null ) {
                logger.log(Level.SEVERE, "Unable to find our torus by using it's name, which contains special characters");
            }
            else logger.log(Level.INFO, "Finished loading export!");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "BinaryImporter failed to load file", e);
        }

    }
}
