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

import java.util.regex.Pattern;

import org.caleydo.core.util.base.ILabelProvider;

/**
 * @author Samuel Gratzl
 *
 */
public enum EStringCompareOperator implements ILabelProvider {
	CONTAINS, NOT_CONTAINS, MATCHES, NOT_MATCHES, EQUAL, NOT_EQUAL, EQUAL_IGNORECASE, NOT_EQUAL_IGNORECASE;

	public boolean apply(String a, String operand) {
		if (a == null)
			return false;
		switch (this) {
		case CONTAINS:
			return a.contains(operand);
		case NOT_CONTAINS:
			return !a.contains(operand);
		case EQUAL:
			return a.equals(operand);
		case NOT_EQUAL:
			return !a.equals(operand);
		case EQUAL_IGNORECASE:
			return a.equalsIgnoreCase(operand);
		case NOT_EQUAL_IGNORECASE:
			return !a.equalsIgnoreCase(operand);
		case MATCHES:
			return Pattern.matches(operand, a);
		case NOT_MATCHES:
			return !Pattern.matches(operand, a);
		default:
			throw new IllegalStateException("invalid selection: " + this);
		}
	}

	@Override
	public String getLabel() {
		switch (this) {
		case CONTAINS:
			return "contains";
		case NOT_CONTAINS:
			return "doesn't contain";
		case EQUAL:
			return "is";
		case NOT_EQUAL:
			return "is not";
		case EQUAL_IGNORECASE:
			return "is ignoring case";
		case NOT_EQUAL_IGNORECASE:
			return "is not ignoring case";
		case MATCHES:
			return "matches regex";
		case NOT_MATCHES:
			return "doesn't match regex";
		}
		throw new IllegalStateException("unknown compare operator " + this);
	}

	@Override
	public String getProviderName() {
		return null;
	}
}
