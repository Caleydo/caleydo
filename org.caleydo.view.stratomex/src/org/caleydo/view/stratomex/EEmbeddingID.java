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
package org.caleydo.view.stratomex;

/**
 * All embedding ids that can be referred to by views that want to be plugged into StratomeX.
 *
 * @author Christian Partl
 *
 */
public enum EEmbeddingID {

	/**
	 * Embedding ID for views that represent numerical data in segment bricks.
	 */
	NUMERICAL_SEGMENT_BRICK("org.caleydo.view.stratomex.numerical.segment.brick"),
	/**
	 * Embedding ID for views that represent numerical data in header bricks.
	 */
	NUMERICAL_HEADER_BRICK("org.caleydo.view.stratomex.numerical.header.brick"),
	/**
	 * Embedding ID for views that represent categorical data in segment bricks.
	 */
	CATEGORICAL_SEGMENT_BRICK("org.caleydo.view.stratomex.categorical.segment.brick"),
	/**
	 * Embedding ID for views that represent categorical data in header bricks.
	 */
	CATEGORICAL_HEADER_BRICK("org.caleydo.view.stratomex.categorical.header.brick"),
	/**
	 * Embedding ID for views that represent pathway data in segment bricks.
	 */
	PATHWAY_SEGMENT_BRICK("org.caleydo.view.stratomex.pathway.segment.brick"),
	/**
	 * Embedding ID for views that represent pathway data in header bricks.
	 */
	PATHWAY_HEADER_BRICK("org.caleydo.view.stratomex.pathway.header.brick"),
	/**
	 * Embedding ID for views that represent pathway data in segment bricks.
	 */
	CLINICAL_SEGMENT_BRICK("org.caleydo.view.stratomex.clinical.segment.brick"),
	/**
	 * Embedding ID for views that represent pathway data in header bricks.
	 */
	CLINICAL_HEADER_BRICK("org.caleydo.view.stratomex.clinical.header.brick");


	/**
	 * Actual ID of embedding.
	 */
	private final String id;

	private EEmbeddingID(String id) {
		this.id = id;
	}

	/**
	 * @return see {@link #id}
	 */
	public String id() {
		return id;
	}
}
