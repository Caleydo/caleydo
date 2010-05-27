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
package com.jme.system.dummy;

import com.jme.system.DisplaySystem;
import com.jme.system.SystemProvider;
import com.jme.util.NanoTimer;
import com.jme.util.Timer;

/**
 * <p>
 * SystemProvider for DummyDisplaySystem.
 * </p>
 * <p>
 * It is a basic SystemProvider which allows to specify which DisplaySystem owns
 * it and what Timer to use. If a Timer is not providen, a NanoTimer will be
 * used.
 * <p>
 */
public class DummySystemProvider implements SystemProvider {

    /**
     * The DummySystemProvider identifier
     */
    public static final String DUMMY_SYSTEM_IDENTIFIER = "dummy";

    /**
     * The timer hold by this SystemProvider.
     */
    protected Timer timer = null;

    /**
     * The DisplaySystem that this SystemProvider belongs to.
     */
    protected DisplaySystem displaySystem = null;

    /**
     * Creates a new DummySystemProvider
     */
    public DummySystemProvider() {
        this(null, new NanoTimer());
    }

    /**
     * Creates a new DummySystemProvider
     * 
     * @param displaySystem
     *            The DisplaySystem that this SystemProvider belongs to.
     */
    public DummySystemProvider(DisplaySystem displaySystem) {
        this(displaySystem, new NanoTimer());
    }

    /**
     * Creates a new DummySystemProvider
     * 
     * @param displaySystem
     *            The DisplaySystem that this SystemProvider belongs to.
     * @param timer
     *            The timer hold by this SystemProvider.
     */
    public DummySystemProvider(DisplaySystem displaySystem, Timer timer) {
        this.timer = timer;
        this.displaySystem = displaySystem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.system.SystemProvider#getProviderIdentifier()
     */
    public String getProviderIdentifier() {
        return DUMMY_SYSTEM_IDENTIFIER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.system.SystemProvider#getDisplaySystem()
     */
    public DisplaySystem getDisplaySystem() {
        if (displaySystem == null) {
            displaySystem = new DummyDisplaySystem();
        }
        return displaySystem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.jme.system.SystemProvider#getTimer()
     */
    public Timer getTimer() {
        return timer;
    }

    public void disposeDisplaySystem() {
        // TODO Auto-generated method stub
    }

}
