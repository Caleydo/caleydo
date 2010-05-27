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

package jmetest.effects;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Line;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.ZBufferState;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleLines;

/**
 * For fun, try modifying the pLines' stipple and mode settings!
 * 
 * @author Joshua Slack
 * @version $Id: TestLineParticles.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestLineParticles extends SimpleGame {

    private ParticleLines pLines;
    private Vector3f currentPos = new Vector3f(), newPos = new Vector3f();
    private float frameRate = 0;

    public static void main(String[] args) {
        TestLineParticles app = new TestLineParticles();
        app.setConfigShowMode(ConfigShowMode.AlwaysShow);
        app.start();
    }

    protected void simpleUpdate() {
        if (tpf > 1f)
            tpf = 1.0f; // do this to prevent a long pause at start

        if ((int) currentPos.x == (int) newPos.x
                && (int) currentPos.y == (int) newPos.y
                && (int) currentPos.z == (int) newPos.z) {
            newPos.x = (float) Math.random() * 50 - 25;
            newPos.y = (float) Math.random() * 50 - 25;
            newPos.z = (float) Math.random() * 50 - 150;
        }

        frameRate = timer.getFrameRate() / 2;
        currentPos.x -= (currentPos.x - newPos.x) / frameRate;
        currentPos.y -= (currentPos.y - newPos.y) / frameRate;
        currentPos.z -= (currentPos.z - newPos.z) / frameRate;

        pLines.setOriginOffset(currentPos);

    }

    protected void simpleInitGame() {
        display.setTitle("Particle System - Line Particles");
        lightState.setEnabled(false);

        pLines = ParticleFactory.buildLineParticles("particles", 300);
        pLines.setLineWidth(3);
        pLines.setMode(Line.Mode.Segments);
        pLines.setAntialiased(true);
        pLines.setParticleOrientation(FastMath.HALF_PI); // Particle Lines
                                                        // are horizontal by
                                                        // default, this
                                                        // turns it vertical
        pLines.setEmissionDirection(new Vector3f(0, 1, 0));
        pLines.setOriginOffset(new Vector3f(0, 0, 0));
        pLines.setInitialVelocity(.006f);
        pLines.setStartSize(4f);
        pLines.setEndSize(1.5f);
        pLines.setMinimumLifeTime(1250f);
        pLines.setMaximumLifeTime(1800f);
        pLines.setParticleSpinSpeed(3 * FastMath.PI); // rotate 3pi times (1.5 rotations) per second
        pLines.setStartColor(new ColorRGBA(1, 0, 0, 1));
        pLines.setEndColor(new ColorRGBA(0, 1, 0, 0));
        pLines.setMaximumAngle(360f * FastMath.DEG_TO_RAD);
        pLines.getParticleController().setControlFlow(false);
        pLines.warmUp(120);

        BlendState as1 = display.getRenderer().createBlendState();
        as1.setBlendEnabled(true);
        as1.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as1.setDestinationFunction(BlendState.DestinationFunction.One);
        as1.setEnabled(true);
        rootNode.setRenderState(as1);

        ZBufferState zstate = display.getRenderer().createZBufferState();
        zstate.setEnabled(false);
        pLines.setRenderState(zstate);

        pLines.setModelBound(new BoundingSphere());
        pLines.updateModelBound();

        rootNode.attachChild(pLines);
    }
}
