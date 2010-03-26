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

public final class ApplicationAccessListHelper
{
    public static void
    write(IceInternal.BasicStream __os, ApplicationAccessInfo[] __v)
    {
        if(__v == null)
        {
            __os.writeSize(0);
        }
        else
        {
            __os.writeSize(__v.length);
            for(int __i0 = 0; __i0 < __v.length; __i0++)
            {
                __v[__i0].__write(__os);
            }
        }
    }

    public static ApplicationAccessInfo[]
    read(IceInternal.BasicStream __is)
    {
        ApplicationAccessInfo[] __v;
        final int __len0 = __is.readSize();
        __is.checkFixedSeq(__len0, 5);
        __v = new ApplicationAccessInfo[__len0];
        for(int __i0 = 0; __i0 < __len0; __i0++)
        {
            __v[__i0] = new ApplicationAccessInfo();
            __v[__i0].__read(__is);
        }
        return __v;
    }
}
