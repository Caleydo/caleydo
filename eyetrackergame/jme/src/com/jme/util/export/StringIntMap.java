/*
* @(#)$Id: StringIntMap.java 4551 2009-07-26 18:32:26Z blaine.dev $
*
* Copyright (c) 2009, Blaine Simpson and the jMonkeyEngine Dev Team.
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


package com.jme.util.export;

import java.io.IOException;
import java.util.Map;

/**
 * A Savable String-to-Int map.
 */
public class StringIntMap extends AbstractStringKeyMap<Integer> {
    static final long serialVersionUID = -6443631854556869718L;

    public StringIntMap() {
        super();
    }

    public StringIntMap(int initialCapacity) {
        super(initialCapacity);
    }

    public StringIntMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public StringIntMap(Map<? extends String, ? extends Integer> m) {
        super(m);
    }

    /**
     * Subclasses must super.write(e)!
     */
    public void write(JMEExporter e) throws IOException {
        super.write(e);
        // TODO:  Verify that Map.keySet() and Map.values() guarantee parallel
        // entrySet ordering.
        int[] nativeArray = new int[size()];
        int i = -1;
        for (int integ : values()) nativeArray[++i] = integ;
        e.getCapsule(this).write(nativeArray, "vals", null);
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        int[] vals = e.getCapsule(this).readIntArray("vals", null);
        if (unsavedKeys == null)
            throw new IOException("Keys not stored");
        if (vals == null)
            throw new IOException("Vals not stored");
        if (unsavedKeys.length != vals.length)
            throw new IOException("Key/Val size mismatch: "
                    + unsavedKeys.length + " vs. " + vals.length);
       for (int i = 0; i < unsavedKeys.length; i++)
           put(unsavedKeys[i], vals[i]);
    }
}
