package cerberus.util.opengl;

/**
 * Object stores the pathway that is currently dragged
 * until it is dropped.
 * 
 * @author Marc Streit
 *
 */
public class GLDragAndDrop {

	private int iDraggedObjectId = -1;
	
	private boolean bDragActionRunning = false;
	
	public void setDraggedObjectId(final int iDragObjectId) {
		
		this.iDraggedObjectId = iDragObjectId;
	}
	
	public int getDraggedObjectedId() {
		
		return iDraggedObjectId;
	}
	
	public void startDragAction() {
		
		bDragActionRunning = true;
	}
	
	public void stopDragAction() {
		
		bDragActionRunning = false;
		iDraggedObjectId = -1;
	}
	
	public boolean isDragActionRunning() {
		
		return bDragActionRunning;
	}
}
