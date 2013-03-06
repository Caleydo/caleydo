/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.vis.rank.ui.mapping;

import static org.caleydo.vis.rank.ui.RenderStyle.binsForWidth;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.function.ArrayFloatList;
import org.caleydo.core.util.function.FloatFunctions;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.vis.rank.internal.event.CodeUpdateEvent;
import org.caleydo.vis.rank.internal.ui.ButtonBar;
import org.caleydo.vis.rank.model.DataUtils;
import org.caleydo.vis.rank.model.SimpleHistogram;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.caleydo.vis.rank.ui.RenderUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * ui for a {@link PiecewiseMapping}
 *
 * @author Samuel Gratzl
 *
 */
public class MappingFunctionUI extends GLElementContainer implements GLButton.ISelectionCallback, IGLLayout {

	private static final float PADDING = 5;

	private static final int RAW_HIST = 0;
	private static final int NORMAL_HIST = 1;
	private static final int SPECIFIC = 4;
	private static final int TEXT = 2;
	private static final int BUTTONS = 3;

	public static final float TEXT_HEIGHT = 50;

	protected final IMappingFunction model;
	protected final IFloatList raw;

	protected final Color color;
	protected final Color backgroundColor;
	/**
	 * callback to call when the mapping changes
	 */
	private final ICallback<? super IMappingFunction> callback;

	private List<AMappingFunctionMode<?>> modes = new ArrayList<>();

	private final RadioController radio = new RadioController(this);

	public MappingFunctionUI(IMappingFunction model, IFloatList data, Color color, Color bgColor,
			ICallback<? super IMappingFunction> callback) {
		this.callback = callback;
		this.model = model;
		this.raw = data;
		this.color = color;
		this.backgroundColor = bgColor;

		this.add(new RawHistogramElement(raw.map(FloatFunctions.normalize(model.getActMin(), model.getActMax()))));
		this.add(new NormalizedHistogramElement());
		this.add(new CodeElement());
		ButtonBar buttons = new ButtonBar();
		buttons.addSpacer();
		GLButton b = new GLButton();
		b.setRenderer(GLRenderers.fillImage(RenderStyle.ICON_MAPPING_RESET));
		b.setCallback(this);
		b.setPickingObjectId(-1);
		b.setSize(RenderStyle.BUTTON_WIDTH, Float.NaN);
		buttons.add(b);
		this.add(buttons);

		setLayout(this);
	}

	private AMappingFunctionMode<?> getActive() {
		return (AMappingFunctionMode<?>) this.get(SPECIFIC);
	}

	@Override
	public void doLayout(List<? extends IGLLayoutElement> children, float w, float h) {
		IGLLayoutElement buttons = children.get(MappingFunctionUI.BUTTONS);
		buttons.setBounds(0, 0, w, RenderStyle.BUTTON_WIDTH);
		float y = RenderStyle.BUTTON_WIDTH + 2;
		h -= y;

		IGLLayoutElement text = children.get(MappingFunctionUI.TEXT);
		text.setBounds(PADDING, y + h - MappingFunctionUI.TEXT_HEIGHT, w - PADDING * 2, MappingFunctionUI.TEXT_HEIGHT);
		h -= MappingFunctionUI.TEXT_HEIGHT;

		h -= PADDING;
		w -= 2 * PADDING;
		getActive().doLayout(children.get(RAW_HIST), children.get(NORMAL_HIST), children.get(SPECIFIC), PADDING, y, w,
				h);
	}

