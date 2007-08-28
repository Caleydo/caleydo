/**
 * 
 */
package cerberus.view.jogl.mouse;

import cerberus.view.jogl.IJoglMouseListener;
import cerberus.view.opengl.util.GLDragAndDrop;


/**
 * @author michael
 *
 */
public class DragAndDropMouseListener extends PickingJoglMouseListener {

	protected GLDragAndDrop dragAndDrop;
	
	/**
	 * @param refParentGearsMain
	 */
	public DragAndDropMouseListener(IJoglMouseListener refParentGearsMain) {

		super(refParentGearsMain);
		
		dragAndDrop = new GLDragAndDrop();
	}

	
	/**
	 * @return the dragAndDrop
	 */
	public final GLDragAndDrop getDragAndDrop() {
	
		return dragAndDrop;
	}

	
	/**
	 * @param dragAndDrop the dragAndDrop to set
	 */
	public final void setDragAndDrop(GLDragAndDrop dragAndDrop) {
	
		this.dragAndDrop = dragAndDrop;
	}

}
