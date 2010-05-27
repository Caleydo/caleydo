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

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.event.TrackStateAdapter;

/**
 * A container for environmental sounds - ambient sound effects that play in
 * head space (as opposed to a 3d location in space.)
 * 
 * @author Joshua Slack
 * @version $Id: EnvironmentalPool.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class EnvironmentalPool {
    private ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
    private ArrayList<ChangeListener> listListeners = new ArrayList<ChangeListener>();

    private float crossfadeoutTime = 3.5f;
    private float crossfadeinTime = 3.5f;

    public EnvironmentalPool() {
    }
    

    public void addTrack(AudioTrack track) {
        track.setType(TrackType.ENVIRONMENT);
        if (!tracks.contains(track)) {
            tracks.add(track);
            fireListChanged();
        }
    }

    public void removeTrack(AudioTrack track) {
        track.stop();
        track.getPlayer().cleanup();
        tracks.remove(track);
        fireListChanged();
    }

    private void fireListChanged() {
        ChangeEvent e = new ChangeEvent(this);
        
        for (int x = 0; x < listListeners.size(); x++) {
            listListeners.get(x).stateChanged(e);
        }
    }

    public ArrayList<AudioTrack> getTrackList() {
        return tracks;
    }

    public void clearTracks() {
        stopAllTracks();
        tracks.clear();
        fireListChanged();
    }

    public void update(float dt) {
        for (int x = tracks.size(); --x >= 0; ) {
            AudioTrack t = tracks.get(x);
            if (t.isEnabled() && !t.isActive()) {
                t.stop();
                if (crossfadeinTime > 0)
                    t.fadeIn(crossfadeinTime, 1.0f);
                t.play();
            } else if (!t.isEnabled() && t.isActive()) {
                if (crossfadeoutTime > 0) {
                    if (t.getVolume() != t.getTargetVolume()) continue; // already fading
                    t.fadeOut(crossfadeoutTime);
                    t.addTrackStateListener(new TrackStateAdapter() {
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
                } else
                    t.stop();
            }
        }
    }

    public void stopAllTracks() {
        for (AudioTrack t : tracks) {
            if (t.isActive()) t.stop();
        }
    }

    public void fadeOutAllTracks(float fadeTime) {
        for (AudioTrack t : tracks) {
            if (t.isActive()) t.fadeOut(fadeTime);
        }
    }


    public void addSongListChangeListener(ChangeListener listener) {
        listListeners.add(listener);
    }

    public void removeSongListChangeListener(ChangeListener listener) {
        listListeners.remove(listener);
    }

    public void clearSongListChangeListeners() {
        listListeners.clear();
    }

    public float getCrossfadeinTime() {
        return crossfadeinTime;
    }

    public void setCrossfadeinTime(float crossfadeinTime) {
        this.crossfadeinTime = crossfadeinTime;
    }

    public float getCrossfadeoutTime() {
        return crossfadeoutTime;
    }

    public void setCrossfadeoutTime(float crossfadeoutTime) {
        this.crossfadeoutTime = crossfadeoutTime;
    }

    public void fadeOutAndClear(float fadeTime) {
        // remove any listeners
        listListeners.clear();
        
        // fade out the current tracks.
        fadeOutAllTracks(fadeTime);

        // remove all tracks.
        tracks.clear();
    }
}
