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

package com.jmex.audio.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.event.TrackLoadListener;

/**
 * Queues up loading threads to be loaded one by one to reduce load on the
 * system.
 * 
 * @author Joshua Slack
 * @version $Id: SoundLoadingQueue.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class SoundLoadingQueue {
    private static final Logger logger = Logger
            .getLogger(SoundLoadingQueue.class.getName());

    private static HashMap<URI, Vector<TrackLoadListener>> queue = new HashMap<URI, Vector<TrackLoadListener>>();
    private static QueueProcessorThread processThread = null;

    public synchronized static void loadSound(URI loc,
            TrackLoadListener callBack) {
        synchronized (queue) {
            // add our info to the queue
            Vector<TrackLoadListener> listeners = queue.get(loc);
            if (listeners == null) {
                listeners = new Vector<TrackLoadListener>();
                queue.put(loc, listeners);
            }
            listeners.add(callBack);
        }

        loadQueue();
    }

    private static void loadQueue() {
        // if the processing thread is not active, activate it.
        if (processThread == null || !processThread.isAlive()) {
            processThread = new QueueProcessorThread();
            processThread.start();
        }
    }

    private static void loadNextSound(URI loc) {
        // pull out our current list of callbacks
        Vector<TrackLoadListener> listeners = null;
        synchronized (queue) {
            listeners = queue.remove(loc);
        }
        
        AudioTrack sound;
        try {
            sound = AudioSystem.getSystem().createAudioTrack(loc.toURL(), false);
        } catch (MalformedURLException e) {
            logger.log(Level.WARNING, "Error creating AutioTrack", e);
            return;
        }

        // notify any callbacks that this track is ready for use
        if (listeners != null) {
            for (TrackLoadListener callBack : listeners)
                callBack.trackLoaded(sound);
        }
    }

    static class QueueProcessorThread extends Thread {
        private static final long PROCESSOR_SLEEP = 250;

        QueueProcessorThread() {
            setDaemon(true);
        }

        @Override
        public void run() {
            // This value can change during runtime, so we'll check at the
            // beginning of each thread start.
            int processorsNum = Runtime.getRuntime().availableProcessors();
            
            // while we have keys to process
            while (queue.size() > 0) {
                // sleep for a bit before we move on to the next one.
                try {
                    sleep(PROCESSOR_SLEEP);
                } catch (InterruptedException e) {
                    logger.logp(Level.SEVERE, this.getClass().toString(), "run()", "Exception",
                                    e);
                }

                // grab as many as we have cpus to handle
                Set<URI> keys = queue.keySet();
                if (keys != null) {
                    Object[] keyArray = keys.toArray();
                    for (int i = 0; i < processorsNum && i < keys.size(); i++) {
                        URI soundURL = (URI) keyArray[i];
                        // load the sound
                        loadNextSound(soundURL);
                    }
                }
            }
        }
    }
}
