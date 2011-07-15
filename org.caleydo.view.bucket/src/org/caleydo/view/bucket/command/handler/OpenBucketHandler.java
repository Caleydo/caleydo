package org.caleydo.view.bucket.command.handler;

import org.caleydo.view.bucket.GLBucket;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class OpenBucketHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.showView(GLBucket.VIEW_TYPE);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
