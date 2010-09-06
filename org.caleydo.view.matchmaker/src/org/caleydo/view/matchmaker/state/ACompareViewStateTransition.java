package org.caleydo.view.matchmaker.state;

import gleem.linalg.Vec2f;
import gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.IDCategory;
import org.caleydo.core.data.selection.ContentGroupList;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
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

public abstract class ACompareViewStateTransition extends ACompareViewState {

	private double previousTimeStamp;
	/**
	 * Determines, whether the animation has been started or not.
	 */
	protected boolean animationStarted;
	/**
	 * Determines the duration of the animation.
	 */
	protected float animationDuration;

	protected HashMap<Integer, MovementVector3> captionPositions;
	protected HashMap<Integer, MovementVector2> captionTextDimensions;
	protected HashMap<Integer, MovementVector2> captionTextSpacing;
	protected HashMap<Integer, MovementVector3> heatMapPositions;
	protected HashMap<Integer, MovementVector2> heatMapDimensions;

	protected Vec3f[] captionPositionOffset = { null, null };
	protected Vec2f[] captionTextDimensionsOffset = { null, null };
	protected Vec2f[] captionTextSpacingOffset = { null, null };
	protected Vec3f[] heatMapPositionOffset = { null, null };
	protected Vec2f[] heatMapDimensionsOffset = { null, null };

	public ACompareViewStateTransition(GLMatchmaker view, int viewID,
			TextRenderer textRenderer, TextureManager textureManager,
			PickingManager pickingManager, GLMouseListener glMouseListener,
			SetBar setBar, RenderCommandFactory renderCommandFactory,
			ASetBasedDataDomain dataDomain, DragAndDropController dragAndDropController,
			CompareViewStateController compareViewStateController) {

		super(view, viewID, textRenderer, textureManager, pickingManager,
				glMouseListener, setBar, renderCommandFactory, dataDomain,
				dragAndDropController, compareViewStateController);

		captionPositions = new HashMap<Integer, MovementVector3>();
		captionTextDimensions = new HashMap<Integer, MovementVector2>();
		captionTextSpacing = new HashMap<Integer, MovementVector2>();
		heatMapDimensions = new HashMap<Integer, MovementVector2>();
		heatMapPositions = new HashMap<Integer, MovementVector3>();
		previousTimeStamp = 0;
		animationStarted = false;
	}

