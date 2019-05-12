#Android 焦点源码分析（sdk 28.0.0）

##一、获取焦点

Android中获取焦点的方式比较简单，直接 `View.requestFocus()`或`View.requestFocus(int direction)`，View.requestFocus（）= View.requestFocus(View.FOCUS_DOWN)。

    public final boolean requestFocus() {
        return requestFocus(View.FOCUS_DOWN);
    }

###1.1 View 请求焦点流程

####1.1.1 View.requestFocus(int direction) 请求获取焦点

direction 6种取值类型：

- View.FOCUS_BACKWARD 
- View.FOCUS_FORWARD
- View.FOCUS_LEFT
- View.FOCUS_UP
- View.FOCUS_RIGHT
- View.FOCUS_DOWN

这6种类型，只在 ViewGroup 获取焦点流程中有用到，我们放在 1.2.1 节中详细展开分析。

    public final boolean requestFocus(int direction) {
        return requestFocus(direction, null);
    }

	// 1.2.1 ViewGroup.requestFocus(direction, previouslyFocusedRect) 
	// 和 ViewGroup.requestFocus(direction, previouslyFocusedRect)
	// 处理过程不同.

	public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return requestFocusNoSearch(direction, previouslyFocusedRect);
    }

`View.requestFocus(int direction)` 最终调用的是 `View.requestFocusNoSearch(int direction, Rect previouslyFocusedRect)`

####1.1.2 View.requestFocusNoSearch(int direction, Rect previouslyFocusedRect) 

    private boolean requestFocusNoSearch(int direction, Rect previouslyFocusedRect) {
        // 1.1.3 不可获取焦点返回 false
        if (!canTakeFocus()) {
            return false;
        }

        // 1.1.4 判断全局的 inTouchMode 和 当前 view 的 inTouchMode
        if (isInTouchMode() &&
            (FOCUSABLE_IN_TOUCH_MODE != (mViewFlags & FOCUSABLE_IN_TOUCH_MODE))) {
               return false;
        }

        // 1.1.5 父节点阻拦焦点 返回 false
        if (hasAncestorThatBlocksDescendantFocus()) {
            return false;
        }

        if (!isLayoutValid()) {
            mPrivateFlags |= PFLAG_WANTS_FOCUS;
        } else {
            clearParentsWantFocus();
        }

		// 1.1.6 处理并获取焦点
        handleFocusGainInternal(direction, previouslyFocusedRect);
        return true;
    }

####1.1.3 View.canTakeFocus() 是否可获取焦点

	private boolean canTakeFocus() {
        return ((mViewFlags & VISIBILITY_MASK) == VISIBLE)
                && ((mViewFlags & FOCUSABLE) == FOCUSABLE)
                && ((mViewFlags & ENABLED_MASK) == ENABLED)
                && (sCanFocusZeroSized || !isLayoutValid() || hasSize());
    }

####1.1.4 View.isInTouchMode() 是否 inTouchMode

判断全局属性是否是 inTouchMode：

- 当前 view 已 attach 到其他 view 树时，判断该 view 树是否是 inTouchMode；
- 当前 view 没有 attach 到其他 view 树时，判断当前 Window 是不是 inTouchMode。

	    public boolean isInTouchMode() {
	        if (mAttachInfo != null) {
	            return mAttachInfo.mInTouchMode;
	        } else {
	            return ViewRootImpl.isInTouchMode();
	        }
	    }


####1.1.5 View.hasAncestorThatBlocksDescendantFocus() View 树父节点是否屏蔽子节点焦点

    private boolean hasAncestorThatBlocksDescendantFocus() {
        final boolean focusableInTouchMode = isFocusableInTouchMode();
        ViewParent ancestor = mParent;
		
		// 遍历 view 树，父节点是否阻拦焦点
        while (ancestor instanceof ViewGroup) {
            final ViewGroup vgAncestor = (ViewGroup) ancestor;
            if (vgAncestor.getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS
                    || (!focusableInTouchMode && vgAncestor.shouldBlockFocusForTouchscreen())) {
                return true;
            } else {
                ancestor = vgAncestor.getParent();
            }
        }
        return false;
    }

