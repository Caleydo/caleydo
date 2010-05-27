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

/*
 * Created on Apr 6, 2004
 */
package com.jmex.effects.transients;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;

/**
 * A <code>FadeInOutController</code> controlls a <code>FadeInOut</code> object.
 * The <code>FadeInOut</code> object is updated by having its alpha value increased
 * every update.  When the alpha value is >= 1, it stage is increased and the
 * alpha value of the <code>FadeInOut</code> object is decreased untill its color is <= 0
 * and then it's stage is increased again.
 *
 * @author Ahmed
 * @author Jack Lindamood (javadoc only)
 */
public class FadeInOutController extends Controller {

    private static final long serialVersionUID = 1L;
	/** The FadeInOut object to fade upon. */
    private FadeInOut fio;


    /**
     * Creates a new FadeInOutController that fades the given object.
     * @param f The object to fade per time.
     */ 
    public FadeInOutController(FadeInOut f) {
        fio = f;
    }

    /**
     * Returns "Alpha: "+{alpha value of fade object's quad's color}
     * @return Stat string for this controller.
     */
    public String getStats() {
    	return ("Alpha: " + fio.getFadeColor().a);
    }

    /**
     * Updates the fade colors of the fade object.
     * @param timeF A time value to change the color by.
     */
    public void update(float timeF) {
        float time = timeF * fio.getSpeed();
        ColorRGBA color = fio.getFadeColor();
        if (fio.getCurrentStage() == 0) {
            color.a += time;
            fio.setFadeColor(color);
            if (fio.getFadeColor().a >= 1.0f) {
                fio.detachChild(fio.getFadeOutNode());
                fio.attachChild(fio.getFadeInNode());
                fio.setCurrentStage(fio.getCurrentStage() + 1);
            }
        }else if (fio.getCurrentStage() == 1) {
            color.a -= time;
            fio.setFadeColor(color);
            if (fio.getFadeColor().a <= 0.0f) {
                fio.setCurrentStage(fio.getCurrentStage() + 1);
            }
        }
    }
}