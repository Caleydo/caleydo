package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.awt.Dimension;
import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventListenerManagers.QueuedEventListenerManager;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.camera.CameraProjectionMode;
import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.IGLKeyListener;
import org.caleydo.core.view.opengl.canvas.internal.IGLCanvasFactory;
import org.caleydo.core.view.opengl.canvas.internal.swt.SWTGLCanvasFactory;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.SimplePickingManager;
import org.caleydo.core.view.opengl.util.text.CompositeTextRenderer;
import org.caleydo.core.view.opengl.util.text.ITextRenderer;
import org.caleydo.core.view.opengl.util.texture.TextureManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.data.loader.ResourceLocators;
import org.caleydo.data.loader.ResourceLocators.IResourceLocator;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * acts as a sandbox for elements, just use {@link GLSandBox#main(String[], GLElement)} and provide a element, and run the
 * application to open a window with the element shown, without the need of the whole caleydo / eclipse overhead
 *
 * perfect for prototyping
 *
 * supports picking, textures, ...
 *
 * @author Samuel Gratzl
 *
 */
public class GLSandBox implements GLEventListener, IGLElementParent, IGLElementContext {
	private final FPSAnimator animator;
	private TextureManager textures;
	private ITextRenderer text;
	private final WindowGLElement root;
	private boolean dirty = true;

	protected boolean tracingGL = false;
	private final ViewFrustum viewFrustum = new ViewFrustum(CameraProjectionMode.ORTHOGRAPHIC, 0, 100, 100, 0, -20, 20);

	private final DisplayListPool pool = new DisplayListPool();

	private final SimplePickingManager pickingManager = new SimplePickingManager();

	protected final IGLCanvas canvas;
	private final IResourceLocator loader;
	protected boolean renderPick;

	private GLPadding padding = GLPadding.ZERO;

	protected final QueuedEventListenerManager eventListeners = EventListenerManagers.createQueued();
	protected final Display display;
	protected final Shell shell;

	/**
	 * @param canvas
	 */
	public GLSandBox(String title, GLElement root, GLPadding padding, Dimension dim) {
		IResourceLocator loader = ResourceLocators.chain(
				ResourceLocators.classLoader(root.getClass().getClassLoader()), ResourceLocators.FILE);
		this.display = new Display();
		this.shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setText(title);
		IGLCanvasFactory canvasFactory = new SWTGLCanvasFactory();
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		this.canvas = canvasFactory.create(caps, shell);
		// canvas.asComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL));
		canvas.asComposite().setSize(dim.width, dim.height);
		this.padding = padding;
		canvas.addGLEventListener(this);
		shell.setSize(dim.width, dim.height);

		this.animator = new FPSAnimator(canvas.asGLAutoDrawAble(), 30);
		this.loader = loader;
		this.canvas.addMouseListener(pickingManager.getListener());
		this.canvas.addKeyListener(new IGLKeyListener() {
			@Override
			public void keyReleased(IKeyEvent e) {
				if (e.isKey('p')) {
					renderPick = !renderPick;
				}
			}

			@Override
			public void keyPressed(IKeyEvent e) {

			}
		});
		this.root = new WindowGLElement(root);
	}

	/**
	 * @return the root, see {@link #root}
	 */
	public GLElement getRoot() {
		return this.root.getRoot();
	}

	@Override
	public int registerPickingListener(IPickingListener l) {
		return registerPickingListener(l, 0);
	}

	@Override
	public int registerPickingListener(IPickingListener l, int objectId) {
		return pickingManager.register(l, objectId);
	}

	@Override
	public void unregisterPickingListener(int pickingID) {
		pickingManager.unregister(pickingID);
	}

	@Override
	public IPickingListener createTooltip(ILabelProvider label) {
		return canvas.createTooltip(label);
	}

	@Override
	public void showContextMenu(Iterable<? extends AContextMenuItem> items) {
		new SWTGLCanvasFactory().showPopupMenu(canvas, items);
	}

	@Override
	public void repaintPick() {

	}


	@Override
	public Vec2f getAbsoluteLocation() {
		return new Vec2f(padding.left, padding.top);
	}

	@Override
	public TextureManager getTextureManager() {
		return textures;
	}

	@Override
	public void init(GLElement element) {
		eventListeners.register(element, null, AGLElementView.isNotBaseClass);
	}

	@Override
	public void takeDown(GLElement element) {
		eventListeners.unregister(element);
	}

	@Override
	public IMouseLayer getMouseLayer() {
		return root.getMouseLayer();
	}

	@Override
	public IPopupLayer getPopupLayer() {
		return root.getPopupLayer();
	}

	@Override
	public boolean moved(GLElement child) {
		return true;
	}



	@Override
	public IGLElementParent getParent() {
		return null;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		text = new CompositeTextRenderer(8, 16, 24, 40);
		textures = new TextureManager(new ResourceLoader(loader));

		AGLView.initGLContext(gl);

		gl.glLoadIdentity();
		this.root.setParent(this);
		this.root.init(this);
	}

	@Override
	public DisplayListPool getDisplayListPool() {
		return pool;
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		root.takeDown();
		GL2 gl = drawable.getGL().getGL2();
		pool.deleteAll(gl);
	}

	private float getWidth() {
		return viewFrustum.getRight();
	}

	private float getHeight() {
		return viewFrustum.getBottom();
	}


	@Override
	public void display(GLAutoDrawable drawable) {
		eventListeners.processEvents();

		GL2 gl = drawable.getGL().getGL2();
		// clear screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.375f, 0.375f, 0);

		final GLGraphics g = tracingGL ? new GLGraphicsTracing(gl, text, textures, loader, true) : new GLGraphics(gl,
				text, textures, loader, true);

		float paddedWidth = getWidth() - padding.left - padding.right;
		float paddedHeight = getHeight() - padding.top - padding.bottom;
		g.move(padding.left, padding.right);

		if (dirty) {
			root.setBounds(0, 0, paddedWidth, paddedHeight);
			root.layout();
			dirty = false;
		}

		Runnable toRender = new Runnable() {
			@Override
			public void run() {
				root.renderPick(g);
			}
		};

		Point mousePos = pickingManager.getCurrentMousePos();
		if (mousePos != null) {
			root.getMouseLayer().setBounds(mousePos.x - padding.left, mousePos.y - padding.top,
					getWidth() - mousePos.x, getHeight() - mousePos.y);
			root.getMouseLayer().relayout();
		}
		pickingManager.doPicking(g.gl, toRender);

		if (renderPick)
			root.renderPick(g);
		else
			root.render(g);

		g.move(-padding.left, -padding.right);
		g.destroy();

		drawable.swapBuffers();
	}

	@Override
	public void relayout() {
		dirty = true;
	}

	@Override
	public void repaint() {

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		viewFrustum.setRight(width);
		viewFrustum.setBottom(height);

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		viewFrustum.setProjectionMatrix(gl);

		relayout();

	}

	@Override
	public void setCursor(final int swtCursorConst) {
		final Composite c = canvas.asComposite();
		final Display d = c.getDisplay();
		d.asyncExec(new Runnable() {
			@Override
			public void run() {
				c.setCursor(swtCursorConst < 0 ? null : d.getSystemCursor(swtCursorConst));
			}
		});
	}

	public void run() {
		try {
			shell.open();
			animator.start();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			animator.stop();
			display.dispose();
		} catch (IllegalArgumentException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (java.lang.NoClassDefFoundError e) {
			// expected error as we aren't part of eclipse
			System.exit(0);
		} finally {
			System.err.flush();
			System.out.flush();
			// System.exit(0);
		}
	}

	public static void main(String[] args, IGLRenderer renderer) {
		main(args, new GLElement(renderer));
	}

	public static void main(String[] args, GLElement root) {
		main(args, root, GLPadding.ZERO);
	}

	public static void main(String[] args, GLElement root, GLPadding padding) {
		main(args, root, padding, new Dimension(800, 600));
	}

	public static void main(String[] args, GLElement root, GLPadding padding, Dimension dim) {
		new GLSandBox(root.getClass().getSimpleName(), root, padding, dim).run();
	}

}
