/**
 * 
 */
package org.caleydo.core.data.datadomain;

import javax.xml.bind.annotation.XmlElement;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;

/**
 * @author Alexander Lex
 */
public class DataDomainConfiguration {

	@XmlElement
	boolean isColumnDimension;
	@XmlElement
	String mappingFile;
	@XmlElement
	String recordIDCategory;
	@XmlElement
	String dimensionIDCategory;
	
	@XmlElement
	String primaryRecordMappingType;
	@XmlElement
	String humanReadableRecordIDType;
	
	@XmlElement
	String primaryDimensionMappingType;
	@XmlElement
	String humanReadableDimensionIDType;

	@XmlElement
	String recordDenominationSingular = "not set";
	@XmlElement
	String recordDenominationPlural = "not set";

	@XmlElement
	String dimensionDenominationSingular = "not set";
	@XmlElement
	String dimensionDenominationPlural = "not set";

	/**
	 * @param isColumnDimension
	 *            setter, see {@link #isColumnDimension}
	 */
	public void setColumnDimension(boolean isColumnDimension) {
		this.isColumnDimension = isColumnDimension;
	}

	/**
	 * @param recordIDCategory
	 *            setter, see {@link #recordIDCategory}
	 */
	public void setRecordIDCategory(String recordIDCategory) {
		this.recordIDCategory = recordIDCategory;
	}

	/**
	 * @param dimensionIDCategory
	 *            setter, see {@link #dimensionIDCategory}
	 */
	public void setDimensionIDCategory(String dimensionIDCategory) {
		this.dimensionIDCategory = dimensionIDCategory;
	}

	/**
	 * @param primaryRecordMappingType
	 *            setter, see {@link #primaryRecordMappingType}
	 */
	public void setPrimaryRecordMappingType(String primaryRecordMappingType) {
		this.primaryRecordMappingType = primaryRecordMappingType;
	}

	/**
	 * @param primaryDimensionMappingType
	 *            setter, see {@link #primaryDimensionMappingType}
	 */
	public void setPrimaryDimensionMappingType(String primaryDimensionMappingType) {
		this.primaryDimensionMappingType = primaryDimensionMappingType;
	}

	/**
	 * @param humanReadableRecordIDType
	 *            setter, see {@link #humanReadableRecordIDType}
	 */
	public void setHumanReadableRecordIDType(String humanReadableRecordIDType) {
		this.humanReadableRecordIDType = humanReadableRecordIDType;
	}

	/**
	 * @param humanReadableDimensionIDType
	 *            setter, see {@link #humanReadableDimensionIDType}
	 */
	public void setHumanReadableDimensionIDType(String humanReadableDimensionIDType) {
		this.humanReadableDimensionIDType = humanReadableDimensionIDType;
	}

	/**
	 * @param recordDenominationPlural
	 *            setter, see {@link #recordDenominationPlural}
	 */
	public void setRecordDenominationPlural(String recordDenominationPlural) {
		this.recordDenominationPlural = recordDenominationPlural;
	}

	/**
	 * @param recordDenominationSingular
	 *            setter, see {@link #recordDenominationSingular}
	 */
	public void setRecordDenominationSingular(String recordDenominationSingular) {
		this.recordDenominationSingular = recordDenominationSingular;
	}

	/**
	 * @param dimensionDenominationPlural
	 *            setter, see {@link #dimensionDenominationPlural}
	 */
	public void setDimensionDenominationPlural(String dimensionDenominationPlural) {
		this.dimensionDenominationPlural = dimensionDenominationPlural;
	}

	/**
	 * @param dimensionDenominationSingular
	 *            setter, see {@link #dimensionDenominationSingular}
	 */
	public void setDimensionDenominationSingular(String dimensionDenominationSingular) {
		this.dimensionDenominationSingular = dimensionDenominationSingular;
	}

	/**
	 * @param mappingFile
	 *            setter, see {@link #mappingFile}
	 */
	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}

	/**
	 * @param recordIDType
	 *            setter, see {@link #recordIDType}
	 */
	// public void setRecordIDType(IDType recordIDType) {
	// this.recordIDType = recordIDType;
	// }
	//
	// /**
	// * @param dimensionIDType
	// * setter, see {@link #dimensionIDType}
	// */
	// public void setDimensionIDType(IDType dimensionIDType) {
	// this.dimensionIDType = dimensionIDType;
	// }

}
