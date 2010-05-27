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

import com.jme.renderer.ColorRGBA;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>MaterialState</code> defines a state to define an objects material
 * settings. Material is defined by the emissive quality of the object, the
 * ambient color, diffuse color and specular color. The material also defines
 * the shininess of the object and the alpha value of the object.
 * 
 * @author Mark Powell
 * @author Joshua Slack - Material Face and Performance enhancements
 * @author Three Rings - contributed color material
 * @version $Id: MaterialState.java 4723 2009-10-13 14:22:36Z blaine.dev $
 */
public abstract class MaterialState extends RenderState {
    
    public enum ColorMaterial {
        /** Geometry colors are ignored. This is default. */
        None,

        /** Geometry colors determine material ambient color. */
        Ambient,

        /** Geometry colors determine material diffuse color. */
        Diffuse,

        /** Geometry colors determine material ambient and diffuse colors. */
        AmbientAndDiffuse,

        /** Geometry colors determine material specular colors. */
        Specular,

        /** Geometry colors determine material emissive color. */
        Emissive;
    }
    
    public enum MaterialFace {
        /** Apply materials to front face only. This is default. */
        Front,
        
        /** Apply materials to back face only. */
        Back,
        
        /** Apply materials to front and back faces. */
        FrontAndBack;
    }

    
    /** Default ambient color for all material states. */
    public static final ColorRGBA defaultAmbient = new ColorRGBA(0.2f, 0.2f,
            0.2f, 1.0f);
    
    /** Default diffuse color for all material states. */
    public static final ColorRGBA defaultDiffuse = new ColorRGBA(0.8f, 0.8f,
            0.8f, 1.0f);
    
    /** Default specular color for all material states. */
    public static final ColorRGBA defaultSpecular = new ColorRGBA(0.0f, 0.0f,
            0.0f, 1.0f);
    
    /** Default emissive color for all material states. */
    public static final ColorRGBA defaultEmissive = new ColorRGBA(0.0f, 0.0f,
            0.0f, 1.0f);
    
    /** Default shininess for all material states. */
    public static final float defaultShininess = 0.0f;

    /** Default color material mode for all material states. */
    public static final ColorMaterial defaultColorMaterial = ColorMaterial.None;

    /** Default material face for all material states. */
    public static final MaterialFace defaultMaterialFace = MaterialFace.Front;

    // attributes of the material
    protected ColorRGBA ambient;
    protected ColorRGBA diffuse;
    protected ColorRGBA specular;
    protected ColorRGBA emissive;
    protected float shininess;
    protected ColorMaterial colorMaterial;
    protected MaterialFace materialFace;

    /**
     * Constructor instantiates a new <code>MaterialState</code> object.
     */
    public MaterialState() {
        emissive = defaultEmissive.clone();
        ambient = defaultAmbient.clone();
        diffuse = defaultDiffuse.clone();
        specular = defaultSpecular.clone();
        shininess = defaultShininess;
        colorMaterial = defaultColorMaterial;
        materialFace = defaultMaterialFace;
    }

    /**
     * <code>getAmbient</code> retreives the ambient color of the material.
     * 
     * @return the color of the ambient value.
     */
    public ColorRGBA getAmbient() {
        return ambient;
    }

    /**
     * <code>setAmbient</code> sets the ambient color of the material.
     * 
     * @param ambient
     *            the ambient color of the material.
     */
    public void setAmbient(ColorRGBA ambient) {
        this.ambient.set(ambient);
        setNeedsRefresh(true);
    }

    /**
     * <code>getDiffuse</code> retrieves the diffuse color of the material.
     * 
     * @return the color of the diffuse value.
     */
    public ColorRGBA getDiffuse() {
        return diffuse;
    }

    /**
     * <code>setDiffuse</code> sets the diffuse color of the material.
     * 
     * @param diffuse
     *            the diffuse color of the material.
     */
    public void setDiffuse(ColorRGBA diffuse) {
        this.diffuse.set(diffuse);
        setNeedsRefresh(true);
    }

    /**
     * <code>getEmissive</code> retrieves the emissive color of the material.
     * 
     * @return the color of the emissive value.
     */
    public ColorRGBA getEmissive() {
        return emissive;
    }

    /**
     * <code>setEmissive</code> sets the emissive color of the material.
     * 
     * @param emissive
     *            the emissive color of the material.
     */
    public void setEmissive(ColorRGBA emissive) {
        this.emissive.set(emissive);
        setNeedsRefresh(true);
    }

