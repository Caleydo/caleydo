/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jmex.swt.input;

import java.util.BitSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;

/**
 * Note: portions originally from the jme-swt source.
 */
public class SWTKeyInput extends KeyInput implements KeyListener {
	private static final Logger logger = Logger.getLogger(SWTKeyInput.class
			.getName());

	LinkedList<KeyEvent> events = new LinkedList<KeyEvent>();
	BitSet keyDown = new BitSet(256);
	private boolean enabled = true;

	protected SWTKeyInput() {
	}

	@Override
	public boolean isKeyDown(int key) {
		return keyDown.get(key);
	}

	@Override
	public String getKeyName(int key) {
		return getKeyText(toSWTCode(key));
	}

	@Override
	public int getKeyIndex(String name) {
		throw new UnsupportedOperationException(
				"getKeyIndex is not supported by SWTKeyInput.");
	}

	@Override
	public void update() {
		if (listeners != null && listeners.size() > 0) {
			while (!events.isEmpty()) {
				KeyEvent e = events.poll();
				char c = e.character;
				int keyCode = toInputCode(e.keyCode);
				boolean pressed = (e.stateMask | SWT.KeyDown) == 1;

				for (int i = 0; i < listeners.size(); i++) {
					KeyInputListener listener = listeners.get(i);
					listener.onKey(c, keyCode, pressed);
				}
			}
		}
	}

	@Override
	protected void destroy() {
		; // nothing to do
	}

