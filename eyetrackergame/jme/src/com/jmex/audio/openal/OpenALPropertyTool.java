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

import org.lwjgl.openal.AL10;

import com.jmex.audio.player.AudioPlayer;

/**
 * OpenAL utility class - used for keeping code access to openal properties in a
 * single location.
 * 
 * @author Joshua Slack
 * @version $Id: OpenALPropertyTool.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class OpenALPropertyTool {

    public static void applyProperties(AudioPlayer player, OpenALSource source) {
        applyChannelVolume(source, player.getVolume());
        applyChannelPitch(source, player.getPitch());
        applyChannelMaxVolume(source, player.getMaxVolume());
        applyChannelMinVolume(source, player.getMinVolume());
        applyChannelRolloff(source, player.getRolloff());
        applyChannelMaxAudibleDistance(source, player.getMaxDistance());
        applyChannelReferenceDistance(source, player.getRefDistance());
    }

    public static void applyChannelVolume(OpenALSource source, float volume) {
        if (source != null)
            AL10.alSourcef(source.getId(), AL10.AL_GAIN, volume);
    }

    public static void applyChannelMaxVolume(OpenALSource source, float maxVolume) {
        if (source != null)
            AL10.alSourcef(source.getId(), AL10.AL_MAX_GAIN, maxVolume);
    }

    public static void applyChannelMinVolume(OpenALSource source, float minVolume) {
        if (source != null)
            AL10.alSourcef(source.getId(), AL10.AL_MIN_GAIN, minVolume);
    }

    public static void applyChannelRolloff(OpenALSource source, float rolloff) {
        if (source != null)
            AL10.alSourcef(source.getId(), AL10.AL_ROLLOFF_FACTOR, rolloff);
    }

    public static void applyChannelMaxAudibleDistance(OpenALSource source, float maxDistance) {
        if (source != null)
            AL10.alSourcef(source.getId(), AL10.AL_MAX_DISTANCE, maxDistance);
    }

    public static void applyChannelReferenceDistance(OpenALSource source, float refDistance) {
    		if (refDistance == 0) refDistance = 0.0000000001f; // 0 causes issues on some cards and the open al spec shows this value used in division
        if (source != null)
            AL10.alSourcef(source.getId(), AL10.AL_REFERENCE_DISTANCE, refDistance);
    }

    public static void applyChannelPitch(OpenALSource source, float pitch) {
        if (source != null)
            AL10.alSourcef(source.getId(), AL10.AL_PITCH, pitch);
    }

}
