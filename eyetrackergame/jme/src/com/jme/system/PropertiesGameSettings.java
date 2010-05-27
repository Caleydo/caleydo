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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;


/**
 * <code>PropertiesGameSettings</code> handles loading and saving a properties
 * file that
 * defines the display settings. A property file is identified during creation
 * of the object. The properties file should have the following format:
 * <PRE><CODE>
 * FREQ=60
 * RENDERER=LWJGL
 * WIDTH=1280
 * HEIGHT=1024
 * DEPTH=32
 * FULLSCREEN=false
 * </CODE></PRE>
 *
 * @author Mark Powell
 * @version $Revision: 4131 $
 */
public class PropertiesGameSettings extends AbstractGameSettings {
    private static final Logger logger = Logger.getLogger(
            PropertiesGameSettings.class.getName());

    //property object
    private Properties prop;
    //the file that contains our properties.
    private String filename;
    private static boolean dfltsInitted = false;

    /**
     * Legacy wrapper constructor
     *
     * @see #PropertiesGameSettings(String, String)
     */
    public PropertiesGameSettings(String userFile) {
        this(userFile, "game-defaults.properties");
    }

    /**
     * Constructor creates the <code>PropertiesGameSettings</code> object for use.
     *
     * @param personalFilename the properties file to use, read from filesystem.
     *                      Must not be null.
     * @param dfltsFilename the properties file to use, read from CLASSPATH.
     *                      Null to not seek any runtime defaults file.
     * @throws JmeException if the personalFilename is null.
     */
    public PropertiesGameSettings(
            String personalFilename, String dfltsFilename) {
        if (null == personalFilename) {
            throw new JmeException("Must give a valid filename");
        }
        if (!dfltsInitted) {
            dfltsInitted = true;
            // default* setting values are static, therefore, regardless of
            // how many GameSettings we instantiate, the defaults are
            // assigned only once.
            assignDefaults(dfltsFilename);
        }

        this.filename = personalFilename;
        isNew = !(new File(filename).isFile());
        prop = new Properties();

        logger.info("PropertiesGameSettings created");
    }

    /**
     * <code>load</code> attempts to load the properties file defined during
     * instantiation and put all properties in the table. If there is a problem
     * loading or reading the file, false is returned. If all goes well, true is
     * returned.
     * 
     * @return the success of the load, true indicated success and false
     *         indicates failure.
     */
    public boolean load() {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            logger.warning("Could not load properties. Creating a new one.");
            return false;
        }

        try {
            if (fin != null) {
                prop.load(fin);
                fin.close();
            }
        } catch (IOException e) {
            logger.warning("Could not load properties. Creating a new one.");
            return false;
        }

        //confirm that the properties file has all the data we need.
        if (null == prop.getProperty("WIDTH")
            || null == prop.getProperty("HEIGHT")
            || null == prop.getProperty("DEPTH")
            || null == prop.getProperty("FULLSCREEN")) {
            logger.warning("Properties file not complete.");
            return false;
        }

