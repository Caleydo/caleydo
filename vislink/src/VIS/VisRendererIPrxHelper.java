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

public final class VisRendererIPrxHelper extends Ice.ObjectPrxHelperBase implements VisRendererIPrx
{
    public void
    clearAll()
    {
        clearAll(null, false);
    }

    public void
    clearAll(java.util.Map<String, String> __ctx)
    {
        clearAll(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    clearAll(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                _VisRendererIDel __del = (_VisRendererIDel)__delBase;
                __del.clearAll(__ctx);
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

    public void
    clearSelections()
    {
        clearSelections(null, false);
    }

    public void
    clearSelections(java.util.Map<String, String> __ctx)
    {
        clearSelections(__ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    clearSelections(java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                _VisRendererIDel __del = (_VisRendererIDel)__delBase;
                __del.clearSelections(__ctx);
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

    public AccessInformation
    getAccessInformation(int sourceApplicationId)
    {
        return getAccessInformation(sourceApplicationId, null, false);
    }

    public AccessInformation
    getAccessInformation(int sourceApplicationId, java.util.Map<String, String> __ctx)
    {
        return getAccessInformation(sourceApplicationId, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private AccessInformation
    getAccessInformation(int sourceApplicationId, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("getAccessInformation");
                __delBase = __getDelegate(false);
                _VisRendererIDel __del = (_VisRendererIDel)__delBase;
                return __del.getAccessInformation(sourceApplicationId, __ctx);
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

    public boolean
    registerSelectionContainer(SelectionContainer container)
    {
        return registerSelectionContainer(container, null, false);
    }

    public boolean
    registerSelectionContainer(SelectionContainer container, java.util.Map<String, String> __ctx)
    {
        return registerSelectionContainer(container, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    registerSelectionContainer(SelectionContainer container, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("registerSelectionContainer");
                __delBase = __getDelegate(false);
                _VisRendererIDel __del = (_VisRendererIDel)__delBase;
                return __del.registerSelectionContainer(container, __ctx);
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

    public void
    renderAllLinks(SelectionGroup[] selections)
    {
        renderAllLinks(selections, null, false);
    }

    public void
    renderAllLinks(SelectionGroup[] selections, java.util.Map<String, String> __ctx)
    {
        renderAllLinks(selections, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    renderAllLinks(SelectionGroup[] selections, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                _VisRendererIDel __del = (_VisRendererIDel)__delBase;
                __del.renderAllLinks(selections, __ctx);
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

    public void
    renderLinks(SelectionGroup selections)
    {
        renderLinks(selections, null, false);
    }

    public void
    renderLinks(SelectionGroup selections, java.util.Map<String, String> __ctx)
    {
        renderLinks(selections, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    renderLinks(SelectionGroup selections, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                _VisRendererIDel __del = (_VisRendererIDel)__delBase;
                __del.renderLinks(selections, __ctx);
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

    public void
    unregisterSelectionContainer(int id)
    {
        unregisterSelectionContainer(id, null, false);
    }

    public void
    unregisterSelectionContainer(int id, java.util.Map<String, String> __ctx)
    {
        unregisterSelectionContainer(id, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private void
    unregisterSelectionContainer(int id, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                _VisRendererIDel __del = (_VisRendererIDel)__delBase;
                __del.unregisterSelectionContainer(id, __ctx);
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

    public boolean
    updateSelectionContainer(SelectionContainer container)
    {
        return updateSelectionContainer(container, null, false);
    }

    public boolean
    updateSelectionContainer(SelectionContainer container, java.util.Map<String, String> __ctx)
    {
        return updateSelectionContainer(container, __ctx, true);
    }

    @SuppressWarnings("unchecked")
    private boolean
    updateSelectionContainer(SelectionContainer container, java.util.Map<String, String> __ctx, boolean __explicitCtx)
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
                __checkTwowayOnly("updateSelectionContainer");
                __delBase = __getDelegate(false);
                _VisRendererIDel __del = (_VisRendererIDel)__delBase;
                return __del.updateSelectionContainer(container, __ctx);
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

    public static VisRendererIPrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        VisRendererIPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (VisRendererIPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::VisRendererI"))
                {
                    VisRendererIPrxHelper __h = new VisRendererIPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static VisRendererIPrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        VisRendererIPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (VisRendererIPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::VIS::VisRendererI", __ctx))
                {
                    VisRendererIPrxHelper __h = new VisRendererIPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static VisRendererIPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        VisRendererIPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::VisRendererI"))
                {
                    VisRendererIPrxHelper __h = new VisRendererIPrxHelper();
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

    public static VisRendererIPrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        VisRendererIPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::VIS::VisRendererI", __ctx))
                {
                    VisRendererIPrxHelper __h = new VisRendererIPrxHelper();
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

    public static VisRendererIPrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        VisRendererIPrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (VisRendererIPrx)__obj;
            }
            catch(ClassCastException ex)
            {
                VisRendererIPrxHelper __h = new VisRendererIPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static VisRendererIPrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        VisRendererIPrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            VisRendererIPrxHelper __h = new VisRendererIPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _VisRendererIDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _VisRendererIDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, VisRendererIPrx v)
    {
        __os.writeProxy(v);
    }

    public static VisRendererIPrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            VisRendererIPrxHelper result = new VisRendererIPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
