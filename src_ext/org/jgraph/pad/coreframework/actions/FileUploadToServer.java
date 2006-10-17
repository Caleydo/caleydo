/*
 * Copyright (C) 2001-2004 Gaudenz Alder
 *
 * GPGraphpad is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * GPGraphpad is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPGraphpad; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jgraph.pad.coreframework.actions;

import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.jgraph.JGraph;
import org.jgraph.pad.coreframework.GPSessionParameters;

/*
 * Currently implemented to work along with the MoinMoin wiki (instead of TwikiDraw), but could work with other servers/wiki with
 * little work. JGraphpad 3.0 was also known to work with TikiWiki.
 */
public class FileUploadToServer extends FileExit {

	protected boolean exitOnSave = true;

	static String NL = "\r\n";

	static String NLNL = NL + NL;

	public void actionPerformed(ActionEvent e) {
		if (graphpad != null) {
			JGraph graph = getCurrentGraph();
			try {
				upload(graph);
				
				//disabled as it was wronly used in the MoinMoin wiki (the map seems correct however):
				//TODO make it work again!
				//uploadMap(graph);

				Object[] selection = graph.getSelectionCells();
				boolean gridVisible = graph.isGridVisible();
				boolean opaque = graph.isOpaque();
				graph.setGridVisible(false);
				graph.clearSelection();
				graph.setOpaque(false);

				BufferedImage image = graph.getImage(null, 5);
				uploadPNG(image);

				graph.setSelectionCells(selection);
				graph.setGridVisible(gridVisible);
				graph.setOpaque(opaque);

				getCurrentDocument().setModified(false);
			} catch (IOException e1) {
				graphpad.error(e1.toString());
			}
		}
		if (exitOnSave)
			super.actionPerformed(e);
	}

