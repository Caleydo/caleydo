/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.model.mapping;

import java.util.Arrays;



/**
 * @author Samuel Gratzl
 *
 */
public class JavaScriptFunctions {
	public static int compare(double a, double b) {
		return Double.compare(a, b);
	}

	public static double linear(double start, double end, double in, double startTo, double endTo) {
		if (Double.isNaN(in))
			return in;
		if (in < start)
			return Double.NaN;
		if (in > end)
			return Double.NaN;
		// linear interpolation between start and end
		double v = (in - start) / (end - start); // to ratio
		// to mapped value
		double r = startTo + v * (endTo - startTo);
		// finally clamp
		return clamp01(r);
	}


	public static double clamp01(double in) {
		if (Double.isNaN(in))
			return in;
		return (in < 0 ? 0 : (in > 1 ? 1 : in));
	}

	public static double clamp(double in, double min, double max) {
		if (Double.isNaN(in))
			return in;
		return (in < min ? min : (in > max ? max : in));
	}

	public static double normalize(double in, double min, double max) {
		if (Double.isNaN(in))
			return in;
		return clamp01((in - min) / (max - min));
	}

	public static double abs(double in) {
		return Math.abs(in);
	}

	public static double log(double in) {
		return Math.log(in);
	}

	public static double log10(double in) {
		return Math.log10(in);
	}

	public static double max(double[] in) {
		if (in.length == 0)
			return Double.NaN;
		double m = in[0];
		for (int i = 1; i < in.length; ++i)
			if (in[i] > m)
				m = in[i];
		return m;
	}

	public static double min(double[] in) {
		if (in.length == 0)
			return Double.NaN;
		double m = in[0];
		for (int i = 1; i < in.length; ++i)
			if (in[i] < m)
				m = in[i];
		return m;
	}

	public static double sum(double[] in) {
		double m = 0;
		for (int i = 0; i < in.length; ++i)
			m += in[i];
		return m;
	}

	public static double mean(double[] in) {
		if (in.length == 0)
			return 0;
		return sum(in) / in.length;
	}

	public static double geometricMean(double[] data) {
		if (data.length == 0)
			return 1;
		double c = 1;
		for (int i = 0; i < data.length; ++i)
			c *= data[i];
		return Math.pow(c, 1. / data.length);
	}

	public static double median(double[] data) {
		if (data.length == 0)
			return 0;
		data = Arrays.copyOf(data, data.length);
		Arrays.sort(data);
		int center = data.length / 2;
		if (data.length % 2 == 0)
			return 0.5f * (data[center] + data[center + 1]);
		else
			return data[center + 1];
	}
}
