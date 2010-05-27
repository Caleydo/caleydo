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

package com.jme.scene.state.lwjgl;

import java.util.Stack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GLContext;

import com.jme.light.DirectionalLight;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.Matrix4f;
import com.jme.renderer.AbstractCamera;
import com.jme.renderer.RenderContext;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.StateRecord;
import com.jme.scene.state.lwjgl.records.LightRecord;
import com.jme.scene.state.lwjgl.records.LightStateRecord;
import com.jme.system.DisplaySystem;

/**
 * <code>LWJGLLightState</code> subclasses the Light class using the LWJGL API
 * to access OpenGL for light processing.
 * 
 * @author Mark Powell
 * @author Joshua Slack - reworked for StateRecords.
 * @version $Id: LWJGLLightState.java 4725 2009-10-15 11:51:31Z andreas.grabner@gmail.com $
 */
public class LWJGLLightState extends LightState {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor instantiates a new <code>LWJGLLightState</code>.
     */
    public LWJGLLightState() {
        super();
    }

    /**
     * <code>set</code> iterates over the light queue and processes each
     * individual light.
     * 
     * @see com.jme.scene.state.RenderState#apply()
     */
    public void apply() {        
        RenderContext<?> context = DisplaySystem.getDisplaySystem().getCurrentContext();
        LightStateRecord record = (LightStateRecord) context.getStateRecord(StateType.Light);
        context.currentStates[StateType.Light.ordinal()] = this;

        if (isEnabled() && LIGHTS_ENABLED && getQuantity() > 0) {
            setLightEnabled(true, record);
            setTwoSided(twoSidedOn, record);
            setLocalViewer(localViewerOn, record);
            if (GLContext.getCapabilities().OpenGL12) {
                setSpecularControl(separateSpecularOn, record);
            }

            for (int i = 0, max = getQuantity(); i < max; i++) {
                Light light = get(i);                
                LightRecord lr = record.getLightRecord(i);
                //TODO: use the reference to get the lightrecord - rherlitz

                if (lr == null) {
                    lr = new LightRecord();
                    record.setLightRecord(lr, i);
                }

                if (light == null) {
                    setSingleLightEnabled(false, i, record, lr);
                } else {
                    if (light.isEnabled()) {
                        setLight(i, light, record, lr);
                    } else {
                        setSingleLightEnabled(false, i, record, lr);
                    }
                }
            }

            // disable lights at and above the max count in this state
            for (int i = getQuantity(); i < MAX_LIGHTS_ALLOWED; i++) {
                LightRecord lr = record.getLightRecord(i);

                if (lr == null) {
                    lr = new LightRecord();
                    record.setLightRecord(lr, i);
                }
                setSingleLightEnabled(false, i, record, lr);
            }

            if ((lightMask & MASK_GLOBALAMBIENT) == 0) {
                setModelAmbient(record, globalAmbient[0], globalAmbient[1],
                        globalAmbient[2], globalAmbient[3]);
            } else {
                setDefaultModelAmbient(record);
            }

            if (!record.isValid())
                record.validate();
        } else {
            setLightEnabled(false, record);
        }
    }

