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
package com.jme.input.controls.controller;

import com.jme.input.controls.GameControl;
import com.jme.scene.Controller;

/**
 * ActionRepeatController allows you to specify the rate at which an action is repeatable
 * and will invoke the supplied Runnable at that repeat rate while the GameControl is being
 * pressed.
 * 
 * @author Matthew D. Hicks
 */
public class ActionRepeatController extends Controller {
	private static final long serialVersionUID = 1L;

	private GameControl control;
	private long rate;
	private Runnable action;
	
	private long lastInvoked;
	
	public ActionRepeatController(GameControl control, long rate, Runnable action) {
		this.control = control;
		this.rate = rate;
		this.action = action;
	}
	
	public void setRate(long rate) {
		this.rate = rate;
	}
	
	public void update(float time) {
		if (control.getValue() > 0.0f) {
			if (System.currentTimeMillis() >= lastInvoked + rate) {
				action.run();
				lastInvoked = System.currentTimeMillis();
			}
		}
	}
}
