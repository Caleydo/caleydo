package org.caleydo.view.datawindows;

import gleem.linalg.Vec3f;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

import javax.media.opengl.GL;

import org.caleydo.core.data.selection.EVAOperation;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.Pick;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.tracking.TrackDataProvider;
import org.caleydo.core.view.opengl.camera.IViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.opengl.mouse.GLMouseListener;
import org.caleydo.core.view.opengl.util.GLHelperFunctions;
import org.caleydo.core.view.opengl.util.overlay.infoarea.GLInfoAreaManager;

/**
 * Rendering the Datawindow
 * 
 * @author Hannes Plank
 * @author Marc Streit
 */
@SuppressWarnings("unused")
public class GLDataWindows extends AGLView {

	public final static String VIEW_ID = "org.caleydo.view.datawindows";

	
	private int numberOfSquares = 30;
	  
	 
	private float[] squarePositionX;
	private float[] squarePositionY;
	private float[] squareSizes;
	private boolean[] squareChecked;
	private float squareBaseSize;
	
	
	private float mouseCoordX=0;
	private float mouseCoordY=0;
	
	private Point mousePoint;
	int viewport[] = new int[4];
 
    private float canvasWidth=5;
    private float canvasHeight=5;
    
    private TrackDataProvider tracker;
	private float[] receivedEyeData;
    
	
	private org.eclipse.swt.graphics.Point upperLeftScreenPos = new org.eclipse.swt.graphics.Point(
			0, 0);
	/**
	 * Constructor.
	 * 
	 * @param glCanvas
	 * @param sLabel
	 * @param viewFrustum
	 */
	public GLDataWindows(GLCaleydoCanvas glCanvas, final String sLabel,
			final IViewFrustum viewFrustum) {

		super(glCanvas, sLabel, viewFrustum, true);
		viewType = GLDataWindows.VIEW_ID;
		
		
		this.tracker = new TrackDataProvider();
		
		tracker.startTracking();
		
		//setting square data: 
		squarePositionX = new float[numberOfSquares];
		squarePositionY = new float[numberOfSquares];
		squareSizes = new float[numberOfSquares];
		squareChecked= new boolean[numberOfSquares];
		 
		//Point pt = new Point(0, 0);
		Random r = new Random();
		squareBaseSize =  canvasHeight/20;
		float relX;
		float relY;
		float dist;
		for(int i=0;i < numberOfSquares;i++){
			
			squarePositionX[i] = r.nextFloat() * canvasWidth;
			squarePositionY[i] = r.nextFloat() * canvasHeight;
			
			squareSizes[i] = squareBaseSize;
			squareChecked[i]=false;
			for(int backcounter=0; backcounter < i; backcounter++){
				relX = Math.abs(squarePositionX[backcounter]-squarePositionX[i]);
				relY = Math.abs(squarePositionY[backcounter]-squarePositionY[i]);
		        dist = (float) Math.sqrt(relX*relX+relY*relY);
		        
				
				if(dist < squareBaseSize*3) {
		        	i = i-1;
		        	break;
		        }				
			}
			
		}		
		
	
		getParentGLCanvas().getParentComposite().getDisplay().asyncExec(
				new Runnable() {
					@Override
					public void run() {
						upperLeftScreenPos = getParentGLCanvas()
								.getParentComposite().toDisplay(1, 1);
					}
				});
		
		
		
	}

	@Override
	public void initLocal(GL gl) {

		iGLDisplayListIndexLocal = gl.glGenLists(5);

		iGLDisplayListToCall = iGLDisplayListIndexLocal;
		init(gl);

	}

	@Override
	public void initRemote(final GL gl, final AGLView glParentView,
			final GLMouseListener glMouseListener,
			GLInfoAreaManager infoAreaManager) {

		this.glMouseListener = glMouseListener;

		iGLDisplayListIndexRemote = gl.glGenLists(1);
		iGLDisplayListToCall = iGLDisplayListIndexRemote;

	init(gl);
	}

