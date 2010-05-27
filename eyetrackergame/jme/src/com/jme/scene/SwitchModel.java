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

package com.jme.scene;

import com.jme.util.export.Savable;

/**
 * <code>SwitchModel</code> defines an interface for selection of switch
 * nodes. Implementing classes provide the way to set the selection criteria, as
 * well as define what this criteria is. The interface simply defines a set
 * method that accepts an <code>Object</code>. What is done with the provided
 * object is defined by the implementor. The child that the switch node is to
 * make active should be defined from the <code>getSwitchChild</code> method.
 * 
 * @see com.jme.scene.SwitchNode
 * @author Mark Powell
 * @version $Id: SwitchModel.java 4636 2009-08-28 14:52:25Z skye.book $
 */
public interface SwitchModel extends Savable {
	/**
	 * 
	 * <code>getSwitchChild</code> returns the index of the node that should
	 * be set active in the <code>SwitchNode</code>.
	 * 
	 * @return the index of the active child.
	 */
	public int getSwitchChild();

	/**
	 * 
	 * <code>set</code> provides a generic set method for implementing
	 * classes. The value set can be anything, and it is the responsibility of
	 * the implementing to define what this method will do for the particular
	 * implementation.
	 * 
	 * @param value
	 *            the value to set.
	 */
	public void set(Object value);
}