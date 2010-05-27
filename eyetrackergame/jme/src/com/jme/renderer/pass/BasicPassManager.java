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

package com.jme.renderer.pass;

import java.util.ArrayList;

import com.jme.renderer.Renderer;

/**
 * <code>BasicPassManager</code> controls a set of passes and sends through
 * calls to render and update.
 * 
 * @author Joshua Slack
 * @version $Id: BasicPassManager.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class BasicPassManager {

    protected ArrayList<Pass> passes = new ArrayList<Pass>();

    public void add(Pass toAdd) {
        if (toAdd != null)
            passes.add(toAdd);
    }

    public void insert(Pass toAdd, int index) {
        passes.add(index, toAdd);
    }

    public boolean contains(Pass s) {
        return passes.contains(s);
    }

    public boolean remove(Pass toRemove) {
        return passes.remove(toRemove);
    }

    public Pass get(int index) {
        return passes.get(index);
    }

    public int passes() {
        return passes.size();
    }

    public void clearAll() {
        cleanUp();
        passes.clear();
    }

    public void cleanUp() {
        for (int i = 0, sSize = passes.size(); i < sSize; i++) {
            Pass p = passes.get(i);
            p.cleanUp();
        }
    }

    public void renderPasses(Renderer r) {
        for (int i = 0, sSize = passes.size(); i < sSize; i++) {
            Pass p = passes.get(i);
            p.renderPass(r);
        }
    }

    public void updatePasses(float tpf) {
        for (int i = 0, sSize = passes.size(); i < sSize; i++) {
            Pass p = passes.get(i);
            p.updatePass(tpf);
        }
    }

}
