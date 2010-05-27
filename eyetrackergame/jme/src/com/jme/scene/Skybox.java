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

package com.jme.scene;

import java.io.IOException;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * A Box made of textured quads that simulate having a sky, horizon and so forth
 * around your scene. Either attach to a camera node or update on each frame to
 * set this skybox at the camera's position.
 * 
 * @author David Bitkowski
 * @author Jack Lindamood (javadoc only)
 * @version $Id: Skybox.java 4636 2009-08-28 14:52:25Z skye.book $
 */
public class Skybox extends Node {
    private static final long serialVersionUID = 1L;

    public enum Face {
        /** The +Z side of the skybox. */
        North,
        /** The -Z side of the skybox. */
        South,
        /** The -X side of the skybox. */
        East,
        /** The +X side of the skybox. */
        West,
        /** The +Y side of the skybox. */
        Up,
        /** The -Y side of the skybox. */
        Down;
    }

    private float xExtent;
    private float yExtent;
    private float zExtent;

    private Quad[] skyboxQuads;

    public Skybox() {}
    
    /**
     * Creates a new skybox. The size of the skybox and name is specified here.
     * By default, no textures are set.
     * 
     * @param name
     *            The name of the skybox.
     * @param xExtent
     *            The x size of the skybox in both directions from the center.
     * @param yExtent
     *            The y size of the skybox in both directions from the center.
     * @param zExtent
     *            The z size of the skybox in both directions from the center.
     */
    public Skybox(String name, float xExtent, float yExtent, float zExtent) {
        super(name);

        this.xExtent = xExtent;
        this.yExtent = yExtent;
        this.zExtent = zExtent;

        initialize();
    }

    /**
     * Set the texture to be displayed on the given face of the skybox. Replaces
     * any existing texture on that face.
     * 
     * @param face
     *            the face to set
     * @param texture
     *            The texture for that side to assume.
     * @throws IllegalArgumentException
     *             if face is null.
     */
    public void setTexture(Face face, Texture texture) {
        if (face == null) {
            throw new IllegalArgumentException("Face can not be null.");
        }

        skyboxQuads[face.ordinal()].clearRenderState(RenderState.StateType.Texture);
        setTexture(face, texture, 0);
    }

    /**
     * Set the texture to be displayed on the given side of the skybox. Only
     * replaces the texture at the index specified by textureUnit.
     * 
     * @param face
     *            the face to set
     * @param texture
     *            The texture for that side to assume.
     * @param textureUnit
     *            The texture unite of the given side's TextureState the texture
     *            will assume.
     */
    public void setTexture(Face face, Texture texture, int textureUnit) {
        // Validate
        if (face == null) {
            throw new IllegalArgumentException("Face can not be null.");
        }

        TextureState ts = (TextureState) skyboxQuads[face.ordinal()].getRenderState(RenderState.StateType.Texture);
        if (ts == null) {
            ts = DisplaySystem.getDisplaySystem().getRenderer()
                    .createTextureState();
        }

        // Initialize the texture state
        ts.setTexture(texture, textureUnit);
        ts.setEnabled(true);

        // Set the texture to the quad
        skyboxQuads[face.ordinal()].setRenderState(ts);

        return;
    }
    
    public Texture getTexture(Face face) {
        if (face == null) {
            throw new IllegalArgumentException("Face can not be null.");
        }
        return ((TextureState)skyboxQuads[face.ordinal()].getRenderState(RenderState.StateType.Texture)).getTexture();
    }

    private void initialize() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();

        // Skybox consists of 6 sides
        skyboxQuads = new Quad[6];

