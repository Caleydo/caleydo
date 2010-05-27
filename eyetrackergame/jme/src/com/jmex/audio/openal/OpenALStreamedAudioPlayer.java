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
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.openal.AL10;

import com.jme.math.Vector3f;
import com.jme.util.geom.BufferUtils;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.player.StreamedAudioPlayer;
import com.jmex.audio.stream.AudioInputStream;

/**
 * @see StreamedAudioPlayer
 * @author Joshua Slack
 * @version $Id: OpenALStreamedAudioPlayer.java 4833 2010-02-23 07:12:46Z christoph.luder $
 */
public class OpenALStreamedAudioPlayer extends StreamedAudioPlayer {
    private static final Logger logger = Logger.getLogger(OpenALStreamedAudioPlayer.class.getName());

    private static int BUFFER_SIZE = 256 * 1024; // 256 KB
    private int BUFFER_COUNT = 4; // 4 * 256 is ca. 1 MB in total

    private ByteBuffer dataBuffer = BufferUtils.createByteBuffer(BUFFER_SIZE);

    private IntBuffer buffers = BufferUtils.createIntBuffer(BUFFER_COUNT);
    private IntBuffer idBuffer = BufferUtils.createIntBuffer(1);
    private ArrayList<Integer> openBuffers = new ArrayList<Integer>(
            BUFFER_COUNT);

    // a seperate thread that calls update.
    private PlayerThread playerThread = null;

    // set to true when player is initalized.
    private boolean initalized = false;

    private OpenALSource source;

    private boolean isPaused = false;
    private boolean isStopped = false;

    public OpenALStreamedAudioPlayer(AudioInputStream stream, AudioTrack parent) {
        super(stream, parent);
    }

    @Override
    public void init() {
        buffers.clear();
        for (int x = 0; x < BUFFER_COUNT; x++) {
            idBuffer.clear();
            try {
                AL10.alGenBuffers(idBuffer);
            } catch (Exception e) {
                BUFFER_COUNT = x+1;
                break;
            }
            int id = idBuffer.get(0);
            openBuffers.add(id);
            buffers.put(x, id);
        }

        initalized = true;
    }

