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

package com.jme.scene.lod;

import java.util.Vector;

/**
 * <code>ExVector</code> is an Extended Vector that does not allow multiple
 * instances.
 * 
 * @author Joshua Slack
 * @version $Id: ExVector.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */

public class ExVector extends Vector<Object> {

	private static final long serialVersionUID = 1L;

	/**
	 * Basic constructor.
	 */
	public ExVector() {
		super();
	}

	/**
	 * Constructor allowing you to set initialCapacity
	 * @param initialCapacity how large of a vector to start with
	 */
	public ExVector(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructor allowing you to set initialCapacity
	 * @param initialCapacity how large of a vector to start with
	 * @param capacityIncrement how much space to add when we run low
	 */
	public ExVector(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	}

	/**
	 * Add the given object to the vector if and only if it is not already present.
	 * @param obj Object to add
	 * @return boolean if the object was added.
	 */
	public boolean add(Object obj) {
		if (indexOf(obj) >= 0)
			return false;

		return super.add(obj);
	}

	/**
	 * Retrieve an object from this vector.
	 * @param obj the object to retrieve.  This uses indexOf to determine location
	 *            and then get(index) to retrieve it
	 * @return the object if present, null if not.
	 */
	public Object get(Object obj) {
		int i = indexOf(obj);
		if (i < 0)
			return null;
		
		return get(i);
	}
}
