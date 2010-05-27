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

package com.jmex.awt.lwjgl;

import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBMultisample;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

import com.jme.input.InputSystem;
import com.jme.renderer.ColorRGBA;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.JMECanvasImplementor;
import com.jme.system.lwjgl.LWJGLDisplaySystem;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;

/**
 * <code>LWJGLCanvas</code>
 * 
 * @author Joshua Slack
 * @version $Id: LWJGLCanvas.java 4133 2009-03-19 20:40:11Z blaine.dev $
 */
public class LWJGLCanvas extends AWTGLCanvas implements JMECanvas {

    private static final Logger logger = Logger.getLogger(LWJGLCanvas.class
            .getName());

    private static final long serialVersionUID = 1L;

    private JMECanvasImplementor impl;

    private boolean updateInput = false;
    
    private double syncNS = 0;
    private int syncRate = 0;

    private long lastRender = 0;

    private boolean shouldAutoKillContext = true;
    private boolean glInitialized = false;
    private boolean drawWhenDirty = false;
    private boolean dirty = true;

    public LWJGLCanvas() throws LWJGLException {
        super(generatePixelFormat());
    }

    private static PixelFormat generatePixelFormat() {
        return ((LWJGLDisplaySystem) DisplaySystem.getDisplaySystem())
                .getFormat();
    }

    public void setImplementor(JMECanvasImplementor impl) {
        this.impl = impl;
    }

    @Override
    protected void initGL() {
        
        if( glInitialized ){
            return;
        }
        glInitialized = true;
                
        try {
            LWJGLDisplaySystem display = (LWJGLDisplaySystem) DisplaySystem
                    .getDisplaySystem();
            display.switchContext(this);

            // Complete canvas configuration.
            Dimension size = this.getSize();
            display.initForCanvas(size.width, size.height);

            impl.doSetup();

            if (display.getMinSamples() != 0
                    && GLContext.getCapabilities().GL_ARB_multisample) {
                GL11.glEnable(ARBMultisample.GL_MULTISAMPLE_ARB);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in initGL()", e);
        }
    }

    @Override
    protected void paintGL() {

        try {
            ((LWJGLDisplaySystem) DisplaySystem.getDisplaySystem())
                    .switchContext(this);
            
            if (updateInput) {
                InputSystem.update();
            }

            GameTaskQueueManager.getManager().getQueue(
                    GameTaskQueue.UPDATE).execute();

            impl.doUpdate();

            if (!drawWhenDirty || dirty) {
                GameTaskQueueManager.getManager().getQueue(
                        GameTaskQueue.RENDER).execute();

                impl.doRender();

                swapBuffers();
                dirty = false;
            }
        } catch (LWJGLException e) {
            logger.log(Level.SEVERE, "Exception in paintGL()", e);
        }

        // sync
        if (syncRate > 0) {
            long sinceLast = System.nanoTime() - lastRender;
            if (sinceLast < syncNS) {
                try {
                    Thread.sleep((Math
                            .round((syncNS - sinceLast) / 1000000L)));
                } catch (InterruptedException e) {
                }
            }
            lastRender = System.nanoTime();
        }

        repaint();
    }

    public void setBackground(Color bgColor) {
        impl.setBackground(makeColorRGBA(bgColor));
    }

    protected ColorRGBA makeColorRGBA(Color color) {
        return new ColorRGBA(color.getRed() / 255f, color.getGreen() / 255f,
                color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    /*
     * (non-Javadoc)
     * @see com.jmex.awt.JMECanvas#doUpdateInput()
     */
    public boolean isUpdateInput() {
        return updateInput;
    }

    /*
     * (non-Javadoc)
     * @see com.jmex.awt.JMECanvas#setUpdateInput(boolean)
     */
    public void setUpdateInput(boolean doUpdate) {
        updateInput = doUpdate;
    }

    public void setTargetRate(int fps) {
        this.syncRate = fps;
        this.syncNS = 1000000000.0 / syncRate;
    }

    public int getTargetSyncRate() {
        return syncRate;
    }


    public void setDrawWhenDirty(boolean whenDirty) {
        this.drawWhenDirty = whenDirty;
    }

    public boolean isDrawWhenDirty() {
        return drawWhenDirty;
    }

    public void makeDirty() {
        dirty = true;
    }
    
    @Override
    public void removeNotify() {
        if ( shouldAutoKillContext ) {
            glInitialized = false;
            super.removeNotify();
}
    }

    public void setAutoKillGfxContext( boolean shouldAutoKillGfxContext ) {
        this.shouldAutoKillContext = shouldAutoKillGfxContext;
    }

    public boolean shouldAutoKillGfxContext() {
        return shouldAutoKillContext;
    }

    public void killGfxContext() {
        glInitialized = false;
        super.removeNotify();
    }
}