核心逻辑是 `ViewGroup.getDescendantFocusability()` 

- ViewGroup.FOCUS_BEFORE_DESCENDANTS：ViewGroup会优先其子view而获取到焦点
- ViewGroup.FOCUS_AFTER_DESCENDANTS：ViewGroup只有当其子view不需要获取焦点时才获取焦点
- ViewGroup.FOCUS_BLOCK_DESCENDANTS：ViewGroup会覆盖子view而直接获得焦点

####1.1.6 View.handleFocusGainInternal(int direction, Rect previouslyFocusedRect) 处理并获取焦点

	void handleFocusGainInternal(@FocusRealDirection int direction, Rect previouslyFocusedRect) {
		// 如果当前没有获得焦点，才做处理
        if ((mPrivateFlags & PFLAG_FOCUSED) == 0) {
			
			// 设置focus标志
            mPrivateFlags |= PFLAG_FOCUSED;

			// 获取焦点 View 和 ViewGroup 有所区别稍微注意一下，在第三节中分析
            View oldFocus = (mAttachInfo != null) ? getRootView().findFocus() : null;

            if (mParent != null) {
				// 1.1.7
                mParent.requestChildFocus(this, this);
                updateFocusedInCluster(oldFocus, direction);
            }

            if (mAttachInfo != null) {
                mAttachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus, this);
            }

            onFocusChanged(true, direction, previouslyFocusedRect);
            refreshDrawableState();
        }
    }

	// 获取 view 树的根节点
	public View getRootView() {
        if (mAttachInfo != null) {
            final View v = mAttachInfo.mRootView;
            if (v != null) {
                return v;
            }
        }

        View parent = this;

        while (parent.mParent != null && parent.mParent instanceof View) {
            parent = (View) parent.mParent;
        }

        return parent;
    }

该方法在当前 view 原本没有焦点时才会处理，主要流程如下：

1. 设置PFLAG_FOCUSED标志位；
1. 获取旧的焦点；
1. 调用 mParent.requestChildFocus()获取焦点；
1. 通知焦点改变；

####1.1.7 ViewGroup.requestChildFocus(View child, View focused) 处理并获取焦点

	public void requestChildFocus(View child, View focused) {
		// 阻拦子view 焦点
        if (getDescendantFocusability() == FOCUS_BLOCK_DESCENDANTS) {
            return;
        }

        // 清除焦点
        super.unFocus(focused);

        // 保存新的焦点
        if (mFocused != child) {
            if (mFocused != null) {
                mFocused.unFocus(focused);
            }

            mFocused = child;
        }

		// 遍历view树调用 requestChildFocus()
        if (mParent != null) {
            mParent.requestChildFocus(this, focused);
        }
    }

到这整个获取焦点的流程就结束了

###1.2 ViewGroup 请求焦点流程细节

####1.2.1 ViewGroup.requestFocus(int direction, Rect previouslyFocusedRect)

	public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        int descendantFocusability = getDescendantFocusability();

        boolean result;
        switch (descendantFocusability) {
			// 阻拦子view焦点，自己直接请求焦点
            case FOCUS_BLOCK_DESCENDANTS:
                result = super.requestFocus(direction, previouslyFocusedRect);
                break;
			
			// 优先于子view获得焦点，自己先获取焦点，获取失败则让子view获取
            case FOCUS_BEFORE_DESCENDANTS: {
                final boolean took = super.requestFocus(direction, previouslyFocusedRect);
                result = took ? took : onRequestFocusInDescendants(direction,
                        previouslyFocusedRect);
                break;
            }

			// 子view优先获得焦点，子view获取失败，自己再尝试获取焦点。
            case FOCUS_AFTER_DESCENDANTS: {
                final boolean took = onRequestFocusInDescendants(direction, previouslyFocusedRect);
                result = took ? took : super.requestFocus(direction, previouslyFocusedRect);
                break;
            }
            default:
                throw new IllegalStateException("descendant focusability must be "
                        + "one of FOCUS_BEFORE_DESCENDANTS, FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS "
                        + "but is " + descendantFocusability);
        }
        if (result && !isLayoutValid() && ((mPrivateFlags & PFLAG_WANTS_FOCUS) == 0)) {
            mPrivateFlags |= PFLAG_WANTS_FOCUS;
        }
        return result;
    }