	@Override
	public void setDetailLevel(EDetailLevel detailLevel) {
			super.setDetailLevel(detailLevel);
	}

@Override
	public void displayLocal(GL gl) {
		processEvents();
		if (!isVisible())
			return;
		if (set == null)
			return;
	
		if (bIsDisplayListDirtyLocal) {

			buildDisplayList(gl, iGLDisplayListIndexLocal);
			bIsDisplayListDirtyLocal = false;

		}
		iGLDisplayListToCall = iGLDisplayListIndexLocal;

		display(gl);
		checkForHits(gl);

		if (eBusyModeState != EBusyModeState.OFF)
			renderBusyMode(gl);

	}
@Override
	public void displayRemote(GL gl) {
	}

	
	@Override
	public void display(GL gl) {
		// processEvents();
		
		
		// GLHelperFunctions.drawAxis(gl);
		 //GLHelperFunctions.drawViewFrustum(gl, viewFrustum);
		// gl.glEnable(GL.GL_DEPTH_TEST);
		// clipToFrustum(gl);
		 
		 
            gl.glMatrixMode(GL.GL_PROJECTION);
			gl.glLoadIdentity();
			
			gl.glOrtho(0.0f, canvasWidth, canvasHeight, 0.0f, -1.0f, 1.0f);

			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
		 
	
		if (glMouseListener.getPickedPoint() != null) {
			mousePoint = glMouseListener.getPickedPoint();

			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
			double factorX = (double) canvasWidth / (double) viewport[2];
			double factorY = (double) canvasHeight / (double) viewport[3];

			mouseCoordX = (float) (mousePoint.getX() * factorX);
			mouseCoordY = (float) (mousePoint.getY() * factorY);
			
		}

		
		float helpX;
		float helpY;

		for (int i = 0; i < numberOfSquares; i++) {

			helpX = (float) squarePositionX[i];
			helpY = (float) squarePositionY[i];

			if (squareChecked[i]) {
				gl.glColor3f(0.6f, 0, 0);
			} else {
				gl.glColor3f(1, 0, 0);
			}

			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(helpX - (squareSizes[i] / 2), helpY
					- (squareSizes[i] / 2), 0);
			gl.glVertex3f(helpX + (squareSizes[i] / 2), helpY
					- (squareSizes[i] / 2), 0);
			gl.glVertex3f(helpX + (squareSizes[i] / 2), helpY
					+ (squareSizes[i] / 2), 0);
			gl.glVertex3f(helpX - (squareSizes[i] / 2), helpY
					+ (squareSizes[i] / 2), 0);
			gl.glEnd();

		}
		 
		
		//Collision detection (cheap & dirty)
		for (int i = 0; i < numberOfSquares; i++) {
			helpX = (float) squarePositionX[i];
			helpY = (float) squarePositionY[i];
			
			//reset the size of the squares
			squareSizes[i] = squareBaseSize;
			
			if ((mouseCoordX > (helpX - (squareSizes[i] / 2)))
			&& (mouseCoordY > (helpY - (squareSizes[i] / 2)))
			&& (mouseCoordX < (helpX + (squareSizes[i] / 2)))
			&& (mouseCoordY < (helpY + (squareSizes[i] / 2)))
			) {
				
				squareSizes[i]=squareBaseSize*2;
				squareChecked[i]=true;
				
			}

		}
		
		Rectangle screenRect = getParentGLCanvas().getBounds();
		
	
		 
		 
		 
		 
		
		
		receivedEyeData = tracker.getEyeTrackData();
		
		System.out.println("Eye position: " +receivedEyeData[0] + " / " + receivedEyeData[1]);
		
		
		int offsetX=upperLeftScreenPos.x;
		int offsetY=upperLeftScreenPos.y;
		
		
		
		receivedEyeData[0]=receivedEyeData[0]-(float)offsetX;
		receivedEyeData[1]=receivedEyeData[1]-(float)offsetY;

		System.out.println("Eye position korrigiert: " +receivedEyeData[0] + " / " + receivedEyeData[1]);
		
		GLHelperFunctions.drawPointAt(gl, new Vec3f(receivedEyeData[0] / screenRect.width *5f,
				 (receivedEyeData[1] / screenRect.height) * 5f , 0.01f));
				
		
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		
		float factorX =  canvasWidth /  (float)viewport[2];
		float factorY = canvasHeight / (float)viewport[3];


		gl.glBegin(GL.GL_LINE);
		gl.glVertex3f( receivedEyeData[0]*factorX,receivedEyeData[1]*factorY, 0);
		gl.glVertex3f(2, 2, 0);
		gl.glEnd();
		 
		// buildDisplayList(gl, iGLDisplayListIndexRemote);
		// if (!isRenderedRemote())
		// contextMenu.render(gl, this);
	}
private void buildDisplayList(final GL gl, int iGLDisplayListIndex) {

		if (bHasFrustumChanged) {
			bHasFrustumChanged = false;
		}

		gl.glNewList(iGLDisplayListIndex, GL.GL_COMPILE);
		gl.glEndList();
	}

	@Override
	public String getShortInfo() {
		if (contentVA == null)
			return "Scatterplot - 0 " + useCase.getContentLabel(false, true)
					+ " / 0 experiments";

		return "Scatterplot - " + contentVA.size() + " "
				+ useCase.getContentLabel(false, true) + " / "
				+ storageVA.size() + " experiments";
	}

	@Override
	public String getDetailedInfo() {
		StringBuffer sInfoText = new StringBuffer();
		sInfoText.append("<b>Type:</b> Scatter Plot\n");
		// TODO Everything

		// return sInfoText.toString();
		return "TODO: ScatterploT Deatil Info";
	}

	@Override
	protected void handlePickingEvents(EPickingType ePickingType,
			EPickingMode pickingMode, int iExternalID, Pick pick) {
		if (detailLevel == EDetailLevel.VERY_LOW) {
			return;
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDataWindowsView serializedForm = new SerializedDataWindowsView(
				dataDomain);
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public String toString() {
		return "Standalone Scatterplot, rendered remote: " + isRenderedRemote()
				+ ", contentSize: " + contentVA.size() + ", storageSize: "
				+ storageVA.size() + ", contentVAType: " + contentVAType
				+ ", remoteRenderer:" + getRemoteRenderingGLCanvas();
	}


	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
	}

	@Override
	public void broadcastElements(EVAOperation type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAllSelections() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GL gl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNumberOfSelections(SelectionType SelectionType) {
		// TODO Auto-generated method stub
		return 0;
	}
}
