/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

/**
 * @author Samuel Gratzl
 *
 */
public class SimpleMapping extends AMappingFunction implements Cloneable {

	public SimpleMapping(SimpleMapping copy) {
		super(copy);
	}

	public SimpleMapping(double fromMin, double fromMax) {
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
	public double[] getMappedMin() {
		return new double[] { 0, 0 };
	}

	@Override
	public double[] getMappedMax() {
		return new double[] { 1, 1 };
	}

	@Override
	public double getMaxTo() {
		return 1;
	}

	@Override
	public double getMinTo() {
		return 0;
	}

	@Override
	public boolean isMappingDefault() {
		return false;
	}

	@Override
	public double apply(double in) {
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
