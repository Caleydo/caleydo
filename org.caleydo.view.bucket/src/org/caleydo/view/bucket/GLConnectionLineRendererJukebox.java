package org.caleydo.view.bucket;

import javax.media.opengl.GL2;

import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Marc Streit
 */
public class GLConnectionLineRendererJukebox extends AGLConnectionLineRenderer {
	/**
	 * Constructor.
	 * 
	 * @param focusLevel
	 * @param stackLevel
	 * @param poolLevel
	 */
	public GLConnectionLineRendererJukebox(final RemoteLevel focusLevel,
			final RemoteLevel stackLevel, final RemoteLevel poolLevel) {
		super();
	}

	@Override
	protected void renderConnectionLines(final GL2 gl) {

		// Vec3f vecTranslation;
		// Vec3f vecScale;
		//
		// Rotf rotation;
		// Mat4f matSrc = new Mat4f();
		// Mat4f matDest = new Mat4f();
		// matSrc.makeIdent();
		// matDest.makeIdent();
		//
		// Iterator<Integer> iterSelectedElementID = connectedElementRepManager
		// .getAllSelectedElements().iterator();
		//
		// ArrayList<ArrayList<Vec3f>> alPointLists = null;//
		//
		// while (iterSelectedElementID.hasNext())
		// {
		// int iSelectedElementID = iterSelectedElementID.next();
		//
		// ArrayList<SelectedElementRep> alSelectedElementRep =
		// connectedElementRepManager
		// .getSelectedElementRepsByElementID(iSelectedElementID);
		//
		// for (int iStackPositionIndex = 0; iStackPositionIndex <
		// stackLevel.getCapacity(); iStackPositionIndex++)
		// {
		// for (int iElementIndex = 0; iElementIndex <
		// alSelectedElementRep.size(); iElementIndex++)
		// {
		// SelectedElementRep selectedElementRep = alSelectedElementRep
		// .get(iElementIndex);
		//
		// // Check if element is in stack
		// RemoteLevel activeLevel = null;
		// if
		// (stackLevel.containsElement(selectedElementRep.getContainingViewID()))
		// {
		// activeLevel = stackLevel;
		// }
		//
		// // Check if the element is in the currently iterated view in
		// // the stack
		// if (stackLevel.getPositionIndexByElementID(selectedElementRep
		// .getContainingViewID()) != iStackPositionIndex
		// && stackLevel.getPositionIndexByElementID(selectedElementRep
		// .getContainingViewID()) != iStackPositionIndex + 1)
		// {
		// continue;
		// }
		//
		// if (activeLevel != null)
		// {
		// vecTranslation = activeLevel.getTransformByElementId(
		// selectedElementRep.getContainingViewID()).getTranslation();
		// vecScale = activeLevel.getTransformByElementId(
		// selectedElementRep.getContainingViewID()).getScale();
		// rotation = activeLevel.getTransformByElementId(
		// selectedElementRep.getContainingViewID()).getRotation();
		//
		// ArrayList<Vec3f> alPoints = selectedElementRep.getPoints();
		// ArrayList<Vec3f> alPointsTransformed = new ArrayList<Vec3f>();
		//
		// for (Vec3f vecCurrentPoint : alPoints)
		// {
		// alPointsTransformed.add(transform(vecCurrentPoint, vecTranslation,
		// vecScale, rotation));
		// }
		// int iKey = selectedElementRep.getContainingViewID();
		//
		// alPointLists = hashIDTypeToViewToPointLists.get(iKey);
		// if (alPointLists == null)
		// {
		// alPointLists = new ArrayList<ArrayList<Vec3f>>();
		// hashIDTypeToViewToPointLists.put(iKey, alPointLists);
		// }
		// alPointLists.add(alPointsTransformed);
		//
		// }
		// }
		//
		// if (hashIDTypeToViewToPointLists.size() > 1)
		// {
		// renderLineBundling(gl);
		// }
		//
		// hashIDTypeToViewToPointLists.clear();
		// }
		// }

	}
}
