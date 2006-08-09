/*
 * Project: GenView
 * 
 * Author: Michael Kalkusch
 * 
 *  creation date: 18-05-2005
 *  
 */
package cerberus.net.dwt.swing.component;

import gleem.linalg.Vec3f;

import javax.swing.JPanel;

import cerberus.manager.GeneralManager;
import cerberus.manager.type.ManagerObjectType;

import cerberus.net.dwt.base.ViewingAreaComponent;
import cerberus.data.IUniqueManagedObject;

/**
 * Abstract class provinging get and set for ViewingAreaComponent interface.
 * 
 * @see prometheus.net.dwt.swing.base.ViewingAreaComponent
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class DSwingJPanel extends JPanel implements
		ViewingAreaComponent, IUniqueManagedObject {

	
	protected GeneralManager refGeneralManager;
		
	protected int iDNetEventComponentId;
	
	
	/* --- Pan, Zoom, Rotate --- */
	protected Vec3f gui_v3f_pan = Vec3f.VEC_NULL;
	
	protected Vec3f gui_v3f_zoom = Vec3f.VEC_ONE;
	
	protected Vec3f gui_v3f_rotate = Vec3f.VEC_NULL;
	
	
	/**
	 * 
	 */
	public DSwingJPanel() {
		super();
	}
	
	
//	/**
//	 * @param arg0
//	 * @param arg1
//	 */
//	public DSwingJPanel(LayoutManager arg0, boolean arg1) {
//		super(arg0, arg1);
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * @param arg0
//	 */
//	public DSwingJPanel(LayoutManager arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}
//
//	/**
//	 * @param arg0
//	 */
//	public DSwingJPanel(boolean arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}
	

	/* (non-Javadoc)
	 * @see prometheus.net.dwt.base.ViewingAreaComponent#setVisibleArea(gleem.linalg.Vec3f, gleem.linalg.Vec3f, gleem.linalg.Vec3f)
	 */
	public void setVisibleArea(Vec3f v3fPanPercent, Vec3f v3fWindowPercent,
			Vec3f v3fRotation) {
		gui_v3f_pan 		= v3fPanPercent;
		gui_v3f_zoom 		= v3fWindowPercent;
		gui_v3f_rotate 	= v3fRotation;

	}

	/* (non-Javadoc)
	 * @see prometheus.net.dwt.base.ViewingAreaComponent#getVisibleAreaPanPercentage()
	 */
	public final Vec3f getVisibleAreaPanPercentage() {
		return gui_v3f_pan;
	}

	/* (non-Javadoc)
	 * @see prometheus.net.dwt.base.ViewingAreaComponent#getVisibleAreaWindowPercentage()
	 */
	public final Vec3f getVisibleAreaWindowPercentage() {
		return gui_v3f_zoom;
	}

	/* (non-Javadoc)
	 * @see prometheus.net.dwt.base.ViewingAreaComponent#getVisibleAreaRoation()
	 */
	public final Vec3f getVisibleAreaRoationXYZ() {
		return gui_v3f_rotate;
	}
	

	/**
	 * Get the singelton
	 * 
	 * @see prometheus.data.IUniqueManagedObject#getManager()
	 */
	public final GeneralManager getManager() {
		return refGeneralManager;
	}
	

	/**
	 * @see prometheus.data.IUniqueManagedObject#setId(int)
	 *
	 */
	public final void setId(int iSetDNetEventId) {
		
		//refParentCreator = (DComponentManager) creator;
		//FIXME check...	
		
		iDNetEventComponentId = iSetDNetEventId;
	}
	
	/**
	 * @see prometheus.data.IUniqueManagedObject#getId()
	 */
	public final int getId() {
		return iDNetEventComponentId;
	}
	
	/**
	 * @see prometheus.data.collection.UniqueManagedInterface#getBaseType()()
	 */
	public abstract ManagerObjectType getBaseType();


}
