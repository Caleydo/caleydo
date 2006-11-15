/**
 * 
 */
package cerberus.util.midi.device;

/**
 * @author kalkusch
 *
 */
public interface IMidiCallback {

	public void callbackSetValue( String value, int control, int iValue );
}
