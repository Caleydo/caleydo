/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.rank.model.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author Samuel Gratzl
 *
 */
public class ScriptedMappingFunction extends AMappingFunction {
	/**
	 *
	 */
	private static final String DEFAULT_CODE = "return linear(value_min, value_max, value, 0, 1)";
	private final static String prefix;
	private final static String postfix;

	private String code = "";
	private final ScriptEngine engine;
	private CompiledScript script;

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

	public ScriptedMappingFunction(float fromMin, float fromMax) {
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
			e.printStackTrace();
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
		this.script = null;
	}

	@Override
	public String toJavaScript() {
		return code;
	}

	@Override
	public float[] getMappedMin() {
		// TODO no idea how to compute that out of a script
		return new float[] { getActMin(), 0 };
	}

	@Override
	public float[] getMappedMax() {
		return new float[] { getActMax(), 1 };
	}

	@Override
	public float getMaxTo() {
		return 1;
	}

	@Override
	public float getMinTo() {
		return 0;
	}

	@Override
	public boolean isMappingDefault() {
		return true;
	}

	@Override
	public float apply(float in) {
		try {
			Bindings bindings = engine.createBindings();
			bindings.put("v", in);
			addBindings(bindings);
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

	public static void main(String[] args) {
		ScriptedMappingFunction m = new ScriptedMappingFunction(0, 1);
		m.fromJavaScript("return 1-Math.abs(value)");
		System.out.println(m.apply(0.2f));
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
	}

}
