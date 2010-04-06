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

public final class WindowLockEventPrxHelper extends Ice.ObjectPrxHelperBase implements WindowLockEventPrx
{
    public static WindowLockEventPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        WindowLockEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (WindowLockEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::WindowLockEvent"))
                {
                    WindowLockEventPrxHelper __h = new WindowLockEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static WindowLockEventPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        WindowLockEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (WindowLockEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::WindowLockEvent", __ctx))
                {
                    WindowLockEventPrxHelper __h = new WindowLockEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static WindowLockEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        WindowLockEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::WindowLockEvent"))
                {
                    WindowLockEventPrxHelper __h = new WindowLockEventPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static WindowLockEventPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        WindowLockEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::WindowLockEvent", __ctx))
                {
                    WindowLockEventPrxHelper __h = new WindowLockEventPrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static WindowLockEventPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        WindowLockEventPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (WindowLockEventPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                WindowLockEventPrxHelper __h = new WindowLockEventPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static WindowLockEventPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        WindowLockEventPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            WindowLockEventPrxHelper __h = new WindowLockEventPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _WindowLockEventDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _WindowLockEventDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, WindowLockEventPrx v)
    {
        __os.writeProxy(v);
    }

    public static WindowLockEventPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            WindowLockEventPrxHelper result = new WindowLockEventPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
