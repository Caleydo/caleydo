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

public final class VisManagerIPrxHelper extends Ice.ObjectPrxHelperBase implements VisManagerIPrx
{
    public void
    reportEvent(InteractionEvent event)
    {
        reportEvent(event, null, false);
    }

    public void
    reportEvent(InteractionEvent event, java.util.Map<String, String> __ctx)
    {
        reportEvent(event, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    reportEvent(InteractionEvent event, java.util.Map<String, String> __ctx, boolean __explicitCtx)
    {
        if(__explicitCtx && __ctx == null)
        {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while(true)
        {
            Ice._ObjectDel __delBase = null;
            try
            {
                __delBase = __getDelegate(false);
                _VisManagerIDel __del = (_VisManagerIDel)__delBase;
                __del.reportEvent(event, __ctx);
                return;
            }
            catch(IceInternal.LocalExceptionWrapper __ex)
            {
                __handleExceptionWrapper(__delBase, __ex, null);
            }
            catch(Ice.LocalException __ex)
            {
                __cnt = __handleException(__delBase, __ex, null, __cnt);
            }
        }
    }

    public static VisManagerIPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        VisManagerIPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (VisManagerIPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::VisManagerI"))
                {
                    VisManagerIPrxHelper __h = new VisManagerIPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static VisManagerIPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        VisManagerIPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (VisManagerIPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::VisManagerI", __ctx))
                {
                    VisManagerIPrxHelper __h = new VisManagerIPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static VisManagerIPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        VisManagerIPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::VisManagerI"))
                {
                    VisManagerIPrxHelper __h = new VisManagerIPrxHelper();
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

    public static VisManagerIPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        VisManagerIPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::VisManagerI", __ctx))
                {
                    VisManagerIPrxHelper __h = new VisManagerIPrxHelper();
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

    public static VisManagerIPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        VisManagerIPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (VisManagerIPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                VisManagerIPrxHelper __h = new VisManagerIPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static VisManagerIPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        VisManagerIPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            VisManagerIPrxHelper __h = new VisManagerIPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _VisManagerIDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _VisManagerIDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, VisManagerIPrx v)
    {
        __os.writeProxy(v);
    }

    public static VisManagerIPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            VisManagerIPrxHelper result = new VisManagerIPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
