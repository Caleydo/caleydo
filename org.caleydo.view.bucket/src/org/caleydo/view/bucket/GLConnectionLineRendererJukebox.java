/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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
