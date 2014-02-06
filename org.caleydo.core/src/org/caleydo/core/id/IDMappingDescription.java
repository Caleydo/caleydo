/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.id;

import javax.xml.bind.annotation.XmlRootElement;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.io.parser.ascii.IDMappingParser;

/**
 * Describes the mapping to be created from one {@link IDType} to another.
 *
 * @author Christian
 *
 */
@XmlRootElement
public class IDMappingDescription {

	protected String fileName;
	protected int parsingStartLine;
	protected int parsingStopLine;
	protected String delimiter;

	protected String idCategory;

	protected String fromIDType;
	protected EDataType fromDataType;
	protected String toIDType;
	protected EDataType toDataType;

	protected String codeResolvedFromIDType;
	protected EDataType codeResolvedFromDataType;
	protected String codeResolvedToIDType;
	protected EDataType codeResolvedToDataType;

	protected boolean isMultiMapping;
	protected boolean createReverseMapping;
	protected boolean resolveCodeMappingUsingCodeToId_LUT;

	public void addMapping() {
		IDCategory category = IDCategory.registerCategoryIfAbsent(idCategory);

		IDMappingParser.loadMapping(fileName, parsingStartLine, parsingStopLine,
				getOrCreateIDType(fromIDType, fromDataType), getOrCreateIDType(toIDType, toDataType), delimiter,
				category, isMultiMapping, createReverseMapping,
				resolveCodeMappingUsingCodeToId_LUT,
				getOrCreateIDType(codeResolvedFromIDType, codeResolvedFromDataType),
				getOrCreateIDType(codeResolvedToIDType, codeResolvedToDataType));
	}

	private IDType getOrCreateIDType(String typeName, EDataType dataType) {
		if (typeName == null)
			return null;
		IDType idType = IDType.getIDType(typeName);
		if (idType != null)
			return idType;
		return IDType.registerType(typeName, IDCategory.getIDCategory(idCategory), dataType);
	}

	/**
	 * @return the fileName, see {@link #fileName}
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            setter, see {@link fileName}
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the parsingStartLine, see {@link #parsingStartLine}
	 */
	public int getParsingStartLine() {
		return parsingStartLine;
	}

	/**
	 * @param parsingStartLine
	 *            setter, see {@link parsingStartLine}
	 */
	public void setParsingStartLine(int parsingStartLine) {
		this.parsingStartLine = parsingStartLine;
	}

	/**
	 * @return the parsingStopLine, see {@link #parsingStopLine}
	 */
	public int getParsingStopLine() {
		return parsingStopLine;
	}

	/**
	 * @param parsingStopLine
	 *            setter, see {@link parsingStopLine}
	 */
	public void setParsingStopLine(int parsingStopLine) {
		this.parsingStopLine = parsingStopLine;
	}

	/**
	 * @return the separator, see {@link #delimiter}
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter
	 *            setter, see {@link separator}
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return the idCategory, see {@link #idCategory}
	 */
	public String getIdCategory() {
		return idCategory;
	}

	/**
	 * @param idCategory
	 *            setter, see {@link idCategory}
	 */
	public void setIdCategory(String idCategory) {
		this.idCategory = idCategory;
	}

	/**
	 * @return the fromIDType, see {@link #fromIDType}
	 */
	public String getFromIDType() {
		return fromIDType;
	}

	/**
	 * @param fromIDType
	 *            setter, see {@link fromIDType}
	 */
	public void setFromIDType(String fromIDType) {
		this.fromIDType = fromIDType;
	}

	/**
	 * @return the fromDataType, see {@link #fromDataType}
	 */
	public EDataType getFromDataType() {
		return fromDataType;
	}

	/**
	 * @param fromDataType
	 *            setter, see {@link fromDataType}
	 */
	public void setFromDataType(EDataType fromDataType) {
		this.fromDataType = fromDataType;
	}

	/**
	 * @return the toIDType, see {@link #toIDType}
	 */
	public String getToIDType() {
		return toIDType;
	}

