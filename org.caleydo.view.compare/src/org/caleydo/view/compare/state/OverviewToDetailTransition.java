package org.caleydo.view.compare.state;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.manager.IUseCase;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.animation.MovementVector2;
import org.caleydo.core.view.opengl.util.animation.MovementVector3;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.view.compare.GLCompare;
import org.caleydo.view.compare.HeatMapWrapper;
import org.caleydo.view.compare.SetBar;
import org.caleydo.view.compare.layout.AHeatMapLayout;
import org.caleydo.view.compare.layout.HeatMapLayoutConfigurable;
import org.caleydo.view.compare.rendercommand.RenderCommandFactory;

import com.sun.opengl.util.j2d.TextRenderer;

public class OverviewToDetailTransition extends ACompareViewStateTransition {

	private HashMap<Integer, MovementVector3> captionPositions;
	private HashMap<Integer, MovementVector2> captionTextDimensions;
	private HashMap<Integer, MovementVector2> captionTextSpacing;
	private HashMap<Integer, MovementVector3> heatMapPositions;
	private HashMap<Integer, MovementVector2> heatMapDimensions;

	private Vec3f[] captionPositionOffset = { null, null };
	private Vec2f[] captionTextDimensionsOffset = { null, null };
	private Vec2f[] captionTextSpacingOffset = { null, null };
	private Vec3f[] heatMapPositionOffset = { null, null };
	private Vec2f[] heatMapDimensionsOffset = { null, null };

	private ArrayList<AHeatMapLayout> sourceLayouts;

	public OverviewToDetailTransition(GLCompare view, int viewID,
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
		captionPositions = new HashMap<Integer, MovementVector3>();
		captionTextDimensions = new HashMap<Integer, MovementVector2>();
		captionTextSpacing = new HashMap<Integer, MovementVector2>();
		heatMapDimensions = new HashMap<Integer, MovementVector2>();
		heatMapPositions = new HashMap<Integer, MovementVector3>();
	}

