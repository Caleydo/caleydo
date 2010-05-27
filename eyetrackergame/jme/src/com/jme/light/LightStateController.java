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

import com.jme.renderer.Camera;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * The <code> LightStateController </code> sorts the Lights from a collection of
 * lights, the best eight for a particular Spatial. This can be used to create
 * many lights inside a scene with moving objects. All spatials that use this
 * controller are required to have there own light state and have the Light
 * Combine Mode set to REPLACE. The controller uses a copy of a
 * LightStateController. New lights that are added to the original
 * LightStateContraller after the controller was created will be ignored. The
 * Controller allows you to set a update Interval that allows you to tell the
 * controller when to update.
 * <br>
 * This class should not be used anymore, use <code>Node.sortLights()</code> instead.
 * <br>
 * @see jmetest.util.TestManyLights
 * @author Badmi
 * @author Mark Powell
 */
@Deprecated
public class LightStateController extends Controller {

    private static final long serialVersionUID = 1L;

    private float timePass;

    private float updateInterval;

    private Spatial parent;

    public LightStateController() { }
    
    /**
     * Creates a new instance of LightStateController. The spatial passed to the
     * function is required to have a LightState attached. A copy is made of the
     * lightCreator that passed so all addition the original lightCreator will
     * be ignored.
     */
    public LightStateController(Spatial par, LightManagement manager) {
        this.parent = par;

        //Not needed but put in for clarification
        timePass = 0;
        updateInterval = 0;
    }

    /**
     * Creates a new instance of LightStateController The spatial passed to the
     * function is required to have a LightState attached. A copy is made of the
     * lightCreator that passed so all addition the original lightCreator will
     * be ignored.
     */
    public LightStateController(Spatial par, LightManagement manager,
            float updateInt, int timeSlot) {
        this.parent = par;

        //Not needed but put in for clarification
        if (timeSlot != 0) {
            timePass = ((int) updateInt) % (timeSlot);
        } else {
            timePass = 0;
        }
        updateInterval = updateInt;
    }

    /** Sets the Update Interval. */
    public void setUpdateInterval(float interval) {
        updateInterval = interval;
    }

    /** Returns the Update Interval. */
    public float getUpdateInterval() {
        return updateInterval;
    }

    /**
     * Update is called internally. The parent (if it is visible) has all lights
     * maintained by the LightStateCreator resorted for possible changes.
     */
    public void update(float time) {
        if(parent == null) {
            return;
        }
        timePass += time;
        if (parent.getLastFrustumIntersection() != Camera.FrustumIntersect.Outside) {
                if (timePass >= updateInterval || time < 0) {
                    timePass = 0;
                    LightControllerManager.lm.resortLightsFor((LightState) parent
                            .getRenderState(RenderState.StateType.Light), parent);
                }
        }
    }
    
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule cap = e.getCapsule(this);
        cap.write(timePass, "timePass", 0);
        cap.write(updateInterval, "updateInterval", 0);
        cap.write(parent, "parent", null);
    }
    
    @Override
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule cap = e.getCapsule(this);
        timePass = cap.readFloat("timePass", 0);
        updateInterval = cap.readFloat("updateInterval", 0);
        parent = (Spatial)cap.readSavable("parent", null);
    }
}