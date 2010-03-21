package org.caleydo.view.compare.state;

import static org.caleydo.view.heatmap.dendrogram.DendrogramRenderStyle.DENDROGRAM_Z;
import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.graph.tree.Tree;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.clusterer.ClusterNode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.util.vislink.NURBSCurve;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.layout.AHeatMapLayout;

public abstract class ACompareViewState {

	public abstract void executeDrawingPreprocessing(GL gl,
			boolean isDisplayListDirty);

	public abstract void setSetsToCompare(ArrayList<ISet> setsToCompare);

	public abstract void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed);

	public abstract int getNumSetsInFocus();

	public abstract boolean isInitialized();

	public abstract void handleContentGroupListUpdate(int setID,
			ContentGroupList contentGroupList);

	public abstract void handleReplaceContentVA(int setID,
			EIDCategory idCategory, ContentVAType vaType);

	public abstract void init(GL gl);

	public abstract void drawDisplayListElements(GL gl);

	public abstract void drawActiveElements(GL gl);

	public abstract void handleStateSpecificPickingEvents(
			EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick, boolean isControlPressed);

	public abstract ECompareViewStateType getStateType();

	public abstract void duplicateSetBarItem(int itemID);

	public abstract void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info);

	public abstract void handleSelectionCommand(EIDCategory category,
			SelectionCommand selectionCommand);

	public abstract void setSetsInFocus(ArrayList<ISet> setsInFocus);

	public abstract void adjustPValue();

	public abstract int getMaxSetsInFocus();

	public abstract int getMinSetsInFocus();

	public abstract void handleMouseWheel(GL gl, int amount, Point wheelPoint);

	protected abstract void setupLayouts();
}
