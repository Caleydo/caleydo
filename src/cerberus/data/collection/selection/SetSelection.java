/**
 * 
 */
package cerberus.data.collection.selection;

import cerberus.data.collection.SetType;
import cerberus.data.collection.set.SetPlanarSimple;
import cerberus.manager.IGeneralManager;


/**
 * @author Michael Kalkusch
 *
 */
public class SetSelection extends SetPlanarSimple implements ISelectionSet {


	/**
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 */
	public SetSelection(int iSetCollectionId, IGeneralManager setGeneralManager) {

		super(iSetCollectionId, setGeneralManager, SetType.SET_SELECTION);
	}

}
