/*
 * Copyright (c) 2005-2007 jME Physics 2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of 'jME Physics 2' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
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

package com.jmex.physics.util.states;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.util.Timer;
import com.jme.util.lwjgl.LWJGLTimer;
import com.jmex.game.state.BasicGameState;
import com.jmex.physics.PhysicsSpace;

/**
 * <code>PhysicsGameState</code> provides physics encapsulation into a GameState.
 * 
 * @author Matthew D. Hicks
 */
public class PhysicsMultithreadedGameState extends BasicGameState {
	private PhysicsSpace physics;
	private PhysicsThread thread;
	
	public PhysicsMultithreadedGameState(String name) {
		super(name);
		thread = new PhysicsThread(30);
		physics = thread.getPhysics();
		new Thread(thread).start();
	}
	
	public PhysicsSpace getPhysicsSpace() {
		return physics;
	}
	
	public void update(float tpf) {
		super.update(tpf);
		if (!thread.isEnabled()) {
			thread.setEnabled(true);
		}
	}
	
	public void setActive(boolean active) {
		super.setActive(active);
		if (!active) {
			thread.setEnabled(false);
		}
	}
	
	public void shutdown() {
		thread.shutdown();
	}
	
	public void lock() {
		thread.lock();
	}
	
	public void unlock() {
		thread.unlock();
	}
}

class PhysicsThread implements Runnable {
	private PhysicsSpace physics;
	private long preferredTicksPerFrame;
	private boolean enabled;
	private boolean keepAlive;
	private Timer timer;
	private boolean limitUpdates;
	private Lock updateLock;
	
	public PhysicsThread(int desiredUpdatesPerSecond) {
		physics = PhysicsSpace.create();
		physics.update(0.01f);
		enabled = false;
		keepAlive = true;
		updateLock = new ReentrantLock(true); // Make our lock be fair (first come, first serve)
		
		timer = new LWJGLTimer();
		if (desiredUpdatesPerSecond == -1) {
			limitUpdates = false;
		} else {
			preferredTicksPerFrame = Math.round((float)timer.getResolution() / (float)desiredUpdatesPerSecond);
			limitUpdates = true;
		}
	}
	
	public PhysicsSpace getPhysics() {
		return physics;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void run() {
		// We have to lock it up while we're starting
		lock();
		
		long frameStartTick = 0;
		long frameDurationTicks = 0;
		float tpf;
		while (keepAlive) {
			if (limitUpdates) {
				frameStartTick = timer.getTime();
			}
			timer.update();
			tpf = timer.getTimePerFrame();
			update(tpf);
			if ((limitUpdates) && (preferredTicksPerFrame >= 0)) {
				frameDurationTicks = timer.getTime() - frameStartTick;
				while (frameDurationTicks < preferredTicksPerFrame) {
					long sleepTime = ((preferredTicksPerFrame - frameDurationTicks) * 1000) / timer.getResolution();
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException exc) {
						Logger.getLogger( PhysicsSpace.LOGGER_NAME ).log(Level.SEVERE, "Interrupted while sleeping in fixed-framerate",
										exc);
					}
					frameDurationTicks = timer.getTime() - frameStartTick;
				}
			}
			Thread.yield();
		}
	}
	
	public void update(float tpf) {
		if (!enabled) return;
		// Open the lock up for any work that needs to be done
		unlock();
		// Now lock up again so nothing can happen while we're updating
		lock();
		
		physics.update(tpf);
	}
	
	public void lock() {
		updateLock.lock();
	}
	
	public void unlock() {
		updateLock.unlock();
	}
	
	public void shutdown() {
		keepAlive = false;
	}
}
