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

package com.jme.renderer.lwjgl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.state.lwjgl.records.RendererRecord;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * <code>Font2D</code> maintains display lists for each ASCII character
 * defined by an image. <code>Font2D</code> assumes that the texture is
 * 256x256 and that the characters are 16 pixels high by 16 pixels wide. The
 * order of the characters is also important: <br>
 *
 * <img src ="fonttable.gif"> <br>
 *
 * After the font is loaded, it can be used with a call to <code>print</code>.
 * The <code>Font2D</code> class is also printed in Ortho mode and
 * billboarded, as well as depth buffering turned off. This means that the font
 * will be placed at a two dimensional coordinate that corresponds to screen
 * coordinates.
 *
 * The users is assumed to set a TextureState to the Text Geometry calling this
 * class.
 *
 * @see com.jme.scene.Text
 * @see com.jme.scene.state.TextureState
 * @author Mark Powell
 * @version $Id: LWJGLFont.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class LWJGLFont {

    /**
     * Sets the style of the font to normal.
     */
    public static final int NORMAL = 0;

    /**
     * Sets the style of the font to italics.
     */
    public static final int ITALICS = 1;

    //display list offset.
    private int base;

    //buffer that holds the text.
    private ByteBuffer scratch;

    //Color to render the font.
    private ColorRGBA fontColor;

    /**
     * Constructor instantiates a new <code>LWJGLFont</code> object. The
     * initial color is set to white.
     *
     */
    public LWJGLFont() {
        fontColor = new ColorRGBA(1, 1, 1, 1);
        scratch = BufferUtils.createByteBuffer(1);
        buildDisplayList();
    }

    /**
     * <code>deleteFont</code> deletes the current display list of font
     * objects. The font will be useless until a call to
     * <code>buildDisplayLists</code> is made.
     */
    public void deleteFont() {
        GL11.glDeleteLists(base, 256);
    }

    /**
     * <code>setColor</code> sets the RGBA values to render the font as. By
     * default the color is white with no transparency.
     *
     * @param color
     *            the color to set.
     */
    public void setColor(ColorRGBA color) {
        fontColor.set(color);
    }

    /**
     * <code>print</code> renders the specified string to a given (x,y)
     * location. The x, y location is in terms of screen coordinates. There are
     * currently two sets of fonts supported: NORMAL and ITALICS.
     * @param r 
     *
     * @param x
     *            the x screen location to start the string render.
     * @param y
     *            the y screen location to start the string render.
     * @param text
     *            the String to render.
     * @param set
     *            the mode of font: NORMAL or ITALICS.
     */
    public void print(Renderer r, float x, float y, Vector3f scale, StringBuffer text, int set) {
        RendererRecord matRecord = (RendererRecord) DisplaySystem.getDisplaySystem().getCurrentContext().getRendererRecord();
        if (set > 1) {
            set = 1;
        } else if (set < 0) {
            set = 0;
        }

        boolean alreadyOrtho = r.isInOrthoMode();
        if (!alreadyOrtho)
            r.setOrtho();
        else {
            matRecord.switchMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
        }
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale.x, scale.y, scale.z);
        GL11.glListBase(base - 32 + (128 * set));

        //Put the string into a "pointer"
        if (text.length() > scratch.capacity()) {
            scratch = BufferUtils.createByteBuffer(text.length());
        } else {
            scratch.clear();
        }

        int charLen = text.length();
        for (int z = 0; z < charLen; z++)
            scratch.put((byte) text.charAt(z));
        scratch.flip();
        matRecord.setCurrentColor(fontColor);
        //call the list for each letter in the string.
        GL11.glCallLists(scratch);
        //set color back to white
        matRecord.setCurrentColor(1,1,1,1);

        if (!alreadyOrtho) {
            r.unsetOrtho();
        } else {
            matRecord.switchMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
        }
    }

    /**
     * <code>buildDisplayList</code> sets up the 256 display lists that are
     * used to render each font character. Each list quad is 16x16, as defined
     * by the font image size.
     */
    public void buildDisplayList() {
        float cx;
        float cy;

        base = GL11.glGenLists(256);

        for (int loop = 0; loop < 256; loop++) {
            cx = (loop % 16) / 16.0f;
            cy = (loop / 16) / 16.0f;

            GL11.glNewList(base + loop, GL11.GL_COMPILE);
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(cx, 1 - cy - 0.0625f);
            GL11.glVertex2i(0, 0);
            GL11.glTexCoord2f(cx + 0.0625f, 1 - cy - 0.0625f);
            GL11.glVertex2i(16, 0);
            GL11.glTexCoord2f(cx + 0.0625f, 1 - cy);
            GL11.glVertex2i(16, 16);
            GL11.glTexCoord2f(cx, 1 - cy);
            GL11.glVertex2i(0, 16);
            GL11.glEnd();
            GL11.glTranslatef(10, 0, 0);
            GL11.glEndList();
        }
    }

    /**
     * <code>toString</code> returns the string representation of this font
     * object in the Format: <br>
     * <br>
     * jme.geometry.hud.text.Font2D@1c282a1 <br>
     * Color: {RGBA COLOR} <br>
     *
     * @return the string representation of this object.
     */
    public String toString() {
        String string = super.toString();
        string += "\nColor: " + fontColor.toString();

        return string;
    }
}

