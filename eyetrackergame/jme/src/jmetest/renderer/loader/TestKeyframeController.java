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

package jmetest.renderer.loader;

import com.jme.app.SimpleGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.shape.Sphere;
import com.jmex.model.animation.KeyframeController;

/**
 * Started Date: Jun 13, 2004<br><br>
 * Class to test use of KeyframeController
 * 
 * @author Jack Lindamood
 */
public class TestKeyframeController extends SimpleGame{
    KeyframeController kc;
    public static void main(String[] args) {
        new TestKeyframeController().start();
    }

    protected void simpleUpdate(){
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_middle", false)) {
            kc.setNewAnimationTimes(.5f,2.75f);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_total", false)) {
            kc.setNewAnimationTimes(0,3);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("start_end", false)) {
            kc.setNewAnimationTimes(3,3);
        }
        if (KeyBindingManager
            .getKeyBindingManager()
            .isValidCommand("toggle_wrap", false)) {
            if (kc.getRepeatType()==Controller.RT_CYCLE)
                kc.setRepeatType(Controller.RT_WRAP);
            else
                kc.setRepeatType(Controller.RT_CYCLE);
        }
    }

    protected void simpleInitGame() {
        Sphere small=new Sphere("small",9,15,1);
        small.setSolidColor(ColorRGBA.black.clone());
        Sphere medium=new Sphere("med",9,15,4);
        medium.setSolidColor(ColorRGBA.red.clone());
        Sphere big=new Sphere("big",9,15,10);
        big.setSolidColor(ColorRGBA.blue.clone());
        Sphere thisone=new Sphere("blarg",9,15,1);
        thisone.setSolidColor(ColorRGBA.white.clone());
        kc=new KeyframeController();
        kc.setMorphingMesh(thisone);
        kc.setKeyframe(0,small);
        kc.setKeyframe(2.5f,medium);
        kc.setKeyframe(3,big);
        kc.setRepeatType(Controller.RT_CYCLE);
        thisone.addController(kc);
        rootNode.attachChild(thisone);
        lightState.setEnabled(false);
        // Note: T L B C Already used
        KeyBindingManager.getKeyBindingManager().set("start_middle",KeyInput.KEY_Q);
        KeyBindingManager.getKeyBindingManager().set("start_total",KeyInput.KEY_X);
        KeyBindingManager.getKeyBindingManager().set("toggle_wrap",KeyInput.KEY_Z);
        KeyBindingManager.getKeyBindingManager().set("start_end",KeyInput.KEY_E);
    }
}