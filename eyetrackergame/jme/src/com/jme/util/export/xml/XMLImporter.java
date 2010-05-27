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

package com.jme.util.export.xml;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.Savable;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URI;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Part of the jME XML IO system as introduced in the google code jmexml project.
 * @author Kai Rabien (hevee) - original author of the code.google.com jmexml project
 * @author Doug Daniels (dougnukem) - adjustments for jME 2.0 and Java 1.5
 */
public class XMLImporter implements JMEImporter, ResourceLocator {

    private DOMInputCapsule domIn;
    private URI baseUri = null;
    // A single-load-state base URI for texture-loading.
    
    public XMLImporter() {
    }
    
    synchronized public Savable load(InputStream f) throws IOException {
        /* Leave this method synchronized.  Calling this method from more than
         * one thread at a time for the same XMLImporter instance will clobber
         * the XML Document instantiated here. */
        if (baseUri != null) {
            ResourceLocatorTool.addResourceLocator(
                    ResourceLocatorTool.TYPE_TEXTURE, this);
        }
        try {
            domIn = new DOMInputCapsule(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f), this);
            return domIn.readSavable(null, null);
        } catch (SAXException e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        } catch (ParserConfigurationException e) {
            IOException ex = new IOException();
            ex.initCause(e);
            throw ex;
        } finally {
            if (baseUri != null) {
                ResourceLocatorTool.removeResourceLocator(
                        ResourceLocatorTool.TYPE_TEXTURE, this);
            }
        }
    }

    synchronized public Savable load(URL f) throws IOException {
        /* Method is only synchronized to ensure that baseUri effects only
         * the file specified here.
         * Note that the instance would get synchronized in the
         * load(InputStream) call anyways. */

        try {
            try {
                baseUri = f.toURI();
            } catch (Exception e) {
                baseUri = null;
            }
            return load(f.openStream());
        } finally {
            baseUri = null;
        }
    }

    synchronized public Savable load(File f) throws IOException {
        /* Method is only synchronized to ensure that baseUri effects only
         * the file specified here.
         * Note that the instance would get synchronized in the
         * load(InputStream) call anyways. */
        try {
            if (f.isFile() && f.getParentFile().isDirectory())
                //baseUri = f.getParentFile().toURI();
                baseUri = f.toURI();
            return load(new FileInputStream(f));
        } finally {
            baseUri = null;
        }
    }

    public InputCapsule getCapsule(Savable id) {
        return domIn;
    }

    public static XMLImporter getInstance() {
        return new XMLImporter();
    }

    /*
     * This returns rooted URLs, so you can use them to load resources.
     *
     * For persistence purposes, you should store the input to this method,
     * not the output, because the input may be relative whereas the output is
     * rooted to ensure it can load its resource successfully.
     *
     * @see com.jme.util.resource.RelativeRsoruceLocator#locateResource(String)
     */
    public URL locateResource(String resourceName) {
        if (baseUri == null || resourceName == null
                || resourceName.length() < 1 || resourceName.charAt(0) == '/'
                || resourceName.charAt(0) == '\\') return null;
        // No-op unless baseUri set for instance, and resourceName is relative.

        /* The remainder is the safe and conservative subset of code copied
         * from SimpleResourceLocator.locateResource(String). */

        try {
            String spec = URLEncoder.encode(resourceName, "UTF-8");
            //this fixes a bug in JRE1.5 (file handler does not decode "+" to
            //spaces)
            spec = spec.replaceAll("\\+", "%20");

            URL rVal = new URL(baseUri.toURL(), spec);
            rVal.openConnection().connect();  // Validates presence
             // In the case of http, the http server will probably return an
             // error page, but we can do nothing about that here.
            return rVal;
        } catch (IOException e) {
        } catch (IllegalArgumentException e) {
        }
        return null;
    }
}
