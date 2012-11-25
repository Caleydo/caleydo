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
package org.caleydo.datadomain.pathway.graph;

import java.io.Serializable;

import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.base.IUniqueObject;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

/**
 * Overall graph that holds all pathways
 * 
 * @author Marc Streit
 */
public class PathwayGraph extends DirectedMultigraph<PathwayVertexRep, DefaultEdge>
		implements IUniqueObject, Serializable, Comparable<PathwayGraph> {

	private static final long serialVersionUID = 1L;

	private int id;

	private EPathwayDatabaseType type;

	private String name;

	private String title;

	private String imageLink;

	private String externalLink;

	private int width = -1;

	private int height = -1;

	public PathwayGraph(final EPathwayDatabaseType type, final String name,
			final String title, final String imageLink, final String link) {

		// super(GeneralManager.get().getIDCreator().createID(ManagedObjectType.PATHWAY));

		super(DefaultEdge.class);

		id = GeneralManager.get().getIDCreator().createID(ManagedObjectType.PATHWAY);

		this.type = type;
		this.name = name;
		this.title = title;
		this.imageLink = imageLink;
		this.externalLink = link;
	}

	@Override
	public int getID() {
		return id;
	}

	public final String getName() {
		return name;
	}

	public final String getTitle() {
		return title;
	}

	public final String getImageLink() {
		return imageLink;
	}

	public final String getExternalLink() {
		return externalLink;
	}

	public final EPathwayDatabaseType getType() {
		return type;
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public void setWidth(final int iWidth) {
		this.width = iWidth;
	}

	public void setHeight(final int iHeight) {
		this.height = iHeight;
	}

	@Override
	public String toString() {
		return type + ": " + getTitle();
	}

	/**
	 * Returns the internal unique-id as hashcode
	 * 
	 * @return internal unique-id as hashcode
	 */
	@Override
	public int hashCode() {
		return getID();
	}

	/**
	 * Checks if the given object is equals to this one by comparing the
	 * internal unique-id
	 * 
	 * @return <code>true</code> if the 2 objects are equal, <code>false</code>
	 *         otherwise
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof IUniqueObject) {
			return this.getID() == ((IUniqueObject) other).getID();
		} else {
			return false;
		}
	}

	@Override
	public int compareTo(PathwayGraph o) {
		return this.title.compareToIgnoreCase(o.getTitle());
	}

}