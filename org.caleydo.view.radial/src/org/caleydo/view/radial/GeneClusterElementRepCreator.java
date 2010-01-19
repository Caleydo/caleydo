package org.caleydo.view.radial;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;

public class GeneClusterElementRepCreator implements IElementRepCreator {

	@Override
	public void createElementRep(PartialDisc partialDisc, int iViewID,
			float fHierarchyCenterX, float fHierarchyCenterY,
			float fHierarchyCenterZ) {

		APDDrawingStrategy drawingStrategy = partialDisc.getDrawingStrategy();
		PDDrawingStrategySelected dsSelected;

		if (drawingStrategy instanceof PDDrawingStrategySelected) {
			dsSelected = (PDDrawingStrategySelected) drawingStrategy;
			float[] fArConnectionPoint = dsSelected
					.getElementRepConnectionPoint(partialDisc,
							fHierarchyCenterX, fHierarchyCenterY,
							fHierarchyCenterZ);
			ConnectedElementRepresentationManager connectedElementRepresentationManager = GeneralManager
					.get().getViewGLCanvasManager()
					.getConnectedElementRepresentationManager();
		}
	}

}
