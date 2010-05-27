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

package com.jme.renderer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.jme.math.FastMath;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>ColorRGBA</code> defines a color made from a collection of
 * red, green and blue values. An alpha value determines is transparency.
 * All values must be between 0 and 1. If any value is set higher or lower
 * than these constraints they are clamped to the min or max. That is, if
 * a value smaller than zero is set the value clamps to zero. If a value
 * higher than 1 is passed, that value is clamped to 1. However, because the
 * attributes r, g, b, a are public for efficiency reasons, they can be
 * directly modified with invalid values. The client should take care when
 * directly addressing the values. A call to clamp will assure that the values
 * are within the constraints.
 * @author Mark Powell
 * @version $Id: ColorRGBA.java 4763 2009-11-15 01:49:00Z blaine.dev $
 */
public class ColorRGBA implements Externalizable, Savable, Cloneable {

    private static final long serialVersionUID = 1L;
    /**
     * the color black (0,0,0).
     */
    public static final ColorRGBA black = new ColorRGBA(0f, 0f, 0f, 1f);
    /**
     * the color white (1,1,1).
     */
    public static final ColorRGBA white = new ColorRGBA(1f, 1f, 1f, 1f);
    /**
     * the color gray (.2,.2,.2).
     */
    public static final ColorRGBA darkGray = new ColorRGBA(0.2f, 0.2f, 0.2f, 1.0f);
    /**
     * the color gray (.5,.5,.5).
     */
    public static final ColorRGBA gray = new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f);
    /**
     * the color gray (.8,.8,.8).
     */
    public static final ColorRGBA lightGray = new ColorRGBA(0.8f, 0.8f, 0.8f, 1.0f);
    /**
     * the color red (1,0,0).
     */
    public static final ColorRGBA red = new ColorRGBA(1f, 0f, 0f, 1f);
    /**
     * the color green (0,1,0).
     */
    public static final ColorRGBA green = new ColorRGBA(0f, 1f, 0f, 1f);
    /**
     * the color blue (0,0,1).
     */
    public static final ColorRGBA blue = new ColorRGBA(0f, 0f, 1f, 1f);
    /**
     * the color yellow (1,1,0).
     */
    public static final ColorRGBA yellow = new ColorRGBA(1f, 1f, 0f, 1f);
    /**
     * the color magenta (1,0,1).
     */
    public static final ColorRGBA magenta = new ColorRGBA(1f, 0f, 1f, 1f);
    /**
     * the color cyan (0,1,1).
     */
    public static final ColorRGBA cyan = new ColorRGBA(0f, 1f, 1f, 1f);
    /**
     * the color orange (251/255, 130/255,0).
     */
    public static final ColorRGBA orange = new ColorRGBA(251f/255f, 130f/255f, 0f, 1f);
    /**
     * the color brown (65/255, 40/255, 25/255).
     */
    public static final ColorRGBA brown = new ColorRGBA(65f/255f, 40f/255f, 25f/255f, 1f);
    /**
     * the color pink (1, 0.68, 0.68).
     */
    public static final ColorRGBA pink = new ColorRGBA(1f, 0.68f, 0.68f, 1f);

    /**
     * The red component of the color.
     */
    public float r;
    /**
     * The green component of the color.
     */
    public float g;
    /**
     * the blue component of the color.
     */
    public float b;
    /**
     * the alpha component of the color.  0 is transparent and 1 is opaque
     */
    public float a;

    /**
     * The <code>ColorRGBA</code> constructor instantiates a new
     * <code>ColorRGBA</code> object. The default color created
     * is white.
     *
     */
    public ColorRGBA() {
        r = g = b = a = 1.0f;
    }

    /**
     * Constructor instantiates a new <code>ColorRGBA</code> object. The
     * values are defined as passed parameters. These values are then clamped
     * to insure that they are between 0 and 1.
     * @param r the red component of this color.
     * @param g the green component of this color.
     * @param b the blue component of this color.
     * @param a the alpha component of this color.
     */
    public ColorRGBA(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
//      clamp();
    }

    /**
     * Copy constructor creates a new <code>ColorRGBA</code> object, based on
     * a provided color.
     * @param rgba the <code>ColorRGBA</code> object to copy.
     */
    public ColorRGBA(ColorRGBA rgba) {
        this.a = rgba.a;
        this.r = rgba.r;
        this.g = rgba.g;
        this.b = rgba.b;
//        clamp();
    }

    /**
     *
     * <code>set</code> sets the RGBA values of this color. The values are then
     * clamped to insure that they are between 0 and 1.
     *
     * @param r the red component of this color.
     * @param g the green component of this color.
     * @param b the blue component of this color.
     * @param a the alpha component of this color.
     */
    public void set(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
//      clamp();
    }

    /**
     * <code>set</code> sets the values of this color to those set by a parameter
     * color.
     *
     * @param rgba ColorRGBA the color to set this color to.
     * @return this
     */
    public ColorRGBA set(ColorRGBA rgba) {
      if(rgba == null) {
          r = 0;
          g = 0;
          b = 0;
          a = 0;
      } else {
          r = rgba.r;
          g = rgba.g;
          b = rgba.b;
          a = rgba.a;
      }
        return this;
    }

    /**
     * <code>clamp</code> insures that all values are between 0 and 1. If any
     * are less than 0 they are set to zero. If any are more than 1 they are
     * set to one.
     *
     */
    public void clamp() {
        if (r < 0) {
            r = 0;
        } else if (r > 1) {
            r = 1;
        }

        if (g < 0) {
            g = 0;
        } else if (g > 1) {
            g = 1;
        }

        if (b < 0) {
            b = 0;
        } else if (b > 1) {
            b = 1;
        }

        if (a < 0) {
            a = 0;
        } else if (a > 1) {
            a = 1;
        }
    }

    /**
     *
     * <code>getColorArray</code> retrieves the color values of this object as
     * a four element float array.
     * @return the float array that contains the color elements.
     */
    public float[] getColorArray() {
        return new float[] {r,g,b,a};
    }

    /**
     * Stores the current r/g/b/a values into the tempf array.  The tempf array must have a
     * length of 4 or greater, or an array index out of bounds exception will be thrown.
     * @param store The array of floats to store the values into.
     * @return The float[] after storage.
     */
    public float[] getColorArray(float[] store) {
        store[0]=r;
        store[1]=g;
        store[2]=b;
        store[3]=a;
        return store;
    }

    /**
     * Sets this color to the interpolation by changeAmnt from this to the finalColor
     * this=(1-changeAmnt)*this + changeAmnt * finalColor
     * @param finalColor The final color to interpolate towards
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from this towards finalColor
     */
    public void interpolate(ColorRGBA finalColor,float changeAmnt){
        this.r=(1-changeAmnt)*this.r + changeAmnt*finalColor.r;
        this.g=(1-changeAmnt)*this.g + changeAmnt*finalColor.g;
        this.b=(1-changeAmnt)*this.b + changeAmnt*finalColor.b;
        this.a=(1-changeAmnt)*this.a + changeAmnt*finalColor.a;
    }

    /**
     * Sets this color to the interpolation by changeAmnt from beginColor to finalColor
     * this=(1-changeAmnt)*beginColor + changeAmnt * finalColor
     * @param beginColor The begining color (changeAmnt=0)
     * @param finalColor The final color to interpolate towards (changeAmnt=1)
     * @param changeAmnt An amount between 0.0 - 1.0 representing a precentage
     *  change from beginColor towards finalColor
     */
    public void interpolate(ColorRGBA beginColor,ColorRGBA finalColor,float changeAmnt){
        this.r=(1-changeAmnt)*beginColor.r + changeAmnt*finalColor.r;
        this.g=(1-changeAmnt)*beginColor.g + changeAmnt*finalColor.g;
        this.b=(1-changeAmnt)*beginColor.b + changeAmnt*finalColor.b;
        this.a=(1-changeAmnt)*beginColor.a + changeAmnt*finalColor.a;
    }

    /**
     *
     * <code>randomColor</code> is a utility method that generates a random
     * color.
     *
     * @return a random color.
     */
    public static ColorRGBA randomColor() {
      ColorRGBA rVal = new ColorRGBA(0, 0, 0, 1);
      rVal.r = FastMath.nextRandomFloat();
      rVal.g = FastMath.nextRandomFloat();
      rVal.b = FastMath.nextRandomFloat();
      rVal.clamp();
      return rVal;
    }

    /**
     * Multiplies each r/g/b/a of this color by the r/g/b/a of the given color and
     * returns the result as a new ColorRGBA.  Used as a way of combining colors and lights.
     * @param c The color to multiply.
     * @return The new ColorRGBA.  this*c
     */
    public ColorRGBA mult(ColorRGBA c) {
        return new ColorRGBA(c.r * r, c.g * g, c.b * b, c.a * a);
    }

    /**
     * Multiplies each r/g/b/a of this color by the r/g/b/a of the given color and
     * returns the result as a new ColorRGBA.  Used as a way of combining colors and lights.
     * @param scalar scalar to multiply with
     * @return The new ColorRGBA.  this*c
     */
    public ColorRGBA multLocal(float scalar) {
        this.r *= scalar;
        this.g *= scalar;
        this.b *= scalar;
        this.a *= scalar;
        return this;
    }

    /**
     * Adds each r/g/b/a of this color by the r/g/b/a of the given color and
     * returns the result as a new ColorRGBA.
     * @param c The color to add.
     * @return The new ColorRGBA.  this+c
     */
    public ColorRGBA add(ColorRGBA c) {
        return new ColorRGBA(c.r + r, c.g + g, c.b + b, c.a + a);
    }

    /**
     * Multiplies each r/g/b/a of this color by the r/g/b/a of the given color and
     * returns the result as a new ColorRGBA.  Used as a way of combining colors and lights.
     * @param c The color to multiply.
     * @return The new ColorRGBA.  this*c
     */
    public ColorRGBA addLocal(ColorRGBA c) {
        set(c.r + r, c.g + g, c.b + b, c.a + a);
        return this;
    }

    /**
     * <code>toString</code> returns the string representation of this color.
     * The format of the string is:<br>
     * [R=RR.RRRR, G=GG.GGGG, B=BB.BBBB, A=AA.AAAA]
     * <p>
     * If you want to display a class name, then use
     * Vector3f.class.getName() or getClass().getName().
     * </p>
     *
     * @return the string representation of this color.
     */
    public String toString() {
        return "[R="+r+", G="+g+", B="+b+", A="+a+"]";
    }

    @Override
    public ColorRGBA clone() {
        try {
            return (ColorRGBA) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // can not happen
        }
    }

    /**
     * Saves this ColorRGBA into the given float[] object.
     * 
     * @param floats
     *            The float[] to take this ColorRGBA. If null, a new float[4] is
     *            created.
     * @return The array, with R, G, B, A float values in that order
     */
    public float[] toArray(float[] floats) {
        if (floats == null) {
            floats = new float[4];
        }
        floats[0] = r;
        floats[1] = g;
        floats[2] = b;
        floats[3] = a;
        return floats;
    }

    /**
     * <code>equals</code> returns true if this color is logically equivalent
     * to a given color. That is, if the values of the two colors are the same.
     * False is returned otherwise.
     * @param o the object to compare againts.
     * @return true if the colors are equal, false otherwise.
     */
    public boolean equals(Object o) {
        if( !(o instanceof ColorRGBA) ) {
            return false;
        }

        if(this == o) {
            return true;
        }

        ColorRGBA comp = (ColorRGBA)o;
        if (Float.compare(r, comp.r) != 0) return false;
        if (Float.compare(g, comp.g) != 0) return false;
        if (Float.compare(b, comp.b) != 0) return false;
        if (Float.compare(a, comp.a) != 0) return false;
        return true;
    }

    /**
     * <code>hashCode</code> returns a unique code for this color object based
     * on it's values. If two colors are logically equivalent, they will return
     * the same hash code value.
     * @return the hash code value of this color.
     */
    public int hashCode() {
      int hash = 37;
      hash += 37 * hash + Float.floatToIntBits(r);
      hash += 37 * hash + Float.floatToIntBits(g);
      hash += 37 * hash + Float.floatToIntBits(b);
      hash += 37 * hash + Float.floatToIntBits(a);
      return hash;
    }

    /**
     * Used with serialization.  Not to be called manually.
     * @param in where to read from
     * @throws IOException
     * @throws ClassNotFoundException
     * @see java.io.Externalizable
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        r=in.readFloat();
        g=in.readFloat();
        b=in.readFloat();
        a=in.readFloat();
    }

    /**
     * Used with serialization.  Not to be called manually.     *
     * @param out where to write to
     * @throws IOException
     * @see java.io.Externalizable
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(r);
        out.writeFloat(g);
        out.writeFloat(b);
        out.writeFloat(a);
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(r, "r", 0);
        capsule.write(g, "g", 0);
        capsule.write(b, "b", 0);
        capsule.write(a, "a", 0);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        r = capsule.readFloat("r", 0);
        g = capsule.readFloat("g", 0);
        b = capsule.readFloat("b", 0);
        a = capsule.readFloat("a", 0);
    }
    
    public Class<? extends ColorRGBA> getClassTag() {
        return this.getClass();
    }

    public int asIntARGB() {
        int argb = (((int) (a * 255) & 0xFF) << 24)
                 | (((int) (r * 255) & 0xFF) << 16)
                 | (((int) (g * 255) & 0xFF) << 8)
                 | (((int) (b * 255) & 0xFF));
        return argb;
    }

    public int asIntRGBA() {
        int rgba = (((int) (r * 255) & 0xFF) << 24)
                 | (((int) (g * 255) & 0xFF) << 16)
                 | (((int) (b * 255) & 0xFF) << 8)
                 | (((int) (a * 255) & 0xFF));
        return rgba;
    }

    public void fromIntARGB(int color) {
        a = ((byte) (color >> 24) & 0xFF) / 255f;
        r = ((byte) (color >> 16) & 0xFF) / 255f;
        g = ((byte) (color >> 8)  & 0xFF) / 255f;
        b = ((byte) (color)       & 0xFF) / 255f;
    }

    public void fromIntRGBA(int color) {
        r = ((byte) (color >> 24) & 0xFF) / 255f;
        g = ((byte) (color >> 16) & 0xFF) / 255f;
        b = ((byte) (color >> 8)  & 0xFF) / 255f;
        a = ((byte) (color)       & 0xFF) / 255f;
    }
}
