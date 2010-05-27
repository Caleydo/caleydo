package com.jme.util.jogl;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * @see http://jsourcery.com/api/java.net/jogl/1.1.0/com/sun/opengl/impl/mipmap/Mipmap.source.html#jj888377600
 */
public class JOGLUtil {

    public static int nearestPower(int value) {
        int i = 1;
        if (value == 0) {
            return (-1);
        }

        for (;;) {
            if (value == 1) {
                return (i);
            } else if (value == 3) {
                return (i * 4);
            }

            value = value >> 1;
            i *= 2;
        }
    }

    public static int bytesPerPixel(final int format, final int type) {
        if (format == GL.GL_RGB && type == GL.GL_UNSIGNED_BYTE) {
            return 3;
        } else if (format == GL.GL_RGBA && type == GL.GL_UNSIGNED_BYTE) {
            return 4;
        } else {
            throw new UnsupportedOperationException("bytesPerPixel foramt="
                    + format + ", type=" + type);
        }
    }

    public static int glGetIntegerv(final int pname) {
        final GL gl = GLU.getCurrentGL();
        final int params[] = new int[1];
        gl.glGetIntegerv(pname, params, 0);
        return params[0];
    }

}
