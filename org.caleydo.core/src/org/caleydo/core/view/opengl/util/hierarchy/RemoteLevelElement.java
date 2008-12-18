package org.caleydo.core.view.opengl.util.hierarchy;

import gleem.linalg.open.Transform;
import org.caleydo.core.data.AUniqueObject;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;

public class RemoteLevelElement
	extends AUniqueObject
{
	private Transform transform;

	/**
	 * ID of the element that is rendered at this remote level position.
	 */
	private int iContainedElementID = -1;

	public RemoteLevelElement()
	{
		super(GeneralManager.get().getIDManager().createID(
				EManagedObjectType.REMOTE_LEVEL_ELEMENT));

		RemoteElementManager.get().registerItem(this);
	}

	public int getContainedElementID()
	{
		return iContainedElementID;
	}

	public void setContainedElementID(int iContainedElementID)
	{
		this.iContainedElementID = iContainedElementID;
	}

	public Transform getTransform()
	{
		return transform;
	}

	public void setTransform(Transform transform)
	{
		this.transform = transform;
	}

	public boolean isFree()
	{
		return iContainedElementID == -1 ? true : false;
	}
}