    private void setLight(int index, Light light, LightStateRecord record,
            LightRecord lr) {
        setSingleLightEnabled(true, index, record, lr);

        if ((lightMask & MASK_AMBIENT) == 0
                && (light.getLightMask() & MASK_AMBIENT) == 0) {
            setAmbient(index, record, light.getAmbient().r,
                    light.getAmbient().g, light.getAmbient().b, light
                            .getAmbient().a, lr);
        } else {
            setDefaultAmbient(index, record, lr);
        }

        if ((lightMask & MASK_DIFFUSE) == 0
                && (light.getLightMask() & MASK_DIFFUSE) == 0) {

            setDiffuse(index, record, light.getDiffuse().r,
                    light.getDiffuse().g, light.getDiffuse().b, light
                            .getDiffuse().a, lr);
        } else {
            setDefaultDiffuse(index, record, lr);
        }

        if ((lightMask & MASK_SPECULAR) == 0
                && (light.getLightMask() & MASK_SPECULAR) == 0) {

            setSpecular(index, record, light.getSpecular().r, light
                    .getSpecular().g, light.getSpecular().b, light
                    .getSpecular().a, lr);
        } else {
            setDefaultSpecular(index, record, lr);
        }

        if (light.isAttenuate()) {
            setAttenuate(true, index, light, record, lr);

        } else {
            setAttenuate(false, index, light, record, lr);

        }

        switch (light.getType()) {
            case Directional: {
                DirectionalLight pkDL = (DirectionalLight) light;

                setPosition(index, record, -pkDL.getDirection().x, -pkDL
                        .getDirection().y, -pkDL.getDirection().z, 0, lr);
                break;
            }
            case Point:
            case Spot: {
                PointLight pointLight = (PointLight) light;
                setPosition(index, record, pointLight.getLocation().x,
                        pointLight.getLocation().y, pointLight.getLocation().z,
                        1, lr);
                break;
            }
        }

        if (light.getType() == Light.Type.Spot) {
            SpotLight spot = (SpotLight) light;
            setSpotCutoff(index, record, spot.getAngle(), lr);
            setSpotDirection(index, record, spot.getDirection().x, spot
                    .getDirection().y, spot.getDirection().z, 0);
            setSpotExponent(index, record, spot.getExponent(), lr);
        } else {
            // set the cutoff to 180, which causes the other spot params to be
            // ignored.
            setSpotCutoff(index, record, 180, lr);
        }
    }

    private void setSingleLightEnabled(boolean enable, int index,
            LightStateRecord record, LightRecord lr) {
        if (!record.isValid() || lr.isEnabled() != enable) {
            if (enable) {
                GL11.glEnable(GL11.GL_LIGHT0 + index);
            } else {
                GL11.glDisable(GL11.GL_LIGHT0 + index);
            }

            lr.setEnabled(enable);
        }
    }

    private void setLightEnabled(boolean enable, LightStateRecord record) {
        if (!record.isValid() || record.isEnabled() != enable) {
            if (enable) {
                GL11.glEnable(GL11.GL_LIGHTING);
            } else {
                GL11.glDisable(GL11.GL_LIGHTING);
            }
            record.setEnabled(enable);
        }
    }

