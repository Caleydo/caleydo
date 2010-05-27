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

package com.jme.util.geom;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

class VertexData implements Comparable {

	public int smoothGroup = -1;

	public Vector3f coord = null;

	public Vector3f normal = null;

	public ColorRGBA color4 = null;

	public Vector2f[] texCoords = null;

	public VertexData() {
		super();
	}

	public VertexData(VertexData vd) {
		smoothGroup = vd.smoothGroup;
		if (vd.coord != null)
			coord = new Vector3f(vd.coord);
		if (vd.normal != null)
			normal = new Vector3f(vd.normal);
		if (vd.color4 != null)
			color4 = new ColorRGBA(vd.color4);
		if (vd.texCoords != null) {
			texCoords = new Vector2f[vd.texCoords.length];
			for (int i = 0; i < texCoords.length; i++) {
				texCoords[i] = vd.texCoords[i] == null ? null : new Vector2f(
						vd.texCoords[i]);
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (coord != null)
			sb.append("P").append(coord);
		if (normal != null)
			sb.append("N").append(normal);
		if (color4 != null)
			sb.append("C").append(color4);
		for (int i = 0; texCoords != null && i < texCoords.length; i++) {
			sb.append("T").append(texCoords[i]);
		}
		if (smoothGroup >= 0)
			sb.append("S").append(smoothGroup);
		sb.append("]");
		return sb.toString();
	}

	public int hashCode() {
		int h = 0;
		if (coord != null)
			h ^= coord.hashCode();
		if (normal != null)
			h ^= normal.hashCode();
		if (color4 != null)
			h ^= color4.hashCode();
		// texcoords and smoothgroup do not go into hashcode, above should be
		// enough
		return h;
	}

	public int compareTo(Object o) {
		VertexData vd = (VertexData) o;
		int d;
		d = compare(coord, vd.coord);
		if (d != 0)
			return d;
		d = compare(normal, vd.normal);
		if (d != 0)
			return d;
		d = compare(color4, vd.color4);
		if (d != 0)
			return d;
		d = compare(texCoords, vd.texCoords);
		if (d != 0)
			return d;
		d = smoothGroup - vd.smoothGroup;
		if (d != 0)
			return d;
		return 0;
	}

	public boolean equals(Object o) {
		VertexData vd = (VertexData) o;
		return isEqual(coord, vd.coord) && isEqual(normal, vd.normal)
				&& isEqual(color4, vd.color4)
				&& isEqual(texCoords, vd.texCoords)
				&& smoothGroup == vd.smoothGroup;
	}

	private static int fdiff(float diff) {
		if (diff < 0)
			return -1;
		else if (diff > 0)
			return 1;
		else
			return 0;
	}

	private int compare(Vector2f t1, Vector2f t2) {
		float d;
		if (t1 == null && t2 == null)
			return 0;
		if (t1 == null)
			return -1;
		if (t2 == null)
			return 1;
		d = t1.x - t2.x;
		if (d != 0)
			return fdiff(d);
		d = t1.y - t2.y;
		if (d != 0)
			return fdiff(d);
		return 0;
	}

	private int compare(Vector2f[] t1, Vector2f[] t2) {
		int d;
		if (t1 == null && t2 == null)
			return 0;
		if (t1 == null)
			return -1;
		if (t2 == null)
			return 1;
		if (t1.length != t2.length)
			return t1.length - t2.length;
		for (int i = 0; i < t1.length; i++) {
			d = compare(t1[i], t2[i]);
			if (d != 0)
				return d;
		}
		return 0;
	}

	private int compare(Vector3f t1, Vector3f t2) {
		float d;
		if (t1 == null && t2 == null)
			return 0;
		if (t1 == null)
			return -1;
		if (t2 == null)
			return 1;
		d = t1.x - t2.x;
		if (d != 0)
			return fdiff(d);
		d = t1.y - t2.y;
		if (d != 0)
			return fdiff(d);
		d = t1.z - t2.z;
		if (d != 0)
			return fdiff(d);
		return 0;
	}

	private int compare(ColorRGBA t1, ColorRGBA t2) {
		float d;
		if (t1 == null && t2 == null)
			return 0;
		if (t1 == null)
			return -1;
		if (t2 == null)
			return 1;
		d = t1.r - t2.r;
		if (d != 0)
			return fdiff(d);
		d = t1.g - t2.g;
		if (d != 0)
			return fdiff(d);
		d = t1.b - t2.b;
		if (d != 0)
			return fdiff(d);
		d = t1.a - t2.a;
		if (d != 0)
			return fdiff(d);
		return 0;
	}

	private static boolean isEqual(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	private static boolean isEqual(Object[] o1, Object[] o2) {
		if (o1 == null)
			return o2 == null;
		if (o1.length != o2.length)
			return false;
		for (int i = 0; i < o1.length; i++) {
			if (!isEqual(o1[i], o2[i]))
				return false;
		}
		return true;
	}
}