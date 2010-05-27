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

package com.jmex.model.collada;

import java.util.WeakHashMap;
import java.util.logging.Logger;

import com.jmex.model.collada.schema.extraType;
import com.jmex.model.collada.schema.techniqueType5;

public class ExtraPluginManager {
    private static final Logger logger = Logger.getLogger(ExtraPluginManager.class
            .getName());
    
	private static WeakHashMap<String, ExtraPlugin> plugins = new WeakHashMap<String, ExtraPlugin>();
	
	public static void registerExtraPlugin(String key, ExtraPlugin plugin) {
		plugins.put(key, plugin);
	}
	
	public static Object processExtra(Object target, extraType extra) throws Exception {
		if(extra.hastechnique()) {
			techniqueType5 tt = extra.gettechnique();
			
			if(tt.hasprofile()) {
				String key = tt.getprofile().toString();
				ExtraPlugin ep = plugins.get(key);
				if(ep != null) {
                    logger.info("Found plugin to process type: " + key);
					return ep.processExtra(key, target, extra);
				} else {
                    logger.warning("Could not process extra of type: " + key);
                }
                
			}
		} 
		return null;
	}
}
