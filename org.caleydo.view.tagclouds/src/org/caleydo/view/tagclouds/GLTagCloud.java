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
package org.caleydo.view.tagclouds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.selection.ElementConnectionInformation;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.core.view.opengl.picking.PickingType;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.tagclouds.renderstyle.TagCloudRenderStyle;
import org.eclipse.swt.widgets.Composite;

/**
 * Parallel Tag Cloud view
 * 
 * @author Alexander Lex
 */

public class GLTagCloud extends ATableBasedView {
	public static String VIEW_TYPE = "org.caleydo.view.tagclouds";

	public static String VIEW_NAME = "Tag Clouds";

	public final static int MIN_NUMBER_PIXELS_PER_DIMENSION = 100;

	private DimensionVirtualArray clippedDimensionVA;
	private int firstDimensionIndex = -1;
	private int lastDimensionIndex = -1;

	private TagCloudRenderStyle renderStyle;

	private LayoutManager layoutManager;

	private Column baseColumn;
	private Row tagCloudRow;
	private Row captionRow;
	private Row selectionRow;

	private final static int BUTTON_PREVIOUS_ID = 0;
	private final static int BUTTON_NEXT_ID = 1;

	private ArrayList<TagRenderer> selectedTagRenderers = new ArrayList<TagRenderer>();

	/** list sorted based on number of occurrences */
	private ArrayList<Pair<Integer, String>> sortedContent;

	Button previousButton = new Button(PickingType.TAG_DIMENSION_CHANGE.name(),
			BUTTON_PREVIOUS_ID, EIconTextures.HEAT_MAP_ARROW);
	Button nextButton = new Button(PickingType.TAG_DIMENSION_CHANGE.name(),
			BUTTON_NEXT_ID, EIconTextures.HEAT_MAP_ARROW);

	// private DimensionSelectionManager dimensionSelectionManager;