- ViewGroup.FOCUS_BEFORE_DESCENDANTS：ViewGroup会优先其子view而获取到焦点
- ViewGroup.FOCUS_AFTER_DESCENDANTS：ViewGroup只有当其子view不需要获取焦点时才获取焦点
- ViewGroup.FOCUS_BLOCK_DESCENDANTS：ViewGroup会覆盖子view而直接获得焦点

####1.2.2 ViewGroup.onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect)

之前1.1.1 中提到的 direction 只用在该方法中才会用到。

    protected boolean onRequestFocusInDescendants(int direction,
            Rect previouslyFocusedRect) {
        int index;
        int increment;
        int end;
        int count = mChildrenCount;
        if ((direction & FOCUS_FORWARD) != 0) {
            index = 0;
            increment = 1;
            end = count;
        } else {
            index = count - 1;
            increment = -1;
            end = -1;
        }
        final View[] children = mChildren;
        for (int i = index; i != end; i += increment) {
            View child = children[i];
            if ((child.mViewFlags & VISIBILITY_MASK) == VISIBLE) {
                if (child.requestFocus(direction, previouslyFocusedRect)) {
                    return true;
                }
            }
        }
        return false;
    }

这个方法主用于让子view尝试获取焦点。

####1.2.3 ViewGroup.findFocus()
    public View findFocus() {
        if (isFocused()) {
            return this;
        }

        if (mFocused != null) {
            return mFocused.findFocus();
        }
        return null;
    }
##二、清除焦点 

###2.1 clearFocus()

ViewGroup.clearFocus()，view树向下遍历调用 clearFocus()， 最终 mFocused == null 然后调用 `View.clearFocus()`

    public void clearFocus() {
        if (mFocused == null) {
            super.clearFocus();
        } else {
            View focused = mFocused;
			// 置空
            mFocused = null;
            focused.clearFocus();
        }
    }

View.clearFocus()

    public void clearFocus() {
        final boolean refocus = sAlwaysAssignFocus || !isInTouchMode();
        clearFocusInternal(null, true, refocus);
    }
可以看到 `ViewGroup.clearFocus()` 最终调用的是 `View.clearFocus()`

###2.2 unFocus(View focused)

ViewGroup.unFocus(View focused)，view树向下遍历调用 unFocus(View focused)， 最终 mFocused == null 然后调用 `View.clearFocus()`

    void unFocus(View focused) {
        if (mFocused == null) {
            super.unFocus(focused);
        } else {
            mFocused.unFocus(focused);
			// 置空
            mFocused = null;
        }
    }

View.unFocus(View focused)

    void unFocus(View focused) {
        clearFocusInternal(focused, false, false);
    }

可以看到 `ViewGroup.unFocus(View focused)` 最终调用的是 `View.unFocus(View focused)`。

###2.3 clearFocusInternal(View focused, boolean propagate, boolean refocus)
	
2.1和2.2可以看到，清除焦点都会走到该方法。

只有由 unFocus(View focused) 调用的使用时 propagate = false，其他情况都为  propagate = true

    void clearFocusInternal(View focused, boolean propagate, boolean refocus) {
        if ((mPrivateFlags & PFLAG_FOCUSED) != 0) {

			// 清除自身焦点标记PFLAG_FOCUSED
            mPrivateFlags &= ~PFLAG_FOCUSED;

			// 清除parent中的标记PFLAG_WANTS_FOCUS
            clearParentsWantFocus();

			// 向上遍历，将view树的 mFocused 置空.
            if (propagate && mParent != null) {
                mParent.clearChildFocus(this);
            }

			// 下面是通知焦点变化的代码
            onFocusChanged(false, 0, null);
            refreshDrawableState();

            if (propagate && (!refocus || !rootViewRequestFocus())) {
                notifyGlobalFocusCleared(this);
            }
        }
    }

