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

import java.util.logging.Logger;

import com.jme.app.SimpleGame;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.intersection.TriangleCollisionResults;
import com.jme.math.Vector3f;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class TestSwitchNodeCollision extends SimpleGame {
    private static final Logger logger = Logger
            .getLogger(TestSwitchNodeCollision.class.getName());

    private TriMesh t1;
    private TriMesh t2;
    private TriMesh t3;
    private Text text;
    private Node scene;
    private int t1Collided = 0;
    private int t2Collided = 0;
    private int t3Collided = 0;
    private TriangleCollisionResults results;
    private float step = -40;

    protected void simpleInitGame() {
        results = new TriangleCollisionResults();

        display.setTitle("Collision Detection");
        cam.setLocation(new Vector3f(0.0f, 0.0f, 75.0f));
        cam.update();

        text = Text.createDefaultTextLabel("Text Label", "Collision: No");
        text.setLocalTranslation(new Vector3f(1, 60, 0));
        statNode.attachChild(text);

        scene = new Node("3D Scene Root");

        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);

        t1 = new Box("Box 1", min, max);
        t1.setModelBound(new OrientedBoundingBox());
        t1.updateModelBound();

        t2 = new Box("Box 2", min, max);
        t2.setModelBound(new OrientedBoundingBox());
        t2.updateModelBound();

        DistanceSwitchModel dsm = new DistanceSwitchModel(2);
        DiscreteLodNode dln = new DiscreteLodNode("lod node", dsm);
        dln.setLocalTranslation(new Vector3f(-30, 0, 0));
        dsm.setModelDistance(0, 0, 2000);
        dln.attachChild(t1);
        dsm.setModelDistance(1, 2000, 9000);
        dln.attachChild(t2);
        dln.updateGeometricState(0, true);

        t3 = new Box("Char", min.mult(0.5f), max.mult(0.5f));
        t3.setModelBound(new OrientedBoundingBox());
        t3.updateModelBound();
        t3.setLocalTranslation(new Vector3f(30, 0, 0));

        scene.attachChild(dln);
        scene.attachChild(t3);

        TextureState ts = display.getRenderer().createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(
                TestSwitchNodeCollision.class.getClassLoader().getResource(
                        "jmetest/data/images/Monkey.jpg"), Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));

        scene.setRenderState(ts);
        rootNode.attachChild(scene);
    }

    protected void simpleUpdate() {

        t3.getLocalTranslation().x += step * timer.getTimePerFrame();

        if (t3.getLocalTranslation().x > 40) {
            t3.getLocalTranslation().x = 40;
            step *= -1;
        } else if (t3.getLocalTranslation().x < -40) {
            t3.getLocalTranslation().x = -40;
            step *= -1;
        }

        results.clear();
        t3.findCollisions(scene, results);
        if (results.getNumber() == 0)
            text.print("");
        else {
            for (int i = 0; i < results.getNumber(); i++) {
                if (results.getCollisionData(i).getTargetMesh().equals(t1)
                        || results.getCollisionData(i).getSourceMesh().equals(
                                t1))
                    t1Collided++;
                if (results.getCollisionData(i).getTargetMesh().equals(t2)
                        || results.getCollisionData(i).getSourceMesh().equals(
                                t2))
                    t2Collided++;
                if (results.getCollisionData(i).getTargetMesh().equals(t3)
                        || results.getCollisionData(i).getSourceMesh().equals(
                                t3))
                    t3Collided++;
                if (results.getCollisionData(i).getTargetTris().size() > 0) {
                    text.print("Collision with "
                            + results.getCollisionData(i).getTargetMesh()
                                    .getName());
                } else if (results.getCollisionData(i).getSourceTris().size() > 0) {
                    text.print("Collision with "
                            + results.getCollisionData(i).getSourceMesh()
                                    .getName());
                }
            }
        }
    }

    public void cleanup() {
        logger.info("Collisions: t1=" + t1Collided + ", t2="
                + t2Collided + ", t3=" + t3Collided);
    }

    public static void main(String[] args) {
        TestSwitchNodeCollision app = new TestSwitchNodeCollision();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

}
