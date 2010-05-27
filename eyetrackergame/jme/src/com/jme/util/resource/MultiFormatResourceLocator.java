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

package com.jme.util.resource;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

/**
 * This class extends the behavior of the {@link SimpleResourceLocator} by appending different file extensions
 * to the resource name, if it cannot find a resource with the extension specified in the path name.
 * 
 * @author Joshua Slack
 */
public class MultiFormatResourceLocator extends SimpleResourceLocator {

    private String[] extensions;
    private boolean trySpecifiedFormatFirst = false;

    public MultiFormatResourceLocator(URI baseDir) {
        this(baseDir, ".dds", ".tga", ".png", ".jpg", ".gif");
    }

    public MultiFormatResourceLocator(URL baseDir) throws URISyntaxException {
        this(baseDir, ".dds", ".tga", ".png", ".jpg", ".gif");
    }
    
    public MultiFormatResourceLocator(URI baseDir, String ... extensions) {
        super(baseDir);
        
        if (extensions == null) {
            throw new NullPointerException("extensions can not be null.");
        }
        this.extensions = extensions;
    }

    public MultiFormatResourceLocator(URL baseDir, String ... extensions) throws URISyntaxException {
        this(baseDir.toURI(), extensions);
    }
    
    @Override
    public URL locateResource(String resourceName) {
        if (trySpecifiedFormatFirst) {
            URL u = super.locateResource(resourceName);
            if (u != null) {
                return u;
            }
        }
        
        String baseFileName = getBaseFileName(resourceName);
        for ( String extension : extensions ) {
            URL u = super.locateResource( baseFileName + extension );
            if ( u != null ) {
                return u;
            }
        }
        
        if (!trySpecifiedFormatFirst) {
            // If all else fails, just try the original name.
            return super.locateResource(resourceName);
        } else {
            return null;
        }
    }

    private String getBaseFileName(String resourceName) {
        File f = new File(resourceName);
        String name = f.getPath();
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            return name;
        } else {
            return name.substring(0, dot);
        }
    }

    public boolean isTrySpecifiedFormatFirst() {
        return trySpecifiedFormatFirst;
    }

    public void setTrySpecifiedFormatFirst(boolean trySpecifiedFormatFirst) {
        this.trySpecifiedFormatFirst = trySpecifiedFormatFirst;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MultiFormatResourceLocator) {
            return baseDir.equals(((MultiFormatResourceLocator)obj).baseDir) &&
                    Arrays.equals( extensions, ( (MultiFormatResourceLocator) obj ).extensions );
        }
        return false;
    }
}
