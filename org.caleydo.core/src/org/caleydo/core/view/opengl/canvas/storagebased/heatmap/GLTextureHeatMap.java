package org.caleydo.core.view.opengl.canvas.storagebased.heatmap;

import static org.caleydo.core.view.opengl.canvas.storagebased.heatmap.HeatMapRenderStyle.FIELD_Z;
import static org.caleydo.core.view.opengl.canvas.storagebased.heatmap.HeatMapRenderStyle.SELECTION_Z;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.MOUSE_OVER_LINE_WIDTH;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_COLOR;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.SELECTED_LINE_WIDTH;
import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import javax.management.InvalidAttributeValueException;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import org.caleydo.core.data.collection.storage.EDataRepresentation;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.GenericSelectionManager;
import org.caleydo.core.data.selection.ISelectionDelta;
import org.caleydo.core.data.selection.SelectedElementRep;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.util.mapping.color.ColorMapping;
import org.caleydo.core.util.mapping.color.ColorMappingManager;
import org.caleydo.core.util.mapping.color.EColorMappingType;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.remote.IGLCanvasRemoteRendering3D;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.EDataFilterLevel;
import org.caleydo.core.view.opengl.canvas.storagebased.EStorageBasedVAType;
import org.caleydo.core.view.opengl.miniview.GLColorMappingBarMiniView;
import org.caleydo.core.view.opengl.mouse.PickingJoglMouseListener;
import org.caleydo.core.view.opengl.util.EIconTextures;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.GLIconTextureManager;
import org.caleydo.core.view.opengl.util.hierarchy.RemoteHierarchyLevel;
import org.eclipse.swt.widgets.Display;
import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.j2d.TextureRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;


