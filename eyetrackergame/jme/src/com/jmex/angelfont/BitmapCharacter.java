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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single bitmap character. Holds information for position and
 * dimension on the texture and kerning information.
 * 
 * @author dhdd, Andreas Grabner
 * @author Momoko_Fan (enhancements)
 */
public class BitmapCharacter {

    private int x;
    private int y;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;
    private int xAdvance;
    private List<Kerning> kerningList = new ArrayList<Kerning>();

    // / <summary>Clones the BitmapCharacter</summary>
    // / <returns>Cloned BitmapCharacter</returns>
    public BitmapCharacter Clone() {
        BitmapCharacter result = new BitmapCharacter();
        result.x = x;
        result.y = y;
        result.width = width;
        result.height = height;
        result.xOffset = xOffset;
        result.yOffset = yOffset;
        result.xAdvance = xAdvance;
        result.kerningList.addAll(kerningList);
        return result;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getXOffset() {
        return xOffset;
    }

    public void setXOffset(int offset) {
        xOffset = offset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setYOffset(int offset) {
        yOffset = offset;
    }

    public int getXAdvance() {
        return xAdvance;
    }

    public void setXAdvance(int advance) {
        xAdvance = advance;
    }

    public List<Kerning> getKerningList() {
        return kerningList;
    }

    public void setKerningList(List<Kerning> kerningList) {
        this.kerningList = kerningList;
    }
}