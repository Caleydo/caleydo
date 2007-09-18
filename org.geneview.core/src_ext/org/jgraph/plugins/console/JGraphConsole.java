/*
 * @(#)GPLogConsole.java 1.0 12-MAY-2004
 * 
 * Copyright (c) 2001-2004, luzar
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of JGraph nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.jgraph.plugins.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

/**
 * Shows the System.in and System.out in a nice JFrame.
 * 
 * The Frame looks like this: <br>
 * <img src="doc-files/GPLogConsole.jpg">
 * 
 * @author Thomas Genssler (FZI)
 * @author Sven Luzar
 */
public class JGraphConsole extends JTextArea {

	public static int STREAM_OUT = 0, STREAM_ERR = 1;

	protected PrintStream stream;

	/**
	 * Logs the System out and System error
	 */
	private JGraphConsole(PrintStream stream) {
		this.stream = stream;
		PrintStream textStream = new JTextAreaOutputStream(this, stream, true);
		if (stream == System.out)
			System.setOut(textStream);
		else if (stream == System.err)
			System.setErr(textStream);
	}
	
	public boolean isErrorConsole() {
		return stream == System.err;
	}

	public static JGraphConsole createOutConsole() {
		return new JGraphConsole(System.out);
	}
	
	public static JGraphConsole createErrConsole() {
		return new JGraphConsole(System.err);
	}

	/**
	 * A PrintStream for the text area output.
	 * 
	 * @author Sven Luzar
	 */
	class JTextAreaOutputStream extends PrintStream {

		/**
		 * the target for this printstream
		 */
		private JTextArea target = null;

		/**
		 * the original PrintStream to forward this stream to the original
		 * stream
		 */
		private PrintStream orig = null;

		/**
		 * Flag is true if the stream should forward the output to the original
		 * stream
		 *  
		 */
		private boolean showOrig = false;

		/**
		 * creates an instance
		 *  
		 */
		public JTextAreaOutputStream(JTextArea t, PrintStream orig,
				boolean showOrig) {
			super(new ByteArrayOutputStream());
			target = t;

			this.showOrig = showOrig;
			this.orig = orig;
		}

		/**
		 * writes a boolean value to the target
		 */
		public void print(boolean b) {
			if (showOrig)
				orig.print(b);
			if (b)
				target.append("true"/* #Frozen */);
			else
				target.append("false"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes a boolean value to the target
		 *  
		 */
		public void println(boolean b) {
			if (showOrig)
				orig.println(b);

			if (b)
				target.append("true\n"/* #Frozen */);
			else
				target.append("false\n"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void print(char c) {
			if (showOrig)
				orig.print(c);

			char[] tmp = new char[1];
			tmp[0] = c;
			target.append(new String(tmp));
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println(char c) {
			if (showOrig)
				orig.println(c);

			char[] tmp = new char[2];
			tmp[0] = c;
			tmp[1] = '\n';
			target.append(new String(tmp));
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void print(char[] s) {
			if (showOrig)
				orig.print(s);

			target.append(new String(s));
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println(char[] s) {
			if (showOrig)
				orig.println(s);

			target.append(new String(s) + "\n"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void print(double d) {
			if (showOrig)
				orig.print(d);

			target.append(Double.toString(d));
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println(double d) {
			if (showOrig)
				orig.println(d);

			target.append(Double.toString(d) + "\n"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void print(float f) {
			if (showOrig)
				orig.print(f);

			target.append(Float.toString(f));
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println(float f) {
			if (showOrig)
				orig.println(f);

			target.append(Float.toString(f) + "\n"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void print(int i) {
			if (showOrig)
				orig.print(i);

			target.append(Integer.toString(i));
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println(int i) {
			if (showOrig)
				orig.println(i);

			target.append(Integer.toString(i) + "\n"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void print(long l) {
			if (showOrig)
				orig.print(l);

			target.append(Long.toString(l));
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println(long l) {
			if (showOrig)
				orig.println(l);

			target.append(Long.toString(l) + "\n"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void print(Object o) {
			if (showOrig)
				orig.print(o);

			target.append(o.toString());
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println(Object o) {
			if (showOrig)
				orig.println(o);

			target.append(o.toString() + "\n"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void print(String s) {
			if (showOrig)
				orig.print(s);

			target.append(s);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println(String s) {
			if (showOrig)
				orig.println(s);

			target.append(s + "\n"/* #Frozen */);
			target.setCaretPosition(target.getText().length());
		}

		/**
		 * writes the value to the target
		 *  
		 */
		public void println() {
			if (showOrig)
				orig.println();

			target.append(new String("\n"/* #Frozen */));
			target.setCaretPosition(target.getText().length());
		}
	}
}