	/**
	 * @param piecewiseMappingCrossUI
	 */
	public void addMode(AMappingFunctionMode<?> mode) {
		modes.add(mode);
		GLButton b = new GLButton(EButtonMode.BUTTON);
		final String icon =mode.getIcon();
		b.setRenderer(GLRenderers.fillImage(icon));
		b.setSelectedRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.color(Color.RED).fillRect(0, 0, w, h);
				g.fillImage(icon, 0,0,w,h);
			}
		});
		b.setPickingObjectId(modes.size() - 1);
		b.setSize(RenderStyle.BUTTON_WIDTH, Float.NaN);
		((GLElementContainer) this.get(BUTTONS)).add(modes.size() - 1, b);
		if (modes.size() == 1) {
			this.add(modes.get(0));
			b.setSelected(true);
		}
		radio.add(b);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		int id = button.getPickingObjectId();
		AMappingFunctionMode<?> active = getActive();
		if (id < 0) { // reset
			model.reset();
			active.reset();
			fireCallback();
		} else if (active != modes.get(id)) {
			// switch
			set(SPECIFIC, modes.get(id));
		}
	}

	@ListenTo(sendToMe = true)
	private void onCodeChanged(CodeUpdateEvent event) {
		model.fromJavaScript(event.getCode());
		AMappingFunctionMode<?> active = (AMappingFunctionMode<?>) this.get(SPECIFIC);
		active.reset();
		repaintMapping();
		fireCallback();
	}

	public void openJSEditor() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				new JSEditorDialog(new Shell(), MappingFunctionUI.this, model).open();
			}
		});
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(0.95f, .95f, .95f, 0.95f).fillRect(0, 0, w, h);
		super.renderImpl(g, w, h);
	}

	protected final void fireCallback() {
		if (callback != null)
			callback.on(model);
	}

	void repaintMapping() {
		get(RAW_HIST).repaint();
		get(NORMAL_HIST).repaint();
		get(TEXT).repaint();
		get(SPECIFIC).repaint();
	}

	protected float normalizeRaw(float v) {
		return (v - model.getActMin()) / (model.getActMax() - model.getActMin());
	}

	protected float inverseNormalize(float n) {
		return n * (model.getActMax() - model.getActMin()) + model.getActMin();
	}

	protected SimpleHistogram computeHist(float w) {
		return DataUtils.getHist(binsForWidth(w), raw.map(model));
	}

	protected void renderMapping(GLGraphics g, float w, float h, boolean cross, boolean isNormalLeftTop) {
		g.color(.3f, .3f, .3f, .3f);
		GL2 gl = g.gl;
		final float z = g.z();
		gl.glBegin(GL.GL_LINES);
		if (raw.size() < 1000) {
			for (int i = 0; i < raw.size(); ++i) {
				renderLine(w, h, gl, z, i, cross, isNormalLeftTop);
			}
		} else {
			// sample 1000 elements
			List<Integer> r = new ArrayList<>(1000);
			for (int i = 0; i < 1000; ++i)
				r.add(i);
			Collections.shuffle(r);
			for (int i = 0; i < 1000; ++i) {
				int index = r.get(i);
				renderLine(w, h, gl, z, index, cross, isNormalLeftTop);
			}
		}

		gl.glEnd();
	}

	private void renderLine(float w, float h, GL2 gl, final float z, int index, boolean cross, boolean isNormalLeftTop) {
		float v = raw.getPrimitive(index);
		if (cross) {
			float x = normalizeRaw(v) * w;
			float y = (1 - model.apply(v)) * h;
			gl.glVertex3f(x, h, z);
			gl.glVertex3f(isNormalLeftTop ? 0 : w, y, z);
		} else if (isNormalLeftTop) { // horizontal parallel
			float x = normalizeRaw(v) * w;
			float y = model.apply(v) * w;
			gl.glVertex3f(x, h, z);
			gl.glVertex3f(y, 0, z);
		} else { // vertical parallel
			float x = (1 - normalizeRaw(v)) * h;
			float y = (1 - model.apply(v)) * h;
			gl.glVertex3f(0, x, z);
			gl.glVertex3f(w, y, z);
		}
	}

	class HistogramElement extends GLElement {
		protected void render(GLGraphics g, boolean vertical, float min, float max, float w, float h, float minM, float maxM,
				SimpleHistogram hist) {
			if (vertical) {
				g.drawText(Formatter.formatNumber(max), 0, -14, 38, 12, VAlign.LEFT);
				g.drawText(Formatter.formatNumber(min), 0, h + 2, 38, 12, VAlign.LEFT);
			} else {
				g.drawText(Formatter.formatNumber(min), -40, h - 14, 38, 12, VAlign.RIGHT);
				g.drawText(Formatter.formatNumber(max), w + 2, h - 14, 38, 12, VAlign.LEFT);
			}

			g.color(backgroundColor).fillRect(0, 0, w, h);
			g.color(color).drawRect(0, 0, w, h);

			if (vertical) {
				g.save();
				g.gl.glRotatef(-90, 0, 0, 1);
				g.move(-h, 0);
				RenderUtils.renderHist(g, hist, h, w, -1, color, Color.BLACK);

				if (!Float.isNaN(minM)) {
					g.color(0, 0, 0, 0.25f).fillRect(0, 0, minM * h, w);
				}
				if (!Float.isNaN(maxM)) {
					g.color(0, 0, 0, 0.25f).fillRect(maxM * h, 0, (1 - maxM) * h, w);
				}
				g.restore();
			} else {
				RenderUtils.renderHist(g, hist, w, h, -1, color, Color.BLACK);

				if (!Float.isNaN(minM)) {
					g.color(0, 0, 0, 0.25f).fillRect(0, 0, minM * w, h);
				}
				if (!Float.isNaN(maxM)) {
					g.color(0, 0, 0, 0.25f).fillRect(maxM * w, 0, (1 - maxM) * w, h);
				}
			}
		}
	}

	class RawHistogramElement extends HistogramElement {
		private IFloatList data;
		private RawHistogramElement(IFloatList raw) {
			this.data = raw;
		}
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			boolean vertical = w < h;
			float[] m = model.getMappedMin();
			float minM = Float.NaN;
			if (m[0] > model.getActMin()) {
				minM = normalizeRaw(m[0]);
			}
			float maxM = Float.NaN;
			m = model.getMappedMax();
			if (m[0] < model.getActMax()) {
				maxM = normalizeRaw(m[0]);
			}

			SimpleHistogram hist = DataUtils.getHist(binsForWidth(vertical ? h : w), data);
			render(g, vertical, model.getActMin(), model.getActMax(), w, h, minM, maxM, hist);
		}
	}

	class NormalizedHistogramElement extends HistogramElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			super.renderImpl(g, w, h);
			boolean vertical = w < h;
			float minM = model.getMinTo();
			if (minM <= 0)
				minM = Float.NaN;
			float maxM = model.getMaxTo();
			if (maxM >= 1)
				maxM = Float.NaN;
			render(g, vertical, 0, 1, w, h, minM, maxM, computeHist(vertical ? h : w));
		}
	}

	class CodeElement extends PickableGLElement {
		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(Color.WHITE).fillRect(0, 0, w, h);
			g.color(Color.BLACK).drawRect(0, 0, w, h);
			String[] lines = model.toJavaScript().split("\n");
			float y = 2;
			float lineHeight = 10;
			for (String l : lines) {
				if ((y + lineHeight) >= h)
					break;
				g.drawText(l, 2, y, w - 2, lineHeight);
				y += lineHeight + 1;
			}
		}

		@Override
		protected void onDoubleClicked(Pick pick) {
			openJSEditor();
		}
	}

	public static void main(String[] args) {
		PiecewiseMapping model = new PiecewiseMapping(0, Float.NaN);
		float[] arr = new float[100];
		Random r = new Random(100);
		for (int i = 0; i < arr.length; ++i)
			arr[i] = r.nextFloat();
		IFloatList data = new ArrayFloatList(arr);
		float[] s = data.computeStats();
		model.setAct(s[0], s[1]);
		final MappingFunctionUI root = new MappingFunctionUI(model, data, Color.GRAY, Color.LIGHT_GRAY, null);
		root.addMode(new PiecewiseMappingCrossUI(model, true));
		root.addMode(new PiecewiseMappingParallelUI(model, true));
		root.addMode(new PiecewiseMappingCrossUI(model, false));
		root.addMode(new PiecewiseMappingParallelUI(model, false));

		GLSandBox.main(args, root, new GLPadding(10), new Dimension(260, 260));
	}
}
