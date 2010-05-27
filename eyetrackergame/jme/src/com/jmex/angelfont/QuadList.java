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

package com.jmex.angelfont;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dhdd, Andreas Grabner
 * @author Momoko_Fan (enhancements)
 */
public class QuadList {

    private List<FontQuad> quads = new ArrayList<FontQuad>();

    private int numActive = 0;

    public void addQuad(FontQuad quad) {
        quads.add(quad);
    }

    public FontQuad getQuad(int index) {
        return quads.get(index);
    }

    public int getQuantity() {
        return quads.size();
    }

    public void ensureSize(int size) {
        if (quads.size() < size) {
            int quadSize = quads.size();
            for (int i = 0; i < size - quadSize; i++) {
                quads.add(new FontQuad());
            }
        }
        for (int i = 0; i < size; i++) {
            quads.get(i).setSize(0, 0);
        }
    }

    public int getNumActive() {
        return numActive;
    }

    public void setNumActive(int numActive) {
        this.numActive = numActive;
    }

}
