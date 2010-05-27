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

package com.jmex.awt.swingui;

import java.awt.Graphics2D;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.system.DisplaySystem;
import com.jme.system.jogl.JOGLDisplaySystem;
import com.jme.system.lwjgl.LWJGLDisplaySystem;

/**
 * This abstract class provides methods to paint on a {@link com.jme.image.Image} via the awt {@link Graphics2D}.
 */
public abstract class ImageGraphics extends Graphics2D {
	
	/**
	 * if true, dirty region grow by 2x2 to prevent antialiasing problem
	 */
	private boolean expandDirtyRegion = true;

    /**
     * @param width of the image
     * @param height of the image
     * @param paintedMipMapCount number of mipmaps that are painted, rest is drawn by image copying, 0 for no mipmaps,
     *                           1 for a single image painted and mipmaps copied, higher values respective
     * @return a new instance of ImageGraphics matching the display system.
     */
    public static ImageGraphics createInstance( int width, int height, int paintedMipMapCount ) {
        //this is a workaround for a proper factory method in DisplaySystem to avoid an awt dependency
        //todo: maybe this can be done more cleanly
        if ( DisplaySystem.getDisplaySystem() instanceof LWJGLDisplaySystem ) {
            return new LWJGLImageGraphics( width, height, paintedMipMapCount );
        } else if ( DisplaySystem.getDisplaySystem() instanceof JOGLDisplaySystem ) {
            return new JOGLImageGraphics( width, height, paintedMipMapCount );
        }

        throw new UnsupportedOperationException( "No ImageGraphics implementation " +
                "for display system '" + DisplaySystem.getDisplaySystem() + "' found!" );        
    }

    /**
     * where painting in {@link #update()} goes to.
     */
    protected final com.jme.image.Image image;

    /**
     * Protected ctor for subclasses.
     *
     * @param image where painting in {@link #update()} goes to.
     */
    protected ImageGraphics( Image image ) {
        this.image = image;
    }

    /**
     * @return image where painting in {@link #update()} goes to
     * @see #update()
     */
    public com.jme.image.Image getImage() {
        return image;
    }

    /**
     * Update a texture that contains the image from {@link #getImage()}. Only dirty areas are updated. The texture must
     * have mipmapping turned off ({@link Texture#MM_NONE}). The whole area is cleaned (dirty markers removed).
     *
     * @param texture texture to be updated
     */
    public void update( Texture texture ) {
        update( texture, true );
    }

    /**
     * Update a texture that contains the image from {@link #getImage()}. Only dirty areas are updated. The texture must
     * have mipmapping turned off ({@link Texture#MM_NONE}).
     *
     * @param texture texture to be updated
     * @param clean   true to mark whole area as clean after updating, false to keep dirty area for updating more textures
     */
    public abstract void update( Texture texture, boolean clean );

    /**
     * Updates the image data.
     *
     * @see #getImage()
     */
    public abstract void update();

    /**
     * @return true if image/texture needs update
     */
    public abstract boolean isDirty();
    
    
    /**
     * @param expand if true, dirty region grow by 2x2 to prevent antialiasing problem
     */
    public void setExpandDirtyRegion(boolean expand) {
    	expandDirtyRegion = expand;
    }
    
    /**
     * @return if true, dirty region grow by 2x2 to prevent antialiasing problem
     */
    public boolean isExpandDirtyRegion() {
    	return expandDirtyRegion;
    }
}
