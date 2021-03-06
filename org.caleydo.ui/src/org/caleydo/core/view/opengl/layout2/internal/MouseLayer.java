/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout2.internal;

import static org.caleydo.core.view.opengl.layout2.layout.GLLayouts.defaultValue;
import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.internal.CaleydoTransfer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IMouseLayer;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.FileDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource.IDragEvent;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.dnd.IRemoteDragInfoUICreator;
import org.caleydo.core.view.opengl.layout2.dnd.IUIDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.TextDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.URLDragInfo;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.dnd.URLTransfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * implementation of {@link IMouseLayer} using a {@link GLElementContainer} by using the layout data for meta data about
 * elements
 *
 * @author Samuel Gratzl
 *
 */
public final class MouseLayer extends GLElementContainer implements IMouseLayer, IGLLayout2 {
	private final IGLCanvas canvas;

	private boolean registered = false;
	/**
	 * current possible drop targets
	 */
	private final Deque<IDropGLTarget> dropTargets = new ConcurrentLinkedDeque<>();
	/**
	 * current possible drag sources
	 */
	private final Deque<IDragGLSource> dragSources = new ConcurrentLinkedDeque<>();

	/**
	 * registered remote creator factories, can't use extension point since it is instance specific
	 */
	private final Collection<IRemoteDragInfoUICreator> creators = new ArrayList<>(1);

	/**
	 * active drop target
	 */
	volatile IDropGLTarget activeDropTarget;
	/**
	 * currently dragged element
	 */
	volatile DnDItem active;

	/**
	 * a shared version of the current item, shared within the same VM, avoids encryption and decryption, as well as in
	 * some os (I'm talking about Mac OS), you can#t access intermediate data
	 */
	volatile static DnDItem activeShared;

	public MouseLayer(IGLCanvas canvas) {
		super();
		this.canvas = canvas;
		setLayout(this);
	}

	private void ensure() {
		if (registered)
			return;
		canvas.addDragListener(drag);
		canvas.addDropListener(drop);
		registered = true;
	}

	@Override
	protected void takeDown() {
		canvas.removeDragListener(drag);
		canvas.removeDropListener(drop);
		super.takeDown();
	}

	private final DragSourceListener drag = new DragSourceListener() {
		@Override
		public void dragStart(DragSourceEvent event) {
			// convert to event
			DragEvent e = new DragEvent(toPoint(event.x, event.y, false));

			for(IDragGLSource source : dragSources) {
				// check if possible
				IDragInfo info = source.startSWTDrag(e);
				if (info != null) { // if so
					active = new DnDItem(info, source, true);
					activeShared = new DnDItem(info, null, false);
					active.setMousePos(e.getMousePos());
					EventPublisher.trigger(new DragItemEvent(readOnly(active), active.source, false)
							.to(MouseLayer.this));
					return;
				}
			}
			event.doit = false; // abort dragging no drag source there
		}

		@Override
		public void dragSetData(DragSourceEvent event) {
			if (active == null) { // nothing to transfer
				event.doit = false;
				return;
			}
			// set data according to requested type
			IDragInfo info = active.getInfo();
			if (CaleydoTransfer.getInstance().isSupportedType(event.dataType) && CaleydoTransfer.isValid(info)) {
				event.data = info;
			} else if (FileTransfer.getInstance().isSupportedType(event.dataType) && info instanceof FileDragInfo) {
				event.data = ((FileDragInfo) info).getFileNames();
			} else if (URLTransfer.getInstance().isSupportedType(event.dataType) && info instanceof URLDragInfo) {
				event.data = ((URLDragInfo) info).getUrl();
			} else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
				event.data = info instanceof TextDragInfo ? ((TextDragInfo) info).getText() : info.getLabel();
			} else {
				event.doit = false; // invalid type
				return;
			}
		}

