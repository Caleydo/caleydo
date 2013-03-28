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

import static org.caleydo.vis.rank.ui.RenderStyle.LABEL_HEIGHT;
import static org.caleydo.vis.rank.ui.RenderStyle.binsForWidth;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.io.gui.dataimport.widget.ICallback;
import org.caleydo.core.util.function.ArrayFloatList;
import org.caleydo.core.util.function.FloatFunctions;
import org.caleydo.core.util.function.FloatStatistics;
import org.caleydo.core.util.function.IFloatList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.GLSandBox;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLComboBox;
import org.caleydo.core.view.opengl.layout2.basic.RadioController;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.vis.rank.internal.event.CodeUpdateEvent;
import org.caleydo.vis.rank.internal.ui.ButtonBar;
import org.caleydo.vis.rank.model.DataUtils;
import org.caleydo.vis.rank.model.SimpleHistogram;
import org.caleydo.vis.rank.model.mapping.IMappingFunction;
import org.caleydo.vis.rank.model.mapping.PiecewiseMapping;
import org.caleydo.vis.rank.model.mapping.ScriptedMappingFunction;
import org.caleydo.vis.rank.ui.RenderStyle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


/**
 * ui for a {@link PiecewiseMapping}
 *
 * @author Samuel Gratzl
 *
 */
