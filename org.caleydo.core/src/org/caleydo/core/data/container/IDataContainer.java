/**
 * 
 */
package org.caleydo.core.data.container;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;

/**
 * @author Alexander Lex
 */
public interface IDataContainer {
	public ATableBasedDataDomain getDataDomain();

	public void setDataDomain(ATableBasedDataDomain dataDomain);

	public RecordPerspective getRecordPerspective();

	public void setRecordPerspective(RecordPerspective recordPerspective);

	public DimensionPerspective getDimensionPerspective();

	public void setDimensionPerspective(DimensionPerspective dimensionPerspective);
}
