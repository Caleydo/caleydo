package org.caleydo.core.manager.datadomain;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Abstract class that implements data and view management.
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
	public static String dataDomainType = "unspecified";

	protected EIconTextures icon = EIconTextures.NO_ICON_AVAILABLE;

	/** parameters for loading the the data-{@link set} */
	@XmlTransient
	protected LoadDataParameters loadDataParameters;

	/** bootstrap filename this application was started with */
	protected String bootsTrapFileName;

	/**
	 * Every use case needs to state all ID Categories it can handle. The string must specify which primary
	 * VAType ({@link VAType#getPrimaryVAType()} is associated for the ID Category
	 */
	// protected HashMap<IDCategory, String> possibleIDCategories;

	@Override
	public String getDataDomainType() {
		return dataDomainType;
	}

	@Override
	public void setDataDomainType(String dataDomainType) {
		this.dataDomainType = dataDomainType;
	}

	@Override
	public EIconTextures getIcon() {
		return icon;
	};

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

	@XmlTransient
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

	@Override
	public String toString() {
		return dataDomainType;
	}
}
