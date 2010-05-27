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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jme.bounding.BoundingVolume;
import com.jme.light.Light;
import com.jme.light.PointLight;
import com.jme.light.SpotLight;
import com.jme.math.FastMath;
import com.jme.math.Plane;
import com.jme.math.Plane.Side;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial;

public class LightUtil {
    private static class LightComparator implements Comparator<Light> {
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
    private static LightComparator lightComparator = new LightComparator();

    public static void sort(Geometry geometry, List<Light> lights) {
        lightComparator.setSpatial(geometry);
        Collections.sort( lights, lightComparator);
    }

    protected static float getValueFor(Light l, BoundingVolume val) {
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

    protected static float getValueFor(PointLight l, BoundingVolume val) {
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

    protected static float getValueFor(SpotLight l, BoundingVolume val) {
        if(val == null) {
            return 0;
        }
        Plane p = new Plane(l.getDirection(), l.getDirection().dot(
                l.getLocation()));
        if (val.whichSide(p) != Side.NEGATIVE)
                return getValueFor((PointLight) l, val);

        return 0;
    }

    protected static float getColorValue(Light l) {
        return strength(l.getAmbient()) + strength(l.getDiffuse());
    }
    
    protected static float strength(ColorRGBA color) {
        return FastMath.sqrt(color.r * color.r + color.g * color.g + color.b * color.b);
    }
}
