package org.caleydo.datadomain.generic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainConfiguration;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Use case for generic set-based data which is not further specified.
 * 
 * @author Marc Streit
 * @author Alexander lex
 */
@XmlType
@XmlRootElement
public class GenericDataDomain extends ATableBasedDataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.generic";

	/**
	 * Counter used for determining the extension that together with the type
	 * builds the data domain ID.
	 */
	private static int extensionID = 0;

	/**
	 * Constructor.
	 */
	public GenericDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE
				+ DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER + extensionID++);

		recordDenominationSingular = "entity";
		recordDenominationPlural = "entities";
	}

	@Override
	public void init() {
		icon = EIconTextures.DATA_DOMAIN_CLINICAL;

		super.init();
	}

	@Override
	public void createDefaultConfiguration() {
		configuration = new DataDomainConfiguration();

		configuration.setRecordIDCategory("UNSPECIFIED_RECORD");
		configuration.setDimensionIDCategory("UNSPECIFIED_DIMENSION");

		configuration.setHumanReadableRecordIDType("unspecified_record");
		configuration.setHumanReadableDimensionIDType("unspecified_column");

		// recordIDType = IDType.registerType("UNSPECIFIED_RECORD",
		// recordIDCategory,
		// EColumnType.STRING);
		// dimensionIDType = IDType.registerType("UNSPECIFIED_DIMENSION",
		// dimensionIDCategory, EColumnType.STRING);

		// primaryRecordMappingType = recordIDType;//
		// IDType.getIDType(DataTable.RECORD);
		// humanReadableRecordIDType = recordIDType;
		// primaryDimensionMappingType = dimensionIDType;
		// humanReadableDimensionIDType = dimensionIDType;

		configuration.setRecordDenominationPlural("records");
		configuration.setRecordDenominationSingular("record");
		configuration.setDimensionDenominationPlural("dimensions");
		configuration.setDimensionDenominationSingular("dimension");
	}

	@Override
	public void createDefaultConfigurationWithSamplesAsRows() {
		configuration = new DataDomainConfiguration();

		configuration.setRecordIDCategory("UNSPECIFIED_DIMENSION");
		configuration.setDimensionIDCategory("UNSPECIFIED_RECORD");

		configuration.setHumanReadableRecordIDType("unspecified_column");
		configuration.setHumanReadableDimensionIDType("unspecified_record");

		configuration.setRecordDenominationPlural("dimensions");
		configuration.setRecordDenominationSingular("dimension");
		configuration.setDimensionDenominationPlural("records");
		configuration.setDimensionDenominationSingular("record");
	}
}
