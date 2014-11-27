/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.pathway.graph.item.vertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.Average;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDCreator;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.base.IUniqueObject;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;

/**
 * <p>
 * A <code>PathwayVertexRep</code> is a visible representation of a node (a {@link PathwayVertex}) in a pathway texture.
 * It may contain 1-n {@link PathwayVertex} objects.
 * </p>
 * <p>
 * The representation contains information on the type of shape, the position and the size of the representation.
 * </p>
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class PathwayVertexRep implements Serializable, IUniqueObject, ILabeled {

	private static final long serialVersionUID = 1L;

	/** A unique id of the vertex rep */
	private int id;

	private final String name;
	/**
	 * Name that only consists of the first gene name of {@link #name}.
	 */
	private final String shortName;

	/** The type of shape that this vertex rep uses */
	private EPathwayVertexShape shape;

	protected ArrayList<Pair<Short, Short>> coords;

	/** The id type of the vertex rep used in the id mapping manager */
	private static IDType idType = IDType.getIDType(EGeneIDTypes.PATHWAY_VERTEX_REP.name());

	/**
	 * The {@link PathwayVertex} objects that map to this representation there might be several
	 */
	private List<PathwayVertex> pathwayVertices = new ArrayList<PathwayVertex>();

	private PathwayGraph pathway;

	/**
	 * Parent of this vertex rep. May be null, if there is no parent.
	 */
	private PathwayVertexGroupRep parent;

	/**
	 * Constructor.
	 *
	 * @param name
	 * @param shapeType
	 * @param coords
	 */
	public PathwayVertexRep(final String name, final String shapeType, final String coords) {

		id = IDCreator.createVMUniqueID(PathwayVertexRep.class);

		shape = EPathwayVertexShape.valueOf(shapeType);
		this.name = name;
		this.shortName = extractShortName(name);
		setCoordsByCommaSeparatedString(coords);
	}

	/**
	 * Constructor.
	 *
	 * @param name
	 * @param shapeType
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public PathwayVertexRep(final String name, final String shapeType, final short x, final short y, final short width,
			final short height) {

		id = IDCreator.createVMUniqueID(PathwayVertexRep.class);

		if (shapeType == null || shapeType.isEmpty())
			shape = EPathwayVertexShape.rectangle;
		else
			shape = EPathwayVertexShape.valueOf(shapeType);

		this.name = name;
		this.shortName = extractShortName(name);
		setRectangularCoords(x, y, width, height);
	}

	private String extractShortName(String name) {
		int commaIndex = name.indexOf(',');
		String shortName;
		if (commaIndex > 0) {
			shortName = name.substring(0, commaIndex);
		} else {
			shortName = name;
		}
		return shortName;
	}

	/**
	 * Example: 213,521,202,515,248,440,261,447,213,521 Currently used for BioCarta input.
	 */
	private void setCoordsByCommaSeparatedString(final String sCoords) {

		String[] stringCoordinates = sCoords.split(",");
		coords = new ArrayList<Pair<Short, Short>>();

		for (int coordinateCount = 0; coordinateCount < stringCoordinates.length;) {
			// Filter white spaces
			String xString = stringCoordinates[coordinateCount++].replace(" ", "");
			Short xCoord = Short.valueOf(xString);

			if (coordinateCount >= stringCoordinates.length)
				return;

			String yString = stringCoordinates[coordinateCount++].replace(" ", "");
			Short yCoord = Short.valueOf(yString);

			Pair<Short, Short> coordinates = new Pair<Short, Short>(xCoord, yCoord);
			coords.add(coordinates);
		}
	}

	protected void setRectangularCoords(short x, short y, short width, short height) {

		coords = new ArrayList<Pair<Short, Short>>(4);

		coords.add(new Pair<Short, Short>((short) (x - width / 2), (short) (y - height / 2)));
		coords.add(new Pair<Short, Short>((short) (x + width / 2), (short) (y - height / 2)));
		coords.add(new Pair<Short, Short>((short) (x + width / 2), (short) (y + height / 2)));
		coords.add(new Pair<Short, Short>((short) (x - width / 2), (short) (y + height / 2)));
	}

	/**
	 * @return the id, see {@link #id}
	 */
	@Override
	public int getID() {
		return id;
	}

	public String getName() {

		return name;
	}

	/**
	 * @return the shortName, see {@link #shortName}
	 */
	public String getShortName() {
		return shortName;
	}

	public EPathwayVertexShape getShapeType() {

		return shape;
	}

	public ArrayList<Pair<Short, Short>> getCoords() {

		return coords;
	}

	public short getCenterX() {

		return coords.get(0).getFirst();
	}

	public short getCenterY() {

		return coords.get(0).getSecond();
	}

	public short getLowerLeftCornerX() {
		return (short) (coords.get(0).getFirst() + ((coords.get(1).getFirst() - coords.get(0).getFirst()) / 2));
	}

	public short getLowerLeftCornerY() {
		return (short) (coords.get(1).getSecond() + ((coords.get(2).getSecond() - coords.get(1).getSecond()) / 2));
	}

	public short getWidth() {

		return (short) (coords.get(1).getFirst() - coords.get(0).getFirst());
	}

	public short getHeight() {

		return (short) (coords.get(2).getSecond() - coords.get(1).getSecond());
	}
	@Override
	public String toString() {

		return name;
	}

	/** Adds a vertex to {@link #pathwayVertices} */
	public void addPathwayVertex(PathwayVertex vertex) {
		pathwayVertices.add(vertex);
	}

	/**
	 * @return The pathwayVertices, see {@link #pathwayVertices}, or an empty list if no vertices can be resolved.
	 */
	public List<PathwayVertex> getPathwayVertices() {
		return pathwayVertices;
	}

	/**
	 * @param pathway
	 *            setter, see {@link #pathway}
	 */
	public void setPathway(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	/**
	 * @return the pathway, see {@link #pathway}
	 */
	public PathwayGraph getPathway() {
		return pathway;
	}

	/**
	 * Returns the type of the pathway vertex underneath, assuming that alle vertex reps are of the same type
	 */
	public EPathwayVertexType getType() {
		return pathwayVertices.get(0).getType();
	}

	/**
	 * Returns all david IDs of all vertices stored in this <code>PathwayVertexRep</code>, or an empty list if no IDs
	 * can be mapped.
	 *
	 * @see PathwayItemManager#getDavidIDsByPathwayVertexRep(PathwayVertexRep)
	 */
	public ArrayList<Integer> getDavidIDs() {
		return PathwayItemManager.get().getDavidIDsByPathwayVertexRep(this);
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public static IDType getIdType() {
		return idType;
	}

	/**
	 * Calculates the {@link Average} of all mapped genes of this vertexRep for the specified table perspective. Hereby
	 * arithmetic mean and standard deviation are averages of all mappings.
	 *
	 * @param tablePerspectives
	 * @return
	 */
	public Average calcAverage(TablePerspective tablePerspective) {
		List<Integer> davidIDs1 = getDavidIDs();
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				IDCategory.getIDCategory("GENE"));
		float sumStandardDeviation = 0;
		float sumAverage = 0;
		int numIDs = 0;

		Perspective genePerspective = tablePerspective.getPerspective(IDCategory.getIDCategory("GENE"));
		Perspective samplePerspective = null;
		if (tablePerspective.hasDimensionPerspective(genePerspective.getPerspectiveID())) {
			samplePerspective = tablePerspective.getRecordPerspective();
		} else {
			samplePerspective = tablePerspective.getDimensionPerspective();
		}
		for (Integer davidID : davidIDs1) {
			Set<Integer> ids = mappingManager.getIDAsSet(IDType.getIDType("DAVID"), genePerspective.getIdType(),
					davidID);
			if (ids != null) {
				for (Integer id : ids) {
					Average average = TablePerspectiveStatistics.calculateAverage(samplePerspective.getVirtualArray(),
							tablePerspective.getDataDomain(), genePerspective.getIdType(), id);
					sumStandardDeviation += average.getStandardDeviation();
					sumAverage += average.getArithmeticMean();
					numIDs++;
				}
			}
		}

		Average average = new Average();
		average.setArithmeticMean(sumAverage / numIDs);
		average.setStandardDeviation(sumStandardDeviation / numIDs);
		return average;
	}

	@Override
	public String getLabel() {
		if (getType() == EPathwayVertexType.gene) {
			StringBuilder builder = new StringBuilder();
			List<PathwayVertex> vertices = getPathwayVertices();
			List<String> names = new ArrayList<>(vertices.size());
			for (PathwayVertex v : vertices) {
				names.add(v.getHumanReadableName());
			}
			Collections.sort(names);
			for (int i = 0; i < names.size(); i++) {
				builder.append(names.get(i));
				if (i < names.size() - 1)
					builder.append(", ");
			}
			return builder.toString();
		}
		return getShortName();
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	public PathwayVertexGroupRep getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            setter, see {@link parent}
	 */
	void setParent(PathwayVertexGroupRep parent) {
		this.parent = parent;
	}
}
