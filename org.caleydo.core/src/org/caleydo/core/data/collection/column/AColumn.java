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
package org.caleydo.core.data.collection.column;

import java.util.EnumMap;
import java.util.Iterator;

import org.caleydo.core.data.collection.EDataTransformation;
import org.caleydo.core.data.collection.ICollection;
import org.caleydo.core.data.collection.column.container.FloatContainer;
import org.caleydo.core.data.collection.column.container.FloatContainerIterator;
import org.caleydo.core.data.collection.column.container.IContainer;
import org.caleydo.core.data.collection.column.container.IntContainer;
import org.caleydo.core.data.collection.column.container.IntContainerIterator;
import org.caleydo.core.data.collection.column.container.NumericalContainer;
import org.caleydo.core.util.base.AUniqueObject;

/**
 * Interface for Dimensions A Dimension is a container that holds various representations of a particular data
 * entity, for example a microarray experiment, or a column on illnesses in a clinical data file. It contains
 * all information considering one such entity, for example, the raw, normalized and logarithmized data as
 * well as metadata, such as the label of the experiment. Only the raw data and some metadata can be specified
 * manually, the rest is computed on on demand. One distinguishes between two basic dimension types: numerical
 * and nominal. This is reflected in the two sub-interfaces INumericalSet and INominalSet. After construction
 * one of the setRawData methods has to be called. Notice, that only one setRawData may be called exactly
 * once, since a set is designed to contain only one raw data set at a time.
 * 
 * @author Alexander Lex
 */