	@Override
	public void drawActiveElements(GL gl) {
		if (animationStarted) {
			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.drawRemoteItems(gl, glMouseListener, pickingManager);
			}
		}
	}

	@Override
	public void buildDisplayList(GL gl) {
		if (animationStarted) {
			for (HeatMapWrapper heatMapWrapper : heatMapWrappers) {
				heatMapWrapper.drawLocalItems(gl, textureManager, pickingManager,
						glMouseListener, viewID);
			}

			ViewFrustum viewFrustum = view.getViewFrustum();

			setBar.setWidth(viewFrustum.getWidth());
			setBar.render(gl);

			contentIDToIndividualLines.clear();
			leftHeatMapWrapperToDetailBands = new HashMap<HeatMapWrapper, ArrayList<DetailBand>>();
			detailBandID = 0;

			for (int i = 0; i < heatMapWrappers.size() - 1; i++) {

				renderIndiviudalLineRelations(gl, heatMapWrappers.get(i),
						heatMapWrappers.get(i + 1));

				if (bandBundlingActive) {

					// TODO: if we put the heatmapwarpper combination with the
					// calculated detail bands in
					// a hash map we have to calculate it only once!
					calculateDetailBands(heatMapWrappers.get(i),
							heatMapWrappers.get(i + 1), false);

					renderOverviewToDetailBandRelations(gl, heatMapWrappers.get(i), true);
					renderOverviewToDetailBandRelations(gl, heatMapWrappers.get(i + 1),
							false);
					renderDetailBandRelations(gl, heatMapWrappers.get(i),
							heatMapWrappers.get(i + 1));
				}
			}

			if (areTargetsReached()) {
				finish();
			}
		}
	}

	@Override
	public void adjustPValue() {
	}

	@Override
	public void duplicateSetBarItem(int itemID) {

	}

	@Override
	public int getMaxSetsInFocus() {
		return 0;
	}

	@Override
	public int getMinSetsInFocus() {
		return 0;
	}

	@Override
	public int getNumSetsInFocus() {
		return 0;
	}

	@Override
	public void handleContentGroupListUpdate(int setID, ContentGroupList contentGroupList) {
	}

	@Override
	public void handleMouseWheel(GL gl, int amount, Point wheelPoint) {
	}

	@Override
	public void handlePickingEvents(EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick, boolean isControlPressed) {
	}

	@Override
	public void handleReplaceContentVA(int setID, String dataDomain, ContentVAType vaType) {
	}

	@Override
	public void handleSelectionCommand(IDCategory category,
			SelectionCommand selectionCommand) {
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
	}

	@Override
	public abstract void init(GL gl);

	@Override
	public boolean isInitialized() {
		return false;
	}

	@Override
	public void setSetsInFocus(ArrayList<ISet> setsInFocus) {

	}

	@Override
	public void setSetsToCompare(ArrayList<ISet> setsToCompare) {

	}

	@Override
	public void setupLayouts() {
		double currentTimeStamp = Calendar.getInstance().getTimeInMillis();

		if (!animationStarted)
			setupLayouts(0);
		else {
			double timePassed = (currentTimeStamp - previousTimeStamp) / 1000;
			setupLayouts(timePassed);
		}
		animationStarted = true;
		previousTimeStamp = currentTimeStamp;
	}

	@Override
	public void handleDragging(GL gl) {

	}

	/**
	 * Determines the scaling of a specified text that is needed for this text
	 * to fit into the label.
	 * 
	 * @param sText
	 *            Text the scaling shall be calculated for.
	 * @return Scaling factor for the specified text.
	 */
	protected float getCaptionLabelTextWidth(GL gl, String text, AHeatMapLayout layout) {

		float captionLabelHeight = layout.getCaptionLabelHeight();
		float captionLabelWidth = layout.getCaptionLabelWidth();

		Rectangle2D bounds = textRenderer.getBounds(text);
		float fScalingWidth = captionLabelWidth / (float) bounds.getWidth();
		float fScalingHeight = captionLabelHeight / (float) bounds.getHeight();

		return (float) (bounds.getWidth() * Math.min(fScalingHeight, fScalingWidth));
	}

	protected void createOffsets(boolean isLowerOffsets, int id) {
		int index = isLowerOffsets ? 0 : 1;
		captionPositionOffset[index] = captionPositions.get(id).getRemainingVec3f();
		captionTextDimensionsOffset[index] = captionTextDimensions.get(id)
				.getRemainingVec2f();
		captionTextSpacingOffset[index] = captionTextSpacing.get(id).getRemainingVec2f();
		heatMapDimensionsOffset[index] = heatMapDimensions.get(id).getRemainingVec2f();
		heatMapPositionOffset[index] = heatMapPositions.get(id).getRemainingVec3f();
	}

	protected void createMovementValues(GL gl, int id, AHeatMapLayout srcLayout,
			AHeatMapLayout destLayout) {

		HeatMapWrapper heatMapWrapper = heatMapWrappers.get(id);

		float textWidth = getCaptionLabelTextWidth(gl, heatMapWrapper.getCaption(),
				srcLayout);
		Vec3f captionStartPosition = srcLayout.getCaptionLabelPosition(textWidth);
		textWidth = getCaptionLabelTextWidth(gl, heatMapWrapper.getCaption(), destLayout);
		Vec3f captionTargetPosition = destLayout.getCaptionLabelPosition(textWidth);

		float captionStartWidth = srcLayout.getCaptionLabelWidth();
		float captionStartHeight = srcLayout.getCaptionLabelHeight();
		float captionTargetWidth = destLayout.getCaptionLabelWidth();
		float captionTargetHeight = destLayout.getCaptionLabelHeight();

		float captionStartSpacingX = srcLayout.getCaptionLabelHorizontalSpacing();
		float captionStartSpacingY = srcLayout.getCaptionLabelVerticalSpacing();
		float captionTargetSpacingX = destLayout.getCaptionLabelHorizontalSpacing();
		float captionTargetSpacingY = destLayout.getCaptionLabelVerticalSpacing();

		Vec3f heatMapStartPosition = srcLayout.getOverviewHeatMapPosition();
		Vec3f heatMapTargetPosition = destLayout.getOverviewHeatMapPosition();

		float heatMapStartWidth = srcLayout.getOverviewHeatMapWidth();
		float heatMapStartHeight = srcLayout.getOverviewHeight();
		float heatMapTargetWidth = destLayout.getOverviewHeatMapWidth();
		float heatMapTargetHeight = destLayout.getOverviewHeight();

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

	protected void setupLayouts(double timePassed) {

		for (int i = 0; i < heatMapWrappers.size(); i++) {
			captionPositions.get(i).move(timePassed);
			captionTextDimensions.get(i).move(timePassed);
			captionTextSpacing.get(i).move(timePassed);
			heatMapDimensions.get(i).move(timePassed);
			heatMapPositions.get(i).move(timePassed);

			HeatMapLayoutConfigurable transitionLayout = (HeatMapLayoutConfigurable) layouts
					.get(i);
			transitionLayout.setCaptionLabelPosition(captionPositions.get(i).getVec3f());
			transitionLayout.setCaptionLabelWidth(captionTextDimensions.get(i).x());
			transitionLayout.setCaptionLabelHeight(captionTextDimensions.get(i).y());
			transitionLayout.setCaptionLabelHorizontalSpacing(captionTextSpacing.get(i)
					.x());
			transitionLayout.setCaptionLabelHorizontalSpacing(captionTextSpacing.get(i)
					.y());
			transitionLayout.setOverviewHeatMapPosition(heatMapPositions.get(i)
					.getVec3f());
			transitionLayout.setOverviewHeatMapWidth(heatMapDimensions.get(i).x());
			transitionLayout.setOverviewHeight(heatMapDimensions.get(i).y());
		}

		setHeatMapWrapperDisplayListDirty();
	}

	protected abstract void finish();

	// protected abstract void drawActiveElements(GL gl, double timePassed);
	//
	// protected abstract void drawDisplayListElements(GL gl, double
	// timePassed);

}
