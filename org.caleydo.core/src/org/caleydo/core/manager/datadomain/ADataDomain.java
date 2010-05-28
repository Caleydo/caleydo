package org.caleydo.core.manager.datadomain;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.specialized.EOrganism;

/**
 * Abstract use case class that implements data and view management.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
@XmlType
@XmlRootElement
public abstract class ADataDomain
	implements IDataDomain {

	protected String contentLabelSingular = "<not specified>";
	protected String contentLabelPlural = "<not specified>";

	protected EDataFilterLevel dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;

	/**
	 * This mode determines whether the user can load and work with gene expression data or otherwise if an
	 * not further specified data set is loaded. In the case of the unspecified data set some specialized gene
	 * expression features are not available.
	 */
	protected String dataDomainType = "unspecified";

	/** parameters for loading the the data-{@link set} */
	protected LoadDataParameters loadDataParameters;

	/** bootstrap filename this application was started with */
	protected String bootsTrapFileName;

	/** Every use case needs to state all views that can visualize its data */
	protected ArrayList<String> possibleViews;

	/**
	 * Every use case needs to state all ID Categories it can handle. The string must specify which primary
	 * VAType ({@link VAType#getPrimaryVAType()} is associated for the ID Category
	 */
	protected HashMap<EIDCategory, String> possibleIDCategories;

	/**
	 * Organism on which the genetic analysis data bases on.
	 */
	private EOrganism eOrganism = EOrganism.HOMO_SAPIENS;

	public ADataDomain() {
		
	}

	@Override
	public String getDataDomainType() {
		return dataDomainType;
	}

	@Override
	public ArrayList<String> getPossibleViews() {
		return possibleViews;
	}

	public String getContentLabelSingular() {
		return contentLabelSingular;
	}

	public void setContentLabelSingular(String contentLabelSingular) {
		this.contentLabelSingular = contentLabelSingular;
	}

	public String getContentLabelPlural() {
		return contentLabelPlural;
	}

	public void setContentLabelPlural(String contentLabelPlural) {
		this.contentLabelPlural = contentLabelPlural;
	}

	public EDataFilterLevel getDataFilterLevel() {
		return dataFilterLevel;
	}

	public void setDataFilterLevel(EDataFilterLevel dataFilterLevel) {
		this.dataFilterLevel = dataFilterLevel;
	}

	@Override
	public LoadDataParameters getLoadDataParameters() {
		return loadDataParameters;
	}

	@Override
	public void setLoadDataParameters(LoadDataParameters loadDataParameters) {
		this.loadDataParameters = loadDataParameters;
	}

	public String getBootstrapFileName() {
		return bootsTrapFileName;
	}

	public void setBootstrapFileName(String bootsTrapFileName) {
		this.bootsTrapFileName = bootsTrapFileName;
	}

	public void setOrganism(EOrganism eOrganism) {
		this.eOrganism = eOrganism;
	}

	public EOrganism getOrganism() {
		return eOrganism;
	}

	@Override
	public String toString() {
		return dataDomainType;
	}
}
