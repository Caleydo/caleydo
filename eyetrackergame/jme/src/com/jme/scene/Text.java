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
import java.net.URL;
import java.util.Stack;
import java.util.logging.Logger;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.intersection.CollisionResults;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * 
 * <code>Text</code> allows text to be displayed on the screen. The
 * renderstate of this Geometry must be a valid font texture.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 */
public class Text extends Geometry {
    private static final Logger logger = Logger.getLogger(Text.class.getName());

    private static final long serialVersionUID = 1L;

    private StringBuffer text;

    private ColorRGBA textColor = new ColorRGBA();

    public Text() {}
    
    /**
     * Creates a texture object that starts with the given text.
     * 
     * @see com.jme.util.TextureManager
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     * @param text
     *            The text to show.
     */
    public Text(String name, String text) {
        super(name);
        setCullHint(Spatial.CullHint.Never);
        this.text = new StringBuffer(text);
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
    }

    /**
     * 
     * <code>print</code> sets the text to be rendered on the next render
     * pass.
     * 
     * @param text
     *            the text to display.
     */
    public void print(String text) {
        this.text.replace(0, this.text.length(), text);
    }

    /**
     * Sets the text to be rendered on the next render. This function is a more
     * efficient version of print(String).
     * 
     * @param text
     *            The text to display.
     */
    public void print(StringBuffer text) {
        this.text.setLength(0);
        this.text.append(text);
    }

    /**
     * 
     * <code>getText</code> retrieves the text string of this
     * <code>Text</code> object.
     * 
     * @return the text string of this object.
     */
    public StringBuffer getText() {
        return text;
    }

    /**
     * <code>draw</code> calls super to set the render state then calls the
     * renderer to display the text string.
     * 
     * @param r
     *            the renderer used to display the text.
     */
    public void draw(Renderer r) {
        if (!r.isProcessingQueue()) {
            if (r.checkAndAdd(this)) return;
        }
        super.draw(r);
        r.draw(this);
    }

    /**
     * Sets the color of the text.
     * 
     * @param color
     *            Color to set.
     */
    public void setTextColor(ColorRGBA color) {
    	textColor = color;
    }

    /**
     * Returns the current text color.
     * 
     * @return Current text color.
     */
    public ColorRGBA getTextColor() {
        return textColor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.scene.Spatial#hasCollision(com.jme.scene.Spatial,
     *      com.jme.intersection.CollisionResults)
     */
    public void findCollisions(
            Spatial scene, CollisionResults results, int requiredOnBits) {
        //Do nothing.
    }

    public boolean hasCollision(
            Spatial scene, boolean checkTriangles, int requiredOnBits) {
        return false;
    }

    public float getWidth() {
        float rVal = 10f * text.length() * worldScale.x;
        return rVal;
    }

    public float getHeight() {
        float rVal = 16f * worldScale.y;
        return rVal;
    }

    /**
     * @return a Text with {@link #DEFAULT_FONT} and correct blend state
     * @param name name of the spatial
     */
    public static Text createDefaultTextLabel( String name ) {
        return createDefaultTextLabel( name, "" );
    }

    /**
     * @return a Text with {@link #DEFAULT_FONT} and correct blend state
     * @param name name of the spatial
     */
    public static Text createDefaultTextLabel( String name, String initialText ) {
        Text text = new Text( name, initialText );
        text.setCullHint( Spatial.CullHint.Never );
        text.setRenderState( getDefaultFontTextureState() );
        text.setRenderState( getFontBlend() );
        return text;
    }

    /*
    * @return an blend state for doing alpha transparency
    */
    public static BlendState getFontBlend() {
        BlendState as1 = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        as1.setBlendEnabled( true );
        as1.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
        as1.setDestinationFunction( BlendState.DestinationFunction.OneMinusSourceAlpha );
        return as1;
    }

    /**
     * texture state for the default font.
     */
    private static TextureState defaultFontTextureState;

    public static void resetFontTexture() {
        if (defaultFontTextureState != null)
            defaultFontTextureState.deleteAll(true);
    }
    
    /**
     * A default font contained in the jME library.
     */
    public static final String DEFAULT_FONT = "com/jme/app/defaultfont.tga";
    
    protected void applyRenderState(Stack<? extends RenderState>[] states) {
        for (int x = 0; x < states.length; x++) {
            if (states[x].size() > 0) {
                this.states[x] = ((RenderState) states[x].peek()).extract(
                        states[x], this);
            } else {
                this.states[x] = Renderer.defaultStateList[x];
            }
        }
    }

    /**
     * Creates the texture state if not created before.
     * @return texture state for the default font
     */
    public static TextureState getDefaultFontTextureState() {
        if ( defaultFontTextureState == null ) {
            defaultFontTextureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
           final URL defaultUrl = Text.class.getClassLoader().getResource(DEFAULT_FONT);
           if ( defaultUrl == null )
           {
              logger.warning("Default font not found: " + DEFAULT_FONT);
           }
           defaultFontTextureState.setTexture( TextureManager.loadTexture(defaultUrl, Texture.MinificationFilter.Trilinear,
                    Texture.MagnificationFilter.Bilinear, Image.Format.GuessNoCompression, 1.0f, true ) );
            defaultFontTextureState.setEnabled( true );
        }
        return defaultFontTextureState;
    }

    /**
     * Cleans up the default font texture and state for the Text class.
     */
    public static void resetDefaultFontTextureState() {
        if (defaultFontTextureState != null) {
            try {
                defaultFontTextureState.deleteAll(true);
            } catch (Exception e) {
                logger.warning("Unable to clean up existing font texture.  May have already been cleared.");
            }
        }
        defaultFontTextureState = null;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(text.toString(), "textString", "");
        capsule.write(textColor, "textColor", new ColorRGBA());
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        text = new StringBuffer(capsule.readString("textString", ""));
        textColor = (ColorRGBA)capsule.readSavable("textColor", new ColorRGBA());
        
    }
}
