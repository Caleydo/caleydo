package org.caleydo.core.view.opengl.util.vislink;

/**
 * Very simple ENUM which defines the 3 different styles of visual links
 * 
 * @author oliver
 */

public enum EVisLinkStyleType {

	STANDARD_VISLINK,
	SHADOW_VISLINK,
	HALO_VISLINK;

	public static EVisLinkStyleType getStyleType(int index) {
		switch (index) {
			case 1:
				return SHADOW_VISLINK;
			case 2:
				return HALO_VISLINK;
			default:
				return STANDARD_VISLINK;
		}
	}
}
