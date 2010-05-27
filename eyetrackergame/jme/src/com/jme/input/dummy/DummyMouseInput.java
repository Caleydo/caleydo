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

package com.jme.input.dummy;

import java.net.URL;

import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.jme.image.Image;
import com.jme.input.MouseInput;

/**
 * Mouse input handler that is a Dummy input.
 *
 * @see Cursor
 * @see Mouse
 * @author Kai Börnert
 * @version $Id: DummyMouseInput.java 4310 2009-05-01 13:53:42Z blaine.dev $
 */
public class DummyMouseInput extends MouseInput {
    /**
     * Constructor creates a new <code>DummyMouseInput</code> object. 
     */
    protected DummyMouseInput() {
    }

    /**
     * <code>destroy</code> does nothing at all
     * @see com.jme.input.MouseInput#destroy()
     */
    public void destroy() {
    }

    /**
     * <code>getButtonIndex</code> returns 0;
     * @see com.jme.input.MouseInput#getButtonIndex(java.lang.String)
     */
    public int getButtonIndex(String buttonName) {
        return 0;
    }

    /**
     * <code>getButtonName</code> returns the name of a given button index.
     * @see com.jme.input.MouseInput#getButtonName(int)
     */
    public String getButtonName(int buttonIndex) {
        return "Mouse-Nil";
    }

    /**
     * <code>isButtonDown</code> returns false, the Dummymouse can't be pressed.
     * @see com.jme.input.MouseInput#isButtonDown(int)
     */
    public boolean isButtonDown(int buttonCode) {
        return false;
    }

    /**
     * <code>getWheelDelta</code> retrieves the change of the mouse wheel,
     * if any. (There can't be any in ase of a DummyMouse)
     * @see com.jme.input.MouseInput#getWheelDelta()
     */
    public int getWheelDelta() {
        return 0;
    }
    /**
     * <code>getXDelta</code> retrieves the change of the x position, if any. (Dummymouses can'T move)
     * @see com.jme.input.MouseInput#getXDelta()
     */
    public int getXDelta() {
        return 0;
    }
    /**
     * <code>getYDelta</code> retrieves the change of the y position, if any. (Dummymouses can'T move)
     * @see com.jme.input.MouseInput#getYDelta()
     */
    public int getYDelta() {
        return 0;
    }

    /**
     * <code>getXAbsolute</code> gets the absolute x axis value (Always 0 for Dummymouse).
     * @see com.jme.input.MouseInput#getXAbsolute()
     */
    public int getXAbsolute() {
        return 0;
    }

    /**
     * <code>getYAbsolute</code> gets the absolute y axis value.(Always 0 for Dummymouse)
     * @see com.jme.input.MouseInput#getYAbsolute()
     */
    public int getYAbsolute() {
        return 0;
    }

    /**
     * <code>updateState</code> updates the mouse state, in out case it does nothing at all.
     * @see com.jme.input.MouseInput#update()
     */
    public void update() {
    }


    /**
     * <code>setCursorVisible</code> sets the visiblity of the hardware
     * cursor, since it's a Dummymouse we don't do anything!.
     * 
     * @see com.jme.input.MouseInput#setCursorVisible(boolean)
     */
    public void setCursorVisible(boolean v) {
        
    }

    /**
     * <code>isCursorVisible</code> Returns false, can you see a DummyMouse?.
     * @see com.jme.input.MouseInput#isCursorVisible()
     */
    public boolean isCursorVisible() {
        return true;
    }

    public void setHardwareCursor(URL file) {
        //ignore it
    }

    /**
    * Loads and sets a hardware cursor, Dummymouse ignores it
    * 
    * @param url to imagefile 
    * @param xHotspot from image left
    * @param yHotspot from image bottom
    */
    public synchronized void setHardwareCursor(URL file, int xHotspot, int yHotspot) {
    }

    /**
    * This method will set an animated harware cursor, Dummymouse ignores it
    * 
    * @param file in this method file is only used as a key for cursor cashing 
    * @param images the animation frames
    * @param delays delays between changing each frame
    * @param xHotspot from image left
    * @param yHotspot from image bottom
    */
    public synchronized void setHardwareCursor(URL file, Image[] images, int[] delays,int xHotspot, int yHotspot) {
    }

    public int getWheelRotation() {
        return 0;
    }
    
     /**
     * We return the values for a simple standartmouse here.
     */
    public int getButtonCount() {
        return 3;
    }

    public void setCursorPosition( int x, int y) {
        //Nothing to do
    }
    
    public void clear() {
        //Nothing to do
    }
    
    public void clearButton(int buttonCode) {
        //Nothing to do
    }
}
