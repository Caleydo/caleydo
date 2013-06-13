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
package org.caleydo.vis.rank.model;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.vis.rank.internal.event.CodeUpdateEvent;
import org.caleydo.vis.rank.model.mapping.MappedValueException;
import org.caleydo.vis.rank.model.mapping.MappingFunctions;
import org.caleydo.vis.rank.model.mapping.ScriptedMappingFunction;
import org.caleydo.vis.rank.model.mixin.ICollapseableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFilterColumnMixin;
import org.caleydo.vis.rank.model.mixin.IFloatRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IMappedColumnMixin;
import org.caleydo.vis.rank.model.mixin.IRankableColumnMixin;
import org.caleydo.vis.rank.model.mixin.IScriptedColumnMixin;
import org.caleydo.vis.rank.model.mixin.ISnapshotableColumnMixin;
import org.caleydo.vis.rank.ui.GLPropertyChangeListeners;
import org.caleydo.vis.rank.ui.detail.JSCombineEditorDialog;
import org.caleydo.vis.rank.ui.detail.MultiRankScoreSummary;
import org.caleydo.vis.rank.ui.detail.ScoreBarElement;
import org.caleydo.vis.rank.ui.detail.ValueElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Iterables;
import com.jogamp.common.util.IntObjectHashMap;

/**
 * a special combiner based on a scripted java script function
 * 
 * @author Samuel Gratzl
 * 
 */
public class ScriptedRankColumnModel extends AMultiRankColumnModel implements ICollapseableColumnMixin,
		IFilterColumnMixin, ISnapshotableColumnMixin, IScriptedColumnMixin {
	private static final String DEFAULT_CODE = "return mean(values)";
	private final static String prefix;
	private final static String postfix;

	static {
		// create code around the script
		StringBuilder b = new StringBuilder();
		b.append("importPackage(").append(MappingFunctions.class.getPackage().getName()).append(")\n");
		for (Method m : MappingFunctions.class.getDeclaredMethods()) {
			if (Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()))
				b.append("function ").append(m.getName()).append("() { return MappingFunctions.").append(m.getName())
						.append(".apply(this,arguments); }\n");
		}
		b.append("\n");
		b.append("function run(values) {\n");
		prefix = b.toString();

		b.setLength(0);
		b.append("\n}\n");
		b.append("clamp01(run(vs))");
		postfix = b.toString();
	}

	private final PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
			case IFilterColumnMixin.PROP_FILTER:
			case IMappedColumnMixin.PROP_MAPPING:
				cacheMulti.clear();
				cacheValues.clear();
				invalidAllFilter();
				propertySupport.firePropertyChange(evt);
				break;
			}
		}
	};

	private String code = DEFAULT_CODE;
	private IntObjectHashMap cacheMulti = new IntObjectHashMap();
	private IntObjectHashMap cacheValues = new IntObjectHashMap();
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
		this.engine = ScriptedMappingFunction.createEngine();
		cloneInitChildren();
	}

	@Override
	public ScriptedRankColumnModel clone() {
		return new ScriptedRankColumnModel(this);
	}

	public static String fullCode(String code) {
		StringBuilder b = new StringBuilder();
		b.append(prefix);
		if (!code.contains("return "))
			b.append("return ");
		b.append(code);
		b.append(postfix);
		return b.toString();
	}

	private CompiledScript compileScript() {
		if (script != null)
			return script;
		try {
			Compilable c = (Compilable) engine;
			script = c.compile(fullCode(code));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return script;
	}

	@Override
	protected void init(ARankColumnModel model) {
		super.init(model);
		cacheMulti.clear();
		cacheValues.clear();
		model.addPropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.addPropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
	}

	@Override
	protected void takeDown(ARankColumnModel model) {
		super.takeDown(model);
		model.removePropertyChangeListener(IFilterColumnMixin.PROP_FILTER, listener);
		model.removePropertyChangeListener(IMappedColumnMixin.PROP_MAPPING, listener);
		cacheMulti.clear();
		cacheValues.clear();
	}

	@Override
	protected void moved(int from, int to) {
		cacheMulti.clear();
		cacheValues.clear();
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
		if (cacheMulti.containsKey(row.getIndex()))
			return (MultiFloat) cacheMulti.get(row.getIndex());
		float[] s = new float[this.size()];
		for (int i = 0; i < s.length; ++i) {
			s[i] = ((IFloatRankableColumnMixin) get(i)).applyPrimitive(row);
		}
		MultiFloat f = new MultiFloat(-1, s);
		cacheMulti.put(row.getIndex(), f);
		return f;
	}

	@Override
	public float applyPrimitive(IRow row) {
		if (children.isEmpty())
			return 0;
		if (cacheValues.containsKey(row.getIndex()))
			return (Float) cacheValues.get(row.getIndex());
		MultiFloat splittedValue = getSplittedValue(row);

		float v = runScript(splittedValue);
		cacheValues.put(row.getIndex(), v);
		return v;
	}

	private float runScript(MultiFloat splittedValue) {
		try {
			Bindings bindings = engine.createBindings();
			bindings.put("vs", splittedValue.values);
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

	@Override
	public int compare(IRow o1, IRow o2) {
		return Float.compare(applyPrimitive(o1), applyPrimitive(o2));
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
	public void setCode(String code) {
		if (this.code.equals(code)) {
			return;
		}
		invalidAllFilter();
		cacheValues.clear();
		String bak = this.code;
		propertySupport.firePropertyChange(PROP_CODE, this.code, this.code = code);
		propertySupport.firePropertyChange(IMappedColumnMixin.PROP_MAPPING, bak, this.code);
	}

	/**
	 * @return the code, see {@link #code}
	 */
	@Override
	public String getCode() {
		return code;
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
		private void onCodeUpdate(CodeUpdateEvent event) {
			((ScriptedRankColumnModel) model).setCode(event.getCode());
		}

	}

	@Override
	public void orderBy(IRankableColumnMixin model) {
		// nothing to do
	}
}
