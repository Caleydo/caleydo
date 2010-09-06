package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.open.Vec2i;

import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.EventPublisher;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.SelectionCommandEvent;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.ISetBasedView;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionCommandListener;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.view.glyph.GlyphRenderStyle;
import org.caleydo.view.glyph.event.GlyphChangePersonalNameEvent;
import org.caleydo.view.glyph.event.GlyphSelectionBrushEvent;
import org.caleydo.view.glyph.event.GlyphUpdatePositionModelEvent;
import org.caleydo.view.glyph.event.RemoveUnselectedGlyphsEvent;
import org.caleydo.view.glyph.event.SetPositionModelEvent;
import org.caleydo.view.glyph.gridview.data.GlyphAttributeType;
import org.caleydo.view.glyph.gridview.gridpositionmodels.GlyphGridPositionModelPlus;
import org.caleydo.view.glyph.gridview.gridpositionmodels.GlyphGridPositionModelScatterplot;
import org.caleydo.view.glyph.listener.GlyphChangePersonalNameListener;
import org.caleydo.view.glyph.listener.GlyphSelectionBrushListener;
import org.caleydo.view.glyph.listener.GlyphUpdatePositionModelListener;
import org.caleydo.view.glyph.listener.RemoveUnselectedGlyphsListener;
import org.caleydo.view.glyph.listener.SetPositionModelListener;
import org.caleydo.view.glyph.manager.GlyphManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.Screenshot;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Rendering the Glyph View
 * 
 * @author Stefan Sauer
 */
