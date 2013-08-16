/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.function;

/**
 * Wrapper of {@link FloatStatistics} that makes iterative adding possible.
 * 
 * @author Alexander Lex
 *
 */
public class IncrementalFloatStatistics extends FloatStatistics {
	@Override
	public FloatStatistics add(float x) {
		return super.add(x);
	}

}
