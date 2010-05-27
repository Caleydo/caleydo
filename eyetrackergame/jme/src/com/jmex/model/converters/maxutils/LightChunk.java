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

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * parent ==  NAMED_OBJECT == 0x4000<br>
 * type == LIGHT_OBJ == 0x4600<br>
 *
 * @author Jack Lindamood
 */
class LightChunk extends ChunkerClass {
    private static final Logger logger = Logger.getLogger(LightChunk.class
            .getName());
    
    public LightChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    Vector3f myLoc;
    ColorRGBA lightColor;
    float outterRange;
    float innerRange;
    float mult;
    SpotLightChunk spotInfo;
    boolean attenuateOn;

    protected void initializeVariables() throws IOException {
        myLoc=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        decrHeaderLen(4*3);
        mult=1;
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case COLOR_FLOAT:
                lightColor=new ColorRGBA(myIn.readFloat(), myIn.readFloat(), myIn.readFloat(), 1);
                if (DEBUG) logger.info("Light color:"+lightColor);
                return true;
            case LIGHT_OUT_RANGE:
                readOuterLightRange();
                return true;
            case LIGHT_IN_RANGE:
                readInnerLightRange();
                return true;
            case LIGHT_MULTIPLIER:
                readLightMultiplier();
                return true;
            case LIGHT_SPOTLIGHT:
                if (spotInfo!=null)
                    throw new IOException("logic error... spotInfo not null");
                spotInfo = new SpotLightChunk(myIn,i);
                return true;
            case LIGHT_ATTENU_ON:
                attenuateOn=true;
                return true;
            default:
                return false;
            }
    }

    private void readLightMultiplier() throws IOException {
        mult=myIn.readFloat();
    }

    private void readInnerLightRange() throws IOException {
        innerRange=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) logger.info("Inner range:" + innerRange);
    }

    private void readOuterLightRange() throws IOException {
        outterRange=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) logger.info("Outter range:" + outterRange);
    }
}
