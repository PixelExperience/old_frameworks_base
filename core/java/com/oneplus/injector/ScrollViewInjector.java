package com.oneplus.injector;

import android.content.Context;
import com.oneplus.longshot.LongScreenshotManager;

public class ScrollViewInjector {

    public static class ScrollView {
        private static final String TAG = "ScrollViewInjector";
        public static boolean isInjection = false;

        public static void onOverScrolled(Context context, boolean isOverScroll) {
            if (isInjection) {
                LongScreenshotManager sm = (LongScreenshotManager) context.getSystemService(Context.LONGSCREENSHOT_SERVICE);
                if (sm != null && sm.isLongshotMoveState()) {
                    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                    sm.notifyLongshotScroll(isOverScroll);
                }
            }
        }
    }
}
