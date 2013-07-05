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
package org.caleydo.view.subgraph;

/**
 * All embedding ids that can be referred to by views that want to be plugged into {@link GLSubGraph}.
 *
 * @author Christian Partl
 *
 */
public enum EEmbeddingID {

	/**
	 * Embedding id for views that shall be used as one pathway multiform instance for level 1 (highest detail).
	 */
	PATHWAY_LEVEL1("org.caleydo.view.subgraph.pathway.multiform.level1", 4),
	/**
	 * Embedding id for views that shall be used as one pathway multiform instance for level 2 (medium detail).
	 */
	PATHWAY_LEVEL2("org.caleydo.view.subgraph.pathway.multiform.level2", 3),
	/**
	 * Embedding id for views that shall be used as one pathway multiform instance for level 3 (low detail).
	 */
	PATHWAY_LEVEL3("org.caleydo.view.subgraph.pathway.multiform.level3", 2),
	/**
	 * Embedding id for views that shall be used as one pathway multiform instance for level 3 (low detail).
	 */
	PATHWAY_LEVEL4("org.caleydo.view.subgraph.pathway.multiform.level4", 1),

	/**
	 * Embedding id for views that shall represent the extracted path, possibly over multiple pathways, in level 1
	 * (highest detail).
	 */
	PATH_LEVEL1("org.caleydo.view.subgraph.path.level1", 4),
	/**
	 * Embedding id for views that shall represent the extracted path, possibly over multiple pathways, in level 2
	 * (medium detail).
	 */
	PATH_LEVEL2("org.caleydo.view.subgraph.path.level2", 3);

	/**
	 * ID of the embedding that is associated with a certain pathway multiform level.
	 */

	/**
	 * ID of the embedding that is associated with a certain pathway multiform level.
	 */
	private final String embeddingID;

	private final int renderPriority;

	private EEmbeddingID(String id, int renderPriority) {
		this.embeddingID = id;
		this.renderPriority = renderPriority;
	}

	/**
	 * @return see {@link #embeddingID}
	 */
	public String id() {
		return embeddingID;
	}

	public int renderPriority() {
		return renderPriority;
	}

	public static EEmbeddingID levelUp(EEmbeddingID level) {
		switch (level) {
		case PATHWAY_LEVEL1:
			return PATHWAY_LEVEL1;
		case PATHWAY_LEVEL2:
			return PATHWAY_LEVEL1;
		case PATHWAY_LEVEL3:
			return PATHWAY_LEVEL2;
		case PATHWAY_LEVEL4:
			return PATHWAY_LEVEL3;
		case PATH_LEVEL1:
			return PATH_LEVEL1;
		case PATH_LEVEL2:
			return PATH_LEVEL1;
		}
		return null;
	}

	public static EEmbeddingID levelDown(EEmbeddingID level) {
		switch (level) {
		case PATHWAY_LEVEL1:
			return PATHWAY_LEVEL2;
		case PATHWAY_LEVEL2:
			return PATHWAY_LEVEL3;
		case PATHWAY_LEVEL3:
			return PATHWAY_LEVEL4;
		case PATHWAY_LEVEL4:
			return PATHWAY_LEVEL4;
		case PATH_LEVEL1:
			return PATH_LEVEL2;
		case PATH_LEVEL2:
			return PATH_LEVEL2;
		}
		return null;
	}
}