	/**
	 * @return Returns the enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            The enabled to set.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	// from SWT:
	public void keyPressed(KeyEvent arg0) {
		if (!enabled) {
			return;
		}

		events.add(arg0);
		keyDown.set(toInputCode(arg0.keyCode), true);
	}

	public void keyReleased(KeyEvent arg0) {
		if (!enabled) {
			return;
		}

		events.add(arg0);
		keyDown.set(toInputCode(arg0.keyCode), false);

		// Workaround for bug in SWT: When more than one key is pressed, only
		// the first release
		// fires an event!

		for (int x = 0; x < 256; x++) {
			if (keyDown.get(x) == true) {
				keyDown.set(x, false);
				Event ev = new Event();

				// use shell as dummy for creating event
				ev.widget = Display.getCurrent().getShells()[0];
				ev.character = (char) x;
				ev.keyCode = toSWTCode(x);
				events.add(new KeyEvent(ev));
			}
		}
	}

	/**
	 * <code>toSWTCode</code> converts KeyInput key codes to SWT key codes.
	 * 
	 * @param key
	 *            jme KeyInput key code
	 * @return swt KeyEvent key code
	 */
	public static int toSWTCode(int key) {
		switch (key) {
		case KEY_ESCAPE:
			return SWT.ESC;
		case KEY_1:
			return '1';
		case KEY_2:
			return '2';
		case KEY_3:
			return '3';
		case KEY_4:
			return '4';
		case KEY_5:
			return '5';
		case KEY_6:
			return '6';
		case KEY_7:
			return '7';
		case KEY_8:
			return '8';
		case KEY_9:
			return '9';
		case KEY_0:
			return '0';
		case KEY_MINUS:
			return '-';
		case KEY_EQUALS:
			return '=';
		case KEY_BACK:
			return SWT.BS;
		case KEY_TAB:
			return SWT.TAB;
		case KEY_Q:
			return 'q';
		case KEY_W:
			return 'w';
		case KEY_E:
			return 'e';
		case KEY_R:
			return 'r';
		case KEY_T:
			return 't';
		case KEY_Y:
			return 'y';
		case KEY_U:
			return 'u';
		case KEY_I:
			return 'i';
		case KEY_O:
			return 'o';
		case KEY_P:
			return 'p';
		case KEY_LBRACKET:
			return '(';
		case KEY_RBRACKET:
			return ')';
		case KEY_RETURN:
			return '\n';
		case KEY_LCONTROL:
			return SWT.CTRL;
		case KEY_A:
			return 'a';
		case KEY_S:
			return 's';
		case KEY_D:
			return 'd';
		case KEY_F:
			return 'f';
		case KEY_G:
			return 'g';
		case KEY_H:
			return 'h';
		case KEY_J:
			return 'j';
		case KEY_K:
			return 'k';
		case KEY_L:
			return 'l';
		case KEY_SEMICOLON:
			return ';';
		case KEY_APOSTROPHE:
			return '\'';
		case KEY_GRAVE:
			return '`';
		case KEY_LSHIFT:
			return SWT.SHIFT;
		case KEY_BACKSLASH:
			return '\\';
		case KEY_Z:
			return 'z';
		case KEY_X:
			return 'x';
		case KEY_C:
			return 'c';
		case KEY_V:
			return 'v';
		case KEY_B:
			return 'b';
		case KEY_N:
			return 'n';
		case KEY_M:
			return 'm';
		case KEY_COMMA:
			return ',';
		case KEY_PERIOD:
			return '.';
		case KEY_SLASH:
			return '/';
		case KEY_RSHIFT:
			return ' ';
		case KEY_MULTIPLY:
			return '*';
		case KEY_SPACE:
			return ' ';
		case KEY_CAPITAL:
			return SWT.CAPS_LOCK;
		case KEY_F1:
			return SWT.F1;
		case KEY_F2:
			return SWT.F2;
		case KEY_F3:
			return SWT.F3;
		case KEY_F4:
			return SWT.F4;
		case KEY_F5:
			return SWT.F5;
		case KEY_F6:
			return SWT.F6;
		case KEY_F7:
			return SWT.F7;
		case KEY_F8:
			return SWT.F8;
		case KEY_F9:
			return SWT.F9;
		case KEY_F10:
			return SWT.F10;
		case KEY_NUMLOCK:
			return SWT.NUM_LOCK;
		case KEY_SCROLL:
			return SWT.SCROLL_LOCK;
		case KEY_NUMPAD7:
			return SWT.KEYPAD_7;
		case KEY_NUMPAD8:
			return SWT.KEYPAD_8;
		case KEY_NUMPAD9:
			return SWT.KEYPAD_9;
		case KEY_SUBTRACT:
			return SWT.KEYPAD_SUBTRACT;
		case KEY_NUMPAD4:
			return SWT.KEYPAD_4;
		case KEY_NUMPAD5:
			return SWT.KEYPAD_5;
		case KEY_NUMPAD6:
			return SWT.KEYPAD_6;
		case KEY_ADD:
			return SWT.KEYPAD_ADD;
		case KEY_NUMPAD1:
			return SWT.KEYPAD_1;
		case KEY_NUMPAD2:
			return SWT.KEYPAD_2;
		case KEY_NUMPAD3:
			return SWT.KEYPAD_3;
		case KEY_NUMPAD0:
			return SWT.KEYPAD_0;
		case KEY_DECIMAL:
			return SWT.KEYPAD_DECIMAL;
		case KEY_F11:
			return SWT.F11;
		case KEY_F12:
			return SWT.F12;
		case KEY_F13:
			return SWT.F13;
		case KEY_F14:
			return SWT.F14;
		case KEY_F15:
			return SWT.F15;
		case KEY_NUMPADEQUALS:
			return SWT.KEYPAD_EQUAL;
		case KEY_AT:
			return '@';
		case KEY_COLON:
			return '|';
		case KEY_UNDERLINE:
			return '_';
			// case KEY_STOP:
			// return KeyEvent.VK_STOP;
			// case KEY_NUMPADENTER:
			// return SWT.KEYPA;
		case KEY_RCONTROL:
			return SWT.CTRL;
		case KEY_NUMPADCOMMA:
			return ',';
			// case KEY_DIVIDE:
			// return '/';
		case KEY_PAUSE:
			return SWT.PAUSE;
		case KEY_HOME:
			return SWT.HOME;
		case KEY_UP:
			return SWT.ARROW_UP;
		case KEY_PRIOR:
			return SWT.PAGE_UP;
		case KEY_LEFT:
			return SWT.ARROW_LEFT;
		case KEY_RIGHT:
			return SWT.ARROW_RIGHT;
		case KEY_END:
			return SWT.END;
		case KEY_DOWN:
			return SWT.ARROW_DOWN;
		case KEY_NEXT:
			return SWT.PAGE_DOWN;
		case KEY_INSERT:
			return SWT.INSERT;
		case KEY_DELETE:
			return SWT.DEL;
		case KEY_LMENU:
			return SWT.ALT; // todo: location left
			// case KEY_RMENU:
			// return KeyEvent.VK_ALT; //todo: location right

		}
		logger.warning("unsupported key:" + key);
		return ' ';
	}

