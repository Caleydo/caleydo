/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mapping;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleMapping extends AMappingFunction implements Cloneable {

	public SimpleMapping(SimpleMapping copy) {
		super(copy);
	}

	public SimpleMapping(float fromMin, float fromMax) {
		super(fromMin, fromMax);
	}

	@Override
	public String toJavaScript() {
		return "clamp01(value)";
	}

	@Override
	public void fromJavaScript(String code) {

	}

	@Override
	public void reset() {

	}

	@Override
	public float[] getMappedMin() {
		return new float[] { 0, 0 };
	}

	@Override
	public float[] getMappedMax() {
		return new float[] { 1, 1 };
	}

	@Override
	public float getMaxTo() {
		return 1;
	}

	@Override
	public float getMinTo() {
		return 0;
	}

	@Override
	public boolean isMappingDefault() {
		return false;
	}

	@Override
	public float apply(float in) {
		return JavaScriptFunctions.clamp01(in);
	}

	@Override
	public IMappingFunction clone() {
		return new SimpleMapping(this);
	}

	@Override
	public boolean isComplexMapping() {
		return false;
	}

}
