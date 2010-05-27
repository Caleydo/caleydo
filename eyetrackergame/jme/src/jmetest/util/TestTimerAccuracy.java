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
package jmetest.util;

import com.jme.animation.SpatialTransformer;
import com.jme.app.SimpleGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.shape.Box;

/**
 * <code>TestTimeAccuracy</code>
 * 
 * (loosly based on TestSpatialTransform by Philip Wainwright)
 * 
 * This class tests the accuracy of com.jme.util.Timer
 * 
 * It uses a SpatialTransformer to move a Box from the coordinates of one Box to
 * those of another Box. By default the time specified for this is 2 seconds. In
 * addition to that, the background color is changed every 2 seconds. This means
 * the moment the Box using the SpatialTransformer seemingly becomes "one" with
 * the Box it's moving towards, the background color should change. Because a
 * SpatialTransformer effectivly uses RT_WRAP in time small inaccuracies will
 * add up enough to be visible.
 * 
 * By pressing Q during runtime one can add an artificial delay to create a
 * lower framerate, and see how this potentially affects any problems. Note that
 * this test does not take into account any floatingpoint- or other inaccuracies
 * that SpatialTransformer could suffer from.
 * 
 * This test was originally written to demonstrate inaccuracies in LWJGLTimer
 * version 1.9.
 * 
 * @author Tijl Houtbeckers.
 * 
 */

public class TestTimerAccuracy extends SimpleGame {

    private SpatialTransformer spt;

    /**
     * the number of seconds it will take for the transforming box to complete
     * it's path (must be an integer value)
     */
    private final float INTERVAL = 2f;

    /** delay per frame in ms */
    private int updatedelay = 0;

    private Box static1, static2, transformer;

    private final String appname = "TestTimer (Press Q to change update delay) Update delay: ";

    public TestTimerAccuracy() {
    }

    public static void main(String[] args) {
        TestTimerAccuracy testApp = new TestTimerAccuracy();
        testApp.setConfigShowMode(ConfigShowMode.AlwaysShow);
        testApp.start();

    }

    public void simpleInitGame() {
        display.setTitle(appname + "none");

        // initialize boxes
        static1 = new Box("Static Box 1", new Vector3f(0, 0, 0), new Vector3f(
                1f, 1f, 1f));
        static2 = new Box("Static Box 2", new Vector3f(0, 0, 0), new Vector3f(
                1f, 1f, 1f));
        static2.getLocalTranslation().y += 5;
        transformer = new Box("Transforming Box", new Vector3f(0, 0, 0),
                new Vector3f(1f, 1f, 1f));

        cam.setLocation(new Vector3f(1f, 2.5f, 12));

        this.rootNode.attachChild(static1);
        this.rootNode.attachChild(static2);

        this.rootNode.attachChild(transformer);

        display.getRenderer().setBackgroundColor(ColorRGBA.blue.clone());

        setupTransformer();

        KeyBindingManager.getKeyBindingManager().set("change speed",
                KeyInput.KEY_Q);
    }

    private void setupTransformer() {
        // setup transformer
        transformer.removeController(spt);

        spt = new SpatialTransformer(1);
        spt.setRepeatType(Controller.RT_WRAP);
        spt.setPosition(0, 0f, static2.getLocalTranslation());
        spt.setPosition(0, INTERVAL, static1.getLocalTranslation());

        spt.setObject(transformer, 0, -1);

        transformer.addController(spt);
        spt.interpolateMissing();
        spt.setActive(true);
    }

    private long start = 0;

    protected void simpleUpdate() {
        if (start == 0) {
            start = System.currentTimeMillis();
        }

        if (KeyBindingManager.getKeyBindingManager().isValidCommand(
                "change speed", false)) {

            if (updatedelay == 0) {
                updatedelay = 1;
                display.setTitle(appname + "1 ms");
            } else if (updatedelay == 1) {
                updatedelay = 20;
                display.setTitle(appname + "20 ms");
            } else if (updatedelay == 20) {
                updatedelay = 0;
                display.setTitle(appname + "none");
            }
            setupTransformer();
            start = System.currentTimeMillis();
        }

        long currenttime = System.currentTimeMillis();
        long end = start + (((int) INTERVAL) * 1000);
        if (end <= currenttime) {
            if (display.getRenderer().getBackgroundColor().equals(ColorRGBA.blue))
                display.getRenderer().setBackgroundColor(ColorRGBA.black.clone());
            else if (display.getRenderer().getBackgroundColor().equals(ColorRGBA.black)) {
                display.getRenderer().setBackgroundColor(ColorRGBA.blue.clone());
            }
            start = end;
        }
        try {
            if (updatedelay != 0)
                Thread.sleep(updatedelay);
        } catch (InterruptedException e) {

        }
    }
}
