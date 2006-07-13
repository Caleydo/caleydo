/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.protocol.interaction;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.Serializable;

/**
 * Encapsulate mouse events and provide serialized object to send over the network.
 * 
 * @author Michael Kalkusch
 *
 */
public class SuperMouseEvent extends MouseEvent
	implements Serializable {

	/**
	 * Requested by interface Serializable
	 */ 
	static final long serialVersionUID = 80008000;
	
	//private MouseEvent cloneMouseEvent = null;
	
	private boolean bIsDragged = false;
	
	private boolean bIsClicked = false;
	
	private boolean bIsReleased = false;
		
	private boolean bIsExiting = false;
	
	private boolean bIsEntering = false;
	
	private boolean bIsPressed = false;
	
	/**
	 * Identification for source of the event. 
	 * 
	 * An InputSource can either be a machine or an application on a machine.
	 * Each InputSource is linked to one virtual mouse and one virtuel keyboard.
	 */
	private short iInputSourceId = -1;
	
	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 * @param arg6
	 * @param arg7
	 * @param arg8
	 */
	public SuperMouseEvent(Component arg0, int arg1, long arg2, int arg3,
			int arg4, int arg5, int arg6, boolean arg7, int arg8) {
		super(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param arg4
	 * @param arg5
	 * @param arg6
	 * @param arg7
	 */
	public SuperMouseEvent(Component arg0, int arg1, long arg2, int arg3,
			int arg4, int arg5, int arg6, boolean arg7) {
		super(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		// TODO Auto-generated constructor stub
	}
	
	public SuperMouseEvent( SuperMouseEvent setSuperMouseEvent ) {
		super( setSuperMouseEvent.getComponent(),
				setSuperMouseEvent.getID(),
				setSuperMouseEvent.getWhen(),
				setSuperMouseEvent.getModifiers(),
				setSuperMouseEvent.getX(),
				setSuperMouseEvent.getY(),
				setSuperMouseEvent.getClickCount(),
				setSuperMouseEvent.isPopupTrigger(),
				setSuperMouseEvent.getButton() );
		
		//cloneMouseEvent = setSuperMouseEvent.getMouseEvent();
	}
	
	public SuperMouseEvent( SuperMouseEvent setSuperMouseEvent, 
			int iSetMousePosX, int iSetMousePosY ) {
		super( setSuperMouseEvent.getComponent(),
				setSuperMouseEvent.getID(),
				setSuperMouseEvent.getWhen(),
				setSuperMouseEvent.getModifiers(),
				iSetMousePosX,
				iSetMousePosY,
				setSuperMouseEvent.getClickCount(),
				setSuperMouseEvent.isPopupTrigger(),
				setSuperMouseEvent.getButton() );
		
		//cloneMouseEvent = setSuperMouseEvent.getMouseEvent();
	}

	public SuperMouseEvent( MouseEvent setMouseEvent ) {
		super( setMouseEvent.getComponent(),
				setMouseEvent.getID(),
				setMouseEvent.getWhen(),
				setMouseEvent.getModifiers(),
				setMouseEvent.getX(),
				setMouseEvent.getY(),
				setMouseEvent.getClickCount(),
				setMouseEvent.isPopupTrigger(),
				setMouseEvent.getButton() );
		
		//cloneMouseEvent = setMouseEvent;
	}
	
	/**
	 * Get the InputSourceId to match event to a machine
	 * @return InputSourceId
	 */
	public short getInputSourceId() {
		return iInputSourceId;
	}
	
	/**
	 * Set InputSourceId. 
	 * 
	 * Note, that this Id is set by the "EventServer" based on the input stream.
	 * 
	 * @param setInputSourceId new InputSourceId
	 */
	public void setInputSourceId( final short setInputSourceId ) {
		iInputSourceId = setInputSourceId;
	}
	
	/**
	 * Set the state for a dragging event.
	 * 
	 * @param bSetIsDragged TRUE is dragging is happening
	 */
	public void setIsDragged( final boolean bSetIsDragged ) {
		bIsDragged = bSetIsDragged;
		if ( bSetIsDragged )
			bIsPressed = false;
	}
	
	public void setIsClicked( final boolean bSetIsClicked ) {
		bIsClicked = bSetIsClicked;
		if ( bSetIsClicked )
			bIsPressed = false;
	}
	
	public void setIsReleased( final boolean bSetIsReleased ) {
		bIsReleased = bSetIsReleased;
		
		if ( bSetIsReleased )
			bIsPressed = false;
	}
	
	public void setIsEntering( final boolean bSetIsEntering ) {
		bIsEntering = bSetIsEntering;
	}
	
	public void setIsExiting( final boolean bSetIsExiting ) {
		bIsExiting = bSetIsExiting;
	}
	public void setIsPressed( final boolean bSetIsPressed ) {
		bIsPressed = bSetIsPressed;
	}
	
	/**
	 * Get TRUE if a dragging event is current.
	 * 
	 * @return TRUE if dragging is in progress.
	 */
	public boolean isMouseDragged( ) {
		return bIsDragged;
	}
	
	public boolean isMouseClicked( ) {
		return bIsClicked;
	}
	
	public boolean isMouseReleased( ) {
		return bIsReleased;
	}
	
	public boolean isMouseExiting( ) {
		return bIsExiting;
	}
	
	public boolean isMouseEntering( ) {
		return bIsEntering;
	}
	public boolean isMousePressed( ) {
		return bIsPressed;
	}
	
//	public MouseEvent getMouseEvent() {
//		return cloneMouseEvent;
//	}
//	
//	public void setMouseEvent( final MouseEvent setMouseEvent ) {
//		cloneMouseEvent = setMouseEvent;
//	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = "[ x,y=" + this.getX() + 
			"," + this.getY() + " ";
		
		if ( this.source != null ) {
			
			result += " src=" + this.source.toString();
		}
		else {
			result += " src= null";
		}
		
		result += " id=" + this.id;
				
		result += " button_id=" + this.getButton();
		
		result += " clicked#=" + this.getClickCount();
		
		// mouse states...
		result += "(";
		
		if (bIsPressed)
			result += "P";
		else 
			result += "-";
		
		if (this.bIsReleased)
			result += "R";
		else 
			result += "-";
		
		if (bIsClicked)
			result += "C";
		else 
			result += "-";
		
		if (this.bIsDragged)
			result += "D";
		else 
			result += "-";
		
		if (this.bIsEntering)
			result += "E";
		else 
			result += "-";
		
		if (this.bIsExiting)
			result += "X";
		else 
			result += "-";

		
		result += ")";
		
		if ( this.getComponent() == null  ) {
			result += " comp=null ]";
		} 
		else {
			result += " comp= set ]";
		}
			
		return result;
	}

}
