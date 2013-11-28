/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.view.enroute;

/**
 * Picking types for the linearized pathway view.
 *
 * @author Christian
 *
 */
public enum EPickingType {
	BRANCH_SUMMARY_NODE_COLLAPSE_BUTTON,
	BRANCH_SUMMARY_NODE,
	GENE_NODE,
	COMPOUND_NODE,
	LINEARIZABLE_NODE,
	REMOVABLE_NODE,
	REMOVE_NODE_BUTTON,
	BACKGROUND,

	/** Picking type for samples */
	SAMPLE,
	/** Picking type for sample groups */
	SAMPLE_GROUP,
	/** The view mode for the sample group */
	SAMPLE_GROUP_VIEW_MODE,
	/** Picking type for abstract bars */
	SAMPLE_GROUP_RENDERER,
	/** A bar in a histogram which can be resolved to samples */
	HISTOGRAM_BAR,
	CENTER_LINE_ALIGNMENT_BUTTON,
	SUMMARY_STATISTICS,
	FIT_TO_VIEW_WIDTH_BUTTON
}
