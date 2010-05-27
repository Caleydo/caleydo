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

import java.util.ArrayList;
import java.util.List;

import com.jme.input.controls.GameControl;
import com.jme.input.controls.controller.camera.CameraPerspective;
import com.jme.renderer.Camera;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;

/**
 * @author Matthew D. Hicks
 */
public class CameraController extends Controller {
    private static final long serialVersionUID = 1L;
    private Spatial spatial;
	private Camera camera;
	private GameControl control;
	private List<CameraPerspective> perspectives;
	private int currentPerspective;
	private boolean pressed;
	
	private boolean firstUpdate;
	
	public CameraController(Spatial spatial, Camera camera, GameControl control) {
		this.spatial = spatial;
		this.camera = camera;
		this.control = control;
		perspectives = new ArrayList<CameraPerspective>();
		firstUpdate = true;
	}
	
	public void addPerspective(CameraPerspective perspective) {
		perspectives.add(perspective);
	}
	
	public void nextPerspective() {
		perspectives.get(currentPerspective).setActive(camera, spatial, false);
		currentPerspective++;
		if (currentPerspective >= perspectives.size()) {
			currentPerspective = 0;
		}
		perspectives.get(currentPerspective).setActive(camera, spatial, true);
	}
	
	public void update(float time) {
		if (firstUpdate) {
			perspectives.get(currentPerspective).setActive(camera, spatial, true);
			firstUpdate = false;
		}
		if (pressed) {
			if (control.getValue() == 0.0f) {
				pressed = false;
			}
		} else if (control.getValue() > 0.0f) {
			pressed = true;
			nextPerspective();
		}
		if (perspectives.size() > currentPerspective) {
			perspectives.get(currentPerspective).update(camera, spatial, time);
		}
	}
}
