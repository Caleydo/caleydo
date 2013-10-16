/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.id;

import java.rmi.server.UID;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 *
 */
public final class IDCreator {
	/**
	 * salt to have a unique vm ids
	 */
	private static final int salt;

	/**
	 * class to atomic counter
	 */
	private static LoadingCache<String, AtomicInteger> vmuniqueIds = CacheBuilder.newBuilder().build(
			new CacheLoader<String, AtomicInteger>() {
				@Override
				public AtomicInteger load(String arg0) throws Exception {
					return new AtomicInteger();
				}
			});

	static {
		Random r = new SecureRandom();
		int n = r.nextInt(0x7FFF);
		salt = (n & 0x7FFF) << 16;
	}


	/**
	 * returns a global unique id even over multiple vm instances. However, only 16 Bits = 65536 Elements are possible
	 * per VM per type
	 *
	 * please use {@link #createPersistentID(Class)} if possible for a real unique id
	 *
	 * @param class1
	 * @return
	 */
	public static synchronized int createPersistentIntID(Class<?> owner) {
		int id = createVMUniqueID(owner);
		int mask = 0xFFFF;
		int masked = id & mask;
		while (masked != id) { // if we have too many use a bit of our noise
			mask = (mask << 1) | 1;
			masked = id & mask;
		}
		int total = salt | masked;
		return total;
	}

	/**
	 * unique id per VM, e.g. atomic counter
	 * @param owner
	 * @return
	 */
	public static int createVMUniqueID(Class<?> owner) {
		// count atomic integers per owner
		return vmuniqueIds.getUnchecked(owner.getCanonicalName()).incrementAndGet();
	}

	/**
	 * returns a unique id based on {@link UID}
	 *
	 * @param class1
	 * @return
	 */
	public static String createPersistentID(Class<?> owner) {
		return new UID().toString().replace(':', '-');
	}
}
