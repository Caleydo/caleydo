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

package com.jme.system.dummy;

import java.util.HashMap;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.RenderContext;
import com.jme.renderer.Renderer;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.TextureRenderer.Target;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;

/**
 * Started Date: Jul 2, 2004 <br>
 * <br>
 * <p/> This class makes up a shell display system with no functionality. It is
 * here to allow the easy creation of renderer-agnostic jME objects (like
 * various RenderState and Spatial) so that they can be used by conversion
 * utilities to read/write jME. It is <b>NOT </b> to be used for rendering as it
 * won't do anything at all.
 * 
 * @author Jack Lindamood
 * @author Joshua Slack - maintenance, etc.
 */
public class DummyDisplaySystem extends DisplaySystem {

    private DummyRenderer renderer;

    public DummyDisplaySystem() {
        created = true;
        renderer = new DummyRenderer();
    }

    @Override
    public boolean isValidDisplayMode(int width, int height, int bpp, int freq) {
        return false;
    }

    @Override
    public void setIcon(com.jme.image.Image[] iconImages) {
    }

    @Override
    public void setVSyncEnabled(boolean enabled) {
    }

    @Override
    public void setTitle(String title) {
    }

    @Override
    public void createWindow(int w, int h, int bpp, int frq, boolean fs) {
    }

    @Override
    public void createHeadlessWindow(int w, int h, int bpp) {
    }

    @Override
    public void recreateWindow(int w, int h, int bpp, int frq, boolean fs) {
    }

    @Override
    public DummyRenderer getRenderer() {
        return renderer;
    }

    @Override
    public boolean isClosing() {
        return false;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void reset() {
    }

    @Override
    public void close() {
    }

    @Override
    public Vector3f getScreenCoordinates(Vector3f worldPosition, Vector3f store) {
        return null;
    }

    @Override
    public Vector3f getWorldCoordinates(Vector2f screenPosition, float zPos,
            Vector3f store) {
        return null;
    }

    @Override
    public void setRenderer(Renderer r) {
    }

    @Override
    public JMECanvas createCanvas(int w, int h) {
        return null;
    }

    @Override
    public JMECanvas createCanvas(int w, int h, String type,
            HashMap<String, Object> props) {
        return null;
    }

    @Override
    public TextureRenderer createTextureRenderer(int width, int height,
            TextureRenderer.Target target) {
        return null;
    }

    @Override
    public TextureRenderer createTextureRenderer(int width, int height, int samples, Target target) {
        return null;
    }

    @Override
    protected void updateDisplayBGC() {
    }

    @Override
    public String getAdapter() {
        return null;
    }

    /**
     * <code>getDisplayVendor</code> returns the vendor of the graphics adapter
     * 
     * @return The adapter vendor
     */
    @Override
    public String getDisplayVendor() {
        return null;
    }

    /**
     * <code>getDisplayRenderer</code> returns details of the adapter
     * 
     * @return The adapter details
     */
    @Override
    public String getDisplayRenderer() {
        return null;
    }

    /**
     * <code>getDisplayAPIVersion</code> returns the API version supported
     * 
     * @return The api version supported
     */
    @Override
    public String getDisplayAPIVersion() {
        return null;
    }

    @Override
    public String getDriverVersion() {
        return null;
    }

    @Override
    public RenderContext<Object> getCurrentContext() {
        return null;
    }

    @Override
    public void moveWindowTo(int locX, int locY) {
    }
}
