package org.caleydo.core.view.opengl.canvas.glyph.sliderview;

import gleem.linalg.Vec2f;
import java.awt.Font;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import javax.media.opengl.GL;
import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.INominalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.storage.ERawDataType;
import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.manager.event.mediator.IMediatorReceiver;
import org.caleydo.core.manager.event.mediator.IMediatorSender;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphAttributeType;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.miniview.slider.GLDistributionMiniView;
import org.caleydo.core.view.opengl.miniview.slider.GLSliderMiniView;
import org.caleydo.core.view.opengl.mouse.JoglMouseListener;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.renderstyle.border.BorderRenderStyle;
import org.caleydo.core.view.opengl.renderstyle.border.BorderRenderStyleLineSolid;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLayer;
import com.sun.opengl.util.j2d.TextRenderer;

/**
 * Rendering the Glyph Slider View
 * 
 * @author Stefan Sauer
 */
public class GLGlyphSliderView
	extends AGLEventListener
	implements IMediatorSender, IMediatorReceiver
{
	/**
	 * 
	 */
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

	private GenericSelectionManager selectionManager = null;

	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param iViewId
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLGlyphSliderView(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{

		super(iGLCanvasID, sLabel, viewFrustum, true);

		gman = (GlyphManager) generalManager.getGlyphManager();

		alSlider = new ArrayList<GLSliderMiniView>();
		alDistribution = new ArrayList<GLDistributionMiniView>();
		alGlyphAttributeTypes = new ArrayList<GlyphAttributeType>();
		alGridPosition = new ArrayList<Vec2f>();

		selectionManager = new GenericSelectionManager.Builder(EIDType.CLINICAL_ID).build();
		viewType = EManagedObjectType.GL_GLYPH_SLIDER;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void init(GL gl)
	{
		// disable view rotation, zooming
		{
			MouseListener[] ml = parentGLCanvas.getMouseListeners();
			for (MouseListener l : ml)
			{
				if (l instanceof JoglMouseListener)
					((JoglMouseListener) l).setNavigationModes(true, false, false);
			}
		}

		// find dataset
		ISet glyphData = null;

		for (ISet tmpSet : alSets)
		{
			if (tmpSet != null)
			{
				if (tmpSet.getLabel().equals("Set for clinical data"))
					glyphData = tmpSet;
			}
		}

		if (glyphData == null)
		{
			generalManager.getLogger()
					.log(Level.SEVERE, "no glyph data found - shutting down");
			return;
		}

		// load ids to selectionManager
		selectionManager.resetSelectionManager();

		try
		{
			IStorage store = glyphData.get(0);
			if (store instanceof NominalStorage
					&& store.getRawDataType() == ERawDataType.STRING)
			{
				INominalStorage<String> nominalStorage = (INominalStorage<String>) store;

				for (int i = 0; i < nominalStorage.size(); ++i)
				{
					int id = Integer.parseInt(nominalStorage.getRaw(i));
					selectionManager.initialAdd(id);
				}
			}
		}
		catch (NumberFormatException e)
		{
			generalManager.getLogger().log(Level.SEVERE,
					"first glyph data row isn't the index - shutting down");
			return;
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
		for (GlyphAttributeType typ : types)
		{
			if (typ.doesAutomaticAttribute())
				continue;

			alGlyphAttributeTypes.add(typ);

			// slider
			GLSliderMiniView slider = new GLSliderMiniView(pickingTriggerMouseAdapter,
					iUniqueID, slidercounter);
			alSlider.add(slider);

			slider.setBorderStyle(borderStyle);
			slider.setHeight(fSliderHeight);
			slider.setWidth(fSliderWidth);
			slider.setAxisScale(-1, typ.getMaxIndex(), 1);
			slider.setAxisScale(typ.getAttributeNames());

			// distribution
			GLDistributionMiniView dmv = new GLDistributionMiniView(
					pickingTriggerMouseAdapter, iUniqueID, slidercounter);
			alDistribution.add(dmv);
			dmv.setHeight(fSliderHeight);
			dmv.setWidth(fSliderWidth);
			// dmv.setDistributionAlign( GLDistributionMiniView.ALIGN.LEFT);

			slidercounter++;
		}
		// build slider position grid
		int x = 0;
		int y = 0;
		for (int i = 0; i < alSlider.size(); ++i)
		{
			Vec2f pos = new Vec2f(x * (fSliderWidth), y * (fSliderHeight + 2.0f));

			alGridPosition.add(pos);
			++x;
			if (x >= iMaxCols)
			{
				x = 0;
				y++;
			}

		}

	}

	@Override
	public void initLocal(GL gl)
	{
		init(gl);
	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLayer layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)

	{

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;
		iMaxCols = 5;
		init(gl);

	}

	@Override
	public void displayLocal(GL gl)
	{
		pickingManager.handlePicking(iUniqueID, gl, true);

		display(gl);
		checkForHits(gl);

		pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public void displayRemote(GL gl)
	{
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
	public void display(GL gl)
	{
		// gl.glScalef(0.25f, 0.25f, 1f);

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

		for (int i = 0; i < alDistribution.size(); ++i)
		{
			Vec2f position = alGridPosition.get(i);
			gl.glTranslatef(position.x(), position.y(), 0);

			GLDistributionMiniView dmv = alDistribution.get(i);
			GlyphAttributeType typ = alGlyphAttributeTypes.get(i);

			ArrayList<ArrayList<Float>> dist = typ.getDistributionNormalized();
			if (dist.size() == 2)
			{
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

		for (GLSliderMiniView s : alSlider)
		{
			Vec2f position = alGridPosition.get(s.getID());
			gl.glTranslatef(position.x(), position.y(), 0);
			s.setHeight(blockheight);
			s.setWidth(blockwidth);
			s.render(gl, 0, 0, 0);
			gl.glTranslatef(-position.x(), -position.y(), 0);

			// only if the slider was changed
			if (s.hasSelectionChanged())
			{
				// col , value index
				HashMap<Integer, HashSet<Integer>> columnIndexMap = new HashMap<Integer, HashSet<Integer>>();
				int sendParameter = Integer.parseInt(gman
						.getSetting(EGlyphSettingIDs.UPDATESENDPARAMETER));

				for (int i = 0; i < alSlider.size(); ++i)
				{
					ArrayList<Float> ordinal = alSlider.get(i).getSelectionOrdinal();
					int internalColumn = alGlyphAttributeTypes.get(i)
							.getInternalColumnNumber();

					for (Float o : ordinal)
					{
						if (!columnIndexMap.containsKey(internalColumn))
							columnIndexMap.put(internalColumn, new HashSet<Integer>());

						columnIndexMap.get(internalColumn).add(o.intValue());
					}
				}

				selectionManager.clearSelections();

				Iterator<GlyphEntry> it = gman.getGlyphs().values().iterator();
				while (it.hasNext())
				{
					GlyphEntry g = it.next();

					boolean isselected = true;
					for (int internalindex : columnIndexMap.keySet())
					{
						int param = g.getParameter(internalindex);
						HashSet<Integer> p = columnIndexMap.get(internalindex);

						if (!p.contains(param))
							isselected = false;
					}

					int id = g.getParameter(sendParameter);

					if (isselected)
						selectionManager.addToType(ESelectionType.SELECTION, id);
					else
						selectionManager.addToType(ESelectionType.DESELECTED, id);

				}

				triggerUpdate(selectionManager.getDelta());
			}
		}

		// ok....this should not be disabled
		// gl.glDisable(GL.GL_BLEND);

		gl.glPopMatrix();

		gl.glPopMatrix();

	}

	@Override
	public String getShortInfo()
	{
		return "Glyph slider";
	}

	@Override
	public String getDetailedInfo()
	{
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Type: Glyph Slider View");
		sInfoText.append("GL: Showing Sliders for Glyph View");
		return sInfoText.toString();
	}

	@Override
	protected void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
			int externalID, Pick pick)
	{
		if (pickingType == EPickingType.SLIDER_SELECTION)
		{
			for (int i = 0; i < alSlider.size(); ++i)
			{
				alSlider.get(i).handleEvents(pickingType, pickingMode, externalID, pick);
			}
		}

		pickingManager.flushHits(iUniqueID, pickingType);

	}
	@Override
	public void triggerUpdate(ISelectionDelta selectionDelta)
	{
		generalManager.getEventPublisher().handleUpdate(this, selectionDelta);
	}

	@Override
	public void handleUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void broadcastElements(ESelectionType type)
	{

	}
}
