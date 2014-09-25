/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

import com.google.common.base.Function;
import com.google.common.collect.Table;

/**
 * utility class for {@link Function2}
 *
 * @author Samuel Gratzl
 *
 */
public class Functions2 {
	/**
	 * create a lookup function based on a table
	 *
	 * @param table
	 * @return
	 */
	public static <F1, F2, T> Function2<F1, F2, T> fromTable(final Table<F1, F2, T> table) {
		return new Function2<F1, F2, T>() {
			@Override
			public T apply(F1 input1, F2 input2) {
				return table.get(input1, input2);
			}
		};
	}

	/**
	 * wrapper for swapping arguments
	 *
	 * @param f
	 * @return
	 */
	public static <F1, F2, T> Function2<F2, F1, T> swap(final Function2<F1, F2, T> f) {
		return new Function2<F2, F1, T>() {
			@Override
			public T apply(F2 input1, F1 input2) {
				return f.apply(input2, input1);
			}
		};
	}

	public static <F1, F2, T> Function<F1, T> partial(final Function2<F1, F2, T> f, final F2 value) {
		return new Function<F1, T>() {
			@Override
			public T apply(F1 input) {
				return f.apply(input, value);
			}
		};
	}

	public static <F1, F2, T> Function<F2, T> partial2(final Function2<F1, F2, T> f, final F1 value) {
		return new Function<F2, T>() {
			@Override
			public T apply(F2 input) {
				return f.apply(value, input);
			}
		};
	}
}
