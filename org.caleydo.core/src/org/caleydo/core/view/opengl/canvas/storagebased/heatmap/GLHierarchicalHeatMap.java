package org.caleydo.core.view.opengl.canvas.storagebased.heatmap;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.event.CmdEventCreateMediator;
import org.caleydo.core.command.view.opengl.CmdCreateGLEventListener;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.data.selection.SelectionDelta;
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.manager.event.mediator.EMediatorType;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.EProjectionMode;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EStorageBasedVAType;
import org.caleydo.core.view.opengl.miniview.GLColorMappingBarMiniView;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.EIconTextures;
import org.caleydo.core.view.opengl.util.GLIconTextureManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLevel;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;


/**
 * Rendering the GLHierarchicalHeatMap with remote rendering support.
 * 
 * @author Bernhard Schlegl
 * @author Marc Streit
 */
public class GLHierarchicalHeatMap
	extends AStorageBasedView
{
	private final static float HEAT_MAP_SCALE_FACTOR_NORMAL = 0.45f;
	
	private final static float HEAT_MAP_SCALE_FACTOR_INFOCUS = 0.55f;
	
	private final static float SAMPLES_PER_TEXTURE = 1000f;
	
	private final static float SAMPLES_PER_HEATMAP = 25f;
	
	private HeatMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private GLColorMappingBarMiniView colorMappingBar;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;

	// private boolean bRenderHorizontally = false;

	private Vec3f vecTranslation;

	private float fAnimationTranslation = 0;

	private boolean bIsTranslationAnimationActive = false;

	private float fAnimationTargetTranslation = 0;

	private GLIconTextureManager iconTextureManager;
	
	private ArrayList<Float> fAlXDistances;
		
	//selector for heat map in texture
	private int iSelector = 1;

	//selector for texture in overviewBar
	private int iSelectorBar = 1;
	
	//number of partitions for selection in texture
	private int iNrSel = 0;
	
	//number of partitions for selection in overViewBar
	private int iNrSelBar = 0;
	
	//array of textures for holding the data samples
	private Texture []THeatMap;
	
	//embedded heat map
	private GLHeatMap glHeatMapView;
	
	private boolean bIsHeatmapInFocus = false;
	
	private boolean bPanningMode = false;
	
	/*
	 * Stores the last triggered selection delta.
	 * This list is used to trigger the removing of the items 
	 * before a new block is triggered.
	 */
	private SelectionDelta lastSelectionDelta;
	
	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLHierarchicalHeatMap(ESetType setType, final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(setType, iGLCanvasID, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_HIER_HEAT_MAP;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX)
				.externalIDType(EIDType.DAVID).mappingType(
						EMappingType.EXPRESSION_INDEX_2_DAVID,
						EMappingType.DAVID_2_EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(
				EIDType.EXPERIMENT_INDEX).build();

		colorMapper = ColorMappingManager.get().getColorMapping(
				EColorMappingType.GENE_EXPRESSION);

		colorMappingBar = new GLColorMappingBarMiniView(viewFrustum);
		// TODO use constant instead
		iNumberOfRandomElements = generalManager.getPreferenceStore().getInt(
				"hmNumRandomSamplinPoints");
		
		fAlXDistances = new ArrayList<Float>();
	}

	@Override
	public void init(GL gl)
	{
		createHeatMap();
		createEventMediator();
		
		glHeatMapView.initRemote(gl, getID(), null, 
				pickingTriggerMouseAdapter, null);

		iconTextureManager = new GLIconTextureManager();
		initData();

		colorMappingBar.setHeight(renderStyle.getColorMappingBarHeight());
		colorMappingBar.setWidth(renderStyle.getColorMappingBarWidth());
		if (set == null)
			return;
		
		initFloatBuffer(gl);
		triggerSelectionBlock(iSelector);
	}

	@Override
	public void initLocal(GL gl)
	{
		dataFilterLevel = EDataFilterLevel.ONLY_MAPPING;
		bRenderOnlyContext = false;

		bRenderStorageHorizontally = false;

		iGLDisplayListIndexLocal = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(final GL gl, final int iRemoteViewID,
			final RemoteHierarchyLevel layer,
			final PickingJoglMouseListener pickingTriggerMouseAdapter,
			final IGLCanvasRemoteRendering3D remoteRenderingGLCanvas)
	{
		dataFilterLevel = EDataFilterLevel.ONLY_CONTEXT;
		bRenderOnlyContext = true;

		this.remoteRenderingGLCanvas = remoteRenderingGLCanvas;

		bRenderStorageHorizontally = false;

		this.pickingTriggerMouseAdapter = pickingTriggerMouseAdapter;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;
		init(gl);

	}
	
	private void createHeatMap()
	{
		CmdCreateGLEventListener cmdView = (CmdCreateGLEventListener) generalManager
			.getCommandManager().createCommandByType(ECommandType.CREATE_GL_HEAT_MAP_3D);

		ArrayList<Integer> alSet = new ArrayList<Integer>();
		alSet.add(alSets.get(0).getID());
		
		cmdView.setAttributes(EProjectionMode.ORTHOGRAPHIC, 0, 8, 0, 8, -20, 20, alSet,
			-1);

		cmdView.doCommand();

		glHeatMapView = (GLHeatMap)cmdView.getCreatedObject();
		
//		// Register heatmap as sender to event mediator
//		ArrayList<Integer> arMediatorIDs = new ArrayList<Integer>();
//		arMediatorIDs.add(glHeatMapView.getID());
//		
//		generalManager.getEventPublisher().registerSenderToMediatorGroup(
//				EMediatorType.SELECTION_MEDIATOR, glHeatMapView);
//		generalManager.getEventPublisher().registerReceiverToMediatorGroup(
//				EMediatorType.SELECTION_MEDIATOR, glHeatMapView);

	}

	private void createEventMediator()
	{
		CmdEventCreateMediator tmpMediatorCmd = (CmdEventCreateMediator) generalManager
				.getCommandManager().createCommandByType(ECommandType.CREATE_EVENT_MEDIATOR);

		ArrayList<Integer> iAlSenderIDs = new ArrayList<Integer>();
		ArrayList<Integer> iAlReceiverIDs = new ArrayList<Integer>();
		iAlSenderIDs.add(iUniqueID);
		iAlReceiverIDs.add(glHeatMapView.getID());
		tmpMediatorCmd.setAttributes(iAlSenderIDs, iAlReceiverIDs,
				EMediatorType.VIEW_MEDIATOR);
		tmpMediatorCmd.doCommand();
	}
	
	@Override
	public synchronized void setDetailLevel(EDetailLevel detailLevel)
	{
		super.setDetailLevel(detailLevel);
		renderStyle.setDetailLevel(detailLevel);
		renderStyle.updateFieldSizes();
	}

	@Override
	public synchronized void displayLocal(GL gl)
	{
		if (set == null)
			return;

		pickingManager.handlePicking(iUniqueID, gl, true);

		if (bIsDisplayListDirtyLocal)
		{
			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);
		pickingTriggerMouseAdapter.resetEvents();
	}

	@Override
	public synchronized void displayRemote(GL gl)
	{
		if (set == null)
			return;

		if (bIsDisplayListDirtyRemote)
		{
			buildDisplayList(gl, iGLDisplayListIndexRemote);
			bIsDisplayListDirtyRemote = false;
		}
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

		display(gl);
		checkForHits(gl);
		// pickingTriggerMouseAdapter.resetEvents();
	}
	
	private void initFloatBuffer(GL gl) 
	{
		fAlXDistances.clear();
		renderStyle.updateFieldSizes();
		
		iNrSelBar = (int) Math.ceil(set.getVA(iContentVAID).size()/SAMPLES_PER_TEXTURE);
		iNrSel = (int) Math.ceil((set.getVA(iContentVAID).size()/iNrSelBar)/SAMPLES_PER_HEATMAP);
	
		THeatMap = new Texture[iNrSelBar];
		
		int iTextureHeight = set.getVA(iContentVAID).size();
		int iTextureWidth = set.getVA(iStorageVAID).size();
				
		float fLookupValue = 0;
		float fOpacity = 0;
				
		FloatBuffer FbTemp = BufferUtil.newFloatBuffer(iTextureWidth * iTextureHeight * 4 /iNrSelBar); 
	
		int iCount = 0;
		int iTextureCounter = 0;
		
		for (Integer iContentIndex : set.getVA(iContentVAID))	
		{
			iCount ++;
			for (Integer iStorageIndex: set.getVA(iStorageVAID))
			{
				if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iContentIndex)
						|| contentSelectionManager
								.checkStatus(ESelectionType.SELECTION, iContentIndex)
						|| detailLevel.compareTo(EDetailLevel.LOW) > 0)
					fOpacity = 1.0f;
				else
					fOpacity = 0.3f;
				
				fLookupValue = set.get(iStorageIndex).getFloat(EDataRepresentation.NORMALIZED,
						iContentIndex);
				 
				float[] fArMappingColor = colorMapper.getColor(fLookupValue); 
				
				float[] fArRgba = {fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity};
								
				FbTemp.put(fArRgba);
			}
			if (iCount >= (iTextureHeight / iNrSelBar))
			{
				FbTemp.rewind();

			    TextureData texData = new TextureData(
			    		GL.GL_RGBA /*internalFormat*/, 
			    		set.getVA(iStorageVAID).size() /*height*/, 
			    		set.getVA(iContentVAID).size() / iNrSelBar /*width*/, 
			    		0 /*border*/, 
			    		GL.GL_RGBA /*pixelFormat*/, 
			    		GL.GL_FLOAT /*pixelType*/, 
			    		false /*mipmap*/, 
			    		false /*dataIsCompressed*/, 
			    		false /*mustFlipVertically*/, 
			    		FbTemp /*(Float-)Buffer*/, 
			    		null /*TextureData.Flusher flusher*/);
	    
			    THeatMap[iTextureCounter] = TextureIO.newTexture(0);
			    THeatMap[iTextureCounter].updateImage(texData);
				
				iTextureCounter ++;
				iCount = 0;
			}
		}
		
	}
	
	private void renderOverviewBar(GL gl)
	{
		float fHeight;
		float fWidth;
		float fyOffset = 0.0f;
		
		fHeight = viewFrustum.getHeight();
		fWidth = 0.1f;
		
		float fStep = fHeight/iNrSelBar;
		
		for (int i = 0; i < iNrSelBar; i++)
		{
			gl.glColor4f((float)((1.0/iNrSelBar)*i), 0, 0, 1.0f);
			
			gl.glPushName(pickingManager.getPickingID(iUniqueID, 
		    		EPickingType.HIER_HEAT_MAP_TEXTURE_SELECTION, iNrSelBar - i));
			
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(0, fyOffset, 0);
			gl.glVertex3f(fWidth, fyOffset, 0);
			gl.glVertex3f(fWidth, fyOffset + fStep, 0);
			gl.glVertex3f(0, fyOffset + fStep, 0);
			gl.glEnd();	
			
			fyOffset += fStep;
			
			gl.glPopName();
		}   	    
	}
	
	private void renderSelectionQuads(GL gl)
	{
		float fHeight;
		float fWidth;
		float fyOffset = 0.0f;
		
		fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth() / 4.0f;
		
		gl.glColor4f(0, 0, 0, 0);
		
		float fStep = fHeight/iNrSel;
		
		for (int i = 0; i < iNrSel; i++)
		{
			gl.glPushName(pickingManager.getPickingID(iUniqueID, 
		    		EPickingType.HIER_HEAT_MAP_FIELD_SELECTION, iNrSel - i));
			
			gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(0, fyOffset, 0);
			gl.glVertex3f(fWidth, fyOffset, 0);
			gl.glVertex3f(fWidth, fyOffset + fStep, 0);
			gl.glVertex3f(0, fyOffset + fStep, 0);
			gl.glEnd();	
			
			fyOffset += fStep;
			
			gl.glPopName();
		}   	    
	}

	private void renderMarkerTexture(final GL gl)
	{
		if (iSelector != 0)
		{		
			float fHeight = viewFrustum.getHeight();
			float fStep = fHeight/iNrSel;
			float fFieldWith = viewFrustum.getWidth() / 4.0f;

			gl.glColor4f(1f, 1f, 0f, 1f);
			
			gl.glLineWidth(3f);
			
			gl.glBegin(GL.GL_LINE_LOOP);			
			gl.glVertex3f(0, fStep*(iNrSel - iSelector + 1), 0);
			gl.glVertex3f(fFieldWith, fStep*(iNrSel - iSelector + 1), 0);
			gl.glVertex3f(fFieldWith, fStep*(iNrSel - iSelector), 0);		
			gl.glVertex3f(0, fStep*(iNrSel - iSelector), 0);		
			gl.glEnd();
		}
	}
	
	private void renderMarkerOverviewBar(final GL gl)
	{
		if (iSelectorBar != 0)
		{		
			float fHeight = viewFrustum.getHeight();
			float fStep = fHeight/iNrSelBar;
			float fFieldWith = 0.1f;

			gl.glColor4f(1f, 1f, 0f, 1f);
			
			gl.glLineWidth(3f);
			
			gl.glBegin(GL.GL_LINE_LOOP);			
			gl.glVertex3f(0, fStep*(iNrSelBar - iSelectorBar + 1), 0);
			gl.glVertex3f(fFieldWith, fStep*(iNrSelBar - iSelectorBar + 1), 0);
			gl.glVertex3f(fFieldWith, fStep*(iNrSelBar - iSelectorBar), 0);		
			gl.glVertex3f(0, fStep*(iNrSelBar - iSelectorBar), 0);		
			gl.glEnd();
		}
	}
	
	private void renderTextureHeatMap(GL gl)
	{
		float fHeight;
		float fWidth;
		
	    THeatMap[iSelectorBar-1].enable();
		THeatMap[iSelectorBar-1].bind();
			
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
	    		GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
	    		GL.GL_NEAREST);
		
	    TextureCoords texCoords = THeatMap[iSelectorBar-1].getImageTexCoords();
	   
	    gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
	    
	    fHeight = viewFrustum.getHeight();
		fWidth = viewFrustum.getWidth() / 4.0f;
	    
	    gl.glBegin(GL.GL_QUADS);
	    gl.glTexCoord2d(texCoords.left(),texCoords.top());
		gl.glVertex3f(0, 0, 0);
	    gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(0, fHeight, 0);
	    gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(fWidth, fHeight, 0);
	    gl.glTexCoord2d(texCoords.right(), texCoords.top());
		gl.glVertex3f(fWidth, 0, 0);
		gl.glEnd();

		gl.glPopAttrib();
		
		THeatMap[iSelectorBar-1].disable();
			
		renderSelectionQuads(gl);
	}
	
	@Override
	public synchronized void display(GL gl)
	{		
		// GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// GLHelperFunctions.drawAxis(gl);
		gl.glCallList(iGLDisplayListToCall);
		// buildDisplayList(gl, iGLDisplayListIndexRemote);		
	}

	private void buildDisplayList(final GL gl, int iGLDisplayListIndex)
	{		
		if (bHasFrustumChanged)
		{
			bHasFrustumChanged = false;
		}
		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);

		if (contentSelectionManager.getNumberOfElements() == 0)
		{
			renderSymbol(gl);
		}
		else
		{
			// FIXME: bad hack, normalize frustum to 0:1 to avoid that
			// clipToFrustum(gl);
			       
	        gl.glMatrixMode(GL.GL_MODELVIEW);
	        gl.glLoadIdentity();	
						
			renderOverviewBar(gl);	
			renderMarkerOverviewBar(gl);
			
			gl.glTranslatef(0.2f, 0, 0);
			
			if(bIsHeatmapInFocus)
			{
				gl.glScalef(0.2f, 1, 1);
			}
			
			renderTextureHeatMap(gl);
			renderMarkerTexture(gl);
			
			gl.glTranslatef(-0.2f, 0, 0);
			
			if(bIsHeatmapInFocus)
			{
				gl.glScalef(5, 1, 1);
				gl.glTranslatef(0.5f, 0f, 0f);
				gl.glScalef(HEAT_MAP_SCALE_FACTOR_INFOCUS, HEAT_MAP_SCALE_FACTOR_INFOCUS, HEAT_MAP_SCALE_FACTOR_INFOCUS);
			
			}
			else
			{	
				gl.glTranslatef(2.5f, 0f, 0f);
				gl.glScalef(HEAT_MAP_SCALE_FACTOR_NORMAL, HEAT_MAP_SCALE_FACTOR_NORMAL, HEAT_MAP_SCALE_FACTOR_NORMAL);
			}
			
			glHeatMapView.displayRemote(gl);
//			renderEmbeddedHeatMap(gl);
			
			gl.glFlush();
			
			if(bIsHeatmapInFocus)
			{
				gl.glTranslatef(-0.5f, 0f, 0f);
				gl.glScalef(1/HEAT_MAP_SCALE_FACTOR_INFOCUS, 1/HEAT_MAP_SCALE_FACTOR_INFOCUS, 1/HEAT_MAP_SCALE_FACTOR_INFOCUS);
			}
			else
			{
				gl.glTranslatef(-3f, 0f, 0f);
				gl.glScalef(1/HEAT_MAP_SCALE_FACTOR_NORMAL, 1/HEAT_MAP_SCALE_FACTOR_NORMAL, 1/HEAT_MAP_SCALE_FACTOR_NORMAL);
			}

			gl.glDisable(GL.GL_STENCIL_TEST);
			
		}	
		gl.glEndList();	
	}

	/**
	 * Render the symbol of the view instead of the view
	 * 
	 * @param gl
	 */
	private void renderSymbol(GL gl)
	{
		// TODO move to base when a base exists
		float fXButtonOrigin = 0.33f * renderStyle.getScaling();
		float fYButtonOrigin = 0.33f * renderStyle.getScaling();
		Texture tempTexture = iconTextureManager.getIconTexture(gl, EIconTextures.HEAT_MAP_SYMBOL);
		tempTexture.enable();
		tempTexture.bind();

		TextureCoords texCoords = tempTexture.getImageTexCoords();

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
	
	private void triggerSelectionBlock(int iSelectedElement)
	{		
		if (lastSelectionDelta != null)
		{
			// Remove old block selection items from heatmap
			for (SelectionItem item : lastSelectionDelta)
			{
				item.setSelectionType(ESelectionType.REMOVE);
			}
			triggerUpdate(lastSelectionDelta);
			lastSelectionDelta = null;			
		}
		
		int iCount = 0;
		float fCntTexture = set.getVA(iContentVAID).size()/iNrSel/iNrSelBar;
		float fCntBar = set.getVA(iContentVAID).size()/iNrSelBar;
		
		SelectionDelta delta = new SelectionDelta(EIDType.DAVID);
		for (Integer iContentIndex : set.getVA(iContentVAID))
		{
			iCount++;
			
			//overviewbar selection
			if((iCount <= (fCntBar * (iSelectorBar - 1)) || (iCount > (fCntBar *iSelectorBar))))
			{
				continue;
			}
			
			//texture selection
			if(((iCount%fCntBar) <= (fCntTexture * (iSelectedElement -1))) || ((iCount%fCntBar) > (fCntTexture * iSelectedElement)))
			{
				continue;
			}
			
			delta.addSelection(getDavidIDFromStorageIndex(iContentIndex), ESelectionType.ADD);
		}
		
		triggerUpdate(delta);
		
		lastSelectionDelta = delta;
	}
	
	public synchronized void renderHorizontally(boolean bRenderStorageHorizontally)
	{

		this.bRenderStorageHorizontally = bRenderStorageHorizontally;
		renderStyle.setBRenderStorageHorizontally(bRenderStorageHorizontally);
		setDisplayListDirty();
	}

	@Override
	protected void initLists()
	{

		// Set<Integer> setMouseOver = storageSelectionManager
		// .getElements(ESelectionType.MOUSE_OVER);

		if (bRenderOnlyContext)
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		else
		{
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION))
				initCompleteList();
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}
		iStorageVAID = mapVAIDs.get(EStorageBasedVAType.STORAGE_SELECTION);

		contentSelectionManager.resetSelectionManager();
		storageSelectionManager.resetSelectionManager();

		contentSelectionManager.setVA(set.getVA(iContentVAID));

		if (renderStyle != null)
		{
			renderStyle.setActiveVirtualArray(iContentVAID);
		}

		int iNumberOfColumns = set.getVA(iContentVAID).size();
		int iNumberOfRows = set.getVA(iStorageVAID).size();

		for (int iRowCount = 0; iRowCount < iNumberOfRows; iRowCount++)
		{
			storageSelectionManager.initialAdd(set.getVA(iStorageVAID).get(iRowCount));

		}

		// this for loop executes one per axis
		for (int iColumnCount = 0; iColumnCount < iNumberOfColumns; iColumnCount++)
		{
			contentSelectionManager.initialAdd(set.getVA(iContentVAID).get(iColumnCount));

			// if
			// (setMouseOver.contains(set.getVA(iContentVAID).get(iColumnCount
			// )))
			// {
			// storageSelectionManager.addToType(ESelectionType.MOUSE_OVER,
			// set.getVA(
			// iContentVAID).get(iColumnCount));
			// }
		}

		renderStyle = new HeatMapRenderStyle(viewFrustum, contentSelectionManager, set,
				iContentVAID, iStorageVAID, set.getVA(iStorageVAID).size(),
				bRenderStorageHorizontally);
		renderStyle.setDetailLevel(detailLevel);
		// TODO probably remove this here
		// renderStyle.initFieldSizes();

		vecTranslation = new Vec3f(0, renderStyle.getYCenter() * 2, 0);

	}

	@Override
	public String getShortInfo()
	{
		return "Hierarchical Heat Map (" + set.getVA(iContentVAID).size() + " genes / "
		+ set.getVA(iStorageVAID).size() + " experiments)";
	}

	@Override
	public String getDetailedInfo()
	{
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Hierarchical Heat Map\n");

		if (bRenderStorageHorizontally)
			sInfoText.append(set.getVA(iContentVAID).size() + "Genes in columns and "
					+ set.getVA(iStorageVAID).size() + " experiments in rows.\n");
		else
			sInfoText.append(set.getVA(iContentVAID).size() + " Genes in rows and "
					+ set.getVA(iStorageVAID).size() + " experiments in columns.\n");

		if (bRenderOnlyContext)
		{
			sInfoText
					.append("Showing only genes which occur in one of the other views in focus\n");
		}
		else
		{
			if (bUseRandomSampling)
			{
				sInfoText.append("Random sampling active, sample size: "
						+ iNumberOfRandomElements + "\n");
			}
			else
			{
				sInfoText.append("Random sampling inactive\n");
			}

			if (dataFilterLevel == EDataFilterLevel.COMPLETE)
				sInfoText.append("Showing all genes in the dataset\n");
			else if (dataFilterLevel == EDataFilterLevel.ONLY_MAPPING)
				sInfoText.append("Showing all genes that have a known DAVID ID mapping\n");
			else if (dataFilterLevel == EDataFilterLevel.ONLY_CONTEXT)
				sInfoText
						.append("Showing all genes that are contained in any of the KEGG or Biocarta pathways\n");
		}

		return sInfoText.toString();
	}

	@Override
	protected void handleEvents(EPickingType ePickingType, EPickingMode pickingMode,
			int iExternalID, Pick pick)
	{
		if (detailLevel == EDetailLevel.VERY_LOW)
		{
			pickingManager.flushHits(iUniqueID, ePickingType);
			return;
		}

		switch (ePickingType)
		{
			case HIER_HEAT_MAP_TEXTURE_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:
												
						//System.out.println("click on OverviewBar part:" + iExternalID);
						iSelectorBar = iExternalID;
						iSelector = 1;
						triggerSelectionBlock(iSelector);
						setDisplayListDirty();
						break;

					case MOUSE_OVER:

//						System.out.println("mouse over OverviewBar part:" + iExternalID);
						iSelectorBar = iExternalID;
						iSelector = 1;
						triggerSelectionBlock(iSelector);
						setDisplayListDirty();
						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
			
			case HIER_HEAT_MAP_FIELD_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:
			
						triggerSelectionBlock(iExternalID);
						//System.out.println("click on texture part:" + iExternalID);
						iSelector = iExternalID;
						setDisplayListDirty();
						break;

					case MOUSE_OVER:
			
						triggerSelectionBlock(iExternalID);
						//System.out.println("click on texture part:" + iExternalID);
						iSelector = iExternalID;
						setDisplayListDirty();
						break;
				}

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
		}
	}
	
	@Override
	protected void handleConnectedElementRep(ISelectionDelta selectionDelta)
	{
		renderStyle.updateFieldSizes();
		fAlXDistances.clear();
		float fDistance = 0;

		for (Integer iStorageIndex : set.getVA(iContentVAID))
		{
			fAlXDistances.add(fDistance);
			if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER, iStorageIndex)
					|| contentSelectionManager.checkStatus(ESelectionType.SELECTION,
							iStorageIndex))
			// if(selectionDelta.)
			{
				fDistance += renderStyle.getSelectedFieldWidth();
			}
			else
			{
				fDistance += renderStyle.getNormalFieldWidth();
			}

		}
		super.handleConnectedElementRep(selectionDelta);
	}
	
	@Override
	protected SelectedElementRep createElementRep(EIDType idType, int iStorageIndex)
			throws InvalidAttributeValueException
	{
		SelectedElementRep elementRep;// = new SelectedElementRep(iUniqueID,
		// 0.0f, 0.0f, 0.0f);

		int iContentIndex = set.getVA(iContentVAID).indexOf(iStorageIndex);
		if (iContentIndex == -1)
		{
			// TODO this shouldn't happen
			generalManager.getLogger().log(Level.SEVERE,
					"No element in virtual array for storage index");

			return null;
		}
		// renderStyle.resetFieldWidths();
		// Vec2f vecFieldWithAndHeight =
		// renderStyle.getFieldWidthAndHeight(iContentIndex);

		// for (int iCount = 0; iCount <= iContentIndex; iCount++)
		// {
		// vecFieldWithAndHeight = renderStyle.getFieldWidthAndHeight(iCount);
		// }

		float fXValue = fAlXDistances.get(iContentIndex) + renderStyle.getSelectedFieldWidth()
				/ 2;// + renderStyle.getXSpacing();

		float fYValue = renderStyle.getYCenter();// + vecFieldWithAndHeight.y()
		// * set.getVA(iContentVAID).size() / 2;

		if (bRenderStorageHorizontally)
		{
			elementRep = new SelectedElementRep(EIDType.EXPRESSION_INDEX, iUniqueID, fXValue + fAnimationTranslation,
					fYValue, 0);

		}
		else
		{
			Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
			Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue, fYValue, 0));
			vecPoint.setY(vecPoint.y() + vecTranslation.y());
			elementRep = new SelectedElementRep(EIDType.EXPRESSION_INDEX, iUniqueID, vecPoint.x(), vecPoint.y()
					- fAnimationTranslation, 0);

		}
		return elementRep;
	}

	@Override
	protected void rePosition(int iElementID)
	{
		int iSelection;
		if (bRenderStorageHorizontally)
			iSelection = iContentVAID;
		else
			iSelection = iStorageVAID;
		// TODO test this

		float fCurrentPosition = set.getVA(iSelection).indexOf(iElementID)
				* renderStyle.getNormalFieldWidth();// +
		// renderStyle.getXSpacing(
		// );

		float fFrustumLength = viewFrustum.getRight() - viewFrustum.getLeft();
		float fLength = (set.getVA(iSelection).size() - 1) * renderStyle.getNormalFieldWidth()
				+ 1.5f; // MARC
		// :
		// 1.5
		// =
		// correction of
		// lens effect in
		// heatmap

		fAnimationTargetTranslation = -(fCurrentPosition - fFrustumLength / 2);

		if (-fAnimationTargetTranslation > fLength - fFrustumLength)
			fAnimationTargetTranslation = -(fLength - fFrustumLength + 2 * 0.00f);
		else if (fAnimationTargetTranslation > 0)
			fAnimationTargetTranslation = 0;
		else if (-fAnimationTargetTranslation < -fAnimationTranslation + fFrustumLength / 2
				- 0.00f
				&& -fAnimationTargetTranslation > -fAnimationTranslation - fFrustumLength / 2
						+ 0.00f)
		{
			fAnimationTargetTranslation = fAnimationTranslation;
			return;
		}

		bIsTranslationAnimationActive = true;
	}

	@Override
	public void renderContext(boolean bRenderOnlyContext)
	{

		this.bRenderOnlyContext = bRenderOnlyContext;

		if (this.bRenderOnlyContext)
			iContentVAID = mapVAIDs.get(EStorageBasedVAType.EXTERNAL_SELECTION);
		else
		{
			if (!mapVAIDs.containsKey(EStorageBasedVAType.COMPLETE_SELECTION))
				initCompleteList();

			iContentVAID = mapVAIDs.get(EStorageBasedVAType.COMPLETE_SELECTION);
		}

		contentSelectionManager.setVA(set.getVA(iContentVAID));
		renderStyle.setActiveVirtualArray(iContentVAID);

		setDisplayListDirty();

	}

	@Override
	protected void checkUnselection()
	{
		// TODO
	}
	
	@Override
	public void broadcastElements()
	{
		ISelectionDelta delta = contentSelectionManager.getCompleteDelta();
		triggerUpdate(delta);
		setDisplayListDirty();
	}

	@Override
	public void resetSelections()
	{
		contentSelectionManager.clearSelections();
		storageSelectionManager.clearSelections();
		setDisplayListDirty();
	}
	
	@Override
	public void changeOrientation(boolean defaultOrientation)
	{
		renderHorizontally(defaultOrientation); 
	}

	@Override
	public boolean isInDefaultOrientation()
	{
		return bRenderStorageHorizontally;
	}
	
	public void changeFocus(boolean bInFocus)
	{
		if(bIsHeatmapInFocus)
			bIsHeatmapInFocus = false;
		else
			bIsHeatmapInFocus = true;	
		
		setDisplayListDirty();
		triggerSelectionBlock(iSelector);
	}
	
	public boolean isInFocus()
	{
		return bIsHeatmapInFocus;
	}
	
}
