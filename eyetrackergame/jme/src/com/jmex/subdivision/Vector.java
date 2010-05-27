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
/*
 * Created on 2006-okt-29
 */
package com.jmex.subdivision;

import java.nio.FloatBuffer;

/**
 * Helper class for SubdivisionBatch to do vector math with
 * any size on the vectors
 * 
 * @author Tobias (tobbe.a removethisoryourclientgoesape gmail.com)
 */
public class Vector {
	public float[] elem;
	public int size;
	
	public Vector(int size) {
		elem = new float[size];
		this.size = size;
		for (int i = 0; i<size ; i++)
			elem[i] = 0f;
	}
	
	public Vector addLocal(Vector vec) {
		for (int i = 0; i<size; i++)
			this.elem[i] += vec.elem[i];
		return this;
	}

	public Vector multLocal(float factor) {
		for (int i = 0; i<size; i++)
			this.elem[i] *= factor;
		return this;
	}
	
	public Vector interpolate(Vector vec1, Vector vec2, float amount) {
		for (int i = 0; i<size; i++)
			this.elem[i] = vec1.elem[i]*(1f - amount) + vec2.elem[i]*amount;
		return this;
	}
	
	public Vector interpolate(Vector vec1, Vector vec2) {
		for (int i = 0; i<size; i++)
			this.elem[i] = (vec1.elem[i] + vec2.elem[i]) * 0.5f;
		return this;
	}
	
	public Vector populateFromBuffer(FloatBuffer buf, int index) {
		for (int i = 0; i<size; i++)
			elem[i] = buf.get(index*size + i);
		return this;
	}
	
	public FloatBuffer putInBuffer(FloatBuffer buf, int index) {
		for (int i = 0; i<size; i++)
			buf.put(index*size + i, elem[i]);
		return buf;
		
	}
}

