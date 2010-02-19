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

public final class ApplicationIdListHelper
{
    public static void
    write(IceInternal.BasicStream __os, int[] __v)
    {
        __os.writeIntSeq(__v);
    }

    public static int[]
    read(IceInternal.BasicStream __is)
    {
        int[] __v;
        __v = __is.readIntSeq();
        return __v;
    }
}
