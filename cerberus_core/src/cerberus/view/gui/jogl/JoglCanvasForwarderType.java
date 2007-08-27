/**
 * 
 */
package cerberus.view.gui.jogl;


/**
 * @author Michael Kalkusch
 *
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
