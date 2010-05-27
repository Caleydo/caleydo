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

import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.app.SimpleGame;
import com.jme.scene.Spatial;
import com.jme.util.resource.RelativeResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.ogrexml.MaterialLoader;
import com.jmex.model.ogrexml.OgreLoader;
import com.jmex.model.ogrexml.anim.MeshAnimationController;
import java.net.URISyntaxException;
import java.net.URL;

public class TestWeightedBones extends SimpleGame {

    private Spatial model;

    public static void main(String[] args){
        TestWeightedBones app = new TestWeightedBones();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void loadMeshModel(){
        OgreLoader loader = new OgreLoader();
        MaterialLoader matLoader = new MaterialLoader();

        String matUrlString = "/jmetest/data/model/ogrexml/Sphere.material";
        String turretMeshUrlString =
                "/jmetest/data/model/ogrexml/Sphere.mesh.xml";

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
            } catch (URISyntaxException use) {
                throw new RuntimeException(use);
            }

            matLoader.load(matURL.openStream());
            loader.setMaterials(matLoader.getMaterials());

            model = loader.loadModel(meshURL);
            rootNode.attachChild(model);
        } catch (Exception ex){
            ex.printStackTrace();
        }

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    }

    @Override
    protected void simpleInitGame() {
        loadMeshModel();
        // make it larger
        model.setLocalScale(10);
        
        MeshAnimationController animControl =
            (MeshAnimationController) model.getController(0);
        animControl.setAnimation("action");
    }


}
