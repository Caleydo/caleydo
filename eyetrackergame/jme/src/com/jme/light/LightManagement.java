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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Plane.Side;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.state.LightState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * The <code> LightStateCreator </code> class is used to sort lights in a scene.
 * The utility allows the user to place multiple lights in a single container and
 * the best eight lights (those lights that most directly affect a Spatial) will be
 * applied.
 * <br>
 * This class should not be used anymore, use <code>Node.sortLights()</code> instead.
 * <br>
 * @see jmetest.util.TestManyLights
 * 
 * @author Badmi
 * @author Mark Powell (cleaning, savable)
 */
@Deprecated
public class LightManagement implements Serializable, Savable {

    private static final long serialVersionUID = 1L;

    public static boolean LIGHTS_ENABLED = true;

    private ArrayList<Light> lightList;
    
    private ArrayList<Light> tempLightList = new ArrayList<Light>();

    /** Creates a new instance of LightStateCreator */
    public LightManagement() {
        lightList = new ArrayList<Light>();
    }

    /**
     * Adds a light for the controller to sort into a spatial. All the lights
     * must be added before the lights could be sorted.
     */
    public void addLight(Light l) {
        lightList.add(l);
    }
    
    public boolean contains(Light l) {
        return lightList.contains(l);
    }
    
    public boolean removeLight(Light l) {
        return lightList.remove(l);
    }

    /**
     * Gets the Ith light from the creator. The placement of the light is
     * subject to change.
     */
    public Light get(int i) {
        return lightList.get(i);
    }

    /** Returns the number of lights in the creator. */
    public int numberOfLights() {
        return lightList.size();
    }

    /**
     * Creates a new LightState for a spatial placing the "best" eight lights
     * currently maintained by the LightStateCreator.
     */
    public LightState createLightState(Spatial sp) {
        LightState l = com.jme.system.DisplaySystem.getDisplaySystem()
                .getRenderer().createLightState();
        resortLightsFor(l, sp);
        return l;
    }
    /**
     * Gives the LightState the best possible lights for the Spatial. The spatial
     * must be using bounding volumes for this process to work properly.
     */
    public void resortLightsFor(LightState ls, Spatial sp) {
        if(ls == null) {
            return;
        }
        
        tempLightList.clear();
        tempLightList.addAll(ls.getLightList());
        
        if (LIGHTS_ENABLED && lightList.size() > 0) {
            ls.setEnabled(true);
            sort( sp );

            boolean updatelights = false;
            for (int i = 0, max = Math.min(LightState.MAX_LIGHTS_ALLOWED, lightList
                    .size()); i < max; i++) {
                Light light = get(i);
                if (!ls.getLightList().contains(light)) {
                    updatelights = true;
                    break;
                }
            }
            
            if (!updatelights) {
                return;
            }
            
            ls.detachAll();
            for (int i = 0, max = Math.min(LightState.MAX_LIGHTS_ALLOWED, lightList
                    .size()); i < max; i++) {
                ls.attach(get(i));
            }
            
            boolean doUpdate = false;
            if (tempLightList.size() != ls.getLightList().size()) {
                doUpdate = true;
            } else {
                for(int i=0;i<tempLightList.size();i++) {
                    if (tempLightList.get(i) != ls.get(i)) {
                        doUpdate = true;
                        break;
                    }
                }
            }
            
            if (doUpdate) {
                sp.updateRenderState();
            }
        } else {
            ls.setEnabled(false);
        }

        if (sp.getLightCombineMode() != LightCombineMode.Off) {
            sp.setLightCombineMode(LightCombineMode.CombineFirst);
        }
    }

    private class LightComparator implements Comparator<Light> {
        private Spatial sp;
        
        public void setSpatial(Spatial sp) {
            this.sp = sp;
        }
        
        public int compare( Light l1, Light l2 ) {
            float v1 = getValueFor( l1, sp.getWorldBound() );
            float v2 = getValueFor( l2, sp.getWorldBound() );
            float cmp = v1 - v2;
            if ( cmp > FastMath.FLT_EPSILON ) {
                return -1;
            } else if ( cmp < -FastMath.FLT_EPSILON ) {
                return 1;
            } else {
                return 0;
            }
        }        
    }
    private LightComparator lightComparator = new LightComparator();
    
    /**
     * Sort the lightList in descending order according to the {@link #getValueFor(Light, BoundingVolume)} method.
     * @param sp spatial to pass to getValueFor
     */
    protected void sort( final Spatial sp ) {
        lightComparator.setSpatial(sp);
        Collections.sort( lightList, lightComparator);
    }
    protected float max(ColorRGBA a) {
        return Math.max(Math.max(a.r, a.g), a.b);
    }

    protected float getColorValue(Light l) {
        return Math.max(Math.max(max(l.getAmbient()), max(l.getDiffuse())),
                max(l.getSpecular()));
    }

    protected float getValueFor(Light l, BoundingVolume val) {
        if (!l.isEnabled()){
            return 0;
        } else if (l.getType() == Light.Type.Directional) {
            return getColorValue(l);
        } else if (l.getType() == Light.Type.Point) {
            return getValueFor((PointLight) l, val);
        } else if (l.getType() == Light.Type.Spot) { return getValueFor(
                (SpotLight) l, val); }
        //If a new tipe of light was aded and this was not updated return .3
        return .3f;
    }

    float getValueFor(PointLight l, BoundingVolume val) {
        if(val == null) {
            return 0;
        }
        if (l.isAttenuate() && val != null) {
            float dist = val.distanceTo(l.getLocation());

            float color = getColorValue(l);
            float amlat = l.getConstant() + l.getLinear() * dist
                        + l.getQuadratic() * dist * dist;

            return color / amlat;
        }

        return getColorValue(l);        
    }

    float getValueFor(SpotLight l, BoundingVolume val) {
        if(val == null) {
            return 0;
        }
        Plane p = new Plane(l.getDirection(), l.getDirection().dot(
                l.getLocation()));
        if (val.whichSide(p) != Side.NEGATIVE)
                return getValueFor((PointLight) l, val);

        return 0;
    }

	@SuppressWarnings("unchecked")
	public void read(JMEImporter im) throws IOException {
		InputCapsule cap = im.getCapsule(this);
		lightList = cap.readSavableArrayList("lightList", new ArrayList<Light>());
	}

	public void write(JMEExporter ex) throws IOException {
		OutputCapsule cap = ex.getCapsule(this);
		cap.writeSavableArrayList(lightList, "lightList", new ArrayList<Light>());
	}
    
    public Class getClassTag() {
        return this.getClass();
    }

    public ArrayList<Light> getLights() {
        return lightList;
    }
    
    public void setLights(ArrayList<Light> lights) {
        lightList = lights;
    }

    public void reset() {
        lightList.clear();
    }
}
