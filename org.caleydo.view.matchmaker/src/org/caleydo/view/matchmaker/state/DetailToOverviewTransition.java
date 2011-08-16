package org.caleydo.view.matchmaker.state;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.PickingManager;
import org.caleydo.core.view.opengl.util.animation.MovementVector2;
import org.caleydo.core.view.opengl.util.animation.MovementVector3;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.matchmaker.GLMatchmaker;
import org.caleydo.view.matchmaker.HeatMapWrapper;
import org.caleydo.view.matchmaker.SetBar;
import org.caleydo.view.matchmaker.layout.AHeatMapLayout;
import org.caleydo.view.matchmaker.layout.HeatMapLayoutConfigurable;
import org.caleydo.view.matchmaker.rendercommand.RenderCommandFactory;

import com.jogamp.opengl.util.awt.TextRenderer;

public class DetailToOverviewTransition extends ACompareViewStateTransition {

	private ArrayList<AHeatMapLayout> destinationLayouts;

	public DetailToOverviewTransition(GLMatchmaker view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			ATableBasedDataDomain dataDomain, DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {
		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				dragAndDropController, compareViewStateController);
		animationDuration = 0.5f;
	}

	@Override
	protected void finish() {
		for (int i = 0; i < heatMapWrappers.size(); i++) {
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			AHeatMapLayout layout = destinationLayouts.get(i);
			heatMapWrapper.setLayout(layout);
		}
		ACompareViewState overviewState = compareViewStateController
				.getState(ECompareViewStateType.OVERVIEW);
		overviewState.setAllDisplayListsDirty();
		compareViewStateController.setCurrentState(ECompareViewStateType.OVERVIEW);
		animationStarted = false;
	}

