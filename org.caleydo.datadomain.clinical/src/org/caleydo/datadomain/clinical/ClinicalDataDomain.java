package org.caleydo.datadomain.clinical;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * TODO The use case for clinical input data.
 * 
 * @author Marc Streit
 */
@XmlType
@XmlRootElement
public class ClinicalDataDomain extends ATableBasedDataDomain {

	public final static String DATA_DOMAIN_TYPE = "org.caleydo.datadomain.genetic";

	/**
	 * Counter used for determining the extension that together with the type
	 * builds the data domain ID.
	 */
	private static int extensionID = 0;

	/**
	 * Constructor.
	 */
	public ClinicalDataDomain() {

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE
				+ DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER + extensionID++);
		icon = EIconTextures.DATA_DOMAIN_CLINICAL;
	}

	@Override
	public void setTable(DataTable set) {

		super.setTable(set);
	}

	@Override
	public void handleRecordVADelta(RecordVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDimensionVADelta(DimensionVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForeignRecordVAUpdate(String dataDomainType, String vaType,
			PerspectiveInitializationData data) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleSelectionCommand(IDCategory idCategory,
			SelectionCommand selectionCommand) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRecordLabel(IDType idType, Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDimensionLabel(IDType idType, Object id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createDefaultConfiguration() {
		// FIXME create default config

		recordIDCategory = IDCategory.getIDCategory("EXPERIMENT");
		dimensionIDCategory = IDCategory.getIDCategory("EXPERIMENT_DATA");

		throw new IllegalStateException();
	}

	@Override
	public void createDefaultConfigurationWithColumnsAsRecords() {
		// FIXME create default config
		throw new IllegalStateException();
	}
}
