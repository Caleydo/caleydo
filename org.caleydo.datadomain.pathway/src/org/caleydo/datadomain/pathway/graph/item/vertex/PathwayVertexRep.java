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
package org.caleydo.datadomain.pathway.graph.item.vertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.id.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * Pathway vertex representation stored in the overall pathway graph.
 * 
 * @author Marc Streit
 */
public class PathwayVertexRep
	implements Serializable, IUniqueObject {

	private static final long serialVersionUID = 1L;

	private int id;

	private String name;

	private EPathwayVertexShape shape;

	private short[][] coords;

	private short width = 20;

	private short height = 20;

	private List<PathwayVertex> pathwayVertices = new ArrayList<PathwayVertex>();

	private List<PathwayGraph> pathways = new ArrayList<PathwayGraph>();

	/**
	 * Constructor.
	 * 
	 * @param sName
	 * @param sShapeType
	 * @param sCoords
	 */
	public PathwayVertexRep(final String sName, final String sShapeType,
			final String sCoords) {

		id = GeneralManager.get().getIDCreator()
				.createID(ManagedObjectType.PATHWAY_VERTEX_REP);

		shape = EPathwayVertexShape.valueOf(sShapeType);
		this.name = sName;

		setCoordsByCommaSeparatedString(sCoords);
	}

	/**
	 * Constructor.
	 * 
	 * @param sName
	 * @param sShapeType
	 * @param shX
	 * @param shY
	 * @param shWidth
	 * @param shHeight
	 */
	public PathwayVertexRep(final String sName, final String sShapeType,
			final short shX, final short shY, final short shWidth, final short shHeight) {

		if (sShapeType == null || sShapeType.isEmpty())
			shape = EPathwayVertexShape.rect;
		else
			shape = EPathwayVertexShape.valueOf(sShapeType);

		this.name = sName;
		this.width = shWidth;
		this.height = shHeight;

		setRectangularCoords(shX, shY, shWidth, shHeight);
	}

	/**
	 * Example: 213,521,202,515,248,440,261,447,213,521 Currently used for
	 * BioCarta input.
	 */
	private void setCoordsByCommaSeparatedString(final String sCoords) {

		StringTokenizer sToken = new StringTokenizer(sCoords, ",");

		coords = new short[sToken.countTokens() / 2][2];

		int iCount = 0;

		while (sToken.hasMoreTokens()) {
			// Filter white spaces
			short shXCoord = Short.valueOf(sToken.nextToken().replace(" ", "")).shortValue();

			if (!sToken.hasMoreTokens())
				return;

			short shYCoord = Short.valueOf(sToken.nextToken().replace(" ", "")).shortValue();

			coords[iCount][0] = shXCoord;
			coords[iCount][1] = shYCoord;

			iCount++;
		}
	}

	private void setRectangularCoords(final short shX, final short shY, final short shWidth,
			final short shHeight) {

		coords = new short[4][2];

		coords[0][0] = shX;
		coords[0][1] = shY;

		coords[1][0] = (short) (shX + shWidth);
		coords[1][1] = shY;

		coords[2][0] = (short) (shX + shWidth);
		coords[2][1] = (short) (shY + shHeight);

		coords[3][0] = shX;
		coords[3][1] = (short) (shY + shHeight);
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public int getID() {
		return id;
	}

	public String getName() {

		return name;
	}

	public EPathwayVertexShape getShapeType() {

		return shape;
	}

	public short[][] getCoords() {

		return coords;
	}

	public short getXOrigin() {

		return coords[0][0];
	}

	public short getYOrigin() {

		return coords[0][1];
	}

	public short getWidth() {

		return width;
	}

	public short getHeight() {

		return height;
	}

	@Override
	public String toString() {

		return name;
	}

	public void addPathwayVertex(PathwayVertex vertex) {
		pathwayVertices.add(vertex);
	}

	public List<PathwayVertex> getPathwayVertices() {
		return pathwayVertices;
	}

	public void addPathway(PathwayGraph pathway) {
		pathways.add(pathway);
	}

	public List<PathwayGraph> getPathways() {
		return pathways;
	}

	public EPathwayVertexType getType() {

		// We assume that all vertices that are associated to that vertex rep
		// are of the same type
		return pathwayVertices.get(0).getType();
	}
}