	@Override
	public void drawActiveElements(GL gl) {
		if (animationStarted) {
			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.drawRemoteItems(gl, glMouseListener,
						pickingManager);
			}
		}

	}

	@Override
	public void drawDisplayListElements(GL gl) {
		if (animationStarted) {
			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.drawLocalItems(gl, textureManager,
						pickingManager, glMouseListener, viewID);
			}

			IViewFrustum viewFrustum = view.getViewFrustum();

			setBar.setWidth(viewFrustum.getWidth());
			setBar.render(gl);

			if (areTargetsReached()) {
				finish();
			}
		}

		// for (int i = 0; i < heatMapWrappers.size() - 1; i++) {
		// renderTree(gl, heatMapWrappers.get(i), heatMapWrappers.get(i + 1));
		// renderOverviewRelations(gl, heatMapWrappers.get(i), heatMapWrappers
		// .get(i + 1));
		// }

	}

	protected void finish() {
		for (int i = 0; i < heatMapWrappers.size(); i++) {
			HeatMapWrapper heatMapWrapper = heatMapWrappers.get(i);
			AHeatMapLayout layout = sourceLayouts.get(i);
			heatMapWrapper.setLayout(layout);
		}

		compareViewStateController
				.setCurrentState(ECompareViewStateType.DETAIL_VIEW);
		view.setDisplayListDirty();
		animationStarted = false;
	}

	protected boolean areTargetsReached() {

		for (int i = 0; i < heatMapWrappers.size(); i++) {
			if ((!captionPositions.get(i).isTargetReached())
					|| (!captionTextDimensions.get(i).isTargetReached())
					|| (!captionTextSpacing.get(i).isTargetReached())
					|| (!heatMapDimensions.get(i).isTargetReached())
					|| (!heatMapPositions.get(i).isTargetReached())) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected void setupLayouts(double timePassed) {

		for (int i = 0; i < heatMapWrappers.size(); i++) {
			captionPositions.get(i).move(timePassed);
			captionTextDimensions.get(i).move(timePassed);
			captionTextSpacing.get(i).move(timePassed);
			heatMapDimensions.get(i).move(timePassed);
			heatMapPositions.get(i).move(timePassed);

			HeatMapLayoutConfigurable transitionLayout = (HeatMapLayoutConfigurable) layouts
					.get(i);
			transitionLayout.setCaptionLabelPosition(captionPositions.get(i)
					.getVec3f());
			transitionLayout.setCaptionLabelWidth(captionTextDimensions.get(i)
					.x());
			transitionLayout.setCaptionLabelHeight(captionTextDimensions.get(i)
					.y());
			transitionLayout
					.setCaptionLabelHorizontalSpacing(captionTextSpacing.get(i)
							.x());
			transitionLayout
					.setCaptionLabelHorizontalSpacing(captionTextSpacing.get(i)
							.y());
			transitionLayout.setOverviewHeatMapPosition(heatMapPositions.get(i)
					.getVec3f());
			transitionLayout.setOverviewHeatMapWidth(heatMapDimensions.get(i)
					.x());
			transitionLayout.setOverviewHeight(heatMapDimensions.get(i).y());
		}

		view.setDisplayListDirty();
	}

	// FIXME: Use set later on instead of itemOffset
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
			focusLayouts.add(transitionLayout);

			createMovementValues(gl, itemOffset + i, srcLayout, destLayout,
					false, false);

			if (i == 0) {
				createOffsets(true, itemOffset + i);
			} else if (i == destinationLayouts.size() - 1) {
				createOffsets(false, itemOffset + i);
			}
		}

		for (int i = 0; i < sourceLayouts.size(); i++) {
			AHeatMapLayout srcLayout = sourceLayouts.get(i);
			if (i < itemOffset) {
				createMovementValues(gl, i, srcLayout, null, true, true);
				HeatMapLayoutConfigurable transitionLayout = new HeatMapLayoutConfigurable(
						renderCommandFactory);
				transitionLayout.setLocalRenderCommands(srcLayout
						.getRenderCommandsOfLocalItems());
				transitionLayout.setRemoteRenderCommands(srcLayout
						.getRenderCommandsOfRemoteItems());
				layouts.add(transitionLayout);
			} else if (i > itemOffset + destinationLayouts.size() - 1) {
				createMovementValues(gl, i, srcLayout, null, true, false);
				HeatMapLayoutConfigurable transitionLayout = new HeatMapLayoutConfigurable(
						renderCommandFactory);
				transitionLayout.setLocalRenderCommands(srcLayout
						.getRenderCommandsOfLocalItems());
				transitionLayout.setRemoteRenderCommands(srcLayout
						.getRenderCommandsOfRemoteItems());
				layouts.add(transitionLayout);
			} else {
				layouts.add(focusLayouts.get(i - itemOffset));
			}
			heatMapWrappers.get(i).setLayout(layouts.get(i));
			layouts.get(i).setHeatMapWrapper(heatMapWrappers.get(i));
		}
		view.setDisplayListDirty();

	}

	@Override
	public ECompareViewStateType getStateType() {
		return ECompareViewStateType.OVERVIEW_TO_DETAIL_TRANSITION;
	}

	protected void createOffsets(boolean isLowerOffsets, int id) {
		int index = isLowerOffsets ? 0 : 1;
		captionPositionOffset[index] = captionPositions.get(id)
				.getRemainingVec3f();
		captionTextDimensionsOffset[index] = captionTextDimensions.get(id)
				.getRemainingVec2f();
		captionTextSpacingOffset[index] = captionTextSpacing.get(id)
				.getRemainingVec2f();
		heatMapDimensionsOffset[index] = heatMapDimensions.get(id)
				.getRemainingVec2f();
		heatMapPositionOffset[index] = heatMapPositions.get(id)
				.getRemainingVec3f();
	}

	protected void createMovementValues(GL gl, int id,
			AHeatMapLayout srcLayout, AHeatMapLayout destLayout,
			boolean isOffset, boolean isLowerOffset) {
		HeatMapWrapper heatMapWrapper = heatMapWrappers.get(id);

		int index = isLowerOffset ? 0 : 1;

		float textWidth = getCaptionLabelTextWidth(gl, heatMapWrapper
				.getCaption(), srcLayout);
		Vec3f captionStartPosition = srcLayout
				.getCaptionLabelPosition(textWidth);
		Vec3f captionTargetPosition;

		float captionStartWidth = srcLayout.getCaptionLabelWidth();
		float captionStartHeight = srcLayout.getCaptionLabelHeight();
		float captionTargetWidth;
		float captionTargetHeight;

		float captionStartSpacingX = srcLayout
				.getCaptionLabelHorizontalSpacing();
		float captionStartSpacingY = srcLayout.getCaptionLabelVerticalSpacing();

		float captionTargetSpacingX;
		float captionTargetSpacingY;

		Vec3f heatMapStartPosition = srcLayout.getOverviewHeatMapPosition();
		Vec3f heatMapTargetPosition;

		float heatMapStartWidth = srcLayout.getOverviewHeatMapWidth();
		float heatMapStartHeight = srcLayout.getOverviewHeight();
		float heatMapTargetWidth;
		float heatMapTargetHeight;

		if (isOffset) {
			captionTargetPosition = new Vec3f(captionStartPosition.x()
					+ captionPositionOffset[index].x(), captionStartPosition
					.y()
					+ captionPositionOffset[index].y(), captionStartPosition
					.z()
					+ captionPositionOffset[index].z());

			captionTargetWidth = captionStartWidth
					+ captionTextDimensionsOffset[index].x();
			captionTargetHeight = captionStartHeight
					+ captionTextDimensionsOffset[index].y();

			captionTargetSpacingX = captionStartSpacingX
					+ captionTextSpacingOffset[index].x();
			captionTargetSpacingY = captionStartSpacingY
					+ captionTextSpacingOffset[index].y();

			heatMapTargetPosition = new Vec3f(heatMapStartPosition.x()
					+ heatMapPositionOffset[index].x(), heatMapStartPosition
					.y()
					+ heatMapPositionOffset[index].y(), heatMapStartPosition
					.z()
					+ heatMapPositionOffset[index].z());

			heatMapTargetWidth = heatMapStartWidth
					+ heatMapDimensionsOffset[index].x();
			heatMapTargetHeight = heatMapStartHeight
					+ heatMapDimensionsOffset[index].y();

		} else {

			textWidth = getCaptionLabelTextWidth(gl, heatMapWrapper
					.getCaption(), destLayout);
			captionTargetPosition = destLayout
					.getCaptionLabelPosition(textWidth);

			captionTargetWidth = destLayout.getCaptionLabelWidth();
			captionTargetHeight = destLayout.getCaptionLabelHeight();

			captionTargetSpacingX = destLayout
					.getCaptionLabelHorizontalSpacing();
			captionTargetSpacingY = destLayout.getCaptionLabelVerticalSpacing();

			heatMapTargetPosition = destLayout.getOverviewHeatMapPosition();

			heatMapTargetWidth = destLayout.getOverviewHeatMapWidth();
			heatMapTargetHeight = destLayout.getOverviewHeight();
		}

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
