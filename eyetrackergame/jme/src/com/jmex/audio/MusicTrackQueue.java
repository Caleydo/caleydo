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

import com.jme.math.FastMath;
import com.jmex.audio.AudioTrack.TrackType;
import com.jmex.audio.event.TrackStateAdapter;

/**
 * A container for sound files to be played in series, similar to a playlist in
 * iTunes or WinAmp.
 * 
 * @author Joshua Slack
 * @version $Id: MusicTrackQueue.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class MusicTrackQueue {

	/**
	 * The repeat mode used with a MusicTrackQueue. (Other repeat settings are
	 * ignored when used with the MusicTrackQueue.)
	 */
	public enum RepeatType {
		/**
		 * Repeat mode is off. Music will stop playing when the end of the queue
		 * is reached.
		 */
		NONE,
		/** Repeat the current song until we are told to stop. */
		ONE,
		/**
		 * Music will start over from the beginning when it reaches the end of
		 * the queue.
		 */
		ALL;
	}

    private RepeatType repeat = RepeatType.ONE;
    private ArrayList<AudioTrack> tracks = new ArrayList<AudioTrack>();
    private int currentTrack = -1;
    private boolean isPlaying = false;
    private ArrayList<ChangeListener> listListeners = new ArrayList<ChangeListener>();
    private ArrayList<ChangeListener> songListeners = new ArrayList<ChangeListener>();
    
    private float crossfadeoutTime = 3.5f;
    private float crossfadeinTime = 3.5f;

    public MusicTrackQueue() {
    }
    
    public void setRepeatType(RepeatType mode) {
        this.repeat = mode;
    }
    
    public RepeatType getRepeatType() {
        return repeat;
    }

    public void addTrack(AudioTrack track) {
        if (track == null) return;
        track.setType(TrackType.MUSIC);
        if (!tracks.contains(track)) {
            tracks.add(track);
            fireListChanged();
        }
    }

    public void removeTrack(AudioTrack track) {
        if (track == null) return;
        track.stop();
        track.getPlayer().cleanup();
        tracks.remove(track);
        if (isPlaying && getCurrentTrack() == track) {
            nextTrack();
        }
        fireListChanged();
    }

    private void fireListChanged() {
        ChangeEvent e = new ChangeEvent(this);
        
        for (int x = 0; x < listListeners.size(); x++) {
            listListeners.get(x).stateChanged(e);
        }
    }
    
    private void fireCurrentSongChanged() {
        ChangeEvent e = new ChangeEvent(this);
        
        for (int x = 0; x < songListeners.size(); x++) {
            songListeners.get(x).stateChanged(e);
        }
    }

    public ArrayList<AudioTrack> getTrackList() {
        return tracks;
    }
    
    public void clearTracks() {
        stop();
        tracks.clear();
        fireListChanged();
    }

    public void play() {
        isPlaying = true;

        if (currentTrack < 0 || currentTrack >= tracks.size()) {
            currentTrack = 0;
        }
        
        if (tracks.size() < 1) {
            isPlaying = false;
            return;  // nothing to play!
        }
        
        AudioTrack track = tracks.get(currentTrack);
        if (crossfadeinTime > 0)
            track.fadeIn(crossfadeinTime, track.getTargetVolume() > 0 ? track.getTargetVolume() : 1.0f);
        else 
            track.setVolume(track.getTargetVolume() > 0 ? track.getTargetVolume() : 1.0f);
        if (!track.isPlaying())
            track.play();
    }

    public void pause() {
        if (currentTrack < 0 || currentTrack >= tracks.size()) {
            currentTrack = 0;
        }
        
        if (tracks.size() < 1) return;  // nothing to play!
        
        AudioTrack track = tracks.get(currentTrack);
        track.pause();
    }

    public void stop() {
        if (currentTrack < 0 || currentTrack >= tracks.size()) {
            return;
        }
        
        if (tracks.size() < 1) return;  // nothing to play!
        
        AudioTrack track = tracks.get(currentTrack);
        track.stop();
        currentTrack = -1;
        isPlaying = false;
    }
    
    public void setCurrentTrack(int currentTrack) {
        setCurrentTrack(currentTrack, true);
    }

    public void setCurrentTrack(AudioTrack track) {
        int i = tracks.indexOf(track);
        if (i >= 0) setCurrentTrack(i);
    }
    
    public void setCurrentTrack(int currentTrack, boolean fadeOut) {
        AudioTrack track = null;
        if (this.currentTrack >= 0 && this.currentTrack < tracks.size())
            track = tracks.get(this.currentTrack);

        this.currentTrack = currentTrack;
        fireCurrentSongChanged();
        
        if (isPlaying && track != null) {
            if (fadeOut && crossfadeoutTime > 0) {
                track.fadeOut(crossfadeoutTime);
                track.addTrackStateListener(new TrackStateAdapter() {
                    @Override
                    public void trackFinishedFade(AudioTrack track) {
                        track.removeTrackStateListener(this);
                        track.stop();
                        track.setVolume(1.0f);
                        track.setTargetVolume(1.0f);
                        fireCurrentSongChanged();
                    }
                    @Override
                    public void trackStopped(AudioTrack track) {
                        track.removeTrackStateListener(this);
                        track.setVolume(1.0f);
                        track.setTargetVolume(1.0f);
                        fireCurrentSongChanged();
                    }
                });
            } else
                track.stop();
            
            if (currentTrack >= 0)
                play();
        }
    }

    public void update(float dt) {
        AudioTrack track = null;
        
        // Get the current track
        if (currentTrack >= 0 && currentTrack < tracks.size())
            track = getTrack(currentTrack);
        
        // If we're playing...
        if (isPlaying) {
            // See what our next track would be.
            int nextTrack = getNextTrack();

            // If we have a current track, with a known duration,
            // let's see if we need to crossfade to next track
            if (track != null&& track.getTotalTime() > 0) {  
                
                // Enforce loop control in our player to work in conjunction with repeat modes.
                if (!track.isLooping() && nextTrack == currentTrack) track.setLooping(true);
                else if (track.isLooping()&& nextTrack != currentTrack) track.setLooping(false);

                // look for time to cross fade to next track.
                if (!track.isLooping()
                        && track.getTargetVolume() != 0 // already fading
                        && track.getTotalTime() - track.getCurrentTime() <= getCrossfadeoutTime()) {
                    setCurrentTrack(nextTrack, true);
                    update(dt);
                    return;
                }
            }

            // If we have a track, but it is stopped, move onto the next one.
            if (track != null && track.isStopped()) {
                int cTrack = currentTrack;
                currentTrack = nextTrack;
                track.stop();
                if (cTrack != currentTrack) {
                    fireCurrentSongChanged();
                } else {
                    return;
                }
                if (currentTrack != -1) {
                    play();
                } else {
                    isPlaying = false;
                    fireCurrentSongChanged();
                }
            // We have a track, and it's not stopped, but also not playing.
            } else if (track != null && !track.isPlaying()) {
                play();
            // We don't have a track, so try to move on to the next one.
            } else if (track == null) {
                nextTrack();
            }
        }
    }

    public int getNextTrack() {
        if (RepeatType.ONE.equals(repeat) && currentTrack < tracks.size())
            return currentTrack;
        
        int newTrack = currentTrack + 1;
        if (newTrack >= tracks.size()) {
            switch (repeat) {
                case NONE:
                    return -1;
                case ALL:
                default:
                    return 0;
            }
        } else
            return newTrack;
    }

    public int getPrevTrack() {
        if (RepeatType.ONE.equals(repeat)) return currentTrack;
        
        int newTrack = currentTrack - 1;
        if (newTrack < 0) {
            switch (repeat) {
                case NONE:
                    return -1;
                case ALL:
                default:
                    return tracks.size()-1;
            }
        } else
            return newTrack;
    }

    public void addSongListChangeListener(ChangeListener listener) {
        listListeners.add(listener);
    }

    public void addCurrentSongChangeListener(ChangeListener listener) {
        songListeners.add(listener);
    }

    public void removeSongListChangeListener(ChangeListener listener) {
        listListeners.remove(listener);
    }

    public void removeCurrentSongChangeListener(ChangeListener listener) {
        songListeners.remove(listener);
    }

    public void clearSongListChangeListeners() {
        listListeners.clear();
    }

    public void clearCurrentSongChangeListeners() {
        songListeners.clear();
    }

    public void nextTrack() {
        setCurrentTrack(getNextTrack());
    }

    public void prevTrack() {
        setCurrentTrack(getPrevTrack());
    }

    public void randomize() {
        stop();
        ArrayList<AudioTrack> newTracks = new ArrayList<AudioTrack>();
        while (!tracks.isEmpty()) {
            int i = (int)(FastMath.nextRandomFloat() * tracks.size());
            newTracks.add(tracks.remove(i));
        }
        tracks.addAll(newTracks);
        fireListChanged();
    }

    public int getCurrentTrackIndex() {
        return currentTrack;
    }

    public AudioTrack getTrack(int index) {
        return tracks.get(index);
    }

    public AudioTrack getCurrentTrack() {
        if (currentTrack < tracks.size() && currentTrack >= 0)
            return tracks.get(currentTrack);
        else
            return null;
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

    public boolean isPlaying() {
        return isPlaying;
    }

    public void fadeOutAndClear(float fadeTime) {
        // fade out the current track.
        AudioTrack t = getCurrentTrack();
        if (t != null) t.fadeOut(fadeTime);

        // remove all tracks.
        tracks.clear();
    }
}
