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
package org.caleydo.datadomain.generic;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
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

//		recordDenominationSingular = "entity";
//		recordDenominationPlural = "entities";
	}

	@Override
	public void init() {
		icon = EIconTextures.DATA_DOMAIN_CLINICAL;

		super.init();
	}

//	@Override
//	public void createDefaultConfiguration() {
//		configuration = new DataDomainConfiguration();
//		configuration.setDefaultConfiguration(true);
//
//		configuration.setRecordIDCategory("UNSPECIFIED_RECORD");
//		configuration.setDimensionIDCategory("UNSPECIFIED_DIMENSION");
//
////		configuration.setHumanReadableRecordIDType("unspecified_record");
////		configuration.setHumanReadableDimensionIDType("unspecified_column");
//
//		// recordIDType = IDType.registerType("UNSPECIFIED_RECORD",
//		// recordIDCategory,
//		// EColumnType.STRING);
//		// dimensionIDType = IDType.registerType("UNSPECIFIED_DIMENSION",
//		// dimensionIDCategory, EColumnType.STRING);
//
//		// primaryRecordMappingType = recordIDType;//
//		// IDType.getIDType(DataTable.RECORD);
//		// humanReadableRecordIDType = recordIDType;
//		// primaryDimensionMappingType = dimensionIDType;
//		// humanReadableDimensionIDType = dimensionIDType;
//
////		configuration.setRecordDenominationPlural("records");
////		configuration.setRecordDenominationSingular("record");
////		configuration.setDimensionDenominationPlural("dimensions");
////		configuration.setDimensionDenominationSingular("dimension");
//	}
//
//	@Override
//	public void createDefaultConfigurationWithColumnsAsRecords() {
//		configuration = new DataDomainConfiguration();
//		configuration.setDefaultConfiguration(true);
//
//		configuration.setRecordIDCategory("UNSPECIFIED_DIMENSION");
//		configuration.setDimensionIDCategory("UNSPECIFIED_RECORD");
//
////		configuration.setHumanReadableRecordIDType("unspecified_column");
////		configuration.setHumanReadableDimensionIDType("unspecified_record");
////
////		configuration.setRecordDenominationPlural("dimensions");
////		configuration.setRecordDenominationSingular("dimension");
////		configuration.setDimensionDenominationPlural("records");
////		configuration.setDimensionDenominationSingular("record");
//	}
}
