package com.oneplus.longshot;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.view.WindowManagerGlobal;
import com.oneplus.longshot.ILongScreenshotManager.Stub;
import java.util.ArrayList;
import java.util.List;

public class LongScreenshotManagerService extends Stub {
    private static final ComponentName COMPONENT_LONGSHOT = new ComponentName("com.oneplus.screenshot", "com.oneplus.screenshot.LongshotService");
    private static final String TAG = "Longshot.ManagerService";
    private static LongScreenshotManagerService sInstance = null;
    /* access modifiers changed from: private */
    public Context mContext = null;
    private LongshotConnection mLongshot = new LongshotConnection();

    private class LongshotConnection extends ILongScreenshotCallback.Stub implements ServiceConnection {
        private List<ILongScreenshotListener> mListeners;
        /* access modifiers changed from: private */
        public ILongScreenshot mService;

        private LongshotConnection() {
            this.mService = null;
            this.mListeners = new ArrayList();
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            String str = LongScreenshotManagerService.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onServiceConnected : ");
            sb.append(name);
            Slog.d(str, sb.toString());
            this.mService = ILongScreenshot.Stub.asInterface(service);
            try {
                this.mService.start(this);
            } catch (NullPointerException e) {
            } catch (RemoteException e2) {
                Slog.e(LongScreenshotManagerService.TAG, e2.toString());
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Slog.d(LongScreenshotManagerService.TAG, "onServiceDisconnected");
            stop();
        }

        public void stop() {
            Slog.d(LongScreenshotManagerService.TAG, "stop");
            LongScreenshotManagerService.this.mContext.unbindService(this);
            this.mService = null;
            try {
                WindowManagerGlobal.getWindowManagerService().stopLongshotConnection();
            } catch (RemoteException e) {
                Slog.e(LongScreenshotManagerService.TAG, e.toString());
            }
        }

        public void notifyMove() {
            Slog.d(LongScreenshotManagerService.TAG, "notifyMove");
            synchronized (this.mListeners) {
                for (ILongScreenshotListener listener : this.mListeners) {
                    try {
                        listener.onMove();
                    } catch (RemoteException e) {
                        Slog.e(LongScreenshotManagerService.TAG, e.toString());
                    }
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void registerListener(ILongScreenshotListener listener) {
            synchronized (this.mListeners) {
                this.mListeners.add(listener);
            }
        }

        /* access modifiers changed from: 0000 */
        public void unregisterListener(ILongScreenshotListener listener) {
            synchronized (this.mListeners) {
                this.mListeners.remove(listener);
            }
        }
    }

    private LongScreenshotManagerService(Context context) {
        this.mContext = context;
    }

    public static LongScreenshotManagerService getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new LongScreenshotManagerService(context);
        }
        return sInstance;
    }

    public void takeLongshot(boolean statusBarVisible, boolean navBarVisible) {
        stopLongshot();
        boolean result = bindService(createLongshotIntent(statusBarVisible, navBarVisible), this.mLongshot, 1);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("start : bindService=");
        sb.append(result);
        sb.append(", ");
        sb.append(UserHandle.CURRENT);
        Slog.i(str, sb.toString());
    }

    public void registerLongshotListener(ILongScreenshotListener listener) {
        this.mLongshot.registerListener(listener);
    }

    public void unregisterLongshotListener(ILongScreenshotListener listener) {
        this.mLongshot.unregisterListener(listener);
    }

    public void notifyLongshotScroll(boolean isOverScroll) {
        try {
            this.mLongshot.mService.notifyScroll(isOverScroll);
        } catch (NullPointerException e) {
        } catch (RemoteException e2) {
            Slog.e(TAG, e2.toString());
        }
    }

    public boolean isLongshotMoveState() {
        try {
            return this.mLongshot.mService.isMoveState();
        } catch (NullPointerException e) {
            return false;
        } catch (RemoteException e2) {
            Slog.e(TAG, e2.toString());
            return false;
        }
    }

    public boolean isLongshotHandleState() {
        try {
            return this.mLongshot.mService.isHandleState();
        } catch (NullPointerException e) {
            return false;
        } catch (RemoteException e2) {
            Slog.e(TAG, e2.toString());
            return false;
        }
    }

    public void notifyScrollViewTop(int viewTop) {
        try {
            this.mLongshot.mService.notifyScrollViewTop(viewTop);
        } catch (NullPointerException e) {
        } catch (RemoteException e2) {
            Slog.e(TAG, e2.toString());
        }
    }

    public void onUnscrollableView() {
        try {
            Slog.d(TAG, "onUnscrollableView");
            this.mLongshot.mService.onUnscrollableView();
        } catch (NullPointerException e) {
        } catch (RemoteException e2) {
            Slog.e(TAG, e2.toString());
        }
    }

    public boolean isLongshotMode() {
        return this.mLongshot.mService != null;
    }

    public void stopLongshot() {
        if (this.mLongshot.mService != null) {
            Slog.d(TAG, "stopLongshot");
            try {
                this.mLongshot.mService.stopLongshot();
            } catch (RemoteException e) {
                Slog.e(TAG, e.toString());
            }
        }
    }

    private Intent createIntent(ComponentName component) {
        return new Intent().setComponent(component);
    }

    private Intent createLongshotIntent(boolean statusBarVisible, boolean navBarVisible) {
        return createIntent(COMPONENT_LONGSHOT).putExtra(LongScreenshotManager.STATUSBAR_VISIBLE, statusBarVisible).putExtra(LongScreenshotManager.NAVIGATIONBAR_VISIBLE, navBarVisible);
    }

    private boolean bindService(Intent service, ServiceConnection conn, int flags) {
        if (service != null && conn != null) {
            return this.mContext.bindServiceAsUser(service, conn, flags, UserHandle.CURRENT);
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("--- bind failed: service = ");
        sb.append(service);
        sb.append(", conn = ");
        sb.append(conn);
        Slog.e(str, sb.toString());
        return false;
    }
}
