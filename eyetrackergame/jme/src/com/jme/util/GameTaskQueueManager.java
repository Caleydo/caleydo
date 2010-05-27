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

package com.jme.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

/**
 * <code>GameTaskQueueManager</code> is just a simple Singleton class allowing
 * easy access to task queues.
 * 
 * @author Joshua Slack
 * @version $Id: GameTaskQueueManager.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public final class GameTaskQueueManager {

    private static final GameTaskQueueManager MANAGER_INSTANCE = new GameTaskQueueManager();
    
    protected final ConcurrentMap<String, GameTaskQueue> managedQueues = new ConcurrentHashMap<String, GameTaskQueue>(2);

    public static GameTaskQueueManager getManager() {
        return MANAGER_INSTANCE ;
    }
    
    private GameTaskQueueManager() {
        addQueue(GameTaskQueue.RENDER, new GameTaskQueue());
        addQueue(GameTaskQueue.UPDATE, new GameTaskQueue());
    }

    public void addQueue(String name, GameTaskQueue queue) {
        managedQueues.put(name, queue);
    }
    
    public GameTaskQueue getQueue(String name) {
        return managedQueues.get(name);
    }

    /**
     * This method adds <code>callable</code> to the queue to be invoked in
     * the update() method in the OpenGL thread. The Future returned may be
     * utilized to cancel the task or wait for the return object.
     * 
     * @param callable
     * @return Future<V>
     */
    
    public <V> Future<V> update(Callable<V> callable) {
        return getQueue(GameTaskQueue.UPDATE).enqueue(callable);
    }
    
    /**
     * This method adds <code>callable</code> to the queue to be invoked in
     * the render() method in the OpenGL thread. The Future returned may be
     * utilized to cancel the task or wait for the return object.
     * 
     * @param callable
     * @return Future<V>
     */
    
    public <V> Future<V> render(Callable<V> callable) {
        return getQueue(GameTaskQueue.RENDER).enqueue(callable);
    }
}
