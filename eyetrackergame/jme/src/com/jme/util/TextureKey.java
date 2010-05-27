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

package com.jme.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import com.jme.image.Image;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.resource.ResourceLocatorTool;

/**
 * <code>TextureKey</code> provides a way for the TextureManager to cache and
 * retrieve <code>Texture</code> objects.
 * 
 * @author Joshua Slack
 * @version $Id: TextureKey.java 4562 2009-08-03 17:19:25Z blaine.dev $
 */
final public class TextureKey implements Savable {
    protected URL location;
    protected URL persistedUrl;
    protected boolean flipped;
    protected int code = Integer.MAX_VALUE;
    protected Image.Format format = Image.Format.Guess;
    protected String fileType;

    public TextureKey() {

    }

    public TextureKey(URL location, boolean flipped, Image.Format imageType) {
        persistedUrl = location;
        this.location = location;
        this.flipped = flipped;
        this.format = imageType;
    }

    public boolean equals(Object other) {
        /* Question:  should TextureKeys be considered equal because the
         * persisted URLs match, or because the resolved URLs match? */
        if (other == this) {
            return true;
        }
        if (!(other instanceof TextureKey)) {
            return false;
        }

        TextureKey that = (TextureKey) other;
        if (this.location == null) {
            if (that.location != null)
                return false;
        } else if (!this.location.equals(that.location))
            return false;

        if (this.flipped != that.flipped)
            return false;
        if (this.format != that.format)
            return false;
        if (this.fileType == null && that.fileType != null)
            return false;
        else if (this.fileType != null && !this.fileType.equals(that.fileType))
            return false;

        return true;
    }

    public int hashCode() {
        /* Question:  should TextureKeys be considered equal because the
         * persisted URLs match, or because the resolved URLs match? */
        if (code == Integer.MAX_VALUE) {
            code = 37;
            if (location != null) {
                code += 37 * location.hashCode();
            }
            if (fileType != null) {
                code += 37 * fileType.hashCode();
            }
            code += 37 * format.ordinal();
            code += 37 * (flipped ? 1 : 0);
        }
        return code;
    }

    public void resetHashCode() {
        code = Integer.MAX_VALUE;
    }

    public void write(JMEExporter e) throws IOException {
        OutputCapsule capsule = e.getCapsule(this);
        if (persistedUrl != null) {
            capsule.write(persistedUrl.getProtocol(), "protocol", null);
            capsule.write(persistedUrl.getHost(), "host", null);
            capsule.write(persistedUrl.getFile(), "file", null);
        }
        capsule.write(flipped, "flipped", false);
        capsule.write(format, "format", Image.Format.Guess);
        capsule.write(fileType, "fileType", null);
    }

    public void read(JMEImporter e) throws IOException {
        InputCapsule capsule = e.getCapsule(this);
        String protocol = capsule.readString("protocol", null);
        String host = capsule.readString("host", null);
        String file = capsule.readString("file", null);
        if (host != null && protocol == null)
            throw new IOException(
                    "'host' specified with no 'protocol' specied");
        if (file != null) {
            persistedUrl = (protocol == null)
                         ? new URL(file)
                         : new URL(protocol, host, file);
            location = ResourceLocatorTool.locateResource(
                    ResourceLocatorTool.TYPE_TEXTURE, URLDecoder.decode(file,
                            "UTF-8"));
        }
        if (location == null && protocol != null && host != null
                && file != null) {
            location = new URL(protocol, host, file);
        }

        flipped = capsule.readBoolean("flipped", false);
        format = capsule.readEnum("format", Image.Format.class,
                Image.Format.Guess);
        fileType = capsule.readString("fileType", null);
    }

    public Image.Format getFormat() {
        return format;
    }

    public void setFormat(Image.Format format) {
        this.format = format;
    }

    public Class getClassTag() {
        return this.getClass();
    }

    /**
     * @return Returns the flipped.
     */
    public boolean isFlipped() {
        return flipped;
    }

    /**
     * @param flipped
     *            The flipped to set.
     */
    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    /**
     * @return Returns the location.
     */
    public URL getLocation() {
        return location;
    }

    /**
     * @param location
     *            The location to set.
     */
    public void setLocation(URL location) {
        persistedUrl = location;
        this.location = location;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return "tkey: persistedUrl:" + persistedUrl
                + "loc:" + location + " flip: " + flipped + " code: "
                + hashCode() + " imageType: " + format + " fileType: "
                + fileType;
    }
}
