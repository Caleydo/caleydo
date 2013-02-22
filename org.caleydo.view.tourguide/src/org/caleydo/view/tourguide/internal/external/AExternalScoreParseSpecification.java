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
package org.caleydo.view.tourguide.internal.external;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;

public abstract class AExternalScoreParseSpecification extends MatrixDefinition implements Cloneable {
	private List<Integer> columns;

	private String rankingName;

	private ECombinedOperator operator;

	private boolean normalizeScores;

	// FIXME set
	private Color color;

	private float mappingMin;

	private float mappingMax;

	public AExternalScoreParseSpecification() {
	}

	public AExternalScoreParseSpecification(String dataSourcePath) {
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

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the mappingMin, see {@link #mappingMin}
	 */
	public float getMappingMin() {
		return mappingMin;
	}

	/**
	 * @param mappingMin
	 *            setter, see {@link mappingMin}
	 */
	public void setMappingMin(float mappingMin) {
		this.mappingMin = mappingMin;
	}

	/**
	 * @return the mappingMax, see {@link #mappingMax}
	 */
	public float getMappingMax() {
		return mappingMax;
	}

	/**
	 * @param mappingMax
	 *            setter, see {@link mappingMax}
	 */
	public void setMappingMax(float mappingMax) {
		this.mappingMax = mappingMax;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#clone()
	 */
	@Override
	public AExternalScoreParseSpecification clone() {
		AExternalScoreParseSpecification clone;
		try {
			clone = (AExternalScoreParseSpecification) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("can't clone me", e);
		}
		if (this.columns != null)
			clone.columns = new ArrayList<>(this.columns);
		return clone;
	}
}
