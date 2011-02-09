package org.caleydo.view.bookmark;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import org.caleydo.core.data.mapping.IDType;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.ARenderer;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ILayoutedElement;
import org.caleydo.core.view.opengl.util.text.MinSizeTextRenderer;

/**
 * Abstract base class for a single bookmark
 * 
 * @author Alexander Lex
 */
public abstract class ABookmark extends ARenderer implements ILayoutedElement {

	protected IDType idType;
	protected int id;

	MinSizeTextRenderer textRenderer;

	GLBookmarkView manager;

	ABookmarkContainer<?> parentContainer;

	/**
	 * The constructor takes a TextRenderer which is used to render all text
	 * 
	 * @param textRenderer
	 */
	public ABookmark(GLBookmarkView manager, ABookmarkContainer<?> parentContainer,
			IDType idType, MinSizeTextRenderer textRenderer) {
		this.textRenderer = textRenderer;
		this.manager = manager;
		this.idType = idType;
		this.parentContainer = parentContainer;

	}

	public void render(GL2 gl) {
		super.render(gl);

		float[] highlightColor = null;
		ArrayList<SelectionType> selectionTypes = parentContainer.selectionManager
				.getSelectionTypes(id);
		if (selectionTypes == null)
			return;

		SelectionType topLevelType = null;
		for (SelectionType selectionType : selectionTypes) {
			if (!selectionType.isVisible())
				continue;
			if (selectionType == SelectionType.NORMAL)
				continue;
			if (topLevelType == null)
				topLevelType = selectionType;
			else if (topLevelType.getPriority() < selectionType.getPriority())
				topLevelType = selectionType;
		}
		if (topLevelType == null)
			return;
		// highlightColor = new float[] { 1, 0, 1 };// topLevelType.getColor();

		// item.getID())) {
		highlightColor = topLevelType.getColor();
		//
		// }
		// int pickingID = pickingIDManager.getPickingID(this, item.getID());
		// gl.glPushName(pickingID);
		//
		// item.render(gl);
		//
		ElementLayout layout = getLayout();
		if (highlightColor != null) {

			float xOrigin = 0;
			float yOrigin = 0;
			// float width =
			float width = layout.getSizeScaledX();
			float height = layout.getSizeScaledY();

			gl.glColor3fv(highlightColor, 0);
			gl.glBegin(GL2.GL_LINE_LOOP);
			gl.glVertex3f(xOrigin, yOrigin, 0);
			gl.glVertex3f(xOrigin + width, yOrigin, 0);
			gl.glVertex3f(xOrigin + width, yOrigin + height, 0);
			gl.glVertex3f(xOrigin, yOrigin + height, 0);
			gl.glEnd();
			// GLHelperFunctions.drawPointAt(gl, width, height, 0);
		}

		// gl.glPopName();
	}

	public int getID() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.hashCode() == hashCode())
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
