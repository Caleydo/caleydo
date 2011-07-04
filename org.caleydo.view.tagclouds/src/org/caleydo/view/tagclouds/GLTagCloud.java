package org.caleydo.view.tagclouds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.collection.storage.NumericalStorage;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.IDataDomainSetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.DetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.layout.Column;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.LayoutManager;
import org.caleydo.core.view.opengl.layout.LayoutTemplate;
import org.caleydo.core.view.opengl.layout.Row;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.button.Button;
import org.caleydo.core.view.opengl.util.button.ButtonRenderer;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.tagclouds.renderstyle.TagCloudRenderStyle;

/**
 * Parallel Tag Cloud view
 * 
 * @author Alexander Lex
 */

public class GLTagCloud extends AGLView implements IDataDomainSetBasedView,
		IViewCommandHandler, ISelectionUpdateHandler {

	public final static String VIEW_ID = "org.caleydo.view.tagclouds";

	public final static int MIN_NUMBER_PIXELS_PER_DIMENSION = 100;

	private StorageVirtualArray clippedStorageVA;
	private int firstStorageIndex = -1;
	private int lastStorageIndex = -1;

	private TagCloudRenderStyle renderStyle;

	private ISet set;

	private ASetBasedDataDomain dataDomain;

	private LayoutManager layoutManager;
	private LayoutTemplate layoutTemplate;

	private SelectionUpdateListener selectionUpdateListener = new SelectionUpdateListener();

	private Column baseColumn;
	private Row tagCloudRow;
	private Row captionRow;
	private Row selectionRow;

	private final static int BUTTON_PREVIOUS_ID = 0;
	private final static int BUTTON_NEXT_ID = 1;

	private ContentSelectionManager contentSelectionManager;

	private ArrayList<TagRenderer> selectedTagRenderers = new ArrayList<TagRenderer>();

	/** list sorted based on number of occurrences */
	private ArrayList<Pair<Integer, String>> sortedContent;

	Button previousButton = new Button(EPickingType.TAG_DIMENSION_CHANGE,
			BUTTON_PREVIOUS_ID, EIconTextures.HEAT_MAP_ARROW);
	Button nextButton = new Button(EPickingType.TAG_DIMENSION_CHANGE,
			BUTTON_NEXT_ID, EIconTextures.HEAT_MAP_ARROW);

	// private StorageSelectionManager storageSelectionManager;

	/**
	 * Hash map mapping a storage ID to a hash map of occurring strings in the
	 * storage to the count on how many occurences of this string are contained
	 */
	private HashMap<Integer, HashMap<String, Integer>> stringOccurencesPerStorage = new HashMap<Integer, HashMap<String, Integer>>();

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param label
	 * @param viewFrustum
	 */
	public GLTagCloud(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		viewType = GLTagCloud.VIEW_ID;

		layoutManager = new LayoutManager(viewFrustum);
		layoutTemplate = new LayoutTemplate();
		layoutManager.setTemplate(layoutTemplate);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void initData() {
		if (set == null)
			set = dataDomain.getSet();
		if (contentVA == null)
			contentVA = set.getContentData(Set.CONTENT).getContentVA();
		if (storageVA == null)
			storageVA = set.getStorageData(Set.STORAGE).getStorageVA();
		if (contentSelectionManager == null)
			contentSelectionManager = dataDomain.getContentSelectionManager();

		for (Integer storageID : storageVA) {
			HashMap<String, Integer> stringOccurences = new HashMap<String, Integer>();
			stringOccurencesPerStorage.put(storageID, stringOccurences);
			IStorage genericStorage = set.get(storageID);
			NumericalStorage numericalStorage = null;
			NominalStorage<String> storage = null;
			boolean isNumericalStorage = false;
			if (genericStorage instanceof NumericalStorage) {
				isNumericalStorage = true;
				numericalStorage = (NumericalStorage) genericStorage;
			}
			if (genericStorage instanceof NominalStorage<?>) {
				storage = (NominalStorage<String>) genericStorage;
				isNumericalStorage = false;
			}

			for (Integer contentID : contentVA) {
				String string = null;
				if (isNumericalStorage) {
					string = new Float(numericalStorage.getFloat(
							EDataRepresentation.RAW, contentID)).toString();
				} else {
					string = storage.getRaw(contentID);
				}
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
		if (stringOccurencesPerStorage.isEmpty()) {
			initData();
		}
		Row baseRow = new Row("baseRow");
		layoutTemplate.setBaseElementLayout(baseRow);
		baseColumn = new Column("baseColumn");

		StorageVirtualArray visibleStorageVA;

		int numberOfVisibleDimensions = parentGLCanvas.getPixelGLConverter()
				.getPixelWidthForGLWidth(viewFrustum.getWidth())
				/ MIN_NUMBER_PIXELS_PER_DIMENSION;

		if (storageVA.size() > numberOfVisibleDimensions) {
			if (clippedStorageVA == null) {
				clippedStorageVA = new StorageVirtualArray();

				firstStorageIndex = 0;
				lastStorageIndex = numberOfVisibleDimensions - 1;
				for (int count = 0; count < numberOfVisibleDimensions; count++) {
					clippedStorageVA.append(storageVA.get(count));

				}
			} else if (clippedStorageVA.size() > numberOfVisibleDimensions) {
				for (int count = clippedStorageVA.size() - 1; count > numberOfVisibleDimensions; count--) {
					clippedStorageVA.remove(count);
					lastStorageIndex--;
				}
			}

			visibleStorageVA = clippedStorageVA;

			Column previousDimensionColumn = new Column(
					"previousDimensionColumn");
			previousDimensionColumn.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			previousDimensionColumn.setPixelSizeX(15);

			ElementLayout previousButtonLayout = new ElementLayout(
					"previousButtonLayout");
			previousButtonLayout.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			previousButtonLayout.setPixelSizeY(20);
			// previousButtonLayout.setDebug(true);

			previousDimensionColumn.append(previousButtonLayout);

			ButtonRenderer previousButtonRenderer = new ButtonRenderer(
					previousButton, this, textureManager,
					ButtonRenderer.TEXTURE_ROTATION_90);

			previousButtonLayout.setRenderer(previousButtonRenderer);

			Column nextDimensionColumn = new Column("nextDimensionColumn");
			nextDimensionColumn.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
			nextDimensionColumn.setPixelSizeX(15);

			ElementLayout nextButtonLayout = new ElementLayout(
					"nextButtonLayout");
			nextButtonLayout.setPixelGLConverter(parentGLCanvas
					.getPixelGLConverter());
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
			visibleStorageVA = storageVA;
			baseRow.append(baseColumn);
			clippedStorageVA = null;
		}

		// baseColumn.setDebug(true);

		// baseColumn.setAbsoluteSizeY(3);
		tagCloudRow = new Row("tagCloudRow");
		// tagCloudRow.setDebug(true);

		captionRow = new Row("captionRow");
		captionRow.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		captionRow.setPixelSizeY(15);

		selectionRow = new Row("selectionRow");
		selectionRow.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		selectionRow.setPixelSizeY(15);
		// selectionRow.setDebug(true);

		ElementLayout spacing = new ElementLayout("spacing");
		spacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		spacing.setPixelSizeY(2);
		spacing.setRatioSizeX(0);

		ElementLayout largerSpacing = new ElementLayout("spacing");
		largerSpacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
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

		for (Integer storageID : visibleStorageVA) {

			ElementLayout storageCaptionLayout = new ElementLayout(
					"storageCaptionLayout");
			storageCaptionLayout.setGrabX(true);

			StorageCaptionRenderer storageCaptionRenderer = new StorageCaptionRenderer(
					textRenderer, set.get(storageID).getLabel());
			storageCaptionLayout.setRenderer(storageCaptionRenderer);
			// storageCaptionLayout.setDebug(true);

			captionRow.append(storageCaptionLayout);

			sortedContent = new ArrayList<Pair<Integer, String>>();
			HashMap<String, Integer> stringOccurences = stringOccurencesPerStorage
					.get(storageID);

			Column storageColumn = new Column();
			storageColumn.setGrabX(true);
			tagCloudRow.append(storageColumn);
			float remainingRatio = 1;

			for (Entry<String, Integer> entry : stringOccurences.entrySet()) {

				sortedContent.add(new Pair<Integer, String>(entry.getValue(),
						entry.getKey()));

			}

			Collections.sort(sortedContent);

			int pixel = parentGLCanvas.getPixelGLConverter()
					.getPixelHeightForGLHeight(viewFrustum.getHeight());
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
				storageColumn.append(tagLayout);

			}

			ElementLayout selectionTagLayout = new ElementLayout(
					"selectionTagLayout");
			selectionTagLayout.setGrabX(true);
			// selectionTagLayout.setDebug(true);
			selectionRow.setFrameColor(1, 0, 0, 1);
			selectionRow.append(selectionTagLayout);
			TagRenderer tagRenderer = new TagRenderer(textRenderer, this,
					storageID);
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
		glParentView.getParentGLCanvas().getParentComposite().getDisplay()
				.asyncExec(new Runnable() {
					@Override
					public void run() {
						glParentView.getParentGLCanvas().getParentComposite()
								.addKeyListener(glKeyListener);
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
	protected void handlePickingEvents(EPickingType pickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {

		switch (pickingType) {
		case TAG_DIMENSION_CHANGE:
			switch (pickingMode) {
			case CLICKED:
				if (iExternalID == BUTTON_NEXT_ID) {
					if (lastStorageIndex != storageVA.size() - 1) {
						firstStorageIndex++;
						lastStorageIndex++;
						updateClippedVA();
					}
				} else if (iExternalID == BUTTON_PREVIOUS_ID) {
					if (firstStorageIndex != 0) {
						firstStorageIndex--;
						lastStorageIndex--;
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
		clippedStorageVA = new StorageVirtualArray();
		for (int count = firstStorageIndex; count <= lastStorageIndex; count++) {
			clippedStorageVA.append(storageVA.get(count));
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
		selectionUpdateListener.setExclusiveDataDomainType(dataDomain
				.getDataDomainType());
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
	public void handleSelectionUpdate(ISelectionDelta selectionDelta,
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
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public ISet getSet() {
		return set;
	}

	public void setContentVA(ContentVirtualArray contentVA) {
		this.contentVA = contentVA;
	}

	public ContentSelectionManager getContentSelectionManager() {
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
			return Math.max(150, 30 * set.size());
		default:
			return 100;
		}
	}

	@Override
	public void setFrustum(ViewFrustum viewFrustum) {
		super.setFrustum(viewFrustum);
		initMapping();
	}
}
