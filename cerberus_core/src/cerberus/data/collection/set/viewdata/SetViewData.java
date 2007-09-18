/**
 * 
 */
package cerberus.data.collection.set.viewdata;

import cerberus.data.collection.ISet;
import cerberus.data.collection.SetType;
import cerberus.data.view.camera.IViewCamera;
import cerberus.data.view.camera.ViewCameraPitchRollYaw;
import cerberus.manager.IGeneralManager;

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
			this.refIViewCamera = new ViewCameraPitchRollYaw(iSetCollectionId, this);
		} //if ( setViewCamera == null ) 
		else
		{
			this.refIViewCamera = setViewCamera;
		} //if ( setViewCamera == null ) {..} else {..}
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#addSubSet(cerberus.data.collection.ISet)
	 */
	public final boolean addSubSet(ISet addSet) {
		
		assert false : "call dummy method";
		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#getSubSets()
	 */
	public final ISet[] getSubSets() {

		assert false : "call dummy method";
		return new ISet[0];
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#hasSubSets()
	 */
	public final boolean hasSubSets() {

		return false;
	}

	/* (non-Javadoc)
	 * @see cerberus.data.collection.ISubSet#removeSubSet(cerberus.data.collection.ISet)
	 */
	public final boolean removeSubSet(ISet addSet) {

		assert false : "call dummy method";
		return false;
	}
	
	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.ASetSimple#getCacheId()
	 */
	@Override
	public final int getCacheId() {
		
		return iCacheId;
	}
	
	/* ---------------- '/'
	 * 
	 */
	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.viewdata.ISetViewData#getViewCamera()
	 */
	public IViewCamera getViewCamera() {

		return refIViewCamera;
	}


	/* (non-Javadoc)
	 * @see cerberus.data.collection.set.viewdata.ISetViewData#setViewCamera(cerberus.data.view.camera.IViewCamera)
	 */
	public void setViewCamera(IViewCamera setViewCamera) {

		refIViewCamera = setViewCamera;
	}
	/* (non-Javadoc)
	 * @see cerberus.view.jogl.IJoglMouseListener#hasViewCameraChanged()
	 */
	public boolean hasViewCameraChanged() {

		return this.refIViewCamera.hasViewCameraChanged();
	}
}
