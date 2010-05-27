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

package com.jme.system.lwjgl;

import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.input.Cursor;

import com.jme.util.geom.BufferUtils;

/**
 * @author Gregg Patton
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LWJGLStandardCursor extends Cursor {
    private static final Logger logger = Logger
            .getLogger(LWJGLStandardCursor.class.getName());

    public static Cursor cursor;

    private static IntBuffer cursor_image;
    private static int size = 32;

    static {
        cursor_image = BufferUtils.createIntBuffer(size * size);

        int row = 0;
        cursor_image.put(row * size + 8, 0xFF000000);
        cursor_image.put(row * size + 9, 0xFF000000);

        row++;
        cursor_image.put(row * size + 7, 0xFF000000);
        cursor_image.put(row * size + 8, 0xFFFFFFFF);
        cursor_image.put(row * size + 9, 0xFFFFFFFF);
        cursor_image.put(row * size + 10, 0xFF000000);

        row++;
        cursor_image.put(row * size + 7, 0xFF000000);
        cursor_image.put(row * size + 8, 0xFFFFFFFF);
        cursor_image.put(row * size + 9, 0xFFFFFFFF);
        cursor_image.put(row * size + 10, 0xFF000000);

        row++;
        cursor_image.put(row * size + 6, 0xFF000000);
        cursor_image.put(row * size + 7, 0xFFFFFFFF);
        cursor_image.put(row * size + 8, 0xFFFFFFFF);
        cursor_image.put(row * size + 9, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 6, 0xFF000000);
        cursor_image.put(row * size + 7, 0xFFFFFFFF);
        cursor_image.put(row * size + 8, 0xFFFFFFFF);
        cursor_image.put(row * size + 9, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFF000000);
        cursor_image.put(row * size + 5, 0xFF000000);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFFFFFFFF);
        cursor_image.put(row * size + 8, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFF000000);
        cursor_image.put(row * size + 5, 0xFF000000);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFFFFFFFF);
        cursor_image.put(row * size + 8, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFF000000);
        cursor_image.put(row * size + 4, 0xFF000000);
        cursor_image.put(row * size + 5, 0xFFFFFFFF);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFF000000);
        cursor_image.put(row * size + 5, 0xFFFFFFFF);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFFFFFFFF);
        cursor_image.put(row * size + 5, 0xFFFFFFFF);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFF000000);
        cursor_image.put(row * size + 8, 0xFF000000);
        cursor_image.put(row * size + 9, 0xFF000000);
        cursor_image.put(row * size + 10, 0xFF000000);
        cursor_image.put(row * size + 11, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFFFFFFFF);
        cursor_image.put(row * size + 5, 0xFFFFFFFF);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFFFFFFFF);
        cursor_image.put(row * size + 8, 0xFFFFFFFF);
        cursor_image.put(row * size + 9, 0xFFFFFFFF);
        cursor_image.put(row * size + 10, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFFFFFFFF);
        cursor_image.put(row * size + 5, 0xFFFFFFFF);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFFFFFFFF);
        cursor_image.put(row * size + 8, 0xFFFFFFFF);
        cursor_image.put(row * size + 9, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFFFFFFFF);
        cursor_image.put(row * size + 5, 0xFFFFFFFF);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFFFFFFFF);
        cursor_image.put(row * size + 8, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFFFFFFFF);
        cursor_image.put(row * size + 5, 0xFFFFFFFF);
        cursor_image.put(row * size + 6, 0xFFFFFFFF);
        cursor_image.put(row * size + 7, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFFFFFFFF);
        cursor_image.put(row * size + 5, 0xFFFFFFFF);
        cursor_image.put(row * size + 6, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFFFFFFFF);
        cursor_image.put(row * size + 5, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFFFFFFFF);
        cursor_image.put(row * size + 4, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFFFFFFFF);
        cursor_image.put(row * size + 3, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFFFFFFFF);
        cursor_image.put(row * size + 2, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);
        cursor_image.put(row * size + 1, 0xFF000000);

        row++;
        cursor_image.put(row * size, 0xFF000000);

        try {
            cursor = new LWJGLStandardCursor();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to create LWJGLStandardCursor", e);
        }
    }

    private LWJGLStandardCursor()
        throws Exception {
        super(size, size, 0, 19, 1, cursor_image, null);
    }

}
