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
package org.caleydo.view.tourguide.data.load;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.view.tourguide.data.score.ECombinedOperator;

@XmlType
public class ScoreParseSpecification extends MatrixDefinition implements Cloneable {
	private List<Integer> columns;

	private String rankingName;

	private ECombinedOperator operator;

	private boolean normalizeScores;

	public ScoreParseSpecification() {
	}

	public ScoreParseSpecification(String dataSourcePath) {
		this.dataSourcePath = dataSourcePath;
	}

	public void setColumns(List<Integer> columns) {
		this.columns = columns;
	}

	/** Add a single column to the existing ones */
	public void addColum(Integer column) {
		if (columns == null) {
			columns = new ArrayList<Integer>();
		}
		columns.add(column);
	}

	/**
	 * @return the column, see {@link #column}
	 */
	public List<Integer> getColumns() {
		return columns;
	}

	/**
	 * @return the operator
	 */
	public ECombinedOperator getOperator() {
		return operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(ECombinedOperator operator) {
		this.operator = operator;
	}

	/**
	 * @param normalizeScores
	 *            the normalizeScores to set
	 */
	public void setNormalizeScores(boolean normalizeScores) {
		this.normalizeScores = normalizeScores;
	}

	/**
	 * @return the normalizeScores
	 */
	public boolean isNormalizeScores() {
		return normalizeScores;
	}

	/**
	 * @param rankingName
	 *            the rankingName to set
	 */
	public void setRankingName(String rankingName) {
		this.rankingName = rankingName;
	}

	/**
	 * @return the rankingName
	 */
	public String getRankingName() {
		return rankingName;
	}

	public boolean isRankParsing() {
		return columns == null || columns.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ScoreParseSpecification clone() {
		ScoreParseSpecification clone;
		try {
			clone = (ScoreParseSpecification) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("can't clone me", e);
		}
		if (this.columns != null)
			clone.columns = new ArrayList<>(this.columns);
		return clone;
	}
}
