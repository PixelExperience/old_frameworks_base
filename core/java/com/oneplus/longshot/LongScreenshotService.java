package com.oneplus.longshot;

import android.content.Context;
import com.oneplus.longshot.ILongScreenshot.Stub;

public abstract class LongScreenshotService extends Stub {
    private static final String TAG = "Longshot.Service";
    protected Context mContext = null;
    protected boolean mNavBarVisible = false;
    protected boolean mStatusBarVisible = false;

    public LongScreenshotService(Context context, boolean statusBarVisible, boolean navBarVisible) {
        this.mContext = context;
        this.mStatusBarVisible = statusBarVisible;
        this.mNavBarVisible = navBarVisible;
    }

    public void start(ILongScreenshotCallback callback) {
    }

    public void notifyScroll(boolean isOverScroll) {
    }

    public boolean isMoveState() {
        return false;
    }

    public boolean isHandleState() {
        return false;
    }

    public void stopLongshot() {
    }
}
