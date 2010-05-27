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

package com.jme.util.stat.graph;

import java.util.HashMap;
import java.util.TreeMap;

import com.jme.image.Texture2D;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.TextureRenderer;
import com.jme.renderer.TextureRenderer.Target;
import com.jme.system.DisplaySystem;
import com.jme.util.stat.StatListener;
import com.jme.util.stat.StatType;

/**
 * Base class for graphers.
 * 
 * @author Joshua Slack
 */
public abstract class AbstractStatGrapher implements StatListener {

	protected TextureRenderer texRenderer;
	protected Texture2D tex;
	protected int gWidth, gHeight;

	protected TreeMap<StatType, HashMap<String, Object>> config = new TreeMap<StatType, HashMap<String, Object>>();

	protected boolean enabled = true;

	/**
	 * Must be constructed in the GL thread.
	 */
	public AbstractStatGrapher(int width, int height) {
		this.gWidth = width;
		this.gHeight = height;
		// prepare our TextureRenderer
		texRenderer = DisplaySystem.getDisplaySystem().createTextureRenderer(
				width, height, Target.Texture2D);
		texRenderer.setBackgroundColor(ColorRGBA.black.clone());
	}

	// - set a texture for offscreen rendering
	public void setTexture(Texture2D tex) {
		texRenderer.setupTexture(tex);
		this.tex = tex;
	}

	public TextureRenderer getTexRenderer() {
		return texRenderer;
	}

	public void clearConfig() {
		config.clear();
	}

	public void clearConfig(StatType type) {
		if (config.get(type) != null) {
			config.get(type).clear();
		}
	}

	public void clearConfig(StatType type, String key) {
		if (config.get(type) != null) {
			config.get(type).remove(key);
		}
	}

	public void addConfig(StatType type, HashMap<String, Object> configs) {
		config.put(type, configs);
	}

	public void addConfig(StatType type, String key, Object value) {
		HashMap<String, Object> vals = config.get(type);
		if (vals == null) {
			vals = new HashMap<String, Object>();
			config.put(type, vals);
		}
		vals.put(key, value);
	}

	protected ColorRGBA getColorConfig(StatType type, String configName,
			ColorRGBA defaultVal) {
		HashMap<String, Object> vals = config.get(type);
		if (vals != null && vals.containsKey(configName)) {
			Object val = vals.get(configName);
			if (val instanceof ColorRGBA) {
				return (ColorRGBA) val;
			}
		}
		return defaultVal;
	}

	protected String getStringConfig(StatType type, String configName,
			String defaultVal) {
		HashMap<String, Object> vals = config.get(type);
		if (vals != null && vals.containsKey(configName)) {
			Object val = vals.get(configName);
			if (val instanceof String) {
				return (String) val;
			}
		}
		return defaultVal;
	}

	protected short getShortConfig(StatType type, String configName,
			short defaultVal) {
		HashMap<String, Object> vals = config.get(type);
		if (vals != null && vals.containsKey(configName)) {
			Object val = vals.get(configName);
			if (val instanceof Number) {
				return ((Number) val).shortValue();
			}
		}
		return defaultVal;
	}

	protected int getIntConfig(StatType type, String configName, int defaultVal) {
		HashMap<String, Object> vals = config.get(type);
		if (vals != null && vals.containsKey(configName)) {
			Object val = vals.get(configName);
			if (val instanceof Number) {
				return ((Number) val).intValue();
			}
		}
		return defaultVal;
	}

	protected long getLongConfig(StatType type, String configName,
			long defaultVal) {
		HashMap<String, Object> vals = config.get(type);
		if (vals != null && vals.containsKey(configName)) {
			Object val = vals.get(configName);
			if (val instanceof Number) {
				return ((Number) val).longValue();
			}
		}
		return defaultVal;
	}

	protected float getFloatConfig(StatType type, String configName,
			float defaultVal) {
		HashMap<String, Object> vals = config.get(type);
		if (vals != null && vals.containsKey(configName)) {
			Object val = vals.get(configName);
			if (val instanceof Number) {
				return ((Number) val).floatValue();
			}
		}
		return defaultVal;
	}

	protected double getDoubleConfig(StatType type, String configName,
			double defaultVal) {
		HashMap<String, Object> vals = config.get(type);
		if (vals != null && vals.containsKey(configName)) {
			Object val = vals.get(configName);
			if (val instanceof Number) {
				return ((Number) val).doubleValue();
			}
		}
		return defaultVal;
	}

	protected boolean getBooleanConfig(StatType type, String configName,
			boolean defaultVal) {
		HashMap<String, Object> vals = config.get(type);
		if (vals != null && vals.containsKey(configName)) {
			Object val = vals.get(configName);
			if (val instanceof Boolean) {
				return (Boolean) val;
			}
		}
		return defaultVal;
	}

	public boolean hasConfig(StatType type) {
		return config.containsKey(type) && !config.get(type).isEmpty();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Called when the graph needs to be reset back to the original display
	 * state. (iow, remove all points, lines, etc.)
	 */
	public abstract void reset();
}