        // Create each of the quads
        skyboxQuads[Face.North.ordinal()] = new Quad("north", xExtent * 2, yExtent * 2);
        skyboxQuads[Face.North.ordinal()].setLocalRotation(new Quaternion(new float[] { 0,
                (float) Math.toRadians(180), 0 }));
        skyboxQuads[Face.North.ordinal()].setLocalTranslation(new Vector3f(0, 0, zExtent));
        skyboxQuads[Face.South.ordinal()] = new Quad("south", xExtent * 2, yExtent * 2);
        skyboxQuads[Face.South.ordinal()].setLocalTranslation(new Vector3f(0, 0, -zExtent));
        skyboxQuads[Face.East.ordinal()] = new Quad("east", zExtent * 2, yExtent * 2);
        skyboxQuads[Face.East.ordinal()].setLocalRotation(new Quaternion(new float[] { 0,
                (float) Math.toRadians(90), 0 }));
        skyboxQuads[Face.East.ordinal()].setLocalTranslation(new Vector3f(-xExtent, 0, 0));
        skyboxQuads[Face.West.ordinal()] = new Quad("west", zExtent * 2, yExtent * 2);
        skyboxQuads[Face.West.ordinal()].setLocalRotation(new Quaternion(new float[] { 0,
                (float) Math.toRadians(270), 0 }));
        skyboxQuads[Face.West.ordinal()].setLocalTranslation(new Vector3f(xExtent, 0, 0));
        skyboxQuads[Face.Up.ordinal()] = new Quad("up", xExtent * 2, zExtent * 2);
        skyboxQuads[Face.Up.ordinal()].setLocalRotation(new Quaternion(new float[] {
                (float) Math.toRadians(90), (float) Math.toRadians(270), 0 }));
        skyboxQuads[Face.Up.ordinal()].setLocalTranslation(new Vector3f(0, yExtent, 0));
        skyboxQuads[Face.Down.ordinal()] = new Quad("down", xExtent * 2, zExtent * 2);
        skyboxQuads[Face.Down.ordinal()].setLocalRotation(new Quaternion(new float[] {
                (float) Math.toRadians(270), (float) Math.toRadians(270), 0 }));
        skyboxQuads[Face.Down.ordinal()].setLocalTranslation(new Vector3f(0, -yExtent, 0));

        // We don't want the light to effect our skybox
        setLightCombineMode(Spatial.LightCombineMode.Off);
        
        setTextureCombineMode(TextureCombineMode.Replace);

        ZBufferState zbuff = display.getRenderer().createZBufferState();
        zbuff.setWritable(false);
        zbuff.setEnabled(true);
        zbuff.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        setRenderState(zbuff);

        // We don't want it making our skybox disappear, so force view
        setCullHint(Spatial.CullHint.Never);

        for (int i = 0; i < 6; i++) {
            // Make sure texture is only what is set.
            skyboxQuads[i].setTextureCombineMode(TextureCombineMode.Replace);

            // Make sure no lighting on the skybox
            skyboxQuads[i].setLightCombineMode(Spatial.LightCombineMode.Off);

            // Make sure the quad is viewable
            skyboxQuads[i].setCullHint(Spatial.CullHint.Never);

            // Set a bounding volume
            skyboxQuads[i].setModelBound(new BoundingBox());
            skyboxQuads[i].updateModelBound();

            skyboxQuads[i].setRenderQueueMode(Renderer.QUEUE_SKIP);
            skyboxQuads[i].setVBOInfo(null);

            // And attach the skybox as a child
            attachChild(skyboxQuads[i]);
        }
    }

    /**
     * Retrieve the quad indicated by the given side.
     * 
     * @param face
     *            One of Skybox.Face.North, Skybox.Face.South, and so on...
     * @return The Quad that makes up that side of the Skybox.
     */
    public Quad getFace(Face face) {
        return skyboxQuads[face.ordinal()];
    }
    
    public void preloadTexture(Face face) {
    	TextureState ts = (TextureState) skyboxQuads[face.ordinal()].getRenderState(RenderState.StateType.Texture);
    	if(ts != null) {
    		ts.apply();
    	}
    }

    /**
     * Force all of the textures to load. This prevents pauses later during the
     * application as you pan around the world.
     */
    public void preloadTextures() {
        for (int x = 0; x < 6; x++) {
            TextureState ts = (TextureState) skyboxQuads[x].getRenderState(RenderState.StateType.Texture);
            if (ts != null)
                ts.apply();
        }

    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(xExtent, "xExtent", 0);
        capsule.write(yExtent, "yExtent", 0);
        capsule.write(zExtent, "zExtent", 0);
        capsule.write(skyboxQuads, "skyboxQuads", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        xExtent = capsule.readFloat("xExtent", 0);
        yExtent = capsule.readFloat("yExtent", 0);
        zExtent = capsule.readFloat("zExtent", 0);
        Savable[] savs = capsule.readSavableArray("skyboxQuads", null);
        if (savs == null) {
            skyboxQuads = null;
            initialize();
        } else {
            skyboxQuads = new Quad[savs.length];
            for (int x = 0; x < savs.length; x++) {
                skyboxQuads[x] = (Quad)savs[x];
            }
        }
        
    }
}