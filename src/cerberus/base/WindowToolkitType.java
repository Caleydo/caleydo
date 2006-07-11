/**
 * 
 */
package cerberus.base;

/**
 * Define kind of window toolkit.
 * 
 * @author kalkusch
 *
 */
public enum WindowToolkitType {

	SWT("Eclipse Windowing Toolkit, version jan 2006"),
	
	SWING("Swing Windowing Toolkit, version jdk 1.5.06"),
	
	AWT("Advanced Windowing Toolkit, version jdk 1.5.06");
	
	/**
	 * Remark describing window toolkit.
	 */
	private final String sRemark;
	
	/**
	 * Constructor.
	 * 
	 * @param setRemark details on toolkit and version of toolkit.
	 */
	private WindowToolkitType(String setRemark) {
		this.sRemark = setRemark;
	}
	
	/**
	 * Details on toolkit and required version of toolkit.
	 * 
	 * @return toolkit description adn version.
	 */
	public String getCommand() {
		return this.sRemark;
	}
}