		@Override
		public void dragFinished(DragSourceEvent event) {
			if (active == null)
				return; // strange shouldn't happen
			active.setType(event.doit ? event.detail : DND.DROP_NONE);
			// thread switch
			EventPublisher.trigger(new DragItemEvent(readOnly(active), active.source, true).to(MouseLayer.this));
			active = null;
			activeShared = null;
		}
	};
	private final DropTargetListener drop = new DropTargetListener() {
		private int acceptDetail;
		private Vec2f acceptPoint;
		@Override
		public void dragEnter(DropTargetEvent event) {
			// find dragged item
			IDnDItem item = toItem(event);
			// have valid drop target
			if (validateDropTarget(event, item) && event.detail == DND.DROP_DEFAULT)
				// determine default type
				event.detail = fromType(activeDropTarget.defaultSWTDnDType(item));

			if (item != null)
				EventPublisher.trigger(new DropEnterLeaveItemEvent(readOnly(item), activeDropTarget, true)
					.to(MouseLayer.this));
		}

		@Override
		public void dropAccept(DropTargetEvent event) {
			// I don't know why but I need to transfer the method from the accept to the drop
			acceptDetail = event.detail;
			acceptPoint = toPoint(event.x, event.y, true);
		}

		@Override
		public void drop(DropTargetEvent event) {
			event.detail = acceptDetail;
			DnDItem item = toItem(event);
			if (validateDropTarget(event, item)) {
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = fromType(activeDropTarget.defaultSWTDnDType(item));
				// thread switch
				item.setMousePos(acceptPoint);
				EventPublisher.trigger(new DropItemEvent(readOnly(item), activeDropTarget, true)
						.to(MouseLayer.this));
				activeDropTarget = null;
			}
			if (active != null && active.source == null) {// cleanup remote
				active = null;
				activeShared = null;
			}
		}

		private int fromType(EDnDType type) {
			if (type == null)
				return DND.DROP_MOVE;
			switch (type) {
			case COPY:
				return DND.DROP_COPY;
			case LINK:
				return DND.DROP_LINK;
			case MOVE:
				return DND.DROP_MOVE;
			case NONE:
				return DND.DROP_NONE;
			}
			return 0;
		}

		@Override
		public void dragOver(DropTargetEvent event) {
			itemChanged(event);

		}

		private void itemChanged(DropTargetEvent event) {
			DnDItem item = toItem(event);
			if (validateDropTarget(event, item)) {
				if (event.detail == DND.DROP_DEFAULT)
					event.detail = fromType(activeDropTarget.defaultSWTDnDType(item));
				item.setMousePos(toPoint(event.x, event.y, true));
				EventPublisher.trigger(new DropItemEvent(readOnly(item), activeDropTarget, false).to(MouseLayer.this));
			}
		}

		@Override
		public void dragOperationChanged(DropTargetEvent event) {
			itemChanged(event);
		}

		private boolean validateDropTarget(DropTargetEvent event, IDnDItem item) {
			if (item == null) {
				event.detail = DND.DROP_NONE;
				return false;
			}
			if (activeDropTarget == null) {
				activeDropTarget = findDropTarget(item);
				if (activeDropTarget != null) {
					event.detail = DND.DROP_DEFAULT;
				}
			}
			if (activeDropTarget == null) {
				event.detail = DND.DROP_NONE;
				return false;
			}
			return true;
		}

		@Override
		public void dragLeave(final DropTargetEvent event) {
			// later to be after the optional drop event
			Display.getCurrent().asyncExec(new Runnable() {
				@Override
				public void run() {
					IDnDItem item = toItem(event);
					if (item != null)
						EventPublisher.trigger(new DropEnterLeaveItemEvent(readOnly(item), activeDropTarget, false)
								.to(MouseLayer.this));
					activeDropTarget = null;
					if (active != null && active.source == null) // clean up remote cached active
						active = null;
				}
			});
		}
	};


	static IDnDItem readOnly(IDnDItem item) {
		return new DNDReadOnlyItem(item);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	protected Vec2f toPoint(int x, int y, boolean absolute) {
		if (absolute) {
			Point p = canvas.asComposite().toControl(x, y);
			x = p.x;
			y = p.y;
		}
		return canvas.toDIP(new java.awt.Point(x, y));
	}

	@ListenTo(sendToMe = true)
	private void onDropEnterLeaveItemEvent(DropEnterLeaveItemEvent event) {
		IDnDItem item = event.getItem();
		if (event.isEntering()) {
			if (!isAlreadyThere(item.getInfo())) {
				addInfo(item instanceof DnDItem ? ((DnDItem) item).source : null, item);
			}
		} else {
			removeInfo(item.getInfo());
		}

	}

	@ListenTo(sendToMe = true)
	private void onDragItemEvent(DragItemEvent event) {
		final IDragGLSource s = event.getSource();
		final IDnDItem item = event.getItem();

		if (event.isFinished()) {
			s.onDropped(item);
			removeInfo(item.getInfo());
		} else {
			addInfo(s, item);
		}
	}

	/**
	 * add a visual representation of the given item using various creator methods
	 *
	 * @param s
	 * @param item
	 */
	private void addInfo(final IDragGLSource s, final IDnDItem item) {
		final IDragInfo info = item.getInfo();
		if (isAlreadyThere(info))
			return;

		GLElement ui = null;
		for (IRemoteDragInfoUICreator creator : creators)
			// via creator
			if ((ui = creator.createUI(info)) != null)
				break;
		if (ui == null && s != null) // via source
			ui = s.createUI(info);
		if (ui == null && info instanceof IUIDragInfo) // via itself
			ui = ((IUIDragInfo) info).createUI();
		if (ui == null)
			return;
		ui.setLayoutData(info);
		this.add(ui);
	}

	private boolean isAlreadyThere(final IDragInfo info) {
		for (GLElement elem : this)
			if (elem.getLayoutDataAs(IDragInfo.class, null) == info)
				return true; // already there
		return false;
	}

	private void removeInfo(final IDragInfo info) {
		for (GLElement elem : this) {
			IDragInfo d = elem.getLayoutDataAs(IDragInfo.class, null);
			if (d == info) {
				remove(elem);
				break;
			}
		}
	}

	@ListenTo(sendToMe = true)
	private void onDropItemEvent(DropItemEvent event) {
		IDropGLTarget t = event.getTarget();
		if (event.isDropping()) {
			t.onDrop(event.getItem());
			removeInfo(event.getItem().getInfo());
		} else
			t.onItemChanged(event.getItem());
	}

	/**
	 * tries to find a valid drop target, which accepts the given item
	 *
	 * @return
	 */
	protected IDropGLTarget findDropTarget(IDnDItem item) {
		for (IDropGLTarget target : dropTargets) {
			if (target.canSWTDrop(item)) {
				return target;
			}
		}
		return null;
	}

	/**
	 * extract from the given event the {@link DnDItem}
	 *
	 * @param event
	 * @return
	 */
	protected DnDItem toItem(DropTargetEvent event) {
		DnDItem item = null;
		if (active != null) {// cached one e.g local drag
			item = active;
		}
		else if (activeShared != null) { // same vm
			item = active = activeShared;
		} else {
			// really extract the data
			IDragInfo info = extract(event);
			if (info != null)
				item = active = new DnDItem(info, null, false);
		}
		if (item != null) // update type
			item.setType(event.detail);
		return item;
	}

	/**
	 * extract really from the event data a {@link IDragInfo}
	 *
	 * @param event
	 * @return
	 */
	private static IDragInfo extract(DropTargetEvent event) {
		final TransferData d = event.currentDataType;
		Object obj;
		if (CaleydoTransfer.getInstance().isSupportedType(d) && (obj = CaleydoTransfer.getInstance().nativeToJava(d)) instanceof IDragInfo) {
			return (IDragInfo) obj;
		} else if (FileTransfer.getInstance().isSupportedType(d)
				&& (obj = FileTransfer.getInstance().nativeToJava(d)) instanceof String[]) {
			return new FileDragInfo((String[])obj);
		} else if (URLTransfer.getInstance().isSupportedType(d)
				&& (obj = URLTransfer.getInstance().nativeToJava(d)) instanceof String) {
			return new URLDragInfo((String)obj);
		} else if (TextTransfer.getInstance().isSupportedType(d)
				&& (obj = TextTransfer.getInstance().nativeToJava(d)) instanceof String) {
			return new TextDragInfo((String)obj);
		}
		return null;
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {
		for (IGLLayoutElement child : children) {
			child.setBounds(defaultValue2(child.getSetX(), 0), defaultValue2(child.getSetY(), 0),
					defaultValue(child.getSetWidth(), w), defaultValue(child.getSetHeight(), h));
		}
		return false;
	}

	/**
	 * @param setY
	 * @param i
	 * @return
	 */
	private static float defaultValue2(float v, float default_) {
		return Float.isNaN(v) ? default_ : v;
	}

	@Override
	public boolean isDragging(Class<? extends IDragInfo> type) {
		return active != null && type.isInstance(active.info);
	}

	@Override
	public void addDragSource(IDragGLSource dragSource) {
		this.dragSources.add(dragSource);
		ensure();
	}

	@Override
	public void removeDragSource(IDragGLSource dragSource) {
		this.dragSources.remove(dragSource);
	}

	@Override
	public void addDropTarget(IDropGLTarget dropTarget) {
		this.dropTargets.add(dropTarget);
		ensure();
	}

	@Override
	public void removeDropTarget(IDropGLTarget dropTarget) {
		this.dropTargets.remove(dropTarget);
		if (activeDropTarget == dropTarget) {
			// was the active one
			dropTarget.onDropLeave();
			activeDropTarget = null;
		}
	}

	@Override
	public void addRemoteDragInfoUICreator(IRemoteDragInfoUICreator creator) {
		this.creators.add(creator);
	}

	/**
	 * @param detail
	 * @return
	 */
	static EDnDType toType(int detail) {
		switch (detail) {
		case DND.DROP_MOVE:
		case DND.DROP_TARGET_MOVE:
			return EDnDType.MOVE;
		case DND.DROP_COPY:
			return EDnDType.COPY;
		case DND.DROP_LINK:
			return EDnDType.LINK;
		case DND.DROP_DEFAULT:
			return EDnDType.NONE;
		default:
			return EDnDType.NONE;
		}
	}

	private static class DnDItem implements IDnDItem {
		private final IDragGLSource source;
		private final IDragInfo info;
		private final boolean isInternal;
		private EDnDType type = EDnDType.MOVE;
		private Vec2f mousePos;

		public DnDItem(IDragInfo info, IDragGLSource source, boolean isInternal) {
			this.info = info;
			this.source = source;
			this.isInternal = isInternal;
		}

		/**
		 * @return the isInternal, see {@link #isInternal}
		 */
		@Override
		public boolean isInternal() {
			return isInternal;
		}

		/**
		 * @param mousePos
		 *            setter, see {@link mousePos}
		 */
		public void setMousePos(Vec2f mousePos) {
			this.mousePos = mousePos;
		}

		/**
		 * @return the mousePos, see {@link #mousePos}
		 */
		@Override
		public Vec2f getMousePos() {
			return mousePos;
		}

		/**
		 * @param detail
		 */
		public void setType(int detail) {
			this.type = toType(detail);
		}

		@Override
		public IDragInfo getInfo() {
			return info;
		}

		@Override
		public EDnDType getType() {
			return type;
		}
	}

	private static class DNDReadOnlyItem implements IDnDItem {
		private final IDragInfo info;
		private final EDnDType type;
		private final Vec2f mousePos;
		private final boolean isInternal;

		public DNDReadOnlyItem(IDnDItem clone) {
			this.info = clone.getInfo();
			this.type = clone.getType();
			this.mousePos = clone.getMousePos();
			this.isInternal = clone.isInternal();
		}

		/**
		 * @return the isInternal, see {@link #isInternal}
		 */
		@Override
		public boolean isInternal() {
			return isInternal;
		}

		/**
		 * @return the mousePos, see {@link #mousePos}
		 */
		@Override
		public Vec2f getMousePos() {
			return mousePos;
		}

		@Override
		public IDragInfo getInfo() {
			return info;
		}

		@Override
		public EDnDType getType() {
			return type;
		}
	}

	private static class DragEvent implements IDragEvent {
		private final Vec2f offset;

		public DragEvent(Vec2f offset) {
			this.offset = offset;
		}

		/**
		 * @return the offset, see {@link #offset}
		 */
		@Override
		public Vec2f getMousePos() {
			return offset;
		}
	}
}
