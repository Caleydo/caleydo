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

import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.OpenALException;

import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.stream.AudioInputStream;
import com.jmex.audio.util.AudioLoader;

/**
 * @see AudioSystem
 * @author Joshua Slack
 * @version $Id: OpenALSystem.java 4342 2009-05-13 00:45:51Z mulova $
 */
public class OpenALSystem extends AudioSystem {
    private static final Logger logger = Logger.getLogger(OpenALSystem.class.getName());

    private static final long MAX_MEMORY = 16 * 1024 * 1024; // 16 MB
    private OpenALEar ear;
    private LinkedList<OpenALSource> memorySourcePool = new LinkedList<OpenALSource>();
    private LinkedList<OpenALSource> streamSourcePool = new LinkedList<OpenALSource>();
    private static int MAX_SOURCES = 32;
    private Map<String, OpenALAudioBuffer> memoryPool = Collections
            .synchronizedMap(new LinkedHashMap<String, OpenALAudioBuffer>(16,
                    .75f, true));
    private static LinkedList<OpenALAudioTrack> streamPool =  new LinkedList<OpenALAudioTrack>();
    private long held = 0L;
    private long lastTime = System.currentTimeMillis();
    private float lastMasterGain = -1f;
    private float masterGain = 1.0f;

    public OpenALSystem() {
        ear = new OpenALEar();
        try {
            AL.create();
            AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE);
            setupSourcePool();
        } catch (Exception e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "OpenALSystem()", "Exception",
                    e);
        }
    }

    private void setupSourcePool() {
        IntBuffer alSources = BufferUtils.createIntBuffer(1);
        try {
            for (int x = 0; x < MAX_SOURCES; x++) {
                alSources.clear();
                AL10.alGenSources(alSources);
                OpenALSource source = new OpenALSource(alSources.get(0));
                memorySourcePool.add(source);
                alSources.clear();
                AL10.alGenSources(alSources);
                source = new OpenALSource(alSources.get(0));
                streamSourcePool.add(source);
            }
        } catch (OpenALException e) {
            MAX_SOURCES = memorySourcePool.size();
        }
        logger.log(Level.INFO, "max source channels: {0}", MAX_SOURCES);
    }

    @Override
    public OpenALEar getEar() {
        return ear;
    }

    @Override
    public void update() {
        synchronized(this) {
            if (!AL.isCreated()) return;

            long thisTime = System.currentTimeMillis();
            float dt = (thisTime - lastTime) / 1000f; 
            lastTime  = thisTime;
        
            try {
                for (int x = 0; x < MAX_SOURCES; x++) {
                    OpenALSource src = memorySourcePool.get(x);
                    src.setState(AL10.alGetSourcei(src.getId(), AL10.AL_SOURCE_STATE));
                    if (src.getState() == AL10.AL_PLAYING) src.getTrack().update(dt);
                    src = streamSourcePool.get(x);
                    src.setState(AL10.alGetSourcei(src.getId(), AL10.AL_SOURCE_STATE));
                    if (src.getState() == AL10.AL_PLAYING) src.getTrack().update(dt);
                }
                ear.update(dt);
            } catch (Exception e) {
                logger.logp(Level.SEVERE, this.getClass().toString(), "update()", "Exception", e);
            }
            try {
                getMusicQueue().update(dt);
            } catch (Exception e) {
                logger.logp(Level.SEVERE, this.getClass().toString(), "update()", "Exception", e);
                try {
                    getMusicQueue().clearTracks();
                } catch (Exception ex) {
                    logger.logp(Level.SEVERE, this.getClass().toString(), "update()", "Exception", ex);
                }
            }
            try {
                getEnvironmentalPool().update(dt);
            } catch (Exception e) {
                logger.logp(Level.SEVERE, this.getClass().toString(), "update()", "Exception", e);
                try {
                    getEnvironmentalPool().clearTracks();
                } catch (Exception ex) {
                    logger.logp(Level.SEVERE, this.getClass().toString(), "update()", "Exception", ex);
                }
            }
        }
    }

    public OpenALSource getNextFreeMemorySource() {
        return getNextFreeSource(memorySourcePool);
    }
    
    public OpenALSource getNextFreeStreamSource() {
        return getNextFreeSource(streamSourcePool);
    }
    
    private OpenALSource getNextFreeSource(LinkedList<OpenALSource> pool) {
        synchronized(this) {
            for (int x = 0; x < MAX_SOURCES; x++) {
                OpenALSource src = pool.get(x);
                if (isAvailableState(src.getState())) {
                    pool.remove(x);
                    pool.add(src);
                    return src;
                }
            }
        }
        return null;
    }
    
    private boolean isAvailableState(int state) {
        if (state != AL10.AL_PLAYING && state != AL10.AL_PAUSED && state != -1)
            return true;
        return false;
    }

    @Override
    public OpenALAudioTrack createAudioTrack(URL resource, boolean stream) {
        synchronized(this) {
            if (resource == null) {
                logger.warning("Tried to load null audio file.");
                return null;
            }
            String urlString = resource.toString();
            if (!stream) {
                // look for it in memory
                OpenALAudioBuffer buff = memoryPool.get(urlString);
                if (buff == null) {
                    buff = OpenALAudioBuffer.generateBuffer();
                    try {
                        AudioLoader.fillBuffer(buff, resource);
                    } catch (IOException e) {
                        logger.logp(Level.SEVERE, this.getClass().toString(),
                                "createAudioTrack(URL resource, boolean stream)", "Exception", e);
                        return null;
                    }
    
                    held += buff.getData().capacity();
                    memoryPool.put(urlString, buff);
                    if (held > MAX_MEMORY) {
                        Object[] keys = memoryPool.keySet().toArray();
                        Object[] values = memoryPool.values().toArray();
                        int i = keys.length - 1;
                        while (held > MAX_MEMORY && i >= 0) {
                            OpenALAudioBuffer tBuff = (OpenALAudioBuffer) values[i];
                            held -= tBuff.getData().capacity();
                            memoryPool.remove(keys[i]);
                        }
                    }
                }
                // put us at the end!  :)
                return new OpenALAudioTrack(resource, buff);
            }
            return getStreamedTrack(resource);
        }
    }
    
    public static OpenALAudioTrack getStreamedTrack(URL resource) {
        OpenALAudioTrack track = null;
        if (streamPool.size() > 0) {
            track = streamPool.remove();
            track.setResource(resource);
        } else {
            track = new OpenALAudioTrack(resource, true); 
        }
        return track;
    }
    
    @Override
    public void releaseTrack(AudioTrack track) {
        if (track == null || !(track instanceof OpenALAudioTrack))
            return;
        track.stop();
        
        if (track.getPlayer() instanceof OpenALStreamedAudioPlayer) {
            streamPool.add((OpenALAudioTrack)track);
            OpenALStreamedAudioPlayer player = (OpenALStreamedAudioPlayer) track.getPlayer();
            AudioInputStream stream = player.getStream();
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Stream is not closed.", e);
            }
            player.setStream(null);
        }
    }

    @Override
    public OpenALAudioTrack createAudioTrack(String resourceStr, boolean stream) {
        URL resource = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_AUDIO, resourceStr);
        if (resource == null) {
            logger.log(Level.WARNING, "Could not locate audio file: {0}", resourceStr);
            return null;
        }
        return createAudioTrack(resource, stream);
    }

    @Override
    public void setMasterGain(float gain) {
        masterGain = gain;
        AL10.alListenerf(AL10.AL_GAIN, gain);
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        synchronized(this) {
            if (getMusicQueue() != null) {
                getMusicQueue().clearTracks();
            }
            if (getEnvironmentalPool() != null) {
                getEnvironmentalPool().clearTracks();
            }
            AL.destroy();
        }
        memorySourcePool.clear();
        streamSourcePool.clear();
        
        for (int i = 0; i < streamPool.size(); i++) {
            streamPool.get(i).getPlayer().cleanup();
        }
        memoryPool.clear();
        streamPool.clear();
    }

    @Override
    public void setDopplerFactor(float amount) {
        AL10.alDopplerFactor(amount);

    }

    @Override
    public void setSpeedOfSound(float unitsPerSecond) {
        AL10.alDopplerVelocity(unitsPerSecond);
    }

    @Override
    public void mute() {
        super.mute();
        
        lastMasterGain = masterGain;
        setMasterGain(0);
    }

    @Override
    public void unmute() {
        if (lastMasterGain == -1) {
            return;
        }
        super.unmute();

        setMasterGain(lastMasterGain);
    }
}