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
import java.util.logging.Logger;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 * @author Jack Lindamood
 */
class KeyframeInfoChunk extends ChunkerClass{
    private static final Logger logger = Logger
            .getLogger(KeyframeInfoChunk.class.getName());
    
    static final int INSTANCE_NAME = 0xb011;

    public KeyframeInfoChunk(DataInput myIn, ChunkHeader i) throws IOException {
        super(myIn,i);
    }
    String name;
    short parent;
    short myID;
    /** pivot location relative to object origin*/
    Vector3f pivot;
    ArrayList<KeyPointInTime> track;
    float morphSmoothAngle;
    Vector3f BBoxMin;
    Vector3f BBoxMax;

    protected void initializeVariables() throws IOException {
        track=new ArrayList<KeyPointInTime>();
    }


    protected boolean processChildChunk(ChunkHeader i) throws IOException {
        switch (i.type){
            case NODE_ID:
                readNodeID();
                return true;
            case TRACK_HEADER:
                readTrackHeader();
                return true;
            case TRACK_PIVOT:
                readTrackPivot();
                return true;
            case TRACK_POS_TAG:
                readPosTrackTag();
                return true;
            case TRACK_ROT_TAG:
                readRotTrackTag();
                return true;
            case TRACK_SCL_TAG:
                readScaleTrackTag();
                return true;
            case MORPH_SMOOTH:
                readSmoothMorph();
                return true;
            case KEY_FOV_TRACK:
                readFOVTrack();
                return true;
            case KEY_ROLL_TRACK:
                readRollTrack();
                return true;
            case KEY_COLOR_TRACK:
                readColorTrack();
                return true;
            case KEY_HOTSPOT_TRACK:
                readHotspotTrack();
                return true;
            case KEY_FALLOFF_TRACK:
                readFalloffTrack();
                return true;
            case BOUNDING_BOX:
                readBoundingBox();
                return true;
            case INSTANCE_NAME:
                name = readcStr();
                return true;
            default:
                return false;
        }
    }

    private void readBoundingBox() throws IOException {
        if (BBoxMin!=null)
            throw new IOException("logic error, BBoxMin not null:" + BBoxMin);
        BBoxMin=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        BBoxMax=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
    }

    private void readFalloffTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).fallOff=myIn.readFloat();
        }
    }

    private void readHotspotTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).hotSpot=myIn.readFloat();
        }
    }

    private void readColorTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).colorTrack=new ColorRGBA(myIn.readFloat(),myIn.readFloat(),myIn.readFloat(),1);
        }
    }

    private void readRollTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).roll=myIn.readFloat();
        }
    }

    private void readFOVTrack() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).FOV=myIn.readFloat();
        }
    }

    private void readSmoothMorph() throws IOException {
        morphSmoothAngle=myIn.readFloat();
    }

    private void readScaleTrackTag() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            Vector3f scale=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            locateTrack(trackPosition).scale=scale;
        }
    }

    private void readRotTrackTag() throws IOException{
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        Vector3f axis=new Vector3f();
        Quaternion prevRot=null;
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data

            float angle =myIn.readFloat();
            angle*=-1;  // negative correction
            axis.x =myIn.readFloat();
            axis.y =myIn.readFloat();
            axis.z =myIn.readFloat();
            axis.normalizeLocal();
            Quaternion toAdd=new Quaternion();
            toAdd.fromAngleNormalAxis(angle,axis);
            if (i!=0)
                toAdd=prevRot.mult(toAdd);
            prevRot=toAdd;

            locateTrack(trackPosition).rot=toAdd;

        }
    }

    private void readPosTrackTag() throws IOException {
        short flags=myIn.readShort();
        long temp=myIn.readLong();    // unknown
        int keys=myIn.readInt();
        for (int i=0;i<keys;i++){
            int trackPosition=myIn.readInt();
            short accData=myIn.readShort(); // acceleration data
            locateTrack(trackPosition).position=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
            if (DEBUG) logger.info("trackPos#"+trackPosition+"Pos#i"+locateTrack(trackPosition).position);
        }
    }

    private KeyPointInTime locateTrack(int trackPosition) {
        if (track.size()==0){
            KeyPointInTime temp=new KeyPointInTime();
            temp.frame=trackPosition;
            track.add(temp);
            return temp;
        }
        Object[] parts=track.toArray();
        int i;
        for (i=0;i<parts.length;i++){
            if (((KeyPointInTime)parts[i]).frame>trackPosition){
                KeyPointInTime temp=new KeyPointInTime();
                temp.frame=trackPosition;
                track.add(i,temp);
                return temp;
            } else if (((KeyPointInTime)parts[i]).frame==trackPosition){
                return track.get(i);
            }
        }
        KeyPointInTime temp=new KeyPointInTime();
        temp.frame=trackPosition;
        track.add(temp);
        return temp;
    }

    private void readTrackPivot() throws IOException {
        pivot=new Vector3f(myIn.readFloat(),myIn.readFloat(),myIn.readFloat());
        if (DEBUG) logger.info("Pivot of:" + pivot);
    }

    private void readTrackHeader() throws IOException {
        name=readcStr();
        short flag1=myIn.readShort();   // ignored
        short flag2=myIn.readShort();   // ignored
        parent=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) logger.info("Name:" + name + " with parent:"+ parent);
    }

    private void readNodeID() throws IOException {
        myID=myIn.readShort();
    }


    public class KeyPointInTime{
        // acc data ignored
        public int frame;
        public Vector3f position;
        public Quaternion rot;
        public Vector3f scale;
        public float FOV;
        public float roll;
        public String morphName;
        public float hotSpot;
        public float fallOff;
        public ColorRGBA colorTrack;
    }
}