public class MappingFunctionUI extends GLElementContainer implements GLButton.ISelectionCallback, IGLLayout,
		GLComboBox.ISelectionCallback<EStandardMappings> {

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
		GLComboBox<EStandardMappings> mappings = new GLComboBox<>(Arrays.asList(EStandardMappings.values()),
				GLComboBox.DEFAULT, GLRenderers.fillRect(Color.WHITE));
		mappings.setRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.color(Color.BLACK).drawRect(0, 0, w, h);
				g.drawText("Standard Mappings", 1, h * 0.25f, w - 2, h * 0.5f);
			}
		});
		mappings.setCallback(this);
		mappings.setSize(RenderStyle.BUTTON_WIDTH * 4, Float.NaN);
		mappings.setTooltip("Select a standard mapping function");
		buttons.add(mappings);
		buttons.addSpacer();
		GLButton b = new GLButton();
		b.setRenderer(GLRenderers.fillImage(RenderStyle.ICON_MAPPING_RESET));
		b.setTooltip("Reset mapping function, to it's default value");
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
		text.setBounds(PADDING, y + h - MappingFunctionUI.TEXT_HEIGHT - 3, w - PADDING * 2,
				MappingFunctionUI.TEXT_HEIGHT);
		h -= MappingFunctionUI.TEXT_HEIGHT - 3;

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
		b.setTooltip("Switch to the " + mode.getName() + " mode");
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

	@Override
	public void onSelectionChanged(GLComboBox<? extends EStandardMappings> widget, EStandardMappings item) {
		if (item == null)
			return;
		item.apply(model);
		AMappingFunctionMode<?> active = getActive();
		active.reset();
		repaintMapping();
		widget.setSelected(-1); // reset selection
		fireCallback();
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
				new JSEditorDialog(new Shell(), MappingFunctionUI.this, (ScriptedMappingFunction) model).open();
			}
		});
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(ResourceLocators.classLoader(this.getClass().getClassLoader()));
		g.color(0.95f, .95f, .95f, 0.95f).fillRect(0, 0, w, h);
		super.renderImpl(g, w, h);
		g.popResourceLocator();
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
		return DataUtils.getHist(binsForWidth(w, raw.size()), raw.map(model));
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

	class RawHistogramElement extends HistogramElement {
		private IFloatList data;
		private RawHistogramElement(IFloatList raw) {
			super("Raw", color, backgroundColor);
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

			SimpleHistogram hist = DataUtils
					.getHist(binsForWidth((vertical ? h : w) - LABEL_HEIGHT, data.size()), data);
			render(g, vertical, model.getActMin(), model.getActMax(), w, h, minM, maxM, hist,
					getLayoutDataAs(Boolean.class, Boolean.FALSE));
		}
	}

	class NormalizedHistogramElement extends HistogramElement {
		public NormalizedHistogramElement() {
			super("Score", color, backgroundColor);
		}
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
			render(g, vertical, 0, 1, w, h, minM, maxM, computeHist((vertical ? h : w) - LABEL_HEIGHT),
					getLayoutDataAs(Boolean.class, Boolean.FALSE));
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
			if (model instanceof ScriptedMappingFunction)
				openJSEditor();
		}
	}

	public static void main(String[] args) {
		PiecewiseMapping model;
		float[] arr;
		{
			model = new PiecewiseMapping(0, 100);
			// linear
			// filtering
			// model.clear();
			// model.put(20, 0);
			// model.put(60, 1);

			String values = "100 100 100 95.6 100 100 100 96 88.5 51.3 100 96.2 94 96.4 100 60.2 96.1 95.1 " + //
					"94.3 81.9 96.8 100 83.8 82.5 99.4 83.8 88.6 96 75.8 97.6 96 100 79.7 66.9 81.1 " + //
					"100 83.4 68.6 93.5 72.4 95.9 68.8 85.3 96.6 77.8 83.3 84.5 97.6 81 69.6 71.5 " + //
					"95.1 91 70.7 42.8 66.7 63.2 100 47.4 71.9 96.5 74.6 51.7 77 74.5 75.3 75 81 " + //
					"100 35.7 82.2 96.2 69.8 23.7 47.2 66 80.5 43.2 70.9 70.3 51.5 70.1 78.2 22 " + //
					"48.4 64.2 44.6 76.9 48.6 47.7 84.8 96.8 69.2 78.9 83.2 42.8 37.4 26.3 82.2 " + //
					"38 83.3 69.5 72.3 41.8 66.9 21.6 69.5 46.6 43.2 69.5 38.1 65.3 67.3 37.9 26.8 " + //
					"71.6 39.5 27.2 27.3 26.3 31.2 40.4 70.7 66.6 82.3 73.8 55.8 67.3 27.7 37.1 68.6 " + //
					"36.3 60.2 45.5 12.5 39.8 57.4 64.7 76.2 25 76.5 63.5 64.4 40.6 29.1 67 38.8 47.6 " + //
					"29.5 86.7 24.3 35.4 9.9 65.3 25.8 54.4 61.4 38.2 39.2 46.1 27 49.7 63.8 7.1 62.3 " + //
					"73 35.8 39.4 40.9 59.5 48.8 44.2 66.1 67.3 69.8 30.1 49.3 77.5 53.7 33.3 23.6 " + //
					"65.9 67.9 20.5 26.4 32.1 40.4 25.5 59 62 71.5 27 34.9 56.4 75.9 32.5 22.6 78.8 " + //
					"20.9 75.1 66.3 45.5 22.3 23.9 27.6 47.7 52.4 36.9 24.9 54.1 48.9 79.4 74.8 40.7 " + //
					"34.4 42.9 60.7 77.7 18.3 24.1 63.4 45.5 61.3 30.4 66.1 62.1 82.7 44.9 50.5 81.9 " + //
					"18 21.5 71.2 66.8 26.7 63 23 30.7 23.6 4.1 71.8 24.7 83 39.3 77.2 52.9 24.2 31.2 " + //
					"59.3 27.3 28.1 26.7 53.2 21.9 22.3 37.8 65.1 48.3 22.6 26.6 19.8 75 73.4 24.2 " + //
					"28.2 30.8 39.5 21.6 60.1 27.3 36.1 64.4 52.7 66.2 24.1 6.6 50.6 29.7 21.7 65.6 " + //
					"34.1 1 69.3 25.4 36.6 43.8 39.4 29.8 27.7 40.4 17.1 49.9 26.5 43.6 25.3 18.4 30.3 " + //
					"28.9 3.9 51.3 26.5 12.9 5.8 58.8 27.7 74.9 36.4 21.5 23.1 73 8.2 27.4 20.3 18 " + //
					"27.5 28.4 19.9 16.7 27.5 23.5 62.9 18.6 29.7 54.5 47.3 39.1 26.1 47.6 13.4 22.9 " + //
					"19.8 56.3 23 40.2 27.4 34.6 36.6 29.2 24.8 44.1 88.6 65.5 22.9 28.9 63.2 42.3 21.9 " + //
					"42.7 43.6 70.3 22.7 28 27.4 29.7 35.3 29.3 40.6 23.4 30.6 36.8 20.3 32.3 39.8 17.7 " + //
					"23.8 22.6 16.9 18.1 36.9 26.8 17.1 8.6 3.1 15 43.5 55.4 9.4 21.4 28.9 23.9 19.8 " + //
					"31.1 19.6 20.4 32.9 49.4 85.8 43.4 27.2 22.6 31.5 39.4 13.6 52.4 36.1 69.3 43.4 " + //
					"54.8 41.3 50.3 40.6 51.9 39.7 37.5 37.4 42.7 61.6 39.5 43.2 49.9 41.9 49 45 61.9 45.4";
			String[] vs = values.split(" ");
			arr = new float[vs.length];
			for (int i = 0; i < arr.length; ++i)
				arr[i] = Float.parseFloat(vs[i]);
		}

		// complex
		// model.clear();
		// model.put(0, 1);
		// model.put(50, .2f);
		// model.put(100, 1);


		IFloatList data = new ArrayFloatList(arr);
		FloatStatistics s = data.computeStats();
		model.setActStatistics(s);
		final MappingFunctionUI root = new MappingFunctionUI(model, data, Color.decode("#9ECAE1"),
				Color.decode("#DEEBF7"), null);
		root.addMode(new PiecewiseMappingParallelUI(model, true));
		root.addMode(new PiecewiseMappingCrossUI(model, true));

		GLSandBox.main(args, root, new GLPadding(0), new Dimension(300, 350));
	}
}
