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
package jmetest.scene.geometryinstancing;

import java.nio.Buffer;
import java.util.ArrayList;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.NodeHandler;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.CameraNode;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.geometryinstancing.GeometryBatchInstance;
import com.jme.scene.geometryinstancing.GeometryBatchInstanceAttributes;
import com.jme.scene.geometryinstancing.instance.GeometryBatchCreator;
import com.jme.scene.geometryinstancing.instance.GeometryInstance;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

/**
 * <code>TestGeometryInstancing</code> tests the <code>GeometryBatchCreator</code>
 * and the related classes. The <code>GeometryBatchCreator</code> is intended
 * for static meshes, and the code in the <code>simpleUpdate</code> function is
 * just a demo of one way to update the instaces, it should not be done every
 * frame.
 *
 * @author Patrik Lindegrén
 */
public class TestGeometryInstancing extends SimplePassGame {

    /** The batch creator */
    private GeometryBatchCreator geometryBatchCreator;

    /** The mesh containing the created batch */
    private TriMesh mesh;

    /** Number of instances to create */
    private int nInstances = 40;

    /** Update the vertex data every frame */
    private boolean updateBatch = true;

    /** Scale to easy change the size of everything */
    private float scale = 20.0f;

    /** A batch refering to the buffers that should be updated */
    private TriMesh updateBuffers = new TriMesh();

    public static void main(String[] args) {
        TestGeometryInstancing app = new TestGeometryInstancing();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleInitGame() {
        display.setTitle("Test geometry instancing");
        display.getRenderer()
                .setBackgroundColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.0f));

        KeyBindingManager.getKeyBindingManager()
                .set("toggle_update", KeyInput.KEY_U);

        // Setup camera
        cam.setFrustumPerspective(50.0f, (float) display.getWidth() /
                                         (float) display.getHeight(), 0.01f,
                                                                      500f);
        cam.setLocation(new Vector3f(0, 0, 0));
        cam.update();

        // Setup camera node
        CameraNode camNode = new CameraNode("Camera node", cam);
        camNode.setLocalTranslation(new Vector3f(0.0f, 0.0f, 3.0f * scale));
        camNode.lookAt(new Vector3f(0.0f, 0.0f, 0.0f),
                       new Vector3f(0.0f, 1.0f, 0.0f));
        camNode.updateWorldData(0);
        input = new NodeHandler(camNode, 2 * scale, 1);
        rootNode.attachChild(camNode);

        // Add boxes to the scene
        rootNode.attachChild(createBoxes());

        // Add a texture
        TextureState ts = display.getRenderer().createTextureState();
        Texture t0 = TextureManager.loadTexture(
                TestGeometryInstancing.class.getClassLoader().getResource(
                        "jmetest/data/texture/wall.jpg"),
                Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear, 0.0f, false);
        t0.setWrap(Texture.WrapMode.Repeat);
        ts.setTexture(t0, 0);

