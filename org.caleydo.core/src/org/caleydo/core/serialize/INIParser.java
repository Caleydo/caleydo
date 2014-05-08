/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Java XPCOM Bindings.
 *
 * The Initial Developer of the Original Code is
 * IBM Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2005
 * IBM Corporation. All Rights Reserved.
 *
 * Contributor(s):
 *   Javier Pedemonte (jhpedemonte@gmail.com)
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.caleydo.core.serialize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;


/**
 * A simple parser for INI files.
 */
public class INIParser {

	public class Section extends Properties {

		private static final long serialVersionUID = -8488478507038061864L;

		public String extractString(String aKey) {
			String val = (String) get(aKey);
			remove(aKey);
			return val;
		}

		public File extractFile(String key) {
			String path = extractString(key);
			if (path == null || path.isEmpty())
				return null;

			return new File(basePath, path);
		}

		@Override
		public synchronized Enumeration<Object> keys() {
			return Collections.enumeration(new TreeSet<Object>(super.keySet()));
		}
	}

	protected File basePath;

	protected HashMap<String, Section> mSections;

  public INIParser() {
    mSections = new LinkedHashMap<String, Section>();
  }

  /**
   * Creates a new <code>INIParser</code> instance from the INI file at the
   * given path. <code>aCharset</code> specifies the character encoding of
   * the file.
   *
   * @param aFilename path of INI file to parse
   * @param aCharset character encoding of file
   * @throws FileNotFoundException if <code>aFilename</code> does not exist.
   * @throws IOException if there is a problem reading the given file.
   */
  public INIParser(String aFilename, Charset aCharset)
          throws FileNotFoundException, IOException {
    initFromFile(new File(aFilename), aCharset);
  }

  /**
   * Creates a new <code>INIParser</code> instance from the INI file at the
   * given path, which is assumed to be in the <code>UTF-8</code> charset.
   *
   * @param aFilename path of INI file to parse
   * @throws FileNotFoundException if <code>aFilename</code> does not exist.
   * @throws IOException if there is a problem reading the given file.
   */
  public INIParser(String aFilename) throws FileNotFoundException, IOException {
    initFromFile(new File(aFilename), Charset.forName("UTF-8"));
  }

  /**
   * Creates a new <code>INIParser</code> instance from the given file.
   * <code>aCharset</code> specifies the character encoding of the file.
   *
   * @param aFile INI file to parse
   * @param aCharset character encoding of file
   * @throws FileNotFoundException if <code>aFile</code> does not exist.
   * @throws IOException if there is a problem reading the given file.
   */
  public INIParser(File aFile, Charset aCharset)
          throws FileNotFoundException, IOException {
    initFromFile(aFile, aCharset);
  }

  /**
   * Creates a new <code>INIParser</code> instance from the given file,
   * which is assumed to be in the <code>UTF-8</code> charset.
   *
   * @param aFile INI file to parse
   * @throws FileNotFoundException if <code>aFile</code> does not exist.
   * @throws IOException if there is a problem reading the given file.
   */
  public INIParser(File aFile) throws FileNotFoundException, IOException {
    initFromFile(aFile, Charset.forName("UTF-8"));
  }

  /**
   * Parses given INI file.
   *
   * @param aFile INI file to parse
   * @param aCharset character encoding of file
   * @throws FileNotFoundException if <code>aFile</code> does not exist.
   * @throws IOException if there is a problem reading the given file.
   */
  private void initFromFile(File aFile, Charset aCharset)
          throws FileNotFoundException, IOException {
	basePath = aFile.getParentFile();
    FileInputStream fileStream = new FileInputStream(aFile);
    InputStreamReader inStream = new InputStreamReader(fileStream, aCharset);
    BufferedReader reader = new BufferedReader(inStream);

    mSections = new LinkedHashMap<String, Section>();
    String currSection = null;

    String line;
    while ((line = reader.readLine()) != null) {
      // skip empty lines and comment lines
      String trimmedLine = line.trim();
      if (trimmedLine.length() == 0 || trimmedLine.startsWith("#")
              || trimmedLine.startsWith(";")) {
        continue;
      }

      // Look for section headers (i.e. "[Section]").
      if (line.startsWith("[")) {
        /*
         * We are looking for a well-formed "[Section]".  If this header is
         * malformed (i.e. "[Section" or "[Section]Moretext"), just skip it
         * and go on to next well-formed section header.
         */
        if (!trimmedLine.endsWith("]") ||
            trimmedLine.indexOf("]") != (trimmedLine.length() - 1)) {
          currSection = null;
          continue;
        }

        // remove enclosing brackets
        currSection = trimmedLine.substring(1, trimmedLine.length() - 1);
        continue;
      }

      // If we haven't found a valid section header, continue to next line
      if (currSection == null) {
        continue;
      }

      StringTokenizer tok = new StringTokenizer(line, "=");
      if (tok.countTokens() != 2) { // looking for value pairs
        continue;
      }

      Section props = mSections.get(currSection);
      if (props == null) {
        props = new Section();
        mSections.put(currSection, props);
      }
      props.setProperty(tok.nextToken(), tok.nextToken());
    }

    reader.close();
  }

  /**
   * Returns an iterator over the section names available in the INI file.
   *
   * @return an iterator over the section names
   */
  public HashMap<String, Section> getSections() {
    return mSections;
  }

  public Section getOrCreateSection(String name) {
    Section section = mSections.get(name);
    if (section == null) {
      section = new Section();
      mSections.put(name, section);
    }

    return section;
  }

  /**
   * Returns an iterator over the keys available within a section.
   *
   * @param aSection section name whose keys are to be returned
   * @return an iterator over section keys, or <code>null</code> if no
   *          such section exists
   */
  public ArrayList<?> getKeys(String aSection) {
    Properties props = mSections.get(aSection);
    if (props == null)
      return new ArrayList<String>();

    return Collections.list(props.propertyNames());
  }

  /**
   * Gets the string value for a particular section and key.
   *
   * @param aSection a section name
   * @param aKey the key whose value is to be returned.
   * @return string value of particular section and key
   */
  public String getString(String aSection, String aKey) {
    Properties props = mSections.get(aSection);
    if (props == null) {
      return null;
    }

    return props.getProperty(aKey);
  }

  /**
   * Sets the string value for a particular section and key.
   *
   * Creates a new section/key if they do not exist.
   *
   * @param section
   * @param key
   * @param value
   */
  public void setString(String section, String key, String value) {
	  Section props = mSections.get(section);
      if (props == null) {
        props = new Section();
        mSections.put(section, props);
      }
      props.setProperty(key, value);
  }

  public void write(Writer writer) throws IOException {
	for(Map.Entry<String, Section> section: mSections.entrySet()) {
		writer.write("[" + section.getKey() + "]\n");
		for(Object key: Collections.list(section.getValue().keys())) {
			writer.write(key + "=" + section.getValue().get(key) + "\n");
		}
	}
  }

}