public abstract class AColumn
	extends AUniqueObject
	implements ICollection {

	protected EnumMap<DataRepresentation, IContainer> hashCContainers;

	protected String label;

	boolean isRawDataSet = false;

	RawDataType rawDataType = RawDataType.UNDEFINED;

	DataRepresentation dataRep;

	/**
	 * Constructor Initializes objects
	 */
	public AColumn(int uniqueID) {
		super(uniqueID);

		hashCContainers = new EnumMap<DataRepresentation, IContainer>(DataRepresentation.class);
		label = new String("Not specified");
	}

	/**
	 * Returns the data type of the raw data
	 * 
	 * @return a value of ERawDataType
	 */
	public RawDataType getRawDataType() {
		return rawDataType;
	}

	@Override
	public void setLabel(String sLabel) {
		this.label = sLabel;
	}

//	@Override
//	public String getLabel() {
//		return label;
//	}

	/**
	 * Set the raw data with data type float
	 * 
	 * @param rawData
	 *            a float array containing the raw data
	 */
	public void setRawData(float[] rawData) {

		if (isRawDataSet)
			throw new IllegalStateException("Raw data was already set in Dimension " + uniqueID
				+ " , tried to set again.");

		rawDataType = RawDataType.FLOAT;
		isRawDataSet = true;

		FloatContainer container = new FloatContainer(rawData);
		hashCContainers.put(DataRepresentation.RAW, container);
	}

	/**
	 * Set the raw data with data type int
	 * 
	 * @param fArRawData
	 *            a int array containing the raw data
	 */
	public void setRawData(int[] rawData) {

		if (isRawDataSet)
			throw new IllegalStateException("Raw data was already set, tried to set again.");

		rawDataType = RawDataType.INT;
		isRawDataSet = true;

		IntContainer container = new IntContainer(rawData);
		hashCContainers.put(DataRepresentation.RAW, container);
	}

	public void setUncertaintyData(float[] uncertaintyData) {
		if (hashCContainers.containsKey(DataRepresentation.UNCERTAINTY_RAW))
			throw new IllegalStateException("Certainty data was already set in Dimension " + uniqueID
				+ " , tried to set again.");

		FloatContainer container = new FloatContainer(uncertaintyData);
		hashCContainers.put(DataRepresentation.UNCERTAINTY_RAW, container);
	}

	public boolean containsDataRepresentation(DataRepresentation dataRepresentation) {
		return hashCContainers.containsKey(dataRepresentation);
	}

	/**
	 * Returns a float value from a dimension of which the kind has to be specified Use iterator when you want
	 * to iterate over the whole field, it has better performance
	 * 
	 * @param dimensionKind
	 *            Specify which kind of dimension (eg: raw, normalized)
	 * @param iIndex
	 *            The index of the requested Element
	 * @return The associated value
	 */
	public float getFloat(DataRepresentation dimensionKind, int iIndex) {
		if (!hashCContainers.containsKey(dimensionKind))
			throw new IllegalArgumentException("Requested dimension kind " + dimensionKind + " not produced");
		if (!(hashCContainers.get(dimensionKind) instanceof FloatContainer))
			throw new IllegalArgumentException("Requested dimension kind is not of type float");

		FloatContainer container = (FloatContainer) hashCContainers.get(dimensionKind);
		return container.get(iIndex);
	}

	/**
	 * Returns a iterator to the dimension of which the kind has to be specified Good performance
	 * 
	 * @param dimensionKind
	 * @return
	 */
	public FloatContainerIterator floatIterator(DataRepresentation dimensionKind) {

		if (!(hashCContainers.get(dimensionKind) instanceof FloatContainer))
			throw new IllegalArgumentException("Requested dimension kind is not of type float");

		FloatContainer container = (FloatContainer) hashCContainers.get(dimensionKind);
		return container.iterator();
	}

	/**
	 * Returns an int value from a dimension of which the kind has to be specified Use iterator when you want
	 * to iterate over the whole field, it has better performance
	 * 
	 * @param dimensionKind
	 *            Specify which kind of dimension (eg: raw, normalized, log)
	 * @param iIndex
	 *            The index of the requested Element
	 * @return The associated value
	 */
	public int getInt(DataRepresentation dimensionKind, int iIndex) {
		if (!(hashCContainers.get(dimensionKind) instanceof IntContainer))
			throw new IllegalArgumentException("Requested dimension kind is not of type int");

		IntContainer container = (IntContainer) hashCContainers.get(dimensionKind);
		return container.get(iIndex);
	}

	/**
	 * Returns a iterator to the dimension of which the kind has to be specified Good performance
	 * 
	 * @param dimensionKind
	 * @return
	 */
	public IntContainerIterator intIterator(DataRepresentation dimensionKind) {
		if (!(hashCContainers.get(dimensionKind) instanceof IntContainer))
			throw new IllegalArgumentException("Requested dimension kind is not of type int");

		IntContainer container = (IntContainer) hashCContainers.get(dimensionKind);
		return container.iterator();
	}

	/**
	 * Returns a value of the type Number, from the representation chosen in dimensionKind, at the index
	 * specified in iIndex
	 * 
	 * @dimensionKind specifies which kind of dimension (eg: raw, normalized)
	 * @iIndex the index of the element
	 * @return the Number
	 */
	public Number get(DataRepresentation dimensionKind, int iIndex) {
		if (!(hashCContainers.get(dimensionKind) instanceof NumericalContainer<?>))
			throw new IllegalArgumentException("Requested dimension kind is not a subtype of Number");

		NumericalContainer<?> container = (NumericalContainer<?>) hashCContainers.get(dimensionKind);
		return container.get(iIndex);
	}

	/**
	 * Returns an iterator on the representation chosen in dimensionKind
	 * 
	 * @param dimensionKind
	 *            specifies which kind of dimension (eg: raw, normalized)
	 * @return the iterator
	 */
	public Iterator<? extends Number> iterator(DataRepresentation dimensionKind) {
		if (!(hashCContainers.get(dimensionKind) instanceof NumericalContainer<?>))
			throw new IllegalArgumentException("Requested dimension kind is not a subtype of Number");

		NumericalContainer<?> container = (NumericalContainer<?>) hashCContainers.get(dimensionKind);
		return container.iterator();
	}

	/**
	 * Returns the number of raw data elements
	 * 
	 * @return the number of raw data elements
	 */
	public int size() {
		return hashCContainers.get(DataRepresentation.RAW).size();
	}

	@Override
	public String toString() {
		return "Dimension for " + getRawDataType() + ", size: " + size();
	}

	/**
	 * Brings any dataset into a format between 0 and 1. This is used for drawing. Works for nominal and
	 * numerical data. Operates with the raw data as basis by default, however when a logarithmized
	 * representation is in the dimension this is used (only applies to numerical data). For nominal data the
	 * first value is 0, the last value is 1
	 */
	public abstract void normalize();

	/**
	 * Switch the representation of the data. When this is called the data in normalized is replaced with data
	 * calculated from the mode specified.
	 * 
	 * @param dataRep
	 */
	public abstract void setExternalDataRepresentation(EDataTransformation externalDataRep);

}
