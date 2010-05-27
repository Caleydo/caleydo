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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Code generally useful to all typical GameSettings implementations.
 * <P/>
 * Admitted limitation of the static default* design is that we assume
 * only one "game" runs in the JVM.
 * I know of no use case that conflict with this.
 * <P/>
 * A particularly useful feature of AbstractGameSettings is the optional
 * usage of game-specific (not session-specific) overrides.
 * You can override the DEFAULT_* settings defined in the GameSettings
 * interface, and you can also change values for settings without
 * GameSettings <I>setters</I> (i.e. settings not for end-user usage).
 * Here's a sample game-defaults.properties file:<PRE><CODE>
 *   # This overrides GameSettigns.DEFAULT_MUSIC
 *   DEFAULT_MUSIC: false
 *   # Similarly...
 *   DEFAULT_SFX: false
 *   # This assigns a resource which end-users can't modify.
 *   SETTINGS_WIDGET_IMAGE: /jmetest/data/texture/spark.jpg
 * </CODE></PRE>
 * The defaults can also be changed programmatically instead of
 * declaratively, of course.  See BaseGame.java for examples of that.
 * 
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 * @see GameSettings
 * @see com.jme.app.BaseGame
 * @since jME 2.0
 * @version $Revision: 4318 $, $Date: 2009-05-02 01:10:05 +0200 (Sa, 02 Mai 2009) $
 * 
 * @see GameSettings
 */
public abstract class AbstractGameSettings implements GameSettings {
    private static final Logger logger = Logger
            .getLogger(AbstractGameSettings.class.getName());
    
    protected boolean isNew = true;

    // These are all objects so it is very clear when they have been
    // explicitly set.
    protected static Integer defaultWidth = null;
    protected static Integer defaultHeight = null;
    protected static Integer defaultDepth = null;
    protected static Integer defaultFrequency = null;
    protected static Boolean defaultFullscreen = null;
    protected static String defaultRenderer = null;
    protected static Boolean defaultVerticalSync = null;
    protected static Integer defaultDepthBits = null;
    protected static Integer defaultAlphaBits = null;
    protected static Integer defaultStencilBits = null;
    protected static Integer defaultSamples = null;
    protected static Boolean defaultMusic = null;
    protected static Boolean defaultSFX = null;
    protected static Integer defaultFramerate = null;
    protected static String defaultSettingsWidgetImage = null;
    private static boolean defaultsAssigned = false;

    /**
     * This is only getting the "default" value, which may not be changed
     * by end-users.
     */
    public String getDefaultSettingsWidgetImage() {
        return defaultSettingsWidgetImage;
    }

