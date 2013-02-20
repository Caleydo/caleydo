package org.caleydo.view.tourguide.v3.data;

import com.google.common.base.Function;

public interface IFloatFunction<F> extends Function<F, Float> {
	float applyPrimitive(F in);
}

