package org.caleydo.core.view.opengl.layout2;

import gleem.linalg.Vec2f;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.canvas.internal.IGLCanvasFactory;
import org.caleydo.core.view.opengl.canvas.internal.swt.SWTGLCanvasFactory;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.PickingMouseListener;
import org.caleydo.core.view.opengl.picking.SimplePickingManager;
import org.caleydo.core.view.opengl.util.text.CompositeTextRenderer;
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
	private CompositeTextRenderer text;
	private final WindowGLElement root;
	private boolean dirty = true;
	private final PickingMouseListener mouseListener;

	protected boolean tracingGL = false;
	private int width;
	private int height;

	private final DisplayListPool pool = new DisplayListPool();

	private final SimplePickingManager pickingManager = new SimplePickingManager();

	private final IGLCanvas canvas;
	private final IResourceLocator loader;

	/**
	 * @param canvas
	 */
	public GLSandBox(IGLCanvas canvas, GLElement root, IResourceLocator loader) {
		this.canvas = canvas;
		this.animator = new FPSAnimator(canvas.asGLAutoDrawAble(), 30);
		this.mouseListener = new PickingMouseListener();
		this.loader = loader;
		this.canvas.addMouseListener(mouseListener);
		this.root = new WindowGLElement(root);
		this.root.init(this);
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
	public void unregisterPickingListener(IPickingListener l) {
		pickingManager.unregister(l);
	}

	@Override
	public void unregisterPickingListener(int pickingID) {
		pickingManager.unregister(pickingID);
	}

	@Override
	public void repaintPick() {

	}


	@Override
	public Vec2f getAbsoluteLocation() {
		return new Vec2f(0, 0);
	}

	@Override
	public TextureManager getTextureManager() {
		return textures;
	}

	@Override
	public void init(GLElement element) {

	}

	@Override
	public GLElementContainer getMouseLayer() {
		return root.getMouseLayer();
	}

	@Override
	public void moved(GLElement child) {

	}

	@Override
	public void takeDown(GLElement element) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		text = new CompositeTextRenderer(8, 16, 24, 40);
		textures = new TextureManager(new ResourceLoader(loader));

		gl.glShadeModel(GLLightingFunc.GL_SMOOTH); // Enables Smooth Shading
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // white Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glEnable(GL2.GL_POINT_SMOOTH);
		// gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		// gl.glEnable(GL2.GL_POLYGON_SMOOTH);
		// gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);

		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT, GLLightingFunc.GL_DIFFUSE);

		gl.glLoadIdentity();
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

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		// clear screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.375f, 0.375f, 0);

		final GLGraphics g = tracingGL ? new GLGraphicsTracing(gl, text, textures, loader, true) : new GLGraphics(gl,
				text, textures, loader, true);

		if (dirty) {
			root.setBounds(0, 0, width, height);
			root.layout();
			dirty = false;
		}

		Runnable toRender = new Runnable() {
			@Override
			public void run() {
				root.renderPick(g);
			}
		};

		Point mousPos = mouseListener.getCurrentMousePos();
		if (mousPos != null) {
			root.getMouseLayer().setLocation(mousPos.x, mousPos.y);
		}
		pickingManager.doPicking(mouseListener, g.gl, toRender);
		root.render(g);

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

		this.width = width;
		this.height = height;

		gl.glViewport(x, y, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, this.width, this.height, 0, -20, 20);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);

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

	public static void main(String[] args, IGLRenderer renderer) {
		main(args, new GLElement(renderer));
	}

	public static void main(String[] args, GLElement root) {
		IResourceLocator l = ResourceLocators.chain(ResourceLocators.classLoader(root.getClass().getClassLoader()),
				ResourceLocators.FILE);
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		shell.setSize(800, 600);
		IGLCanvasFactory canvasFactory = new SWTGLCanvasFactory();
		GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
		IGLCanvas canvas = canvasFactory.create(caps, shell);
		// canvas.asComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL));
		canvas.asComposite().setSize(800, 600);

		try {
			GLSandBox sandbox = new GLSandBox(canvas, root, l);
			canvas.addGLEventListener(sandbox);

			shell.open();
			sandbox.animator.start();

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			sandbox.animator.stop();
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
}
