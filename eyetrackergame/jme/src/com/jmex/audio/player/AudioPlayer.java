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

package com.jmex.audio.player;

import com.jmex.audio.AudioTrack;

/**
 * @author Joshua Slack
 * @version $Id: AudioPlayer.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public abstract class AudioPlayer {

    private AudioTrack track;
    private float minVolume;
    private float maxVolume = 1.0f;
    private float maxDistance;
    private float refDistance = 1.0f;
    private float rolloff;
    private float volume;
    private float pitch = 1f;
    private long startTime;
    private long pauseTime;
    private boolean loop;

    public AudioPlayer(AudioTrack parent) {
        this.track = parent;
    }

    public abstract void init();

    public abstract void play();

    public abstract void pause();

    public abstract void stop();

    public abstract void cleanup();

    public abstract boolean isPlaying();

    public abstract boolean isActive();

    public abstract boolean isStopped();

    public abstract void updateTrackPlacement();

    public abstract int getChannels();

    public abstract int getBitRate();

    public abstract int getDepth();

    public abstract float getLength();
    
    public abstract void applyTrackProperties();

    public void loop(boolean shouldLoop) {
        loop = shouldLoop;
    }
    
    public boolean isLoop() {
        return loop;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public void setMinVolume(float minVolume) {
        this.minVolume = minVolume;
    }

    public void setMaxVolume(float maxVolume) {
        this.maxVolume = maxVolume;
    }

    public void setMaxAudibleDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setReferenceDistance(float refDistance) {
        this.refDistance = refDistance;
    }

    public void setRolloff(float rolloff) {
        this.rolloff = rolloff;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    
    public float getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public float getRefDistance() {
        return refDistance;
    }

    public void setRefDistance(float refDistance) {
        this.refDistance = refDistance;
    }

    public float getMaxVolume() {
        return maxVolume;
    }

    public float getMinVolume() {
        return minVolume;
    }

    public float getRolloff() {
        return rolloff;
    }

    public float getVolume() {
        return volume;
    }
    
    public float getPitch() {
        return pitch;
    }

    public float getCurrentTime() {
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public void setStartTime(long time) {
        startTime = time;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setPauseTime(long time) {
        pauseTime = time;
    }

    public long getPauseTime() {
        return pauseTime;
    }

}