    /**
     * cleanup the used resources
     */
    public void cleanup() {
        if (initalized) {
            stop();
            for (int x = 0; x < BUFFER_COUNT; x++) {
                idBuffer.clear();
                idBuffer.put(buffers.get(x));
                try {
                    AL10.alDeleteBuffers(idBuffer);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    public void stop() {
    	if (playerThread != null) {
    	    if (source == null)
    	        return;
    	    synchronized (this) {
    	        playerThread.interrupt();
    	        AL10.alSourceStop(source.getId());
    	        isStopped = true;
    	        empty();
    	        playerThread = null;
    	        source = null;
    	    }
    	}
    }
    
    
    /**
     * Called when the stream reached end of file.
     */
    protected void onFinish() {
        getTrack().stop(); // enforce states and event firing
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

            source = ((OpenALSystem) AudioSystem.getSystem()).getNextFreeStreamSource();
            if (source == null) return;
            source.setTrack(getTrack());
            applyTrackProperties();

            try {
                setStream(getStream().makeNew());
            } catch (IOException e) {
                logger.logp(Level.SEVERE, this.getClass().toString(), "play()", "Exception", e);
                return;
            }

            AL10.alSource3f(source.getId(), AL10.AL_POSITION, 0, 0, 0);
            AL10.alSource3f(source.getId(), AL10.AL_VELOCITY, 0, 0, 0);
            AL10.alSource3f(source.getId(), AL10.AL_DIRECTION, 0, 0, 0);
            AL10.alSourcei(source.getId(), AL10.AL_SOURCE_RELATIVE, getTrack()
                    .isRelative() ? AL10.AL_TRUE : AL10.AL_FALSE);

            playInNewThread(200);
        }
    }

    @Override
    public void pause() {
        isPaused = true;
        AL10.alSourcePause(source.getId());
        setPauseTime(System.currentTimeMillis());
    }

    /**
     * Plays the stream. update() must be called regularly so that the data is
     * copied to OpenAl
     */
    public boolean playStream() {
        isStopped = false;
        
        if (isPlaying()) {
            return true;
        }
        
        if (openBuffers.size() > 0) {
            for (int i = 0; i < BUFFER_COUNT; i++) {
                int id = openBuffers.remove(openBuffers.size() - 1);
                if (!stream(id)) {
                    openBuffers.add(id);
                    break;
                }
                idBuffer.put(0, id);
                idBuffer.rewind();
                AL10.alSourceQueueBuffers(source.getId(), idBuffer);
            }
        }

        AL10.alSourcePlay(source.getId());
        setStartTime(System.currentTimeMillis());

        return true;
    }

    /**
     * Plays the track in a newly created thread.
     * 
     * @param updateInterval
     *            at which interval should the thread call update, in
     *            milliseconds.
     */
    public boolean playInNewThread(long updateIntervalMillis) {
        try {
            if (playStream()) {
                playerThread = new PlayerThread(updateIntervalMillis);
                playerThread.start();
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Audio Error!", e);
        }

        return false;
    }

    /**
     * check if the source is playing
     */
    public boolean isPlaying() {
        return source != null && source.getState() == AL10.AL_PLAYING;
    }

    @Override
    public boolean isActive() {
        return source != null
                && (source.getState() == AL10.AL_PLAYING || source.getState() == AL10.AL_PAUSED);
    }

    @Override
    public boolean isStopped() {
        return source != null && source.getState() == AL10.AL_STOPPED;
    }

    /**
     * Copies data from the ogg stream to openAL10. Must be called often to
     * prevent the buffers from starving.
     * 
     * @return true if sound is still playing, false if the end of file is
     *         reached.
     */
    public synchronized boolean update() throws IOException {
        if (isPaused) {
            return true;
        } else if (isStopped) {
            return false;
        }

        boolean active = true;

        unqueueBuffer();

        boolean starved = false;
        // Possibly starved the stream.
        if (openBuffers.size() > 1) {
            starved = true;
        }

        while (openBuffers.size() > 0) {
            int id = openBuffers.remove(0);
            active = stream(id);
            if (active) {
                idBuffer.put(0, id);
                idBuffer.rewind();
                AL10.alSourceQueueBuffers(source.getId(), idBuffer);
                break;
            }
        }

        if (active && starved && !isPlaying())
            AL10.alSourcePlay(source.getId());
        return active;
    }

    

    /**
     * reloads a buffer
     * 
     * @return true if success, false if read failed or end of file.
     */
    protected boolean stream(int buffer) {
        if (isStopped) return false;
        try {
            dataBuffer.clear();
            int bytesRead = getStream().read(dataBuffer, 0,
                    dataBuffer.capacity());
            if (bytesRead >= 0) {
                dataBuffer.rewind();
                dataBuffer.limit(bytesRead);
                int format = AL10.AL_FORMAT_STEREO8;

                boolean mono = getStream().getChannelCount() == 1;

                if (getStream().getDepth() == 8) {
                    format = (mono ? AL10.AL_FORMAT_MONO8
                            : AL10.AL_FORMAT_STEREO8);
                } else if (getStream().getDepth() == 16) {
                    format = (mono ? AL10.AL_FORMAT_MONO16
                            : AL10.AL_FORMAT_STEREO16);
                } else return false;

                AL10.alBufferData(buffer, format, dataBuffer, getStream()
                        .getBitRate());
                return true;
            }
            if (isLoop() && getTrack().isEnabled()) {
                setStream(getStream().makeNew());
                return stream(buffer);
            }
        } catch (IOException e) {
            logger.logp(Level.SEVERE, this.getClass().toString(), "stream(int buffer)", "Exception", e);
        }

        return false;
    }

    /**
     * empties the queue
     */
    protected void empty() {
        int queued = AL10.alGetSourcei(source.getId(), AL10.AL_BUFFERS_QUEUED);
        while (queued-- > 0) {
            AL10.alSourceUnqueueBuffers(source.getId(), idBuffer);
        }
        for (int x = 0; x < BUFFER_COUNT; x++) {
            int id = buffers.get(x);
            if (!openBuffers.contains(id)) {
                openBuffers.add(id);
            }
        }
    }
    
    private void unqueueBuffer() {
        int processed = AL10.alGetSourcei(source.getId(), AL10.AL_BUFFERS_PROCESSED);

        while (processed-- > 0) {
            AL10.alSourceUnqueueBuffers(source.getId(), idBuffer);
            openBuffers.add(idBuffer.get(0));
            idBuffer.rewind();
        }
    }

    /**
     * The thread that updates the sound. 
     * XXX: I am considering abolishing these one-per-sound threads.
     */
    class PlayerThread extends Thread {
        // at what interval update is called.
        long interval;

        /** Creates the PlayerThread */
        PlayerThread(long interval) {
            this.interval = interval;
            setDaemon(true);
            source.setState(AL10.AL_PLAYING);
        }

        /** Calls update at an interval */
        public void run() {
            try {
                while (!isStopped && update() && !isInterrupted()) {
                    sleep(interval);
                }
                while (isActive() && !isInterrupted()) {
                    sleep(interval);
                }
                onFinish();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    @Override
    public void applyTrackProperties() {
        OpenALPropertyTool.applyProperties(this, source);
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
        return getStream().getBitRate();
    }

    @Override
    public int getChannels() {
        return getStream().getChannelCount();
    }

    @Override
    public int getDepth() {
        return getStream().getDepth();
    }
}