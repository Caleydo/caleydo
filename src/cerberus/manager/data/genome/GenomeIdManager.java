/**
 * 
 */
package cerberus.manager.data.genome;

import cerberus.data.mapping.GenomeIdType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.data.IGenomeIdManager;


/**
 * @author java
 *
 */
public class GenomeIdManager extends AGenomeIdManager
		implements IGenomeIdManager {

	/**
	 * @param setGeneralManager
	 */
	public GenomeIdManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_startEditingSetTypes(cerberus.data.mapping.GenomeIdType, cerberus.data.mapping.GenomeIdType)
	 */
	public void buildLUT_startEditingSetTypes(GenomeIdType typeFromId,
			GenomeIdType typeToId) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT(int, int)
	 */
	public void buildLUT(int iFirst, int iSecond) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT(java.lang.String, java.lang.String)
	 */
	public void buildLUT(String iFirst, String iSecond) {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see cerberus.manager.data.IGenomeIdManager#buildLUT_stopEditing()
	 */
	public boolean buildLUT_stopEditing() {

		// TODO Auto-generated method stub
		return false;
	}

}
