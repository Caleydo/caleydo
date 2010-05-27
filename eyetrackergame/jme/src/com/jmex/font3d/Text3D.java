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
package com.jmex.font3d;

import java.util.logging.Logger;

import com.jme.bounding.OrientedBoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.SharedMesh;
import com.jme.scene.SharedNode;
import com.jme.scene.TriMesh;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

/**
 * This class represents a peace of text compiled using the
 * {@link Font3D#createText(String, String, int, boolean, boolean, boolean)}.
 * 
 * @author emanuel
 */
public class Text3D extends SharedNode implements JmeText {
    private static final Logger logger = Logger.getLogger(Text3D.class
            .getName());

    private static final long serialVersionUID = 7715674618025080804L;
    private Font3D factory;
    private float height, width;
    private float size;
    private StringBuffer text = new StringBuffer();
    private ColorRGBA fontcolor = new ColorRGBA(0.9f, 0.9f, 0.9f, 1.0f);
    private float localscale;
    // TriMesh render_mesh;
    OrientedBoundingBox render_mesh_bounds;

    public Text3D(Font3D factory, String text, float size) {
        super("Some3DText", factory.getRenderNode());
        // Save for later
        this.factory = factory;

        // Setup a render-container
        // render_mesh = new TriMesh("RenderMesh");
        render_mesh_bounds = new OrientedBoundingBox();
        // attachChild(render_mesh);

        // And now scale to the correct "size" (all font are size 1)
        setSize(size);

        // Ready the glyphs
        setText(text);

        updateRenderState();
    }

    @Override
    public void updateWorldBound() {
        worldBound = render_mesh_bounds.transform(getWorldRotation(),
                getWorldTranslation(), getWorldScale(), worldBound);
    }

    public TextFactory getFactory() {
        return factory;
    }

    public int getFlags() {
        return 0; // TODO: this should be working
    }

    public float getSize() {
        return size;
    }

    public StringBuffer getText() {
        return text;
    }

    public void setSize(float size) {
        this.size = size;
        localscale = size / factory.getFont().getSize();
        // render_mesh.setLocalScale(localscale);
        setLocalScale(localscale);
    }

    public void setText(String text) {
        // Set width and text to zip
        this.width = 0;
        this.height = 0;
        this.text.setLength(0);
        detachAllChildren();

        // Now append the text
        appendText(text);
    }

    public void appendText(String moretext) {
        // Create the damn things
        for (char c : moretext.toCharArray()) {
            Glyph3D glyph = factory.getGlyph(c);
            width += glyph.getBounds().getWidth() * localscale;
            height = (float) Math.max(height, glyph.getBounds().getHeight()
                    * localscale);

            if (glyph.getChildIndex() != -1) {
                SharedMesh mesh = new SharedMesh(""+c, (TriMesh)factory
                        .getRenderNode().getChild(glyph.getChildIndex()));
                attachChild(mesh);
            }
        }
        this.text.append(moretext);
        updateModelBound();
    }

    @Override
    public void updateModelBound() {
        render_mesh_bounds.extent.x = (width / 2) / localscale;
        render_mesh_bounds.extent.y = (height / 2) / localscale;
        render_mesh_bounds.extent.z = (localscale / 2) / localscale;
        render_mesh_bounds.getCenter().x = (width / 2) / localscale;
        render_mesh_bounds.getCenter().y = (height / 2) / localscale;
    }

    private static Vector3f _direction1 = new Vector3f();
    private static Vector3f _direction2 = new Vector3f();
    private static Vector3f _direction3 = new Vector3f();

    @Override
    public void draw(Renderer r) {
        // Calculate the translation in world-space
        getWorldRotation().mult(Vector3f.UNIT_X, _direction1).multLocal(
                getWorldScale());

        // attachChild(factory.getRenderTriMesh());
        // factory.getRenderTriMesh().setDefaultColor(fontcolor);
        // factory.getRenderTriMesh().updateRenderState();
        // factory.getRenderTriMesh().getWorldRotation().set(getWorldRotation());
        // factory.getRenderTriMesh().getWorldTranslation().set(getWorldTranslation());
        // factory.getRenderTriMesh().getWorldScale().set(getWorldScale());

        SharedMesh mesh;
        _direction2.set(getWorldTranslation());
        int childCounter = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Get glyph and set translation
            Glyph3D glyph = factory.getGlyph(c);

            if (glyph.getChildIndex() != -1) {
                mesh = (SharedMesh) getChild(childCounter++);
                mesh.getWorldTranslation().set(_direction2);
                mesh.onDraw(r);
            }
            float trans = (float) (glyph.getBounds().getWidth());

            // getWorldTranslation().addLocal(_direction1.mult(trans,
            // _direction3));
            // factory.getRenderTriMesh().getWorldTranslation().addLocal(_direction1.mult(trans,
            // _direction3));
            _direction2.addLocal(_direction1.mult(trans, _direction3));
        }
        // updateWorldVectors(); // Clean up
    }

    public ColorRGBA getFontColor() {
        return fontcolor;
    }

    public void setFontColor(ColorRGBA fontcolor) {
        this.fontcolor.set(fontcolor);
        if (!factory.has_diffuse_material) {
            MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer()
                    .createMaterialState();
            ms.setDiffuse(fontcolor);
            ms.setEnabled(true);
            setRenderState(ms);
        } else {
            logger.warning("You cannot set the font-color on "
                    + "Text3D when the Font3D has a font color already.");
        }
    }

    public float getWidth() {
        return width;
    }

    /**
     * Just a hack that positions the text with its center in the origo (only on
     * the X-axis).
     */
    public void alignCenter() {
        getLocalTranslation().x = -width / 2;
        updateWorldVectors();
    }

}
