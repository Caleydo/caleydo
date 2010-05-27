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
package com.jme.input.controls;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.jme.system.GameSettings;

/**
 * GameControlManager maintains a mapping of controls. Utilize the update method
 * in order to process events for underlying controls.
 * 
 * Use this directly instead of GameControl for management of an array of controls.
 * 
 * @author Matthew D. Hicks
 */
public class GameControlManager implements Serializable {
	private static final long serialVersionUID = -59765658953779536L;

	private Map<String, GameControl> controls;
	private boolean enabled;
	
	public GameControlManager() {
		controls = new LinkedHashMap<String, GameControl>();
		enabled = true;
	}
	
	public void createControls(String ... names) {
		for (String name : names) {
			addControl(name);
		}
	}
	
	public GameControl addControl(String name) {
		GameControl control = new GameControl(name, this);
		controls.put(name, control);
		return control;
	}
	
	public GameControl getControl(String name) {
		return controls.get(name);
	}
	
	public GameControl removeControl(String name) {
		return controls.remove(name);
	}
	
	public Set<String> getControlNames() {
		return controls.keySet();
	}
	
	public Collection<GameControl> getControls() {
		return controls.values();
	}
	
	public void clearBindings() {
		for (GameControl control : controls.values()) {
			control.clearBindings();
		}
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public static final void save(GameControlManager manager, GameSettings settings) {
    	settings.setObject("GameControls", manager);
    }
    
    public static final GameControlManager load(GameSettings settings) {
        return (GameControlManager)settings.getObject("GameControls", null);
    }
}