public class GLGlyph
	extends AGLView
	implements ISelectionUpdateHandler, ISelectionCommandHandler, IViewCommandHandler, ISetBasedView {

	public final static String VIEW_ID = "org.caleydo.view.glyph";

	private ISet set;

	GLGlyphGrid grid_;

	int displayList_ = -1;

	int displayListSelectionBrush_ = -1;

	boolean bRedrawDisplayListGrid = true;
	boolean bRedrawDisplayListGlyph = true;
	boolean bDrawConnectionRepLines = true;

	boolean bIsLocal = false;

	private GlyphMouseListener mouseListener_ = null;

	private GlyphKeyListener keyListener_ = null;

	private GlyphRenderStyle renderStyle = null;

	private GlyphManager gman = null;

	private ContentSelectionManager selectionManager = null;

	private GlyphEntry oldMouseOverGlyphEntry = null;

	private int iCornerOffset = 8;

	private float iViewScale = 0.15f;

	private String sLabelPersonal = "";

	private boolean bEnableSelection = false;
	private int iSelectionBrushSize = 2;
	private ArrayList<Vec2i> alSelectionBrushCornerPoints = null;

	private int iFrameBufferObject = -1;

	protected SelectionUpdateListener selectionUpdateListener;
	protected SelectionCommandListener selectionCommandListener;

	private RemoveUnselectedGlyphsListener removeUnselectedGlyphsListener;
	private SetPositionModelListener setPositionModelListener;
	private ClearSelectionsListener clearSelectionsListener;
	private GlyphSelectionBrushListener glyphSelectionBrushListener;
	private GlyphChangePersonalNameListener glyphChangePersonalNameListener;
	private GlyphUpdatePositionModelListener glyphUpdatePositionModelListener;

	protected ASetBasedDataDomain dataDomain;

	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLGlyph(GLCaleydoCanvas glCanvas, final String sLabel, final ViewFrustum viewFrustum) {
		super(glCanvas, sLabel, viewFrustum, true);
		viewType = VIEW_ID;

		alSelectionBrushCornerPoints = new ArrayList<Vec2i>();
		mouseListener_ = new GlyphMouseListener(this);
		keyListener_ = new GlyphKeyListener();
		renderStyle = new GlyphRenderStyle(viewFrustum);

		gman = generalManager.getGlyphManager();
	}

	/**
	 * Sets the used positioning model
	 * 
	 * @param iconIDs
	 *            the used Position model
	 */
	public void setPositionModel(EPositionModel iconIDs) {
		grid_.setGlyphPositionModel(iconIDs);
		forceRebuild();

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
			.clear(EIDType.EXPERIMENT_INDEX);
	}

	/**
	 * Gets the used positioning model
	 * 
	 * @return the used Position Model
	 */
	public EPositionModel getPositionModel() {
		if (grid_ != null)
			return grid_.getGlyphPositionModel();
		return EPositionModel.DISPLAY_RECTANGLE;
	}

	/**
	 * Sets the axis mapping of a position model
	 * 
	 * @param positionmodel
	 *            the used Position Model
	 * @param axisnumber
	 *            the axis on what the mapping should happen. The numbers depend on the Position Model.
	 * @param value
	 *            internal column number, what should be mapped on the axis
	 */
	public void setPositionModelAxis(EPositionModel positionmodel, int axisnumber, int value) {
		if (positionmodel == EPositionModel.DISPLAY_SCATTERPLOT) {
			GlyphGridPositionModelScatterplot model =
				(GlyphGridPositionModelScatterplot) grid_.getGlyphPositionModel(positionmodel);
			switch (axisnumber) {
				case 0:
					model.setParameterWithInternalColnumX(value);
					break;
				case 1:
					model.setParameterWithInternalColnumY(value);
					break;
			}
		}
		if (positionmodel == EPositionModel.DISPLAY_PLUS) {
			GlyphGridPositionModelPlus model =
				(GlyphGridPositionModelPlus) grid_.getGlyphPositionModel(positionmodel);
			switch (axisnumber) {
				case 0:
					model.setParameterWithInternalColnumX(value);
					break;
				case 1:
					model.setParameterWithInternalColnumY(value);
					break;
			}
		}
		this.forceRebuild();
	}

	/**
	 * Sets the Selection Brush
	 * 
	 * @param size
	 *            Size of the Selection Brush
	 */
	public void setSelectionBrush(int size) {
		if (size <= 0) {
			bEnableSelection = false;
			ArrayList<Integer> ids = null;
			if (size == -1)
				ids = grid_.selectAll();
			if (size == -2)
				ids = grid_.deSelectAll();

			selectionManager.clearSelections();
			alSelectionBrushCornerPoints.clear();

			if (ids != null)
				for (int id : ids)
					selectionManager.addToType(SelectionType.SELECTION, id);

			bDrawConnectionRepLines = false;

			triggerSelectionUpdate();

			bDrawConnectionRepLines = true;
			bRedrawDisplayListGlyph = true;
		}
		else {
			bEnableSelection = true;
			iSelectionBrushSize = size;
		}
	}

	/**
	 * Triggers the selection Update.
	 */
	private void triggerSelectionUpdate() {
		ISelectionDelta selectionDelta = selectionManager.getDelta();
		if (selectionDelta.getAllItems().size() > 0) {
			handleConnectedElementRep(selectionDelta);

			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta((SelectionDelta) selectionDelta);
			event.setInfo(getShortInfo());
			event.setDataDomainType(dataDomain.getDataDomainType());
			eventPublisher.triggerEvent(event);
		}
	}

	@Override
	public void init(GL gl) {

		grid_ = new GLGlyphGrid(renderStyle, !this.isRenderedRemote());

		selectionManager = dataDomain.getContentSelectionManager();

		set = dataDomain.getSet();

		grid_.loadData(set);

		// init glyph gl
		forceRebuild();

		{ // load ids to the selection manager
			selectionManager.resetSelectionManager();

			// ArrayList<Integer> tmpExtID = new ArrayList<Integer>(gman.getGlyphs().keySet());
			// selectionManager.initialAdd(tmpExtID);
		}

		grid_.selectAll();
	}

	@Override
	public void initLocal(GL gl) {
		bIsLocal = true;

		float fInitZoom = -10f;

		// position /scale camera
		Rotf t = new Rotf();
		t.set(new Vec3f(-1, 0, 0), (float) (Math.PI / 4.0 - 2.0 * Math.PI * -(fInitZoom + 3) / 360.0));
		this.getViewCamera().setCameraRotation(t);
		this.getViewCamera().addCameraPosition(new Vec3f(0, 0, fInitZoom));

		init(gl);

		// disable standard mouse movement (DON't remove the listeners, it will
		// affect the picking!
		{
			MouseListener[] ml = parentGLCanvas.getMouseListeners();
			for (MouseListener l : ml)
				if (l instanceof GLMouseListener)
					((GLMouseListener) l).setNavigationModes(false, false, false);
		}

		// Register specialized mouse wheel listener
		if (mouseListener_ != null) {
			parentGLCanvas.addMouseListener(mouseListener_);
			mouseListener_.setNavigationModes(true, false, true);
		}

		if (mouseListener_ != null) {
			parentGLCanvas.addMouseMotionListener(mouseListener_);
			parentGLCanvas.addMouseWheelListener(mouseListener_);

			parentGLCanvas.addKeyListener(keyListener_);
		}

		grid_.setGlyphPositionModel(EPositionModel.DISPLAY_SCATTERPLOT);

	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView, final GLMouseListener glMouseListener,
		GLInfoAreaManager infoAreaManager) {

		bIsLocal = false;

		// Collection<GLCaleydoCanvas> cc = generalManager.getViewGLCanvasManager().getAllGLCanvasUsers();

		// FIXXXME: YOU SHOULD NOT ADD THE KEY LISTENER TO ALL CANVAS OBJECTS!!!!!
		// for (GLCaleydoCanvas c : cc) {
		// c.addKeyListener(keyListener_);
		// }

		init(gl);

		grid_.setGridSize(30, 60);
		grid_.setGlyphPositionModel(EPositionModel.DISPLAY_SCATTERPLOT);
		// grid_.setGlyphPositionModel(EPositionModel.DISPLAY_SCATTERPLOT);
		// grid_.setGlyphPositionModel(EPositionModel.DISPLAY_PLUS);
	}

	@Override
	public void displayLocal(GL gl) {

		pickingManager.handlePicking(this, gl);

		display(gl);
		checkForHits(gl);

	}

	@Override
	public void displayRemote(GL gl) {
		display(gl);
		checkForHits(gl);
	}

	@Override
	public void display(GL gl) {
		// processEvents();
		if (grid_ == null) {
			renderSymbol(gl);
			return;
		}

		if (grid_.getGlyphList() == null) {
			renderSymbol(gl);
			return;
		}

		if (grid_.getGlyphList().keySet().size() == 0) {
			// This should not be - it only happens, if the glyph view didn't get an clinical set
			grid_.loadData(null);
			// renderSymbol(gl);
			return;
		}

		// switch between detail level
		if (mouseListener_ != null && !isRenderedRemote()) {
			float height = -mouseListener_.getCameraHeight();

			if (height > 20) {
				if (grid_.getGlyphGenerator().getDetailLevel() != GLGlyphGenerator.DETAILLEVEL.LEVEL_MIN) {
					grid_.getGlyphGenerator().setDetailLevel(GLGlyphGenerator.DETAILLEVEL.LEVEL_MIN);
					bRedrawDisplayListGlyph = true;
				}
			}
			else if (height < 4) {
				if (grid_.getGlyphGenerator().getDetailLevel() != GLGlyphGenerator.DETAILLEVEL.LEVEL_MAX) {
					grid_.getGlyphGenerator().setDetailLevel(GLGlyphGenerator.DETAILLEVEL.LEVEL_MAX);
					bRedrawDisplayListGlyph = true;
				}
			}
			else {
				if (grid_.getGlyphGenerator().getDetailLevel() != GLGlyphGenerator.DETAILLEVEL.LEVEL_MID) {
					grid_.getGlyphGenerator().setDetailLevel(GLGlyphGenerator.DETAILLEVEL.LEVEL_MID);
					bRedrawDisplayListGlyph = true;
				}
			}
		}

		if (bRedrawDisplayListGrid || bRedrawDisplayListGlyph)
			initDisplayLists(gl);

		gl.glPushMatrix();

		// rotate grid
		gl.glRotatef(45f, 0, 0, 1);

		float scale = iViewScale;

		gl.glScalef(scale, scale, scale);
		gl.glTranslatef(iCornerOffset, 0, 0f);

		if (displayList_ < 0 || bRedrawDisplayListGlyph)
			redrawView(gl);

		int displayListGrid = grid_.getGridLayout(bIsLocal);
		if (displayListGrid >= 0)
			gl.glCallList(displayListGrid);

		if (displayList_ >= 0)
			gl.glCallList(displayList_);

		if (alSelectionBrushCornerPoints != null)
			renderSelectionBrush(gl);

		gl.glTranslatef(-7.0f, 0.0f, 0f);
		gl.glRotatef(-45f, 0, 0, 1);

		gl.glPopMatrix();

		// if (System.currentTimeMillis() - ticker > 10000)
		// {
		// ticker = System.currentTimeMillis();
		// renderToImage(gl, new File("d:/" + System.currentTimeMillis() +
		// ".png"), -1, -1);
		// }
	}

	/**
	 * Inits all display lists (for glyphs and grid representations)
	 * 
	 * @param gl
	 *            GL Context
	 */
	private void initDisplayLists(GL gl) {
		gl.glPushMatrix();
		Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();

		GLGlyphGenerator generator = grid_.getGlyphGenerator();

		while (git.hasNext()) {
			GlyphEntry e = git.next();
			e.generateGLLists(gl, generator);
		}
		gl.glPopMatrix();

		if (bRedrawDisplayListGrid) {
			gl.glPushMatrix();
			grid_.buildGrids(gl);
			grid_.setGlyphPositionModel();
			gl.glPopMatrix();
		}

		bRedrawDisplayListGrid = false;
	}

	/**
	 * Redraws the view
	 * 
	 * @param gl
	 *            GL Context
	 */
	private void redrawView(GL gl) {
		gl.glPushMatrix();

		gl.glDeleteLists(displayList_, 1);

		displayList_ = gl.glGenLists(1);
		gl.glNewList(displayList_, GL.GL_COMPILE);

		if (grid_.getGlyphList() == null) {
			// something is wrong (no data?)
			gl.glPopMatrix();
			return;
		}

		Iterator<GlyphEntry> git = grid_.getGlyphList().values().iterator();

		while (git.hasNext()) {
			GlyphEntry ge = git.next();

			Vec2i pos = grid_.getGridPosition(ge);
			if (pos == null)
				continue;

			gl.glTranslatef(pos.x(), -(float) pos.y(), 0f);
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GLYPH_FIELD_SELECTION,
				ge.getID()));
			gl.glCallList(ge.getGlList(gl));
			gl.glPopName();
			gl.glTranslatef(-(float) pos.x(), pos.y(), 0f);
		}
		gl.glEndList();

		bRedrawDisplayListGlyph = false;

		gl.glPopMatrix();
	}

	/**
	 * Renders the selection brush
	 * 
	 * @param gl
	 *            GL Context
	 */
	private void renderSelectionBrush(GL gl) {
		int size = alSelectionBrushCornerPoints.size();

		Vec2i oldpoint = null;
		if (size > 1)
			oldpoint = alSelectionBrushCornerPoints.get(size - 1);

		for (Vec2i point : alSelectionBrushCornerPoints) {
			gl.glPushMatrix();
			gl.glTranslatef(point.x(), -point.y(), 0);

			gl.glLineWidth(3);

			if (!keyListener_.isControlDown())
				gl.glColor4f(0, 1, 0, 1);
			else
				gl.glColor4f(1, 0, 0, 1);

			gl.glBegin(GL.GL_LINES);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 0, 1);
			gl.glEnd();

			if (oldpoint != null) {
				int x = point.x() - oldpoint.x();
				int y = point.y() - oldpoint.y();

				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(0, 0, 1);
				gl.glVertex3f(-x, y, 1);
				gl.glEnd();

				gl.glBegin(GL.GL_LINES);
				gl.glVertex3f(0, 0, 0);
				gl.glVertex3f(-x, y, 0);
				gl.glEnd();
			}
			gl.glPopMatrix();

			oldpoint = point;
		}
	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL gl) {
		float fXButtonOrigin = 1.3f * renderStyle.getScaling();
		float fYButtonOrigin = 1.3f * renderStyle.getScaling();
		Texture tempTexture = textureManager.getIconTexture(gl, EIconTextures.GLYPH_SYMBOL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// gl.glTranslatef(0.2f, 0.2f, 0);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glColor4f(1f, 1, 1, 1f);
		gl.glBegin(GL.GL_POLYGON);

		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin, fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(fXButtonOrigin * 2, 2 * fYButtonOrigin, 0.01f);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fXButtonOrigin * 2, fYButtonOrigin, 0.01f);
		gl.glEnd();
		gl.glPopAttrib();
		tempTexture.disable();
	}

	/**
	 * This method saves either an high resolution screenshot, or the current view if no framebuffer is
	 * available. You can either give it an resolution (no aspect ratio correction!) or use -1 as width &
	 * height to use 4096x? (aspect ratio of the screen). It uses ImagieIO, so the file extension defines the
	 * written picture format.
	 * 
	 * @param gl
	 *            context
	 * @param file
	 *            to write
	 * @param width
	 *            of the picture
	 * @param height
	 *            of the picture
	 */
	@SuppressWarnings("unused")
	private void renderToImage(GL gl, File outFile, int width, int height) {
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		if (height <= 0 && width <= 0) {
			width = 4096;

			float aspect = (float) viewport[3] / (float) viewport[2];
			height = (int) (width * aspect);
		}

		if (iFrameBufferObject < 0) {
			iFrameBufferObject = createFrameBufferObject(gl, width, height);
		}

		// we don't have an framebuffer, so we just save the current screen
		if (iFrameBufferObject < 0) {
			try {
				Screenshot.writeToFile(outFile, viewport[2], viewport[3]);
			}
			catch (GLException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("damn");
			return;
		}

		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, iFrameBufferObject);

		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.5f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		// extend viewport to high resolution
		gl.glViewport(0, 0, width, height);

		display(gl);

		try {
			Screenshot.writeToFile(outFile, width, height);
		}
		catch (GLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// reset viewport
		gl.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);

		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
	}

	/**
	 * Creates a frame buffer object with the given size. This is from an example of Nehe Productions Lession
	 * 36 (Radial Blur & Rendering To A Texture).
	 * 
	 * @return the newly created frame buffer object is or -1 if a frame buffer object could not be created
	 */
	private int createFrameBufferObject(GL gl, int width, int height) {
		// Create the FBO
		int[] frameBuffer = new int[1];
		gl.glGenFramebuffersEXT(1, frameBuffer, 0);
		gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, frameBuffer[0]);

		// Create a TEXTURE_SIZE x TEXTURE_SIZE RGBA texture that will be used
		// as color attachment for the fbo.
		int[] colorBuffer = new int[1];
		gl.glGenTextures(1, colorBuffer, 0); // Create 1 Texture
		gl.glBindTexture(GL.GL_TEXTURE_2D, colorBuffer[0]); // Bind The Texture
		gl.glTexImage2D(
			// Build Texture Using Information In data
			GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
			BufferUtil.newByteBuffer(width * height * 4));
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

		// Attach the texture to the frame buffer as the color attachment. This
		// will cause the results of rendering to the FBO to be written in the
		// blur texture.
		gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D,
			colorBuffer[0], 0);

		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);

		// Create a 24-bit TEXTURE_SIZE x TEXTURE_SIZE depth buffer for the FBO.
		// We need this to get correct rendering results.
		int[] depthBuffer = new int[1];
		gl.glGenRenderbuffersEXT(1, depthBuffer, 0);
		gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, depthBuffer[0]);
		gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT, GL.GL_DEPTH_COMPONENT24, width, height);

		// Attach the newly created depth buffer to the FBO.
		gl.glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_DEPTH_ATTACHMENT_EXT,
			GL.GL_RENDERBUFFER_EXT, depthBuffer[0]);

		// Make sure the framebuffer object is complete (i.e. set up correctly)
		int status = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		if (status == GL.GL_FRAMEBUFFER_COMPLETE_EXT)
			return frameBuffer[0];
		else {
			// No matter what goes wrong, we simply delete the frame buffer
			// object
			// This switch statement simply serves to list all possible error
			// codes
			switch (status) {
				case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
					// One of the attachments is incomplete
				case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
					// Not all attachments have the same size
				case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
					// The desired read buffer has no attachment
				case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
					// The desired draw buffer has no attachment
				case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
					// Not all color attachments have the same internal format
				case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
					// No attachments have been attached
				case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
					// The combination of internal formats is not supported
				case GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT:
					// This value is no longer in the EXT_framebuffer_object
					// specification
				default:
					// Delete the color buffer texture
					gl.glDeleteTextures(1, colorBuffer, 0);
					// Delete the depth buffer
					gl.glDeleteRenderbuffersEXT(1, depthBuffer, 0);
					// Delete the FBO
					gl.glDeleteFramebuffersEXT(1, frameBuffer, 0);
					return -1;
			}
		}
	}

	/**
	 * Gives the Position of a Glyph in relation to the "normal" World, without view interferences
	 * 
	 * @param glyph
	 *            The Glyph you are interested in
	 * @return the center position of the glyph
	 */
	private Vec3f getGlyphPosition(GlyphEntry glyph) {
		Vec2i gridpos = grid_.getGridPosition(glyph);
		gridpos.setY(-gridpos.y());

		// rotate
		double w = Math.PI / 4.0;
		float sxx = (float) (gridpos.x() * Math.cos(w) - gridpos.y() * Math.sin(w));
		float syy = (float) (gridpos.x() * Math.sin(w) + gridpos.y() * Math.cos(w));

		// translate grid offset
		sxx += (float) (iCornerOffset * Math.cos(w));
		syy += (float) (iCornerOffset * Math.sin(w));

		// translate to glyph center
		syy += (float) Math.sin(w);

		Vec3f realpos = new Vec3f();
		realpos.set(sxx, syy, 1.0f);
		realpos.scale(iViewScale);

		return realpos;
	}

	/**
	 * This method handles selection per brushing. It selects glyphs around the given Glyph, acording to the
	 * Brush size (iSelectionBrushSize).
	 * 
	 * @param glpyh
	 *            a Glyph Entry (the middle one of the brush)
	 * @param selectDeselect
	 *            select (true) or deselect (false) mode
	 */
	private void brushSelect(GlyphEntry glyph, boolean selectDeselect) {
		Vec2i pos = grid_.getPosition(glyph);
		alSelectionBrushCornerPoints.clear();

		int k = (iSelectionBrushSize - 1) * 2 + 1;
		int[] selectedGridHeight = new int[k];

		if (pos.y() % 2 == 0) {
			int u = 0, v = k - 1;
			// generates 1,3,4,2,0
			for (int i = 0; i < k; ++i) {
				if (i % 2 == 0) {
					selectedGridHeight[v] = i;
					v--;
				}
				else {
					selectedGridHeight[u] = i;
					u++;
				}
			}

		}
		else {
			int u = 0, v = k - 1;
			// generates 0,2,4,3,1
			for (int i = 0; i < k; ++i) {
				if (i % 2 == 0) {
					selectedGridHeight[u] = i;
					u++;
				}
				else {
					selectedGridHeight[v] = i;
					v--;
				}
			}

		}

		ArrayList<Integer> glyphIDs = new ArrayList<Integer>();
		for (int x = -iSelectionBrushSize + 1; x <= iSelectionBrushSize - 1; ++x) {
			int ymax = selectedGridHeight[x + iSelectionBrushSize - 1];
			for (int y = -ymax; y <= ymax; ++y)
				glyphIDs.add(grid_.getGlyphID(pos.x() + x, pos.y() - y));
		}

		if (selectDeselect) {
			for (int id : glyphIDs)
				if (id >= 0)
					grid_.getGlyph(id).select();
		}
		else {
			for (int id : glyphIDs)
				if (id >= 0)
					grid_.getGlyph(id).deSelect();
		}

		Vec2i gridPos = grid_.getGridPosition(glyph);
		Vec2i temp = null;

		{ // left
			temp = new Vec2i(gridPos);
			temp.setX(gridPos.x() - (iSelectionBrushSize - 1));
			temp.setY(gridPos.y() - (iSelectionBrushSize - 0));
			alSelectionBrushCornerPoints.add(temp);
		}

		{ // top
			temp = new Vec2i(gridPos);
			temp.setX(gridPos.x() + iSelectionBrushSize - 0);
			temp.setY(gridPos.y() - (iSelectionBrushSize - 0));
			alSelectionBrushCornerPoints.add(temp);
		}

		{ // right
			temp = new Vec2i(gridPos);
			temp.setX(gridPos.x() + iSelectionBrushSize - 0);
			temp.setY(gridPos.y() + iSelectionBrushSize - 1);
			alSelectionBrushCornerPoints.add(temp);
		}

		{ // bottom
			temp = new Vec2i(gridPos);
			temp.setX(gridPos.x() - (iSelectionBrushSize - 1));
			temp.setY(gridPos.y() + iSelectionBrushSize - 1);
			alSelectionBrushCornerPoints.add(temp);
		}
	}

	@Override
	public String getShortInfo() {
		if (sLabelPersonal != null)
			return "Glpyh - " + sLabelPersonal;

		return "Glpyh";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Type: Glyph Map");
		sInfoText.append("GL: Showing Glyphs for clinical data");
		return sInfoText.toString();
	}

	@Override
	protected void handlePickingEvents(EPickingType pickingType, EPickingMode pickingMode, int iExternalID,
		Pick pick) {

		if (pickingType == EPickingType.GLYPH_FIELD_SELECTION) {
			switch (pickingMode) {
				case MOUSE_OVER:
					GlyphEntry g = grid_.getGlyph(iExternalID);

					if (g == null) {
						generalManager.getLogger().log(
							new Status(IStatus.WARNING, GeneralManager.PLUGIN_ID, "Glyph with external ID "
								+ iExternalID + " not found!"));
						return;
					}

					// nothing changed, we don't need to do anything
					if (g == oldMouseOverGlyphEntry)
						return;

					selectionManager.clearSelection(SelectionType.MOUSE_OVER);
					if (oldMouseOverGlyphEntry != null)
						selectionManager.addToType(SelectionType.NORMAL, oldMouseOverGlyphEntry.getID());

					selectionManager.addToType(SelectionType.MOUSE_OVER, iExternalID);

					oldMouseOverGlyphEntry = g;

					selectionManager.addConnectionID(
						generalManager.getIDManager().createID(EManagedObjectType.CONNECTION), iExternalID);

					if (bEnableSelection)
						brushSelect(g, !keyListener_.isControlDown());

					generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager()
						.clear(EIDType.EXPERIMENT_INDEX);

					SelectionCommand command =
						new SelectionCommand(ESelectionCommandType.CLEAR, SelectionType.MOUSE_OVER);
					sendSelectionCommandEvent(EIDType.EXPERIMENT_INDEX, command);

					triggerSelectionUpdate();

					// only the glyphs need to be redrawn
					bRedrawDisplayListGlyph = true;

					break;
				default:
					// System.out.println("picking Mode " + pickingMode.toString());

			}
		}
	}

	@Override
	public void handleSelectionUpdate(ISelectionDelta selectionDelta, boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() != EIDType.EXPERIMENT_INDEX)
			return;

		selectionManager.clearSelections();
		selectionManager.setDelta(selectionDelta);

		if (selectionDelta.size() > 0)
			handleConnectedElementRep(selectionDelta);

		forceRebuild();

		GlyphEntry actualGlyph = null;
		for (SelectionDeltaItem item : selectionDelta) {
			actualGlyph = grid_.getGlyph(item.getPrimaryID());

			if (actualGlyph == null)
				continue;

			if (item.getSelectionType() == SelectionType.DESELECTED && actualGlyph.isSelected())
				actualGlyph.deSelect();

			if (item.getSelectionType() == SelectionType.SELECTION && !actualGlyph.isSelected())
				actualGlyph.select();

		}

	}

	@Override
	public void handleSelectionCommand(EIDCategory category, SelectionCommand selectionCommand) {
		if (EIDCategory.EXPERIMENT == category)
			selectionManager.executeSelectionCommand(selectionCommand);
	}

	/**
	 * This method forces a rebuild of every display list in this view
	 */
	public void forceRebuild() {
		bRedrawDisplayListGrid = true;
		bRedrawDisplayListGlyph = true;
	}

	@Override
	public void broadcastElements(EVAOperation type) {

	}

	/**
	 * Handles the creation of {@link SelectedElementRep} uses selection delta
	 */
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta) {
		if (!bDrawConnectionRepLines)
			return;

		ConnectedElementRepresentationManager connectedElementRepresentationManager =
			generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager();

		Vec3f vecGlyphPos;
		GlyphEntry actualGlyph = null;

		Collection<SelectionDeltaItem> selection = selectionDelta.getAllItems();
		for (SelectionDeltaItem item : selection) {
			actualGlyph = grid_.getGlyph(item.getPrimaryID());

			if (actualGlyph == null)
				continue;

			if (item.getSelectionType() != SelectionType.MOUSE_OVER)
				continue;

			vecGlyphPos = getGlyphPosition(actualGlyph);

			SelectedElementRep rep =
				new SelectedElementRep(EIDType.EXPERIMENT_INDEX, iUniqueID, vecGlyphPos.x(), vecGlyphPos.y(),
					vecGlyphPos.z());

			for (Integer iConnectionID : item.getConnectionIDs())
				connectedElementRepresentationManager.addSelection(iConnectionID, rep);

		}
	}

	/**
	 * This removes all unselected Glyphs from the Grid. It does NOT delete them.
	 */
	public void removeUnselected() {
		grid_.loadData(null);
		forceRebuild();
	}

	/**
	 * Sets the Glyph Views Personal Name. This is used for Labeling the view.
	 * 
	 * @param name
	 *            The new Name
	 */
	public void setPersonalName(String name) {
		sLabelPersonal = name;
	}

	/**
	 * Returns the Glyph Views Personal Name
	 * 
	 * @return The Name
	 */
	public String getPersonalName() {
		return sLabelPersonal;
	}

	/**
	 * Temporary fix Exporting Clinical Data
	 * 
	 * @param addHeader
	 *            you want a header?
	 * @param selectionOnly
	 *            export selected glyphs only
	 * @param originalData
	 *            unused now
	 * @return
	 */
	@Deprecated
	public void exportAsCSV(final String sFileName, final boolean addHeader, final boolean selectionOnly,
		final boolean originalData) {
		Collection<GlyphEntry> list = grid_.getGlyphList().values();
		// String tab = "; ";
		String tab = "\t";

		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFileName)));

			// make header
			if (addHeader) {
				out.print("ID");

				for (int i = 1; i < gman.getGlyphAttributes().size(); ++i) {
					String name = gman.getGlyphAttributeTypeWithInternalColumnNumber(i).getName();
					out.print(tab + name);
				}

				GlyphEntry ge = (GlyphEntry) list.toArray()[0];
				ArrayList<String> names = ge.getStringParameterColumnNames();

				for (String name : names) {
					out.print(tab + name);
				}
				out.println("");
			}

			for (GlyphEntry ge : list) {
				if (selectionOnly && !ge.isSelected()) {
					continue;
				}

				if (GeneralManager.get().getIDMappingManager()
					.hasMapping(EIDType.EXPERIMENT_INDEX, EIDType.EXPERIMENT)) {
					String id =
						GeneralManager.get().getIDMappingManager()
							.getID(EIDType.EXPERIMENT_INDEX, EIDType.EXPERIMENT, ge.getID());

					out.print(id);
				}
				else {
					out.print(ge.getID());
				}

				for (int i = 0; i < ge.getNumberOfParameters(); ++i) {
					GlyphAttributeType type = gman.getGlyphAttributeTypeWithInternalColumnNumber(i);

					out.print(tab + type.getParameterString(ge.getParameter(i)));
				}

				for (String name : ge.getStringParameterColumnNames()) {
					out.print(tab + ge.getStringParameter(name));
				}
				out.println("");
			}

			out.close();
		}
		catch (IOException e) {

		}
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		return selectionManager.getElements(SelectionType).size();
	}

	@Override
	public void clearAllSelections() {
		for (GlyphEntry g : gman.getGlyphs().values())
			g.select();

		grid_.loadData(null);
		forceRebuild();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedGlyphView serializedForm = new SerializedGlyphView(dataDomain.getDataDomainType());
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		EventPublisher eventPublisher = generalManager.getEventPublisher();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		selectionUpdateListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);

		selectionCommandListener = new SelectionCommandListener();
		selectionCommandListener.setHandler(this);
		selectionCommandListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(SelectionCommandEvent.class, selectionCommandListener);

		removeUnselectedGlyphsListener = new RemoveUnselectedGlyphsListener();
		removeUnselectedGlyphsListener.setHandler(this);
		removeUnselectedGlyphsListener.setExclusiveDataDomainType(dataDomain.getDataDomainType());
		eventPublisher.addListener(RemoveUnselectedGlyphsEvent.class, removeUnselectedGlyphsListener);

		setPositionModelListener = new SetPositionModelListener();
		setPositionModelListener.setHandler(this);
		eventPublisher.addListener(SetPositionModelEvent.class, setPositionModelListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);

		glyphSelectionBrushListener = new GlyphSelectionBrushListener();
		glyphSelectionBrushListener.setHandler(this);
		eventPublisher.addListener(GlyphSelectionBrushEvent.class, glyphSelectionBrushListener);

		glyphChangePersonalNameListener = new GlyphChangePersonalNameListener();
		glyphChangePersonalNameListener.setHandler(this);
		eventPublisher.addListener(GlyphChangePersonalNameEvent.class, glyphChangePersonalNameListener);

		glyphUpdatePositionModelListener = new GlyphUpdatePositionModelListener();
		glyphUpdatePositionModelListener.setHandler(this);
		eventPublisher.addListener(GlyphUpdatePositionModelEvent.class, glyphUpdatePositionModelListener);

	}

	/**
	 * Unregisters the listeners for this view from the event system. To release the allocated resources
	 * unregisterEventListenrs() has to be called. If inherited classes override this method, they should
	 * usually call it via super.
	 */
	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		EventPublisher eventPublisher = generalManager.getEventPublisher();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		if (selectionCommandListener != null) {
			eventPublisher.removeListener(selectionCommandListener);
			selectionCommandListener = null;
		}

		if (removeUnselectedGlyphsListener != null) {
			eventPublisher.removeListener(removeUnselectedGlyphsListener);
			removeUnselectedGlyphsListener = null;
		}

		if (setPositionModelListener != null) {
			eventPublisher.removeListener(setPositionModelListener);
			setPositionModelListener = null;
		}

		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}

		if (glyphSelectionBrushListener != null) {
			eventPublisher.removeListener(glyphSelectionBrushListener);
			glyphSelectionBrushListener = null;
		}

		if (glyphChangePersonalNameListener != null) {
			eventPublisher.removeListener(glyphChangePersonalNameListener);
			glyphChangePersonalNameListener = null;
		}

		if (glyphUpdatePositionModelListener != null) {
			eventPublisher.removeListener(glyphUpdatePositionModelListener);
			glyphUpdatePositionModelListener = null;
		}
	}

	@Override
	public void handleClearSelections() {
		clearAllSelections();
	}

	@Override
	public void handleRedrawView() {
		forceRebuild();
	}

	@Override
	public void handleUpdateView() {
		forceRebuild();
	}

	@Override
	public void setSet(ISet set) {
		this.set = set;
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;

	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

}
