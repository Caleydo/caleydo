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

package com.jme.renderer.lwjgl;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.renderer.AbstractCamera;
import com.jme.scene.state.lwjgl.records.RendererRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>LWJGLCamera</code> defines a concrete implementation of a
 * <code>AbstractCamera</code> using the LWJGL library for view port setting.
 * Most functionality is provided by the <code>AbstractCamera</code> class with
 * this class handling the OpenGL specific calls to set the frustum and
 * viewport.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 */
public class LWJGLCamera extends AbstractCamera {

    private static final long serialVersionUID = 1L;
    private static final FloatBuffer tmp_FloatBuffer = BufferUtils.createFloatBuffer(16);

    public LWJGLCamera() {}

    /**
     * Constructor instantiates a new <code>LWJGLCamera</code> object. The
     * width and height are provided, which corresponds to either the
     * width and height of the rendering window, or the resolution of the
     * fullscreen display.
     * @param width the width/resolution of the display.
     * @param height the height/resolution of the display.
     */
    public LWJGLCamera(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        update();
    }
    
    /**
     * Constructor instantiates a new <code>LWJGLCamera</code> object. The
     * width and height are provided, which corresponds to either the
     * width and height of the rendering window, or the resolution of the
     * fullscreen display.
     * @param width the width/resolution of the display.
     * @param height the height/resolution of the display.
     */
    public LWJGLCamera(int width, int height, boolean dataOnly) {
        super(dataOnly);
        this.width = width;
        this.height = height;
        setDataOnly(dataOnly);
        update();
    }

    /**
     * @return the width/resolution of the display.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return the height/resolution of the display.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Resizes this camera's view with the given width and height. This is
     * similar to constructing a new camera, but reusing the same Object. This
     * method is called by an associated renderer to notify the camera of
     * changes in the display dimensions.
     * 
     * @param width
     *            the view width
     * @param height
     *            the view height
     */
    public void resize(final int width, final int height) {
        this.width = width;
        this.height = height;
        onViewPortChange();
    }
    
    /**
     * Resizes this camera's view with the given width and height. This is
     * similar to constructing a new camera, but reusing the same Object. This
     * method is called by an associated renderer to notify the camera of
     * changes in the display dimensions. A renderer can use the forceDirty
     * parameter for a newly associated camera to ensure that the settings for a
     * previously used camera will be part of the next rendering phase.
     * 
     * @param width
     *            the view width
     * @param height
     *            the view height
     * @param forceDirty
     *            <code>true</code> if camera settings should be treated as
     *            changed
     */
    void resize(final int width, final int height, final boolean forceDirty) {
        // Only override dirty flags when forceDirty is true.
        if (forceDirty) {
            frustumDirty = true;
            viewPortDirty = true;
            frameDirty = true;
        }

        resize(width, height);
    }

    private boolean frustumDirty;
    private boolean viewPortDirty;
    private boolean frameDirty;

    public void apply() {
        if ( frustumDirty ) {
            doFrustumChange();
            frustumDirty = false;
        }
        if ( viewPortDirty ) {
            doViewPortChange();
            viewPortDirty = false;
        }
        if ( frameDirty ) {
            doFrameChange();
            frameDirty = false;
        }
    }

    @Override
    public void onFrustumChange() {
        super.onFrustumChange();
        frustumDirty = true;
    }

    public void onViewPortChange() {
        viewPortDirty = true;
    }

    @Override
    public void onFrameChange() {
        super.onFrameChange();
        frameDirty = true;
    }

    /**
     * Sets the OpenGL frustum.
     * @see com.jme.renderer.Camera#onFrustumChange()
     */
    protected void doFrustumChange() {

        if (!isDataOnly()) {
            // set projection matrix
            RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL11.GL_PROJECTION);

            tmp_FloatBuffer.rewind();
            getProjectionMatrix().fillFloatBuffer(tmp_FloatBuffer);
            tmp_FloatBuffer.rewind();
            GL11.glLoadMatrix(tmp_FloatBuffer);
        }

    }

    /**
     * Sets OpenGL's viewport.
     * @see com.jme.renderer.Camera#onViewPortChange()
     */
    protected void doViewPortChange() {

        if (!isDataOnly()) {
            // set view port
            int x = (int) (viewPortLeft * width);
            int y = (int) (viewPortBottom * height);
            int w = (int) ((viewPortRight - viewPortLeft) * width);
            int h = (int) ((viewPortTop - viewPortBottom) * height);
            GL11.glViewport(x, y, w, h);
        }
    }

    /**
     * Uses GLU's lookat function to set the OpenGL frame.
     * @see com.jme.renderer.Camera#onFrameChange()
     */
    protected void doFrameChange() {

        if (!isDataOnly()) {
            // set view matrix
            RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
            matRecord.switchMode(GL11.GL_MODELVIEW);

            tmp_FloatBuffer.rewind();
            getModelViewMatrix().fillFloatBuffer(tmp_FloatBuffer);
            tmp_FloatBuffer.rewind();
            GL11.glLoadMatrix(tmp_FloatBuffer);
        }
    }
}