###2.4 ViewGroup.clearChildFocus(View child)

向上遍历，将view树的 mFocused 置空.

    public void clearChildFocus(View child) {
        mFocused = null;
        if (mParent != null) {
            mParent.clearChildFocus(this);
        }
    }


##三、查找焦点

###3.1 View.focusSearch(int direction)

    public View focusSearch(@FocusRealDirection int direction) {
        if (mParent != null) {
            return mParent.focusSearch(this, direction);
        } else {
            return null;
        }
    }

###3.2 ViewGroup.focusSearch(View focused, int direction)

    public View focusSearch(View focused, int direction) {
        if (isRootNamespace()) {
			// 3.2.1			
            return FocusFinder.getInstance().findNextFocus(this, focused, direction);
        } else if (mParent != null) {
            return mParent.focusSearch(focused, direction);
        }
        return null;
    }

###3.2.1 FocusFinder.findNextFocus(ViewGroup root, View focused, int direction)

	public final View findNextFocus(ViewGroup root, View focused, int direction) {
        return findNextFocus(root, focused, null, direction);
    }


###3.2.2 FocusFinder.findNextFocus(ViewGroup root, View focused, Rect focusedRect, int direction)

	private View findNextFocus(ViewGroup root, View focused, Rect focusedRect, int direction) {
        View next = null;
		// 3.2.3 返回一个有效的根view
        ViewGroup effectiveRoot = getEffectiveRoot(root, focused);

        if (focused != null) {
			// 3.2.4
            next = findNextUserSpecifiedFocus(effectiveRoot, focused, direction);
        }
        if (next != null) {
            return next;
        }
        ArrayList<View> focusables = mTempList;
        try {
            focusables.clear();
            effectiveRoot.addFocusables(focusables, direction);
            if (!focusables.isEmpty()) {
                next = findNextFocus(effectiveRoot, focused, focusedRect, direction, focusables);
            }
        } finally {
            focusables.clear();
        }
        return next;
    }

###3.2.3 FocusFinder.getEffectiveRoot(ViewGroup root, View focused) 还没搞懂先留着

    private ViewGroup getEffectiveRoot(ViewGroup root, View focused) {
        if (focused == null || focused == root) {
            return root;
        }
        ViewGroup effective = null;
        ViewParent nextParent = focused.getParent();
        do {
            if (nextParent == root) {
                return effective != null ? effective : root;
            }
            ViewGroup vg = (ViewGroup) nextParent;
            if (vg.getTouchscreenBlocksFocus()
                    && focused.getContext().getPackageManager().hasSystemFeature(
                            PackageManager.FEATURE_TOUCHSCREEN)
                    && vg.isKeyboardNavigationCluster()) {
                // Don't stop and return here because the cluster could be nested and we only
                // care about the top-most one.
                effective = vg;
            }
            nextParent = nextParent.getParent();
        } while (nextParent instanceof ViewGroup);
        return root;
    }

###3.2.4 FocusFinder.findNextUserSpecifiedFocus(ViewGroup root, View focused, int direction) 

查找用户手动指定（方向）的焦点view

    private View findNextUserSpecifiedFocus(ViewGroup root, View focused, int direction) {
        View userSetNextFocus = focused.findUserSetNextFocus(root, direction);
        View cycleCheck = userSetNextFocus;
        boolean cycleStep = true; // we want the first toggle to yield false
        while (userSetNextFocus != null) {
            if (userSetNextFocus.isFocusable()
                    && userSetNextFocus.getVisibility() == View.VISIBLE
                    && (!userSetNextFocus.isInTouchMode()
                            || userSetNextFocus.isFocusableInTouchMode())) {
                return userSetNextFocus;
            }
            userSetNextFocus = userSetNextFocus.findUserSetNextFocus(root, direction);
            if (cycleStep = !cycleStep) {
                cycleCheck = cycleCheck.findUserSetNextFocus(root, direction);
                if (cycleCheck == userSetNextFocus) {
                    // found a cycle, user-specified focus forms a loop and none of the views
                    // are currently focusable.
                    break;
                }
            }
        }
        return null;
    }