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

/**
 * Started Date: Jul 2, 2004<br><br>
 * 
 * @author Jack Lindamood
 */
abstract class ChunkerClass implements MaxChunkIDs{
    private static final Logger logger = Logger.getLogger(ChunkerClass.class
            .getName());
    
    static boolean DEBUG_LIGHT=false;
    static boolean DEBUG=false;
    static boolean DEBUG_SEVERE=false;

    public DataInput myIn;
    private ChunkHeader header;

    public ChunkerClass(DataInput myIn){
        this.myIn=myIn;
    }

    public ChunkerClass(DataInput myIn,ChunkHeader header) throws IOException {
        this.myIn=myIn;
        this.header=header;
        initializeVariables();
        chunk();
    }

    protected void initializeVariables() throws IOException {}

    protected void readChunk(ChunkHeader inChunk) throws IOException {
        inChunk.type = myIn.readUnsignedShort();
        inChunk.length=myIn.readInt();
        // Correct length for the 6 bytes read
        header.length-=6;
        inChunk.length-=6;
    }

    protected void skipSize(int length) throws IOException {
       while (length>0){
          int prevSkip = length;
          length-=myIn.skipBytes(length);
            if (length>=prevSkip)
                throw new IOException("Unable to skip bits in InputStream");
        }
    }

    public void chunk() throws IOException {
        try{
        if (DEBUG_LIGHT) logger.info("Reading ChunkHeader len=" + header.length);
        ChunkHeader i=new ChunkHeader();
        while (header.length>0){
            readChunk(i);
            if (DEBUG) logger.info("Begin ID#:" + Integer.toHexString(i.type) + " len=" + i.length + " parentID#="+Integer.toHexString(header.type));
            if (DEBUG) logger.info("Header.len="+header.length+" chunk.lenth="+i.length);
            if (!processChildChunk(new ChunkHeader(i))){
                if (DEBUG) logger.info("*****UNKNOWN TYPE*****" +
                        Integer.toHexString(i.type) + "*** for parent " +Integer.toHexString(header.type));
                if (DEBUG_SEVERE) throw new IOException("Unknown type:" + Integer.toHexString(i.type) +
                        ": in readFile" + "for parent " +Integer.toHexString(header.type));
                skipSize(i.length);
            }
            header.length-=i.length;
            if (header.length<0)
                throw new IOException("Header length doesn't match up: End ID#:" +
                    Integer.toHexString(i.type) + " len left to read=" + header.length +
                    " parentID#="+Integer.toHexString(header.type));
            if (DEBUG) logger.info("End ID#:" + Integer.toHexString(i.type));
        }
        } catch (IOException e){
            if (DEBUG) logger.info("HeaderID=" + Integer.toHexString(header.type) + " len=" + header.length);
            throw e;
        }

    }

    abstract protected boolean processChildChunk(ChunkHeader i) throws IOException;

    protected final String readcStr() throws IOException {
        ArrayList<Byte> byteArray=new ArrayList<Byte>(16);
        byte inByte=myIn.readByte();
        while (inByte!=0){
            byteArray.add(inByte);
            inByte=myIn.readByte();
        }
        byte[] name=new byte[byteArray.size()];
        for (int i=0;i<byteArray.size();i++){
            name[i]= byteArray.get(i);
        }
        String cStr=new String(name, "US-ASCII");
        return cStr;
    }

    protected final String readcStrAndDecrHeader() throws IOException{
        String temp=readcStr();
        header.length-=temp.length()+1; // as encoded in ASCII number of chars matches number of bytes
        return temp;
    }


    protected final String readcStr(int byteLen) throws IOException {
        byte[] inByte=new byte[byteLen];
        myIn.readFully(inByte);
        String cStr=new String(inByte,0,byteLen-1);
        return cStr;
    }

    protected void setHeader(ChunkHeader header) {
        this.header = header;
    }

    protected void decrHeaderLen(int length){
        header.length-=length;
    }

}
