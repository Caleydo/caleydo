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

package com.jme.scene.state.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.jme.renderer.ColorRGBA;
import com.jme.renderer.RenderContext;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.jogl.records.MaterialStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>JOGLMaterialState</code> subclasses MaterialState using the JOGL
 * API to access OpenGL to set the material for a given node and it's children.
 *
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @author Steve Vaughan - JOGL port
 * @version $Id: JOGLMaterialState.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class JOGLMaterialState extends MaterialState {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor instantiates a new <code>JOGLMaterialState</code> object.
     */
    public JOGLMaterialState() {
        super();
    }

    /**
     * <code>set</code> calls the OpenGL material function to set the proper
     * material state.
     *
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {
        final GL gl = GLU.getCurrentGL();

        // ask for the current state record
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        MaterialStateRecord record = (MaterialStateRecord) context.getStateRecord(StateType.Material);
        context.currentStates[StateType.Material.ordinal()] = this;

        if(isEnabled()) {
	        int face = getGLMaterialFace(getMaterialFace());
	
	        // setup colormaterial, if changed.
	        applyColorMaterial(getColorMaterial(), face, record);
	
	        // apply colors, if needed and not what is currently set.
	        applyColor(GL.GL_AMBIENT, getAmbient(), face, record);
	        applyColor(GL.GL_DIFFUSE, getDiffuse(), face, record);
	        applyColor(GL.GL_EMISSION, getEmissive(), face, record);
	        applyColor(GL.GL_SPECULAR, getSpecular(), face, record);
	
	        // set our shine
	        if (!record.isValid() || face != record.face || record.shininess != shininess) {
	            gl.glMaterialf(face, GL.GL_SHININESS, shininess);
	            record.shininess = shininess;
	        }
	
	        record.face = face;
        }
        else {
        	// apply defaults
        	int face = getGLMaterialFace(defaultMaterialFace);
        	
	        applyColorMaterial(defaultColorMaterial, face, record);
	        
	        applyColor(GL.GL_AMBIENT, defaultAmbient, face, record);
	        applyColor(GL.GL_DIFFUSE, defaultDiffuse, face, record);
	        applyColor(GL.GL_EMISSION, defaultEmissive, face, record);
	        applyColor(GL.GL_SPECULAR, defaultSpecular, face, record);
	
	        // set our shine
	        if (!record.isValid() || face != record.face || record.shininess != defaultShininess) {
	            gl.glMaterialf(face, GL.GL_SHININESS, defaultShininess);
	            record.shininess = defaultShininess;
	        }
	        
	        record.face = face;
        }

        if (!record.isValid())
            record.validate();
    }

    private static void applyColor(int glMatColor, ColorRGBA color, int face, MaterialStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        if (!isVertexProvidedColor(glMatColor, record)
                && (!record.isValid() || face != record.face || !record
                        .isSetColor(face, glMatColor, color, record))) {

            record.tempColorBuff.clear();
            record.tempColorBuff.put(color.r).put(color.g).put(color.b).put(color.a);
            record.tempColorBuff.flip();
            gl.glMaterialfv(face, glMatColor, record.tempColorBuff); // TODO Check for float

            record.setColor(face, glMatColor, color);
        }
    }

    private static boolean isVertexProvidedColor(int glMatColor, MaterialStateRecord record) {
        switch (glMatColor) {
            case GL.GL_AMBIENT:
                return record.colorMaterial == GL.GL_AMBIENT
                        || record.colorMaterial == GL.GL_AMBIENT_AND_DIFFUSE;
            case GL.GL_DIFFUSE:
                return record.colorMaterial == GL.GL_DIFFUSE
                        || record.colorMaterial == GL.GL_AMBIENT_AND_DIFFUSE;
            case GL.GL_SPECULAR:
                return record.colorMaterial == GL.GL_SPECULAR;
            case GL.GL_EMISSION:
                return record.colorMaterial == GL.GL_EMISSION;
        }
        return false;
    }

    private void applyColorMaterial(ColorMaterial colorMaterial, int face, MaterialStateRecord record) {
        final GL gl = GLU.getCurrentGL();

        int glMat = getGLColorMaterial(colorMaterial);
        if (!record.isValid() || face != record.face || glMat != record.colorMaterial) {
            if (glMat == GL.GL_NONE) {
                gl.glDisable(GL.GL_COLOR_MATERIAL);
            } else {
                gl.glColorMaterial(face, glMat);
                gl.glEnable(GL.GL_COLOR_MATERIAL);
                record.resetColorsForCM(face, glMat);
            }
            record.colorMaterial = glMat;
        }

    }

    /**
     * Converts the color material setting of this state to a GL constant.
     *
     * @return the GL constant
     */
    private static int getGLColorMaterial(ColorMaterial material) {
        switch (material) {
            case None:
                return GL.GL_NONE;
            case Ambient:
                return GL.GL_AMBIENT;
            case Diffuse:
                return GL.GL_DIFFUSE;
            case AmbientAndDiffuse:
                return GL.GL_AMBIENT_AND_DIFFUSE;
            case Emissive:
                return GL.GL_EMISSION;
            case Specular:
                return GL.GL_SPECULAR;
        }
        throw new IllegalArgumentException("invalid color material setting: "+material);
    }

    /**
     * Converts the material face setting of this state to a GL constant.
     *
     * @return the GL constant
     */
    private static int getGLMaterialFace(MaterialFace face) {
        switch (face) {
            case Front:
                return GL.GL_FRONT;
            case Back:
                return GL.GL_BACK;
            case FrontAndBack:
                return GL.GL_FRONT_AND_BACK;
        }
        throw new IllegalArgumentException("invalid material face setting: "+face);
    }

    @Override
    public StateRecord createStateRecord() {
        return new MaterialStateRecord();
    }
}
