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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager class for locator utility classes used to find various assets. (XXX: Needs more documentation)
 * 
 * @author Joshua Slack
 */

public class ResourceLocatorTool {
    private static final Logger logger = Logger.getLogger(ResourceLocatorTool.class
            .getName());

    public static final String TYPE_TEXTURE = "texture";
    public static final String TYPE_MODEL = "model";
    public static final String TYPE_PARTICLE = "particle";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_SHADER = "shader";

    private static final Map<String, ArrayList<ResourceLocator>> locatorMap = new HashMap<String, ArrayList<ResourceLocator>>();

    public static URL locateResource(String resourceType, String resourceName) {
        if (resourceName == null) {
            return null;
        }
        synchronized (locatorMap) {
            ArrayList<ResourceLocator> bases = locatorMap.get(resourceType);
            if (bases != null) {
                for (int i = bases.size(); --i >= 0; ) {
                    ResourceLocator loc = bases.get(i);
                    URL rVal = loc.locateResource(resourceName);
                    if (rVal != null) {
                        return rVal;
                    }
                }
            }
            // last resort...
            try {
                URL u = ResourceLocatorTool.class.getResource(resourceName);
                if (u != null) {
                    return u;
                }
            } catch (Exception e) { 
                logger.logp(Level.WARNING, ResourceLocatorTool.class.getName(),
                        "locateResource(String, String)", e.getMessage(), e);
            }

            logger.log(Level.WARNING, "Unable to locate: {0}", resourceName);
            return null;
        }
    }

    public static void addResourceLocator(String resourceType,
            ResourceLocator locator) {
        if (locator == null) return;
        synchronized (locatorMap) {
            ArrayList<ResourceLocator> bases = locatorMap.get(resourceType);
            if (bases == null) {
                bases = new ArrayList<ResourceLocator>();
                locatorMap.put(resourceType, bases);
            }

            if (!bases.contains(locator)) {
                bases.add(locator);
            }
        }
    }

    public static boolean removeResourceLocator(String resourceType,
            ResourceLocator locator) {
        synchronized (locatorMap) {
            ArrayList<ResourceLocator> bases = locatorMap.get(resourceType);
            if (bases == null) {
                return false;
            }
            return bases.remove(locator);
        }
    }
}
