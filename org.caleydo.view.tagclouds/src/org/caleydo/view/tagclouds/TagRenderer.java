package org.caleydo.view.tagclouds;

import java.util.Set;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.storage.NominalStorage;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.util.text.MinSizeTextRenderer;

public class TagRenderer extends LayoutRenderer {
	private MinSizeTextRenderer textRenderer;
	private String text = "";

	private GLTagCloud tagCloud;
	private ContentSelectionManager contentSelectionManager;
	private Integer storageID;

	private float color[];

	public TagRenderer(MinSizeTextRenderer textRenderer, String text) {
		this.textRenderer = textRenderer;
		this.text = text;
		color = new float[] { 0, 0, 0};
	}

	public TagRenderer(MinSizeTextRenderer textRenderer, GLTagCloud tagCloud,
			Integer storageID) {
		this.textRenderer = textRenderer;
		this.tagCloud = tagCloud;
		this.contentSelectionManager = tagCloud.getContentSelectionManager();
		this.storageID = storageID;
		color = SelectionType.MOUSE_OVER.getColor();
	}

	public void selectionUpdated() {
		Set<Integer> selectedElements = contentSelectionManager
				.getElements(SelectionType.MOUSE_OVER);
		if (selectedElements.isEmpty())
			return;

		int contentID = -1;
		for (Integer tempID : selectedElements) {
			contentID = tempID;
			break;
		}

		NominalStorage<String> storage = (NominalStorage<String>) tagCloud.getSet().get(
				storageID);
		text = storage.getRaw(contentID);
	}

	public void render(GL2 gl) {

		textRenderer.setWindowSize(x, y);
		textRenderer.setColor(color);
		textRenderer.renderTextInBounds(gl, text, 0, 0, 0, x, y);
	};

}
