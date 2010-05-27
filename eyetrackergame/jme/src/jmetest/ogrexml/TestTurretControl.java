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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.input.KeyInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.util.resource.RelativeResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.ModelFormatException;
import com.jmex.model.ogrexml.MaterialLoader;
import com.jmex.model.ogrexml.OgreLoader;
import com.jmex.model.ogrexml.anim.Bone;
import com.jmex.model.ogrexml.anim.MeshAnimationController;

public class TestTurretControl extends SimpleGame {

    private static final Logger logger = Logger.getLogger(
            TestTurretControl.class.getName());

    private Spatial model;
    private Bone turretBone;
    private float angle = 0f;
    private float angleVel = 0f;

    public static void main(String[] args){
        TestTurretControl app = new TestTurretControl();
        app.setConfigShowMode(ConfigShowMode.NeverShow);
        app.start();
    }

    @Override
    protected void simpleUpdate(){
        // acceleration
        if (KeyInput.get().isKeyDown(KeyInput.KEY_LEFT)){
            angleVel += tpf * 0.03f;
        }else if (KeyInput.get().isKeyDown(KeyInput.KEY_RIGHT)){
            angleVel -= tpf * 0.03f;
        }

        // drag
        if (angleVel > FastMath.ZERO_TOLERANCE){
            angleVel = Math.max(0f, angleVel - (tpf * 0.025f));
        }else if (angleVel < -FastMath.ZERO_TOLERANCE){
            angleVel = Math.min(0f, angleVel + (tpf * 0.025f));
        }

        // speed limit
        if (angleVel < -0.1f)
            angleVel = -0.1f;
        else if (angleVel > 0.1f)
            angleVel = 0.1f;

        // apply velocity
        angle += angleVel;

        Quaternion tempRot = new Quaternion();
        tempRot.fromAngleAxis(angle, Vector3f.UNIT_Y);
        turretBone.setUserTransforms(Vector3f.ZERO, tempRot, Vector3f.UNIT_XYZ);
    }

    protected void loadMeshModel(){
        OgreLoader loader = new OgreLoader();
        MaterialLoader matLoader = new MaterialLoader();

        String matUrlString = "/jmetest/data/model/ogrexml/Turret.material";
        String turretMeshUrlString =
                "/jmetest/data/model/ogrexml/Turret.mesh.xml";

        try {
            URL matURL = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_TEXTURE, matUrlString);
            URL meshURL = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_MODEL, turretMeshUrlString);

            if (meshURL == null)
                throw new IllegalStateException(
                        "Required runtime resource missing: "
                        + turretMeshUrlString);
            if (matURL == null)
                throw new IllegalStateException(
                        "Required runtime resource missing: " + matUrlString);
            try {
                ResourceLocatorTool.addResourceLocator(
                        ResourceLocatorTool.TYPE_TEXTURE,
                        new RelativeResourceLocator(matURL));
                  // This causes relative references in the .material file to
                  // resolve to the same dir as the material file.
                  // Don't have to set up a relative locator for TYPE_MODEL
                  // here, because OgreLoader.loadModel() takes care of that.
            } catch (URISyntaxException use) {
                // Since we're generating the URI from a URL we know to be
                // good, we won't get here.  This is just to satisfy the
                // compiler.
                throw new RuntimeException(use);
            }

            matLoader.load(matURL.openStream());
            if (matLoader.getMaterials().size() > 0)
                loader.setMaterials(matLoader.getMaterials());

            model = loader.loadModel(meshURL);
            rootNode.attachChild(model);
        } catch (ModelFormatException mfe) {
            logger.log(Level.SEVERE, null, mfe);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    }

    protected void setupTurretControl(){
        MeshAnimationController animControl =
            (MeshAnimationController) model.getController(0);

        // must set some animation otherwise user control is ignored
        animControl.setAnimation("Rotate");
        animControl.setSpeed(0.25f);

        turretBone = animControl.getBone("Turret");
        turretBone.setUserControl(true);
    }

    @Override
    protected void simpleInitGame() {
        loadMeshModel();
        setupTurretControl();

        // disable 3rd person camera
        input.setEnabled(false);

        cam.setLocation(new Vector3f(5f, 5f, -6f));
        cam.lookAt(model.getWorldBound().getCenter(), Vector3f.UNIT_Y);

        Text t = Text.createDefaultTextLabel("Text",
                "Use left and right arrow keys to rotate turret");
        statNode.attachChild(t);
    }


}