        logger.info("Read properties");
        return true;
    }

    /**
     * Persists current property mappings to designated file, overwriting
     * if file already present.
     *
     * @throws IOException for I/O failures
     */
    public void save() throws IOException {
        FileOutputStream fout = new FileOutputStream(filename);
        prop.store(fout, "Game Settings written by " + getClass().getName()
                + " at " + new java.util.Date());

        fout.close();
        logger.info("Saved properties");
    }

    /**
     * <code>save(int, int, int, int, boolean, String)</code>
     * overwrites the properties file with the given parameters.
     *
     * @param width the width of the resolution.
     * @param height the height of the resolution.
     * @param depth the bits per pixel.
     * @param freq the frequency of the monitor.
     * @param fullscreen use fullscreen or not.
     * @deprecated
     * @return true if save was successful, false otherwise.
     */
    public boolean save(int width, int height, int depth, int freq,
            boolean fullscreen, String renderer) { 

        prop.clear();
        setWidth(width);
        setHeight(height);
        setDepth(depth);
        setFrequency(freq);
        setFullscreen(fullscreen);
        setRenderer(renderer);

        try {
            save();
        } catch (IOException e) {
            logger.warning("Could not save properties: " + e);
            return false;
        }
        return true;
    }

    /**
     * <code>getWidth</code> returns the width as read from the properties
     * file. If the properties file does not contain width or was not read
     * properly, the default width is returned.
     *
     * @return the width determined by the properties file, or the default.
     */
    public int getWidth() {
        String w = prop.getProperty("WIDTH");
        if (null == w) {
            return defaultWidth;
        } 
            
        return Integer.parseInt(w);        
    }

    /**
     * <code>getHeight</code> returns the height as read from the properties
     * file. If the properties file does not contain height or was not read
     * properly, the default height is returned.
     *
     * @return the height determined by the properties file, or the default.
     */
    public int getHeight() {
        String h = prop.getProperty("HEIGHT");
        if (null == h) {
            return defaultHeight;
        }
            
        return Integer.parseInt(h);        
    }

    /**
     * <code>getDepth</code> returns the depth as read from the properties
     * file. If the properties file does not contain depth or was not read
     * properly, the default depth is returned.
     *
     * @return the depth determined by the properties file, or the default.
     */
    public int getDepth() {
        String d = prop.getProperty("DEPTH");
        if (null == d) {
            return defaultDepth;
        } 
            
        return Integer.parseInt(d);        
    }

    /**
     * <code>getFrequency</code> returns the frequency of the monitor as read from
     * the properties file. If the properties file does not contain frequency
     * or was not read properly the default frequency is returned.
     *
     * @see GameSettings#getFrequency()
     * @return the frequency determined by the properties file, or the default.
     */
    public int getFrequency() {
        String f = prop.getProperty("FREQ");
        if(null == f) {
            return defaultFrequency;
        } 
            
        return Integer.parseInt(f);        
    }

    /**
     * <code>isFullscreen</code> returns the fullscreen flag as read from the
     * properties file. If the properties file does not contain the fullscreen
     * flag or was not read properly, the default value is returned.
     *
     * @see PreferencesGameSettings#isFullscreen()
     * @return the fullscreen flag determined by the properties file, or the
     *      default.
     */
    public boolean isFullscreen() {
        String f = prop.getProperty("FULLSCREEN");
        if(null == f) {
            return defaultFullscreen;
        }
            
        return Boolean.valueOf(prop.getProperty("FULLSCREEN"));        
    }

    /**
     *
     * <code>getRenderer</code> returns the requested rendering API, or the
     * default.
     * @return the rendering API or the default.
     */
    public String getRenderer() {
        String renderer = prop.getProperty("RENDERER");
        if(null == renderer) {
            return defaultRenderer;
        } 
            
        return renderer;        
    }

    /**
     * <code>get</code> takes an arbitrary string as a key and returns any
     * value associated with it, null if none.
     * @param key the key to use for data retrieval.
     * @return the string associated with the key, null if none.
     */
    public String get(String key) {
        return prop.getProperty(key);
    }

    /* REMAINING METHODS COMPLETE IMPLEMENTATION OF GameSettings INTERFACE */
    /**
     * @see GameSettings#clear()
     */
    public void clear() {
        prop.clear();
    }

    /**
     * @see GameSettings#get(String, String)
     */
    public String get(String name, String defaultValue) {
        String value = get(name);
        return (value == null) ? defaultValue : value;
    }

    /**
     * If the properties file does not contain the setting or was not read
     * properly, the default value is returned.
     *
     * @see GameSettings#getAlphaBits()
     * @throws InternalError in all cases
     */
    public int getAlphaBits() {
        String s = prop.getProperty("ALPHA_BITS");
        return (s == null) ? defaultAlphaBits : Integer.parseInt(s);
    }

    /**
     * If the properties file does not contain the setting or was not read
     * properly, the default value is returned.
     *
     * @see GameSettings#getDepthBits()
     * @throws InternalError in all cases
     */
    public int getDepthBits() {
        String s = prop.getProperty("DEPTH_BITS");
        return (s == null) ? defaultDepthBits : Integer.parseInt(s);
    }

    /**
     * If the properties file does not contain the setting or was not read
     * properly, the default value is returned.
     *
     * @see GameSettings#getFramerate()
     * @throws InternalError in all cases
     */
    public int getFramerate() {
        String s = prop.getProperty("FRAMERATE");
        return (s == null) ? defaultFramerate : Integer.parseInt(s);
    }

    /**
     * If the properties file does not contain the setting or was not read
     * properly, the default value is returned.
     *
     * @see GameSettings#getSamples()
     * @throws InternalError in all cases
     */
    public int getSamples() {
        String s = prop.getProperty("SAMPLES");
        return (s == null) ? defaultSamples : Integer.parseInt(s);
    }

    /**
     * If the properties file does not contain the setting or was not read
     * properly, the default value is returned.
     *
     * @see GameSettings#getStencilBits()
     * @throws InternalError in all cases
     */
    public int getStencilBits() {
        String s = prop.getProperty("STENCIL_BITS");
        return (s == null) ? defaultStencilBits : Integer.parseInt(s);
    }

    /**
     * If the properties file does not contain the setting or was not read
     * properly, the default value is returned.
     *
     * @see GameSettings#isMusic()
     * @throws InternalError in all cases
     */
    public boolean isMusic() {
        String s = prop.getProperty("MUSIC");
        return (s == null) ? defaultMusic : Boolean.parseBoolean(s);
    }

    /**
     * If the properties file does not contain the setting or was not read
     * properly, the default value is returned.
     *
     * @see GameSettings#isSFX()
     * @throws InternalError in all cases
     */
    public boolean isSFX() {
        String s = prop.getProperty("SFX");
        return (s == null) ? defaultSFX : Boolean.parseBoolean(s);
    }

    /**
     * If the properties file does not contain the setting or was not read
     * properly, the default value is returned.
     *
     * @see GameSettings#isVerticalSync()
     * @throws InternalError in all cases
     */
    public boolean isVerticalSync() {
        String s = prop.getProperty("VERTICAL_SYNC");
        return (s == null) ? defaultVerticalSync : Boolean.parseBoolean(s);
    }

    /**
     * Legacy method.
     *
     * @deprecated  Use method getFrequency instead.
     * @see #getFrequency()
     */
    public int getFreq() {
        return getFrequency();
    }

    /**
     * Legacy method.
     *
     * @deprecated  Use method isFullscreen instead.
     * @see #isFullscreen()
     */
    public boolean getFullscreen() {
        return isFullscreen();
    }

    /**
     * @see GameSettings#getBoolean(String, boolean)
     */
    public boolean getBoolean(String name, boolean defaultValue) {
        String stringValue = get(name);
        return (stringValue == null)
                ? defaultValue : Boolean.parseBoolean(stringValue);
    }

    /**
     * @see GameSettings#getByteArray(String, byte[])
     */
    public byte[] getByteArray(String name, byte[] defaultValue) {
        String stringValue = get(name);
        return (stringValue == null)
                ? defaultValue : stringValue.getBytes();
    }

    /**
     * @see GameSettings#getDouble(String, double)
     */
    public double getDouble(String name, double defaultValue) {
        String stringValue = get(name);
        return (stringValue == null)
                ? defaultValue : Double.parseDouble(stringValue);
    }

    /**
     * @see GameSettings#getFloat(String, float)
     */
    public float getFloat(String name, float defaultValue) {
        String stringValue = get(name);
        return (stringValue == null)
                ? defaultValue : Float.parseFloat(stringValue);
    }

    /**
     * @see GameSettings#getInt(String, int)
     */
    public int getInt(String name, int defaultValue) {
        String stringValue = get(name);
        return (stringValue == null)
                ? defaultValue : Integer.parseInt(stringValue);
    }

    /**
     * @see GameSettings#getLong(String, long)
     */
    public long getLong(String name, long defaultValue) {
        String stringValue = get(name);
        return (stringValue == null)
                ? defaultValue : Long.parseLong(stringValue);
    }

    /**
     * @see GameSettings#getObject(String, Object)
     */
    public Object getObject(String name, Object defaultValue) {
        String stringValue = get(name);
        return (stringValue == null) ? defaultValue : stringValue;
    }

    /**
     * Removes specified property, if present.
     */
    public void remove(String name) {
        prop.remove(name);
    }

    /**
     * Sets a property.
     *
     * @see GameSettings#set(String, String)
     */
    public void set(String name, String value) {
        prop.setProperty(name, value);
    }

    /**
     * save() method which throws only a RuntimeExceptin.
     *
     * @throws RuntimeSetting for IO failure
     * @see #save()
     */
    public void wrappedSave() {
        try {
            save();
        } catch (IOException ioe) {
            logger.log(Level.WARNING,
                    "Failed to persist properties", ioe);
            throw new RuntimeException(ioe);
        }
    }

    /**
     * @see #set(String, String)
     * @see PreferencesGameSettings#setBoolean(String, boolean)
     * @throws RuntimeSetting for IO failure
     */
    public void setBoolean(String name, boolean value) {
        set(name, Boolean.toString(value));
    }

    /**
     * @see #set(String, String)
     * @see PreferencesGameSettings#setByteArray(String, byte[])
     * @throws RuntimeSetting for IO failure
     */
    public void setByteArray(String name, byte[] value) {
        set(name, new String(value));
    }

    /**
     * @see #set(String, String)
     * @see PreferencesGameSettings#setDouble(String, double)
     * @throws RuntimeSetting for IO failure
     */
    public void setDouble(String name, double value) {
        set(name, Double.toString(value));
    }

    /**
     * @see #set(String, String)
     * @see PreferencesGameSettings#setFloat(String, float)
     * @throws RuntimeSetting for IO failure
     */
    public void setFloat(String name, float value) {
        set(name, Float.toString(value));
    }

    /**
     * @see #set(String, String)
     * @see PreferencesGameSettings#setInt(String, int)
     * @throws RuntimeSetting for IO failure
     */
    public void setInt(String name, int value) {
        set(name, Integer.toString(value));
    }

    /**
     * @see #set(String, String)
     * @see PreferencesGameSettings#setLong(String, long)
     * @throws RuntimeSetting for IO failure
     */
    public void setLong(String name, long value) {
        set(name, Long.toString(value));
    }

    /**
     * Not implemented.
     * Properties can not store an arbitrary Object in human-readable format.
     * Use set(String, String) instead.
     *
     * @see PreferencesGameSettings#setObject(String, boolean)
     * @see #set(String, String)
     * @throws InternalError in all cases
     */
    public void setObject(String name, Object value) {
        throw new InternalError(getClass().getName()
                + " Can't store arbitrary objects.  "
                + "If there is a toString() method for your Object, and it is "
                + "Properties-compatible, use " + getClass().getName()
                + ".set(String, String).");
    }

    /**
     * @see GameSettings#setWidth(int)
     */
    public void setWidth(int width) {
        setInt("WIDTH", width);
    }

    /**
     * @see GameSettings#setHeight(int)
     */
    public void setHeight(int height) {
        setInt("HEIGHT", height);
    }

    /**
     * @see GameSettings#setDepth(int)
     */
    public void setDepth(int depth) {
        setInt("DEPTH", depth);
    }

    /**
     * @see GameSettings#setFrequency(int)
     */
    public void setFrequency(int freq) {
        setInt("FREQ", freq);
    }

    /**
     * @see GameSettings#setFullscreen(boolean)
     */
    public void setFullscreen(boolean fullscreen) {
        setBoolean("FULLSCREEN", fullscreen);
    }

    /**
     * @see GameSettings#setRenderer(String)
     */
    public void setRenderer(String renderer) {
        set("RENDERER", renderer);
    }

    /**
     * @see GameSettings#setAlphaBits(int)
     * @throws InternalError in all cases
     */
    public void setAlphaBits(int alphaBits) {
        setInt("ALPHA_BITS", alphaBits);
    }

    /**
     * @see GameSettings#setDepthBits(int)
     * @throws InternalError in all cases
     */
    public void setDepthBits(int depthBits) {
        setInt("DEPTH_BITS", depthBits);
    }

    /**
     * @see GameSettings#setFramerate(int)
     * @throws InternalError in all cases
     */
    public void setFramerate(int framerate) {
        setInt("FRAMERATE", framerate);
    }

    /**
     * @see GameSettings#setMusic(boolean)
     * @throws InternalError in all cases
     */
    public void setMusic(boolean music) {
        setBoolean("MUSIC", music);
    }

    /**
     * @see GameSettings#setSamples(int)
     * @throws InternalError in all cases
     */
    public void setSamples(int samples) {
        setInt("SAMPLES", samples);
    }

    /**
     * @see GameSettings#setSFX(boolean)
     * @throws InternalError in all cases
     */
    public void setSFX(boolean sfx) {
        setBoolean("SFX", sfx);
    }

    /**
     * @see GameSettings#setStencilBits(int)
     * @throws InternalError in all cases
     */
    public void setStencilBits(int stencilBits) {
        setInt("STENCIL_BITS", stencilBits);
    }

    /**
     * @see GameSettings#setVerticalSync(boolean)
     * @throws InternalError in all cases
     */
    public void setVerticalSync(boolean verticalSync) {
        setBoolean("VERTICAL_SYNC", verticalSync);
    }
}
