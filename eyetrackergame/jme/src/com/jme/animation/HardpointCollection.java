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
package com.jme.animation;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

public class HardpointCollection implements Savable {
    private ArrayList<Bone> hardpoints;
    
    public void addHardpoint(Bone hardpoint) {
        if(hardpoints == null) {
            hardpoints = new ArrayList<Bone>();
        }
        
        hardpoints.add(hardpoint);
    }
    
    public void removeHardpoint(int index) {
        if(hardpoints != null) {
            hardpoints.remove(index);
        }
    }
    
    public void removeHardpoint(Bone hardpoint) {
        if(hardpoints != null) {
            hardpoints.remove(hardpoint);
        }
    }
    
    public void clear() {
        if(hardpoints != null) {
            hardpoints.clear();
        }
    }
    
    public void addHardpoints(Bone[] newHardpoints) {
        if(hardpoints == null) {
            hardpoints = new ArrayList<Bone>();
        }
        
        for(int i = 0; i < newHardpoints.length; i++) {
            hardpoints.add(newHardpoints[i]);
        }
    }
    
    public void addHardpoints(ArrayList<Bone> newHardpoints) {
        if(newHardpoints == null) {
            return;
        }
        
        if(hardpoints == null) {
            hardpoints = new ArrayList<Bone>();
        }
            
        hardpoints.addAll(newHardpoints);
    }
    
    public Bone getHardpoint(int index) {
        if(hardpoints == null) {
            return null;
        }
        
        return hardpoints.get(index);
    }
    
    public Bone[] toArray() {
        Bone[] hardpointArray = new Bone[hardpoints.size()];
        hardpoints.toArray(hardpointArray);
        return hardpointArray;
    }
    
    public Bone getHardpoint(String name) {
        if(hardpoints == null || name == null) {
            return null;
        }
        
        for(int i = 0, max = hardpoints.size(); i < max; i++) {
            if(name.equals(hardpoints.get(i).getName())) {
                return hardpoints.get(i);
            }
        }
        
        return null;
    }
    
    public int getNumber() {
        if(hardpoints == null) {
            return 0;
        }
        
        return hardpoints.size();
    }

    public Class getClassTag() {
        return this.getClass();
    }

    public void read(JMEImporter im) throws IOException {
        InputCapsule cap = im.getCapsule(this);
        hardpoints = cap.readSavableArrayList("hardpoints", null);
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule cap = ex.getCapsule(this);
        cap.writeSavableArrayList(hardpoints, "hardpoints", null);
    }

}
