package org.caleydo.core.data.collection.set.viewdata;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.view.camera.IViewCamera;
import org.caleydo.core.data.view.camera.ViewCameraPitchRollYaw;
import org.caleydo.core.manager.IGeneralManager;

/**
 * A Set handling a ViewCamera.
 * 
 * @author Michael Kalkusch
 *
 */
public class SetViewData 
extends ASetViewData 
implements ISetViewData {
	
	/**
	 * Creates a new ViewCamera namly a ViewCameraPitchRollYaw camera.
	 * 
	 * @param iSetCollectionId
	 * @param setGeneralManager
	 * @param setType
	 */
	public SetViewData(int iSetCollectionId,
			IGeneralManager setGeneralManager,
			IViewCamera setViewCamera,
			SetType setType) {

		super(iSetCollectionId, setGeneralManager, setType);	
		
		if ( setViewCamera == null ) 
		{
			this.refIViewCamera = new ViewCameraPitchRollYaw(iSetCollectionId);
		} //if ( setViewCamera == null ) 
		else
		{
			this.refIViewCamera = setViewCamera;
		} //if ( setViewCamera == null ) {..} else {..}
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISubSet#addSubSet(org.caleydo.core.data.collection.ISet)
	 */
	public final boolean addSubSet(ISet addSet) {
		
		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISubSet#getSubSets()
	 */
	public final ISet[] getSubSets() {

		assert false : "call dummy method";
		return new ISet[0];
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISubSet#hasSubSets()
	 */
	public final boolean hasSubSets() {

		return false;
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.ISubSet#removeSubSet(org.caleydo.core.data.collection.ISet)
	 */
	public final boolean removeSubSet(ISet addSet) {

		assert false : "call dummy method";
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.set.ASetSimple#getCacheId()
	 */
	@Override
	public final int getCacheId() {
		
		return iCacheId;
	}
	
	/* ---------------- '/'
	 * 
	 */
	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.set.viewdata.ISetViewData#getViewCamera()
	 */
	public IViewCamera getViewCamera() {

		return refIViewCamera;
	}


	/* (non-Javadoc)
	 * @see org.caleydo.core.data.collection.set.viewdata.ISetViewData#setViewCamera(org.caleydo.core.data.view.camera.IViewCamera)
	 */
	public void setViewCamera(IViewCamera setViewCamera) {

		refIViewCamera = setViewCamera;
	}
	/* (non-Javadoc)
	 * @see org.caleydo.core.view.jogl.IJoglMouseListener#hasViewCameraChanged()
	 */
	public boolean hasViewCameraChanged() {

		return this.refIViewCamera.hasViewCameraChanged();
	}
}
