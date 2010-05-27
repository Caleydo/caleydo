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
package com.jme.system;

import java.io.IOException;

/**
 * <code>GameSettings</code> offers an abstraction from the internals of getting/setting
 * settings for a game.
 * 
 * @author Matthew D. Hicks
 */
public interface GameSettings {
    /* Most values for settingsWidgetImage are purposefully missing from this
     * interface, because (1) no DEFAULT_ because we want to allow for NO
     * image; and (2) we do not want end-users to be able to change images
     * in their session .properties file.
     */
    String getDefaultSettingsWidgetImage();

    /**
     * The default width, used if there is a problem with the properties file.
     */
    int DEFAULT_WIDTH = 640;
    /**
     * The default height, used if there is a problem with the properties file.
     */
    int DEFAULT_HEIGHT = 480;
    /**
     * The default depth, used if there is a problem with the properties file.
     */
    int DEFAULT_DEPTH = 16;
    /**
     * The default frequency, used if there is a problem with the properties
     * file.
     */
    int DEFAULT_FREQUENCY = 60;
    /**
     * The default fullscreen flag, used if there is a problem with the
     * properties file.
     */
    boolean DEFAULT_FULLSCREEN = false;
    /**
     * The default renderer flag, used if there is a problem with the
     * properties file.
     */
    String DEFAULT_RENDERER = "LWJGL";

    boolean DEFAULT_VERTICAL_SYNC = true;
    int DEFAULT_DEPTH_BITS = 8;
    int DEFAULT_ALPHA_BITS = 0;
    int DEFAULT_STENCIL_BITS = 0;
    int DEFAULT_SAMPLES = 0;
    boolean DEFAULT_MUSIC = true;
    boolean DEFAULT_SFX = true;
    int DEFAULT_FRAMERATE = -1;

    /**
     * Returns the stored rendering API name, or the default
     * 
     * @return
     *      String
     */
    String getRenderer();
    
    /**
     * Sets the rendering API.
     * 
     * @param renderer
     */
    void setRenderer(String renderer);
    
    /**
     * Returns the width for the screen as stored or the default.
     * 
     * @return
     *      int
     */
    int getWidth();
    
    /**
     * Sets the width for the screen.
     * 
     * @param width
     */
    void setWidth(int width);
    
    /**
     * Returns the height for the screen as stored or the default.
     * 
     * @return
     *      int
     */
    int getHeight();
    
    /**
     * Sets the height for the screen.
     * 
     * @param height
     */
    void setHeight(int height);
    
    /**
     * Returns the depth for the screen as stored or the default.
     * 
     * @return
     *      int
     */
    int getDepth();
    
    /**
     * Sets the depth for the screen.
     * 
     * @param depth
     */
    void setDepth(int depth);
    
    /**
     * Returns the screen refresh frequency as stored or the default.
     * 
     * @return
     *      int
     */
    int getFrequency();
    
    /**
     * Sets the screen refresh frequency.
     * 
     * @param frequency
     */
    void setFrequency(int frequency);
    
    /**
     * Returns the current state of vertical synchronization. This synchronizes
     * the game update frequency to the monitor update frequency. This can help
     * provide a much smoother game experience and help with screen tearing.
     * 
     * @return
     *      boolean
     */
    boolean isVerticalSync();
    
    /**
     * Sets the state of vertical synchronization. This synchronizes
     * the game update frequency to the monitor update frequency. This can help
     * provide a much smoother game experience and help with screen tearing.
     * 
     * @param vsync
     */
    void setVerticalSync(boolean vsync);
    
    /**
     * Returns the screen's fullscreen status as stored or the default.
     * 
     * @return
     *      boolean
     */
    boolean isFullscreen();
    
    /**
     * Sets the fullscreen status for the screen.
     * 
     * @param fullscreen
     */
    void setFullscreen(boolean fullscreen);
    
