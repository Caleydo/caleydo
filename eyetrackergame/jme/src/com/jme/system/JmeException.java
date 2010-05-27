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

/**
 * <code>JmeException</code> handles all exceptions that could be thrown and
 * should be handled in the client software. By extending
 * <code>RuntimeException</code> all functionality of the built-in exception
 * system is kept, but by applying the jME name it will be easier to visualize
 * what is causing the exception.
 * 
 * @author Mark Powell
 * @version $Id: JmeException.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class JmeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor creates a new <code>JmeException</code> with out
     * any description of what caused the exception.
     */
    public JmeException() {
        super();
    }

    /**
     * Constructor creates a new <code>JmeException</code> with a description
     * of the exception that will be displayed when it's thrown.
     * 
     * @param desc
     *            the description of this exception.
     */
    public JmeException(String desc) {
        super(desc);
    }

    /**
     * Constructor creates a new <code>JmeException</code> with the cause of
     * this exception.
     * 
     * @param cause
     *            the cause of this exception.
     */
    public JmeException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor creates a new <code>JmeException</code> with a description
     * of the exception that will be displayed when it's thrown and the cause of
     * this exception.
     * 
     * @param desc
     *            the description of this exception.
     * @param cause
     *            the cause of this exception
     */
    public JmeException(String desc, Throwable cause) {
        super(desc, cause);
    }
}

