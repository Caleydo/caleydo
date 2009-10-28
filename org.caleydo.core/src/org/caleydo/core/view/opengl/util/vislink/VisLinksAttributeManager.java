package org.caleydo.core.view.opengl.util.vislink;

import org.caleydo.core.view.opengl.renderstyle.ConnectionLineRenderStyle;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class VisLinksAttributeManager {
	
	private static int color = 0;
	static boolean animation;
	static EVisLinkStyleType style;
	
	public static void toggleAnimation() {
		if(ConnectionLineRenderStyle.ANIMATION)
			ConnectionLineRenderStyle.ANIMATION = false;
		else
			ConnectionLineRenderStyle.ANIMATION = true;
	}
	
	public static void setConnectionLinesWidth(float width) {
		ConnectionLineRenderStyle.CONNECTION_LINE_WIDTH = width;		
	}
	
	public static void setConnectionLineStyle(EVisLinkStyleType style) {
		ConnectionLineRenderStyle.CONNECTION_LINE_STYLE = style;
	}
	
	//Fixme: temporary
	public static void switchConnectionLineStyle() {
		if(ConnectionLineRenderStyle.CONNECTION_LINE_STYLE == EVisLinkStyleType.STANDARD_VISLINK) {
			ConnectionLineRenderStyle.CONNECTION_LINE_STYLE = EVisLinkStyleType.SHADOW_VISLINK;
		}
		else if(ConnectionLineRenderStyle.CONNECTION_LINE_STYLE == EVisLinkStyleType.SHADOW_VISLINK) {
			ConnectionLineRenderStyle.CONNECTION_LINE_STYLE = EVisLinkStyleType.HALO_VISLINK;
		}
		else {
			ConnectionLineRenderStyle.CONNECTION_LINE_STYLE = EVisLinkStyleType.STANDARD_VISLINK;
		}
	}
	
	public static void switchConnectionLineColor() {
		if(color == 3) {
			ConnectionLineRenderStyle.CONNECTION_LINE_COLOR = GeneralRenderStyle.MOUSE_OVER_COLOR;
			color = 0;
		}
		else if(color == 0) {
//			ConnectionLineRenderStyle.CONNECTION_LINE_COLOR = new float[]{ 0.54f, 0.17f, 0.89f, 1f}; // blue-violet
			ConnectionLineRenderStyle.CONNECTION_LINE_COLOR = new float[]{ 1f, 0f, 1f, 1f}; // pink
			color = 1;
		}
		else if(color == 1) {
//			ConnectionLineRenderStyle.CONNECTION_LINE_COLOR = new float[]{ 0.79f, 1f, 0.44f, 1f}; // dark olive green
			ConnectionLineRenderStyle.CONNECTION_LINE_COLOR = new float[]{ 0.5f, 1f, 0.5f, 1f}; // dark olive green
			color = 2;
		}
		else if(color == 2) {
//			ConnectionLineRenderStyle.CONNECTION_LINE_COLOR = new float[]{ 1f, 0.49f, 0.31f, 1f}; // coral
			ConnectionLineRenderStyle.CONNECTION_LINE_COLOR = new float[]{ 0f, 1f, 1f, 1f}; // light blue
			color = 3;
		}
	}
	
	public static void toggleAnimatedHighlighting() {
		if(ConnectionLineRenderStyle.ANIMATED_HIGHLIGHTING) {
			ConnectionLineRenderStyle.ANIMATED_HIGHLIGHTING = false;
			ConnectionLineRenderStyle.CONNECTION_LINE_STYLE = style;
			ConnectionLineRenderStyle.ANIMATION = animation;
		}
		else {
			style = ConnectionLineRenderStyle.CONNECTION_LINE_STYLE;
			animation = ConnectionLineRenderStyle.ANIMATION;
			ConnectionLineRenderStyle.CONNECTION_LINE_STYLE = EVisLinkStyleType.HALO_VISLINK;
			ConnectionLineRenderStyle.ANIMATION = false;
			ConnectionLineRenderStyle.ANIMATED_HIGHLIGHTING = true;
		}
	}

}
