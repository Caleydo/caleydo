package org.caleydo.core.view.opengl.canvas.glyph.sliderview;

import gleem.linalg.Vec2f;

import java.awt.Font;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.media.opengl.GL;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.StorageSelectionManager;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.manager.ISetBasedDataDomain;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.clinical.glyph.GlyphManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;
import org.caleydo.core.view.opengl.miniview.slider.GLDistributionMiniView;
import org.caleydo.core.view.opengl.miniview.slider.GLSliderMiniView;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.renderstyle.border.BorderRenderStyleLineSolid;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Rendering the Glyph Slider View
 * 
 * @author Stefan Sauer
 */
public class GLGlyphSliderView
	extends AGLView {

	public final static String VIEW_ID = "org.caleydo.view.glyph.slider";

	private static final long serialVersionUID = 1L;

	private GlyphManager gman = null;

	private ArrayList<GLSliderMiniView> alSlider = null;
	private ArrayList<GLDistributionMiniView> alDistribution = null;
	private ArrayList<GlyphAttributeType> alGlyphAttributeTypes = null;

	private ArrayList<Vec2f> alGridPosition = null;

	private TextRenderer textRenderer = null;

	private float fSliderWidth = 1.0f;
	private float fSliderHeight = 1.0f;
	private int iMaxCols = 100000;

	private StorageSelectionManager selectionManager = null;
	
	protected ISetBasedDataDomain dataDomain;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLGlyphSliderView(GLCaleydoCanvas glCanvas, final String sLabel, final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);

		gman = generalManager.getGlyphManager();

		alSlider = new ArrayList<GLSliderMiniView>();
		alDistribution = new ArrayList<GLDistributionMiniView>();
		alGlyphAttributeTypes = new ArrayList<GlyphAttributeType>();
		alGridPosition = new ArrayList<Vec2f>();

		selectionManager = dataDomain.getStorageSelectionManager();
		viewType = VIEW_ID;
	}

	@Override
	public void init(GL gl) {
		// disable view rotation, zooming
		{
			MouseListener[] ml = parentGLCanvas.getMouseListeners();
			for (MouseListener l : ml) {
				if (l instanceof GLMouseListener) {
					((GLMouseListener) l).setNavigationModes(true, false, false);
				}
			}
		}

		{ // load ids to the selection manager
			selectionManager.resetSelectionManager();

			// ArrayList<Integer> tmpExtID = new ArrayList<Integer>(gman.getGlyphs().keySet());
			// selectionManager.initialAdd(tmpExtID);
		}

		// build slider

		textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 16), false);
		textRenderer.setColor(0, 0, 0, 1);

		Collection<GlyphAttributeType> types = gman.getGlyphAttributes();

		BorderRenderStyleLineSolid borderStyle = new BorderRenderStyleLineSolid();
		// borderStyle.setBorder((BorderRenderStyle.BORDER.RIGHT |
		// BorderRenderStyle.BORDER.LEFT), false);
		borderStyle.init(gl);

		int slidercounter = 0;
		for (GlyphAttributeType typ : types) {
			if (typ.doesAutomaticAttribute()) {
				continue;
			}

			alGlyphAttributeTypes.add(typ);

			// slider
			GLSliderMiniView slider = new GLSliderMiniView(glMouseListener, iUniqueID, slidercounter);
			alSlider.add(slider);

			slider.setBorderStyle(borderStyle);
			slider.setHeight(fSliderHeight);
			slider.setWidth(fSliderWidth);
			slider.setAxisScale(-1, typ.getMaxIndex(), 1);
			slider.setAxisScale(typ.getAttributeNames());

			// distribution
			GLDistributionMiniView dmv =
				new GLDistributionMiniView(glMouseListener, iUniqueID, slidercounter);
			alDistribution.add(dmv);
			dmv.setHeight(fSliderHeight);
			dmv.setWidth(fSliderWidth);
			// dmv.setDistributionAlign( GLDistributionMiniView.ALIGN.LEFT);

			slidercounter++;
		}
		// build slider position grid
		int x = 0;
		int y = 0;
		for (int i = 0; i < alSlider.size(); ++i) {
			Vec2f pos = new Vec2f(x * fSliderWidth, y * (fSliderHeight + 2.0f));

			alGridPosition.add(pos);
			++x;
			if (x >= iMaxCols) {
				x = 0;
				y++;
			}

		}

	}

	@Override
	public void initLocal(GL gl) {
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView, final GLMouseListener glMouseListener,
		GLInfoAreaManager infoAreaManager) {

		this.glMouseListener = glMouseListener;
		iMaxCols = 5;
		init(gl);

	}

	@Override
	public void displayLocal(GL gl) {

		display(gl);
		checkForHits(gl);
	}

	@Override
	public void displayRemote(GL gl) {
		float scale = 0.25f;
		float offset = 1.0f;

		gl.glPushMatrix();

		gl.glTranslatef(offset, offset, 0);
		gl.glScalef(scale, scale, scale);

		display(gl);
		gl.glPopMatrix();

		checkForHits(gl);

	}

	@Override
	public void display(GL gl) {
		// gl.glScalef(0.25f, 0.25f, 1f);
		// processEvents();
		gl.glPushMatrix();

		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		gl.glEnable(GL.GL_DEPTH);
		gl.glEnable(GL.GL_BLEND);

		float blockheight = 1.0f;
		float blockwidth = 1.0f;

		gl.glTranslatef(0.35f, 0.25f, 0f);
		blockheight = viewFrustum.getHeight() - 0.5f;
		blockwidth = viewFrustum.getWidth() / (alSlider.size() * 3);

		gl.glPushMatrix();
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);

		for (int i = 0; i < alDistribution.size(); ++i) {
			Vec2f position = alGridPosition.get(i);
			gl.glTranslatef(position.x(), position.y(), 0);

			GLDistributionMiniView dmv = alDistribution.get(i);
			GlyphAttributeType typ = alGlyphAttributeTypes.get(i);

			ArrayList<ArrayList<Float>> dist = typ.getDistributionNormalized();
			if (dist.size() == 2) {
				dmv.setNormalicedDistribution(dist.get(0));
				dmv.setNormalicedSelectedDistribution(dist.get(1));
			}

			dmv.setHeight(blockheight);
			dmv.setWidth(blockwidth);

			dmv.render(gl, 0, 0, 0);

			gl.glTranslatef(-0.2f, 0f, 0f);
			gl.glRotatef(90f, 0, 0, 1);
			gl.glScalef(blockwidth / 5f, blockwidth / 5f, blockwidth / 5f);

			textRenderer.begin3DRendering();
			textRenderer.draw3D(typ.getName(), 0, 0, 0, 0.1f);
			textRenderer.end3DRendering();

			gl.glRotatef(-90f, 0, 0, 1);
			gl.glScalef(5f / blockwidth, 5f / blockwidth, 5f / blockwidth);
			gl.glTranslatef(-position.x() + 0.2f, -position.y(), 0);
		}
		gl.glPopMatrix();

		gl.glPushMatrix();

		for (GLSliderMiniView s : alSlider) {
			Vec2f position = alGridPosition.get(s.getID());
			gl.glTranslatef(position.x(), position.y(), 0);
			s.setHeight(blockheight);
			s.setWidth(blockwidth);
			s.render(gl, 0, 0, 0);
			gl.glTranslatef(-position.x(), -position.y(), 0);

			// only if the slider was changed
			if (s.hasSelectionChanged()) {
				// col , value index
				HashMap<Integer, HashSet<Integer>> columnIndexMap = new HashMap<Integer, HashSet<Integer>>();

				for (int i = 0; i < alSlider.size(); ++i) {
					ArrayList<Float> ordinal = alSlider.get(i).getSelectionOrdinal();
					int internalColumn = alGlyphAttributeTypes.get(i).getInternalColumnNumber();

					for (Float o : ordinal) {
						if (!columnIndexMap.containsKey(internalColumn)) {
							columnIndexMap.put(internalColumn, new HashSet<Integer>());
						}

						columnIndexMap.get(internalColumn).add(o.intValue());
					}
				}

				selectionManager.clearSelections();

				Iterator<GlyphEntry> it = gman.getGlyphs().values().iterator();
				while (it.hasNext()) {
					GlyphEntry g = it.next();

					boolean isselected = true;
					for (int internalindex : columnIndexMap.keySet()) {
						int param = g.getParameter(internalindex);
						HashSet<Integer> p = columnIndexMap.get(internalindex);

						if (!p.contains(param)) {
							isselected = false;
						}
					}

					if (isselected) {
						selectionManager.addToType(SelectionType.SELECTION, g.getID());
					}
					else {
						selectionManager.addToType(SelectionType.DESELECTED, g.getID());
					}

				}

				generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clear(
					EIDType.EXPERIMENT_INDEX);

				SelectionCommand command =
					new SelectionCommand(ESelectionCommandType.CLEAR, SelectionType.SELECTION);
				sendSelectionCommandEvent(EIDType.EXPERIMENT_INDEX, command);

				ISelectionDelta selectionDelta = selectionManager.getDelta();
				if (selectionDelta.getAllItems().size() > 0) {
					SelectionUpdateEvent event = new SelectionUpdateEvent();
					event.setSender(this);
					event.setSelectionDelta((SelectionDelta) selectionDelta);
					event.setInfo(getShortInfo());
					eventPublisher.triggerEvent(event);
				}
			}
		}

		// ok....this should not be disabled
		// gl.glDisable(GL.GL_BLEND);

		gl.glPopMatrix();

		gl.glPopMatrix();

	}

	@Override
	public String getShortInfo() {
		return "Glyph slider";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Type: Glyph Slider View");
		sInfoText.append("GL: Showing Sliders for Glyph View");
		return sInfoText.toString();
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType, EPickingMode pickingMode, int externalID,
		Pick pick) {
		if (pickingType == EPickingType.SLIDER_SELECTION) {
			for (int i = 0; i < alSlider.size(); ++i) {
				alSlider.get(i).handleEvents(pickingType, pickingMode, externalID, pick);
			}
		}

	}

	@Override
	public void broadcastElements(EVAOperation type) {

	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		throw new IllegalStateException("Not implemented yet. Do this now!");
	}

	@Override
	public void clearAllSelections() {
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedGlyphSliderView serializedForm = new SerializedGlyphSliderView(dataDomain.getDataDomainType());
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

}
