package org.caleydo.core.view.opengl.canvas.glyph.gridview;

import gleem.linalg.Rotf;
import gleem.linalg.Vec2f;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

import org.caleydo.core.data.IUniqueObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.DeltaEventContainer;
import org.caleydo.core.data.selection.ESelectionCommandType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionCommand;
import org.caleydo.core.data.selection.SelectionCommandEventContainer;
import org.caleydo.core.data.selection.SelectionDeltaItem;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.manager.view.ConnectedElementRepresentationManager;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphRenderStyle;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels.GlyphGridPositionModelPlus;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.gridpositionmodels.GlyphGridPositionModelScatterplot;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering;
import org.caleydo.core.view.opengl.mouse.JoglMouseListener;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

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
	extends AGLEventListener
	implements IMediatorSender, IMediatorReceiver {

	private static final long serialVersionUID = -7899479912218913482L;

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

	private GenericSelectionManager selectionManager = null;

	private GlyphEntry oldMouseOverGlyphEntry = null;

	private int iViewRole = 0;

	private int iCornerOffset = 8;

	private float iViewScale = 0.15f;

	private String sLabel = null;
	private String sLabelPersonal = null;

	private boolean bEnableSelection = false;
	private int iSelectionBrushSize = 2;
	private ArrayList<Vec2i> alSelectionBrushCornerPoints = null;

	private int iFrameBufferObject = -1;

	// private long ticker = 0;

	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLGlyph(final int iGLCanvasID, final String sLabel, final IViewFrustum viewFrustum) {
		super(iGLCanvasID, sLabel, viewFrustum, true);

		this.sLabel = sLabel;

		alSelectionBrushCornerPoints = new ArrayList<Vec2i>();
		mouseListener_ = new GlyphMouseListener(this);
		keyListener_ = new GlyphKeyListener();
		renderStyle = new GlyphRenderStyle(viewFrustum);

		gman = generalManager.getGlyphManager();

		selectionManager = new GenericSelectionManager.Builder(EIDType.EXPERIMENT_INDEX).build();
		viewType = EManagedObjectType.GL_GLYPH;

		// TODO change this to real parameter
		// if (sLabel.equals("Glyph Single View"))
		iViewRole = 1;

		if (sLabel.equals("Glyph Selection View"))
			iViewRole = 2;

	}

	/**
	 * Sets the used positioning model
	 * 
	 * @param iconIDs
	 */
	public synchronized void setPositionModel(EIconIDs iconIDs) {
		grid_.setGlyphPositions(iconIDs);
		forceRebuild();

		generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clear(
			EIDType.EXPERIMENT_INDEX);
	}

	/**
	 * Gets the used positioning model
	 */
	public synchronized EIconIDs getPositionModel() {
		if (grid_ != null)
			return grid_.getGlyphPositions();
		return EIconIDs.DISPLAY_RECTANGLE;
	}

	/**
	 * Sets the axis mapping of a position model
	 * 
	 * @param positionmodel
	 * @param axisnumber
	 * @param internal
	 *          column number
	 */
	public synchronized void setPositionModelAxis(EIconIDs positionmodel, int axisnumber, int value) {
		if (positionmodel == EIconIDs.DISPLAY_SCATTERPLOT) {
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
		if (positionmodel == EIconIDs.DISPLAY_PLUS) {
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
	 * @param enable
	 *          / disable selection brush
	 * @param size
	 *          of the brush
	 */
	public synchronized void setSelectionBrush(int size) {
		if (size <= 0) {
			bEnableSelection = false;
			ArrayList<Integer> ids = null;
			if (size == -1)
				ids = grid_.selectAll();
			if (size == -2)
				ids = grid_.deSelectAll();

			selectionManager.clearSelections();
			alSelectionBrushCornerPoints.clear();

			if (ids != null) // no grid set
				for (int id : ids)
					selectionManager.addToType(ESelectionType.SELECTION, id);

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

	private void triggerSelectionUpdate() {
		ISelectionDelta selectionDelta = selectionManager.getDelta();
		if (selectionDelta.getAllItems().size() > 0) {
			handleConnectedElementRep(selectionDelta);

			generalManager.getEventPublisher().triggerEvent(EMediatorType.SELECTION_MEDIATOR, this,
				new DeltaEventContainer<ISelectionDelta>(selectionDelta));
		}
	}

	@Override
	public synchronized void init(GL gl) {
		ISet glyphData = null;

		for (ISet tmpSet : alSets) {
			if (tmpSet != null) {
				if (tmpSet.getLabel().equals("Set for clinical data"))
					glyphData = tmpSet;
			}
		}

		grid_ = new GLGlyphGrid(renderStyle, !this.isRenderedRemote());
		grid_.loadData(glyphData);

		// grid_.selectAll();

		// init glyph gl
		forceRebuild();

		{ // load ids to the selection manager
			selectionManager.resetSelectionManager();

			ArrayList<Integer> tmpExtID = new ArrayList<Integer>(gman.getGlyphs().keySet());
			selectionManager.initialAdd(tmpExtID);
		}

		grid_.selectAll();
	}

	@Override
	public synchronized void initLocal(GL gl) {
		generalManager.getEventPublisher().addSender(EMediatorType.SELECTION_MEDIATOR, this);
		generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);

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
			for (MouseListener l : ml) {
				if (l instanceof JoglMouseListener)
					((JoglMouseListener) l).setNavigationModes(false, false, false);
			}
		}

		// Register specialized mouse wheel listener
		if (mouseListener_ != null) {
			parentGLCanvas.addMouseListener(mouseListener_);
			mouseListener_.setNavigationModes(true, false, true);
		}

		if (iViewRole != 2 && mouseListener_ != null) {
			parentGLCanvas.addMouseMotionListener(mouseListener_);
			parentGLCanvas.addMouseWheelListener(mouseListener_);

			parentGLCanvas.addKeyListener(keyListener_);
		}

		grid_.setGlyphPositions(EIconIDs.DISPLAY_RECTANGLE);
		// grid_.setGlyphPositions(EIconIDs.DISPLAY_PLUS);

		if (this.iViewRole == 2)
			grid_.setGlyphPositions(EIconIDs.DISPLAY_CIRCLE);

	}

	@Override
	public synchronized void initRemote(final GL gl, final int iRemoteViewID,
		final PickingJoglMouseListener pickingTriggerMouseAdapter,
		final IGLCanvasRemoteRendering remoteRenderingGLCanvas)

	{

		bIsLocal = false;
		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		Collection<GLCaleydoCanvas> cc = generalManager.getViewGLCanvasManager().getAllGLCanvasUsers();

		for (GLCaleydoCanvas c : cc) {
			c.addKeyListener(keyListener_);
		}

		init(gl);

		grid_.setGridSize(30, 60);
		grid_.setGlyphPositions(EIconIDs.DISPLAY_RECTANGLE);
		// grid_.setGlyphPositions(EIconIDs.DISPLAY_SCATTERPLOT);
		// grid_.setGlyphPositions(EIconIDs.DISPLAY_PLUS);
	}

	@Override
	public synchronized void displayLocal(GL gl) {
		pickingManager.handlePicking(iUniqueID, gl);

		display(gl);
		checkForHits(gl);

	}

	@Override
	public synchronized void displayRemote(GL gl) {

		display(gl);
		checkForHits(gl);
	}

	@Override
	public synchronized void display(GL gl) {
		if (grid_ == null) {
			renderSymbol(gl);
			return;
		}

		if (grid_.getGlyphList() == null) {
			renderSymbol(gl);
			return;
		}

		if (grid_.getGlyphList().keySet().size() == 0) {
			renderSymbol(gl);
			return;
		}

		// switch between detail level
		if (mouseListener_ != null && !isRenderedRemote()) {
			float height = -mouseListener_.getCameraHeight();

			// System.out.println(height);

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

		organizeGlyphsForViewRole();

		gl.glPushMatrix();

		// rotate grid
		gl.glRotatef(45f, 0, 0, 1);

		float scale = iViewScale;

		if (iViewRole == 2) // don't scale down for the selectionview
			scale = 1;

		gl.glScalef(scale, scale, scale);
		gl.glTranslatef(iCornerOffset, 0, 0f);

		if (mouseListener_ != null)
			handleMouseListenerRubberband(gl, scale);

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

		if (mouseListener_ != null)
			mouseListener_.render(gl);

		// if (System.currentTimeMillis() - ticker > 10000)
		// {
		// ticker = System.currentTimeMillis();
		// renderToImage(gl, new File("d:/" + System.currentTimeMillis() +
		// ".png"), -1, -1);
		// }
	}

	/**
	 * inits all display lists (for glyphs and grid representations)
	 * 
	 * @param gl
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
			grid_.setGlyphPositions();
			gl.glPopMatrix();
		}

		bRedrawDisplayListGrid = false;
	}

	/**
	 * Redraws the view
	 * 
	 * @param gl
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
			gl.glPushName(pickingManager.getPickingID(iUniqueID, EPickingType.GLYPH_FIELD_SELECTION, ge.getID()));
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
		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.GLYPH_SYMBOL);
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
	 * available. You can either give it an resolution (no aspect ratio correction!) or use -1 as width & height
	 * to use 4096x? (aspect ratio of the screen). It uses ImagieIO, so the file extension defines the written
	 * picture format.
	 * 
	 * @param gl
	 *          context
	 * @param file
	 *          to write
	 * @param width
	 *          of the picture
	 * @param height
	 *          of the picture
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

		if (iFrameBufferObject < 0)
			iFrameBufferObject = createFrameBufferObject(gl, width, height);

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
	 * Creates a frame buffer object with the given size. This is from an example of Nehe Productions Lession 36
	 * (Radial Blur & Rendering To A Texture).
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
			GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, width, height, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, BufferUtil
				.newByteBuffer(width * height * 4));
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
		if (status == GL.GL_FRAMEBUFFER_COMPLETE_EXT) {
			return frameBuffer[0];
		}
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

	private void organizeGlyphsForViewRole() {
		if (iViewRole != 2)
			return;

		HashMap<Integer, GlyphEntry> tmp = new HashMap<Integer, GlyphEntry>();
		for (GlyphEntry g : gman.getGlyphs().values()) {
			if (g.isSelected())
				tmp.put(g.getID(), g);
		}
		grid_.setGlyphList(tmp);

		// position camera
		Vec3f campos = new Vec3f();
		Vec2f camcenter = grid_.getGlyphCenter();

		float diag = grid_.getGlyphLowerLeftUpperRightDiagonale();

		float x = (float) (Math.sin(Math.PI / 4.0) * (camcenter.x() + 8.0f));
		float y = (float) (Math.cos(Math.PI / 4.0) * (camcenter.x() + 8.0f));

		float f = 1.0f - diag * 0.05f;

		campos.set(-x, -y + 8, 7 + f * 10);

		this.getViewCamera().setCameraPosition(campos);
	}

	/**
	 * Handles the Rubberband Selection. This method does only the transformation/scale/rotation of the view.
	 * The real rubberband is drawn in the grid class.
	 * 
	 * @param gl
	 * @param used
	 *          scale
	 */
	private void handleMouseListenerRubberband(GL gl, float scale) {
		ArrayList<Vec3f> points = mouseListener_.getRubberBandPoints();
		if (points.size() < 2)
			return;

		Vec3f sPos = points.get(0);
		Vec3f cPos = points.get(1);

		// scale
		sPos.scale(1 / scale);
		cPos.scale(1 / scale);

		// rotate
		double w = -Math.PI / 4.0;
		float sxx = (float) (sPos.x() * Math.cos(w) - sPos.y() * Math.sin(w));
		float syy = (float) (sPos.x() * Math.sin(w) + sPos.y() * Math.cos(w));

		float cxx = (float) (cPos.x() * Math.cos(w) - cPos.y() * Math.sin(w));
		float cyy = (float) (cPos.x() * Math.sin(w) + cPos.y() * Math.cos(w));

		// translate
		cxx -= iCornerOffset;
		sxx -= iCornerOffset;

		Vec3f sPosNew = new Vec3f(sxx, syy, sPos.z());
		Vec3f cPosNew = new Vec3f(cxx, cyy, cPos.z());

		// handle selections
		grid_.selectRubberBand(gl, sPosNew, cPosNew);

		bRedrawDisplayListGlyph = true;

	}

	/**
	 * Gives the Position of a Glyph in relation to the "normal" World, without view interferences
	 * 
	 * @param glyph
	 * @return the center position ot the glyph
	 */
	private Vec3f getGlyphPosition(GlyphEntry glyph) {
		// Vec2i gridpos = grid_.getGridPosition( glyph.getX(), glyph.getY() );
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
	 * @param a
	 *          Glyph Entry (the middle one of the brush)
	 * @param select
	 *          (true) or deselect (false) mode?
	 */
	private void brushSelect(GlyphEntry glyph, boolean selectDeselect) {
		Vec2i pos = grid_.getPosition(glyph);
		alSelectionBrushCornerPoints.clear();

		int k = (iSelectionBrushSize - 1) * 2 + 1;
		int[] selectedGridHeight = new int[k];

		if (pos.y() % 2 == 0) {
			int u = 0, v = k - 1;
			for (int i = 0; i < k; ++i) // generates 1,3,4,2,0
			{
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
			for (int i = 0; i < k; ++i) // generates 0,2,4,3,1
			{
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
	public synchronized String getShortInfo() {
		if (sLabelPersonal != null)
			return "Glpyh - " + sLabelPersonal;

		return "Glpyh";
	}

	@Override
	public synchronized String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("Type: Glyph Map");
		sInfoText.append("GL: Showing Glyphs for clinical data");
		return sInfoText.toString();
	}

	@Override
	protected synchronized void handleEvents(EPickingType pickingType, EPickingMode pickingMode,
		int iExternalID, Pick pick) {

		if (pickingType == EPickingType.GLYPH_FIELD_SELECTION) {
			switch (pickingMode) {
				case MOUSE_OVER:
					GlyphEntry g = grid_.getGlyph(iExternalID);

					if (g == null) {
						generalManager.getLogger().log(Level.WARNING,
							"Glyph with external ID " + iExternalID + " not found!");
						pickingManager.flushHits(iUniqueID, pickingType);
						return;
					}

					// nothing changed, we don't need to do anything
					if (g == oldMouseOverGlyphEntry) {
						pickingManager.flushHits(iUniqueID, pickingType);
						return;
					}

					selectionManager.clearSelections();
					if (oldMouseOverGlyphEntry != null)
						selectionManager.addToType(ESelectionType.NORMAL, oldMouseOverGlyphEntry.getID());

					selectionManager.addToType(ESelectionType.MOUSE_OVER, iExternalID);

					oldMouseOverGlyphEntry = g;

					selectionManager.addConnectionID(generalManager.getIDManager().createID(
						EManagedObjectType.CONNECTION), iExternalID);

					if (bEnableSelection)
						brushSelect(g, !keyListener_.isControlDown());

					generalManager.getViewGLCanvasManager().getConnectedElementRepresentationManager().clear(
						EIDType.EXPERIMENT_INDEX);

					triggerEvent(EMediatorType.SELECTION_MEDIATOR, new SelectionCommandEventContainer(
						EIDType.EXPERIMENT_INDEX, new SelectionCommand(ESelectionCommandType.CLEAR,
							ESelectionType.MOUSE_OVER)));

					triggerSelectionUpdate();

					// only the glyphs need to be redrawn
					bRedrawDisplayListGlyph = true;

					break;
				default:
					// System.out.println("picking Mode " +
					// pickingMode.toString());

			}
		}

		pickingManager.flushHits(iUniqueID, pickingType);
	}

	private void handleSelectionUpdate(IUniqueObject eventTrigger, ISelectionDelta selectionDelta,
		EMediatorType eMediatorType) {
		if (selectionDelta.getIDType() != EIDType.EXPERIMENT_INDEX)
			return;

		// generalManager.getLogger().log(Level.INFO,
		// sLabel + ": Update called by " +
		// eventTrigger.getClass().getSimpleName());

		selectionManager.clearSelections();
		selectionManager.setDelta(selectionDelta);

		if (selectionDelta.size() > 0)
			handleConnectedElementRep(selectionDelta);

		forceRebuild();

		if (eventTrigger instanceof GLGlyph)
			return;

		// grid_.deSelectAll();

		GlyphEntry actualGlyph = null;
		for (SelectionDeltaItem item : selectionDelta) {
			actualGlyph = grid_.getGlyph(item.getPrimaryID());

			if (actualGlyph == null) // not glyph to this id found
				continue;

			if (item.getSelectionType() == ESelectionType.DESELECTED)
				actualGlyph.deSelect();

			if (item.getSelectionType() == ESelectionType.SELECTION)
				actualGlyph.select();

		}

	}

	@Override
	public void triggerEvent(EMediatorType eMediatorType, IEventContainer eventContainer) {
		generalManager.getEventPublisher().triggerEvent(eMediatorType, this, eventContainer);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void handleExternalEvent(IUniqueObject eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {
		generalManager.getLogger().log(
			Level.INFO,
			sLabel + ": Event of type " + eventContainer.getEventType() + " called by "
				+ eventTrigger.getClass().getSimpleName());

		switch (eventContainer.getEventType()) {
			case SELECTION_UPDATE:
				DeltaEventContainer<ISelectionDelta> selectionDeltaEventContainer =
					(DeltaEventContainer<ISelectionDelta>) eventContainer;
				handleSelectionUpdate(eventTrigger, selectionDeltaEventContainer

				.getSelectionDelta(), EMediatorType.SELECTION_MEDIATOR);
				break;

			case TRIGGER_SELECTION_COMMAND:
				SelectionCommandEventContainer commandEventContainer =
					(SelectionCommandEventContainer) eventContainer;
				switch (commandEventContainer.getIDType()) {
					case EXPERIMENT_INDEX:
						selectionManager.executeSelectionCommands(commandEventContainer.getSelectionCommands());
						break;
				}
				break;

		}

	}

	/**
	 * This method forces a rebuild of every display list in this view
	 */
	public synchronized void forceRebuild() {
		bRedrawDisplayListGrid = true;
		bRedrawDisplayListGlyph = true;
	}

	@Override
	public synchronized void broadcastElements(EVAOperation type) {

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

			if (item.getSelectionType() != ESelectionType.MOUSE_OVER)
				continue;

			vecGlyphPos = getGlyphPosition(actualGlyph);

			SelectedElementRep rep =
				new SelectedElementRep(EIDType.EXPERIMENT_INDEX, iUniqueID, vecGlyphPos.x(), vecGlyphPos.y(),
					vecGlyphPos.z());

			for (Integer iConnectionID : item.getConnectionID())
				connectedElementRepresentationManager.addSelection(iConnectionID, rep);

		}
	}

	public synchronized void removeUnselected() {
		grid_.loadData(null);
		forceRebuild();
	}

	public void setPersonalName(String name) {
		sLabelPersonal = name;
	}

	public String getPersonalName() {
		return sLabelPersonal;
	}

	/**
	 * Temporary fix
	 * 
	 * @param addHeader
	 *          you want a header?
	 * @param viewName
	 * @return
	 */
	@Deprecated
	public String getContainingDataAsCSV(boolean addHeader, String viewName) {
		Collection<GlyphEntry> list = grid_.getGlyphList().values();

		String content = "";

		// make header
		if (addHeader) {
			content += "GROUP; ID";

			for (int i = 1; i < gman.getGlyphAttributes().size(); ++i) {
				String name = gman.getGlyphAttributeTypeWithInternalColumnNumber(i).getName();

				content += "; " + name;
			}

			GlyphEntry ge = (GlyphEntry) list.toArray()[0];
			ArrayList<String> names = ge.getStringParameterColumnNames();

			for (String name : names) {
				content += "; " + name;
			}
			content += "\r\n";
		}

		for (GlyphEntry ge : list) {
			String line = viewName;// + "; ";
			//
			// line += GeneralManager.get().getIDMappingManager().getID(
			// EMappingType.EXPERIMENT_INDEX_2_EXPERIMENT, ge.getID());

			for (int i = 0; i < ge.getNumberOfParameters(); ++i) {
				GlyphAttributeType type = gman.getGlyphAttributeTypeWithInternalColumnNumber(i);

				line += "; " + type.getParameterString(ge.getParameter(i));
			}

			for (String name : ge.getStringParameterColumnNames()) {
				line += "; " + ge.getStringParameter(name);
			}
			content += line + "\r\n";
		}

		return content;
	}

	/**
	 * Temporary fix
	 * 
	 * @param addHeader
	 *          you want a header?
	 * @param selectionOnly
	 *          export selected glyphs only
	 * @param originalData
	 *          unused now
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
				if (selectionOnly && !ge.isSelected())
					continue;

				if (GeneralManager.get().getIDMappingManager().hasMapping(EMappingType.EXPERIMENT_INDEX_2_EXPERIMENT)) {
					String id =
						GeneralManager.get().getIDMappingManager().getID(EMappingType.EXPERIMENT_INDEX_2_EXPERIMENT,
							ge.getID());

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
	public int getNumberOfSelections(ESelectionType eSelectionType) {
		return selectionManager.getElements(eSelectionType).size();
		// throw new IllegalStateException("Not implemented yet. Do this now!");
	}

	@Override
	public void clearAllSelections() {
		for (GlyphEntry g : gman.getGlyphs().values())
			g.select();

		grid_.loadData(null);
		forceRebuild();
	}

}
