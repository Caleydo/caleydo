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

import com.jme.math.Vector3f;

/**
 * Started Date: Jul 3, 2004<br><br>
 *
 * type == LIGHT_SPOTLIGHT == 0x4610<br>
 * parent == LIGHT_OBJ == 0x4600<br>
 *
 * @author Jack Lindamood
 */
class SpotLightChunk extends ChunkerClass{
    Vector3f target;
    float hotSpot;
    float fallOff;
    boolean shadowed;
    float roll;
    short shadowSize;
    float lightBias;
    float filter;
    float shadowBias;

    public SpotLightChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    protected void initializeVariables() throws IOException {
        target=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        hotSpot=myIn.readFloat();
        fallOff=myIn.readFloat();
        decrHeaderLen(4*5);
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case LIGHT_SPOT_ROLL:
                readSpotlightRollAngles();
                return true;
            case LIGHT_SPOT_SHADOWED:
                shadowed=true;
                return true;
            case LIGHT_SPOT_BIAS:
                readLightBias();
                return true;
            case LIGHT_LOC_SHADOW:
                readLightShadow();
                return true;
            case LIGHT_SEE_CONE:
                return true;    // A visable cone is ignored
            case LIGHT_SPOT_OVERSHOOT:
                return true;    // Overshoot ignored
            default:
                return false;
        }
    }

    private void readLightShadow() throws IOException {
        shadowBias=myIn.readFloat();
        filter=myIn.readFloat();
        shadowSize=myIn.readShort();
    }

    private void readLightBias() throws IOException {
        lightBias=myIn.readFloat();
    }

    private void readSpotlightRollAngles() throws IOException {
        roll=myIn.readFloat();
    }
}
