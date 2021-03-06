/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.api.external;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.io.MatrixDefinition;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.tourguide.api.score.ECombinedOperator;
import org.caleydo.vis.lineup.model.mapping.EStandardMappings;

public abstract class AExternalScoreParseSpecification extends MatrixDefinition implements Cloneable {
	private List<Integer> columns;

	private String rankingName;

	private ECombinedOperator operator;

	private boolean normalizeScores;

	private Color color;

	private double mappingMin;

	private double mappingMax;

	private EStandardMappings mapping = EStandardMappings.LINEAR;

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
	public double getMappingMin() {
		return mappingMin;
	}

	/**
	 * @param mappingMin
	 *            setter, see {@link mappingMin}
	 */
	public void setMappingMin(double mappingMin) {
		this.mappingMin = mappingMin;
	}

	/**
	 * @return the mapping, see {@link #mapping}
	 */
	public EStandardMappings getMapping() {
		return mapping;
	}

	/**
	 * @param mapping
	 *            setter, see {@link mapping}
	 */
	public void setMapping(EStandardMappings mapping) {
		this.mapping = mapping;
	}

	/**
	 * @return the mappingMax, see {@link #mappingMax}
	 */
	public double getMappingMax() {
		return mappingMax;
	}

	/**
	 * @param mappingMax
	 *            setter, see {@link mappingMax}
	 */
	public void setMappingMax(double mappingMax) {
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
