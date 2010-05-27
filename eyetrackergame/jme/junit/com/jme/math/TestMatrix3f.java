/*
 * Copyright (c) 2003-2008 jMonkeyEngine
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
package com.jme.math;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

/*
 * $Log: TestMatrix3f.java,v $
 * Revision 1.1  2008/01/07 16:14:00  irrisor
 * topic 6810: moved test code
 *
 */

/**
 * @see Matrix4f
 */
public class TestMatrix3f {

    @Test
    public void testTranspose() {
        Matrix3f a = new Matrix3f();
        Quaternion q = new Quaternion();
        q.fromAngles(0.1f, 0.2f, 0.3f);
        a.set(q);
        Matrix3f b = a.transposeNew();
        assertTrue(Matrix3f.equalIdentity(b.mult(a)));
        assertTrue(Matrix3f.equalIdentity(a.mult(b)));
    }

    @Test
    public void testScale() {
        Matrix3f a = new Matrix3f(0, 1, 0, -1, 0, 0, 0, 0, 1);
        Matrix3f b = a.transposeNew();
        a.scale(new Vector3f(2, 2, 2));
        assertTrue(a.mult(new Vector3f(1, 0, 0)).lengthSquared() == 4);
        b.scale(new Vector3f(0.5f, 0.5f, 0.5f));
        assertTrue(Matrix3f.equalIdentity(a.mult(b)));
    }

}