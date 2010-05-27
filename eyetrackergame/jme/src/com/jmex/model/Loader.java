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

package com.jmex.model;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.jme.scene.Node;
import com.jme.system.JmeException;

/**
 * Generic abstract Loader class for FileFormat Loaders to
 * inherit from to standardize file loading.  Future file loaders should
 * extend this class.
 * @author Jack Lindamood
 */
public abstract class Loader {
    public long loadFlags=LOAD_ALL;
    public final static long LOAD_CONTROLLERS=1;
    public final static long LOAD_ALL=2*2*2*2*2*2*2*2-1;
    public final static long PRECOMPUTE_BOUNDS=2;
    protected boolean dirty=false;
    protected URL baseUrl;


    /**
     * Default Constructor.  Flags are LOAD_ALL by default.
     */
    public Loader(){
    }

    /**
     * Constructs file loader with given flags.
     * @param flags The flags for this file loader
     */
    public Loader(int flags){
        loadFlags=flags;
    }

    /**
     * Sets the base path to load textures from.
     * @param path New texture path
     */
    public void setBase(URL path){
        baseUrl=path;
    }
    /**
     * Returns base path for textures
     * @return URL to base path
     */
    public URL getBase(){
        return baseUrl;
    }

    /**
     * Returns a copy of the previously loaded file.  The two should
     * be able to operate separately acording to the needs of the loader,
     * but are allowed and encouraged to share as much as posible.
     * @return A node to the new copy
     */
    public abstract Node fetchCopy();
    /**
     * Loads a MilkShape file from the path in the string s.  All texture/alpha
     * maps associated with the file are by default in the same directory as the
     * .ms3d file specified.  Texture directory can be changed by a call to
     * setBasePath(String), allowing the programmer to seperate storage of model
     * files and pictures.
     *
     * @param s Filename
     * @throws JmeException Either .ms3d file or texture files don't exist
     * @return Node to the loaded file.
     * @see Node
     */
    public Node load(String s){
        try {
            return load(new File(s).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new JmeException("Couldn't find file in load(String): " + e.getMessage());
        }
    }

    /**
     * Loads a URL, similar to <code>load(String s)</code>
     * @param url URL to load
     * @return Node to the loaded file
     */
    public abstract Node load(URL url);

    /**
     * Sets the give flag to true
     * @param flag New flag to set
     */
    public void setLoadFlag(long flag) {
        loadFlags|=flag;
        dirty=true;
    }
    /**
     * Removes the given flag, setting it to false
     * @param flag The flag to remove
     */
    public void removeLoadFlag(long flag){
        loadFlags&=~flag;
        dirty=true;
    }
    /**
     * Returns the current flag state, as a long
     * @return long to represent current flag state
     */
    public long getLoadFlags(){
        return loadFlags;
    }

}
