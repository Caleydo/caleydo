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

package com.jme.scene;

import java.util.HashMap;
import java.util.WeakHashMap;

import com.jme.util.export.Savable;


/**
 * UserDataManager maintains a map of objects that are assigned to scene
 * data (Spatials). This data can be any user defined Savable data.
 * @author Mark Powell
 *
 */
public class UserDataManager {
	/** Handle for the singleton instance */
	private static UserDataManager instance;
	
    /** Map for maintaining bindings between Spatials and user data */
	private WeakHashMap<Spatial, HashMap<String, Savable>> dataMap;
	
	/**
	 * private constructor instantiates the UserDataManager instance.
	 *
	 */
	private UserDataManager() {
		dataMap = new WeakHashMap<Spatial, HashMap<String, Savable>>();
	}
	
	/**
	 * Obtains the singleton instance of the UserDataManager.
	 * @return the singleton instance of UserDataManager.
	 */
	public static UserDataManager getInstance() {
		if(instance == null) {
			instance = new UserDataManager();
		}
		return instance;
	}
	
	public HashMap<String, Savable> getAllData(Spatial key) {
		return dataMap.get(key);
	}
	
	public void setAllData(Spatial key, HashMap<String, Savable> data) {
		dataMap.put(key, data);
	}
	
    /**
     * Maps a Spatial and a key to user data(a Savable)
     * @param spatial Main key used in mapping
     * @param key Key for finegrained mapping inside the provided Spatial
     * @param data User data to map against the Spatial and key
     */
	public void setUserData(Spatial spatial, String key, Savable data) {
		HashMap<String, Savable> userData = dataMap.get(spatial);
		if(userData == null) {
			userData = new HashMap<String, Savable>();
			dataMap.put(spatial, userData);
		}
		
		userData.put(key, data);
	}
	
    /**
     * Retrieves a user data object(Savable) using a Spatial key and a finegrained key
     * @param spatial Main key used in mapping
     * @param key Key for finegrained mapping inside the provided Spatial
     * @return User data object retrieved with the keys provided(or null if not stored)
     */
	public Savable getUserData(Spatial spatial, String key) {
		HashMap<String, Savable> userData = dataMap.get(spatial);
		if(userData == null) {
			return null;
		}
		
		return userData.get(key);
	}
	
    /**
     * Removed a user data object(Savable) from the map using a Spatial key and a finegrained key
     * @param spatial Main key used in mapping
     * @param key Key for finegrained mapping inside the provided Spatial
     * @return User data object removed with the keys provided(or null if not stored)
     */
	public Savable removeUserData(Spatial spatial, String key) {
		HashMap<String, Savable> userData = dataMap.get(spatial);
		if(userData == null) {
			return null;
		}
		
		return userData.remove(key);
	}
	
	public void bind(Spatial key, Spatial original) {
		HashMap<String, Savable> userData = dataMap.get(original);
		if(userData != null) {
			HashMap<String, Savable> userDataCopy = (HashMap<String, Savable>)userData.clone();
			dataMap.put(key, userDataCopy);
		}
	}

    public void clear() {
        dataMap.clear();
    }

}
