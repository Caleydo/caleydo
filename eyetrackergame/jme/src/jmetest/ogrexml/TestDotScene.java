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

package jmetest.ogrexml;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.system.DisplaySystem;
import com.jme.util.resource.ClasspathResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.ModelFormatException;
import com.jmex.model.ogrexml.SceneLoader;
import com.jmex.model.ogrexml.anim.MeshAnimationController;

/**
 * Shows how to load an Ogre dotScene file.
 *
 * The supplied model has 20 skeletal animations, and we cycle through them
 * for your visual entertainment.
 *
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 * @since jME 2.0
 * @version $Revision: 4336 $, $Date: 2009-05-03 22:57:01 +0200 (So, 03 Mai 2009) $
 */
public class TestDotScene extends SimpleGame {
    private static final Logger logger = Logger.getLogger(
            TestDotScene.class.getName());

    private MeshAnimationController controller = null;

    public static void main(String[] args){
        TestDotScene app = new TestDotScene();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    @Override
    protected void simpleInitGame() {
        DisplaySystem.getDisplaySystem().setTitle(baseTitle);
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_TEXTURE,
                new ClasspathResourceLocator());
        /* If you keep all of the scene resources in a single directory,
         * the SceneLoader class will find everything else automatically.
         * That's how we have the resources located for this example. */

        cam.setLocation(new Vector3f(0f, 1f, 3f));
          // Move the camera in closer

        SceneLoader ogreSceneLoader = null;
        String ninjaSceneString = "/jmetest/data/model/ogrexml/ninja-scene.xml";
        try {
            URL sceneUrl = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_MODEL, ninjaSceneString);

            if (sceneUrl == null)
                throw new IllegalStateException(
                        "Required runtime resource missing: "
                        + ninjaSceneString);
            ogreSceneLoader = new SceneLoader();
            ogreSceneLoader.load(sceneUrl);
            ogreSceneLoader.setModelsOnly(true);
             // modelsOnly means to ignore lights, cams, env. in scene file.
            rootNode.attachChild(ogreSceneLoader.getScene());
            logger.info("Successfully loaded and attached scene to rootNode");
        } catch (ModelFormatException mfe) {
            logger.log(Level.SEVERE, "Model file is corrupted", mfe);
            // Not recoverable.
            throw new RuntimeException(mfe);
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, "Unrecoverable I/O failure", ioe);
            // Not recoverable.
            throw new RuntimeException(ioe);
        } finally {
            ogreSceneLoader = null;  // encourage GC
              // Pretty useless here, but useful in a real app.
        }
        Spatial ninjaNode = rootNode.getChild("Ninja");
        // N.b. Spatial.getChild(String) returns a descendant, not necessarily
        // a child.
        if (ninjaNode == null)
            throw new RuntimeException("The 'Ninja' is missing");
        controller = (MeshAnimationController) ninjaNode.getController(0);
        if (controller == null)
            throw new RuntimeException("'Ninja' is missing his Controller");
        animationNames = controller.getAnimationNames().toArray(new String[0]);
        logger.info(Integer.toString(animationNames.length)
                + " animations loaded");
    }

    private long switchTime = -1L;
    // We will switch to next animation when this time is reached
    private long cycleMillis = 3000L;
    private String[] animationNames = null;
    private int animationIndex = -1;
    static private String baseTitle =
            TestDotScene.class.getName().replaceFirst(".*\\.", "");

    public void retitle() {
        DisplaySystem.getDisplaySystem().setTitle(
                baseTitle + " | " + (animationIndex + 1) + '/'
                + animationNames.length + " : "
                + animationNames[animationIndex]);
    }

    @Override
    public void simpleUpdate() {
        super.simpleUpdate();
        long now = new Date().getTime();
        if (switchTime > now) return;
        if (++animationIndex == animationNames.length) animationIndex = 0;
        controller.setAnimation(animationNames[animationIndex]);
        switchTime = now + cycleMillis;
        retitle();
    }
}
