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

package com.jmex.model.converters.maxutils;

import java.io.DataInput;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent=afff=MAT_BLOCK
 * type=0xA200 - 0xA34C=various
 *
 * @author Jack Lindamood
 */
class TextureChunk extends ChunkerClass{
    private static final Logger logger = Logger.getLogger(TextureChunk.class
            .getName());

    float percent;
    String texName;
    int flags;
    float textureBlur;
    float bumpPercentage;
    float vScale;
    float uScale;
    public TextureChunk(DataInput myIn, ChunkHeader header) throws IOException {
        super(myIn, header);
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
            switch (i.type){
                case PRCT_INT_FRMT:
                    percent=myIn.readShort()/100f;
                    if (DEBUG) logger.info("Texture percent:"+percent);
                    return true;
                case MAT_TEXNAME:
                    texName=readcStr(i.length);
                    return true;
                case MAT_TEX_FLAGS:
                    flags=myIn.readUnsignedShort();
                    return true;
                case MAT_TEX_BLUR:
                    textureBlur=myIn.readFloat();
                    return true;
                case MAT_TEX_BUMP_PER:
                    bumpPercentage=myIn.readShort()/100f;
                    if (DEBUG) logger.info("Texture bump percent:"+bumpPercentage);
                    return true;
                case TEXTURE_V_SCALE:
                    vScale=myIn.readFloat();
                    return true;
                case TEXTURE_U_SCALE:
                    uScale=myIn.readFloat();
                    return true;
                default:
                    return false;
            }
    }
}