	/**
	 * @param toIDType
	 *            setter, see {@link toIDType}
	 */
	public void setToIDType(String toIDType) {
		this.toIDType = toIDType;
	}

	/**
	 * @return the toDataType, see {@link #toDataType}
	 */
	public EDataType getToDataType() {
		return toDataType;
	}

	/**
	 * @param toDataType
	 *            setter, see {@link toDataType}
	 */
	public void setToDataType(EDataType toDataType) {
		this.toDataType = toDataType;
	}

	/**
	 * @return the codeResolvedFromIDType, see {@link #codeResolvedFromIDType}
	 */
	public String getCodeResolvedFromIDType() {
		return codeResolvedFromIDType;
	}

	/**
	 * @param codeResolvedFromIDType
	 *            setter, see {@link codeResolvedFromIDType}
	 */
	public void setCodeResolvedFromIDType(String codeResolvedFromIDType) {
		this.codeResolvedFromIDType = codeResolvedFromIDType;
	}

	/**
	 * @return the codeResolvedFromDataType, see {@link #codeResolvedFromDataType}
	 */
	public EDataType getCodeResolvedFromDataType() {
		return codeResolvedFromDataType;
	}

	/**
	 * @param codeResolvedFromDataType
	 *            setter, see {@link codeResolvedFromDataType}
	 */
	public void setCodeResolvedFromDataType(EDataType codeResolvedFromDataType) {
		this.codeResolvedFromDataType = codeResolvedFromDataType;
	}

	/**
	 * @return the codeResolvedToIDType, see {@link #codeResolvedToIDType}
	 */
	public String getCodeResolvedToIDType() {
		return codeResolvedToIDType;
	}

	/**
	 * @param codeResolvedToIDType
	 *            setter, see {@link codeResolvedToIDType}
	 */
	public void setCodeResolvedToIDType(String codeResolvedToIDType) {
		this.codeResolvedToIDType = codeResolvedToIDType;
	}

	/**
	 * @return the codeResolvedToDataType, see {@link #codeResolvedToDataType}
	 */
	public EDataType getCodeResolvedToDataType() {
		return codeResolvedToDataType;
	}

	/**
	 * @param codeResolvedToDataType
	 *            setter, see {@link codeResolvedToDataType}
	 */
	public void setCodeResolvedToDataType(EDataType codeResolvedToDataType) {
		this.codeResolvedToDataType = codeResolvedToDataType;
	}

	/**
	 * @return the isMultiMapping, see {@link #isMultiMapping}
	 */
	public boolean isMultiMapping() {
		return isMultiMapping;
	}

	/**
	 * @param isMultiMapping
	 *            setter, see {@link isMultiMapping}
	 */
	public void setMultiMapping(boolean isMultiMapping) {
		this.isMultiMapping = isMultiMapping;
	}

	/**
	 * @return the createReverseMapping, see {@link #createReverseMapping}
	 */
	public boolean isCreateReverseMapping() {
		return createReverseMapping;
	}

	/**
	 * @param createReverseMapping
	 *            setter, see {@link createReverseMapping}
	 */
	public void setCreateReverseMapping(boolean createReverseMapping) {
		this.createReverseMapping = createReverseMapping;
	}

	/**
	 * @return the resolveCodeMappingUsingCodeToId_LUT, see {@link #resolveCodeMappingUsingCodeToId_LUT}
	 */
	public boolean isResolveCodeMappingUsingCodeToId_LUT() {
		return resolveCodeMappingUsingCodeToId_LUT;
	}

	/**
	 * @param resolveCodeMappingUsingCodeToId_LUT
	 *            setter, see {@link resolveCodeMappingUsingCodeToId_LUT}
	 */
	public void setResolveCodeMappingUsingCodeToId_LUT(boolean resolveCodeMappingUsingCodeToId_LUT) {
		this.resolveCodeMappingUsingCodeToId_LUT = resolveCodeMappingUsingCodeToId_LUT;
	}
}
