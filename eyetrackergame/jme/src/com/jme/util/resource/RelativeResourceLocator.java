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

import java.net.URLEncoder;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

import com.jme.util.resource.ResourceLocator;

/**
 * A conservative ResourceLocator implementation that adds to the search
 * path just the parent directory of the specified URI, and it is  only
 * used for resources requested with relative paths.
 * </P> <P>
 * Relative paths may have multiple segments, like <CODE>a/b/c.txt</CODE>,
 * they just can't be absolute, like <CODE>/a/b.c.txt</CODE>.
 * </P> <P>
 * It is basic behavior of relative URL lookups that a lookup of "x" relative
 * to "/a/b" would match both "/a/x" and "/a/b/x".
 * </P>
 *
 * @see ResourceLocator
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class RelativeResourceLocator implements ResourceLocator {
    private URI baseUri;

    public RelativeResourceLocator(URI baseUri) {
        this.baseUri = baseUri;
    }

    /**
     * Convenience wrapper
     *
     * @throws URISyntaxException if input URL is invalid.
     *         Only doing this due to Java language contraints.
     *         Would prefer to throw an unchecked exception for this.
     * @see #RelativeResourceLocator(URI)
     */
    public RelativeResourceLocator(URL baseUrl) throws URISyntaxException {
        this(baseUrl.toURI());
    }

    /*
     * This returns rooted URLs, so you can use them to load resources.
     *
     * For persistence purposes, you should store the input to this method,
     * not the output, because the input may be relative whereas the output is
     * rooted to ensure it can load its resource successfully.
     *
     * @returns null if resource not found, according to algorithm specified
     *          in the class JavaDoc.
     * @see RelativeResourceLocator
     * @see ResourceLocator#locateResource(String)
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
