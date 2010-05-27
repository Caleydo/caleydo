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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.audio.event.TrackStateListener;
import com.jmex.audio.player.AudioPlayer;

/**
 * Represents a sound file. 
 * @author Joshua Slack
 * @version $Id: AudioTrack.java 4342 2009-05-13 00:45:51Z mulova $
 */
public abstract class AudioTrack {
    private static final Logger logger = Logger.getLogger(AudioTrack.class
            .getName());

    public enum Format {
        WAV,
        OGG;
    }

    public enum TrackType {
        /** Sound track **/
        MUSIC,
        /** non-positional sound, generally ambient and looped **/
        ENVIRONMENT,
        /** 3d positional sound effect **/
        POSITIONAL,
        /** non-positional sound effect **/
        HEADSPACE
    }

    private float volume = 1.0f;
    private float targetVolume = 1.0f;
    private float volumeChangeRate = .286f;
    private AudioPlayer player = null;
    private boolean relative = false;

    private Vector3f position = new Vector3f();
    private Vector3f lastPosition = new Vector3f();

    private Vector3f currVelocity = new Vector3f();
    private Spatial trackedSpatial;

    private float maxAudibleDistance = 0;
    private float referenceDistance = 0;
    private float rolloff = 0;
    private float pitch = 1.0f;
    private float maxVolume = 1.0f;
    private float minVolume = 0;
    private URL resource = null;
    private boolean streaming;
    private boolean enabled = true;
    private boolean isStopped = false;
    private AudioTrack.TrackType type;

    private ArrayList<TrackStateListener> trackListeners = new ArrayList<TrackStateListener>();

    
    public AudioTrack(URL resource, boolean streaming) {
        this.resource = resource;
        this.streaming = streaming;
        this.type = TrackType.MUSIC;
    }

    public void pause() {
        if (enabled) {
            player.pause();
            fireTrackPaused();
        }
    }

    public void play() {
        if (enabled) {
            try {
                isStopped = false;
                // init from current volume.
                player.setVolume(getVolume());

                // Play!
                player.play();
                fireTrackPlayed();
            } catch (Exception e) {
                logger.logp(Level.SEVERE, this.getClass().toString(), "play()", "Exception", e);
            }
        }
    }

    public void stop() {
        if (!isStopped) {
            isStopped = true;
            player.stop();
            fireTrackStopped();
        }
    }

    public void setLooping(boolean shouldLoop) {
        player.loop(shouldLoop);
    }

    private void fireTrackPlayed() {
        for (int x = 0; x < trackListeners.size(); x++) {
            trackListeners.get(x).trackPlayed(this);
        }
    }

    private void fireTrackPaused() {
        for (int x = 0; x < trackListeners.size(); x++) {
            trackListeners.get(x).trackPaused(this);
        }
    }

    private void fireTrackStopped() {
        for (int x = 0; x < trackListeners.size(); x++) {
            trackListeners.get(x).trackStopped(this);
        }
    }

    private void fireFinishedFade() {
        for (int x = 0; x < trackListeners.size(); x++) {
            trackListeners.get(x).trackFinishedFade(this);
        }
    }

    public void addTrackStateListener(TrackStateListener listener) {
        trackListeners.add(listener);
    }

    public void removeTrackStateListener(TrackStateListener listener) {
        trackListeners.remove(listener);
    }

    public void clearTrackStateListeners() {
        trackListeners.clear();
    }

    public boolean isLooping() {
        return player.isLoop();
    }

    public void unmute() {
        setVolume(volume);
    }

    public void mute() {
        if (enabled)
            setVolume(0.0f);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        if (volume > 1.0f)
            volume = 1.0f;
        this.volume = volume;
        player.setVolume(volume);
    }

    public void fadeOut(float time) {
        targetVolume = 0;
        volumeChangeRate = (volume - targetVolume) / time;
    }

    public void fadeIn(float time, float maxVolume) {
        setVolume(0);
        targetVolume = maxVolume;
        volumeChangeRate = maxVolume / time;
    }
    
    public AudioPlayer getPlayer() {
        return player;
    }