    /**
     * <code>getShininess</code> retrieves the unshininess value of the
     * material.
     * 
     * @return the un-shininess value of the material.
     * @see #setShininess(float)
     */
    public float getShininess() {
        return shininess;
    }

    /**
     * <code>setShininess</code> sets the un-shininess of the material.
     * <P>
     * This property has a misleading name, since higher magnitude corresponds
     * to <i>unshininess</i>, not <i>shininess</i>.
     * </P>
     * 
     * @param unshininess  Between 0 (completely shiny) and 128 (no shininess)
     *                     inclusive.
     */
    public void setShininess(float unshininess) {
        if (unshininess < 0 || unshininess > 128) {
            throw new IllegalArgumentException(
                    "Unshininess must be between 0 and 128.");
        }
        this.shininess = unshininess;
        setNeedsRefresh(true);
    }

    /**
     * <code>getSpecular</code> retrieves the specular color of the material.
     * 
     * @return the specular color of the material.
     */
    public ColorRGBA getSpecular() {
        return specular;
    }

    /**
     * <code>setSpecular</code> sets the specular color of the material.
     * 
     * @param specular
     *            the specular color of the material.
     */
    public void setSpecular(ColorRGBA specular) {
        this.specular.set(specular);
        setNeedsRefresh(true);
    }

    /**
     * <code>getColorMaterial</code> retrieves the color material mode, which
     * determines how geometry colors affect the material.
     * 
     * @return the color material mode
     */
    public ColorMaterial getColorMaterial() {
        return colorMaterial;
    }

    /**
     * <code>setColorMaterial</code> sets the color material mode.
     * 
     * @param material
     *            the color material mode
     * @throws IllegalArgumentException
     *             if material is null
     */
    public void setColorMaterial(ColorMaterial material) {
        if (material == null) {
            throw new IllegalArgumentException("material can not be null.");
        }
        this.colorMaterial = material;
        setNeedsRefresh(true);
    }

    /**
     * <code>getMaterialFace</code> retrieves the face this material state
     * affects.
     * 
     * @return the current face setting
     */
    public MaterialFace getMaterialFace() {
        return materialFace;
    }

    /**
     * <code>setMaterialFace</code> sets the face this material state affects.
     * 
     * @param face
     *            the new face setting
     * @throws IllegalArgumentException
     *             if material is null
     */
    public void setMaterialFace(MaterialFace face) {
        if (materialFace == null) {
            throw new IllegalArgumentException("face can not be null.");
        }
        this.materialFace = face;
        setNeedsRefresh(true);
    }

    /**
     * <code>getType</code> returns the render state type of this.
     * (RS_MATERIAL).
     * 
     * @see com.jme.scene.state.RenderState#getType()
     * @deprecated As of 2.0, use {@link RenderState#getStateType()} instead.
     */
    public int getType() {
        return RS_MATERIAL;
    }

    /**
     * <code>getStateType</code> returns the type {@link RenderState.StateType#Material}
     * 
     * @return {@link RenderState.StateType#Material}
     * @see com.jme.scene.state.RenderState#getStateType()
     */
    public StateType getStateType() {
    	
        return StateType.Material;
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(ambient, "ambient", ColorRGBA.black);
        capsule.write(diffuse, "diffuse", ColorRGBA.black);
        capsule.write(specular, "specular", ColorRGBA.black);
        capsule.write(emissive, "emissive", ColorRGBA.black);
        capsule.write(shininess, "shininess", defaultShininess);
        capsule.write(colorMaterial, "colorMaterial", defaultColorMaterial);
        capsule.write(materialFace, "materialFace", defaultMaterialFace);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        ambient = (ColorRGBA)capsule.readSavable("ambient", ColorRGBA.black.clone());
        diffuse = (ColorRGBA)capsule.readSavable("diffuse", ColorRGBA.black.clone());
        specular = (ColorRGBA)capsule.readSavable("specular", ColorRGBA.black.clone());
        emissive = (ColorRGBA)capsule.readSavable("emissive", ColorRGBA.black.clone());
        shininess = capsule.readFloat("shininess", defaultShininess);
        colorMaterial = capsule.readEnum("colorMaterial", ColorMaterial.class, defaultColorMaterial);
        materialFace = capsule.readEnum("materialFace", MaterialFace.class, defaultMaterialFace);
    }
    
    public Class<?> getClassTag() {
        return MaterialState.class;
    }
}
