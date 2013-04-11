/**
 *
 */
package org.caleydo.core.data.datadomain;

import java.util.Collection;

import org.caleydo.core.data.perspective.table.TablePerspective;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;



/**
 * Specifies whether {@link IDataDomain}s are
 * supported. This is required for views to determine whether they are able to
 * handle certain data or not.
 *
 * @author Christian Partl
 *
 */
public abstract class ADataSupportDefinition implements IDataSupportDefinition {

	@Override
	public Collection<TablePerspective> filter(Collection<TablePerspective> tablePerspectives) {
		return Collections2.filter(tablePerspectives, asTablePerspectivePredicate());
	}

	@Override
	public Predicate<TablePerspective> asTablePerspectivePredicate() {
		return new Predicate<TablePerspective>() {
			@Override
			public boolean apply(TablePerspective in) {
				return in != null && ADataSupportDefinition.this.apply(in.getDataDomain());
			}
		};
	}
}
