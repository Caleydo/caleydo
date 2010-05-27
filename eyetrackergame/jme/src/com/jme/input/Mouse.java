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

/*
 * EDIT:  02/08/2004 - Added update(boolean updateState) to allow for a
 *                      WidgetViewport to update an AbstractInputHandler
 *                      without polling the mouse.  GOP
 */

package com.jme.input;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;

/**
 * <code>Mouse</code> defines a node that handles the rendering and updating
 * of a mouse input device. If a cursor is set, this cursor is displayed in the
 * position defined by the device.
 * 
 * @author Mark Powell
 * @author Gregg Patton
 * @version $Id: Mouse.java 4666 2009-09-04 17:22:52Z skye.book $
 */
public abstract class Mouse extends Quad {

	private static final long serialVersionUID = -2865530220854857049L;

	/**
     * the cursor's texture.
     */
    protected boolean hasCursor = false;

    /**
     * Width of this mouse's texture.
     */
    protected int imageWidth;

    /**
     * Height of this mouse's texture.
     * 
     */
    protected int imageHeight;

    /** This mouse's actual location after hotspot offset is taken into account. */
    protected Vector3f hotSpotLocation = new Vector3f();

    /**
     * This mouse's hotspot location. The location on the texture where the
     * mouse is actually clicking, relative to the bottom left.
     */
    protected Vector3f hotSpotOffset = new Vector3f();

    /**
     * Constructor creates a new <code>Mouse</code> object.
     * 
     * @param name
     *            the name of the scene element. This is required for
     *            identification and comparison purposes.
     */
    public Mouse(String name) {
        super(name, 32, 32);
        setCullHint(Spatial.CullHint.Never);
        setRenderQueueMode(Renderer.QUEUE_ORTHO);
        setZOrder(Integer.MIN_VALUE);
        setLightCombineMode(Spatial.LightCombineMode.Off);
        setTextureCombineMode(TextureCombineMode.Replace);
        
        updateGeometry(0, 0);
    }

    /**
     * 
     * <code>setRenderState</code> sets a render state for this node. Note,
     * there can only be one render state per type per node. That is, there can
     * only be a single BlendState a single TextureState, etc. If there is
     * already a render state for a type set the old render state will be
     * rendered. Otherwise, null is returned.
     * 
     * @param rs
     *            the render state to add.
     * @return the old render state.
     */
    public RenderState setRenderState(RenderState rs) {
        if (rs.getStateType() == RenderState.StateType.Texture) {
            hasCursor = true;
            imageHeight = ((TextureState) rs).getTexture().getImage()
                    .getHeight();
            imageWidth = ((TextureState) rs).getTexture().getImage().getWidth();
            resize(imageWidth, imageHeight);
            hotSpotOffset = new Vector3f(-imageWidth / 2, imageHeight / 2, 0);
        }
        return super.setRenderState(rs);
    }

    /**
     * 
     * <code>getImageHeight</code> retrieves the height of the cursor image.
     * 
     * @return the height of the cursor image.
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * 
     * <code>getImageWidth</code> retrieves the width of the cursor image.
     * 
     * @return the width of the cursor image.
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * 
     * <code>hasCursor</code> returns true if there is a texture associated
     * with the mouse.
     * 
     * @return true if there is a texture for the mouse, false otherwise.
     */
    public boolean hasCursor() {
        return hasCursor;
    }

    /**
     * Sets the speed multiplier for updating the cursor position
     * 
     * @param speed
     */
    public abstract void setSpeed(float speed);

    /**
     * Returns this mouse's location relative to the hotspot offset. Put
     * simply, where the mouse is on the screen.
     * 
     * @return The mouse's location.
     */
    public Vector3f getHotSpotPosition() {
        return hotSpotLocation;
    }

    /**
     * Returns the currently set hotspot of the mouse. This is the spot relative
     * to the bottom left of the texture where the mouse is actually pointed.
     * 
     * @return The mouse's hotspot offset.
     */
    public Vector3f getHotSpotOffset() {
        return hotSpotOffset;
    }

    /**
     * Sets the mouse's hotspot offset. The hotspot is the spot relative to the
     * bottom left of the texture where the mouse is actually pointed. Note that
     * this is a shallow copy, not a deep copy.<br>
     * Please note that if a TextureState is set for this object, the hotspot offset 
     * is overwritten again.
     * 
     * @param offset
     *            The new hotspot for this mouse.
     */
    public void setHotSpotOffset(Vector3f offset) {
        hotSpotOffset = offset;
    }

    public abstract void registerWithInputHandler( InputHandler inputHandler );
}