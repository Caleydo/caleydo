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

package com.jme.input;

/**
 * This interface is used to receive mouse events from {@link com.jme.input.MouseInput#update()}.
 */
public interface MouseInputListener {
    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever a mouse button is pressed or released.
     * @param button index of the mouse button that was pressed/released
     * @param pressed true if button was pressed, false if released
     * @param x x position of the mouse while button was pressed/released
     * @param y y position of the mouse while button was pressed/released
     */
    void onButton( int button, boolean pressed, int x, int y );

    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever the mouse wheel is rotated.
     * @param wheelDelta steps the wheel was rotated
     * @param x x position of the mouse while wheel was rotated
     * @param y y position of the mouse while wheel was rotated
     */
    void onWheel( int wheelDelta, int x, int y );

    /**
     * Called in {@link com.jme.input.KeyInput#update()} whenever the mouse is moved.
     * @param xDelta delta of the x coordinate since the last mouse movement event
     * @param yDelta delta of the y coordinate since the last mouse movement event
     * @param newX x position of the mouse after the mouse was moved
     * @param newY y position of the mouse after the mouse was moved
     */
    void onMove( int xDelta, int yDelta, int newX, int newY );
}
