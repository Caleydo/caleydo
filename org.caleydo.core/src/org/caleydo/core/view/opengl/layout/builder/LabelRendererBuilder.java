package org.caleydo.core.view.opengl.layout.builder;

import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.layout.Dims;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;
import org.caleydo.core.view.opengl.layout.Padding;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer.LabelAlignment;
import org.caleydo.core.view.opengl.layout.util.LabelRenderer2;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;

public class LabelRendererBuilder implements ILayoutRendererBuilder {
	private final CaleydoTextRenderer textRenderer;
	private final ILabelProvider label;
	private LabelAlignment alignment = LabelAlignment.LEFT;
	private IColor textColor = Colors.BLACK;
	private Padding padding = new Padding(Dims.xpixel(1), Dims.ypixel(1));

	public LabelRendererBuilder(CaleydoTextRenderer textRenderer, ILabelProvider label) {
		this.textRenderer = textRenderer;
		this.label = label;
	}

	@Override
	public LayoutRenderer build() {
		return new LabelRenderer2(label, padding, textColor, alignment, textRenderer);
	}

	public LabelRendererBuilder textColor(IColor color) {
		this.textColor = color;
		return this;
	}

	public LabelRendererBuilder padding(Padding p) {
		this.padding = p;
		return this;
	}

	public LabelRendererBuilder alignLeft() {
		this.alignment = LabelAlignment.LEFT;
		return this;
	}

	public LabelRendererBuilder alignCenter() {
		this.alignment = LabelAlignment.CENTER;
		return this;
	}

	public LabelRendererBuilder alignRight() {
		this.alignment = LabelAlignment.RIGHT;
		return this;
	}
}