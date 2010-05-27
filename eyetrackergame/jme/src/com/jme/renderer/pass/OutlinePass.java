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

package com.jme.renderer.pass;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.CullState.Face;
import com.jme.system.DisplaySystem;

/**
 * Started Date: Jan 21, 2006<br>
 * 
 * This Pass can be used for drawing an outline around geometry objects. It does
 * this by first drawing the geometry as normal, and then drawing an outline
 * using the geometry's wireframe.<br>
 * 
 * @author Beskid Lucian Cristian
 * @author Tijl Houtbeckers (only minor changes / extra javadoc)
 * @version $Id: OutlinePass.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class OutlinePass extends RenderPass {

    private static final long serialVersionUID = 1L;
    
    public static final float DEFAULT_LINE_WIDTH = 3f;
    public static final ColorRGBA DEFAULT_OUTLINE_COLOR = ColorRGBA.black.clone();

    // render states needed to draw the outline
    private CullState frontCull;
    private CullState backCull;
    private WireframeState wireframeState;
    private LightState noLights;
    private TextureState noTexture;
    private BlendState blendState;

    public OutlinePass() {
        wireframeState = DisplaySystem.getDisplaySystem().getRenderer().createWireframeState();
        wireframeState.setFace(WireframeState.Face.FrontAndBack);
        wireframeState.setLineWidth(DEFAULT_LINE_WIDTH);
        wireframeState.setEnabled(true);
        
        frontCull = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        frontCull.setCullFace(Face.Front);
        
        backCull = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
        backCull.setCullFace(Face.Back);

        // On some systems anti-aliased lines only look good when AA is used for the scene
        if (DisplaySystem.getDisplaySystem().getMinSamples() > 0) {
            wireframeState.setAntialiased(true);
        } else {
            wireframeState.setAntialiased(false);
        }

        noLights = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        noLights.setGlobalAmbient(DEFAULT_OUTLINE_COLOR);
        noLights.setEnabled(true);

        noTexture = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
        noTexture.setEnabled(true);

        blendState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        blendState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        blendState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        blendState.setBlendEnabled(true);
        blendState.setEnabled(true);

    }

    public void doRender(Renderer renderer) {
        // if there's nothing to do
        if (spatials.size() == 0)
            return;

        // normal render
        context.enforceState(frontCull);
        super.doRender(renderer);

        // set up the render states
//        CullState.setFlippedCulling(true);
        context.enforceState(backCull);
        context.enforceState(wireframeState);
        context.enforceState(noLights);
        context.enforceState(noTexture);
        context.enforceState(blendState);

        // this will draw the wireframe
        super.doRender(renderer);

        // revert state changes
//        CullState.setFlippedCulling(false);
        context.clearEnforcedStates();
    }

    public void setOutlineWidth(float width) {
        wireframeState.setLineWidth(width);
    }

    public float getOutlineWidth() {
        return wireframeState.getLineWidth();
    }

    public void setOutlineColor(ColorRGBA outlineColor) {
        noLights.setGlobalAmbient(outlineColor);
    }

    public ColorRGBA getOutlineColor() {
        return noLights.getGlobalAmbient();
    }

    public BlendState getBlendState() {
        return blendState;
    }

    public void setBlendState(BlendState alphaState) {
        this.blendState = alphaState;
    }
}

