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
package org.caleydo.view.tourguide.data.filter;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Pair;

/**
 * @author Samuel Gratzl
 *
 */
public class CompareDomainFilter implements IDataDomainFilter {
	private final boolean againstStratification;
	private String operand;
	private EStringCompareOperator op;

	public CompareDomainFilter(EStringCompareOperator op, String operand, boolean againstStratification) {
		this.operand = operand;
		this.op = op;
		this.againstStratification = againstStratification;
	}

	/**
	 * @return the againstStratification, see {@link #againstStratification}
	 */
	public boolean isAgainstStratification() {
		return againstStratification;
	}

	/**
	 * @return the op, see {@link #op}
	 */
	public EStringCompareOperator getOp() {
		return op;
	}

	/**
	 * @param op
	 *            the op to set
	 */
	public void setOp(EStringCompareOperator op) {
		this.op = op;
	}

	/**
	 * @param operand
	 *            the operand to set
	 */
	public void setOperand(String operand) {
		this.operand = operand;
	}

	/**
	 * @return the operand, see {@link #operand}
	 */
	public String getOperand() {
		return operand;
	}

	@Override
	public boolean apply(Pair<TablePerspective, Group> pair) {
		if (againstStratification) {
			return op.apply(pair.getFirst().getRecordPerspective().getLabel(), operand);
		} else {
			Group group = pair.getSecond();
			return group == null || op.apply(group.getLabel(), operand);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (againstStratification ? 1231 : 1237);
		result = prime * result + ((op == null) ? 0 : op.hashCode());
		result = prime * result + ((operand == null) ? 0 : operand.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompareDomainFilter other = (CompareDomainFilter) obj;
		if (againstStratification != other.againstStratification)
			return false;
		if (op != other.op)
			return false;
		if (operand == null) {
			if (other.operand != null)
				return false;
		} else if (!operand.equals(other.operand))
			return false;
		return true;
	}


}
