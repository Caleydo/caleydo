package org.caleydo.vis.rank.data;

public abstract class AFloatFunction<F> implements IFloatFunction<F> {
	@Override
	public final Float apply(F in) {
		return applyPrimitive(in);
	}
}