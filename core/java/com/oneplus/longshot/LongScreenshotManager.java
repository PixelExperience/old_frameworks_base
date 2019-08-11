package com.oneplus.longshot;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.oneplus.longshot.ILongScreenshotManager.Stub;

public final class LongScreenshotManager {
    public static final String NAVIGATIONBAR_VISIBLE = "navigationbar_visible";
    public static final String STATUSBAR_VISIBLE = "statusbar_visible";
    private static final String TAG = "Longshot.Manager";
    private static LongScreenshotManager sInstance = null;
    private final ILongScreenshotManager mService = Stub.asInterface(ServiceManager.getService(Context.LONGSCREENSHOT_SERVICE));

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

    public void takeLongshot(boolean statusBarVisible, boolean navBarVisible) {
        try {
            if (this.mService != null) {
                this.mService.takeLongshot(statusBarVisible, navBarVisible);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerLongshotListener(ILongScreenshotListener listener) {
        try {
            if (this.mService != null) {
                this.mService.registerLongshotListener(listener);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregisterLongshotListener(ILongScreenshotListener listener) {
        try {
            if (this.mService != null) {
                this.mService.unregisterLongshotListener(listener);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyLongshotScroll(boolean isOverScroll) {
        try {
            if (this.mService != null) {
                this.mService.notifyLongshotScroll(isOverScroll);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLongshotMoveState() {
        try {
            if (this.mService != null) {
                return this.mService.isLongshotMoveState();
            }
            return false;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLongshotHandleState() {
        try {
            if (this.mService != null) {
                return this.mService.isLongshotHandleState();
            }
            return false;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLongshotMode() {
        try {
            if (this.mService != null) {
                return this.mService.isLongshotMode();
            }
            return false;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void notifyScrollViewTop(int viewTop) {
        try {
            if (this.mService != null) {
                this.mService.notifyScrollViewTop(viewTop);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void onUnscrollableView() {
        try {
            if (this.mService != null) {
                this.mService.onUnscrollableView();
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
