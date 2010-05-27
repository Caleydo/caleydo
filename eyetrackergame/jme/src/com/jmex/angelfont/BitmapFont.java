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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.image.Texture;

/**
 * 
 * Represents a font within jME that is generated with the AngelCode Bitmap Font
 * Generator
 * 
 * @author dhdd, Andreas Grabner
 * @author Momoko_Fan (enhancements)
 * @author Core_Dump, dhdd (\n for new line feature)
 */
public class BitmapFont {

    public enum Align {
        Left, Center, Right
    }

    private BitmapCharacterSet charSet;

    private Texture fontTexture;

    public BitmapFont() {
        // nothing to do
    } // BitmapFont

    private Kerning findKerningNode(int newLineLastChar, int nextChar) {
        BitmapCharacter c = charSet.getCharacter(newLineLastChar);
        for (Kerning k : c.getKerningList()) {
            if (k.getSecond() == nextChar) {
                return k;
            } // if
        } // for

        return null;
    } // findKerningNode

    /**
     * The method updates a {@link BitmapText} and restricts it to a rectangle,
     * given in {@link StringBlock}.textBox
     * 
     * @param block
     *            the {@link StringBlock} that holds the necessary information
     *            for the update of the text
     * @param target
     *            the {@link QuadList} holds all the {@link FontQuad}s that each
     *            represent a character
     * @param rightToLeft
     *            indicates weather the text should be written from right to
     *            left
     * @return
     */
    public float updateText(StringBlock block, QuadList target, boolean rightToLeft) {

        String text = block.getText();
        float x = 0;
        float y = 0;
        float lineWidth = 0f;
        float sizeScale = (float) block.getSize() / charSet.getRenderedSize();
        char lastChar = 0;
        int lineNumber = 1;
        int wordNumber = 1;
        int quadIndex = -1;
        float wordWidth = 0f;
        boolean firstCharOfLine = true;
        boolean useKerning = block.isKerning();
        target.ensureSize(text.length());
        int numActive = 0;
        Align alignment = block.getAlignment();
        float lastLineWidth = 0f;

        float incrScale = rightToLeft ? -1f : 1f;

        for (int i = 0; i < text.length(); i++) {
            char theChar = text.charAt(i);
            BitmapCharacter c = charSet.getCharacter((int) theChar);
            if (c == null && (text.charAt(i) != '\n' && text.charAt(i) != '\r')) {
                Logger.getLogger("").log(Level.FINE,
                        "Character '" + text.charAt(i) + "' is not in alphabet, skipping it.");
            } else if (text.charAt(i) == ' ' && firstCharOfLine) {
                Logger.getLogger("").log(Level.FINE,
                        "Character '" + text.charAt(i) + "' is blank, skipping it because first char in line.");
            } else {
                float xOffset = 0;
                float yOffset = 0;
                float xAdvance = 0;
                float width = 0;
                float height = 0;
                if (c != null) {
                    xOffset = c.getXOffset() * sizeScale;
                    yOffset = (charSet.getyOffset() + c.getYOffset()) * sizeScale;
                    xAdvance = c.getXAdvance() * sizeScale;
                    width = c.getWidth() * sizeScale;
                    height = c.getHeight() * sizeScale;
                }

                if (text.charAt(i) == '\n' || text.charAt(i) == '\r') {
                    x = 0;
                    y -= charSet.getLineHeight() * sizeScale;
                    // float offset = 0f;

                    // Justify the last (now complete) line
                    if (alignment == Align.Center) {
                        for (int k = 0; k < target.getQuantity(); k++) {
                            FontQuad q = target.getQuad(k);
                            if (q.getLineNumber() == lineNumber) {
                                q.setX(q.getX() - lineWidth / 2f);
                            } // if
                        } // for
                    } // if
                    if (alignment == Align.Right) {
                        for (int k = 0; k < target.getQuantity(); k++) {
                            FontQuad q = target.getQuad(k);
                            if (q.getLineNumber() == lineNumber) {
                                q.setX(q.getX() - lineWidth);
                            } // if
                        } // for
                    } // if
                    if (rightToLeft) {
                        // move all characters so that the current X = 0
                        for (int k = 0; k < target.getQuantity(); k++) {
                            FontQuad q = target.getQuad(k);
                            if (q.getLineNumber() == lineNumber) {
                                q.setX(q.getX() + lineWidth);
                            } // if
                        } // for
                    } // if

                    // New line without any "carry-down" word
                    firstCharOfLine = true;
                    lastLineWidth = lineWidth;
                    lineWidth = 0f;
                    wordNumber = 1;
                    lineNumber++;
                    continue;
                } // End new line check

                // Adjust for kerning
                float kernAmount = 0f;
                if (!firstCharOfLine && useKerning) {
                    Kerning kern = findKerningNode(lastChar, theChar);
                    if (kern != null) {
                        kernAmount = kern.getAmount() * sizeScale;
                        x += kernAmount * incrScale;
                        lineWidth += kernAmount;
                        wordWidth += kernAmount;
                    } // if
                } // if
                firstCharOfLine = false;

                // Create the quad
                quadIndex++;
                FontQuad q = target.getQuad(quadIndex);
                numActive++;

                // Determine quad position
                float quadPosX = x + (xOffset * incrScale);
                if (rightToLeft) {
                    quadPosX -= width;
                } // if

                float quadPosY = y - yOffset;
                q.setPosition(quadPosX, quadPosY);
                q.setSize(width, height);

                float u0 = (float) c.getX() / charSet.getWidth();
                float v0 = (float) c.getY() / charSet.getHeight();
                float w = (float) c.getWidth() / charSet.getWidth();
                float h = (float) c.getHeight() / charSet.getHeight();
                q.setUV(u0, v0, w, h);

                q.setLineNumber(lineNumber);

                if (theChar == ' ') {
                    // since this is a space,
                    // increment wordnumber and reset wordwidth
                    wordNumber++;
                    wordWidth = 0f;
                } // if

                // set data
                q.setWordNumber(wordNumber);
                q.setWordWidth(wordWidth);
                q.setBitmapChar(c);
                q.setSizeScale(sizeScale);
                q.setCharacter(text.charAt(i));
                q.setTotalWidth(kernAmount + xAdvance);

                x += xAdvance * incrScale;
                wordWidth += xAdvance;
                lineWidth += xAdvance;

                lastChar = theChar;
            } // else
        } // for

        // Justify the last (now complete) line
        if (alignment == Align.Center) {
            for (int k = 0; k < target.getQuantity(); k++) {
                FontQuad q = target.getQuad(k);
                if (q.getLineNumber() == lineNumber) {
                    q.setX(q.getX() - lineWidth / 2f);
                } // if
            } // for
        } // if
        if (alignment == Align.Right) {
            for (int k = 0; k < target.getQuantity(); k++) {
                FontQuad q = target.getQuad(k);
                if (q.getLineNumber() == lineNumber) {
                    q.setX(q.getX() - lineWidth);
                } // if
            } // for
        } // if
        if (rightToLeft) {
            // move all characters so that the current X = 0
            for (int k = 0; k < target.getQuantity(); k++) {
                FontQuad q = target.getQuad(k);
                if (q.getLineNumber() == lineNumber) {
                    q.setX(q.getX() + lineWidth);
                } // if
            } // for
        } // if

        block.setNumLines(lineNumber);
        target.setNumActive(numActive);
        return lineWidth;
    } // updateText

