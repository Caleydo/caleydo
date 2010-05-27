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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * A conservative ResourceLocator implementation that only resolves resources
 * with absolute resource name paths.
 * It may be used in two ways <UL>
 *   <LI>Specify no base URI, and requested resource names must be in the form
 *       of absolute URL strings.
 *   <LI>Specify a base URI, and requested resources names may be of the form
 *       above (which must match or be a sub-URL of the base URI), or be an
 *       absolute path like "/a/b/c" the beginning of which must match the
 *       path portion of the base URI.
 * </UL>
 * Either way, lookups succeed only for resources requested with absolute
 * paths, and there is no recursion.
 * <P>
 * This ResourceLocator is specifically NOT for loading resources from the
 * Classpath.  Use *Classpath*ResourceLocator class(es) for that purpose.
 * </P>
 *
 * @see com.jme.util.resource.ResourceLocator
 * @author Blaine Simpson (blaine dot simpson at admc dot com)
 */
public class AbsoluteResourceLocator implements ResourceLocator {
    /*
     * I have commented out the encoding stuff, because everything seems to
     * just work better without it.  Methods like URI.getPath() return
     * unencoded paths, and this works great, including converting paths with
     * \, " ", +, / to URLs and fetching with that.
     * If I have missed some case, please fix, but good luck testing all of
     * the combinations!
     */
    private static final Logger logger =
            Logger.getLogger(AbsoluteResourceLocator.class.getName());

    private Pattern baseUriPattern;
    private URL pathlessBaseUrl;

    /**
     * Instantiate a locator for any resource present at the specified
     * absolute resource path.
     */
    public AbsoluteResourceLocator() {
        try {
            pathlessBaseUrl = new URL("file:");
        } catch (MalformedURLException mue) {
            throw new RuntimeException(mue);
        }
    }

    /**
     * Instantiate a locator for resources residing underneath the specified
     * base URI, and requested with absolute path.
     * <P>
     * To restrict to any absolute paths within a URI base, use a URI path of
     * just "/", like <CODE>new URI("http://acme.com/")</CODE> or
     * <CODE>new File("/").toURI()</CODE>.
     * </P> <P>
     *   A rather non-intuitive aspect of Java file URIs, is that a
     *   trailing "/" will be truncated unless the item is present and is a
     *   directory...
     *   And also that resolution of URL paths ending with /, like
     *   "/home/blaine/info.txt/" will succeed and can read the file.
     * </P>
     *
     * @param baseURI <B>IMPORTANT</B>:  The whole purpose here is to specify
     *        a path with an absolute path to validate against.
     *        Therefore, to cover an entire web site, you must use
     *        <CODE>http://pub.admc.com/</CODE>, not
     *        <CODE>http://pub.admc.com</CODE>.
     * @throws IllegalArgumentException if the specified baseUri does not have
     *         an absolute URI path, or is otherwise unacceptable.
     */
    public AbsoluteResourceLocator(URI baseUri) {
        String basePath = baseUri.getPath();
        // Every test I run shows a real getPath(), but there may be URI types,
        // current or future, which I have not tested, therefore...
        if (basePath == null || basePath.length() < 1)
            throw new IllegalArgumentException(
                    "Specified URI has no path: " + baseUri);
        try {
            URL tmpUrl = baseUri.toURL();
            pathlessBaseUrl = new URL(tmpUrl.getProtocol(),
                    tmpUrl.getHost(), tmpUrl.getPort(), "");
        } catch (MalformedURLException mue) {
            throw new IllegalArgumentException(mue);
        }
        String matchString = basePath.matches(".*[/\\\\]")
                ? ("\\Q" + basePath + "\\E.*")
                : ("\\Q" + basePath + "\\E(?:[/\\\\].*)?");
        // These patterns test for either
        //     (basePattern or basPattern*)  IFF basePattern ends with / or \.
        // Or
        //     (basePattern[/\]*) otherwise
        // starts with basePath + "/" or "\".
        logger.fine(
            "URL path-matching pattern for AbsoluteResourceLocator instance: '"
            + matchString + "'");
        baseUriPattern = Pattern.compile(matchString);
    }

    /*
     * @returns null if resource not found, according to algorithm specified
     *          in the class JavaDoc.
     * @resourceName String may be of the format <CODE><PRE>
     *     protocol:.../absolute/path
     *   </PRE></CODE> or
     *   <CODE><PRE>
     *     /absolute/path
     *   </PRE></CODE>
     *   The first type must be a valid URL with absolute url.getPath().
     *   For the second form, the specified absolute path will be sought in
     *   this AbsoluteResourceLocator's baseUri.
     * @see AbsoluteResourceLocator
     * @see com.jme.util.resource.ResourceLocator#locateResource(String)
     */
    public URL locateResource(String resourceName) {
        if (resourceName == null) return null;
        String spec = resourceName;
        /*
        String spec = null;
        try {
            //this fixes a bug in JRE1.5 (file handler does not decode "+" to
            //spaces)
            spec = URLEncoder.encode(resourceName, "UTF-8")
                    .replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException uee) {
            logger.warning("Malformatted URL: " + uee);
            // Don't pass the Throwable.  Stack trace would be too verbose here.
            return null;
        }
        */
        URL resourceUrl = null;
        try {
            resourceUrl = new URL(resourceName);
        } catch (Exception e) {
            // baseURI required for non-URL-string resource names
            if (pathlessBaseUrl == null) return null;
            // Interpret spec as an absolute URL "path".
            try {
                resourceUrl = new URL(pathlessBaseUrl, spec);
            } catch (MalformedURLException mue) {
                logger.warning("Malformatted URL: " + mue);
                // Don't pass the Throwable.  Stack trace would be too verbose
                // here.
                return null;
            }
        }

        // We now have the URL object that we wish to load (resourceUrl).
        // Need to check if we allow this URL, and if the resource is present.
        String resourcePath = resourceUrl.getPath();
        if (resourcePath == null
                || resourcePath.length() < 1 || (resourcePath.charAt(0) != '/'
                && resourcePath.charAt(0) != '\\')) return null;
        // No-op unless resource path is absolute.

        if (baseUriPattern != null
                && !baseUriPattern.matcher(resourcePath).matches())
            return null;
        // No-op if baseUri set, but requested resource name doesn't match it.

        try {
            resourceUrl.openConnection().connect();  // Validates presence
             // In the case of http, the http server will probably return an
             // error page, but we can do nothing about that here.
            return resourceUrl;
        } catch (MalformedURLException mue) {
        } catch (IOException ioe) {
        }
        return null;
    }
}
