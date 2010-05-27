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
import java.util.HashMap;
import java.util.logging.Logger;

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jmex.model.converters.FormatConverter;

/**
 * Started Date: Jul 2, 2004<br><br>
 *
 *
 * parent == 4d4d == MAIN_3DS
 * type == 3d3d == EDIT_3DS
 *
 * @author Jack Lindamood
 *
 */
class EditableObjectChunk extends ChunkerClass{
    private static final Logger logger = Logger
            .getLogger(EditableObjectChunk.class.getName());

    HashMap<String, MaterialBlock> materialBlocks;
    HashMap namedObjects;
    float masterScale;
    float shadowMapRange;
    float rayTraceBias;
    Vector3f oConstPlanes;
    ColorRGBA genAmbientColor;
    ColorRGBA backGroundColor;
    String backGroundBigMap;
    boolean useBackColor;
    float shadowBias;
    short shadowMapSize;
    LayeredFogChunk fogOptions;
    FogChunk myFog;
    DistanceQueueChunk distanceQueue;
    FormatConverter formatConverter;

    public EditableObjectChunk(DataInput myIn, ChunkHeader i, FormatConverter converter) throws IOException {
        super(myIn);
        this.formatConverter = converter;
        setHeader(i);
        initializeVariables();
        chunk();
    }

    protected void initializeVariables(){
        materialBlocks=new HashMap<String, MaterialBlock>();
        namedObjects=new HashMap();
    }

    protected boolean processChildChunk(ChunkHeader i) throws IOException {
            switch (i.type){
                case MESH_VERSION:
                    readMeshVersion();
                    return true;
                case MAT_BLOCK:
                    MaterialBlock tempMat=new MaterialBlock(myIn,i, formatConverter);
                    materialBlocks.put(tempMat.name,tempMat);
                    return true;

                case MASTER_SCALE:
                    readMasterScale();
                    return true;

                case NAMED_OBJECT:
                    NamedObjectChunk tempOb=new NamedObjectChunk(myIn,i);
                    namedObjects.put(tempOb.name,tempOb);
                    return true;
                case KEY_VIEWPORT:  // Viewport layout is unneeded so is ignored
                    skipSize(i.length);
//                    readViewLayout(i.length);
                    return true;

                case SHADOW_MAP_RANGE:
                    readShadowRange();
                    return true;

                case RAYTRACE_BIAS:
                    readRayTraceBias();
                    return true;

                case O_CONSTS:
                    readOConst();
                    return true;

                case GEN_AMB_COLOR:
                    genAmbientColor=new ColorChunk(myIn,i).getBestColor();
                    return true;

                case BACKGRD_COLOR:
                    backGroundColor=new ColorChunk(myIn,i).getBestColor();
                    return true;
                case BACKGRD_BITMAP:
                    backGroundBigMap=readcStr();
                    return true;
                case V_GRADIENT:
                    skipSize(i.length); // ignored/unneeded
                    return true;
                case USE_BCK_COLOR:
                    useBackColor=true;
                    return true;
                case FOG_FLAG:
                    myFog=new FogChunk(myIn,i);
                    return true;
                case SHADOW_BIAS:
                    readShadowBias();
                    return true;
                case SHADOW_MAP_SIZE:
                    readShadowMapSize();
                    return true;
                case LAYERED_FOG_OPT:
                    fogOptions=new LayeredFogChunk(myIn,i);
                    return true;
                case DISTANCE_QUEUE:
                    distanceQueue=new DistanceQueueChunk(myIn,i);
                    return true;
                case DEFAULT_VIEW:
                    skipSize(i.length); // view ignored
                    return true;
                case UNKNOWN1:
                    skipSize(i.length);   // Unknown
                    return true;
                default:
                    return false;
            }
    }

    private void readOConst() throws IOException{
        oConstPlanes=new Vector3f(myIn.readFloat(), myIn.readFloat(), myIn.readFloat());
        if (DEBUG || DEBUG_LIGHT) logger.info("Planes:" + oConstPlanes);
    }

    private void readRayTraceBias() throws IOException{
        rayTraceBias=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) logger.info("Raytrace bias:" + rayTraceBias);
    }

    private void readShadowRange() throws IOException {
        shadowMapRange=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) logger.info("Shadow map range:" + shadowMapRange);
    }

    private void readMasterScale() throws IOException{
        masterScale=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) logger.info("Master scale:" + masterScale);
    }

    private void readMeshVersion() throws IOException {
        int i=myIn.readInt();
        if (DEBUG || DEBUG_LIGHT) logger.info("Mesh version:" + i);
    }

    private void readShadowBias() throws IOException {
        shadowBias=myIn.readFloat();
        if (DEBUG || DEBUG_LIGHT) logger.info("Bias:" + shadowBias);
    }

    private void readShadowMapSize() throws IOException{
        shadowMapSize=myIn.readShort();
        if (DEBUG || DEBUG_LIGHT) logger.info("Shadow map siz:" + shadowMapSize);
    }

}
