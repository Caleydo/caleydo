/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.vis.lineup.model.mapping.extra.Filter;

/**
 * @author Samuel Gratzl
 *
 */
public class ScriptedMappingFunction extends AMappingFunction {
	private static final Logger log = Logger.create(ScriptedMappingFunction.class);
	/**
	 *
	 */
	private static final String DEFAULT_CODE = "return linear(value_min, value_max, value, 0, 1)";
	private final static String prefix;
	private final static String postfix;

	private String code = "";
	private final ScriptEngine engine;
	private CompiledScript script;

	private Map<String, Object> extraBindings = new HashMap<>(4);

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
		b.append("function run(value, value_min, value_max) {\n");
		prefix = b.toString();

		b.setLength(0);
		b.append("\n}\n");
		b.append("clamp01(run(v, v_min, v_max))");
		postfix = b.toString();
	}

	public ScriptedMappingFunction(double fromMin, double fromMax) {
		super(fromMin, fromMax);
		this.engine = createEngine();
		this.script = null;
		this.code = DEFAULT_CODE;
	}

	public static ScriptEngine createEngine() {
		return new ScriptEngineManager().getEngineByName("JavaScript");
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

	@Override
	public void reset() {
		this.code = DEFAULT_CODE;
		this.script = null;
	}

	@Override
	public boolean isComplexMapping() {
		return !DEFAULT_CODE.equals(code);
	}

	private CompiledScript compileScript() {
		if (script != null)
			return script;
		try {
			Compilable c = (Compilable) engine;
			script = c.compile(fullCode(code));
		} catch (ScriptException e) {
			log.error("can't compile: " + fullCode(code), e);
		}
		return script;
	}



	public ScriptedMappingFunction(ScriptedMappingFunction copy) {
		super(copy);
		this.code = copy.code;
		this.engine = copy.engine;
		this.script = copy.script;
	}

	@Override
	public ScriptedMappingFunction clone() {
		return new ScriptedMappingFunction(this);
	}

	/**
	 * @param code
	 *            setter, see {@link code}
	 */
	@Override
	public void fromJavaScript(String code) {
		if (this.code.equals(code))
			return;
		this.code = code;
		if (this.code.contains("filter.") && !extraBindings.containsKey("filter"))
			extraBindings.put("filter", new Filter());
		this.script = null;
	}

	@Override
	public String toJavaScript() {
		return code;
	}

	@Override
	public double[] getMappedMin() {
		// TODO no idea how to compute that out of a script
		return new double[] { getActMin(), 0 };
	}

	@Override
	public double[] getMappedMax() {
		return new double[] { getActMax(), 1 };
	}

	@Override
	public double getMaxTo() {
		return 1;
	}

	@Override
	public double getMinTo() {
		return 0;
	}

	@Override
	public boolean isMappingDefault() {
		return true;
	}

	public void addExtraBinding(String key, Object value) {
		this.extraBindings.put(key, value);
	}

	public void removeExtraBinding(String key) {
		this.extraBindings.remove(key);
	}

	public <T> T getExtraBinding(String key, Class<T> type) {
		Object r = extraBindings.get(key);
		if (type.isInstance(r))
			return type.cast(r);
		return null;
	}

	@Override
	public double apply(double in) {
		try {
			Bindings bindings = engine.createBindings();
			bindings.put("v", in);
			addBindings(bindings);
			CompiledScript c = compileScript();
			if (c == null)
				return Double.NaN;
			Object r = compileScript().eval(bindings);
			if (r instanceof Number)
				return ((Number) r).doubleValue();
		} catch (MappedValueException e) {
			return e.getValue();
		} catch (ScriptException e) {
			log.warn("can't execute: " + fullCode(code), e);
		}
		return Double.NaN;
	}

	/**
	 * @param b
	 */
	public void addBindings(Bindings bindings) {
		bindings.put("v_min", getActMin());
		bindings.put("v_max", getActMax());
		if (actStats != null) {
			bindings.put("data", actStats);
		}
		for (Map.Entry<String, Object> extra : extraBindings.entrySet()) {
			Object v = extra.getValue();
			if (v instanceof Filter && actStats != null) {
				((Filter) v).use(getActMin(), getActMax());
			}
			bindings.put(extra.getKey(), v);
		}
	}

	public static void main(String[] args) {
		ScriptedMappingFunction m = new ScriptedMappingFunction(0, 1);
		m.fromJavaScript("return 1-Math.abs(value)");
		System.out.println(m.apply(0.2f));
	}

}
