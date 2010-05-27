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
package com.jme.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * AnimationEventManager maintains a collection of animation triggers. A trigger
 * is found using the BoneAnimation and the keyframe. The bone animation will
 * request the list of events on a keyframe change. All events will have the 
 * performAction method called as needed from the bone animation. 
 * 
 * @author Mark Powell
 *
 */
public class AnimationEventManager {
	//singleton instance of this manager
	private static AnimationEventManager instance;
	//data container
	private WeakHashMap<BoneAnimation, HashMap<Integer, ArrayList<AnimationEvent>>> dataMap;

	/**
	 * private constructor instantiates the UserDataManager instance.
	 *
	 */
	private AnimationEventManager() {
		dataMap = new WeakHashMap<BoneAnimation, HashMap<Integer, ArrayList<AnimationEvent>>>();
	}
	
	/**
	 * Obtains the singleton instance of the AnimationEventManager.
	 * 
	 * @return the singleton instance of AnimationEventManager.
	 */
	public static AnimationEventManager getInstance() {
		if (instance == null) {
			instance = new AnimationEventManager();
		}
		return instance;
	}

	/**
	 * retrieves all events assigned to a BoneAnimation.
	 * @param key the bone animation object to retrieve events for.
	 * @return the event collection for a given BoneAnimation.
	 */
	public HashMap<Integer, ArrayList<AnimationEvent>> getAllEvents(
			BoneAnimation key) {
		return dataMap.get(key);
	}
	
	/**
	 * returns a list of frames that contain events for a given bone animation.
	 * @param key the bone animation to obtain the frames for.
	 * @return the list of frames that contain events.
	 */
	public Integer[] getFrames(BoneAnimation key) {
		HashMap<Integer, ArrayList<AnimationEvent>> userData = dataMap.get(key);
		if(userData != null) {
			return userData.keySet().toArray(new Integer[userData.keySet().size()]);
		}
		
		return null;
	}
	
	/**
	 * Obtains a list of events for a given animation at a specified frame. If
	 * no events are defined for the animation at the specified frame, null is
	 * returned. 
	 * @param key the animation to obtain the events for.
	 * @param frame the keyframe to obtain events for.
	 * @return the list of events for the provided animation at the provided
	 * 		frame, if no events are defined, null is returned.
	 */
	public ArrayList<AnimationEvent> getEvents(BoneAnimation key, int frame) {
		HashMap<Integer, ArrayList<AnimationEvent>> userData = dataMap.get(key);
		if(userData != null) {
			return userData.get(frame);
		}
		return null;
	}

	/**
	 * stores a map of animation events for a specified animation. 
	 * @param key the animation that will be linked to the events.
	 * @param data the events to store.
	 */
	public void setAnimationEventList(BoneAnimation key,
			HashMap<Integer, ArrayList<AnimationEvent>> data) {
		dataMap.put(key, data);
	}

	/**
	 * Adds a single animation event to the list of events for a specified
	 * animation at a given keyframe.
	 * @param ba the animation to link to this event.
	 * @param key the frame to use to trigger the event.
	 * @param data the event.
	 */
	public void addAnimationEvent(BoneAnimation ba, Integer key,
			AnimationEvent data) {
		HashMap<Integer, ArrayList<AnimationEvent>> userData = dataMap.get(ba);
		if (userData == null) {
			userData = new HashMap<Integer, ArrayList<AnimationEvent>>();
			dataMap.put(ba, userData);
		}

		ArrayList<AnimationEvent> list = userData.get(key);

		if (list == null) {
			list = new ArrayList<AnimationEvent>();
			userData.put(key, list);
		}

		list.add(data);
	}

	public ArrayList<AnimationEvent> getAnimationEventList(BoneAnimation ba,
			Integer key) {
		HashMap<Integer, ArrayList<AnimationEvent>> userData = dataMap.get(ba);
		if (userData == null) {
			return null;
		}

		return userData.get(key);
	}

	public ArrayList<AnimationEvent> removeAnimationEventList(BoneAnimation ba,
			Integer key) {
		HashMap<Integer, ArrayList<AnimationEvent>> userData = dataMap.get(ba);
		if (userData == null) {
			return null;
		}

		return userData.remove(key);
	}

	public boolean removeAnimationEvent(BoneAnimation ba, Integer key,
			AnimationEvent event) {
		HashMap<Integer, ArrayList<AnimationEvent>> userData = dataMap.get(ba);
		if (userData == null) {
			return false;
		}

		ArrayList<AnimationEvent> eventList = userData.remove(key);
		return eventList.remove(event);
	}

	public void bind(BoneAnimation key, BoneAnimation original) {
		HashMap<Integer, ArrayList<AnimationEvent>> userData = dataMap
				.get(original);
		if (userData != null) {
			dataMap.put(key, userData);
		}
	}
}
