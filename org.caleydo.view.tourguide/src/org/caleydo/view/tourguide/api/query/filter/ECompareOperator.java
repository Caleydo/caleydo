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
package org.caleydo.view.tourguide.api.query.filter;

import org.caleydo.core.util.base.ILabelProvider;

/**
 * @author Samuel Gratzl
 *
 */
public enum ECompareOperator implements ILabelProvider {
	IS_NOT_NA, GE, GT, LE, LT;

	public boolean apply(float a, float b) {
		switch(this) {
		case IS_NOT_NA:
			return !Float.isNaN(a);
		case GE:
			return !Float.isNaN(a) && a >= b;
		case GT:
			return !Float.isNaN(a) && a > b;
		case LE:
			return !Float.isNaN(a) && a <= b;
		case LT:
			return !Float.isNaN(a) && a < b;
		}
		throw new IllegalStateException("unknown compare operator " + this);
	}

	@Override
	public String getLabel() {
		switch (this) {
		case IS_NOT_NA:
			return "is not NA";
		case GE:
			return "Greater Equal (>=)";
		case GT:
			return "Greater Than (>)";
		case LE:
			return "Less Equal (<=)";
		case LT:
			return "Less Than (<)";
		}
		throw new IllegalStateException("unknown compare operator " + this);
	}

	@Override
	public String getProviderName() {
		return null;
	}
}
