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

package com.jme.system.canvas;

/**
 * <code>JMECanvas</code> is an interface to classes allowing jME generated
 * graphics to be displayed in an AWT/Swing/SWT or other such UI framework.
 * 
 * @author Joshua Slack
 */

public interface JMECanvas {

    /**
     * Sets the logic/gameplay implementation for this JMECanvas
     * 
     * @param impl
     *            the implementor object that will provide rendering/update
     *            logic.
     */
    void setImplementor(JMECanvasImplementor impl);

    /**
     * @return true if the logic loop of this canvas should ask jME's input
     *         system to "poll".
     */
    boolean isUpdateInput();

    /**
     * @param doUpdate
     *            true if the logic loop of this canvas should ask jME's input
     *            system to "poll".
     */
    void setUpdateInput(boolean doUpdate);

    
    /**
     * @param shouldAutoKillGfxContext 
     *          true(default) if the GFX Context should be destroyed
     *          as soon as the canvas is removed from it's parent container
     */
    void setAutoKillGfxContext( boolean shouldAutoKillGfxContext );
    
    /**
     * @return 
     *      true(default) if the GFX Context should be destroyed
     *      as soon as the canvas is removed from it's parent container
     */
    boolean shouldAutoKillGfxContext();
    
    /**
     *  Destroy GFX context
     */
    void killGfxContext();

    /**
     * Set the desired update/redraw frequency of this canvas. If
     * setDrawWhenDirty was called with true, this frequency is just a cap to
     * possible redraw rate.
     * 
     * @param fps
     *            the desired target rate in frames per second
     */
    void setTargetRate(int fps);

    /**
     * @return the desired target rate in frames per second
     * @see #setTargetRate(int)
     */
    int getTargetSyncRate();

    /**
     * 
     * 
     * @param whenDirty
     *            true if we should only draw if the canvas is flagged as dirty.
     *            false if we should draw on every loop regardless of dirty
     *            state.
     */
    void setDrawWhenDirty(boolean whenDirty);

    /**
     * 
     * @return true if we should only draw if the canvas is flagged as dirty.
     *         false if we should draw on every loop regardless of dirty state.
     */
    boolean isDrawWhenDirty();

    /**
     * Force this canvas to be flagged as dirty. See
     * {@link #setDrawWhenDirty(boolean)}.
     */
    void makeDirty();
}
