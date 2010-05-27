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

package com.jme.bounding;

import java.util.List;
import java.util.Map;

import com.jme.scene.TriMesh;

/**
 * UsageTreeController defines a CollisionTreeController implementation that
 * removes cache elements based on the frequency of usage. By default, and 
 * implementation in the CollisionTreeManager, the cache's key set will be ordered
 * with the first element being the oldest used. Therefore, UsageTreeController
 * simply removes elements from the cache starting at the first key and working
 * up until the deisred size is reached or we run out of elements. 
 * @author Mark Powell
 *
 */
public class UsageTreeController implements CollisionTreeController {

	/**
	 * removes elements from cache (that are not in the protectedList) until
	 * the desiredSize is reached. It removes elements from the keyset as they
	 * are ordered.
	 * @param cache the cache to clean.
	 * @param protectedList the list of elements to not remove.
	 * @param desiredSize the final size of the cache to attempt to reach.
	 */
	public void clean(Map<TriMesh, CollisionTree> cache, 
			List<TriMesh> protectedList, int desiredSize) {
		
		//get the ordered keyset (this will be ordered with oldest to newest).
		Object[] set = cache.keySet().toArray();
		int count = 0;
		//go through the cache removing items that are not protected until the
		//size of the cache is small enough to return.
		while(cache.size() > desiredSize && count < set.length) {
			if(protectedList == null || !protectedList.contains(set[count])) {
				cache.remove(set[count]);
			}
			count++;
		}
	}

}
