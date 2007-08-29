package cerberus.view.opengl;

import java.util.Collection;

import cerberus.data.AUniqueManagedObject;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.jogl.JoglCanvasDirectForwarder;
import cerberus.view.jogl.JoglCanvasForwarder;
import cerberus.view.jogl.JoglCanvasForwarderType;

/**
 * Special RCP Canvas Director for RCP external call.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class RcpCanvasDirector 
extends AUniqueManagedObject 
implements IGLCanvasDirector {

	// FIXME: must be loaded from XML file!
	protected int iGLEventListernerId = 99000;
	
	private JoglCanvasForwarder forwarder_GLEventListener = null;
	
	/**
	 * Constructor
	 */
	public RcpCanvasDirector( final int iGLCanvasId,
			final int iGLEventListenerId, 
			final IGeneralManager setGeneralManager,
			final JoglCanvasForwarderType canvasForwarderType) {
		
		super(iGLCanvasId, setGeneralManager);
		
		this.iGLEventListernerId = iGLEventListenerId;
		
		if  (forwarder_GLEventListener == null) {
			
			switch (canvasForwarderType) {
			case DEFAULT_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasForwarder(refGeneralManager,
						this, 
						iGLEventListernerId );
				break;
			case GLEVENT_LISTENER_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasDirectForwarder(refGeneralManager,
						this, 
						iGLEventListernerId );
				break;
				
			case ONLY_2D_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasForwarder(refGeneralManager,
						this, 
						iGLEventListernerId );
				break;
				
				default:
					throw new GeneViewRuntimeException("initView() unsupported JoglCanvasForwarderType=[" +
							canvasForwarderType.toString() + "]");
			}
		}
		
		IViewGLCanvasManager canvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();

		canvasManager.registerGLCanvasDirector( this, iGLCanvasId);
		canvasManager.registerGLEventListener( forwarder_GLEventListener, iGLEventListernerId );
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#addGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public final void addGLCanvasUser( IGLCanvasUser user ) {
		forwarder_GLEventListener.addGLCanvasUser(user);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#removeGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public final void removeGLCanvasUser( IGLCanvasUser user ) {
		forwarder_GLEventListener.removeGLCanvasUser(user);
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#removeAllGLCanvasUsers()
	 */
	public final void removeAllGLCanvasUsers() {
		
		forwarder_GLEventListener.removeAllGLCanvasUsers();
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.opengl.IGLCanvasDirector#containsGLCanvasUser(cerberus.view.opengl.IGLCanvasUser)
	 */
	public final boolean containsGLCanvasUser(IGLCanvasUser user) {

		return forwarder_GLEventListener.containsGLCanvasUser(user);		
	}
	
	/* (non-Javadoc)
	 * @see cerberus.view.swt.jogl.IGLCanvasDirector#getAllGLCanvasUsers()
	 */
	public final Collection<IGLCanvasUser> getAllGLCanvasUsers() {
		
		return forwarder_GLEventListener.getAllGLCanvasUsers();
	}	
	
	public final JoglCanvasForwarder getJoglCanvasForwarder() {

		return forwarder_GLEventListener;
	}

	@Override
	public void destroyDirector() {

		// TODO Auto-generated method stub
		
	}

	@Override
	public ManagerObjectType getBaseType() {

		// TODO Auto-generated method stub
		return null;
	}
	
	/* ----- END: forward to cerberus.view.jogl.JoglCanvasForwarder ----- */
	/* ------------------------------------------------------------------ */
}