	/**
	 * <code>toInputCode</code> converts SWT key codes to KeyInput key codes.
	 * 
	 * @param key
	 *            swt KeyEvent key code
	 * @return jme KeyInput key code
	 */
	public static int toInputCode(int key) {
		switch (key) {
		case SWT.ESC:
			return KEY_ESCAPE;
		case '1':
			return KEY_1;
		case '2':
			return KEY_2;
		case '3':
			return KEY_3;
		case '4':
			return KEY_4;
		case '5':
			return KEY_5;
		case '6':
			return KEY_6;
		case '7':
			return KEY_7;
		case '8':
			return KEY_8;
		case '9':
			return KEY_9;
		case '0':
			return KEY_0;
		case '-':
			return KEY_MINUS;
		case '=':
			return KEY_EQUALS;
		case SWT.BS:
			return KEY_BACK;
		case SWT.TAB:
			return KEY_TAB;
		case 'q':
			return KEY_Q;
		case 'w':
			return KEY_W;
		case 'e':
			return KEY_E;
		case 'r':
			return KEY_R;
		case 't':
			return KEY_T;
		case 'y':
			return KEY_Y;
		case 'u':
			return KEY_U;
		case 'i':
			return KEY_I;
		case 'o':
			return KEY_O;
		case 'p':
			return KEY_P;
		case '(':
			return KEY_LBRACKET;
		case ')':
			return KEY_RBRACKET;
		case SWT.TRAVERSE_RETURN:
			return KEY_RETURN;
		case SWT.CTRL:
			return KEY_LCONTROL;
		case 'a':
			return KEY_A;
		case 's':
			return KEY_S;
		case 'd':
			return KEY_D;
		case 'f':
			return KEY_F;
		case 'g':
			return KEY_G;
		case 'h':
			return KEY_H;
		case 'j':
			return KEY_J;
		case 'k':
			return KEY_K;
		case 'l':
			return KEY_L;
		case ';':
			return KEY_SEMICOLON;
		case '"':
			return KEY_APOSTROPHE;
		case '`':
			return KEY_GRAVE;
		case SWT.SHIFT:
			return KEY_LSHIFT;
		case '\\':
			return KEY_BACKSLASH;
		case 'z':
			return KEY_Z;
		case 'x':
			return KEY_X;
		case 'c':
			return KEY_C;
		case 'v':
			return KEY_V;
		case 'b':
			return KEY_B;
		case 'n':
			return KEY_N;
		case 'm':
			return KEY_M;
		case ',':
			return KEY_COMMA;
		case '.':
			return KEY_PERIOD;
		case '/':
			return KEY_SLASH;
		case '*':
			return KEY_MULTIPLY;
		case ' ':
			return KEY_SPACE;
		case SWT.CAPS_LOCK:
			return KEY_CAPITAL;
		case SWT.F1:
			return KEY_F1;
		case SWT.F2:
			return KEY_F2;
		case SWT.F3:
			return KEY_F3;
		case SWT.F4:
			return KEY_F4;
		case SWT.F5:
			return KEY_F5;
		case SWT.F6:
			return KEY_F6;
		case SWT.F7:
			return KEY_F7;
		case SWT.F8:
			return KEY_F8;
		case SWT.F9:
			return KEY_F9;
		case SWT.F10:
			return KEY_F10;
		case SWT.NUM_LOCK:
			return KEY_NUMLOCK;
		case SWT.SCROLL_LOCK:
			return KEY_SCROLL;
		case SWT.KEYPAD_7:
			return KEY_NUMPAD7;
		case SWT.KEYPAD_8:
			return KEY_NUMPAD8;
		case SWT.KEYPAD_9:
			return KEY_NUMPAD9;
		case SWT.KEYPAD_4:
			return KEY_NUMPAD4;
		case SWT.KEYPAD_5:
			return KEY_NUMPAD5;
		case SWT.KEYPAD_6:
			return KEY_NUMPAD6;
		case '+':
			return KEY_ADD;
		case SWT.KEYPAD_1:
			return KEY_NUMPAD1;
		case SWT.KEYPAD_2:
			return KEY_NUMPAD2;
		case SWT.KEYPAD_3:
			return KEY_NUMPAD3;
		case SWT.KEYPAD_0:
			return KEY_NUMPAD0;
		case SWT.KEYPAD_DECIMAL:
			return KEY_DECIMAL;
		case SWT.F11:
			return KEY_F11;
		case SWT.F12:
			return KEY_F12;
		case SWT.F13:
			return KEY_F13;
		case SWT.F14:
			return KEY_F14;
		case SWT.F15:
			return KEY_F15;
		case '@':
			return KEY_AT;
		case '|':
			return KEY_COLON;
		case '_':
			return KEY_UNDERLINE;
		case SWT.KEYPAD_DIVIDE:
			return KEY_DIVIDE;
		case SWT.PAUSE:
			return KEY_PAUSE;
		case SWT.HOME:
			return KEY_HOME;
		case SWT.ARROW_UP:
			return KEY_UP;
		case SWT.PAGE_UP:
			return KEY_PRIOR;
		case SWT.ARROW_LEFT:
			return KEY_LEFT;
		case SWT.ARROW_RIGHT:
			return KEY_RIGHT;
		case SWT.END:
			return KEY_END;
		case SWT.ARROW_DOWN:
			return KEY_DOWN;
		case SWT.PAGE_DOWN:
			return KEY_NEXT;
		case SWT.INSERT:
			return KEY_INSERT;
		case SWT.DEL:
			return KEY_DELETE;

		}
		logger.warning("unsupported key:" + key);
		if (key >= 0x10000) {
			return key - 0x10000;
		}

		return 0;
	}

