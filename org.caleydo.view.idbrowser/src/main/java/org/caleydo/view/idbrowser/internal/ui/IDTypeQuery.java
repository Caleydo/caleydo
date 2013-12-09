/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.idbrowser.internal.ui;

import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.view.idbrowser.internal.model.IDRow;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * @author Samuel Gratzl
 *
 */
public class IDTypeQuery extends GLButton implements Comparable<IDTypeQuery> {

	private BitSet mask;

	private final IDType idType;

	public IDTypeQuery(IDType idType) {
		super(EButtonMode.CHECKBOX);
		setRenderer(createCheckRenderer(idType.getTypeName()));
		this.idType = idType;
		setLayoutData(idType);
	}

	/**
	 * @return the mask, see {@link #mask}
	 */
	public BitSet getMask() {
		return mask;
	}

	public void init(int from, int to) {
		this.mask = new BitSet(to);
		this.mask.set(from, to);
	}

	public boolean inited() {
		return mask != null;
	}

	public Collection<IDRow> create() {
		// find all ids and check the predicate
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());

		Set<?> ids = mappingManager.getAllMappedIDs(idType);
		return Collections2.transform(ids, new Function<Object,IDRow>() {
			@Override
			public IDRow apply(Object input) {
				assert input != null;
				return new IDRow(idType, input);
			}
		});
	}

	@Override
	public int compareTo(IDTypeQuery o) {
		int r = String.CASE_INSENSITIVE_ORDER.compare(idType.getIDCategory().getCategoryName(), o.idType
				.getIDCategory().getCategoryName());
		if (r != 0)
			return r;
		return String.CASE_INSENSITIVE_ORDER.compare(idType.getTypeName(), o.idType.getTypeName());
	}
}
