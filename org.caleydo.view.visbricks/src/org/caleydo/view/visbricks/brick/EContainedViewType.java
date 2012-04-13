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
package org.caleydo.view.visbricks.brick;

/**
 * View Types for views contained in a brick.
 * 
 * @author Christian Partl
 * @author Alexander Lex
 * @author Marc Streit
 * 
 */
public enum EContainedViewType
{
	DUMMY_VIEW(false),

	CATEGORY(true),

	KAPLAN_MEIER_VIEW(false),
	KAPLAN_MEIER_VIEW_COMPACT(false),
	KAPLAN_MEIER_SUMMARY(false),
	KAPLAN_MEIER_SUMMARY_COMPACT(false),

	// Numerical Views
	HEATMAP_VIEW(true),
	PARCOORDS_VIEW(false),
	HISTOGRAM_VIEW(false),
	OVERVIEW_HEATMAP(false),
	OVERVIEW_HEATMAP_COMPACT(false),

	// text view
	TAGCLOUD_VIEW(false),

	// pathways
	PATHWAY_VIEW(false),
	PATHWAY_VIEW_COMPACT(false),
	PATHWAYS_SUMMARY(false),
	PATHWAYS_SUMMARY_COMPACT(false);

	private boolean useProportionalHeight;

	/**
	 * 
	 */
	private EContainedViewType(boolean useProportionalHeight)
	{
		this.useProportionalHeight = useProportionalHeight;
	}

	/**
	 * @return the useProportionalHeight, see {@link #useProportionalHeight}
	 */
	public boolean isUseProportionalHeight()
	{
		return useProportionalHeight;
	}
}
