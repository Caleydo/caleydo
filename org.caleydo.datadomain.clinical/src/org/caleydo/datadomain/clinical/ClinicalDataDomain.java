package org.caleydo.datadomain.clinical;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.id.IDCategory;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
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

		super(DATA_DOMAIN_TYPE, DATA_DOMAIN_TYPE + DataDomainManager.DATA_DOMAIN_INSTANCE_DELIMITER + extensionID++);
		icon = EIconTextures.DATA_DOMAIN_CLINICAL;
	}

	@Override
	protected void initIDMappings() {
		// nothing to do ATM
	}

	@Override
	public void setTable(DataTable set) {

		super.setTable(set);
	}

	@Override
	public void handleVAUpdate(RecordVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleVAUpdate(DimensionVADelta vaDelta, String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleForeignRecordVAUpdate(int tableID, String dataDomainType,
			String vaType, RecordVirtualArray virtualArray) {
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
	protected void assignIDCategories() {
		recordIDCategory = IDCategory.getIDCategory("EXPERIMENT");
		dimensionIDCategory = IDCategory.getIDCategory("EXPERIMENT_DATA");

	}

}
