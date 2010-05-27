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

package com.jme.input.action;

import com.jme.system.DisplaySystem;

/**
 * <code>KeyScreenShotAction</code> allows the user to press a key to take a
 * screenshot of the current display. This screenshot is saved in the current
 * running directory with a supplied filename.png. If no filename is supplied it
 * is saved as screenshot.png.
 * 
 * @author Mark Powell
 * @author Jack Lindamood (javadoc only)
 * @version $Id: KeyScreenShotAction.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class KeyScreenShotAction extends KeyInputAction {
    //the name of the file to save the screenshot as.
    private String filename;

    /**
     * A call to KeyScreenShotAction("screenshot")
     * 
     * @see #KeyScreenShotAction(java.lang.String)
     */
    public KeyScreenShotAction() {
        this("screenshot");
    }

    /**
     * Creates a screenshot action that saves to the given filename. Usually, an
     * extention is appended to the filename signaling the screenshot image
     * type. The file is accessed with the <code>File</code> class
     * 
     * @param filename
     *            The filename to save the current renderer screen to.
     * @see java.io.File
     */
    public KeyScreenShotAction(String filename) {
        this.filename = filename;
    }

    /**
     * <code>performAction</code> saves the current renderer screen to the
     * filename as an image.
     * 
     * @see com.jme.input.action.KeyInputAction#performAction(InputActionEvent)
     */
    public void performAction(InputActionEvent evt) {
        DisplaySystem.getDisplaySystem().getRenderer().takeScreenShot(filename);
    }

    /**
     * Sets the name of the file to save screenshots too.
     * 
     * @param filename
     *            The filename to save too.
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Returns the currently set filename that screenshots are saved too.
     * 
     * @return The current filename where screenshots are saved.
     */
    public String getFilename() {
        return filename;
    }
}