	static String getKeyText(int keyCode) {
		switch (keyCode) {

		/* Keyboard and Mouse Masks */
		case SWT.ALT:
			return "ALT";
		case SWT.SHIFT:
			return "SHIFT";
		case SWT.CONTROL:
			return "CONTROL";
		case SWT.COMMAND:
			return "COMMAND";

			/* Non-Numeric Keypad Keys */
		case SWT.ARROW_UP:
			return "ARROW_UP";
		case SWT.ARROW_DOWN:
			return "ARROW_DOWN";
		case SWT.ARROW_LEFT:
			return "ARROW_LEFT";
		case SWT.ARROW_RIGHT:
			return "ARROW_RIGHT";
		case SWT.PAGE_UP:
			return "PAGE_UP";
		case SWT.PAGE_DOWN:
			return "PAGE_DOWN";
		case SWT.HOME:
			return "HOME";
		case SWT.END:
			return "END";
		case SWT.INSERT:
			return "INSERT";

			/* Virtual and Ascii Keys */
		case SWT.BS:
			return "BS";
		case SWT.CR:
			return "CR";
		case SWT.DEL:
			return "DEL";
		case SWT.ESC:
			return "ESC";
		case SWT.LF:
			return "LF";
		case SWT.TAB:
			return "TAB";

			/* Functions Keys */
		case SWT.F1:
			return "F1";
		case SWT.F2:
			return "F2";
		case SWT.F3:
			return "F3";
		case SWT.F4:
			return "F4";
		case SWT.F5:
			return "F5";
		case SWT.F6:
			return "F6";
		case SWT.F7:
			return "F7";
		case SWT.F8:
			return "F8";
		case SWT.F9:
			return "F9";
		case SWT.F10:
			return "F10";
		case SWT.F11:
			return "F11";
		case SWT.F12:
			return "F12";
		case SWT.F13:
			return "F13";
		case SWT.F14:
			return "F14";
		case SWT.F15:
			return "F15";

			/* Numeric Keypad Keys */
		case SWT.KEYPAD_ADD:
			return "KEYPAD_ADD";
		case SWT.KEYPAD_SUBTRACT:
			return "KEYPAD_SUBTRACT";
		case SWT.KEYPAD_MULTIPLY:
			return "KEYPAD_MULTIPLY";
		case SWT.KEYPAD_DIVIDE:
			return "KEYPAD_DIVIDE";
		case SWT.KEYPAD_DECIMAL:
			return "KEYPAD_DECIMAL";
		case SWT.KEYPAD_CR:
			return "KEYPAD_CR";
		case SWT.KEYPAD_0:
			return "KEYPAD_0";
		case SWT.KEYPAD_1:
			return "KEYPAD_1";
		case SWT.KEYPAD_2:
			return "KEYPAD_2";
		case SWT.KEYPAD_3:
			return "KEYPAD_3";
		case SWT.KEYPAD_4:
			return "KEYPAD_4";
		case SWT.KEYPAD_5:
			return "KEYPAD_5";
		case SWT.KEYPAD_6:
			return "KEYPAD_6";
		case SWT.KEYPAD_7:
			return "KEYPAD_7";
		case SWT.KEYPAD_8:
			return "KEYPAD_8";
		case SWT.KEYPAD_9:
			return "KEYPAD_9";
		case SWT.KEYPAD_EQUAL:
			return "KEYPAD_EQUAL";

			/* Other keys */
		case SWT.CAPS_LOCK:
			return "CAPS_LOCK";
		case SWT.NUM_LOCK:
			return "NUM_LOCK";
		case SWT.SCROLL_LOCK:
			return "SCROLL_LOCK";
		case SWT.PAUSE:
			return "PAUSE";
		case SWT.BREAK:
			return "BREAK";
		case SWT.PRINT_SCREEN:
			return "PRINT_SCREEN";
		case SWT.HELP:
			return "HELP";
		}
		return character((char) keyCode);
	}

	static String character(char character) {
		switch (character) {
		case 0:
			return "'\\0'";
		case SWT.BS:
			return "'\\b'";
		case SWT.CR:
			return "'\\r'";
		case SWT.DEL:
			return "DEL";
		case SWT.ESC:
			return "ESC";
		case SWT.LF:
			return "'\\n'";
		case SWT.TAB:
			return "'\\t'";
		}
		return "'" + character + "'";
	}

	@Override
	public void clear() {
		keyDown.clear();
	}

	@Override
	public void clearKey(int keycode) {
		keyDown.set(keycode, false);
	}
}
