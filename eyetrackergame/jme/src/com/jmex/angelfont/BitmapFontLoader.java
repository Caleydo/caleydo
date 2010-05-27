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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.util.TextureManager;

/**
 * 
 * A Loader class for {@link BitmapFont} objects.
 * 
 * @author dhdd, Andreas Grabner
 * @author Momoko_Fan (enhancements)
 */
public class BitmapFontLoader {

    /**
     * 
     * Loads the jme default {@link BitmapFont}
     * 
     * @return the BitmapFont that is the jme default
     */
    public static BitmapFont loadDefaultFont() {
        URL fontFile = BitmapFontLoader.class.getClassLoader().getResource("com/jmex/angelfont/angelFont.fnt");
        URL textureFile = BitmapFontLoader.class.getClassLoader().getResource("com/jmex/angelfont/angelFont.png");
        try {
            return load(fontFile, textureFile);
        } catch (IOException e) {
            return null;
        } // catch
    } // loadDefaultFont

    /**
     * 
     * loads the {@link BitmapFont} defined by the two provided URLs
     * 
     * @param fontFile the URL to the .fnt file of the {@link BitmapFont}
     * @param textureFile the URL to the texture file of the {@link BitmapFont}
     * @return the BitmapFont defined by the two provided {@link URL}s
     * @throws IOException if one of the provided {@link URL}s is null
     */
    public static BitmapFont load(URL fontFile, URL textureFile) throws IOException {
        try {
            BitmapCharacterSet charSet = new BitmapCharacterSet();
            BitmapFont font = new BitmapFont();

            if (fontFile == null) {
                throw new IOException("The given URL to the requested font file is null!");
            } // if

            if (textureFile == null) {
                throw new IOException("The given URL to the requested font texture file is null!");
            } // if

            font.setFontTexture(TextureManager.loadTexture(textureFile, true));
            font.getFontTexture().setMinificationFilter(MinificationFilter.Trilinear);
            font.getFontTexture().setMagnificationFilter(MagnificationFilter.Bilinear);

            BufferedReader reader = new BufferedReader(new InputStreamReader(fontFile.openStream()));
            String regex = "[\\s=]+";

            font.setCharSet(charSet);
            while (reader.ready()) {
                String line = reader.readLine();
                String[] tokens = line.split(regex);
                if (tokens[0].equals("info")) {
                    // Get rendered size
                    for (int i = 1; i < tokens.length; i++) {
                        if (tokens[i].equals("size")) {
                            charSet.setRenderedSize(Math.abs(Integer.parseInt(tokens[i + 1])));
                        }
                    }
                } else if (tokens[0].equals("common")) {
                    // Fill out BitmapCharacterSet fields
                    for (int i = 1; i < tokens.length; i++) {
                        String token = tokens[i];
                        if (token.equals("lineHeight")) {
                            charSet.setLineHeight(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("base")) {
                            charSet.setBase(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("scaleW")) {
                            charSet.setWidth(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("scaleH")) {
                            charSet.setHeight(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("yoffset")) {
                            charSet.setyOffset(Integer.parseInt(tokens[i + 1]));
                        } // else if
                    }
                } else if (tokens[0].equals("char")) {
                    // New BitmapCharacter
                    BitmapCharacter ch = null;
                    for (int i = 1; i < tokens.length; i++) {
                        String token = tokens[i];
                        if (token.equals("id")) {
                            int index = Integer.parseInt(tokens[i + 1]);
                            ch = new BitmapCharacter();
                            charSet.addCharacter(index, ch);
                        } else if (token.equals("x")) {
                            ch.setX(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("y")) {
                            ch.setY(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("width")) {
                            ch.setWidth(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("height")) {
                            ch.setHeight(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("xoffset")) {
                            ch.setXOffset(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("yoffset")) {
                            ch.setYOffset(Integer.parseInt(tokens[i + 1]));
                        } else if (token.equals("xadvance")) {
                            ch.setXAdvance(Integer.parseInt(tokens[i + 1]));
                        }
                    }
                } else if (tokens[0].equals("kerning")) {
                    // Build kerning list
                    int index = 0;
                    Kerning k = new Kerning();

                    for (int i = 1; i < tokens.length; i++) {
                        if (tokens[i].equals("first")) {
                            index = Integer.parseInt(tokens[i + 1]);
                        } else if (tokens[i].equals("second")) {
                            k.setSecond(Integer.parseInt(tokens[i + 1]));
                        } else if (tokens[i].equals("amount")) {
                            k.setAmount(Integer.parseInt(tokens[i + 1]));
                        }
                    }

                    BitmapCharacter ch = charSet.getCharacter(index);
                    ch.getKerningList().add(k);
                }
            }
            reader.close();
            return font;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    } // load

}
