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

package com.jme.util;

import java.util.ArrayList;

/**
 * A set of configuration describing how fields will be treated during the cloning process
 * including ignoring and shallow copying fields by name.
 *
 * @author kevin
 * @version $Id: CloneConfiguration.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class CloneConfiguration {
        /** A configuration that specifies that all geometry buffers should be shared between copies */
        public static final CloneConfiguration SHARED_GEOM_BUFFER_CLONE = 
                                                                                        new CloneConfiguration(new String[] {},
                                                                                                                                   new String[] {"vertBuf","colorBuf", "texBuf","normBuf"});
        /** A configuration that specifies that color and texture buffers should be shared between copies */
        public static final CloneConfiguration SHARED_COLOR_AND_TEXTURE_BUFFER_CLONE = 
                                                                                        new CloneConfiguration(new String[] {},
                                                                                                                                   new String[] {"colorBuf", "texBuf"});
        
        /** The list of ignored fields */
        private ArrayList<String> ignored = new ArrayList<String>();
        /** THe list of fields that should only be shallow copied */
        private ArrayList<String> shallow = new ArrayList<String>();
        
        /**
         * Create a new empty clone configuration
         */
        public CloneConfiguration() {
        }
        
        /**
         * Create a configuration 
         * 
         * @param ignore The list of fields to ignore 
         * @param shal The list of fields to shallow copy
         */
        public CloneConfiguration(String[] ignore, String[] shal) {
                for (int i=0;i<ignore.length;i++) {
                        ignored.add(ignore[i]);
                }
                for (int i=0;i<shal.length;i++) {
                        shallow.add(shal[i]);
                }
        }
        
        /**
         * Add an ignored field
         * 
         * @param name The name of the field to ignore during the cloning process
         */
        public void addIgnoredField(String name) {
                ignored.add(name);
        }

        /**
         * Add a fied to be shallow copied
         * 
         * @param name The name of the field to ignore during the cloning process
         */
        public void addShallowCopyField(String name) {
                shallow.add(name);
        }
        
        /**
         * Get the list of fields to be ignored
         * 
         * @return The list of fields to be ignored
         */
        public ArrayList<String> getIgnored() {
                return ignored;
        }

        /**
         * Get the list of fields to be shallow copied
         * 
         * @return The list of fields to be shallow copied
         */
        public ArrayList<String> getShallow() {
                return shallow;
        }
}


