/**
 * 
 */
package org.caleydo.core.data.datadomain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;

/**
 * Configuration for {@link ATableBasedDataDomain}. This initializes the parts of the configuration of the
 * data domain which are specific to the type of the data set loaded. Examples are {@link IDType}s, and
 * {@link IDCategory}s.
 * <p>
 * TODO: check whether a valid configuration has been set
 * </p>
 * 
 * @author Alexander Lex
 */
public class DataDomainConfiguration {

	/**
	 * Flag determining whether this configuration was created as a default configuration by a data domain
	 * (true) or was manually specified (false, the default). We may throw away defaults and rebuild them
	 * (e.g. when column - dimension association is changed).
	 */
	@XmlTransient
	boolean isDefaultConfiguration = false;

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
	 * @param isDefaultConfiguration
	 *            setter, see {@link #isDefaultConfiguration}.
	 */
	public void setDefaultConfiguration(boolean isDefaultConfiguration) {
		this.isDefaultConfiguration = isDefaultConfiguration;
	}

	/**
	 * @return the isDefaultConfiguration, see {@link #isDefaultConfiguration}
	 */
	public boolean isDefaultConfiguration() {
		return isDefaultConfiguration;
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
}
