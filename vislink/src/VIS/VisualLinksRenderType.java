// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package VIS;

public enum VisualLinksRenderType implements java.io.Serializable
{
    RenderTypeUndefined,
    RenderTypeNormal,
    RenderTypeOneShot,
    RenderTypePermanent;

    public static final int _RenderTypeUndefined = 0;
    public static final int _RenderTypeNormal = 1;
    public static final int _RenderTypeOneShot = 2;
    public static final int _RenderTypePermanent = 3;

    public static VisualLinksRenderType
    convert(int val)
    {
        assert val >= 0 && val < 4;
        return values()[val];
    }

    public static VisualLinksRenderType
    convert(String val)
    {
        try
        {
            return valueOf(val);
        }
        catch(java.lang.IllegalArgumentException ex)
        {
            return null;
        }
    }

    public int
    value()
    {
        return ordinal();
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeByte((byte)value());
    }

    public static VisualLinksRenderType
    __read(IceInternal.BasicStream __is)
    {
        int __v = __is.readByte(4);
        return VisualLinksRenderType.convert(__v);
    }
}
