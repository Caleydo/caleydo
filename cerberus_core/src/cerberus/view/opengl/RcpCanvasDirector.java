package cerberus.view.opengl;

import org.eclipse.swt.widgets.Composite;

import cerberus.data.collection.SetType;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewGLCanvasManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.IView;
import cerberus.view.ViewType;
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
extends AGLCanvasDirector 
implements IGLCanvasDirector, IView {
	
	/**
	 * Constructor
	 */
	public RcpCanvasDirector( final int iGLCanvasId,
			final int iGLEventListenerId, 
			final IGeneralManager setGeneralManager,
			final JoglCanvasForwarderType canvasForwarderType) {
		
		super(iGLCanvasId, setGeneralManager);
		
		this.iGlEventListernerId = iGLEventListenerId;
		
		if  (forwarder_GLEventListener == null) {
			
			switch (canvasForwarderType) {
			case DEFAULT_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasForwarder(refGeneralManager,
						this, 
						iGlEventListernerId );
				break;
			case GLEVENT_LISTENER_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasDirectForwarder(refGeneralManager,
						this, 
						iGlEventListernerId );
				break;
				
			case ONLY_2D_FORWARDER:
				forwarder_GLEventListener = new JoglCanvasForwarder(refGeneralManager,
						this, 
						iGlEventListernerId );
				break;
				
				default:
					throw new GeneViewRuntimeException("initView() unsupported JoglCanvasForwarderType=[" +
							canvasForwarderType.toString() + "]");
			}
		}
		
		IViewGLCanvasManager canvasManager = 
			refGeneralManager.getSingelton().getViewGLCanvasManager();

		canvasManager.registerGLCanvasDirector( this, iGLCanvasId);
		canvasManager.registerGLEventListener( forwarder_GLEventListener, iGlEventListernerId );
	}
		
	@Override
	public ManagerObjectType getBaseType() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addSetId(int[] set) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawView() {

		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] getAllSetId() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ViewType getViewType() {

		return ViewType.NONE;
	}

	@Override
	public boolean hasSetId(int setId) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final void initView() {

		// TODO Auto-generated method stub
		
	}
	
	@Override
	public final void initViewRCP(Composite swtContainer) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllSetIdByType(SetType setType) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeSetId(int[] set) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParentContainerId(int parentContainerId) {

		// TODO Auto-generated method stub
		
	}

	
	/* ----- END: forward to cerberus.view.jogl.JoglCanvasForwarder ----- */
	/* ------------------------------------------------------------------ */
}
