package org.caleydo.view.tagclouds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.ITableBasedDataDomainView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
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

public class GLTagCloud extends AGLView implements ITableBasedDataDomainView,
		IViewCommandHandler, ISelectionUpdateHandler {

	public final static String VIEW_TYPE = "org.caleydo.view.tagclouds";

	public final static int MIN_NUMBER_PIXELS_PER_DIMENSION = 100;

	private DimensionVirtualArray clippedDimensionVA;
	private int firstDimensionIndex = -1;
	private int lastDimensionIndex = -1;

	private TagCloudRenderStyle renderStyle;

	private DataTable table;

	private ATableBasedDataDomain dataDomain;

	private LayoutManager layoutManager;
	private LayoutTemplate layoutTemplate;

	private SelectionUpdateListener selectionUpdateListener = new SelectionUpdateListener();

	private Column baseColumn;
	private Row tagCloudRow;
	private Row captionRow;
	private Row selectionRow;

	private final static int BUTTON_PREVIOUS_ID = 0;
	private final static int BUTTON_NEXT_ID = 1;

	private RecordSelectionManager contentSelectionManager;

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

		super(glCanvas, parentComposite, viewFrustum);

		viewType = GLTagCloud.VIEW_TYPE;

		layoutManager = new LayoutManager(viewFrustum);
		layoutTemplate = new LayoutTemplate();
		layoutManager.setTemplate(layoutTemplate);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void initData() {
		if (table == null)
			table = dataDomain.getTable();
		if (recordVA == null)
			recordVA = table.getRecordPerspective(recordPerspectiveID)
					.getVirtualArray();
		if (dimensionVA == null)
			dimensionVA = table.getDimensionPerspective(dimensionPerspectiveID)
					.getVirtualArray();
		if (contentSelectionManager == null)
			contentSelectionManager = dataDomain.getRecordSelectionManager();

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
		layoutTemplate.setBaseElementLayout(baseRow);
		baseColumn = new Column("baseColumn");

		DimensionVirtualArray visibleDimensionVA;

		int numberOfVisibleDimensions = pixelGLConverter
				.getPixelWidthForGLWidth(viewFrustum.getWidth())
				/ MIN_NUMBER_PIXELS_PER_DIMENSION;

		if (dimensionVA.size() > numberOfVisibleDimensions) {
			if (clippedDimensionVA == null) {
				clippedDimensionVA = new DimensionVirtualArray();

				firstDimensionIndex = 0;
				lastDimensionIndex = numberOfVisibleDimensions - 1;
				for (int count = 0; count < numberOfVisibleDimensions; count++) {
					clippedDimensionVA.append(dimensionVA.get(count));

				}
			} else if (clippedDimensionVA.size() > numberOfVisibleDimensions) {
				for (int count = clippedDimensionVA.size() - 1; count > numberOfVisibleDimensions; count--) {
					clippedDimensionVA.remove(count);
					lastDimensionIndex--;
				}
			}

			visibleDimensionVA = clippedDimensionVA;

			Column previousDimensionColumn = new Column(
					"previousDimensionColumn");
			previousDimensionColumn.setPixelGLConverter(pixelGLConverter);
			previousDimensionColumn.setPixelSizeX(15);

			ElementLayout previousButtonLayout = new ElementLayout(
					"previousButtonLayout");
			previousButtonLayout.setPixelGLConverter(pixelGLConverter);
			previousButtonLayout.setPixelSizeY(20);
			// previousButtonLayout.setDebug(true);

			previousDimensionColumn.append(previousButtonLayout);

			ButtonRenderer previousButtonRenderer = new ButtonRenderer(
					previousButton, this, textureManager,
					ButtonRenderer.TEXTURE_ROTATION_90);

			previousButtonLayout.setRenderer(previousButtonRenderer);

			Column nextDimensionColumn = new Column("nextDimensionColumn");
			nextDimensionColumn.setPixelGLConverter(pixelGLConverter);
			nextDimensionColumn.setPixelSizeX(15);

			ElementLayout nextButtonLayout = new ElementLayout(
					"nextButtonLayout");
			nextButtonLayout.setPixelGLConverter(pixelGLConverter);
			nextButtonLayout.setPixelSizeY(20);
			// nextButtonLayout.setDebug(true);

			ButtonRenderer nextButtonRenderer = new ButtonRenderer(nextButton,
					this, textureManager, ButtonRenderer.TEXTURE_ROTATION_270);

			nextButtonLayout.setRenderer(nextButtonRenderer);

			nextDimensionColumn.append(nextButtonLayout);

			baseRow.append(previousDimensionColumn);
			baseRow.append(baseColumn);
			baseRow.append(nextDimensionColumn);

		} else {
			visibleDimensionVA = dimensionVA;
			baseRow.append(baseColumn);
			clippedDimensionVA = null;
		}

		// baseColumn.setDebug(true);

		// baseColumn.setAbsoluteSizeY(3);
		tagCloudRow = new Row("tagCloudRow");
		// tagCloudRow.setDebug(true);

		captionRow = new Row("captionRow");
		captionRow.setPixelGLConverter(pixelGLConverter);
		captionRow.setPixelSizeY(15);

		selectionRow = new Row("selectionRow");
		selectionRow.setPixelGLConverter(pixelGLConverter);
		selectionRow.setPixelSizeY(15);
		// selectionRow.setDebug(true);

		ElementLayout spacing = new ElementLayout("spacing");
		spacing.setPixelGLConverter(pixelGLConverter);
		spacing.setPixelSizeY(2);
		spacing.setRatioSizeX(0);

		ElementLayout largerSpacing = new ElementLayout("spacing");
		largerSpacing.setPixelGLConverter(pixelGLConverter);
		largerSpacing.setPixelSizeY(7);
		largerSpacing.setRatioSizeX(0);
		if (detailLevel != DetailLevel.LOW) {
			baseColumn.append(spacing);
			baseColumn.append(selectionRow);

			baseColumn.append(spacing);
			baseColumn.append(captionRow);

		}
		baseColumn.append(largerSpacing);
		baseColumn.append(tagCloudRow);
		baseColumn.append(spacing);

		// tagCloudRow.setDebug(true);

		for (Integer dimensionID : visibleDimensionVA) {

			ElementLayout dimensionCaptionLayout = new ElementLayout(
					"dimensionCaptionLayout");
			dimensionCaptionLayout.setGrabX(true);

			DimensionCaptionRenderer dimensionCaptionRenderer = new DimensionCaptionRenderer(
					textRenderer, dataDomain.getDimensionLabel(dimensionID));
			dimensionCaptionLayout.setRenderer(dimensionCaptionRenderer);
			// dimensionCaptionLayout.setDebug(true);

			captionRow.append(dimensionCaptionLayout);

			sortedContent = new ArrayList<Pair<Integer, String>>();
			HashMap<String, Integer> stringOccurences = stringOccurencesPerDimension
					.get(dimensionID);

			Column dimensionColumn = new Column();
			dimensionColumn.setGrabX(true);
			tagCloudRow.append(dimensionColumn);
			float remainingRatio = 1;

			for (Entry<String, Integer> entry : stringOccurences.entrySet()) {

				sortedContent.add(new Pair<Integer, String>(entry.getValue(),
						entry.getKey()));

			}

			Collections.sort(sortedContent);

			int pixel = pixelGLConverter.getPixelHeightForGLHeight(viewFrustum
					.getHeight());
			int numberEntries = pixel / 27;

			ArrayList<Pair<String, Integer>> shortenedAlpahbeticalList = new ArrayList<Pair<String, Integer>>(
					numberEntries > sortedContent.size() ? sortedContent.size()
							: numberEntries);

			double totalOccurencesRendered = 0;

			for (int count = sortedContent.size() - 1; (count >= sortedContent
					.size() - numberEntries)
					&& (count > 0); count--) {
				Pair<Integer, String> sortedPair = sortedContent.get(count);
				shortenedAlpahbeticalList.add(new Pair<String, Integer>(
						sortedPair.getSecond(), sortedPair.getFirst()));
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
				TagRenderer tagRenderer = new TagRenderer(textRenderer,
						entry.getFirst(), this);
				tagRenderer.setEven(isEven);
				if (shortenedAlpahbeticalList.size() < numberEntries)
					tagRenderer.setAllowTextScaling(true);
				isEven = !isEven;
				tagLayout.setRenderer(tagRenderer);
				dimensionColumn.append(tagLayout);

			}

			ElementLayout selectionTagLayout = new ElementLayout(
					"selectionTagLayout");
			selectionTagLayout.setGrabX(true);
			// selectionTagLayout.setDebug(true);
			selectionRow.setFrameColor(1, 0, 0, 1);
			selectionRow.append(selectionTagLayout);
			TagRenderer tagRenderer = new TagRenderer(textRenderer, this,
					dimensionID);
			selectedTagRenderers.add(tagRenderer);
			selectionTagLayout.setRenderer(tagRenderer);

		}
		layoutManager.updateLayout();
	}

	@Override
	public void init(GL2 gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new TagCloudRenderStyle(viewFrustum);

		textRenderer = new CaleydoTextRenderer(80);
		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

	}

	@Override
	public void initLocal(GL2 gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL2 gl, final AGLView glParentView,
			final GLMouseListener glMouseListener) {

		// Register keyboard listener to GL2 canvas
		glParentView.getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentComposite().addKeyListener(
								glKeyListener);
					}
				});

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);
	}

	@Override
	public void displayLocal(GL2 gl) {

		pickingManager.handlePicking(this, gl);
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
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
	public String getShortInfo() {

		return "LayoutTemplate Caleydo View";
	}

	@Override
	public String getDetailedInfo() {
		return "LayoutTemplate Caleydo View";

	}

	@Override
	protected void handlePickingEvents(PickingType pickingType,
			PickingMode pickingMode, int externalID, Pick pick) {

		switch (pickingType) {
		case TAG_DIMENSION_CHANGE:
			switch (pickingMode) {
			case CLICKED:
				if (externalID == BUTTON_NEXT_ID) {
					if (lastDimensionIndex != dimensionVA.size() - 1) {
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
		clippedDimensionVA = new DimensionVirtualArray();
		for (int count = firstDimensionIndex; count <= lastDimensionIndex; count++) {
			clippedDimensionVA.append(dimensionVA.get(count));
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedTagCloudView serializedForm = new SerializedTagCloudView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "TODO: ADD INFO THAT APPEARS IN THE LOG";
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setExclusiveDataDomainID(dataDomain
				.getDataDomainID());
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class,
				selectionUpdateListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
	}

	@Override
	public void handleSelectionUpdate(SelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() == contentSelectionManager.getIDType()) {
			contentSelectionManager.setDelta(selectionDelta);
			for (TagRenderer tagRenderer : selectedTagRenderers)
				tagRenderer.selectionUpdated();
		}

	}

	@Override
	public void handleRedrawView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleUpdateView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setTable(DataTable table) {
		this.table = table;
	}

	public DataTable getTable() {
		return table;
	}

	public void setRecordVA(RecordVirtualArray recordVA) {
		this.recordVA = recordVA;
	}

	public RecordSelectionManager getContentSelectionManager() {
		return contentSelectionManager;
	}

	@Override
	public int getMinPixelHeight(DetailLevel detailLevel) {
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
	public int getMinPixelWidth(DetailLevel detailLevel) {
		switch (detailLevel) {
		case HIGH:
			return 100;
		case MEDIUM:
			return 100;
		case LOW:
			return Math.max(150, 30 * table.getMetaData().size());
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
	public void setRecordPerspectiveID(String recordPerspectiveID) {
		this.recordPerspectiveID = recordPerspectiveID;
	}

	@Override
	public void setDimensionPerspectiveID(String dimensionPerspectiveID) {
		this.dimensionPerspectiveID = dimensionPerspectiveID;
	}
}