    private void setTwoSided(boolean twoSided, LightStateRecord record) {
        if (!record.isValid() || record.isTwoSidedOn() != twoSided) {
            if (twoSided) {
                GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
            } else {
                GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_FALSE);
            }
            record.setTwoSidedOn(twoSided);
        }
    }

    private void setLocalViewer(boolean localViewer, LightStateRecord record) {
        if (!record.isValid() || record.isLocalViewer() != localViewer) {
            if (localViewer) {
                GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER,
                        GL11.GL_TRUE);
            } else {
                GL11.glLightModeli(GL11.GL_LIGHT_MODEL_LOCAL_VIEWER,
                        GL11.GL_FALSE);
            }
            record.setLocalViewer(localViewer);
        }
    }

    private void setSpecularControl(boolean separateSpecularOn,
            LightStateRecord record) {
        if (!record.isValid()
                || record.isSeparateSpecular() != separateSpecularOn) {
            if (separateSpecularOn) {
                GL11.glLightModeli(GL12.GL_LIGHT_MODEL_COLOR_CONTROL,
                        GL12.GL_SEPARATE_SPECULAR_COLOR);
            } else {
                GL11.glLightModeli(GL12.GL_LIGHT_MODEL_COLOR_CONTROL,
                        GL12.GL_SINGLE_COLOR);
            }
            record.setSeparateSpecular(separateSpecularOn);
        }
    }

    private void setModelAmbient(LightStateRecord record, float red,
            float green, float blue, float alpha) {
        if (!record.isValid() || record.globalAmbient.r != red
                || record.globalAmbient.g != green
                || record.globalAmbient.b != blue
                || record.globalAmbient.a != alpha) {
            record.lightBuffer.clear();
            record.lightBuffer.put(red);
            record.lightBuffer.put(green);
            record.lightBuffer.put(blue);
            record.lightBuffer.put(alpha);
            record.lightBuffer.flip();
            GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, record.lightBuffer);
            record.globalAmbient.set(red, green, blue, alpha);
        }
    }

    private void setDefaultModelAmbient(LightStateRecord record) {
        if (!record.isValid() || record.globalAmbient.r != 0
                || record.globalAmbient.g != 0 || record.globalAmbient.b != 0
                || record.globalAmbient.a != 0) {
            GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, zeroBuffer);
            record.globalAmbient.set(0, 0, 0, 0);
        }
    }

    private void setAmbient(int index, LightStateRecord record, float red,
            float green, float blue, float alpha, LightRecord lr) {
        if (!record.isValid() || lr.ambient.r != red || lr.ambient.g != green
                || lr.ambient.b != blue || lr.ambient.a != alpha) {
            record.lightBuffer.clear();
            record.lightBuffer.put(red);
            record.lightBuffer.put(green);
            record.lightBuffer.put(blue);
            record.lightBuffer.put(alpha);
            record.lightBuffer.flip();
            GL11.glLight(GL11.GL_LIGHT0 + index, GL11.GL_AMBIENT,
                    record.lightBuffer);
            lr.ambient.set(red, green, blue, alpha);
        }
    }

    private void setDefaultAmbient(int index, LightStateRecord record,
            LightRecord lr) {
        if (!record.isValid() || lr.ambient.r != 0 || lr.ambient.g != 0
                || lr.ambient.b != 0 || lr.ambient.a != 0) {
            GL11.glLight(GL11.GL_LIGHT0 + index, GL11.GL_AMBIENT, zeroBuffer);
            lr.ambient.set(0, 0, 0, 0);
        }
    }

    private void setDiffuse(int index, LightStateRecord record, float red,
            float green, float blue, float alpha, LightRecord lr) {
        if (!record.isValid() || lr.diffuse.r != red || lr.diffuse.g != green
                || lr.diffuse.b != blue || lr.diffuse.a != alpha) {
            record.lightBuffer.clear();
            record.lightBuffer.put(red);
            record.lightBuffer.put(green);
            record.lightBuffer.put(blue);
            record.lightBuffer.put(alpha);
            record.lightBuffer.flip();
            GL11.glLight(GL11.GL_LIGHT0 + index, GL11.GL_DIFFUSE,
                    record.lightBuffer);
            lr.diffuse.set(red, green, blue, alpha);
        }
    }

    private void setDefaultDiffuse(int index, LightStateRecord record,
            LightRecord lr) {
        if (!record.isValid() || lr.diffuse.r != 0 || lr.diffuse.g != 0
                || lr.diffuse.b != 0 || lr.diffuse.a != 0) {
            GL11.glLight(GL11.GL_LIGHT0 + index, GL11.GL_DIFFUSE, zeroBuffer);
            lr.diffuse.set(0, 0, 0, 0);
        }
    }

    private void setSpecular(int index, LightStateRecord record, float red,
            float green, float blue, float alpha, LightRecord lr) {
        if (!record.isValid() || lr.specular.r != red || lr.specular.g != green
                || lr.specular.b != blue || lr.specular.a != alpha) {
            record.lightBuffer.clear();
            record.lightBuffer.put(red);
            record.lightBuffer.put(green);
            record.lightBuffer.put(blue);
            record.lightBuffer.put(alpha);
            record.lightBuffer.flip();
            GL11.glLight(GL11.GL_LIGHT0 + index, GL11.GL_SPECULAR,
                    record.lightBuffer);
            lr.specular.set(red, green, blue, alpha);
        }
    }

    private void setDefaultSpecular(int index, LightStateRecord record,
            LightRecord lr) {
        if (!record.isValid() || lr.specular.r != 0 || lr.specular.g != 0
                || lr.specular.b != 0 || lr.specular.a != 0) {
            GL11.glLight(GL11.GL_LIGHT0 + index, GL11.GL_SPECULAR, zeroBuffer);
            lr.specular.set(0, 0, 0, 0);
        }
    }

    private void setPosition(int index, LightStateRecord record,
            float positionX, float positionY, float positionZ, float value,
            LightRecord lr) {
        // From OpenGL Docs:
        // The light position is transformed by the contents of the current top
        // of the ModelView matrix stack when you specify the light position
        // with a call to glLightfv(GL_LIGHT_POSITION,). If you later change
        // the ModelView matrix, such as when the view changes for the next
        // frame, the light position isn't automatically retransformed by the
        // new contents of the ModelView matrix. If you want to update the
        // lights position, you must again specify the light position with a
        // call to glLightfv(GL_LIGHT_POSITION,).
        
        //XXX: This is a hack until we get a better lighting model up
        Matrix4f modelViewMatrix = ((AbstractCamera) DisplaySystem
                .getDisplaySystem().getRenderer().getCamera())
                .getModelViewMatrix();
        
        if (!record.isValid() || lr.position.x != positionX || lr.position.y != positionY
                || lr.position.z != positionZ || lr.position.w != value ||
                !lr.modelViewMatrix.equals(modelViewMatrix)) {

            record.lightBuffer.clear();
            record.lightBuffer.put(positionX);
            record.lightBuffer.put(positionY);
            record.lightBuffer.put(positionZ);
            record.lightBuffer.put(value);
            record.lightBuffer.flip();
            GL11.glLight(GL11.GL_LIGHT0 + index, GL11.GL_POSITION,
                    record.lightBuffer);
            
            lr.position.set(positionX, positionY, positionZ, value);
            lr.modelViewMatrix.set(modelViewMatrix);
        }
    }

    private void setSpotDirection(int index, LightStateRecord record,
            float directionX, float directionY, float directionZ, float value) {
        // From OpenGL Docs:
        // The light position is transformed by the contents of the current top
        // of the ModelView matrix stack when you specify the light position
        // with a call to glLightfv(GL_LIGHT_POSITION,). If you later change
        // the ModelView matrix, such as when the view changes for the next
        // frame, the light position isn't automatically retransformed by the
        // new contents of the ModelView matrix. If you want to update the
        // lights position, you must again specify the light position with a
        // call to glLightfv(GL_LIGHT_POSITION,).
        record.lightBuffer.clear();
        record.lightBuffer.put(directionX);
        record.lightBuffer.put(directionY);
        record.lightBuffer.put(directionZ);
        record.lightBuffer.put(value);
        record.lightBuffer.flip();
        GL11.glLight(GL11.GL_LIGHT0 + index, GL11.GL_SPOT_DIRECTION,
                record.lightBuffer);
    }

    private void setConstant(int index, float constant, LightRecord lr,
            boolean force) {
        if (force || constant != lr.getConstant()) {
            GL11.glLightf(GL11.GL_LIGHT0 + index, GL11.GL_CONSTANT_ATTENUATION,
                    constant);
            lr.setConstant(constant);
        }
    }

    private void setLinear(int index, float linear, LightRecord lr,
            boolean force) {
        if (force || linear != lr.getLinear()) {
            GL11.glLightf(GL11.GL_LIGHT0 + index, GL11.GL_LINEAR_ATTENUATION,
                    linear);
            lr.setLinear(linear);
        }
    }

    private void setQuadratic(int index, float quad, LightRecord lr,
            boolean force) {
        if (force || quad != lr.getQuadratic()) {
            GL11.glLightf(GL11.GL_LIGHT0 + index,
                    GL11.GL_QUADRATIC_ATTENUATION, quad);
            lr.setQuadratic(quad);
        }
    }

    private void setAttenuate(boolean attenuate, int index, Light light,
            LightStateRecord record, LightRecord lr) {
        if (attenuate) {
            setConstant(index, light.getConstant(), lr, !record.isValid());
            setLinear(index, light.getLinear(), lr, !record.isValid());
            setQuadratic(index, light.getQuadratic(), lr, !record.isValid());
        } else {
            setConstant(index, 1, lr, !record.isValid());
            setLinear(index, 0, lr, !record.isValid());
            setQuadratic(index, 0, lr, !record.isValid());
        }
        lr.setAttenuate(attenuate);
    }

    private void setSpotExponent(int index, LightStateRecord record,
            float exponent, LightRecord lr) {
        if (!record.isValid() || lr.getSpotExponent() != exponent) {
            GL11.glLightf(GL11.GL_LIGHT0 + index, GL11.GL_SPOT_EXPONENT,
                    exponent);
            lr.setSpotExponent(exponent);
        }
    }

    private void setSpotCutoff(int index, LightStateRecord record,
            float cutoff, LightRecord lr) {
        if (!record.isValid() || lr.getSpotCutoff() != cutoff) {
            GL11.glLightf(GL11.GL_LIGHT0 + index, GL11.GL_SPOT_CUTOFF, cutoff);
            lr.setSpotCutoff(cutoff);
        }
    }

    @Override
    public StateRecord createStateRecord() {
        return new LightStateRecord();
    }

    public RenderState extract(Stack<? extends RenderState> stack, Spatial spat) {        
        LightCombineMode mode = spat.getLightCombineMode();
        
        Geometry geom = (Geometry)spat;
        LightState lightState = geom.getLightState();        
        if (lightState == null) {
            lightState = new LWJGLLightState();
            geom.setLightState(lightState);
        }
        
        lightState.detachAll();
        
        if (mode == LightCombineMode.Replace
                || (mode != LightCombineMode.Off && stack.size() == 1)) {
            // todo: use dummy state if off?                        
            
            LWJGLLightState copyLightState = (LWJGLLightState)stack.peek();            
            copyLightState(copyLightState, lightState);            
        } else {
            // accumulate the lights in the stack into a single LightState object
            Object states[] = stack.toArray();
            boolean foundEnabled = false;
            switch (mode) {
                case CombineClosest:
                case CombineClosestEnabled:
                    for (int iIndex = states.length - 1; iIndex >= 0; iIndex--) {
                        LWJGLLightState pkLState = (LWJGLLightState) states[iIndex];
                        if (!pkLState.isEnabled()) {
                            if (mode == LightCombineMode.CombineClosestEnabled)
                                break;

                            continue;
                        }

                        foundEnabled = true;
                        copyLightState(pkLState, lightState);
                    }
                    break;
                case CombineFirst:
                    for (int iIndex = 0, max = states.length; iIndex < max; iIndex++) {
                        LWJGLLightState pkLState = (LWJGLLightState) states[iIndex];
                        if (!pkLState.isEnabled())
                            continue;

                        foundEnabled = true;
                        copyLightState(pkLState, lightState);
                    }
                    break;
                case Off:
                    break;
            }
            lightState.setEnabled(foundEnabled);
        }

        return lightState;
    }

    private void copyLightState(LWJGLLightState pkLState, LightState lightState) {
        lightState.setTwoSidedLighting(pkLState.getTwoSidedLighting());
        lightState.setLocalViewer(pkLState.getLocalViewer());
        lightState.setSeparateSpecular(pkLState.getSeparateSpecular());
        lightState.setEnabled(pkLState.isEnabled());
        lightState.setGlobalAmbient(pkLState.getGlobalAmbient());
        for (int i = 0, maxL = pkLState.getLightList().size(); i < maxL; i++) {
            Light pkLight = pkLState.get(i);
            if (pkLight != null) {
                lightState.attach(pkLight);
            }
        }
    }
}
