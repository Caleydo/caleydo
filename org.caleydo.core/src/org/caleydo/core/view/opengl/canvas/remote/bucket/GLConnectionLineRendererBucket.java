package org.caleydo.core.view.opengl.canvas.remote.bucket;

import gleem.linalg.Mat4f;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.AGLConnectionLineRenderer;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevel;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteLevelElement;

/**
 * Specialized connection line renderer for bucket view.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLConnectionLineRendererBucket
	extends AGLConnectionLineRenderer {

	/**
	 * Constructor.
	 * 
	 * @param underInteractionLevel
	 * @param stackLevel
	 * @param poolLevel
	 */
	public GLConnectionLineRendererBucket(final RemoteLevel focusLevel, final RemoteLevel stackLevel,
		final RemoteLevel poolLevel) {
		super(focusLevel, stackLevel, poolLevel);
	}

	@Override
	protected void renderConnectionLines(final GL gl) {
		Vec3f vecTranslation;
		Vec3f vecScale;

		Rotf rotation;
		Mat4f matSrc = new Mat4f();
		Mat4f matDest = new Mat4f();
		matSrc.makeIdent();
		matDest.makeIdent();

		// int iViewID = 0;
		RemoteLevel activeLevel = null;
		RemoteLevelElement remoteLevelElement = null;

		IViewManager viewGLCanvasManager = GeneralManager.get().getViewGLCanvasManager();

		for (EIDType idType : connectedElementRepManager.getOccuringIDTypes()) {
			ArrayList<ArrayList<Vec3f>> alPointLists = null;
			
			HashMap<Integer, ArrayList<ArrayList<Vec3f>>> hashViewToPointList = hashIDTypeToViewToPointLists.get(idType);
			
			if(hashViewToPointList == null)
			{
				hashViewToPointList = new HashMap<Integer, ArrayList<ArrayList<Vec3f>>>();
				hashIDTypeToViewToPointLists.put(idType, hashViewToPointList);
			}

			for (int iSelectedElementID : connectedElementRepManager.getIDList(idType)) {
				for (SelectedElementRep selectedElementRep : connectedElementRepManager
					.getSelectedElementRepsByElementID(idType, iSelectedElementID)) {
					
					if(selectedElementRep.getIDType() != idType)
						throw new IllegalStateException("Current ID Type does not match the selected elemen rep's");

					AGLEventListener glView =
						viewGLCanvasManager.getGLEventListener(selectedElementRep.getContainingViewID());

					if (glView == null)
					{
						// TODO: investigate! view must not be null here.
//						GeneralManager.get().getLogger().log(Level.WARNING, "View in connection line manager is null!");
						continue;
					}
					
					remoteLevelElement = glView.getRemoteLevelElement();
					// views that are not rendered remote
					if (remoteLevelElement == null) {
						continue;
					}

					activeLevel = remoteLevelElement.getRemoteLevel();

					if (activeLevel == stackLevel || activeLevel == focusLevel) {
						vecTranslation = remoteLevelElement.getTransform().getTranslation();
						vecScale = remoteLevelElement.getTransform().getScale();
						rotation = remoteLevelElement.getTransform().getRotation();

						ArrayList<Vec3f> alPoints = selectedElementRep.getPoints();
						ArrayList<Vec3f> alPointsTransformed = new ArrayList<Vec3f>();

						for (Vec3f vecCurrentPoint : alPoints) {
							alPointsTransformed.add(transform(vecCurrentPoint, vecTranslation, vecScale,
								rotation, remoteLevelElement));
						}
						int iKey = selectedElementRep.getContainingViewID();

				
						
						alPointLists = hashIDTypeToViewToPointLists.get(idType).get(iKey);
						if (alPointLists == null) {
							alPointLists = new ArrayList<ArrayList<Vec3f>>();
							hashViewToPointList.put(iKey, alPointLists);
						}

						alPointLists.add(alPointsTransformed);
					}
				}

				if (hashViewToPointList.size() > 1) {
					renderLineBundling(gl, idType, new float[] { 0, 0, 0 });
					hashIDTypeToViewToPointLists.clear();
				}
			}
		}
	}
}