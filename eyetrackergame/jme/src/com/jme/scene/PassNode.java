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

package com.jme.scene;

import java.io.IOException;
import java.util.ArrayList;

import com.jme.renderer.RenderContext;
import com.jme.renderer.Renderer;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/** PassNode Creator: rikard.herlitz, 2007-maj-10 */

public class PassNode extends Node {
    private static final long serialVersionUID = 1L;

    private ArrayList<PassNodeState> passNodeStates =
            new ArrayList<PassNodeState>();

    public PassNode(String name) {
        super(name);
    }

    public PassNode() {
        super();
    }

    @Override
    public void draw(Renderer r) {
        if (children == null) {
            return;
        }

        RenderContext context =
                DisplaySystem.getDisplaySystem().getCurrentContext();
        DisplaySystem.getDisplaySystem().getRenderer().getQueue().swapBuckets();
        for (PassNodeState pass : passNodeStates) {
            if (!pass.isEnabled()) {
                continue;
            }

            pass.applyPassNodeState(r, context);

            Spatial child;
            for (int i = 0, cSize = children.size(); i < cSize; i++) {
                child = children.get(i);
                if (child != null) {
                    child.onDraw(r);
                }
            }
            r.renderQueue();

            pass.resetPassNodeStates(r, context);
        }
        DisplaySystem.getDisplaySystem().getRenderer().getQueue().swapBuckets();
    }

    public void addPass(PassNodeState toAdd) {
        passNodeStates.add(toAdd);
    }

    public void insertPass(PassNodeState toAdd, int index) {
        passNodeStates.add(index, toAdd);
    }

    public boolean containsPass(PassNodeState s) {
        return passNodeStates.contains(s);
    }

    public boolean removePass(PassNodeState toRemove) {
        return passNodeStates.remove(toRemove);
    }

    public PassNodeState getPass(int index) {
        return passNodeStates.get(index);
    }

    public int nrPasses() {
        return passNodeStates.size();
    }

    public void clearAll() {
        passNodeStates.clear();
    }

	@Override
	public void write(JMEExporter e) throws IOException {
		super.write(e);
		OutputCapsule capsule = e.getCapsule(this);
		capsule.writeSavableArrayList(passNodeStates, "passNodeStates", null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(JMEImporter e) throws IOException {
		super.read(e);
		InputCapsule capsule = e.getCapsule(this);
		passNodeStates = capsule.readSavableArrayList("passNodeStates", null);
	}
}
