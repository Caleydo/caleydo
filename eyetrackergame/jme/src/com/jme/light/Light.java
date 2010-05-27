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

package com.jme.light;

import java.io.IOException;
import java.io.Serializable;

import com.jme.renderer.ColorRGBA;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Light</code> defines the attributes of a light element. This class
 * is abstract and intended to be subclassed by specific lighting types. A
 * light will illuminate portions of the scene by assigning its properties
 * to the objects in the scene. This will affect the objects color values,
 * depending on the color of the ambient, diffuse and specular light
 * components.
 *
 * Ambient light defines the general light of the scene, that is the
 * intensity and color of lighting if no particular lights are affecting it.
 *
 * Diffuse lighting defines the reflection of light on matte surfaces.
 *
 * Specular lighting defines the reflection of light on shiny surfaces.
 *
 * @author Mark Powell
 * @version $Id: Light.java 4634 2009-08-27 23:03:38Z skye.book $
 */
public abstract class Light implements Serializable, Savable {

    private static final long serialVersionUID = 2L;

    public enum Type {
        Directional,
        Point,
        Spot
    }

    //light attributes.
    private ColorRGBA ambient;
    private ColorRGBA diffuse;
    private ColorRGBA specular;

    private boolean attenuate;
    private float constant = 1;
    private float linear;
    private float quadratic;
    
    private int lightMask = 0; 
    private int backLightMask = 0;

    private boolean enabled;
    
    /** when true, indicates the lights in this lightState will cast shadows. */
    protected boolean shadowCaster;

