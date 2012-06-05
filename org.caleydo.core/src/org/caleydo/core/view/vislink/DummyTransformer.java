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
package org.caleydo.core.view.vislink;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Transforms and projects selections of {@link GLRemoteRendering} views.
 * 
 * @author Werner Puff
 */
public class DummyTransformer
	extends StandardTransformer {

	/**
	 * Only points located in remote level elements contained in this list are transformed. All others will be
	 * ignored.
	 */
	protected ArrayList<RemoteLevelElement> remoteLevelElementWhiteList;

	/**
	 * Creates a new instance for a related {@link GLRemoteRendering} view.
	 * 
	 * @param viewID
	 *            the viewID of the {@link GLRemoteRendering} view to do transformations.
	 * @param remoteLevelElementWhiteList
	 *            the white list of remote level elements to transform.
	 */
	public DummyTransformer(int viewID, ArrayList<RemoteLevelElement> remoteLevelElementWhiteList) {
		super(viewID);

		this.remoteLevelElementWhiteList = remoteLevelElementWhiteList;
	}

	/**
	 * Transforms all selection points in the source map that have been provided by views, remotely rendered
	 * by the related {@link GLRemoteRendering} view into the {@link GLRemoteRendering}-view's coordinate
	 * system.
	 * 
	 * @see ISelectionTransformer#transform(HashMap, HashMap)
	 */
	@Override
	public boolean transform(HashMap<IDType, ConnectionMap> source, HashMap<IDType, ConnectionMap> target) {

		if (transformationFinished) {
			return false;
		}
		transformationFinished = true;

		for (Entry<IDType, ConnectionMap> typeConnections : source.entrySet()) {

			ConnectionMap connectionMap = target.get(typeConnections.getKey());
			if (connectionMap == null) {
				connectionMap = new ConnectionMap();
				target.put(typeConnections.getKey(), connectionMap);
			}

			for (Entry<Integer, SelectedElementRepList> connections : typeConnections.getValue().entrySet()) {

				SelectedElementRepList repList = connectionMap.get(connections.getKey());
				if (repList == null) {
					repList = new SelectedElementRepList();
					connectionMap.put(connections.getKey(), repList);
				}

				ViewManager vm = GeneralManager.get().getViewManager();
				for (ElementConnectionInformation sel : connections.getValue()) {
					AGLView view = vm.getGLView(sel.getSourceViewID());
					RemoteLevelElement rle = view.getRemoteLevelElement();
					if (remoteLevelElementWhiteList.contains(rle)) {
						ArrayList<Vec3f> transformedPoints = new ArrayList<Vec3f>();
						for (Vec3f vec : sel.getPoints()) {
							transformedPoints.add(transform(vec, rle));
						}
						ElementConnectionInformation trans =
							new ElementConnectionInformation(sel.getIDType(), sel.getSourceViewID(), viewID,
								transformedPoints);
						repList.add(trans);
					}
				}
			}
		}
		return true;
	}

	/**
	 * Transforms a point in a remote view to the coordinate system of the view
	 * 
	 * @param vecOriginalPoint
	 * @param vecTranslation
	 * @param vecScale
	 * @param rotation
	 * @param remoteLevelElement
	 * @return transformed point in the view's coordinate-system
	 */
	private Vec3f transform(Vec3f vecOriginalPoint, RemoteLevelElement remoteLevelElement) {
		Mat4f matSrc = new Mat4f();
		Vec3f vecTransformedPoint = new Vec3f();

		Vec3f vecTranslation = remoteLevelElement.getTransform().getTranslation();
		Vec3f vecScale = remoteLevelElement.getTransform().getScale();
		Rotf rotation = remoteLevelElement.getTransform().getRotation();

		rotation.toMatrix(matSrc);

		// if (GeneralManager.get().getTrackDataProvider().isTrackModeActive()) {
		// // WiiRemote wiiRemote = GeneralManager.get().getWiiRemote();
		//
		// float[] fArHeadPosition = GeneralManager.get().getTrackDataProvider().getEyeTrackData();
		//
		// fArHeadPosition[0] -= 183;
		// fArHeadPosition[1] -= 112;
		//
		// fArHeadPosition[0] = fArHeadPosition[0] - 1730/2;
		// fArHeadPosition[1] = (fArHeadPosition[1] - 1055/2f);
		//
		// fArHeadPosition[0] = fArHeadPosition[0] / 1730 * 4f;
		// fArHeadPosition[1] = fArHeadPosition[1] / 1055 * 4f * 0.61f;
		//
		// // fArHeadPosition[0] = 0f;
		// // fArHeadPosition[1] = -1.3f;
		//
		// // fArHeadPosition[0] = fArHeadPosition[0] * 4 + 4;
		// // fArHeadPosition[1] *= 4;
		//
		// float fBucketWidth = 2f;
		// float fBucketHeight = 2f;
		// float fBucketDepth = 4.0f;
		// float fBucketBottomLeft = -1 * fArHeadPosition[0] - fBucketWidth;// - 1.5f;
		// float fBucketBottomRight = -1 * fArHeadPosition[0] + fBucketWidth;// - 1.5f;
		// float fBucketBottomTop = fArHeadPosition[1] * 1.4f + fBucketHeight;
		// float fBucketBottomBottom = fArHeadPosition[1] * 1.4f - fBucketHeight;
		//
		// float fNormalizedHeadDist =
		// -1 * GeneralManager.get().getWiiRemote().getCurrentHeadDistance() + 7f
		// + Math.abs(fBucketBottomRight - 2) / 2 + Math.abs(fBucketBottomTop - 2) * 2;// / 2;
		// // -1 * GeneralManager.get().getWiiRemote().getCurrentHeadDistance() + 7f +
		// Math.abs(fBucketBottomRight - 2) / 2
		// // + Math.abs(fBucketBottomTop - 2) / 2;
		//
		// Vec3f vecTrackTranformPoint = new Vec3f(vecOriginalPoint);
		//
		// if (stackLevel.getElementByPositionIndex(1) == remoteLevelElement) {
		// float fAK = fBucketDepth - 1 * fNormalizedHeadDist;
		// float fGK = fBucketWidth + fBucketBottomLeft;
		// float fPlaneWidth = (float) Math.sqrt((double) (Math.pow(fAK, 2) + Math.pow(fGK, 2)));
		//
		// float fTransformedX = vecOriginalPoint.x() / 8f * fPlaneWidth;
		// float fTransformedY = vecOriginalPoint.y() / 8f * fBucketHeight * 2f;// * (4 + fYTop - (-4 +
		// // fYBottom));
		//
		// float fXScaling = fTransformedX / fPlaneWidth;
		// fTransformedY += fArHeadPosition[1] * fXScaling;
		//
		// vecTrackTranformPoint =
		// new Vec3f(fTransformedX, -fBucketHeight + fTransformedY, vecOriginalPoint.z()); // / 4f *
		// // fBucketHeight
		// }
		// else if (stackLevel.getElementByPositionIndex(3) == remoteLevelElement) {
		// float fAK = fBucketDepth - 1 * fNormalizedHeadDist;
		// float fGK = fBucketWidth - fBucketBottomRight;
		// float fPlaneWidth = (float) Math.sqrt((double) (Math.pow(fAK, 2) + Math.pow(fGK, 2)));
		//
		// float fTransformedX = vecOriginalPoint.x() / 8f * fPlaneWidth;
		// float fTransformedY = vecOriginalPoint.y() / 8f * fBucketHeight * 2f;// * (4 + fYTop - (-4 +
		// // fYBottom));
		//
		// float fXScaling = fTransformedX / fPlaneWidth;
		// fTransformedY += fArHeadPosition[1] * (1 - fXScaling);
		//
		// vecTrackTranformPoint =
		// new Vec3f(fPlaneWidth - fTransformedX, -fBucketHeight + fTransformedY, vecOriginalPoint
		// .z()); // /
		// // 4f
		// // *
		// // fBucketHeight
		// }
		// else if (stackLevel.getElementByPositionIndex(0) == remoteLevelElement) {
		// float fAK = fBucketDepth - 1 * fNormalizedHeadDist;
		// float fGK = fBucketWidth - fBucketBottomTop;
		// float fPlaneWidth = (float) Math.sqrt((double) (Math.pow(fAK, 2) + Math.pow(fGK, 2)));
		//
		// float fTransformedX = vecOriginalPoint.x() / 8f * fBucketHeight * 2f;
		// float fTransformedY = vecOriginalPoint.y() / 8f * fPlaneWidth;// * (4 + fYTop - (-4 +
		// // fYBottom));
		//
		// float fYScaling = fTransformedY / fPlaneWidth;
		// // fTransformedX += fArHeadPosition[0] * fYScaling;
		//
		// vecTrackTranformPoint =
		// new Vec3f((fBucketWidth + fBucketBottomLeft) * (1 - fYScaling) + fTransformedX,
		// fPlaneWidth - fTransformedY, vecOriginalPoint.z());
		// }
		// else if (stackLevel.getElementByPositionIndex(2) == remoteLevelElement) {
		// float fAK = fBucketDepth - 1 * fNormalizedHeadDist;
		// float fGK = fBucketWidth + fBucketBottomBottom;
		// float fPlaneWidth = (float) Math.sqrt((double) (Math.pow(fAK, 2) + Math.pow(fGK, 2)));
		//
		// float fTransformedX = vecOriginalPoint.x() / 8f * fBucketHeight * 2f;
		// float fTransformedY = vecOriginalPoint.y() / 8f * fPlaneWidth;
		//
		// float fYScaling = fTransformedY / fPlaneWidth;
		// // fTransformedX += fArHeadPosition[1] * (1-fYScaling);
		//
		// vecTrackTranformPoint =
		// new Vec3f((fBucketWidth + fBucketBottomLeft) * fYScaling + fTransformedX, fTransformedY,
		// vecOriginalPoint.z());
		// }
		//
		// matSrc.xformPt(vecTrackTranformPoint, vecTransformedPoint);
		// }
		// else {
		matSrc.xformPt(vecOriginalPoint, vecTransformedPoint);
		// }

		vecTransformedPoint.componentMul(vecScale);
		vecTransformedPoint.add(vecTranslation);

		return vecTransformedPoint;
	}

}
