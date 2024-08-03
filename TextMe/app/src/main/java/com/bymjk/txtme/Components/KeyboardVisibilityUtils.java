package com.bymjk.txtme.Components;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class KeyboardVisibilityUtils {

    public static void setKeyboardVisibilityListener(Activity activity, final OnKeyboardVisibilityListener listener) {
        final View activityRootView = activity.findViewById(android.R.id.content);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasOpened;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = activityRootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                boolean isOpen = keypadHeight > screenHeight * 0.15;
                if (isOpen == wasOpened) {
                    return;
                }
                wasOpened = isOpen;
                listener.onVisibilityChanged(isOpen);
            }
        });
    }

    public interface OnKeyboardVisibilityListener {
        void onVisibilityChanged(boolean isOpen);
    }
}