	/**
	 * Hash map mapping a dimension ID to a hash map of occurring strings in the
	 * dimension to the count on how many occurences of this string are
	 * contained
	 */
	private HashMap<Integer, HashMap<String, Integer>> stringOccurencesPerDimension = new HashMap<Integer, HashMap<String, Integer>>();

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLTagCloud(GLCanvas glCanvas, Composite parentComposite,
			ViewFrustum viewFrustum) {

		super(glCanvas, parentComposite, viewFrustum, VIEW_TYPE, VIEW_NAME);

		layoutManager = new LayoutManager(viewFrustum, pixelGLConverter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void initData() {

		DataTable table = dataDomain.getTable();

		RecordVirtualArray recordVA = tablePerspective.getRecordPerspective()
				.getVirtualArray();
		DimensionVirtualArray dimensionVA = tablePerspective.getDimensionPerspective()
				.getVirtualArray();

		for (Integer dimensionID : dimensionVA) {
			HashMap<String, Integer> stringOccurences = new HashMap<String, Integer>();
			stringOccurencesPerDimension.put(dimensionID, stringOccurences);

			for (Integer recordID : recordVA) {
				String string = table.getRawAsString(dimensionID, recordID);
				if (string.isEmpty() || string.equals("NaN"))
					string = "???";
				if (stringOccurences.containsKey(string)) {
					Integer count = stringOccurences.get(string);

					stringOccurences.put(string, ++count);
				} else {
					stringOccurences.put(string, 1);
				}
			}
		}

	}

	private void initMapping() {
		if (stringOccurencesPerDimension.isEmpty()) {
			initData();
		}
		Row baseRow = new Row("baseRow");
		layoutManager.setBaseElementLayout(baseRow);
		baseColumn = new Column("baseColumn");

		DimensionVirtualArray visibleDimensionVA;

		int numberOfVisibleDimensions = pixelGLConverter
				.getPixelWidthForGLWidth(viewFrustum.getWidth())
				/ MIN_NUMBER_PIXELS_PER_DIMENSION;

		if (tablePerspective.getNrDimensions() > numberOfVisibleDimensions) {
			if (clippedDimensionVA == null) {
				clippedDimensionVA = new DimensionVirtualArray(dimensionIDType);

				firstDimensionIndex = 0;
				lastDimensionIndex = numberOfVisibleDimensions - 1;
				for (int count = 0; count < numberOfVisibleDimensions; count++) {
					clippedDimensionVA.append(tablePerspective.getDimensionPerspective()
							.getVirtualArray().get(count));

				}
			} else if (clippedDimensionVA.size() > numberOfVisibleDimensions) {
				for (int count = clippedDimensionVA.size() - 1; count > numberOfVisibleDimensions; count--) {
					clippedDimensionVA.remove(count);
					lastDimensionIndex--;
				}
			}

			visibleDimensionVA = clippedDimensionVA;

			Column previousDimensionColumn = new Column("previousDimensionColumn");
			previousDimensionColumn.setPixelSizeX(15);

			ElementLayout previousButtonLayout = new ElementLayout("previousButtonLayout");
			previousButtonLayout.setPixelSizeY(20);

			previousDimensionColumn.append(previousButtonLayout);

			ButtonRenderer previousButtonRenderer = new ButtonRenderer(previousButton,
					this, textureManager, ButtonRenderer.TEXTURE_ROTATION_90);

			previousButtonLayout.setRenderer(previousButtonRenderer);

			Column nextDimensionColumn = new Column("nextDimensionColumn");
			nextDimensionColumn.setPixelSizeX(15);

			ElementLayout nextButtonLayout = new ElementLayout("nextButtonLayout");
			nextButtonLayout.setPixelSizeY(20);

			ButtonRenderer nextButtonRenderer = new ButtonRenderer(nextButton, this,
					textureManager, ButtonRenderer.TEXTURE_ROTATION_270);

			nextButtonLayout.setRenderer(nextButtonRenderer);

			nextDimensionColumn.append(nextButtonLayout);

			baseRow.append(previousDimensionColumn);
			baseRow.append(baseColumn);
			baseRow.append(nextDimensionColumn);

		} else {
			visibleDimensionVA = tablePerspective.getDimensionPerspective()
					.getVirtualArray();
			baseRow.append(baseColumn);
			clippedDimensionVA = null;
		}

		// baseColumn.setAbsoluteSizeY(3);
		tagCloudRow = new Row("tagCloudRow");

		captionRow = new Row("captionRow");
		captionRow.setPixelSizeY(15);

		selectionRow = new Row("selectionRow");
		selectionRow.setPixelSizeY(15);

		ElementLayout spacing = new ElementLayout("spacing");
		spacing.setPixelSizeY(2);
		spacing.setRatioSizeX(0);

		ElementLayout largerSpacing = new ElementLayout("spacing");
		largerSpacing.setPixelSizeY(7);
		largerSpacing.setRatioSizeX(0);
		if (detailLevel != EDetailLevel.LOW) {
			baseColumn.append(spacing);
			baseColumn.append(selectionRow);

			baseColumn.append(spacing);
			baseColumn.append(captionRow);

		}
		baseColumn.append(largerSpacing);
		baseColumn.append(tagCloudRow);
		baseColumn.append(spacing);

		for (Integer dimensionID : visibleDimensionVA) {

			ElementLayout dimensionCaptionLayout = new ElementLayout(
					"dimensionCaptionLayout");
			dimensionCaptionLayout.setGrabX(true);

			DimensionCaptionRenderer dimensionCaptionRenderer = new DimensionCaptionRenderer(
					textRenderer, dataDomain.getDimensionLabel(dimensionID));
			dimensionCaptionLayout.setRenderer(dimensionCaptionRenderer);

			captionRow.append(dimensionCaptionLayout);

			sortedContent = new ArrayList<Pair<Integer, String>>();
			HashMap<String, Integer> stringOccurences = stringOccurencesPerDimension
					.get(dimensionID);

			Column dimensionColumn = new Column();
			dimensionColumn.setGrabX(true);
			tagCloudRow.append(dimensionColumn);
			float remainingRatio = 1;

			for (Entry<String, Integer> entry : stringOccurences.entrySet()) {

				sortedContent.add(new Pair<Integer, String>(entry.getValue(), entry
						.getKey()));

			}

			Collections.sort(sortedContent);

			int pixel = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum
					.getHeight());
			int numberEntries = pixel / 27;

			ArrayList<Pair<String, Integer>> shortenedAlpahbeticalList = new ArrayList<Pair<String, Integer>>(
					numberEntries > sortedContent.size() ? sortedContent.size()
							: numberEntries);

			double totalOccurencesRendered = 0;

			for (int count = sortedContent.size() - 1; (count >= sortedContent.size()
					- numberEntries)
					&& (count > 0); count--) {
				Pair<Integer, String> sortedPair = sortedContent.get(count);
				shortenedAlpahbeticalList.add(new Pair<String, Integer>(sortedPair
						.getSecond(), sortedPair.getFirst()));
				totalOccurencesRendered += Math.log(sortedPair.getFirst());
			}

			Collections.sort(shortenedAlpahbeticalList);

			boolean isEven = true;
			for (Pair<String, Integer> entry : shortenedAlpahbeticalList) {
				ElementLayout tagLayout = new ElementLayout();
				double ratio;
				ratio = Math.log(entry.getSecond()) / totalOccurencesRendered
						* remainingRatio;
				tagLayout.setRatioSizeY((float) ratio);
				TagRenderer tagRenderer = new TagRenderer(textRenderer, entry.getFirst(),
						this);
				tagRenderer.setEven(isEven);
				if (shortenedAlpahbeticalList.size() < numberEntries)
					tagRenderer.setAllowTextScaling(true);
				isEven = !isEven;
				tagLayout.setRenderer(tagRenderer);
				dimensionColumn.append(tagLayout);

			}

			ElementLayout selectionTagLayout = new ElementLayout("selectionTagLayout");
			selectionTagLayout.setGrabX(true);
			selectionRow.setFrameColor(1, 0, 0, 1);
			selectionRow.append(selectionTagLayout);
			TagRenderer tagRenderer = new TagRenderer(textRenderer, this, dimensionID);
			selectedTagRenderers.add(tagRenderer);
			selectionTagLayout.setRenderer(tagRenderer);

		}
		layoutManager.updateLayout();
	}

	@Override
	public void init(GL2 gl) {
		displayListIndex = gl.glGenLists(1);

		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new TagCloudRenderStyle(viewFrustum);

		textRenderer = new CaleydoTextRenderer(80);
		super.renderStyle = renderStyle;
		detailLevel = EDetailLevel.HIGH;

	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				glParentView.getParentComposite().addKeyListener(glKeyListener);
			}
		});

		this.glMouseListener = glMouseListener;

		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {

		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		initMapping();
		layoutManager.updateLayout();
	}

	@Override
	public void displayRemote(GL2 gl) {
		checkForHits(gl);
		display(gl);

	}

	@Override
	public void display(GL2 gl) {

		layoutManager.render(gl);
	}

	@Override
	protected void handlePickingEvents(PickingType pickingType, PickingMode pickingMode,
			int externalID, Pick pick) {

		switch (pickingType) {
		case TAG_DIMENSION_CHANGE:
			switch (pickingMode) {
			case CLICKED:
				if (externalID == BUTTON_NEXT_ID) {
					if (lastDimensionIndex != tablePerspective.getNrDimensions() - 1) {
						firstDimensionIndex++;
						lastDimensionIndex++;
						updateClippedVA();
					}
				} else if (externalID == BUTTON_PREVIOUS_ID) {
					if (firstDimensionIndex != 0) {
						firstDimensionIndex--;
						lastDimensionIndex--;
						updateClippedVA();
					}
				}
				initMapping();

				break;

			default:
				break;
			}

			break;

		default:
			break;
		}
	}

	private void updateClippedVA() {
		clippedDimensionVA = new DimensionVirtualArray(dimensionIDType);
		for (int count = firstDimensionIndex; count <= lastDimensionIndex; count++) {
			clippedDimensionVA.append(tablePerspective.getDimensionPerspective()
					.getVirtualArray().get(count));
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTagCloudView serializedForm = new SerializedTagCloudView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta) {
		if (selectionDelta.getIDType() == recordSelectionManager.getIDType()) {
			recordSelectionManager.setDelta(selectionDelta);
			for (TagRenderer tagRenderer : selectedTagRenderers)
				tagRenderer.selectionUpdated();
		}

	}

	@Override
	public int getMinPixelHeight(EDetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 120;
		case MEDIUM:
			return 80;
		case LOW:
			return 40;
		default:
			return 50;
		}
	}

	@Override
	public int getMinPixelWidth(EDetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 100;
		case MEDIUM:
			return 100;
		case LOW:
			return Math.max(150, 30 * tablePerspective.getNrRecords());
		default:
			return 100;
		}
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		initMapping();
	}

	@Override
	protected ArrayList<ElementConnectionInformation> createElementConnectionInformation(
			IDType idType, int id) throws InvalidAttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void destroyViewSpecificContent(GL2 gl) {
		layoutManager.destroy(gl);
	}
}