    /**
     * Constructor instantiates a new <code>Light</code> object. All
     * light color values are set to white.
     *
     */
    public Light() {
        ambient = new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f);
        diffuse = new ColorRGBA();
        specular = new ColorRGBA();
    }

    /**
     *
     * <code>getType</code> returns the type of the light that has been
     * created.
     * @return the type of light that has been created.
     */
    public abstract Type getType();

    /**
     * <code>getConstant</code> returns the value for the constant attenuation.
     * @return the value for the constant attenuation.
     */
    public float getConstant() {
        return constant;
    }

    /**
     * <code>setConstant</code> sets the value for the constant attenuation.
     * @param constant the value for the constant attenuation.
     */
    public void setConstant(float constant) {
        this.constant = constant;
    }

    /**
     * <code>getLinear</code> returns the value for the linear attenuation.
     * @return the value for the linear attenuation.
     */
    public float getLinear() {
        return linear;
    }

    /**
     * <code>setLinear</code> sets the value for the linear attenuation.
     * @param linear the value for the linear attenuation.
     */
    public void setLinear(float linear) {
        this.linear = linear;
    }

    /**
     * <code>getQuadratic</code> returns the value for the quadratic
     * attenuation.
     * @return the value for the quadratic attenuation.
     */
    public float getQuadratic() {
        return quadratic;
    }

    /**
     * <code>setQuadratic</code> sets the value for the quadratic attenuation.
     * @param quadratic the value for the quadratic attenuation.
     */
    public void setQuadratic(float quadratic) {
        this.quadratic = quadratic;
    }

    /**
     * <code>isAttenuate</code> returns true if attenuation is to be used
     * for this light.
     * @return true if attenuation is to be used, false otherwise.
     */
    public boolean isAttenuate() {
        return attenuate;
    }

    /**
     * <code>setAttenuate</code> sets if attenuation is to be used. True sets
     * it on, false otherwise.
     * @param attenuate true to use attenuation, false not to.
     */
    public void setAttenuate(boolean attenuate) {
        this.attenuate = attenuate;
    }

    /**
     *
     * <code>isEnabled</code> returns true if the light is enabled, false
     * otherwise.
     * @return true if the light is enabled, false if it is not.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     *
     * <code>setEnabled</code> sets the light on or off. True turns it on,
     * false turns it off.
     * @param value true to turn the light on, false to turn it off.
     */
    public void setEnabled(boolean value) {
        enabled = value;
    }

    /**
     * <code>getSpecular</code> returns the specular color value for this
     * light.
     * @return the specular color value of the light.
     */
    public ColorRGBA getSpecular() {
        return specular;
    }

    /**
     * <code>setSpecular</code> sets the specular color value for this light.
     * @param specular the specular color value of the light.
     */
    public void setSpecular(ColorRGBA specular) {
        this.specular = specular;
    }

    /**
     * <code>getDiffuse</code> returns the diffuse color value for this light.
     * @return the diffuse color value for this light.
     */
    public ColorRGBA getDiffuse() {
        return diffuse;
    }

    /**
     * <code>setDiffuse</code> sets the diffuse color value for this light.
     * @param diffuse the diffuse color value for this light.
     */
    public void setDiffuse(ColorRGBA diffuse) {
        this.diffuse = diffuse;
    }

    /**
     * <code>getAmbient</code> returns the ambient color value for this light.
     * @return the ambient color value for this light.
     */
    public ColorRGBA getAmbient() {
        return ambient;
    }

    /**
     * <code>setAmbient</code> sets the ambient color value for this light.
     * @param ambient the ambient color value for this light.
     */
    public void setAmbient(ColorRGBA ambient) {
        this.ambient = ambient;
    }

    /**
     * @return Returns the lightMask - default is 0 or not masked.
     */
    public int getLightMask() {
        return lightMask;
    }

    /**
     * <code>setLightMask</code> sets what attributes of this light to apply
     * as an int comprised of bitwise |'ed values from LightState.Mask_XXXX.
     * LightMask.MASK_GLOBALAMBIENT is ignored.
     * 
     * @param lightMask
     *            The lightMask to set.
     */
    public void setLightMask(int lightMask) {
        this.lightMask = lightMask;
    }

    /**
     * Saves the light mask to a back store. That backstore is recalled with
     * popLightMask. Despite the name, this is not a stack and additional pushes
     * will simply overwrite the backstored value.
     */
    public void pushLightMask() {
        backLightMask = lightMask;
    }
    
    /**
     * Recalls the light mask from a back store or 0 if none was pushed.
     * 
     * @see com.jme.light.Light#pushLightMask()
     */
    public void popLightMask() {
        lightMask = backLightMask;
    }

    /**
     * @return Returns whether this light is able to cast shadows.
     */
    public boolean isShadowCaster() {
        return shadowCaster;
    }

    /**
     * @param mayCastShadows
     *            true if this light can be used to derive shadows (when used in
     *            conjunction with a shadow pass.)
     */
    public void setShadowCaster(boolean mayCastShadows) {
        this.shadowCaster = mayCastShadows;
    }

    /**
     * Copies the light values from the given light into this Light.
     * 
     * @param light
     *            the Light to copy from.
     */
    public void copyFrom(Light light) {
        ambient = new ColorRGBA(light.ambient);
        attenuate = light.attenuate;
        constant = light.constant;
        diffuse = new ColorRGBA(light.diffuse);
        enabled = light.enabled;
        linear = light.linear;
        quadratic = light.quadratic;
        shadowCaster = light.shadowCaster;
        specular = new ColorRGBA(light.specular);
    }
    
    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(ambient, "ambient", ColorRGBA.black);
        capsule.write(diffuse, "diffuse", ColorRGBA.black);
        capsule.write(specular, "specular", ColorRGBA.black);
        capsule.write(attenuate, "attenuate", false);
        capsule.write(constant, "constant", 1);
        capsule.write(linear, "linear", 0);
        capsule.write(quadratic, "quadratic", 0);
        capsule.write(lightMask, "lightMask", 0);
        capsule.write(backLightMask, "backLightMask", 0);
        capsule.write(enabled, "enabled", false);
        capsule.write(shadowCaster, "shadowCaster", false);
    }
    
    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        ambient = (ColorRGBA)capsule.readSavable("ambient", ColorRGBA.black.clone());
        diffuse = (ColorRGBA)capsule.readSavable("diffuse", ColorRGBA.black.clone());
        specular = (ColorRGBA)capsule.readSavable("specular", ColorRGBA.black.clone());
        attenuate = capsule.readBoolean("attenuate", false);
        constant = capsule.readFloat("constant", 1);
        linear = capsule.readFloat("linear", 0);
        quadratic = capsule.readFloat("quadratic", 0);
        lightMask = capsule.readInt("lightMask", 0);
        backLightMask = capsule.readInt("backLightMask", 0);
        enabled = capsule.readBoolean("enabled", false);
        shadowCaster = capsule.readBoolean("shadowCaster", false);
    }
    
    public Class<? extends Light> getClassTag() {
        return this.getClass();
    }
}
