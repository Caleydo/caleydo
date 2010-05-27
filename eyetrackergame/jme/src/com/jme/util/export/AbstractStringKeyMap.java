/*
 * @(#)$Id: AbstractStringKeyMap.java 4551 2009-07-26 18:32:26Z blaine.dev $
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

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

/**
 * This class implements persistence for the String keys of a Savable map.
 */
abstract public class AbstractStringKeyMap<V>
        extends HashMap<String,V> implements Savable {
    public AbstractStringKeyMap() {
        super();
    }

    public AbstractStringKeyMap(int initialCapacity) {
        super(initialCapacity);
    }

    public AbstractStringKeyMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public AbstractStringKeyMap(Map<? extends String, ? extends V> m) {
        super(m);
    }

    /**
     * Subclasses must super.write(e)!
     */
    public void write(JMEExporter e) throws IOException {
        // TODO:  Verify that Map.keySet() and Map.values() guarantee parallel
        // entrySet ordering.
        e.getCapsule(this) .write(
                keySet().toArray(new String[0]), "keys", null);
    }

    protected String[] unsavedKeys;

    /**
     * Subclasses must super.read(e), and can then use unsavedKeys to
     * populate the map.
     */
    public void read(JMEImporter e) throws IOException {
        clear();
        unsavedKeys = e.getCapsule(this).readStringArray("keys", null);
    }

    public Class<? extends AbstractStringKeyMap> getClassTag() {
        return this.getClass();
    }
}
