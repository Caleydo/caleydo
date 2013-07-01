/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.external;

import org.caleydo.core.util.color.Color;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;

public abstract class AExternalScoreParseSpecification extends MatrixDefinition implements Cloneable {
	private List<Integer> columns;

	private String rankingName;

	private ECombinedOperator operator;

	private boolean normalizeScores;

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
