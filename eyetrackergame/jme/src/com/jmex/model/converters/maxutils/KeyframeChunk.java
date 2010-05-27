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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * Parent == TDSFile == 0x4d4d<br>
 * type ==  KEYFRAMES == 0xb000<br>
 *
 * @author Jack Lindamood
 */
class KeyframeChunk extends ChunkerClass{
    private static final Logger logger = Logger.getLogger(KeyframeChunk.class
            .getName());
    
    public KeyframeChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }

    int animationLen;
    int begin;
    int end;
    //ArrayList objKeyframes;
    HashMap<String, KeyframeInfoChunk> objKeyframes;
    ArrayList<KeyframeInfoChunk> cameraKeyframes;
    ArrayList<KeyframeInfoChunk> lightKeyframes;

    protected void initializeVariables() throws IOException {
        objKeyframes=new HashMap<String, KeyframeInfoChunk> ();
        cameraKeyframes=new ArrayList<KeyframeInfoChunk>();
        lightKeyframes=new ArrayList<KeyframeInfoChunk>();
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case KEY_HEADER:
                readKeyframeHeader();
                return true;
            case KEY_SEGMENT:
                readSegment();
                return true;
            case KEY_CURTIME:
                readCurTime();
                return true;
            case KEY_VIEWPORT:
                skipSize(i.length); // Ignore changing viewports, not relevant
                return true;
            case KEY_OBJECT:
                KeyframeInfoChunk it=new KeyframeInfoChunk(myIn,i);
                objKeyframes.put(it.name,it);
//                objKeyframes.add(new KeyframeInfoChunk(myIn,i));
                return true;
            case KEY_CAM_TARGET:
            case KEY_CAMERA_OBJECT:
                cameraKeyframes.add(new KeyframeInfoChunk(myIn,i));
                return true;
            case KEY_OMNI_LI_INFO:
            case KEY_AMB_LI_INFO:
            case KEY_SPOT_TARGET:
            case KEY_SPOT_OBJECT:
                lightKeyframes.add(new KeyframeInfoChunk(myIn,i));
                return true;
            default:
                return false;
        }
    }

    private void readSegment() throws IOException {
        begin=myIn.readInt();
        end=myIn.readInt();
        if (DEBUG_LIGHT) logger.info("Reading segment");
        if (DEBUG) logger.info("Segment begins at " + begin + " and ends at " + end);
    }

    private void readCurTime() throws IOException {
        int curFrame=myIn.readInt();
        if (DEBUG) logger.info("Current frame is " + curFrame);
    }

    private void readKeyframeHeader() throws IOException {
        if (DEBUG_LIGHT) logger.info("Reading keyframeHeader");
        short revision=myIn.readShort();
        String flname=readcStr();
        animationLen=myIn.readInt();
        if (DEBUG) logger.info("Revision #" + revision + " with filename " + flname + " and animation len " + animationLen);
    }
}