    /**
     * Returns the depth bits to use for the renderer as stored
     * or the default.
     * 
     * @return
     *      int
     */
    int getDepthBits();
    
    /**
     * Sets the depth bits for use with the renderer.
     * 
     * @param depthBits
     */
    void setDepthBits(int depthBits);
    
    /**
     * Returns the alpha bits to use for the renderer as stored
     * or the default.
     * 
     * @return
     *      int
     */
    int getAlphaBits();
    
    /**
     * Sets the alpha bits for use with the renderer.
     * 
     * @param alphaBits
     */
    void setAlphaBits(int alphaBits);
    
    /**
     * Returns the stencil bits to use for the renderer as stored
     * or the default.
     * 
     * @return
     *      int
     */
    int getStencilBits();
    
    /**
     * Sets the stencil bits for use with the renderer.
     * 
     * @param stencilBits
     */
    void setStencilBits(int stencilBits);
    
    /**
     * Returns the number of samples to use for the multisample buffer
     * as stored or the default.
     * 
     * @return
     *      int
     */
    int getSamples();
    
    /**
     * Sets the number of samples to use for the multisample buffer.
     * 
     * @param samples
     */
    void setSamples(int samples);
    
    /**
     * Returns the enabled status of music as stored or the default.
     * 
     * @return
     *      boolean
     */
    boolean isMusic();
    
    /**
     * Sets the enabled status of music.
     * 
     * @param musicEnabled
     */
    void setMusic(boolean musicEnabled);
    
    /**
     * Returns the enabled status of sound effects as stored or the default.
     * 
     * @return
     *      boolean
     */
    boolean isSFX();
    
    /**
     * Sets the enabled status of sound effects.
     * 
     * @param sfxEnabled
     */
    void setSFX(boolean sfxEnabled);
    
    /**
     * Returns the specified framerate or -1 if variable framerate is specified.
     * 
     * @return
     *      int
     */
    int getFramerate();
    
    /**
     * Sets the framerate. Use -1 to specify variable framerate.
     * 
     * @param framerate
     */
    void setFramerate(int framerate);
    
    /**
     * Clears all settings.
     * <P/>
     * This removes all settings.
     * As a result, get*() will return the default value, but this is
     * very different from <I>setting</I> default values.
     * If all settings are removed, users will automatically receive updated
     * system or game default settings.
     * If a user were to set (then save) default values, they would always
     * retrieve those values regardless to any changes to defaults.
     * <P/>
     * clear() followed by save() will persist an indication that there are
     * no values saved.
     *
     * @see #save()
     * @throws IOException If there is some consistency or access problem
     *         obtaining the values to be cleared.
     */
    void clear() throws IOException;
    
    void set(String name, String value);
    
    void setBoolean(String name, boolean value);
    
    void setInt(String name, int value);
    
    void setLong(String name, long value);
    
    void setFloat(String name, float value);
    
    void setDouble(String name, double value);
    
    void setByteArray(String name, byte[] bytes);
    
    void setObject(String name, Object obj);
    
    String get(String name, String defaultValue);
    
    boolean getBoolean(String name, boolean defaultValue);
    
    int getInt(String name, int defaultValue);
    
    long getLong(String name, long defaultValue);
    
    float getFloat(String name, float defaultValue);
    
    double getDouble(String name, double defaultValue);
    
    byte[] getByteArray(String name, byte[] bytes);
    
    Object getObject(String name, Object obj);

    /**
     * @returns true if there was no backing persistence object when
     * this GameSettings was instantiated.
     */
    boolean isNew();

    /**
     * This method will persist all changed settings.
     * For backing implementations which automatically persist all value
     * changes immediately, this method should re-persist the settings.
     * <P/>
     * A call to save() when the GameSettings holds no settings
     * should persist something showing that no settings are saved for this
     * game.  (As opposed to saving nothing at all, or removing all traces
     * of these GameSettings).
     */
    void save() throws IOException;
}
