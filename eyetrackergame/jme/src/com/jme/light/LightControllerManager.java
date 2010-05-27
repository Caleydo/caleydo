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

import java.util.ArrayList;

import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;

/**
 * <code>LightControllerManager</code>
 * <br>
 * This class should not be used anymore, use <code>Node.sortLights()</code> instead.
 * <br>
 * @see jmetest.util.TestManyLights
 * @author Mark Powell
 */
@Deprecated
public class LightControllerManager {
    static ArrayList<LightStateController> controllerList = new ArrayList<LightStateController>(1);
    static LightManagement lm = new LightManagement();
    
    public static void addController(LightStateController lsc) {
        controllerList.add(lsc);
    }
    
    public static void clearSpatials() {
        controllerList.clear();
    }
    
    public static void clearLights() {
        lm.reset();
    }
    
    public LightManagement getLightManagement() {
        return lm;
    }
    
    public static void addSpatial(Spatial s) {
        //remove any existing lsc's
        clearLSCs(s);
        
        LightStateController lsc = new LightStateController(s,
                lm);
        controllerList.add(lsc);
        s.addController(lsc);
        if(s.getRenderState(RenderState.StateType.Light) == null) {
            s.setRenderState(DisplaySystem.getDisplaySystem().getRenderer().createLightState());
        }
        if (s.getLightCombineMode() != Spatial.LightCombineMode.Off)
            s.setLightCombineMode(Spatial.LightCombineMode.Replace);
    }
    
    private static void clearLSCs(Spatial s) {
        for (int i = 0; i < s.getControllers().size(); i++) {
            if(s.getController(i) instanceof LightStateController) {
                s.removeController(s.getController(i));
                i--;
            }
        }
        
        if (s instanceof Node) {
            Node n = (Node)s;
            for (int i = n.getQuantity(); --i > 0; ) {
                clearLSCs(n.getChild(i));
            }
        }
    }

    public static void addLight(Light l) {
        if(!lm.contains(l)) {
            lm.addLight(l);
        }
    }
    
    public static void removeLight(Light l) {
        lm.removeLight(l);
    }

    public static void removeSpatial(Spatial spatial) {
        for(int i = 0; i < spatial.getControllers().size(); i++) {
            if(spatial.getController(i) instanceof LightStateController) {
                controllerList.remove(spatial.getController(i));
            }
        }
    }
    
    public static void reset() {
    	lm.reset();
    }
}
