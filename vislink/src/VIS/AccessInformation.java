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

public final class AccessInformation implements java.lang.Cloneable, java.io.Serializable
{
    public String pointerId;

    public int[] applicationIds;

    public AccessInformation()
    {
    }

    public AccessInformation(String pointerId, int[] applicationIds)
    {
        this.pointerId = pointerId;
        this.applicationIds = applicationIds;
    }

    public boolean
    equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        AccessInformation _r = null;
        try
        {
            _r = (AccessInformation)rhs;
        }
        catch(ClassCastException ex)
        {
        }

        if(_r != null)
        {
            if(pointerId != _r.pointerId && pointerId != null && !pointerId.equals(_r.pointerId))
            {
                return false;
            }
            if(!java.util.Arrays.equals(applicationIds, _r.applicationIds))
            {
                return false;
            }

            return true;
        }

        return false;
    }

    public int
    hashCode()
    {
        int __h = 0;
        if(pointerId != null)
        {
            __h = 5 * __h + pointerId.hashCode();
        }
        if(applicationIds != null)
        {
            for(int __i0 = 0; __i0 < applicationIds.length; __i0++)
            {
                __h = 5 * __h + applicationIds[__i0];
            }
        }
        return __h;
    }

    public java.lang.Object
    clone()
    {
        java.lang.Object o = null;
        try
        {
            o = super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return o;
    }

    public void
    __write(IceInternal.BasicStream __os)
    {
        __os.writeString(pointerId);
        ApplicationIdListHelper.write(__os, applicationIds);
    }

    public void
    __read(IceInternal.BasicStream __is)
    {
        pointerId = __is.readString();
        applicationIds = ApplicationIdListHelper.read(__is);
    }
}