	@Override
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.DETAIL_TO_OVERVIEW_TRANSITION;
	}

	@Override
	public void init(GL2 gl) {
		isInitialized = true;
		compareConnectionRenderer.init(gl);

		ACompareViewState overviewState = compareViewStateController
				.getState(ECompareViewStateType.OVERVIEW);
		ACompareViewState detailViewState = compareViewStateController
				.getState(ECompareViewStateType.DETAIL_VIEW);

		setBar.setViewState(overviewState);
		setBar.adjustSelectionWindowSizeCentered(overviewState.getNumSetsInFocus());
		setBar.setMaxSelectedItems(overviewState.getMaxSetsInFocus());
		setBar.setMinSelectedItems(overviewState.getMinSetsInFocus());
		if (!overviewState.isInitialized()) {
			overviewState.init(gl);
		}
		overviewState.setTablesInFocus(setBar.getTablesInFocus());

		heatMapWrappers = overviewState.getHeatMapWrappers();

		ArrayList<HeatMapWrapper> sourceHeatMapWrappers = detailViewState
				.getHeatMapWrappers();

		int indexOffset = 0;

		for (int i = 0; i < heatMapWrappers.size(); i++) {
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			if (heatMapWrapper.getTable().getID() == sourceHeatMapWrappers.get(0).getTable()
					.getID()) {
				indexOffset = i;
				break;
			}
		}

		layouts.clear();
		captionPositions.clear();
		captionTextDimensions.clear();
		captionTextSpacing.clear();
		heatMapDimensions.clear();
		heatMapPositions.clear();

		ArrayList<AHeatMapLayout> sourceLayouts = detailViewState.getLayouts();
		destinationLayouts = overviewState.getLayouts();
		ArrayList<AHeatMapLayout> focusLayouts = new ArrayList<AHeatMapLayout>();

		for (int i = 0; i < sourceLayouts.size(); i++) {
			AHeatMapLayout srcLayout = sourceLayouts.get(i);
			AHeatMapLayout destLayout = destinationLayouts.get(indexOffset + i);
			HeatMapLayoutConfigurable transitionLayout = new HeatMapLayoutConfigurable(
					renderCommandFactory);
			transitionLayout.setLocalRenderCommands(destLayout
					.getRenderCommandsOfLocalItems());
			transitionLayout.setRemoteRenderCommands(destLayout
					.getRenderCommandsOfRemoteItems());
			transitionLayout.setGroupPickingType(destLayout.getGroupPickingType());
			focusLayouts.add(transitionLayout);

			createMovementValues(gl, indexOffset + i, srcLayout, destLayout);

			if (i == 0) {
				createOffsets(true, indexOffset + i);
			} else if (i == sourceLayouts.size() - 1) {
				createOffsets(false, indexOffset + i);
			}
			srcLayout.useDendrogram(false);
		}

		for (int i = 0; i < destinationLayouts.size(); i++) {
			AHeatMapLayout destLayout = destinationLayouts.get(i);
			if (i < indexOffset) {
				createMovementValuesSourceOffset(gl, i, destLayout, true);
				HeatMapLayoutConfigurable transitionLayout = new HeatMapLayoutConfigurable(
						renderCommandFactory);
				transitionLayout.setLocalRenderCommands(destLayout
						.getRenderCommandsOfLocalItems());
				transitionLayout.setRemoteRenderCommands(destLayout
						.getRenderCommandsOfRemoteItems());
				transitionLayout.setGroupPickingType(destLayout.getGroupPickingType());
				layouts.add(transitionLayout);
			} else if (i > indexOffset + sourceLayouts.size() - 1) {
				createMovementValuesSourceOffset(gl, i, destLayout, false);
				HeatMapLayoutConfigurable transitionLayout = new HeatMapLayoutConfigurable(
						renderCommandFactory);
				transitionLayout.setLocalRenderCommands(destLayout
						.getRenderCommandsOfLocalItems());
				transitionLayout.setRemoteRenderCommands(destLayout
						.getRenderCommandsOfRemoteItems());
				transitionLayout.setGroupPickingType(destLayout.getGroupPickingType());
				layouts.add(transitionLayout);
			} else {
				layouts.add(focusLayouts.get(i - indexOffset));
			}
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			heatMapWrapper.setLayout(layouts.get(i));
			layouts.get(i).setHeatMapWrapper(heatMapWrapper);
			setsInFocus.add(heatMapWrapper.getTable());
		}

		setAllDisplayListsDirty();
	}

	protected void createMovementValuesSourceOffset(GL2 gl, int id,
			AHeatMapLayout destLayout, boolean isLowerOffset) {

		int index = isLowerOffset ? 0 : 1;

		HeatMapWrapper heatMapWrapper = heatMapWrappers.get(id);

		float textWidth = getCaptionLabelTextWidth(gl, heatMapWrapper.getCaption(),
				destLayout);
		Vec3f captionTargetPosition = destLayout.getCaptionLabelPosition(textWidth);
		Vec3f captionStartPosition = new Vec3f(captionTargetPosition.x()
				- captionPositionOffset[index].x(), captionTargetPosition.y()
				- captionPositionOffset[index].y(), captionTargetPosition.z()
				- captionPositionOffset[index].z());

		float captionTargetWidth = destLayout.getCaptionLabelWidth();
		float captionTargetHeight = destLayout.getCaptionLabelHeight();
		float captionStartWidth = captionTargetWidth
				- captionTextDimensionsOffset[index].x();
		float captionStartHeight = captionTargetHeight
				- captionTextDimensionsOffset[index].y();

		float captionTargetSpacingX = destLayout.getCaptionLabelHorizontalSpacing();
		float captionTargetSpacingY = destLayout.getCaptionLabelVerticalSpacing();
		float captionStartSpacingX = captionTargetSpacingX
				- captionTextSpacingOffset[index].x();
		float captionStartSpacingY = captionTargetSpacingY
				- captionTextSpacingOffset[index].y();

		Vec3f heatMapTargetPosition = destLayout.getOverviewHeatMapPosition();
		Vec3f heatMapStartPosition = new Vec3f(heatMapTargetPosition.x()
				- heatMapPositionOffset[index].x(), heatMapTargetPosition.y()
				- heatMapPositionOffset[index].y(), heatMapTargetPosition.z()
				- heatMapPositionOffset[index].z());

		float heatMapTargetWidth = destLayout.getOverviewHeatMapWidth();
		float heatMapTargetHeight = destLayout.getOverviewHeight();
		float heatMapStartWidth = heatMapTargetWidth - heatMapDimensionsOffset[index].x();
		float heatMapStartHeight = heatMapTargetHeight
				- heatMapDimensionsOffset[index].y();

		MovementVector3 captionPosition = new MovementVector3(captionStartPosition,
				captionTargetPosition, animationDuration);
		captionPositions.put(id, captionPosition);

		MovementVector2 captionDimenstions = new MovementVector2(captionStartWidth,
				captionTargetWidth, captionStartHeight, captionTargetHeight,
				animationDuration);
		captionTextDimensions.put(id, captionDimenstions);

		MovementVector2 captionSpacings = new MovementVector2(captionStartSpacingX,
				captionTargetSpacingX, captionStartSpacingY, captionTargetSpacingY,
				animationDuration);
		captionTextSpacing.put(id, captionSpacings);

		MovementVector3 heatMapPosition = new MovementVector3(heatMapStartPosition,
				heatMapTargetPosition, animationDuration);
		heatMapPositions.put(id, heatMapPosition);

		MovementVector2 heatMapDims = new MovementVector2(heatMapStartWidth,
				heatMapTargetWidth, heatMapStartHeight, heatMapTargetHeight,
				animationDuration);
		heatMapDimensions.put(id, heatMapDims);
	}

}
