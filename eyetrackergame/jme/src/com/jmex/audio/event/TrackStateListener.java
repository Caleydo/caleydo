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

package com.jmex.audio.event;

import com.jmex.audio.AudioTrack;

/**
 * @author Joshua Slack
 * @version $Id: TrackStateListener.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public interface TrackStateListener {

    /**
     * Called when a track is played.
     * 
     * @param track
     *            the track that just started playing
     */
    public void trackPlayed(AudioTrack track);

    /**
     * Called when a track is stopped.
     * 
     * @param track
     *            the track that was just stopped
     */
    public void trackPaused(AudioTrack track);

    /**
     * Called when a track is paused.
     * 
     * @param track
     *            the track that was just paused
     */
    public void trackStopped(AudioTrack track);

    /**
     * Called when a fading track hits volume == 0. This is only triggered by a
     * programatic fade, not one in the sound file itself.
     * 
     * @param track
     *            the track that was fading
     */
    public void trackFinishedFade(AudioTrack track);

}
