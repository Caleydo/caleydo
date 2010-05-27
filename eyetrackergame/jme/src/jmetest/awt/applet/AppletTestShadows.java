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

package jmetest.awt.applet;

import com.jme.bounding.BoundingBox;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.shape.PQTorus;
import com.jme.scene.shape.Quad;
import com.jmex.awt.applet.SimpleJMEPassApplet;

public class AppletTestShadows extends SimpleJMEPassApplet {
    private static final long serialVersionUID = 1L;

    private float rps = .5f;
    private float angle = 0;
    private float elapsed = 0;
    private float throttle = 1/30f; // only update 30 times per frame
    private Quaternion rotQuat = new Quaternion();
    private PointLight p;
    
    public AppletTestShadows() {
        stencilBits = 8;
    }
    
    public void simpleAppletUpdate() {
        elapsed += getTimePerFrame();
        if (elapsed >= throttle) {
            angle = elapsed * rps * FastMath.TWO_PI;
            
            rotQuat.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
            rotQuat.mult(p.getLocation(), p.getLocation());
            elapsed = 0;
        }
    }

    public void simpleAppletSetup() {
        getCamera().setLocation(new Vector3f(0, 50, -100));
        getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        ShadowedRenderPass srp = new ShadowedRenderPass();
        srp.setLightingMethod(ShadowedRenderPass.LightingMethod.Modulative);
        getManager().clearAll();
        getManager().add(srp);
        
        getLightState().detachAll();
        p = new PointLight();
        p.setDiffuse(new ColorRGBA(1f, 1f, 1f, 1f));
        p.setAmbient(new ColorRGBA(.8f, .8f, .8f, .4f));
        p.getLocation().set(10, 10, -10);
        p.setShadowCaster(true);
        getLightState().attach(p);
        
        PQTorus pqt = new PQTorus("actor", 3, 2, 2.0f, 1.0f, 128, 16);
        pqt.setModelBound(new BoundingBox());
        pqt.updateModelBound();
        
        Quad floor = new Quad("floor", 100, 100);
        floor.setModelBound(new BoundingBox());
        floor.updateModelBound();
        
        Quaternion rotateDown = new Quaternion(new float[] {90*FastMath.DEG_TO_RAD,0,0});
        floor.setLocalRotation(rotateDown);
        floor.getLocalTranslation().y-=2;
        
        getRootNode().attachChild(pqt);
        getRootNode().attachChild(floor);
        srp.add(getRootNode());
        srp.addOccluder(pqt);
    }
    
    
}
