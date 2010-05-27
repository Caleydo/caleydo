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

package com.jmex.model.collada;

import java.util.ArrayList;

import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.image.Texture.WrapMode;
import com.jme.scene.Controller;
import com.jme.scene.state.RenderState;

/**
 * ColladaMaterial is designed to hold all the material attributes of a Collada
 * object. This may include many RenderState objects. ColladaMaterial is a
 * container object for jME RenderStates needed.
 * 
 * @author Mark Powell
 */
public class ColladaMaterial {
    public String minFilter;
    public String magFilter;
    public String wrapS;
    public String wrapT;
    
    RenderState[] stateList;
    ArrayList<Controller> controllerList;
    

    public ColladaMaterial() {
        stateList = new RenderState[RenderState.StateType.values().length];
    }
    
    public void addController(Controller c) {
    	if(controllerList == null) {
    		controllerList = new ArrayList<Controller>();
    	}
    	
    	controllerList.add(c);
    }
    
    public ArrayList<Controller> getControllerList() {
    	return controllerList;
    }

    public void setState(RenderState ss) {
    	if(ss == null) return;
        stateList[ss.getStateType().ordinal()] = ss;
    }

    /**
     * @deprecated As of 2.0, use {@link #getState(com.jme.scene.state.RenderState.StateType)} instead.
     */
    public RenderState getState(int index) {
        return stateList[index];
    }

    public RenderState getState(RenderState.StateType type) {
        return stateList[type.ordinal()];
    }
    
    public MagnificationFilter getMagFilterConstant() {
        if(magFilter == null) {
            return Texture.MagnificationFilter.Bilinear;
        }
        
        if(magFilter.equals("NEAREST")) {
            return Texture.MagnificationFilter.NearestNeighbor;
        }
        
        if(magFilter.equals("LINEAR")) {
            return Texture.MagnificationFilter.Bilinear;
        }
        
        return Texture.MagnificationFilter.Bilinear;
    }
    
    public MinificationFilter getMinFilterConstant() {
        if(minFilter == null) {
            return Texture.MinificationFilter.Trilinear;
        }
        
        if(minFilter.equals("NEAREST")) {
            return Texture.MinificationFilter.NearestNeighborNoMipMaps;
        }
        
        if(minFilter.equals("LINEAR")) {
            return Texture.MinificationFilter.BilinearNoMipMaps;
        }
        
        if(minFilter.equals("NEAREST_MIPMAP_NEAREST")) {
            return Texture.MinificationFilter.NearestNeighborNearestMipMap;
        }
        
        if(minFilter.equals("NEAREST_MIPMAP_LINEAR")) {
            return Texture.MinificationFilter.NearestNeighborLinearMipMap;
        }
        
        if(minFilter.equals("LINEAR_MIPMAP_NEAREST")) {
            return Texture.MinificationFilter.BilinearNearestMipMap;
        }
        
        if(minFilter.equals("LINEAR_MIPMAP_LINEAR")) {
            return Texture.MinificationFilter.Trilinear;
        }
        
        return Texture.MinificationFilter.Trilinear;
    }

    public WrapMode getWrapSConstant() {
        if(wrapS == null) {
            return Texture.WrapMode.Repeat;
        }
        
        if(wrapS.equals("WRAP")) {
            return Texture.WrapMode.Repeat;
        }
        
        if(wrapS.equals("NONE")) {
            return Texture.WrapMode.Clamp;
        }
        
        return Texture.WrapMode.Repeat;
    }    

    public WrapMode getWrapTConstant() {
        if(wrapT == null) {
            return Texture.WrapMode.Repeat;
        }
        
        if(wrapT.equals("WRAP")) {
            return Texture.WrapMode.Repeat;
        }
        
        if(wrapT.equals("NONE")) {
            return Texture.WrapMode.Clamp;
        }
        
        return Texture.WrapMode.Repeat;
    }    
}
