/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
		return MappingFunctions.clamp01(in);
	}

	@Override
	public IMappingFunction clone() {
		return new SimpleMapping(this);
	}

}
