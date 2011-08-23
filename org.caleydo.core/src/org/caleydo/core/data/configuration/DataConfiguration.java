/**
 * 
 */
package org.caleydo.core.data.configuration;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;

/**
 * Bean holding a set of configuration for data properties, thereby specifying excatly which data to use.
 * 
 * @author Alexander Lex
 */
public class DataConfiguration {

	private IDataDomain dataDomain;
	private RecordPerspective recordPerspective;
	private DimensionPerspective dimensionPerspective;

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @param dataDomain
	 *            setter, see {@link #dataDomain}
	 */
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	/**
	 * @return the recordPerspective, see {@link #recordPerspective}
	 */
	public RecordPerspective getRecordPerspective() {
		return recordPerspective;
	}

	/**
	 * @param recordPerspective
	 *            setter, see {@link #recordPerspective}
	 */
	public void setRecordPerspective(RecordPerspective recordPerspective) {
		this.recordPerspective = recordPerspective;
	}

	/**
	 * @return the dimensionPerspective, see {@link #dimensionPerspective}
	 */
	public DimensionPerspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #dimensionPerspective}
	 */
	public void setDimensionPerspective(DimensionPerspective dimensionPerspective) {
		this.dimensionPerspective = dimensionPerspective;
	}

}
