/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.vis.lineup.internal.event.DualCodeUpdateEvent;
import org.caleydo.vis.lineup.model.mapping.JavaScriptFunctions;
import org.caleydo.vis.lineup.model.mapping.MappedValueException;
import org.caleydo.vis.lineup.model.mapping.ScriptedMappingFunction;
import org.caleydo.vis.lineup.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IManualComparatorMixin;
import org.caleydo.vis.lineup.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.lineup.model.mixin.IScriptedColumnMixin;
import org.caleydo.vis.lineup.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.lineup.ui.GLPropertyChangeListeners;
import org.caleydo.vis.lineup.ui.RenderStyle;
import org.caleydo.vis.lineup.ui.detail.JSCombineEditorDialog;
import org.caleydo.vis.lineup.ui.detail.MultiRankScoreSummary;
import org.caleydo.vis.lineup.ui.detail.ScoreBarElement;
import org.caleydo.vis.lineup.ui.detail.ValueElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterables;
import com.jogamp.common.util.IntObjectHashMap;
import com.jogamp.common.util.IntObjectHashMap.Entry;

/**
 * a special combiner based on a scripted java script function
 *
 * @author Samuel Gratzl
 *
 */
public class ScriptedRankColumnModel extends AMultiRankColumnModel implements ICollapseableColumnMixin,
		IFilterColumnMixin, ISnapshotableColumnMixin, IScriptedColumnMixin, IManualComparatorMixin {
	private static final String DEFAULT_CODE = "return mean(values)";
	private static final String DEFAULT_ORDER_CODE = "return -compare(a.value,b.value)";
	private final static String prefix;
	private final static String infix;
	private final static String postfix;

	static {
		// create code around the script
		StringBuilder b = new StringBuilder();
		b.append("importPackage(").append(JavaScriptFunctions.class.getPackage().getName()).append(")\n");
		for (Method m : JavaScriptFunctions.class.getDeclaredMethods()) {
			if (Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
				b.append("function ").append(m.getName()).append("() { return JavaScriptFunctions.")
						.append(m.getName())
						.append(".apply(this,arguments); }\n");
		}
		b.append("\n");
		b.append("function run(v) {\n");
		b.append("  var values = v.values;\n");
		b.append("  var inferred = v.inferred;\n");
		b.append("  var raws = v.raws;\n");
		prefix = b.toString();

		b.setLength(0);
		b.append("\n}\n");
		b.append("function order(a,b) {\n");
		infix = b.toString();

		b.setLength(0);
		b.append("\n}\n");
		b.append("if(mode === 'order') order(a,b);\n");
		b.append("else if(mode === 'apply') clamp01(run(v));\n");
		b.append("else {\n");
		b.append("  a.value = clamp01(run(a));\n");
		b.append("  b.value = clamp01(run(b));\n");
		b.append("  order(a,b);");
		b.append("}");
		postfix = b.toString();
	}

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case IFilterColumnMixin.PROP_FILTER:
			case IMappedColumnMixin.PROP_MAPPING:
				cacheMulti.clear();
				invalidAllFilter();
				propertySupport.firePropertyChange(evt);
				break;
			}
		}
	};

	private String code = DEFAULT_CODE;
	private String codeOrder = DEFAULT_ORDER_CODE;
	private IntObjectHashMap cacheMulti = new IntObjectHashMap();
	private final ScriptEngine engine;
	private CompiledScript script;

	public ScriptedRankColumnModel() {
		this(Color.GRAY, new Color(0.95f, .95f, .95f));
	}

	public ScriptedRankColumnModel(Color color, Color bgColor) {
		super(color, bgColor, "CODE");
		this.engine = ScriptedMappingFunction.createEngine();
	}

	public ScriptedRankColumnModel(ScriptedRankColumnModel copy) {
		super(copy);
		this.code = copy.code;
		this.codeOrder = copy.codeOrder;
		this.engine = ScriptedMappingFunction.createEngine();
		cloneInitChildren();
	}

	@Override
	public ScriptedRankColumnModel clone() {
		return new ScriptedRankColumnModel(this);
	}

	public static String fullCode(String code, String codeOrder) {
		StringBuilder b = new StringBuilder();
		b.append(prefix);
		if (!code.contains("return "))
			b.append("return ");
		b.append(code);
		b.append(infix);
		if (!code.contains("return "))
			b.append("return ");
		b.append(codeOrder);
		b.append(postfix);
		return b.toString();
	}

	private CompiledScript compileScript() {
		if (script != null)
			return script;
		try {
			Compilable c = (Compilable) engine;
			script = c.compile(fullCode(code, codeOrder));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return script;
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		cacheMulti.clear();
		model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		cacheMulti.clear();
	}

	@Override
	protected void moved(int from, int to) {
		cacheMulti.clear();
		super.moved(from, to);
	}

	@Override
	public GLElement createSummary(boolean interactive) {
		return new ScriptedMultiRankScoreSummary(this, interactive);
	}

	@Override
	public ValueElement createValue() {
		return new RepaintingGLElement();
	}

	private boolean isRecursive(ARankColumnModel model) {
		return false; // (model instanceof MaxCompositeRankColumnModel);
	}

	@Override
	public boolean canAdd(ARankColumnModel model) {
		return !isRecursive(model) && super.canAdd(model);
	}

	@Override
	public boolean isFlatAdding(ACompositeRankColumnModel model) {
		return isRecursive(model);
	}

	@Override
	public MultiFloat getSplittedValue(IRow row) {
		CacheRow f = getCacheRow(row);
		return new MultiFloat(-1, f.values);
	}

	private CacheRow getCacheRow(IRow row) {
		if (cacheMulti.containsKey(row.getIndex()))
			return ((CacheRow) cacheMulti.get(row.getIndex()));
		float[] s = new float[this.size()];
		String[] raws = new String[s.length];
		boolean[] inferred = new boolean[s.length];
		for (int i = 0; i < s.length; ++i) {
			ARankColumnModel child = get(i);
			if (child instanceof IFloatRankableColumnMixin) {
				s[i] = ((IFloatRankableColumnMixin)child).applyPrimitive(row);
				inferred[i] = ((IFloatRankableColumnMixin) child).isValueInferred(row);
			} else {
				s[i] = Float.NaN;
				inferred[i] = false;
			}
			raws[i] = get(i).getValue(row);
		}
		CacheRow cacheRow = new CacheRow(s, raws, inferred);
		cacheMulti.put(row.getIndex(), cacheRow);
		return cacheRow;
	}

	@Override
	public float applyPrimitive(IRow row) {
		if (children.isEmpty())
			return 0;
		if (cacheMulti.containsKey(row.getIndex()) && ((CacheRow) cacheMulti.get(row.getIndex())).getValue() != null)
			return ((CacheRow) cacheMulti.get(row.getIndex())).value;

		float v = runScript(getCacheRow(row));
		((CacheRow) cacheMulti.get(row.getIndex())).value = v;
		return v;
	}

	/**
	 * returns the weights how much a individual column contributes to the overall scores, i.e. the normalized weights
	 *
	 * @return
	 */
	public float[] getWeights() {
		float[] r = new float[this.size()];
		float base = width - getSpaces();
		int i = 0;
		for (ARankColumnModel col : this) {
			r[i++] = col.getWidth() / base;
		}
		return r;
	}

	/**
	 * @return
	 */
	private float getSpaces() {
		return RenderStyle.STACKED_COLUMN_PADDING * 2 + RenderStyle.COLUMN_SPACE * size();
	}

	private float runScript(CacheRow row) {
		try {
			Bindings bindings = engine.createBindings();
			bindings.put("weights", getWeights());
			bindings.put("v", row);
			bindings.put("mode", "apply");
			CompiledScript c = compileScript();
			if (c == null)
				return Float.NaN;
			Object r = compileScript().eval(bindings);
			if (r instanceof Number)
				return ((Number) r).floatValue();
		} catch (MappedValueException e) {
			return e.getValue();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Float.NaN;
	}

	private int runOrderScript(CacheRow a, CacheRow b) {
		try {
			Bindings bindings = engine.createBindings();
			bindings.put("a", a);
			bindings.put("b", b);
			bindings.put("mode", "order");
			CompiledScript c = compileScript();
			if (c == null)
				return 0;
			Object r = compileScript().eval(bindings);
			if (r instanceof Number)
				return ((Number) r).intValue();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int compare(IRow o1, IRow o2) {
		applyPrimitive(o1);
		applyPrimitive(o2);
		CacheRow r1 = getCacheRow(o1);
		CacheRow r2 = getCacheRow(o2);
		if (codeOrder.equalsIgnoreCase(DEFAULT_ORDER_CODE)) {
			// fast way
			return -Float.compare(r1.value, r2.value);
		} else
			return runOrderScript(r1, r2);
	}

	@Override
	public boolean isValueInferred(IRow row) {
		for (IFloatRankableColumnMixin child : Iterables.filter(this, IFloatRankableColumnMixin.class))
			if (child.isValueInferred(row))
				return true;
		return false;
	}

	/**
	 * @param code
	 *            setter, see {@link code}
	 */
	public void setCode(String code, String codeOrder) {
		if (this.code.equals(code) && this.codeOrder.equals(codeOrder)) {
			return;
		}
		invalidAllFilter();
		for (Iterator<Entry> it = this.cacheMulti.iterator(); it.hasNext();) {
			((CacheRow) it.next().value).value = null;
		}
		Pair<String, String> bak = Pair.make(this.code, this.codeOrder);
		Pair<String, String> new_ = Pair.make(code, codeOrder);
		this.code = code;
		this.codeOrder = codeOrder;
		propertySupport.firePropertyChange(PROP_CODE, bak, new_);
		propertySupport.firePropertyChange(IMappedColumnMixin.PROP_MAPPING, bak, new_);
	}

	/**
	 * @return the code, see {@link #code}
	 */
	@Override
	public String getCode() {
		return code;
	}

	/**
	 * @return the codeOrder, see {@link #codeOrder}
	 */
	public String getCodeOrder() {
		return codeOrder;
	}

	@Override
	public void editCode(final GLElement summary, IGLElementContext context) {
		context.getSWTLayer().run(new ISWTLayerRunnable() {
			@Override
			public void run(Display display, Composite canvas) {
				JSCombineEditorDialog editor = new JSCombineEditorDialog(canvas.getShell(), summary,
						ScriptedRankColumnModel.this);
				editor.open();
			}
		});
	}

	private class RepaintingGLElement extends ScoreBarElement {
		private final PropertyChangeListener l = GLPropertyChangeListeners.repaintOnEvent(this);

		public RepaintingGLElement() {
			super(ScriptedRankColumnModel.this);
		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);
			addPropertyChangeListener(PROP_CHILDREN, l);
			addPropertyChangeListener(PROP_CODE, l);
		}

		@Override
		protected void takeDown() {
			removePropertyChangeListener(PROP_CHILDREN, l);
			removePropertyChangeListener(PROP_CODE, l);
			super.takeDown();
		}
	}

	private static class ScriptedMultiRankScoreSummary extends MultiRankScoreSummary {

		public ScriptedMultiRankScoreSummary(AMultiRankColumnModel model, boolean interactive) {
			super(model, interactive);
		}

		@ListenTo(sendToMe = true)
		private void onCodeUpdate(DualCodeUpdateEvent event) {
			((ScriptedRankColumnModel) model).setCode(event.getCode(), event.getCodeOrder());
		}

	}

	@Override
	public void orderBy(IRankableColumnMixin model) {
		parent.orderBy(model);
	}

	public static class CacheRow {
		private final boolean[] inferred;
		private final float[] values;
		private final String[] raws;
		private Float value = null;

		public CacheRow(float[] values, String[] raws, boolean[] inferred) {
			this.raws = raws;
			this.values = values;
			this.inferred = inferred;
		}

		/**
		 * @return the inferred, see {@link #inferred}
		 */
		public boolean[] getInferred() {
			return inferred;
		}

		/**
		 * @return the values, see {@link #values}
		 */
		public float[] getValues() {
			return values;
		}

		/**
		 * @return the raws, see {@link #raws}
		 */
		public String[] getRaws() {
			return raws;
		}

		/**
		 * @return the value, see {@link #value}
		 */
		public Float getValue() {
			return value;
		}
	}
}