    /**
     * Sets default* static variables according to GameSettings.DEFAULT_*
     * values and an optional .properties file.
     * Note that we are talking about <b>defaults</b> here, not
     * user-specific settings.
     * <P/>
     * This method should be called once the game name is known to the
     * subclass.
     * To override any default with your subclass (as opposed to by using
     * a .properties file), just set the static variable before or after
     * calling this method (before or after depends on the precedence you
     * want among programmatic and declarative DEFAULT_*, default* settings).
     * <P/>
     * Add new setting names by making your own method which does its own
     * thing and calls AbstractGameSettings.assignDefaults(propfilename).
     * <P/>
     * Property file paths are relative to CLASSPATH element roots.
     *
     * @param propFileName Properties file read as CLASSPATH resource.
     *                 If you give null, no properties file will be loaded.
     */
    protected static void assignDefaults(String propFileName) {
        if (defaultsAssigned) {
            logger.fine("Skipping repeat invocation of assignDefaults()");
            return;
        }
        logger.fine("Initializing static default* setting variables");

        //hansen.playground.logging.LoggerInformation.getInfo();
//System.exit(0);

        defaultsAssigned = true;
        if (defaultWidth == null)
            defaultWidth = Integer.valueOf(DEFAULT_WIDTH);
        if (defaultHeight == null)
            defaultHeight = Integer.valueOf(DEFAULT_HEIGHT);
        if (defaultDepth == null)
            defaultDepth = Integer.valueOf(DEFAULT_DEPTH);
        if (defaultFrequency == null)
            defaultFrequency = Integer.valueOf(DEFAULT_FREQUENCY);
        if (defaultFullscreen == null)
            defaultFullscreen = Boolean.valueOf(DEFAULT_FULLSCREEN);
        if (defaultRenderer == null)
            defaultRenderer = DEFAULT_RENDERER;
        if (defaultVerticalSync == null)
            defaultVerticalSync = Boolean.valueOf(DEFAULT_VERTICAL_SYNC);
        if (defaultDepthBits == null)
            defaultDepthBits = Integer.valueOf(DEFAULT_DEPTH_BITS);
        if (defaultAlphaBits == null)
            defaultAlphaBits = Integer.valueOf(DEFAULT_ALPHA_BITS);
        if (defaultStencilBits == null)
            defaultStencilBits = Integer.valueOf(DEFAULT_STENCIL_BITS);
        if (defaultSamples == null)
            defaultSamples = Integer.valueOf(DEFAULT_SAMPLES);
        if (defaultMusic == null)
            defaultMusic = Boolean.valueOf(DEFAULT_MUSIC);
        if (defaultSFX == null)
            defaultSFX = Boolean.valueOf(DEFAULT_SFX);
        if (defaultFramerate == null)
            defaultFramerate = Integer.valueOf(DEFAULT_FRAMERATE);
        InputStream istream = null;
        if (propFileName != null)
            istream = AbstractGameSettings.class.getClassLoader().
                    getResourceAsStream(propFileName);
        if (istream == null) {
            logger.fine("No customization properties file found");
            return;
        }
        logger.fine("Customizing defaults according to '" + propFileName + "'");
        Properties p = new Properties();
        try {
            p.load(istream);
        } catch (IOException ioe) {
            logger.log(Level.WARNING,
                    "Failed to load customizations from '" + propFileName
                    + "'.  Continuing without customizations.",
                    ioe);
            return;
        }
        Integer i;
        String s;
        Boolean b;
        i = loadInteger("DEFAULT_WIDTH", p);
        if (i != null) defaultWidth = i.intValue();
        i = loadInteger("DEFAULT_HEIGHT", p);
        if (i != null) defaultHeight = i.intValue();
        i = loadInteger("DEFAULT_DEPTH", p);
        if (i != null) defaultDepth = i.intValue();
        i = loadInteger("DEFAULT_FREQUENCY", p);
        if (i != null) defaultFrequency = i.intValue();
        b = loadBoolean("DEFAULT_FULLSCREEN", p);
        if (b != null) defaultFullscreen = b.booleanValue();
        s = p.getProperty("DEFAULT_RENDERER");
        if (s != null) defaultRenderer = s;
        b = loadBoolean("DEFAULT_VERTICAL_SYNC", p);
        if (b != null) defaultVerticalSync = b.booleanValue();
        i = loadInteger("DEFAULT_DEPTH_BITS", p);
        if (i != null) defaultDepthBits = i.intValue();
        i = loadInteger("DEFAULT_ALPHA_BITS", p);
        if (i != null) defaultAlphaBits = i.intValue();
        i = loadInteger("DEFAULT_STENCIL_BITS", p);
        if (i != null) defaultStencilBits = i.intValue();
        i = loadInteger("DEFAULT_SAMPLES", p);
        if (i != null) defaultSamples = i.intValue();
        b = loadBoolean("DEFAULT_MUSIC", p);
        if (b != null) defaultMusic = b.booleanValue();
        b = loadBoolean("DEFAULT_SFX", p);
        if (b != null) defaultSFX = b.booleanValue();
        i = loadInteger("DEFAULT_FRAMERATE", p);
        if (i != null) defaultFramerate = i.intValue();
        s = p.getProperty("SETTINGS_WIDGET_IMAGE");
        if (s != null) defaultSettingsWidgetImage = s;
    }

    public static Integer loadInteger(String name, Properties props) {
        String s = props.getProperty(name);
        if (s == null) return null;
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException nfe) {
        }
        logger.warning("Malformatted value in game properties file: " + s);
        return null;
    }

    public static Boolean loadBoolean(String name, Properties props) {
        String s = props.getProperty(name);
        if (s == null) return null;
        return Boolean.valueOf(s);
    }

    /**
     * @param inName  Must be non-null
     * @returns normalized name.  All lower-case with no shell meta-characters.
     */
    protected static String normalizeName(String inName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inName.length(); i++) {
            char c = inName.charAt(i);
            sb.append((Character.isLetter(c)
                    || Character.isDigit(c)
                    || c == '-' || c == '_') ? c : '_');
        }
        return sb.toString().toUpperCase();
    }

    /**
     * @see GameSettings#isNew()
     */
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
    
    /**
     * @see GameSettings#isNew()
     */
    public boolean isNew() {
        return isNew;
    }
}

