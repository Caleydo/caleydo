package org.caleydo.view.tagclouds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.EVAOperation;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
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
import org.caleydo.core.view.opengl.util.text.MinSizeTextRenderer;
import org.caleydo.view.tagclouds.renderstyle.TagCloudRenderStyle;

/**
 * Parallel Tag Cloud view
 * 
 * @author Alexander Lex
 */

public class GLTagCloud extends AGLView implements IDataDomainSetBasedView,
		IViewCommandHandler, ISelectionUpdateHandler {

	public final static String VIEW_ID = "org.caleydo.view.tagclouds";

	private TagCloudRenderStyle renderStyle;

	private ISet set;

	private ASetBasedDataDomain dataDomain;

	private LayoutManager layoutManager;

	private LayoutTemplate layoutTemplate;

	private SelectionUpdateListener selectionUpdateListener = new SelectionUpdateListener();

	private Column baseColumn;
	private Row tagCloudRow;
	private Row selectionRow;

	private MinSizeTextRenderer textRenderer;

	private ContentSelectionManager contentSelectionManager;

	private ArrayList<TagRenderer> selectedTagRenderers = new ArrayList<TagRenderer>();
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
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLTagCloud(GLCaleydoCanvas glCanvas, final ViewFrustum viewFrustum) {
		super(glCanvas, viewFrustum, true);

		viewType = GLTagCloud.VIEW_ID;

		layoutManager = new LayoutManager(viewFrustum);
		layoutTemplate = new LayoutTemplate();
		layoutManager.setTemplate(layoutTemplate);
		baseColumn = new Column("baseColumn");
		// baseColumn.setDebug(true);

		tagCloudRow = new Row("tagCloudRow");
		// tagCloudRow.setDebug(true);

		selectionRow = new Row("selectionRow");
		selectionRow.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		selectionRow.setPixelSizeY(40);
//		selectionRow.setDebug(true);
		
		ElementLayout spacing = new ElementLayout("spacing");
		spacing.setPixelGLConverter(parentGLCanvas.getPixelGLConverter());
		spacing.setPixelSizeY(10);
		spacing.setRatioSizeX(0);

		baseColumn.append(spacing);
		baseColumn.append(selectionRow);
		baseColumn.append(spacing);
		baseColumn.append(tagCloudRow);
		baseColumn.append(spacing);

		// tagCloudRow.setDebug(true);
		layoutTemplate.setBaseElementLayout(baseColumn);

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
			IStorage genericStorage = (NominalStorage<String>) set.get(storageID);
			if (!(genericStorage instanceof NominalStorage<?>))
				continue;
			NominalStorage<String> storage = (NominalStorage<String>) genericStorage;

			for (Integer contentID : contentVA) {
				String string = storage.getRaw(contentID);
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

	@Override
	public void init(GL2 gl) {
		// renderStyle = new GeneralRenderStyle(viewFrustum);
		renderStyle = new TagCloudRenderStyle(viewFrustum);

		textRenderer = new MinSizeTextRenderer(80);
		super.renderStyle = renderStyle;
		detailLevel = DetailLevel.HIGH;

		for (Integer storageID : storageVA) {

			HashMap<String, Integer> stringOccurences = stringOccurencesPerStorage
					.get(storageID);

			Column storageColumn = new Column();
			storageColumn.setGrabX(true);
			tagCloudRow.append(storageColumn);
			float remainingRatio = 1;
			float maxRatio = 0.02f;
			for (Entry<String, Integer> entry : stringOccurences.entrySet()) {

				if (entry.getValue() / contentVA.size() > maxRatio) {
					remainingRatio -= maxRatio;
				}

			}

			for (Entry<String, Integer> entry : stringOccurences.entrySet()) {
				ElementLayout tagLayout = new ElementLayout();
				// tagLayout.setDebug(true);
				float ratio;
				if ((float) entry.getValue() / contentVA.size() > maxRatio)
					ratio = maxRatio;
				else
					ratio = (float) entry.getValue() / contentVA.size() * remainingRatio;

				if (ratio > maxRatio)
					ratio = maxRatio;
				tagLayout.setRatioSizeY(ratio);
				tagLayout.setRenderer(new TagRenderer(textRenderer, entry.getKey()));
				storageColumn.append(tagLayout);
			}

			ElementLayout selectionTagLayout = new ElementLayout("selectionTagLayout");
			selectionTagLayout.setGrabX(true);
			// selectionTagLayout.setDebug(true);
			selectionRow.setFrameColor(1, 0, 0, 1);
			selectionRow.append(selectionTagLayout);
			TagRenderer tagRenderer = new TagRenderer(textRenderer, this, storageID);
			selectedTagRenderers.add(tagRenderer);
			selectionTagLayout.setRenderer(tagRenderer);

		}
		layoutManager.updateLayout();
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
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		layoutManager.updateLayout();
	}

	@Override
	public void displayRemote(GL2 gl) {

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

		// TODO: Implement picking processing here!
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
		selectionUpdateListener
				.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

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
		initData();
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
}
