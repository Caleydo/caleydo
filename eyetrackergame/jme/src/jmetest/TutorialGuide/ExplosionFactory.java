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

package jmetest.TutorialGuide;

import java.util.ArrayList;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.scene.state.BlendState.TestFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

/**
 * <code>ExplosionFactory</code>
 * @author Joshua Slack
 * @version $Id: ExplosionFactory.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class ExplosionFactory {
    
    private static ArrayList<ParticleMesh> explosions = new ArrayList<ParticleMesh>();
    private static ArrayList<ParticleMesh> smallExplosions = new ArrayList<ParticleMesh>();
    private static BlendState bs;
    private static TextureState ts;
    private static ZBufferState zstate;

    public static void cleanExplosions() {
        int count = 0;
        for (int x = 0, tSize = explosions.size(); x < tSize; x++) {
            ParticleMesh e = (ParticleMesh)explosions.get(x);
            if (!e.isActive()) {
                if (e.getParent() != null)
                    e.removeFromParent();
                count++;
                if (count > 5) {
                    explosions.remove(x);
                    tSize--;
                }
            }
        }

        int scount = 0;
        for (int x = 0, tSize = smallExplosions.size(); x < tSize; x++) {
            ParticleMesh e = (ParticleMesh)smallExplosions.get(x);
            if (!e.isActive()) {
                if (e.getParent() != null)
                    e.removeFromParent();
                scount++;
                if (scount > 5) {
                    smallExplosions.remove(x);
                    tSize--;
                }
            }
        }
    }

    public static ParticleMesh getExplosion() {
        for (int x = 0, tSize = explosions.size(); x < tSize; x++) {
            ParticleMesh e = (ParticleMesh)explosions.get(x);
            if (!e.isActive()) {
                return e;
            }
        }
        return createExplosion();
    }
    
    private static ParticleMesh createExplosion() {
        ParticleMesh explosion = ParticleFactory.buildParticles("big", 80);
        explosion.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        explosion.setMaximumAngle(FastMath.PI);
        explosion.setSpeed(1f);
        explosion.setMinimumLifeTime(600.0f);
        explosion.setStartSize(3.0f);
        explosion.setEndSize(7.0f);
        explosion.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
        explosion.setEndColor(new ColorRGBA(1.0f, 0.24313726f, 0.03137255f, 0.0f));
        explosion.setControlFlow(false);
        explosion.setInitialVelocity(0.02f);
        explosion.setParticleSpinSpeed(0.0f);
        explosion.setRepeatType(Controller.RT_CLAMP);

        explosion.warmUp(1000);

        explosion.setRenderState(ts);
        explosion.setRenderState(bs);
        explosion.setRenderState(zstate);
        explosion.updateRenderState();
        
        explosions.add(explosion);
        
        return explosion;
    }

    public static ParticleMesh getSmallExplosion() {
        for (int x = 0, tSize = smallExplosions.size(); x < tSize; x++) {
            ParticleMesh e = (ParticleMesh)explosions.get(x);
            if (!e.getParticleController().isActive()) {
                return e;
            }
        }
        return createSmallExplosion();
    }
    
    private static ParticleMesh createSmallExplosion() {
        ParticleMesh explosion = ParticleFactory.buildParticles("small", 40);
        explosion.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        explosion.setMaximumAngle(FastMath.PI);
        explosion.setSpeed(0.7f);
        explosion.setMinimumLifeTime(600.0f);
        explosion.setStartSize(4.0f);
        explosion.setEndSize(8.0f);
        explosion.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
        explosion.setEndColor(new ColorRGBA(1.0f, 0.24313726f, 0.03137255f, 0.0f));
        explosion.setControlFlow(false);
        explosion.setInitialVelocity(0.02f);
        explosion.setParticleSpinSpeed(0.0f);
        explosion.setRepeatType(Controller.RT_CLAMP);

        explosion.warmUp(1000);

        explosion.setRenderState(ts);
        explosion.setRenderState(bs);
        explosion.setRenderState(zstate);
        explosion.updateRenderState();
        
        explosions.add(explosion);
        
        return explosion;
    }

    public static void warmup() {
        DisplaySystem display = DisplaySystem.getDisplaySystem();
        bs = display.getRenderer().createBlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        bs.setDestinationFunction(DestinationFunction.One);
        bs.setTestEnabled(true);
        bs.setTestFunction(TestFunction.GreaterThan);

        ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture(
                ExplosionFactory.class.getClassLoader().getResource(
                        "jmetest/data/texture/flaresmall.jpg")));

        zstate = display.getRenderer().createZBufferState();
        zstate.setEnabled(false);

        for (int i = 0; i < 3; i++)
            createExplosion();
        for (int i = 0; i < 5; i++)
            createSmallExplosion();
    }

}