    /**
     * The method updates a {@link BitmapText} and restricts it to a rectangle,
     * given in {@link StringBlock}.textBox
     * 
     * @param block
     *            the {@link StringBlock} that holds the necessary information
     *            for the update of the text
     * @param target
     *            the {@link QuadList} holds all the {@link FontQuad}s that each
     *            represent a character
     */
    public void updateTextRect(StringBlock block, QuadList target) {

        String text = block.getText();
        float x = block.getTextBox().x;
        float y = block.getTextBox().y;
        float maxWidth = block.getTextBox().width;
        float lastLineWidth = 0f;
        float lineWidth = 0f;
        float sizeScale = block.getSize() / charSet.getRenderedSize();
        char lastChar = 0;
        int lineNumber = 1;
        int wordNumber = 1;
        int quadIndex = -1;
        float wordWidth = 0f;
        boolean firstCharOfLine = true;
        boolean useKerning = block.isKerning();
        Align alignment = block.getAlignment();
        int numActive = 0;

        target.ensureSize(text.length());

        for (int i = 0; i < text.length(); i++) {
            BitmapCharacter c = charSet.getCharacter((int) text.charAt(i));
            if (c == null && (text.charAt(i) != '\n' && text.charAt(i) != '\r')) {
                Logger.getLogger("").log(Level.FINE,
                        "Character '" + text.charAt(i) + "' is not in alphabet, skipping it.");
            } else if (text.charAt(i) == ' ' && firstCharOfLine) {
                Logger.getLogger("").log(Level.FINE,
                        "Character '" + text.charAt(i) + "' is blank, skipping it because first char in line.");
            } else {
                float xOffset = 0;
                float yOffset = 0;
                float xAdvance = 0;
                float width = 0;
                float height = 0;
                if (c != null) {
                    xOffset = c.getXOffset() * sizeScale;
                    yOffset = (charSet.getyOffset() + c.getYOffset()) * sizeScale;
                    xAdvance = c.getXAdvance() * sizeScale;
                    width = c.getWidth() * sizeScale;
                    height = c.getHeight() * sizeScale;
                }
                if (text.charAt(i) == '\n' || text.charAt(i) == '\r' || (lineWidth + xAdvance >= maxWidth)) {
                    x = block.getTextBox().x;
                    y -= charSet.getLineHeight() * sizeScale;
                    // float offset = 0f;
                    if ((lineWidth + xAdvance >= maxWidth) && (wordNumber != 1)) {
                        // Next character extends past text box width
                        // We have to move the last word down one line
                        char newLineLastChar = 0;
                        lastLineWidth = lineWidth;
                        lineWidth = 0f;

                        for (int j = 0; j <= quadIndex; j++) {
                            FontQuad q = target.getQuad(j);
                            BitmapCharacter localChar = q.getBitmapChar();

                            float localxOffset = localChar.getXOffset() * sizeScale;
                            float localyOffset = (charSet.getyOffset() + localChar.getYOffset()) * sizeScale;
                            float localxAdvance = localChar.getXAdvance() * sizeScale;

                            // Move current word to the left side of the text
                            // box
                            if ((q.getLineNumber() == lineNumber) && (q.getWordNumber() == wordNumber)) {
                                if (alignment == Align.Left && q.getCharacter() == ' ') {
                                    continue;
                                } // if
                                q.setLineNumber(q.getLineNumber() + 1);
                                q.setWordNumber(1);
                                float quadPosX = x + localxOffset;
                                float quadPosY = y - localyOffset;
                                q.setPosition(quadPosX, quadPosY);

                                x += localxAdvance;
                                lastLineWidth -= localxAdvance;
                                lineWidth += localxAdvance;
                                Kerning kern = findKerningNode(newLineLastChar, q.getCharacter());
                                if (kern != null && useKerning) {
                                    x += kern.getAmount() * sizeScale;
                                    lineWidth += kern.getAmount() * sizeScale;
                                } // if
                            } // if

                            newLineLastChar = q.getCharacter();
                        } // for
                    } else {
                        // New line without any "carry-down" word
                        firstCharOfLine = true;
                        lastLineWidth = lineWidth;
                        lineWidth = 0f;
                    } // else

                    // Justify the previous (now complete) line
                    if (alignment == Align.Center) {
                        for (int k = 0; k < target.getQuantity(); k++) {
                            FontQuad q = target.getQuad(k);

                            if (q.getLineNumber() == lineNumber) {
                                q.setX(q.getX() + block.getTextBox().width / 2f - lastLineWidth / 2f);
                            } // if
                        } // for
                    } // if
                    if (alignment == Align.Right) {
                        for (int k = 0; k < target.getQuantity(); k++) {
                            FontQuad q = target.getQuad(k);
                            if (q.getLineNumber() == lineNumber) {
                                q.setX(q.getX() + block.getTextBox().width - lastLineWidth);
                            } // if
                        } // for
                    } // if

                    wordNumber = 1;
                    lineNumber++;

                } // End new line check

                // Dont print these
                if (text.charAt(i) == '\n' || text.charAt(i) == '\r' || text.charAt(i) == '\t') {
                    continue;
                } // if

                // Set starting cursor for alignment
                if (firstCharOfLine) {
                    x = block.getTextBox().x;
                } // if

                // Adjust for kerning
                float kernAmount = 0f;
                if (!firstCharOfLine && useKerning) {
                    Kerning kern = findKerningNode(lastChar, (char) text.charAt(i));
                    if (kern != null) {
                        kernAmount = kern.getAmount() * sizeScale;
                        x += kernAmount;
                        lineWidth += kernAmount;
                        wordWidth += kernAmount;
                    } // if
                } // if
                firstCharOfLine = false;

                // edit the quad
                quadIndex++;
                FontQuad q = target.getQuad(quadIndex);
                numActive++;

                float quadPosX = x + (xOffset);
                float quadPosY = y - yOffset;
                q.setPosition(quadPosX, quadPosY);
                q.setSize(width, height);

                float u0 = (float) c.getX() / charSet.getWidth();
                float v0 = (float) c.getY() / charSet.getHeight();
                float w = (float) c.getWidth() / charSet.getWidth();
                float h = (float) c.getHeight() / charSet.getHeight();
                q.setUV(u0, v0, w, h);

                q.setLineNumber(lineNumber);
                if (text.charAt(i) == ' ') {
                    wordNumber++;
                    wordWidth = 0f;
                } // if
                q.setWordNumber(wordNumber);
                wordWidth += xAdvance;
                q.setWordWidth(wordWidth);
                q.setBitmapChar(c);
                q.setSizeScale(sizeScale);
                q.setCharacter(text.charAt(i));

                x += xAdvance;
                lineWidth += xAdvance;
                lastChar = text.charAt(i);
            } // else
        } // for

        // Justify the last (now complete) line
        if (alignment == Align.Center) {
            for (int k = 0; k < target.getQuantity(); k++) {
                FontQuad q = target.getQuad(k);
                if (q.getLineNumber() == lineNumber) {
                    q.setX(q.getX() + block.getTextBox().width / 2f - lineWidth / 2f);
                } // if
            } // for
        } // if

        if (alignment == Align.Right) {
            for (int k = 0; k < target.getQuantity(); k++) {
                FontQuad q = target.getQuad(k);
                if (q.getLineNumber() == lineNumber) {
                    q.setX(q.getX() + block.getTextBox().width - lineWidth);
                } // if
            } // for
        } // if

        block.setNumLines(lineNumber);
        target.setNumActive(numActive);
    } // updateTextRect

    /**
     * Gets the line height of a StringBlock.
     * 
     * @param sb
     * @return
     */
    public float getLineHeight(StringBlock sb) {
        return charSet.getLineHeight() * (sb.getSize() / charSet.getRenderedSize());
    }

    public void setCharSet(BitmapCharacterSet charSet) {
        this.charSet = charSet;
    }

    public BitmapCharacterSet getCharSet() {
        return charSet;
    }

    public Texture getFontTexture() {
        return fontTexture;
    }

    public void setFontTexture(Texture fontTexture) {
        this.fontTexture = fontTexture;
    }

}