        // Material
        MaterialState ms = display.getRenderer().createMaterialState();
        ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);

        // Render states
        rootNode.setRenderState(ms);
        rootNode.setRenderState(ts);
        rootNode.updateRenderState();

        // Root render pass
        RenderPass rootPass = new RenderPass();
        rootPass.add(rootNode);
        pManager.add(rootPass);

        // FPS render pass
        RenderPass statPass = new RenderPass();
        statPass.add(statNode);
        pManager.add(statPass);
    }

    /**
     * Use geometry instancing to create a mesh containing a number of box
     * instances
     */
    private TriMesh createBoxes() {
        // A box that will be instantiated
        Box box = new Box("Box", new Vector3f(-0.5f, -0.5f, -0.5f),
                          new Vector3f(0.5f, 0.5f, 0.5f));

        // The batch geometry creator
        geometryBatchCreator = new GeometryBatchCreator();

        // Loop that creates NxN instances
        for (int y = 0; y < nInstances; y++) {
            for (int x = 0; x < nInstances; x++) {
                // Box instance attributes
                GeometryBatchInstanceAttributes attributes =
                        new GeometryBatchInstanceAttributes(
                                new Vector3f(scale * x * 0.13f,
                                             scale * y * 0.13f, 0.0f),
                                // Translation
                                new Vector3f(scale * 0.04f + y * scale * 0.015f,
                                             scale * 0.02f, scale * 0.02f),
                                // Scale
                                new Quaternion().fromAngles(0.0f, (x + y) * -0.1f, 0.0f),
                                // Rotation
                                new ColorRGBA(1.0f - x * (1.0f / nInstances),
                                              y * (1.0f / nInstances), 1.0f,
                                              1.0f));    // Color

                // Box instance (batch and attributes)
                GeometryBatchInstance instance =
                        new GeometryBatchInstance(box, attributes);

                // Add the instance
                geometryBatchCreator.addInstance(instance);
            }
        }

        // Create a TriMesh
        mesh = new TriMesh();
        mesh.setModelBound(new BoundingBox());

        // Create the batch's buffers
        mesh.setIndexBuffer(BufferUtils.createIntBuffer(
                geometryBatchCreator.getNumIndices()));
        mesh.setVertexBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        mesh.setNormalBuffer(BufferUtils.createVector3Buffer(
                geometryBatchCreator.getNumVertices()));
        mesh.setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(
                geometryBatchCreator.getNumVertices())), 0);
        mesh.setColorBuffer(BufferUtils.createFloatBuffer(
                geometryBatchCreator.getNumVertices() * 4));

        // Commit the instances to the mesh batch
        geometryBatchCreator.commit(mesh);

        // Return the mesh
        return mesh;
    }

    // Process keyboard input
    void processKeys() {
        if (KeyBindingManager.getKeyBindingManager()
                .isValidCommand("toggle_update", false)) {
            updateBatch = !updateBatch;
        }
    }

    /** Update the batch buffers */
    protected void simpleUpdate() {
        processKeys();
        if (updateBatch) {
            float time = timer.getTimeInSeconds();

            // Rewind the buffers in the mesh
            rewindBatchBuffers(mesh);

            for (GeometryInstance instance : geometryBatchCreator
                    .getInstances()) {
                Vector3f translation =
                        instance.getAttributes().getTranslation();
                Quaternion rotation = instance.getAttributes().getRotation();
                Vector3f scalev = instance.getAttributes().getScale();

                // Update scale and rotation
                float f = translation.y / scale + translation.x / scale + time;
                rotation.fromAngles(f * 5f, f * 1f, f * 2f);
                f = (f - (float) ((int) f)) * 2.0f;
                if (f > 1.0f) {
                    f = 2.0f - f;
                }
                scalev.set(0.1f * scale + f * 0.2f * scale,
                           0.02f * scale + f * 0.05f * scale,
                           0.02f * scale + f * 0.05f * scale);

                // GeometryBatchInstance specific update
                if (instance instanceof GeometryBatchInstance) {
                    ColorRGBA color = ((GeometryBatchInstance) instance)
                            .getAttributes().getColor();
                    color.r = f;
                }
                instance.getAttributes().buildMatrices();
            }

            // Set the buffers that should be updated
            updateBuffers.setVertexBuffer(mesh.getVertexBuffer());
            updateBuffers.setColorBuffer(mesh.getColorBuffer());
            updateBuffers.setNormalBuffer(mesh.getNormalBuffer());
            updateBuffers.setTextureCoords(mesh.getTextureCoords(0), 0);

            // Commit the instances to the mesh batch
            geometryBatchCreator.commit(updateBuffers);
            mesh.updateModelBound();
        }
    }

    /**
     * Rewind a Buffer if it exists Could a function like this be a part of the
     * batch?
     */
    private void rewindBuffer(Buffer buf) {
        if (buf != null) {
            buf.rewind();
        }
    }

    /**
     * Rewind a Buffer if it exists Could a function like this be a part of the
     * batch?
     */
    private void rewindBuffer(TexCoords tc) {
        if (tc != null && tc.coords != null) {
            tc.coords.rewind();
        }
    }

    /**
     * Rewind all buffers in a batch Could a function like this be a part of the
     * batch?
     */
    public void rewindBatchBuffers(TriMesh batch) {
        rewindBuffer(batch.getIndexBuffer());
        rewindBuffer(batch.getVertexBuffer());
        rewindBuffer(batch.getColorBuffer());
        rewindBuffer(batch.getNormalBuffer());
        ArrayList<TexCoords> textureBuffers = batch.getTextureCoords();
        for (TexCoords textureBuffer : textureBuffers) {
            rewindBuffer(textureBuffer);
		}
	}
}
