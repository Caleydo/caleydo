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

package com.jmex.audio.openal;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.openal.AL10;

import com.jme.math.Vector3f;
import com.jmex.audio.AudioBuffer;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.player.MemoryAudioPlayer;

/**
 * @see MemoryAudioPlayer
 * @author Joshua Slack
 * @version $Id: OpenALMemoryAudioPlayer.java 4342 2009-05-13 00:45:51Z mulova $
 */
public class OpenALMemoryAudioPlayer extends MemoryAudioPlayer {
    private static final Logger logger = Logger.getLogger(OpenALMemoryAudioPlayer.class.getName());
    
    private OpenALSource source;

    private boolean isPaused = false;

    public OpenALMemoryAudioPlayer(AudioBuffer buffer, AudioTrack parent) {
        super(buffer, parent);
    }
    
    @Override
    public void init() {
    }

    @Override
    public void cleanup() {
        
    }

    @Override
    public boolean isPlaying() {
        return source != null && source.getState() == AL10.AL_PLAYING;
    }

    @Override
    public boolean isActive() {
        return source != null && (source.getState() == AL10.AL_PLAYING || source.getState() == AL10.AL_PAUSED);
    }

    @Override
    public boolean isStopped() {
        return source != null && source.getState() == AL10.AL_STOPPED;
    }

    @Override
    public void pause() {
        isPaused = true;
        AL10.alSourcePause(source.getId());
        setPauseTime(System.currentTimeMillis());
    }

    @Override
    public void play() {
        synchronized (this) {
            if (isPaused) {
                isPaused = false;
                AL10.alSourcePlay(source.getId());
                setStartTime(getStartTime()+System.currentTimeMillis()-getPauseTime());
                return;
            }
    
            source = ((OpenALSystem) AudioSystem.getSystem()).getNextFreeMemorySource();
            if (source == null) return;
            source.setTrack(getTrack());
            applyTrackProperties();
            
            AL10.alSource3f(source.getId(), AL10.AL_POSITION, 0, 0, 0);
            AL10.alSource3f(source.getId(), AL10.AL_VELOCITY, 0, 0, 0);
            AL10.alSource3f(source.getId(), AL10.AL_DIRECTION, 0, 0, 0);
            AL10.alSourcei(source.getId(), AL10.AL_SOURCE_RELATIVE, getTrack().isRelative() ? AL10.AL_TRUE : AL10.AL_FALSE);
            
            AL10.alSourcei(source.getId(), AL10.AL_BUFFER, ((OpenALAudioBuffer)getBuffer()).getId());
            AL10.alSourcePlay(source.getId());
            setStartTime(System.currentTimeMillis());
        }
    }
    
    @Override
    public void applyTrackProperties() {
        OpenALPropertyTool.applyProperties(this, source);
        if (source != null)
            AL10.alSourcei(source.getId(), AL10.AL_LOOPING, isLoop() ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    @Override
    public void stop() {
        synchronized (this) {
            if (source == null)
                return;
            AL10.alSourceStop(source.getId());
            source = null;
        }
    }

    /**
     * checks OpenAL error state
     */
    protected void check() {
        int error = AL10.alGetError();
        if (error != AL10.AL_NO_ERROR) {
            logger.log(Level.INFO, "OpenAL error was raised. errorCode={0}", error);
        }
    }

    @Override
    public void loop(boolean shouldLoop) {
        super.loop(shouldLoop);
        if (source != null)
            AL10.alSourcei(source.getId(), AL10.AL_LOOPING, shouldLoop ? AL10.AL_TRUE : AL10.AL_FALSE);
    }
    
    @Override
    public void updateTrackPlacement() {
        Vector3f pos = getTrack().getWorldPosition();
        Vector3f vel = getTrack().getCurrVelocity();

        AL10.alSource3f(source.getId(), AL10.AL_POSITION, pos.x, pos.y, pos.z);
        AL10.alSource3f(source.getId(), AL10.AL_VELOCITY, vel.x, vel.y, vel.z);
    }

    @Override
    public void setVolume(float volume) {
        super.setVolume(volume);
        OpenALPropertyTool.applyChannelVolume(source, volume);
    }
    
    @Override
    public void setPitch(float pitch) {
        if (pitch > 0f && pitch <= 2.0f) {
            super.setPitch(pitch);
            OpenALPropertyTool.applyChannelPitch(source, getPitch());
        } else
            logger.warning("Pitch must be > 0 and <= 2.0f");
    }

    @Override
    public void setMaxAudibleDistance(float maxDistance) {
        super.setMaxAudibleDistance(maxDistance);
        OpenALPropertyTool.applyChannelMaxAudibleDistance(source, maxDistance);
    }

    @Override
    public void setMaxVolume(float maxVolume) {
        super.setMaxVolume(maxVolume);
        OpenALPropertyTool.applyChannelMaxVolume(source, maxVolume);
    }

    @Override
    public void setMinVolume(float minVolume) {
        super.setMinVolume(minVolume);
        OpenALPropertyTool.applyChannelMinVolume(source, minVolume);
    }

    @Override
    public void setReferenceDistance(float refDistance) {
        super.setReferenceDistance(refDistance);
        OpenALPropertyTool.applyChannelReferenceDistance(source, refDistance);
    }

    @Override
    public void setRolloff(float rolloff) {
        super.setRolloff(rolloff);
        OpenALPropertyTool.applyChannelRolloff(source, rolloff);
    }

    @Override
    public int getBitRate() {
        return getBuffer().getBitRate();
    }

    @Override
    public int getChannels() {
        return getBuffer().getChannels();
    }

    @Override
    public int getDepth() {
        return getBuffer().getDepth();
    }

    @Override
    public float getLength() {
        return getBuffer().getLength();
    }
}
