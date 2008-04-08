/**
 * 
 */
package org.geneview.core.view.jogl;

/**�
 * Define type of OpenGL canvas forwarder
 * 
 * @see org.geneview.core.view.jogl.JoglCanvasForwarder
 * @see org.geneview.core.manager.IViewGLCanvasManager#setJoglCanvasForwarderType(JoglCanvasForwarderType)
 * @see org.geneview.core.view.swt.jogl.SwtJoglGLCanvasViewRep#SwtJoglGLCanvasViewRep(org.geneview.core.manager.IGeneralManager, int, int, int, String, JoglCanvasForwarderType)
 * @see org.geneview.core.view.swt.jogl.SwtJoglGLCanvasViewRep#initView()
 * 
 * @author Michael Kalkusch
 *
 * @deprecated Camera relevant parameters (rotation, translation) etc. are directly specified in XML command
 */
public enum JoglCanvasForwarderType
{
	DEFAULT_FORWARDER(),
	NO_ROTATION_FORWARDER(),
	NO_TRANSLATION_FORWARDER(),
	ONLY_2D_FORWARDER(),
	GLEVENT_LISTENER_FORWARDER(),
	NONE();
}