/**
 * Rendering the GLHeatMap
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class GLTextureHeatMap
	extends AStorageBasedView
{

	private HeatMapRenderStyle renderStyle;

	private ColorMapping colorMapper;

	private GLColorMappingBarMiniView colorMappingBar;

	private EIDType eFieldDataType = EIDType.EXPRESSION_INDEX;

	// private boolean bRenderHorizontally = false;

	private Vec4f vecRotation = new Vec4f(-90, 0, 0, 1);

	private Vec3f vecTranslation;

	private float fAnimationDefaultTranslation = 0;

	private float fAnimationTranslation = 0;

	private boolean bIsTranslationAnimationActive = false;

	private float fAnimationTargetTranslation = 0;

	private SelectedElementRep elementRep;

	private GLIconTextureManager iconTextureManager;
	
	private ArrayList<Float> fAlXDistances;
	
	private Texture THeatMap;
	
	private int[] texName = new int[2];
	
	/**
	 * Constructor.
	 * 
	 * @param iViewID
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLTextureHeatMap(final int iGLCanvasID, final String sLabel,
			final IViewFrustum viewFrustum)
	{
		super(iGLCanvasID, sLabel, viewFrustum);
		viewType = EManagedObjectType.GL_HEAT_MAP;

		ArrayList<ESelectionType> alSelectionTypes = new ArrayList<ESelectionType>();
		alSelectionTypes.add(ESelectionType.NORMAL);
		alSelectionTypes.add(ESelectionType.MOUSE_OVER);
		alSelectionTypes.add(ESelectionType.SELECTION);

		contentSelectionManager = new GenericSelectionManager.Builder(EIDType.EXPRESSION_INDEX)
				.externalIDType(EIDType.DAVID).mappingType(
						EMappingType.EXPRESSION_INDEX_2_DAVID,
						EMappingType.DAVID_2_EXPRESSION_INDEX).build();
		storageSelectionManager = new GenericSelectionManager.Builder(
				EIDType.EXPRESSION_EXPERIMENT).build();

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
		iconTextureManager = new GLIconTextureManager(gl);
		initData();

		colorMappingBar.setHeight(renderStyle.getColorMappingBarHeight());
		colorMappingBar.setWidth(renderStyle.getColorMappingBarWidth());
		if (set == null)
			return;
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

	@Override
	public void setDetailLevel(EDetailLevel detailLevel)
	{
		super.setDetailLevel(detailLevel);
		renderStyle.setDetailLevel(detailLevel);
		renderStyle.updateFieldSizes();
	}

	@Override
	public void displayLocal(GL gl)
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
	public void displayRemote(GL gl)
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
	
	private BufferedImage drawHeatMap(GL gl) 
	{
		fAlXDistances.clear();
		renderStyle.updateFieldSizes();
		
		int fXPosition = 0;
		int fYPosition = 0;
		int fFieldWidth = 1;
		int fFieldHeight = 10;
		
		/*
		float fFieldWidt = renderStyle.getNormalFieldWidth();
		float fFieldHeigh = renderStyle.getFieldHeight();
		System.out.println("fFieldWidt" + fFieldWidt);
		System.out.println("fFieldHeigh" + fFieldHeigh);
		*/
		
		int iTextureWidth = set.getVA(iContentVAID).size()*1;
		int iTextureHeight = set.getVA(iStorageVAID).size()*10;
		
		float fLookupValue = 0;
		float fOpacity = 0;
		
		//FloatBuffer fbTexture = BufferUtil.newFloatBuffer(iTextureWidth * iTextureHeight * 4);
		
		BufferedImage img = new BufferedImage(iTextureWidth, iTextureHeight, BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		
		for (Integer iContentIndex : set.getVA(iContentVAID))
		{
			fYPosition = 0;
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
				//float[] rgba = {fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity};
				
				Color HeatMapColor = new Color(fArMappingColor[0], fArMappingColor[1], fArMappingColor[2], fOpacity);
				g.setColor(HeatMapColor);
			
				g.fillRect(fXPosition,fYPosition,fFieldWidth,fFieldHeight);
				
				//fbTexture.put(rgba);
				fYPosition += fFieldHeight;
			}
			fXPosition += fFieldWidth;
		}
		
		//fbTexture.rewind();
		
		return img;	
	}
	
	private void renderTextureHeatMap(GL gl)
	{
		/*
		FloatBuffer img = drawHeatMap(gl);
		
		gl.glGenTextures(1, texName, 0);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, texName[0]);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
	        GL.GL_NEAREST);
	    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
	        GL.GL_NEAREST);
	    gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, set.getVA(iContentVAID).size(),
	    		set.getVA(iStorageVAID).size(), 0, GL.GL_RGBA, GL.GL_FLOAT, img); 
	    
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    gl.glBindTexture(GL.GL_TEXTURE_2D, texName[0]);		
	    */
		
	    gl.glEnable(GL.GL_TEXTURE_2D);
		
	    BufferedImage img = drawHeatMap(gl);
	    
		THeatMap = TextureIO.newTexture(img, true);
		
		THeatMap.enable();
		THeatMap.bind();
		
	    TextureCoords texCoords = THeatMap.getImageTexCoords();
	   
	    gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
	    gl.glColor4f(1f, 1, 1, 1f);
	    
	    gl.glBegin(GL.GL_QUADS);
	    gl.glTexCoord2d(texCoords.left(),texCoords.bottom());
		gl.glVertex3f(0.0f, 0.0f, 0);
	    gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(0.0f, 5.0f, 0);
	    gl.glTexCoord2d(texCoords.right(), texCoords.top());
		gl.glVertex3f(7.0f, 5.0f, 0);
	    gl.glTexCoord2d(texCoords.left(), texCoords.top());
		gl.glVertex3f(7.0f, 0.0f, 0);
		gl.glEnd();
	    gl.glFlush();

		gl.glPopAttrib();
		
		THeatMap.disable();
	}
	
	@Override
	public void display(GL gl)
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
			float fLeftOffset = 0;
			if (remoteRenderingGLCanvas == null)
				fLeftOffset = 0.05f;
			else
				fLeftOffset = 0.15f;
			// GLHelperFunctions.drawAxis(gl);
			if (detailLevel == EDetailLevel.HIGH)
			{
				colorMappingBar.render(gl, fLeftOffset,
						(viewFrustum.getHeight() - colorMappingBar.getHeight()) / 2, 0.2f);
				gl.glTranslatef(fLeftOffset + colorMappingBar.getWidth(), 0, 0);
			}

			/*
			if (!bRenderStorageHorizontally)
			{
				gl.glTranslatef(vecTranslation.x(), viewFrustum.getHeight(), vecTranslation
						.z());
				gl.glRotatef(vecRotation.x(), vecRotation.y(), vecRotation.z(), vecRotation
						.w());
			}
			*/
		
			gl.glTranslatef(fAnimationTranslation, 0.0f, 0.0f);
			
			renderTextureHeatMap(gl);
			
			//renderHeatMap(gl);
			//renderSelection(gl, ESelectionType.MOUSE_OVER);
			//renderSelection(gl, ESelectionType.SELECTION);

			gl.glTranslatef(-fAnimationTranslation, 0.0f, 0.0f);

			if (!bRenderStorageHorizontally)
			{
				gl.glRotatef(-vecRotation.x(), vecRotation.y(), vecRotation.z(), vecRotation
						.w());
				gl.glTranslatef(-vecTranslation.x(), -viewFrustum.getHeight(), -vecTranslation
						.z());
			}
			if (detailLevel == EDetailLevel.HIGH)
			{
				gl.glTranslatef(-fLeftOffset - colorMappingBar.getWidth(), 0, 0);
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
		Texture tempTexture = iconTextureManager.getIconTexture(EIconTextures.HEAT_MAP_SYMBOL);
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

	public void renderHorizontally(boolean bRenderStorageHorizontally)
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
		return "Heat Map (" + set.getVA(iContentVAID).size() + " genes / "
		+ set.getVA(iStorageVAID).size() + " experiments)";
	}

	@Override
	public String getDetailedInfo()
	{
		// StringBuffer sInfoText = new StringBuffer();
		// sInfoText.append("Heat Map");
		// sInfoText.append(set.getVA(iContentVAID).size() +
		// " gene expression values");
		// return sInfoText.toString();

		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Heat Map\n");

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
			case HEAT_MAP_FIELD_SELECTION:
				switch (pickingMode)
				{
					case CLICKED:
						connectedElementRepresentationManager.clear();

						contentSelectionManager.clearSelection(ESelectionType.SELECTION);
						contentSelectionManager.addToType(ESelectionType.SELECTION,
								iExternalID);

						if (eFieldDataType == EIDType.EXPRESSION_INDEX)
						{
							triggerUpdate(contentSelectionManager.getDelta());
						}

						break;

					case MOUSE_OVER:

						if (contentSelectionManager.checkStatus(ESelectionType.MOUSE_OVER,
								iExternalID))
							break;

						connectedElementRepresentationManager.clear();

						contentSelectionManager.clearSelection(ESelectionType.MOUSE_OVER);
						contentSelectionManager.addToType(ESelectionType.MOUSE_OVER,
								iExternalID);

						if (eFieldDataType == EIDType.EXPRESSION_INDEX)
						{
							triggerUpdate(contentSelectionManager.getDelta());
						}

						break;
				}

				bIsDisplayListDirtyLocal = true;
				bIsDisplayListDirtyRemote = true;

				pickingManager.flushHits(iUniqueID, ePickingType);
				break;
		}
	}

	private void renderHeatMap(final GL gl)
	{
		
	}
	
	private void renderElement(final GL gl, final int iStorageIndex, final int iContentIndex,
			final float fXPosition, final float fYPosition, final float fFieldWidth,
			final float fFieldHeight)
	{

	}

	private void renderSelection(final GL gl, ESelectionType eSelectionType)
	{

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
	protected SelectedElementRep createElementRep(int iStorageIndex)
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
			elementRep = new SelectedElementRep(iUniqueID, fXValue + fAnimationTranslation,
					fYValue, 0);

		}
		else
		{
			Rotf myRotf = new Rotf(new Vec3f(0, 0, 1), -(float) Math.PI / 2);
			Vec3f vecPoint = myRotf.rotateVector(new Vec3f(fXValue, fYValue, 0));
			vecPoint.setY(vecPoint.y() + vecTranslation.y());
			elementRep = new SelectedElementRep(iUniqueID, vecPoint.x(), vecPoint.y()
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
	
	private void doTranslation()
	{

		float fDelta = 0;
		if (fAnimationTargetTranslation < fAnimationTranslation - 0.5f)
		{

			fDelta = -0.5f;

		}
		else if (fAnimationTargetTranslation > fAnimationTranslation + 0.5f)
		{
			fDelta = 0.5f;
		}
		else
		{
			fDelta = fAnimationTargetTranslation - fAnimationTranslation;
			bIsTranslationAnimationActive = false;
		}

		if (elementRep != null)
		{
			ArrayList<Vec3f> alPoints = elementRep.getPoints();
			for (Vec3f currentPoint : alPoints)
			{
				currentPoint.setY(currentPoint.y() - fDelta);
			}
		}

		fAnimationTranslation += fDelta;
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

	private void renderCaption(GL gl, String sLabel, float fXOrigin, float fYOrigin,
			float fRotation, float fFontScaling)
	{
		textRenderer.setColor(0, 0, 0, 1);
		gl.glPushAttrib(GL.GL_CURRENT_BIT | GL.GL_LINE_BIT);
		gl.glTranslatef(fXOrigin, fYOrigin, 0);
		gl.glRotatef(fRotation, 0, 0, 1);
		textRenderer.begin3DRendering();
		textRenderer.draw3D(sLabel, 0, 0, 0, fFontScaling);
		textRenderer.end3DRendering();
		gl.glRotatef(-fRotation, 0, 0, 1);
		gl.glTranslatef(-fXOrigin, -fYOrigin, 0);
		// textRenderer.begin3DRendering();
		gl.glPopAttrib();
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
}