    public void setPlayer(AudioPlayer buffer) {
        this.player = buffer;
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public boolean isActive() {
        return player.isActive();
    }

    public boolean isStopped() {
        return player.isStopped();
    }

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    public Vector3f getWorldPosition() {
        return position;
    }

    public float getCurrentTime() {
        return player.getCurrentTime();
    }

    public void setWorldPosition(Vector3f position) {
        setWorldPosition(position.x, position.y, position.z);
    }

    public void setWorldPosition(float x, float y, float z) {
        lastPosition.set(this.position);
        this.position.set(x, y, z);
    }

    public Vector3f getCurrVelocity() {
        return currVelocity;
    }

    public void setCurrVelocity(Vector3f currVelocity) {
        this.currVelocity.set(currVelocity);
    }

    public void track(Spatial spat) {
        this.trackedSpatial = spat;
        spat.updateWorldVectors();
        update(1);
    }

    public void update(float dt) {
        if (dt < FastMath.FLT_EPSILON)
            dt = FastMath.FLT_EPSILON;
        
        // Do volume changes:
        if (volume != targetVolume) {
            if (volume < targetVolume) {
                volume += volumeChangeRate * dt;
                if (volume > targetVolume) volume = targetVolume;
            } else {
                volume -= volumeChangeRate * dt;
                if (volume < targetVolume) volume = targetVolume;
            }
            if (volume < 0 || volume > 1) volume = targetVolume;
            setVolume(volume);
            if (volume == targetVolume) {
                fireFinishedFade();
            }
        }

        if (!isPlaying()) {
            return;
        }
        
        // XXX: do culling here. If outside the max audible distance, stop the
        // sound and return the resources.
        
        if (trackedSpatial != null) {

            // update position
            setWorldPosition(trackedSpatial.getWorldTranslation());

            // update instantaneous velocity
            currVelocity.set(getWorldPosition()).subtractLocal(lastPosition)
                    .divideLocal(
                            dt * AudioSystem.getSystem().getUnitsPerMeter());

        }

        player.updateTrackPlacement();
    }

    public float getMaxAudibleDistance() {
        return maxAudibleDistance;
    }

    public void setMaxAudibleDistance(float maxDistance) {
        this.maxAudibleDistance = maxDistance;
        player.setMaxAudibleDistance(maxDistance);
    }
   
    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        player.setPitch(pitch);
    }

    public float getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(float maxVolume) {
        this.maxVolume = maxVolume;
        player.setMaxVolume(maxVolume);
    }

    public float getMinVolume() {
        return minVolume;
    }

    public void setMinVolume(float minVolume) {
        this.minVolume = minVolume;
        player.setMinVolume(minVolume);
    }

    public float getReferenceDistance() {
        return referenceDistance;
    }

    public void setReferenceDistance(float refDistance) {
        this.referenceDistance = refDistance;
        player.setReferenceDistance(refDistance);
    }

    public float getRolloff() {
        return rolloff;
    }

    public void setRolloff(float rolloff) {
        this.rolloff = rolloff;
        player.setRolloff(rolloff);
    }

    // sets for gain at max distance to be -18 db.
    public void autosetRolloff() {
        float r = (float)(Math.pow(10, 19f/20f) * getReferenceDistance());
        r /= (getMaxAudibleDistance() - getReferenceDistance());
        setRolloff(r);
    }

    public URL getResource() {
        return resource;
    }

    public void setResource(URL resource) {
        this.resource = resource;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isStreaming() {
        return streaming;
    }

    public float getTargetVolume() {
        return targetVolume;
    }

    public void setTargetVolume(float targetVolume) {
        this.targetVolume = targetVolume;
    }

    public float getVolumeChangeRate() {
        return volumeChangeRate;
    }

    public void setVolumeChangeRate(float volumeChangeRate) {
        this.volumeChangeRate = volumeChangeRate;
    }

    public float getTotalTime() {
        return player.getLength();
    }

    public AudioTrack.TrackType getType() {
        return type;
    }
    
    public void setType(AudioTrack.TrackType type) {
        this.type = type;
    }
    
    public void release() {
        AudioSystem.getSystem().releaseTrack(this);
        clearTrackStateListeners();
    }
}