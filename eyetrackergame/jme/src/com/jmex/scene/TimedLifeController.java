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
package com.jmex.scene;

import com.jme.scene.Controller;

/**
 * <code>TimedLifeController</code> provides an easy mechanism for defining a
 * time-lived controller that receives a percentage to the destination life span
 * and then is removed. For example, if you wanted something to fade-in for your
 * game over 5 seconds you could simply create a <code>TimedLifeController</code>
 * that's life is 5.0f and implement <code>updatePercentage</code> to update the
 * current fade-state of the object up to 1.0f.
 * 
 * @author Matthew D. Hicks
 */
public abstract class TimedLifeController extends Controller {
    private static final long serialVersionUID = 1L;
    private float lifeInSeconds;
	private float current;
	
	public TimedLifeController(float lifeInSeconds) {
		this.lifeInSeconds = lifeInSeconds;
		current = -1;
	}
	
	public void update(float tpf) {
		if (current == -1.0f) {
			current = 0.0f;
		} else {
			current += tpf;
			float percentComplete = current / lifeInSeconds;
			if (percentComplete > 1.0f) percentComplete = 1.0f;
			updatePercentage(percentComplete);
			if (percentComplete == 1.0f) {
				remove();
			}
		}
	}
	
	public void remove() {
		setActive(false);
	}
	
	public void reset() {
		current = -1.0f;
		setActive(true);
	}
	
	public void setPercentage(float percentComplete) {
		current = lifeInSeconds * percentComplete;
	}
	
	/**
	 * This method must be implemented for the percentage completion.
	 * The <code>percentComplete</code> begins at 0.0f and ends at 1.0f.
	 * 
	 * @param percentComplete
	 */
	public abstract void updatePercentage(float percentComplete);
}
