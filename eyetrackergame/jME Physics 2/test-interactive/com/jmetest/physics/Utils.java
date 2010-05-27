/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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
package com.jmetest.physics;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.scene.state.BlendState.TestFunction;
import com.jme.scene.state.CullState.Face;
import com.jme.system.DisplaySystem;


public class Utils {
    /**
     * Little helper method to color a spatial.
     *
     * @param spatial the spatial to be colored
     * @param color   desired color
     * @param shininess desired shininess
     */
    public static void color( Spatial spatial, ColorRGBA color, int shininess ) {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        final MaterialState materialState = display.getRenderer().createMaterialState();
        materialState.setDiffuse( color );
        materialState.setAmbient( color.mult( new ColorRGBA( 0.3f, 0.3f, 0.3f, 1 ) ) );
        materialState.setShininess( shininess );
        float mul = 1 + shininess > 18 ? (shininess-28)*0.01f : 0;
        materialState.setSpecular( color.mult( new ColorRGBA( mul, mul, mul, 1 ) ) );
        spatial.setRenderState( materialState );

        if ( color.a < 1 ) {
            final BlendState blendState = display.getRenderer().createBlendState();
            blendState.setEnabled( true );
            blendState.setBlendEnabled( true );
            blendState.setSourceFunction(SourceFunction.SourceAlpha);
            blendState.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
            blendState.setTestEnabled(true);
            blendState.setTestFunction(TestFunction.GreaterThan);
            spatial.setRenderState( blendState );
            spatial.setRenderQueueMode( Renderer.QUEUE_TRANSPARENT );
        }

        CullState cullState = display.getRenderer().createCullState();
        cullState.setCullFace(Face.Back);
        spatial.setRenderState( cullState );
    }
}

/*
 * $Log: Utils.java,v $
 * Revision 1.3  2007/09/22 14:28:36  irrisor
 * spongy contacts, improved material handling, center of mass correctly handled now, breakable joints, use odejava 0.3.1
 *
 * Revision 1.2  2007/08/28 12:19:36  irrisor
 * renamed autodisable to autorest, added unrest method, set root logger to warning level instead of physics logger only
 *
 * Revision 1.1  2007/06/16 14:17:01  irrisor
 * Contacts got an 'applied' flag to avoid application but still generate events -> GHOST material working;
 * New test with dominos, some optimizations concerning enabled nodes; physics speed can be adjusted in SimplePhysicsGame
 *
 */

