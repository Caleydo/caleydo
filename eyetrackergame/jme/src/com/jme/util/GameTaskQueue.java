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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <code>GameTaskQueue</code> is a simple queueing system to enqueue tasks
 * that need to be accomplished in the OpenGL thread and get back a Future
 * object to be able to retrieve a return from the Callable that was passed
 * in.
 * 
 * @author Matthew D. Hicks
 * 
 * @see Future
 * @see Callable
 */
public class GameTaskQueue {
    
    public static final String RENDER = "render";
    public static final String UPDATE = "update";
    
    private final ConcurrentLinkedQueue<GameTask<?>> queue = new ConcurrentLinkedQueue<GameTask<?>>();
    private final AtomicBoolean executeAll = new AtomicBoolean();
    
    /**
     * The state of this <code>GameTaskQueue</code> if it
     * will execute all enqueued Callables on an execute
     * invokation.
     * 
     * @return
     *      boolean
     */
    public boolean isExecuteAll() {
        return executeAll.get();
    }
    
    /**
     * Sets the executeAll boolean value to determine if
     * when execute() is invoked if it should simply execute
     * one Callable, or if it should invoke all. This defaults
     * to false to keep the game moving more smoothly.
     * 
     * @param executeAll
     */
    public void setExecuteAll(boolean executeAll) {
        this.executeAll.set(executeAll);
    }
    
    /**
     * Adds the Callable to the internal queue to invoked and
     * returns a Future that wraps the return. This is useful
     * for checking the status of the task as well as being able
     * to retrieve the return object from Callable asynchronously.
     * 
     * @param <V>
     * @param callable
     * @return
     */
    public <V> Future<V> enqueue(Callable<V> callable) {
        GameTask<V> task = new GameTask<V>(callable);
        queue.add(task);
        return task;
    }
    
    /**
     * This method should be invoked in the update() or render() method
     * inside the main game to make sure the tasks are invoked in the
     * OpenGL thread.
     */
    public void execute() {
        GameTask<?> task = queue.poll();
        do {
            if (task == null) return;
            while (task.isCancelled()) {
                task = queue.poll();
                if (task == null) return;
            }
            task.invoke();
        } while ((executeAll.get()) && ((task = queue.poll()) != null));
    }
}
