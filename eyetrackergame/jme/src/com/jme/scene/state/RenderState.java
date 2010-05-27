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

package com.jme.scene.state;

import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>RenderState</code> is the base class for all states that affect the
 * rendering of a piece of geometry. They aren't created directly, but are
 * created for users from the renderer. The renderstate of a parent affects its
 * children and it is OK to assign to more than one Spatial the same render
 * state.
 * 
 * @author Mark Powell
 * @author Joshua Slack
 * @author Jack Lindamood (javadoc only)
 * @version $Id: RenderState.java 4137 2009-03-20 18:38:24Z christoph.luder $
 */
public abstract class RenderState implements Savable {

	/**
	 * Enumerates every possible {@link RenderState} type. 
	 * 
	 * @note The order of this enumeration matters as long as the deprecated 
	 * integer render state types still exist, because the ordinal should map 
	 * to the old values. When the deprecated integer types are removed, then 
	 * we can re-order this enum.
	 * 
	 * @author Carter
	 */
	public static enum StateType {
		
		/** The value returned by getType() for BlendState. */
        Blend(true),
        
        /** The value returned by getType() for FogState. */
        Fog(false),
        
        /** The value returned by getType() for LightState. */
        Light(false),
        
        /** The value returend by getType() for MaterialState. */
        Material(false),
        
        /** The value returned by getType() for ShadeState. */
        Shade(true),
        
        /** The value returned by getType() for TextureState. */
        Texture(false),
        
        /** The value returned by getType() for WireframeState. */
        Wireframe(false),
        
        /** The value returned by getType() for ZBufferState. */
        ZBuffer(true),
        
        /** The value returned by getType() for CullState. */
        Cull(true),
        
        /** The value returned by getType() for VertexProgramState. */
        VertexProgram(true),
        
        /** The value returned by getType() for FragmentProgramState. */
        FragmentProgram(true),
        
        /** The value returned by getType() for StencilState. */
        Stencil(true),
        
        /** The value returned by getType() for GLSLShaderObjectsState. */
        GLSLShaderObjects(true),
        
        /** The value returned by getType() for ColorMaskState. */ 
        ColorMask(true),
        
        /** The value returned by getType() for ClipState. */
        Clip(true),
        
        /** The value returned by getType() for StippleState. */
        Stipple(true);
        
        /**
         * <p>
         * If false, each renderstate of that type is always applied in the renderer
         * and only field by field checks are done to minimize jni overhead. This is
         * slower than setting to true, but relieves the programmer from situations
         * where he has to remember to update the needsRefresh field of a state.
         * </p>
         * <p>
         * If true, each renderstate of that type is checked for == with the last
         * applied renderstate of the same type. If same and the state's
         * needsRefresh method returns false, then application of the renderstate is
         * skipped. This can be much faster than setting false, but in certain
         * circumstances, the programmer must manually set needsRefresh (for
         * example, in a FogState, if you call getFogColor().set(....) to change the
         * color, the fogstate will not set the needsRefresh field. In non-quick
         * compare mode, this is not a problem because it will go into the apply
         * method and do an actual check of the current fog color in opengl vs. the
         * color in the state being applied.)
         * </p>
         * <p>
         * DEFAULTS:
         * <ul>
         * <li>Blend: true</li>
         * <li>Clip: true</li>
         * <li>ColorMask: true</li>
         * <li>Cull: true</li>
         * <li>Fog: false</li>
         * <li>FragmentProgram: true</li>
         * <li>GLSLShaderObjects: true</li>
         * <li>Light: false</li>
         * <li>Material: false</li>
         * <li>Shade: true</li>
         * <li>Stencil: true</li>
         * <li>Texture: false</li>
         * <li>VertexProgram: true</li>
         * <li>Wireframe: false</li>
         * <li>ZBuffer: true</li>\
         * <li>Stipple: true</li>\
         * </ul>
         */
        private boolean quickCompare = false;
        
        private StateType(boolean quickCompare) {
        	
        	this.quickCompare = quickCompare;
        }
        
        public boolean canQuickCompare() {
        	
            return quickCompare;
        }
    }

    /** The value returned by getType() for BlendState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#Blend} */
    public static final int RS_BLEND = 0;

