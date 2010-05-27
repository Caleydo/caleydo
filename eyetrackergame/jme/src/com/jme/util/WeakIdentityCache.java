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

package com.jme.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * This class is not unsimiliar to the Map interface. It provides methods for
 * fast storing and retrieving object with a key, based on hashes and key/value
 * pairs. However there are some important distinctions from a normal HashMap.
 * <br>
 * 
 * Keys are not compared using object-equality but using reference-equality.
 * This means you can only use the original object to retrieve any values from
 * this cache. It also means the equals() method of your key is never used. <br>
 * 
 * This allows system identy hashes to be used instead of normal hashes, which
 * means the potentially slow hashCode() method of your objects (eg. Buffer
 * objects) are never used.<br>
 * 
 * Finally, the key itself is stored through a WeakReference. Once the key
 * object becomes weakly referable, this reference will be added to the internal
 * ReferenceQueue of this cache. This queue is polled everytime when any of the
 * methods of this are invoked. After the reference is polled the key/value pair
 * is removed from the map, and both key and value can be collected. (In case of
 * the value, if no other references to it exist)
 * 
 * @see WeakIdentityCache#expunge()
 * 
 * <br>
 * 
 * This is an implementation from scratch, but some of the concepts came from
 * other implementations, most notably WeakIdenityHashMap from the jBoss
 * project.
 * 
 * NOTE: this implementation is not synchronized.
 * 
 * @author Tijl Houtbeckers
 * @author Joshua Slack - added generics support
 * @version $Id: WeakIdentityCache.java 4131 2009-03-19 20:15:28Z blaine.dev $
 */
public class WeakIdentityCache<K,V> {

	private Entry<K,V>[] entries;
	private int size;
	private int threshold;
	private final static float LOAD = 0.75f;

	private final ReferenceQueue<K> refqueue = new ReferenceQueue<K>();

	/**
	 * Create a new WeakIdenityCache (see main javadoc entry for this class)
	 */
	@SuppressWarnings("unchecked")
    public WeakIdentityCache() {
		threshold = 16;
		entries = new Entry[threshold];
	}

	private int hash(K x) {
		int hash = System.identityHashCode(x);
		return hash - (hash << 7);
	}

	private int index(int hash, int length) {
		return hash & (length - 1);
	}

	@SuppressWarnings("unchecked")
    private void resize(int newsize) {
		expunge();
		int oldsize = entries.length;

		if (size < threshold || oldsize > newsize)
			return;

		Entry<K,V>[] newentries = new Entry[newsize];

		transfer(entries, newentries);
		entries = newentries;

		if (size >= threshold / 2) {
			threshold = (int) (newsize * LOAD);
		} else {
			expunge();
			transfer(newentries, entries);
		}
	}

	private void transfer(Entry<K,V>[] src, Entry<K,V>[] dest) {
		for (int k = 0; k < src.length; ++k) {
			Entry<K,V> entry = src[k];
			src[k] = null;
			while (entry != null) {
				Entry<K,V> next = entry.nextEntry;
				if (entry.get() == null) {
					entry.nextEntry = null;
					entry.value = null;
					size--;
				} else {
					int i = index(entry.hash, dest.length);
					entry.nextEntry = dest[i];
					dest[i] = entry;
				}
				entry = next;
			}
		}
	}

	/**
	 * Returns value for this key.
	 */
	public V get(K key) {
		expunge();
		int hash = hash(key);
		int index = index(hash, entries.length);
		Entry<K,V> entry = entries[index];
		while (entry != null) {
			if (entry.hash == hash && key == entry.get())
				return entry.value;
			entry = entry.nextEntry;
		}
		return null;
	}

	/**
	 * Put a value in this cache with key. <br>
	 * Both key and value should not be null.
	 */
	public V put(K key, V value) {
		expunge();
		int hash = hash(key);
		int index = index(hash, entries.length);

		for (Entry<K,V> entry = entries[index]; entry != null; entry = entry.nextEntry) {
			if (hash == entry.hash && key == entry.get()) {
				V oldentry = entry.value;
				if (value != oldentry)
					entry.value = value;
				return oldentry;
			}
		}

		entries[index] = new Entry<K,V>(key, value, refqueue, hash, entries[index]);
		if (++size >= threshold)
			resize(entries.length * 2);
		return null;
	}

	/**
	 * Removes the value for this key.
	 */
	public V remove(K key) {
		expunge();
		int hash = hash(key);
		int index = index(hash, entries.length);
		Entry<K,V> temp = entries[index];
		Entry<K,V> previous = temp;

		while (temp != null) {
			Entry<K,V> next = temp.nextEntry;
			if (hash == temp.hash && key == temp.get()) {
				size--;
				if (previous == temp)
					entries[index] = next;
				else
					previous.nextEntry = next;
				return temp.value;
			}
			previous = temp;
			temp = next;
		}

		return null;
	}

	/**
	 * Clear the cache of all entries it has.
	 */
	public void clear() {
		while (refqueue.poll() != null)
			;

		for (int i = 0; i < entries.length; ++i)
			entries[i] = null;
		size = 0;

		while (refqueue.poll() != null)
			;
	}

	/**
	 * Removes all key/value pairs from keys who've become weakly reachable from
	 * this cache. This method is called from every other method in this class
	 * as well, but can be called seperatly to ensure all (in)direct weak
	 * reference are removed, when none of the other methods of this class are
	 * called frequently enough.<br>
	 * 
	 * Note that this method is relativly cheap (espc. if the queue is empty)
	 * but does most likely involve synchronization.
	 * 
	 * @see ReferenceQueue#poll()
	 */
	@SuppressWarnings("unchecked")
    public void expunge() {
        Entry<K,V> entry;
		while ((entry = (Entry<K,V>)refqueue.poll()) != null) {
			int index = index(entry.hash, entries.length);

			Entry<K,V> temp = entries[index];
			Entry<K,V> previous = temp;
			while (temp != null) {
				Entry<K,V> next = temp.nextEntry;
				if (temp == entry) {
					if (previous == entry) {
						entries[index] = next;
					} else {
						previous.nextEntry = next;
					}
					entry.nextEntry = null;
					entry.value = null;
					size--;
					break;
				}
				previous = temp;
				temp = next;
			}
		}
	}

	private static class Entry<K,V> extends WeakReference<K> {

		private Entry<K,V> nextEntry;
		private V value;
		private final int hash;

		Entry(K key, V value, ReferenceQueue<K> queue, int hash, Entry<K,V> next) {
			super(key, queue);
			this.value = value;
			this.hash = hash;
			this.nextEntry = next;
		}
	}
}