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

package com.jmex.audio;

import java.net.URL;

import com.jmex.audio.openal.OpenALSystem;

/**
 * Main entry point for accessing the features and functionality of the
 * com.jmex.audio package.
 * 
 * @author Joshua Slack
 * @version $Id: AudioSystem.java 4342 2009-05-13 00:45:51Z mulova $
 */
public abstract class AudioSystem {

    private static AudioSystem system;

    private MusicTrackQueue musicQueue = new MusicTrackQueue();
    private EnvironmentalPool envPool = new EnvironmentalPool();
    private float unitsPerMeter = 10;
    private boolean muted = false;

    /**
     * Singleton access to the audio system. FIXME: Currently hardcoded to
     * create an openal system.
     * 
     * @return the singleton audio system
     */
    public static synchronized AudioSystem getSystem() {
        if (system == null)
            system = new OpenALSystem();
        return system;
    }

    public abstract Ear getEar();
    public abstract void update();
    public abstract AudioTrack createAudioTrack(URL resource, boolean stream);
    public abstract AudioTrack createAudioTrack(String resource, boolean stream);
    public abstract void releaseTrack(AudioTrack track);
    
    public void mute() {
        muted = true;
    }
    public void unmute() {
        muted = false;
    }

    /**
     * Set the master volume.
     * @param gain 1.0f is default.
     */
    public abstract void setMasterGain(float gain);
    
    /**
     * Sets the degree of doppler applied.
     * @param amount multiplying factor.  1.0f is default.
     */
    public abstract void setDopplerFactor(float amount);
    
    /**
     * Sets the speed of sound using gl units per second
     * @param unitsPerSecond
     */
    public abstract void setSpeedOfSound(float unitsPerSecond);

    public static boolean isCreated() {
        return system != null;
    }

    public MusicTrackQueue getMusicQueue() {
        return musicQueue;
    }

    public EnvironmentalPool getEnvironmentalPool() {
        return envPool;
    }

    public float getUnitsPerMeter() {
        return unitsPerMeter;
    }

    public void setUnitsPerMeter(float toMeterValue) {
        this.unitsPerMeter = toMeterValue;
    }

    public void cleanup() {
        system = null;
    }

    public void fadeOutAndClear(float fadeTime) {
        if (musicQueue != null)
            musicQueue.fadeOutAndClear(fadeTime);
        if (envPool != null)
            envPool.fadeOutAndClear(fadeTime);
    }

    public boolean isMuted() {
        return muted;
    }    
}