    /** The value returned by getType() for FogState.
     * @deprecated As of 2.0, use {@link RenderState.StateType#fog} */
    public static final int RS_FOG = 1;

    /** The value returned by getType() for LightState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#Light} */
    public static final int RS_LIGHT = 2;

    /** The value returend by getType() for MaterialState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#Material} */
    public static final int RS_MATERIAL = 3;

    /** The value returned by getType() for ShadeState.
     * @deprecated As of 2.0, use {@link RenderState.StateType#Shade} */
    public static final int RS_SHADE = 4;

    /** The value returned by getType() for TextureState.
     * @deprecated As of 2.0, use {@link RenderState.StateType#Texture} */
    public static final int RS_TEXTURE = 5;

    /** The value returned by getType() for WireframeState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#Wireframe} */
    public static final int RS_WIREFRAME = 6;

    /** The value returned by getType() for ZBufferState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#ZBuffer} */
    public static final int RS_ZBUFFER = 7;

    /** The value returned by getType() for CullState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#Cull} */
    public static final int RS_CULL = 8;

    /** The value returned by getType() for VertexProgramState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#VertexProgram} */
    public static final int RS_VERTEX_PROGRAM = 9;

    /** The value returned by getType() for FragmentProgramState.
     * @deprecated As of 2.0, use {@link RenderState.StateType#FragmentProgram} */
    public static final int RS_FRAGMENT_PROGRAM = 10;

    /** The value returned by getType() for StencilState.
     * @deprecated As of 2.0, use {@link RenderState.StateType#Stencil} */
    public static final int RS_STENCIL = 11;
    
    /** The value returned by getType() for GLSLShaderObjectsState.
     * @deprecated As of 2.0, use {@link RenderState.StateType#GLSLShaderObjects} */
    public static final int RS_GLSL_SHADER_OBJECTS = 12;

    /** The value returned by getType() for ColorMaskState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#ColorMask} */
    public static final int RS_COLORMASK_STATE = 13; 

    /** The value returned by getType() for ClipState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType#Clip} */
    public static final int RS_CLIP = 14;

    /** The total number of diffrent types of RenderState. 
     * @deprecated As of 2.0, use {@link RenderState.StateType} */
    public static final int RS_MAX_STATE = 15;

    /**
     * <p>
     * If false, each renderstate of that type is always applied in the renderer
     * and only field by field checks are done to minimize jni overhead. This is
     * slower than setting to true, but relieves the programmer from situations
     * where he has to remember to update the needsRefresh field of a state.
     * </p>
     * <p>
     * If true, each renderstate of that type is checked for == with the last
     * applied renderstate of the same type. If same and the state's
     * needsRefresh method returns false, then application of the renderstate is
     * skipped. This can be much faster than setting false, but in certain
     * circumstances, the programmer must manually set needsRefresh (for
     * example, in a FogState, if you call getFogColor().set(....) to change the
     * color, the fogstate will not set the needsRefresh field. In non-quick
     * compare mode, this is not a problem because it will go into the apply
     * method and do an actual check of the current fog color in opengl vs. the
     * color in the state being applied.)
     * </p>
     * <p>
     * DEFAULTS:
     * <ul>
     * <li>RS_ALPHA: true</li>
     * <li>RS_DITHER: true</li>
     * <li>RS_FOG: false</li>
     * <li>RS_LIGHT: false</li>
     * <li>RS_MATERIAL: false</li>
     * <li>RS_SHADE: true</li>
     * <li>RS_TEXTURE: false</li>
     * <li>RS_WIREFRAME: false</li>
     * <li>RS_ZBUFFER: true</li>
     * <li>RS_CULL: true</li>
     * <li>RS_VERTEX_PROGRAM: true</li>
     * <li>RS_FRAGMENT_PROGRAM: true</li>
     * <li>RS_ATTRIBUTE: true</li>
     * <li>RS_STENCIL: true</li>
     * <li>RS_GLSL_SHADER_OBJECTS: true</li>
     * <li>RS_COLORMASK_STATE: true</li>
     * <li>RS_CLIP: true</li>
     * </ul>
     * @deprecated As of 2.0, use {@link StateType} instead.
     */
    public static boolean[] QUICK_COMPARE = new boolean[RS_MAX_STATE];
    static {
        QUICK_COMPARE[RS_BLEND] = true;
        QUICK_COMPARE[RS_FOG] = false; // false because you can change the fog color object directly without telling the state
        QUICK_COMPARE[RS_LIGHT] = false; // false because you can change a light object directly without telling the state
        QUICK_COMPARE[RS_MATERIAL] = false; // false because you can change a material color object directly without telling the state
        QUICK_COMPARE[RS_SHADE] = true;
        QUICK_COMPARE[RS_TEXTURE] = false; // false because you can change a texture object directly without telling the state
        QUICK_COMPARE[RS_WIREFRAME] = false; // false by default because line attributes can change when drawing lines
        QUICK_COMPARE[RS_ZBUFFER] = true;
        QUICK_COMPARE[RS_CULL] = true;
        QUICK_COMPARE[RS_VERTEX_PROGRAM] = true;
        QUICK_COMPARE[RS_FRAGMENT_PROGRAM] = true;
        QUICK_COMPARE[RS_STENCIL] = false;
        QUICK_COMPARE[RS_GLSL_SHADER_OBJECTS] = true;
        QUICK_COMPARE[RS_COLORMASK_STATE] = true;
        QUICK_COMPARE[RS_CLIP] = true;
    }

