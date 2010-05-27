package com.jme.util.geom;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

public class VertKey {

    private Vector3f vert;
    private Vector3f norm;
    private ColorRGBA color;
    private Vector2f[] texs;
    private int options;

    public VertKey(Vector3f vert, Vector3f norm, ColorRGBA color, Vector2f[] texs, int options) {
        this.vert = vert;
        if ((options & GeometryTool.MV_SAME_NORMALS) != 0) {
            this.norm = norm;
        }
        if ((options & GeometryTool.MV_SAME_COLORS) != 0) {
            this.color = color;
        }
        if ((options & GeometryTool.MV_SAME_TEXS) != 0) {
            this.texs = texs;
        }
        this.options = options;
    }

    @Override
    public int hashCode() {
        int rez = vert.hashCode();
        if ((options & GeometryTool.MV_SAME_NORMALS) != 0 && norm != null) {
            rez += 37 * rez + Float.floatToIntBits(norm.x);
            rez += 37 * rez + Float.floatToIntBits(norm.y);
            rez += 37 * rez + Float.floatToIntBits(norm.z);
        }
        if ((options & GeometryTool.MV_SAME_COLORS) != 0 && color != null) {
            rez += 37 * rez + Float.floatToIntBits(color.r);
            rez += 37 * rez + Float.floatToIntBits(color.g);
            rez += 37 * rez + Float.floatToIntBits(color.b);
            rez += 37 * rez + Float.floatToIntBits(color.a);
        }
        if ((options & GeometryTool.MV_SAME_TEXS) != 0 && texs != null) {
            for (int x = 0; x < texs.length; x++) {
                if (texs[x] != null) {
                    rez += 37 * rez + Float.floatToIntBits(texs[x].x);
                    rez += 37 * rez + Float.floatToIntBits(texs[x].y);
                }
            }
        }
        return rez;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VertKey)) return false;
        
        VertKey other = (VertKey)obj;
        
        if (other.options != options) return false;
        if (!other.vert.equals(vert)) return false;
        
        if ((options & GeometryTool.MV_SAME_NORMALS) != 0) {
            if (norm != null) {
                if (!norm.equals(other.norm)) return false;
            } else if (other.norm != null) return false;
        }
        
        if ((options & GeometryTool.MV_SAME_COLORS) != 0) {
            if (color != null) {
                if (!color.equals(other.color)) return false;
            } else if (other.color != null) return false;
        }
        
        if ((options & GeometryTool.MV_SAME_TEXS) != 0) {
            if (texs != null) {
                if (other.texs == null || other.texs.length != texs.length) return false;
                for (int x = 0; x < texs.length; x++) {
                    if (texs[x] != null) {
                        if (!texs[x].equals(other.texs[x])) return false;
                    } else if (other.texs[x] != null) return false;
                }
            } else if (other.texs != null) return false;
        }

        return true;
    }
}
