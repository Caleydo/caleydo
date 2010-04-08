package org.caleydo.view.matchmaker.state;

import gleem.linalg.Vec3f;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
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

import com.sun.opengl.util.j2d.TextRenderer;

public class OverviewToDetailTransition extends ACompareViewStateTransition {

	private ArrayList<AHeatMapLayout> sourceLayouts;

	public OverviewToDetailTransition(GLMatchmaker view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			EDataDomain dataDomain, IUseCase useCase,
			DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				useCase, dragAndDropController, compareViewStateController);
		animationDuration = 0.5f;
	}

	protected void finish() {
		for (int i = 0; i < heatMapWrappers.size(); i++) {
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			AHeatMapLayout layout = sourceLayouts.get(i);
			heatMapWrapper.setLayout(layout);
		}

		compareViewStateController
				.setCurrentState(ECompareViewStateType.DETAIL_VIEW);
		ACompareViewState detailViewState = compareViewStateController
				.getState(ECompareViewStateType.DETAIL_VIEW);
		detailViewState.setAllDisplayListsDirty();
		animationStarted = false;
	}

	// TODO: Use set later on instead of itemOffset
	public void initTransition(GL gl, int itemOffset) {

		if (!isInitialized)
			init(gl);
		ACompareViewState overviewState = compareViewStateController
				.getState(ECompareViewStateType.OVERVIEW);
		ACompareViewState detailViewState = compareViewStateController
				.getState(ECompareViewStateType.DETAIL_VIEW);

		setBar.setViewState(detailViewState);
		setBar.setMaxSelectedItems(detailViewState.getMaxSetsInFocus());
		setBar.setMinSelectedItems(detailViewState.getMinSetsInFocus());
		setBar.setWindowSize(detailViewState.getNumSetsInFocus());
		setBar.increaseLowestItemIndex(itemOffset);
		if (!detailViewState.isInitialized()) {
			detailViewState.init(gl);
		}
		detailViewState.setSetsInFocus(setBar.getSetsInFocus());
		detailViewState.setupLayouts();

		heatMapWrappers = overviewState.getHeatMapWrappers();
		layouts.clear();
		captionPositions.clear();
		captionTextDimensions.clear();
		captionTextSpacing.clear();
		heatMapDimensions.clear();
		heatMapPositions.clear();
		sourceLayouts = overviewState.getLayouts();
		ArrayList<AHeatMapLayout> destinationLayouts = detailViewState
				.getLayouts();
		ArrayList<AHeatMapLayout> focusLayouts = new ArrayList<AHeatMapLayout>();

		for (int i = 0; i < destinationLayouts.size(); i++) {
			AHeatMapLayout destLayout = destinationLayouts.get(i);
			AHeatMapLayout srcLayout = sourceLayouts.get(itemOffset + i);
			HeatMapLayoutConfigurable transitionLayout = new HeatMapLayoutConfigurable(
					renderCommandFactory);
			transitionLayout.setLocalRenderCommands(srcLayout
					.getRenderCommandsOfLocalItems());
			transitionLayout.setRemoteRenderCommands(srcLayout
					.getRenderCommandsOfRemoteItems());
			transitionLayout.setGroupPickingType(srcLayout
					.getGroupPickingType());
			focusLayouts.add(transitionLayout);

			createMovementValues(gl, itemOffset + i, srcLayout, destLayout);

			if (i == 0) {
				createOffsets(true, itemOffset + i);
			} else if (i == destinationLayouts.size() - 1) {
				createOffsets(false, itemOffset + i);
			}
		}

		setsInFocus.clear();

		for (int i = 0; i < sourceLayouts.size(); i++) {

			AHeatMapLayout srcLayout = sourceLayouts.get(i);
			if (i < itemOffset) {
				createMovementValuesTargetOffset(gl, i, srcLayout, true);
				HeatMapLayoutConfigurable transitionLayout = new HeatMapLayoutConfigurable(
						renderCommandFactory);
				transitionLayout.setLocalRenderCommands(srcLayout
						.getRenderCommandsOfLocalItems());
				transitionLayout.setRemoteRenderCommands(srcLayout
						.getRenderCommandsOfRemoteItems());
				transitionLayout.setGroupPickingType(srcLayout
						.getGroupPickingType());
				layouts.add(transitionLayout);
			} else if (i > itemOffset + destinationLayouts.size() - 1) {
				createMovementValuesTargetOffset(gl, i, srcLayout, false);
				HeatMapLayoutConfigurable transitionLayout = new HeatMapLayoutConfigurable(
						renderCommandFactory);
				transitionLayout.setLocalRenderCommands(srcLayout
						.getRenderCommandsOfLocalItems());
				transitionLayout.setRemoteRenderCommands(srcLayout
						.getRenderCommandsOfRemoteItems());
				transitionLayout.setGroupPickingType(srcLayout
						.getGroupPickingType());
				layouts.add(transitionLayout);
			} else {
				layouts.add(focusLayouts.get(i - itemOffset));
			}
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			heatMapWrapper.setLayout(layouts.get(i));
			layouts.get(i).setHeatMapWrapper(heatMapWrapper);
			setsInFocus.add(heatMapWrapper.getSet());
		}
		setAllDisplayListsDirty();

	}

	@Override
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION;
	}

	@Override
	public void init(GL gl) {
		isInitialized = true;

	}

	protected void createMovementValuesTargetOffset(GL gl, int id,
			AHeatMapLayout srcLayout, boolean isLowerOffset) {

		int index = isLowerOffset ? 0 : 1;

		HeatMapWrapper heatMapWrapper = heatMapWrappers.get(id);

		float textWidth = getCaptionLabelTextWidth(gl, heatMapWrapper
				.getCaption(), srcLayout);
		Vec3f captionStartPosition = srcLayout
				.getCaptionLabelPosition(textWidth);
		Vec3f captionTargetPosition = new Vec3f(captionStartPosition.x()
				+ captionPositionOffset[index].x(), captionStartPosition.y()
				+ captionPositionOffset[index].y(), captionStartPosition.z()
				+ captionPositionOffset[index].z());

		float captionStartWidth = srcLayout.getCaptionLabelWidth();
		float captionStartHeight = srcLayout.getCaptionLabelHeight();
		float captionTargetWidth = captionStartWidth
				+ captionTextDimensionsOffset[index].x();
		float captionTargetHeight = captionStartHeight
				+ captionTextDimensionsOffset[index].y();

		float captionStartSpacingX = srcLayout
				.getCaptionLabelHorizontalSpacing();
		float captionStartSpacingY = srcLayout.getCaptionLabelVerticalSpacing();
		float captionTargetSpacingX = captionStartSpacingX
				+ captionTextSpacingOffset[index].x();
		float captionTargetSpacingY = captionStartSpacingY
				+ captionTextSpacingOffset[index].y();

		Vec3f heatMapStartPosition = srcLayout.getOverviewHeatMapPosition();
		Vec3f heatMapTargetPosition = new Vec3f(heatMapStartPosition.x()
				+ heatMapPositionOffset[index].x(), heatMapStartPosition.y()
				+ heatMapPositionOffset[index].y(), heatMapStartPosition.z()
				+ heatMapPositionOffset[index].z());

		float heatMapStartWidth = srcLayout.getOverviewHeatMapWidth();
		float heatMapStartHeight = srcLayout.getOverviewHeight();
		float heatMapTargetWidth = heatMapStartWidth
				+ heatMapDimensionsOffset[index].x();
		float heatMapTargetHeight = heatMapStartHeight
				+ heatMapDimensionsOffset[index].y();

		MovementVector3 captionPosition = new MovementVector3(
				captionStartPosition, captionTargetPosition, animationDuration);
		captionPositions.put(id, captionPosition);

		MovementVector2 captionDimenstions = new MovementVector2(
				captionStartWidth, captionTargetWidth, captionStartHeight,
				captionTargetHeight, animationDuration);
		captionTextDimensions.put(id, captionDimenstions);

		MovementVector2 captionSpacings = new MovementVector2(
				captionStartSpacingX, captionTargetSpacingX,
				captionStartSpacingY, captionTargetSpacingY, animationDuration);
		captionTextSpacing.put(id, captionSpacings);

		MovementVector3 heatMapPosition = new MovementVector3(
				heatMapStartPosition, heatMapTargetPosition, animationDuration);
		heatMapPositions.put(id, heatMapPosition);

		MovementVector2 heatMapDims = new MovementVector2(heatMapStartWidth,
				heatMapTargetWidth, heatMapStartHeight, heatMapTargetHeight,
				animationDuration);
		heatMapDimensions.put(id, heatMapDims);
	}

}
