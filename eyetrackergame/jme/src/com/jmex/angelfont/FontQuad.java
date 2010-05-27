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

package com.jmex.angelfont;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * 
 * Represents the geometrical information of one {@link BitmapCharacter}
 * 
 * @author dhdd, Andreas Grabner
 * @author Momoko_Fan (enhancements)
 */
public class FontQuad {

    private int lineNumber;
    private int wordNumber;
    private float sizeScale;
    private BitmapCharacter bitmapChar = null;
    private char character;
    private float wordWidth;
    private float totalWidth;

    private float quadPosX;
    private float quadPosY;
    private float quadTexX;
    private float quadTexY;
    private float quadPosWidth;
    private float quadPosHeight;
    private float quadTexWidth;
    private float quadTexHeight;

    public FontQuad() {
    }

    public void appendPositions(FloatBuffer fb) {
        // NOTE: subtracting the height here
        // because OGL's Ortho origin is at lower-left
        fb.put(quadPosX).put(quadPosY).put(0f);
        fb.put(quadPosX).put(quadPosY - quadPosHeight).put(0f);
        fb.put(quadPosX + quadPosWidth).put(quadPosY - quadPosHeight).put(0f);
        fb.put(quadPosX + quadPosWidth).put(quadPosY).put(0f);
    }

    public void appendTexCoords(FloatBuffer fb) {
        // flip coords to be compatible with OGL
        float u0 = quadTexX;
        float v0 = 1f - quadTexY;
        float u1 = u0 + quadTexWidth;
        float v1 = v0 - quadTexHeight;

        // upper left
        fb.put(u0).put(v0);
        // lower left
        fb.put(u0).put(v1);
        // lower right
        fb.put(u1).put(v1);
        // upper right
        fb.put(u1).put(v0);
    }

    public void appendIndices(IntBuffer sb, int quadIndex) {
        // each quad has 4 indices
        int v0 = quadIndex * 4;
        int v1 = v0 + 1;
        int v2 = v0 + 2;
        int v3 = v0 + 3;

        sb.put(new int[] { v0, v1, v2, v0, v2, v3 });
    }

    public void setSize(float width, float height) {
        quadPosWidth = width;
        quadPosHeight = height;
    }

    public void setPosition(float x, float y) {
        quadPosX = x;
        quadPosY = y;
    }

    public void setX(float x) {
        quadPosX = x;
    }

    public void setY(float y) {
        quadPosY = y;
    }

    public float getX() {
        return quadPosX;
    }

    public float getY() {
        return quadPosY;
    }

    public void setUV(float u, float v, float uSize, float vSize) {
        quadTexX = u;
        quadTexY = v;
        quadTexWidth = uSize;
        quadTexHeight = vSize;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int number) {
        lineNumber = number;
    }

    public int getWordNumber() {
        return wordNumber;
    }

    public void setWordNumber(int number) {
        wordNumber = number;
    }

    public float getSizeScale() {
        return sizeScale;
    }

    public void setSizeScale(float scale) {
        sizeScale = scale;
    }

    public BitmapCharacter getBitmapChar() {
        return bitmapChar;
    }

    public void setBitmapChar(BitmapCharacter ch) {
        bitmapChar = ch;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char m_character) {
        this.character = m_character;
    }

    public float getWordWidth() {
        return wordWidth;
    }

    public void setWordWidth(float width) {
        wordWidth = width;
    }

    public void setTotalWidth(float totalWidth) {
        this.totalWidth = totalWidth;
    }

    public float getTotalWidth() {
        return totalWidth;
    }

}