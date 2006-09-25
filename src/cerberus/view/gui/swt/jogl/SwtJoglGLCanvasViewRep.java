package cerberus.view.gui.swt.jogl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.media.opengl.GLCanvas;

import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.view.gui.swt.base.AJoglViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.awt.jogl.TriangleMain;
import cerberus.view.gui.opengl.IGLCanvasDirector;
import cerberus.view.gui.opengl.IGLCanvasUser;
import cerberus.util.exception.CerberusRuntimeException;

public class SwtJoglGLCanvasViewRep 
extends AJoglViewRep 
implements IView, IGLCanvasDirector
{
	
	private AtomicBoolean abEnableRendering;
	
	protected int iGLEventListernerId = 99000;
	
	protected int iGLCanvasId = 88000;
	
	protected Vector <IGLCanvasUser> vecGLCanvasUser;
	
	public SwtJoglGLCanvasViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel)
	{
		super(refGeneralManager, iViewId, iParentContainerId, sLabel);
		
		vecGLCanvasUser = new Vector <IGLCanvasUser> ();
		
		abEnableRendering = new AtomicBoolean( true );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#initView()
	 */
	public void initView()
	{
		TriangleMain renderer = new TriangleMain();
		
		IViewGLCanvasManager canvasManager = 
			refGeneralManager.getSingelton().getViewManager();

		canvasManager.registerGLEventListener( renderer, iGLEventListernerId );
		canvasManager.registerGLCanvas( refGLCanvas, iGLCanvasId );
		
		canvasManager.addGLEventListener2GLCanvasById( iGLEventListernerId, iGLCanvasId );
		
		super.setGLEventListener( renderer );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#addGLCanvasUser(cerberus.view.gui.opengl.IGLCanvasUser)
	 */
	public void addGLCanvasUser( IGLCanvasUser user ) {
		if ( vecGLCanvasUser.contains( user ) ) {
			throw new CerberusRuntimeException("addGLCanvasUser() try to same user twice!");
		}
		vecGLCanvasUser.addElement( user );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#removeGLCanvasUser(cerberus.view.gui.opengl.IGLCanvasUser)
	 */
	public void removeGLCanvasUser( IGLCanvasUser user ) {
		if ( ! vecGLCanvasUser.remove( user ) ) {
			throw new CerberusRuntimeException("removeGLCanvasUser() failed, because the user is not registered!");
		}
		user.destroy();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#removeAllGLCanvasUsers()
	 */
	public void removeAllGLCanvasUsers() {
		
		abEnableRendering.set( false );
		
		Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
		
		while ( iter.hasNext() ) {		
			iter.next().destroy();
		}
		vecGLCanvasUser.clear();
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#getAllGLCanvasUsers()
	 */
	public Collection<IGLCanvasUser> getAllGLCanvasUsers() {
		
		return new LinkedList <IGLCanvasUser> ();
	}	
	
	/* (non-Javadoc)
	 * @see cerberus.view.gui.swt.jogl.IGLCanvasDirector#getGLCanvas()
	 */
	public GLCanvas getGLCanvas() {
		return this.refGLCanvas;
	}

	public void render() {
		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			iter.next().render( refGLCanvas );
		}
	}
	
	public void update() {
		if ( abEnableRendering.get() ) 
		{
			Iterator <IGLCanvasUser> iter = vecGLCanvasUser.iterator();
			iter.next().update( refGLCanvas );
		}
	}
}
