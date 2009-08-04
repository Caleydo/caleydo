package org.caleydo.core.view.opengl.canvas.bookmarking;

import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.event.data.BookmarkEvent;

/**
 * @author Alexander Lex
 */
public abstract class ABookmarkContainer {

	protected Dimensions dimensions;

	protected ArrayList<ABookmark> bookmarkItems;

	public ABookmarkContainer() {
		dimensions = new Dimensions();
	}

	public Dimensions getDimensions() {
		return dimensions;
	}

	public void render(GL gl) {
		float yOrigin = dimensions.getYOrigin();
		for (ABookmark item : bookmarkItems) {
			item.getDimensions().setOrigins(0, yOrigin);
			yOrigin += item.getDimensions().getHeight();
			item.render(gl);
		}
	}
	
	public abstract <IDDataType> void handleNewBookmarkEvent(BookmarkEvent<IDDataType> event);

}