    private boolean enabled = true;

    private boolean needsRefresh = false;

    /**
     * Construts a new RenderState. The state is enabled by default.
     */
    public RenderState() {
    }

    /**
     * Defined by the subclass, this returns an int identifying the renderstate.
     * For example, RS_CULL or RS_TEXTURE.
     * 
     * @return An int identifying this render state.
     * @deprecated Use {@link #getStateType()} instead.
     */
    public abstract int getType();
    
    /**
     * Defined by the subclass, this returns a StateType value identifying the renderstate.
     * For example, StateType.Cull or StateType.Texture.
     * 
     * @return A StateType value identifying this render state.
     */
    public abstract StateType getStateType();

    /**
     * Returns if this render state is enabled during rendering. Disabled states
     * are ignored.
     * 
     * @return True if this state is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets if this render state is enabled during rendering. Disabled states
     * are ignored.
     * 
     * @param value
     *            False if the state is to be disabled, true otherwise.
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
        setNeedsRefresh(true);
    }

    /**
     * This function is defined in the RenderState that is actually used by the
     * Renderer. It contains the code that, when executed, applies the render
     * state for the given render system. This should only be called internally
     * and not by users directly.
     */
    public abstract void apply();

    /**
     * Extracts from the stack the correct renderstate that should apply to the
     * given spatial. This is mainly used for RenderStates that can be
     * cumulative such as TextureState or LightState. By default, the top of the
     * static is returned. This function should not be called by users directly.
     * 
     * @param stack
     *            The stack to extract render states from.
     * @param spat
     *            The spatial to apply the render states too.
     * @return The render state to use.
     */
    public RenderState extract(Stack<? extends RenderState> stack, Spatial spat) {
    	
        // The default behavior is to return the top of the stack, the last item
        // pushed during the recursive traversal.
        return stack.peek();
    }
    
    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(enabled, "enabled", true);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        enabled = capsule.readBoolean("enabled", true);
    }
    
    public Class<?> getClassTag() {
        return this.getClass();
    }

    public abstract StateRecord createStateRecord();

    /**
     * @return true if we should apply this state even if we think it is the
     *         current state of its type in the current context. Is reset to
     *         false after apply is finished.
     */
    public boolean needsRefresh() {
        return needsRefresh;
    }
    
    /**
     * This should be called by states when it knows internal data has been altered.
     * 
     * @param refresh true if we should apply this state even if we think it is the
     *         current state of its type in the current context.
     */ 
    public void setNeedsRefresh(boolean refresh) {
        needsRefresh  = refresh;
    }

    /**
     * @see #QUICK_COMPARE
     * @param enabled
     */
    public static void setQuickCompares(boolean enabled) {
        
    	Arrays.fill(QUICK_COMPARE, enabled);
        
        for(StateType type : StateType.values())
        	type.quickCompare = enabled;
    }
}
