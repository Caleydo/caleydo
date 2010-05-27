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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.net.MalformedURLException;

import com.jme.input.MouseInput;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.FirstPersonHandler;
import com.jme.util.export.xml.XMLImporter;
import com.jme.bounding.BoundingBox;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.MorphingTriMesh;
import com.jme.scene.Geometry;
import com.jme.scene.MorphingGeometry;
import com.jme.renderer.Renderer;
import com.jme.app.SimpleGame;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.ClasspathResourceLocator;
import com.jme.util.export.ListenableStringFloatMap;

/**
 * Demonstrates a simple morph consisting of 1 base Geometry + 1 Morph geometry.
 *
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 * @see #main(String[])
 */
public class TestMorph extends SimpleGame {
    static private final Logger logger =
            Logger.getLogger(TestMorph.class.getName());
    private URL[] morphUrls;
    private URL baseUrl;
    protected XMLImporter xmlImporter = XMLImporter.getInstance();
    protected float curMorphVal;
    protected KeyBindingManager keyBindingManager;
    protected MorphingGeometry mg;

    /** * Instantiate a jME game world, loading the specified jME XML models
     * into the scene.
     *
     * @param args
     *     <CODE><PRE>
     *     Syntax:  java... TestMorph [-r]
     *     </PRE><CODE>
     *     where "-r" means to display the settings widget.
     *
     */
    static public void main(String[] args) throws MalformedURLException {
        TestMorph.parseAndRun(new TestMorph(), args);
    }

    /**
     * Juset sets the model URLs for the TestMorph instance.
     */
    static protected void parseAndRun(TestMorph testMorph, String[] args)
            throws MalformedURLException {
        int counter = -1;
        URL url;
        List<URL> urls = new ArrayList<URL>();
        if (args.length > 0) {
            if (args[0].equals("-r")) {
                testMorph.setConfigShowMode(ConfigShowMode.AlwaysShow);
            } else {
                throw new IllegalArgumentException(
                        "SYNTAX:  " + TestMorph.class.getName() + " [-r]");
            }
        }
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_MODEL, new ClasspathResourceLocator());
        url = ResourceLocatorTool.locateResource(
                ResourceLocatorTool.TYPE_MODEL,
                "/jmetest/data/model/suzannetomorph-jme.xml");
        if (url == null) throw new MalformedURLException("Missing resource: "
                + "/jmetest/data/model/suzannetomorph-jme.xml");
        testMorph.setBaseUrl(url);
        url = ResourceLocatorTool.locateResource(
                ResourceLocatorTool.TYPE_MODEL,
                "/jmetest/data/model/conetomorph-jme.xml");
        if (url == null) throw new MalformedURLException("Missing resource: "
                + "/jmetest/data/model/conetomorph-jme.xml");
        urls.add(url);
        testMorph.setMorphUrls(urls.toArray(new URL[0]));
        testMorph.start();
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setMorphUrls(URL[] morphUrls) {
        this.morphUrls = morphUrls;
    }

    /**
     * Adds to scene a MorphingTriMesh, a Text Geometry and sets up key
     * handlers for PGUP and PGDN.
     */
    protected void simpleInitGame() {
        keyBindingManager = KeyBindingManager.getKeyBindingManager();
        curMorphVal = 0.5f;
        Map<String, Float> influences = new HashMap<String, Float>();
        String influenceKey;
        Text textGeo = Text.createDefaultTextLabel(
                "txtLblName", "Click PGUP and PGDN keys to morph");
        textGeo.setRenderQueueMode(Renderer.QUEUE_ORTHO);
        textGeo.setLightCombineMode(Spatial.LightCombineMode.Off);
        statNode.attachChild(textGeo);
        try {
            if (baseUrl == null)
                throw new IllegalStateException(TestMorph.class.getName()
                        + " not initialized properly");
            if (morphUrls == null)
                throw new IllegalStateException(TestMorph.class.getName()
                        + " not initialized properly");
            MouseInput.get().setCursorVisible(true);
            ((FirstPersonHandler) input).setButtonPressRequired(true);
            // Windowed mode is extremely irritating without these two settings.

            Spatial inModel = (Spatial) xmlImporter.load(baseUrl);
            if (!(inModel instanceof TriMesh))
                throw new IllegalArgumentException(
                        "Base model not a TriMesh");
            mg = new MorphingTriMesh("SuzConeMorph", (TriMesh) inModel);

            // Using a loop even though this example adds only a single
            // Morph Geometry to make it easier for you to add more.
            for (URL url : morphUrls) {
                inModel = (Spatial) xmlImporter.load(url);
                if (!(inModel instanceof TriMesh))
                    throw new IllegalArgumentException(
                            "Morph model not a TriMesh");
                influenceKey = url.getPath().replaceFirst(".*/", "");
                mg.addMorph(influenceKey, (TriMesh) inModel);
                influences.put(influenceKey, curMorphVal);
                logger.log(Level.SEVERE, "Loaded morph ''{0}''", influenceKey);
            }
            mg.setMorphInfluencesMap(new ListenableStringFloatMap());
            mg.setMorphInfluences(influences);
            mg.morph();
            mg.setAutoMorph(true);
        } catch (Exception e) {
            // Programs should not just continue obvliviously when exceptions
            // are thrown.  Since we aren't handling them, we exit gracefully.
            e.printStackTrace();
            finish();
        }

        ((Geometry) mg).setModelBound(new BoundingBox());
        rootNode.attachChild((Geometry) mg);
        ((Geometry) mg).updateModelBound();
        // The default update loop will update world bounding volumes.
        rootNode.updateRenderState();

        keyBindingManager.set("+conish", KeyInput.KEY_PGUP);
        keyBindingManager.set("-conish", KeyInput.KEY_PGDN);
    }

    /**
     * Just adds handling for PGUP and PGDOWN on top of SimpleGame's
     * simpleUpdate functionality.
     */
    protected void simpleUpdate() {
        super.simpleUpdate();
        // Just change the Morph Influence value if user presses + or -.

        if (keyBindingManager.isValidCommand("+conish", false)) {
            curMorphVal += .05f;
            mg.setSingleMorphInfluence("conetomorph-jme.xml", curMorphVal);
        } else if (keyBindingManager.isValidCommand("-conish", false)) {
            curMorphVal -= .05f;
            mg.setSingleMorphInfluence("conetomorph-jme.xml", curMorphVal);
        }
    }
}
