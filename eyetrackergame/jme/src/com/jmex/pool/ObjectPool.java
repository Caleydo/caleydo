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
package com.jmex.pool;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <code>ObjectPool</code> allows re-use of Objects.
 * 
 * @author Matthew D. Hicks
 */
public class ObjectPool<T> {
	private Class<T> c;
	private ConcurrentLinkedQueue<T> queue;
	private ObjectGenerator<T> generator;
	private volatile int total;
	
	public ObjectPool(ObjectGenerator<T> generator, int preAllocate) {
		queue = new ConcurrentLinkedQueue<T>();
		this.generator = generator;
		for (int i = 0; i < preAllocate; i++) {
			queue.offer(newInstance());
		}
	}
	
	public ObjectPool(Class<T> c, int preAllocate) {
		queue = new ConcurrentLinkedQueue<T>();
		this.c = c;
		for (int i = 0; i < preAllocate; i++) {
			queue.offer(newInstance());
		}
	}
	
	protected T newInstance() {
		T t = null;
		if (generator != null) {
			t = generator.newInstance();
		} else if (c != null) {
			try {
				t = c.newInstance();
			} catch(Exception exc) {
				throw new RuntimeException("Unable to instantiate Class: " + c.getCanonicalName(), exc);
			}
		}
		if (t != null) total++;
		return t;
	}
	
	/**
	 * Retrieves the first available object in the pool or creates a new instance
	 * if there are none available.
	 * 
	 * @return
	 * 		T
	 */
	public T get() {
		T t = queue.poll();
		if (t == null) {
			t = newInstance();
		}
		if (generator != null) generator.enable(t);
		return t;
	}
	
	/**
	 * Retrieves the first available object in the pool or returns null if none
	 * are available.
	 * 
	 * @return
	 * 		T
	 * @throws Exception
	 */
	public T request() throws Exception {
		T t = queue.poll();
		if (t != null) {
			if (generator != null) generator.enable(t);
		}
		return t;
	}
	
	/**
	 * Releases the object back into the pool for re-use.
	 * 
	 * @param t
	 * @return
	 * 		boolean
	 */
	public boolean release(T t) {
		if (generator != null) generator.disable(t);
		return queue.offer(t);
	}
	
	/**
	 * Retrieves the number of objects available in the queue.
	 * 
	 * @return
	 * 		int
	 */
	public int available() {
		return queue.size();
	}
	
	/**
	 * Returns the actual number of T's created by this ObjectPool.
	 * 
	 * @return
	 * 		int
	 */
	public int size() {
		return total;
	}
}