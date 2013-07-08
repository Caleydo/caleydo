/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
