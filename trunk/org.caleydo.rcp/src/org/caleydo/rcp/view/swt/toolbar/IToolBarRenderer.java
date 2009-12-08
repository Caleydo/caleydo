package org.caleydo.rcp.view.swt.toolbar;

import java.util.List;

import org.caleydo.rcp.view.swt.toolbar.content.AToolBarContent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

public interface IToolBarRenderer {

	public abstract Runnable createRenderJob(RcpToolBarView toolBarView, List<AToolBarContent> toolBarContents);

	public abstract GridLayout createLayout();

	public abstract void addGeneralToolBarActions(Group group);

	public abstract GridData createStandardGridData();

	public abstract int calcWrapCount(int size);
}