package com.five.conquest.Chat;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Terry Antony on 12/5/2016.
 */

public class SizeLayout extends RelativeLayout {

    private Rect rect = new Rect();
    public SizeRelativeLayoutDelegate delegate;

    public interface SizeRelativeLayoutDelegate {
        void onSizeChanged(int keyboardHeight);
    }

    public SizeLayout(Context context) {
        super(context);
    }

    public SizeLayout(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeLayout(android.content.Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (delegate != null) {
            View rootView = this.getRootView();
            int usableViewHeight = rootView.getHeight();
            this.getWindowVisibleDisplayFrame(rect);
            int keyboardHeight = usableViewHeight - (rect.bottom - rect.top);
            delegate.onSizeChanged(keyboardHeight);
        }
    }
}
