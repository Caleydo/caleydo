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
			// model.put(0, 0);
			// model.put(50, .5f);
			// model.put(100, .5f);

			model.clear();
			model.put(0, 1);
			model.put(100, 0);

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
		// {
		// model = new PiecewiseMapping(-1, 1);
		// // linear
		// // filtering
		// model.clear();
		// model.put(-1, 1);
		// model.put(1, 1);
		// model.put(0f, 0.2f);
		//
		// String values =
		// "-0.154140952 -0.586343362 0.374016439 0.227318483 0.783827310 -0.047648931 0.230704662 0.425859512 0.655556594 0.074390675 -0.243971387 -0.172969758 0.631103474 -0.057738837 -0.372980178 0.269653950 0.533675823 -0.223728817 0.124082439 -0.333196424 0.016876392 0.389619805 0.298070628 -0.392708797 -0.626045085 0.010332575 0.248036521 0.092892591 0.597712924 0.258114587 0.529040262 -0.186614471 -0.151914615 0.127658611 -0.352333600 -0.086641563 0.058641087 -0.569197828 -0.040778502 0.212589144 -0.239603798 -0.010206286 -0.277096756 -0.023161890 0.372818971 -0.035245841 -0.337989288 -0.585333562 0.313666887 -1.023651804 -0.780508548 -0.038280169 0.058313564 -0.130692486 -0.936178558 -0.436789522 -0.006945045 0.444431312 -1.195465439 -0.455847200 -0.216274083 -1.516001547 -0.618861630 0.022440557 -0.872342218 -0.884578233 -0.318258501 0.890986741 0.159766778 -0.060892310 0.323571049 0.171176234 -0.333030089 -0.748253906 0.564454987 -0.012704545 -0.348147008 0.015199671 0.097412107 -0.455610032 -1.114997895 -0.021735878 -0.636458698 -0.534438571 0.491747153 0.391326765 -0.015240645 0.354462620 0.162931761 0.769183047 0.857274977 0.397900328 0.351683471 -0.551026676 0.736831535 0.268652417 0.488052008 -0.555791487 -0.531661313 -0.634391370 0.439572635 -0.241643690 -0.697487809 0.325250864 0.137701142 -0.589254097 -0.059705667 0.421163695 -0.838094296 -0.034650647 0.109152769 0.420506483 -0.262448666 -0.118576518 -0.008248335 0.776945557 -0.305790337 0.435032072 -0.349325143 0.769533531 0.401954524 -0.132096170 0.226531381 -0.430868377 0.177504291 0.072582467 -1.182319962 -0.064757626 0.060265394 0.288750582 0.340307468 0.199586095 0.053322919 0.253376522 0.463230167 -0.177590056 -0.366923999 -0.202488171 -0.400585672 -0.788306628 -1.249626897 -0.696807089 0.072083503 0.140606707 0.137646710 -0.245148335 -0.065150368 -0.131095517 -0.640536998 -0.266582381 0.616753024 -0.514294336 0.170546124 0.033312099 0.462161921 0.319270202 0.376243454 -0.765034513 0.669788506 -0.301561686 0.456658535 0.109382965 1.143272554 0.294243119 -0.322312202 -0.321464263 0.434256986 -0.267469386 0.224492776 0.077193648 -0.178486987 -0.744705679 0.368392461 0.146207422 0.017580285 0.089446475 0.489737687 -0.646961146 -0.054617658 0.769371740 -0.491851524 -0.106117040 -0.108433216 0.102389798 -0.316106902 -0.334905937 -0.730149684 0.315262420 0.505102558 0.579214398 -0.997496720 -0.026181817 0.384882556 -0.029796304 -0.223434604 -0.251893343 -0.818159538 0.154319491 -0.607079571 0.130403824 0.459331903 -0.471019100 -0.345207142 0.218150225 -0.586400605 0.282953849 -0.474967621 -0.767499287 0.303063050 0.478708647 -0.400385763 -0.033453387 -0.356446450 0.384350268 -0.919797329 -0.361477872 -0.326202219 0.485137457 0.042754093 0.311079112 0.154029940 0.404465006 0.053030245 0.088159396 0.317545577 -0.613531020 0.447097933 -0.891596053 -0.692672774 0.023152802 -0.129658410 -0.209861445 0.926181029 0.378439769 0.264899329 -0.156737602 0.905928490 0.231627877 0.376547823 -0.303617273 -0.456554089 0.126316471 0.089551929 -0.619648741 0.208130278 -0.227557325 1.139067142 -0.184142748 0.578434657 0.36218581";
		//
		// String[] vs = values.split(" ");
		// arr = new float[vs.length];
		// int j = 0;
		// for (int i = 0; i < arr.length; ++i) {
		// float v = Float.parseFloat(vs[i]);
		// if (v < -1 || v > 1)
		// continue;
		// arr[j++] = v;
		// }
		// arr = Arrays.copyOf(arr, j);
		// }



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
