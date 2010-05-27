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

import com.jme.util.Timer;

/**
 * Interface implemented by any providers of system-level implementations.
 * DisplaySystem and Timer use standard jar file Service-provider lookup,
 * documented here:
 * 
 * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider">Service Provider</a>
 * @author Andy Lubbers
 */
public interface SystemProvider {

    /**
     * <code>getProviderIdentifier</code> returns a unique identifier for this
     * system.
     * 
     * @return a globally unique identifier for the implementation system
     */
    String getProviderIdentifier();

    /**
     * Returns a valid DisplaySystem for the current system.
     *
     * @return a valid displaysystem for the implementation system
     */
    DisplaySystem getDisplaySystem();

    /**
     * Often the display system is in a static field. This should null out this
     * field so that subsequent calls are forced to make a new displaysystem.
     */
    void disposeDisplaySystem();

    /**
     * Returns a hight resolution timer for the current system.
     *
     * @return a high resolution timer for the implementation system
     */
    Timer getTimer();

}
