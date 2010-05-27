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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jmex.audio.event.TrackStateAdapter;

/**
 * Experimental temporary tracking class for AudioTracks, automating the
 * decision as to whether or not to play/stop a track. WARNING: This class is
 * subject to heavy changes and/or removal in the future. You've been warned. :)
 * 
 * @author Joshua Slack
 * @version $Id: RangedAudioTracker.java 4295 2009-04-29 08:08:56Z mulova $
 */
public class RangedAudioTracker {
    private static final Logger logger = Logger.getLogger(RangedAudioTracker.class.getName());

    // XXX: Would be cool to eventually put instances of this class in a tree
    // (bsp, octree maybe?) of some sort

    private AudioTrack track;
    private float playRangeSQ;
    private Vector3f position = new Vector3f();
    private Spatial toTrack = null; 
    private float stopRangeSQ;
    private boolean useCharacterPosition;
    private boolean trackIn3D = false;
    private float fadeTime;
    private float maxVolume = 1.0f;

    public RangedAudioTracker(AudioTrack track) {
        this(track, 80, 100);
    }

    public RangedAudioTracker(AudioTrack track, float playRange,
            float stopRange) {
        setAudioTrack(track);
        setPlayRange(playRange);
        setStopRange(stopRange);
        fadeTime = 3.5f;
        useCharacterPosition = true;
    }

    public RangedAudioTracker(AudioTrack track, float playRange,
            float stopRange, Spatial toTrack) {
        setAudioTrack(track);
        setPlayRange(playRange);
        setStopRange(stopRange);
        fadeTime = 3.5f;
        useCharacterPosition = true;
        this.toTrack = toTrack;
    }

    public void checkTrackAudible(Vector3f from) {
        boolean shouldStop = false, shouldPlay = false;
        
        // update position as needed
        if (toTrack != null) {
            setPosition(toTrack.getWorldTranslation());
        }
        
        float distSQ = getDistanceSquared(from);
        if (!getAudioTrack().isPlaying()) {
            if (distSQ <= playRangeSQ) {
                shouldPlay = true;
            }
        } else { // track is playing
            if (distSQ >= stopRangeSQ) {
                shouldStop = true;
            }
        }

        if (!shouldStop && !shouldPlay) {
            return;
        }
        
        switch (getAudioTrack().getType()) {
            case MUSIC:
                MusicTrackQueue q = AudioSystem.getSystem().getMusicQueue();
                q.addTrack(getAudioTrack());

                if (shouldPlay && !(q.isPlaying() && q.getCurrentTrack() == getAudioTrack())) {
                    logger.log(Level.INFO, "I should start playing music: {0}",
                             getAudioTrack().getResource());
                    q.setCurrentTrack(getAudioTrack());
                } else if (shouldStop) {
                    // already fading!  Probably coming in or out.  Ignore.
                    if (getAudioTrack().getTargetVolume() != getAudioTrack().getVolume()) break;

                    logger.log(Level.INFO, "I should stop playing music: {0}",
                            getAudioTrack().getResource());
                    if (q.getCurrentTrack() == getAudioTrack())
                        q.setCurrentTrack(-1);
                    else
                        getAudioTrack().stop();
                }
                break;
            case ENVIRONMENT:
                AudioSystem.getSystem().getEnvironmentalPool().addTrack(getAudioTrack());
                if (shouldPlay) {
                    getAudioTrack().setEnabled(true);
                    logger.info("I should start playing environment: "
                            + getAudioTrack().getResource());
                } else if (shouldStop) {
                    // already fading!
                    if (getAudioTrack().getTargetVolume() != getAudioTrack().getVolume())
                        break;
                    
                    getAudioTrack().setEnabled(false);
                    logger.info("I should stop playing environment: "
                            + getAudioTrack().getResource());
                }
                break;
            case HEADSPACE:
            case POSITIONAL:
                if (shouldPlay) {
                    getAudioTrack().fadeIn(fadeTime, maxVolume);
                    getAudioTrack().play();
                    logger.info("I should start playing sound: "
                            + getAudioTrack().getResource());
                } else if (shouldStop) {
                    // already fading!
                    if (getAudioTrack().getTargetVolume() != getAudioTrack().getVolume()) break;
                    
                    getAudioTrack().fadeOut(fadeTime);
                    logger.info("I should stop playing sound: "
                            + getAudioTrack().getResource());
                    getAudioTrack().addTrackStateListener(new TrackStateAdapter() {
                        @Override
                        public void trackFinishedFade(AudioTrack track) {
                            track.removeTrackStateListener(this);
                            track.stop();
                            track.setVolume(1.0f);
                            track.setTargetVolume(1.0f);
                        }

                        @Override
                        public void trackStopped(AudioTrack track) {
                            track.removeTrackStateListener(this);
                            track.setVolume(1.0f);
                            track.setTargetVolume(1.0f);
                        }
                    });
                }
                break;
        }
    }

    private float getDistanceSquared(Vector3f from) {
        if (trackIn3D)
            return position.distanceSquared(from);
        else {
            double dx = position.x - from.x;
            double dy = position.y - from.y;
            return (float) (dx * dx + dy * dy);
        }
    }

    public float getPlayRangeSquared() {
        return playRangeSQ;
    }

    public void setPlayRangeSquared(float playRangeSQ) {
        this.playRangeSQ = playRangeSQ;
    }

    public float getPlayRange() {
        return FastMath.sqrt(playRangeSQ);
    }

    public void setPlayRange(float playRange) {
        this.playRangeSQ = playRange * playRange;
        getAudioTrack().setMaxAudibleDistance(playRange);
        getAudioTrack().setReferenceDistance(playRange / 10f);
        getAudioTrack().setRolloff(.5f);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
        if (getAudioTrack().getType().equals(AudioTrack.TrackType.POSITIONAL)) {
            getAudioTrack().setWorldPosition(position);
        }
    }

    public float getStopRangeSquared() {
        return stopRangeSQ;
    }

    public void setStopRangeSquared(float stopRangeSQ) {
        this.stopRangeSQ = stopRangeSQ;
    }

    public float getStopRange() {
        return FastMath.sqrt(stopRangeSQ);
    }

    public void setStopRange(float stopRange) {
        this.stopRangeSQ = stopRange * stopRange;
    }

    public AudioTrack getAudioTrack() {
        return track;
    }

    public void setAudioTrack(AudioTrack track) {
        this.track = track;
    }

    public boolean isUseCharacterPosition() {
        return useCharacterPosition;
    }

    public void setUseCharacterPosition(boolean useCharacterPosition) {
        this.useCharacterPosition = useCharacterPosition;
    }

    public float getFadeTime() {
        return fadeTime;
    }

    public void setFadeTime(float fadeTime) {
        this.fadeTime = fadeTime;
    }

    public boolean isTrackIn3D() {
        return trackIn3D;
    }

    public void setTrackIn3D(boolean trackIn3D) {
        this.trackIn3D = trackIn3D;
    }

    public Spatial getToTrack() {
        return toTrack;
    }

    public void setToTrack(Spatial toTrack) {
        this.toTrack = toTrack;
    }

    public float getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(float maxVolume) {
        this.maxVolume = maxVolume;
    }

}
