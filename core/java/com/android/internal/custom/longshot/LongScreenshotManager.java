package com.android.internal.custom.longshot;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

public final class LongScreenshotManager {
    public static final String NAVIGATIONBAR_VISIBLE = "navigationbar_visible";
    public static final String STATUSBAR_VISIBLE = "statusbar_visible";
    private static final String TAG = "Longshot.Manager";
    private static LongScreenshotManager sInstance = null;
    private final ILongScreenshotManager mService = ILongScreenshotManager.Stub.asInterface(ServiceManager.getService(Context.LONGSCREENSHOT_SERVICE));

    private LongScreenshotManager() {
    }

    public static LongScreenshotManager getInstance() {
        LongScreenshotManager longScreenshotManager;
        synchronized (LongScreenshotManager.class) {
            if (sInstance == null || sInstance.mService == null) {
                sInstance = new LongScreenshotManager();
            }
            longScreenshotManager = sInstance;
        }
        return longScreenshotManager;
    }

    public static LongScreenshotManager peekInstance() {
        return sInstance;
    }

    @Override
    public void takeLongshot(boolean statusBarVisible, boolean navBarVisible) {
        try {
            if (mService != null) {
                mService.takeLongshot(statusBarVisible, navBarVisible);
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in takeLongshot: ", e);
        }
    }

    @Override
    public void registerLongshotListener(ILongScreenshotListener listener) {
        try {
            if (mService != null) {
                mService.registerLongshotListener(listener);
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in registerLongshotListener: ", e);
        }
    }

    @Override
    public void unregisterLongshotListener(ILongScreenshotListener listener) {
        try {
            if (mService != null) {
                mService.unregisterLongshotListener(listener);
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in unregisterLongshotListener: ", e);
        }
    }

    @Override
    public void notifyLongshotScroll(boolean isOverScroll) {
        try {
            if (mService != null) {
                mService.notifyLongshotScroll(isOverScroll);
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in notifyLongshotScroll: ", e);
        }
    }

    @Override
    public boolean isLongshotMoveState() {
        try {
            if (mService != null) {
                return mService.isLongshotMoveState();
            }
            return false;
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in isLongshotMoveState: ", e);
        }
    }

    @Override
    public boolean isLongshotHandleState() {
        try {
            if (mService != null) {
                return mService.isLongshotHandleState();
            }
            return false;
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in isLongshotHandleState: ", e);
        }
    }

    @Override
    public boolean isLongshotMode() {
        try {
            if (mService != null) {
                return mService.isLongshotMode();
            }
            return false;
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in isLongshotMode: ", e);
        }
    }

    @Override
    public void notifyScrollViewTop(int viewTop) {
        try {
            if (mService != null) {
                mService.notifyScrollViewTop(viewTop);
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in notifyScrollViewTop: ", e);
        }
    }

    @Override
    public void onUnscrollableView() {
        try {
            if (mService != null) {
                mService.onUnscrollableView();
            }
        } catch (RemoteException e) {
            Log.w(TAG, "Remote exception in onUnscrollableView: ", e);
        }
    }
}
