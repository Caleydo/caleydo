package org.caleydo.view.compare.state;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.IEventPublisher;
import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public abstract class ACompareViewState {

	protected TextRenderer textRenderer;
	protected TextureManager textureManager;
	protected PickingManager pickingManager;
	protected GLMouseListener glMouseListener;
	protected GLCompare view;
	protected int viewID;
	protected RenderCommandFactory renderCommandFactory;
	protected IEventPublisher eventPublisher;
	protected EDataDomain dataDomain;
	protected IUseCase useCase;
	protected DragAndDropController dragAndDropController;

	protected ArrayList<ISet> setsToCompare;
	protected SetBar setBar;
	protected ArrayList<HeatMapWrapper> heatMapWrappers;
	protected ArrayList<AHeatMapLayout> layouts;
	protected int numSetsInFocus;

	protected boolean setsChanged;

	public ACompareViewState(GLCompare view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController) {
		this.view = view;
		this.viewID = viewID;
		this.textRenderer = textRenderer;
		this.textureManager = textureManager;
		this.pickingManager = pickingManager;
		this.glMouseListener = glMouseListener;
		this.setBar = setBar;
		this.renderCommandFactory = renderCommandFactory;
		this.dataDomain = dataDomain;
		this.useCase = useCase;
		this.dragAndDropController = dragAndDropController;

		setsToCompare = new ArrayList<ISet>();
		heatMapWrappers = new ArrayList<HeatMapWrapper>();
		layouts = new ArrayList<AHeatMapLayout>();

		setsChanged = false;

		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	public abstract void init(GL gl);

	public abstract void executeDrawingPreprocessing(GL gl,
			boolean isDisplayListDirty);

	public abstract void drawDisplayListElements(GL gl);

	public abstract void drawActiveElements(GL gl);

	public abstract void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick,
			boolean isControlPressed);
	
	public abstract void setSetsToCompare(ArrayList<ISet> setsToCompare);
	
	public abstract ECompareViewStateType getStateType();
	
	public abstract void duplicateSetBarItem(int itemID);
	
	public abstract void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info);
	
	public abstract void setSetsInFocus(ArrayList<ISet> setsInFocus);

	public abstract int getMaxSetsInFocus();
	
	public abstract int getMinSetsInFocus();
	
	public int getNumSetsInFocus() {
		return numSetsInFocus;
	}

}