	//TODO: compress (don't forget to decompress the file also then)
	public void upload(JGraph graph) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		getCurrentDocument().getJGraphpadCEFile().saveFile(out);
		String savePath = graphpad.getSessionParameters().getParam(GPSessionParameters.UPLOADPATH,
				true);
		String baseName = graphpad.getSessionParameters().getParam(GPSessionParameters.UPLOADFILE,
				true);
		String drawingPath = graphpad.getSessionParameters().getParam(
				GPSessionParameters.DOWNLOADPATH, true);
		post(savePath, baseName + ".draw", "text/plain", drawingPath, out.toString(), "JGraphpad file");
	}

	public void uploadMap(JGraph graph) throws IOException {
		/*
		 * String mapFile = graphpad.getSessionParameters().getParam(
		 * GPSessionParameters.MAPFILE, true);
		 */
		String html = FileExportImageMap.myEncoder.encode(graph, "map");

		String savePath = graphpad.getSessionParameters().getParam(GPSessionParameters.UPLOADPATH,
				true);
		String baseName = graphpad.getSessionParameters().getParam(GPSessionParameters.UPLOADFILE,
				true);
		String drawingPath = graphpad.getSessionParameters().getParam(
				GPSessionParameters.DOWNLOADPATH, true);
		String mapPath = drawingPath.substring(0, drawingPath.length() - 5);

		post(savePath, baseName + ".map", "text/plain", mapPath + ".map", html,
				"JGraphpad map file");
	}

	public void uploadPNG(BufferedImage image) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", bos);
		bos.flush();
		// This is required for correct conversion
		byte[] aByte = bos.toByteArray();
		int size = aByte.length;
		char[] aChar = new char[size];
		for (int i = 0; i < size; i++) {
			aChar[i] = (char) aByte[i];
		}
		String savePath = graphpad.getSessionParameters().getParam(GPSessionParameters.UPLOADPATH,
				true);
		String baseName = graphpad.getSessionParameters().getParam(GPSessionParameters.UPLOADFILE,
				true);
		String pngPath = graphpad.getSessionParameters().getParam(GPSessionParameters.VIEWPATH,
				true);

		post(savePath, baseName + ".png", "image/png", pngPath, String.valueOf(
				aChar, 0, aChar.length), "JGraphpad PNG file");
	}

	/**
	 * @return whether or not a reply was received
	 */
	static public boolean post(String protocol, String xml, String serverName,
			int port, String uploadURL, String xmlURL)
			throws MalformedURLException, IOException {
		return post(protocol, serverName, port, uploadURL, "", "text/plain",
				xmlURL, xml, "JGraph XML File");
	}

	/**
	 * Submits POST command to the server, and reads the reply.
	 */
	public boolean post(String url, String fileName, String type, String path,
			String content, String comment) throws MalformedURLException,
			IOException {

		String sep = "89692781418184";
		while (content.indexOf(sep) != -1)
			sep += "x";

		String message = makeMimeForm(fileName, type, path, content, comment,
				sep);

		URL server = new URL(graphpad.getSessionParameters().getParam(
				GPSessionParameters.PROTOCOL, true), graphpad.getSessionParameters().getParam(
						GPSessionParameters.HOSTNAME, true), Integer.parseInt(graphpad.getSessionParameters().getParam(GPSessionParameters.HOSTPORT, true)), url);
		URLConnection connection = server.openConnection();

		connection.setAllowUserInteraction(false);
		connection.setDoOutput(true);
		// connection.setDoInput(true);
		connection.setUseCaches(false);

		connection.setRequestProperty("Content-type",
				"multipart/form-data; boundary=" + sep);
		connection.setRequestProperty("Content-length", Integer
				.toString(message.length()));

		System.out.println(url);
		String replyString = null;
		try {
			DataOutputStream out = new DataOutputStream(connection
					.getOutputStream());
			out.writeBytes(message);
			out.close();
			System.out.println("Wrote " + message.length() +
			 " bytes to\n" + connection);

			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String reply = null;
				while ((reply = in.readLine()) != null) {
					if (reply.startsWith("ERROR ")) {
						replyString = reply.substring("ERROR ".length());
					}
				}
				in.close();
			} catch (IOException ioe) {
				replyString = ioe.toString();
			}
		} catch (UnknownServiceException use) {
			replyString = use.getMessage();
			System.out.println(message);
		}
		if (replyString != null) {
			System.out.println("---- Reply " + replyString);
			if (replyString.startsWith("URL ")) {
				/*URL eurl = new URL(graphpad.getSessionParameters().getApplet()
						.getCodeBase(), replyString.substring("URL ".length()));
				graphpad.getSessionParameters().getApplet().getAppletContext()
						.showDocument(eurl);*/
			} else if (replyString.startsWith("java.io.FileNotFoundException")) {
				// debug; when run from appletviewer, the http connection
				// is not available so write the file content
				if (path.endsWith(".draw") || path.endsWith(".map"))
					System.out.println(content);
			} else
				// showStatus(replyString);
				return false;
		} else {
			// showStatus(url + " saved");
			return true;
		}
		return true;// TODO sure?
	}

	/**
	 * @return whether or not a reply was received
	 */
	static public boolean postPng(String protocol, String png,
			String serverName, int port, String uploadURL, String pngURL)
			throws MalformedURLException, IOException {
		return post(protocol, serverName, port, uploadURL, "", "image/png",
				pngURL, png, "JGraph PNG File");
	}

	/**
	 * @return whether or not a reply was received
	 */
	static public boolean postJpg(String protocol, String jpg,
			String serverName, int port, String uploadURL, String jpgURL)
			throws MalformedURLException, IOException {
		return post(protocol, serverName, port, uploadURL, "", "image/jpg",
				jpgURL, jpg, "JGraph PNG File");
	}

	/**
	 * @return whether or not a reply was received
	 */
	static public boolean post(String protocol, String serverName,
			int portNumber, String url, String fileName, String type,
			String path, String content, String comment)
			throws MalformedURLException, IOException {
		String sep = "89692781418184";
		while (content.indexOf(sep) != -1)
			sep += "x";
		String message = makeMimeFormOld(fileName, type, path, content,
				comment, sep);
		// Ask for parameters
		URL server = new URL(protocol, serverName, portNumber, url);
		URLConnection connection = server.openConnection();
		connection.setAllowUserInteraction(false);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-type",
				"multipart/form-data; boundary=" + sep);
		connection.setRequestProperty("Content-length", Integer
				.toString(message.length()));
		String replyString = null;
		try {
			DataOutputStream out = new DataOutputStream(connection
					.getOutputStream());
			out.writeBytes(message);
			out.close();
			System.out.println("Wrote " + message.length() + " bytes to\n"
					+ connection);
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String reply = null;
				while ((reply = in.readLine()) != null) {
					if (reply.startsWith("ERROR ")) {
						replyString = reply.substring("ERROR ".length());
					}
				}
				in.close();
			} catch (IOException ioe) {
				replyString = ioe.toString();
				System.out.println(ioe + ": " + connection);
			}
		} catch (UnknownServiceException use) {
			replyString = use.getMessage();
			System.out.println(message);
		}
		if (replyString != null) {
			return false;
		}
		return true;
	}

	/**
	 * @return the MIME form of the input strings
	 */
	private static String makeMimeFormOld(String fileName, String type,
			String path, String content, String comment, String sep) {
		String binary = "";
		if (type.equals("image/png") || type.equals("image/jpg")
				|| type.equals("image/gif")) {
			binary = "Content-Transfer-Encoding: binary" + NL;
		}
		String mime_sep = NL + "--" + sep + NL;
		return "--" + sep + "\r\n"
				+ "Content-Disposition: form-data; name=\"filename\"" + NLNL
				+ fileName + mime_sep
				+ "Content-Disposition: form-data; name=\"noredirect\"" + NLNL
				+ 1 + mime_sep
				+ "Content-Disposition: form-data; name=\"filepath\"; "
				+ "filename=\"" + path + "\"" + NL + "Content-Type: " + type
				+ NL + binary + NL + content + mime_sep
				+ "Content-Disposition: form-data; name=\"filecomment\"" + NLNL
				+ comment + NL + "--" + sep + "--" + NL;
	}

	/**
	 * empty implementation for this typ of action
	 * 
	 */
	public void update() {
		if (graphpad.getCurrentDocument() == null)
			setEnabled(false);
		else
			setEnabled(true);
	}

	/** Post the given message */
	private String makeMimeForm(String fileName, String type, String path,
			String content, String comment, String sep) {

		String binary = "";
		if (type.equals("image/gif")) {
			binary = "Content-Transfer-Encoding: binary" + NL;
		}

		String mime_sep = NL + "--" + sep + NL;

		return "--" + sep + "\r\n"
				+ "Content-Disposition: form-data; name=\"filename\"" + NLNL
				+ fileName + mime_sep
				+ "Content-Disposition: form-data; name=\"noredirect\"" + NLNL
				+ 1 + mime_sep
				+ "Content-Disposition: form-data; name=\"filepath\"; "
				+ "filename=\"" + path + "\"" + NL + "Content-Type: " + type
				+ NL + binary + NL + content + mime_sep
				+ "Content-Disposition: form-data; name=\"filecomment\"" + NLNL
				+ comment + NL + "--" + sep + "--" + NL;
	}

}
