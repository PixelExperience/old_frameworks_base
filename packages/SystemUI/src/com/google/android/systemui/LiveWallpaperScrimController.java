package com.google.android.systemui;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IWallpaperManager;
import android.app.IWallpaperManager.Stub;
import android.app.WallpaperInfo;
import android.content.ComponentName;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArraySet;

import com.android.internal.colorextraction.ColorExtractor.GradientColors;
import com.android.internal.util.function.TriConsumer;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ScrimState;
import com.google.android.collect.Sets;

import java.util.function.Consumer;

public class LiveWallpaperScrimController extends ScrimController {
    private static ArraySet<ComponentName> REDUCED_SCRIM_WALLPAPERS = Sets.newArraySet(new ComponentName[]{
        new ComponentName("com.breel.geswallpapers", "com.breel.geswallpapers.wallpapers.EarthWallpaperService"), 
        new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV1"), 
        new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV2"), 
        new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.delight.wallpapers.DelightWallpaperV3"), 
        new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.surfandturf.wallpapers.variations.SurfAndTurfWallpaperV2"), 
        new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.cities.wallpapers.variations.SanFranciscoWallpaper"), 
        new ComponentName("com.breel.wallpapers18", "com.breel.wallpapers18.cities.wallpapers.variations.NewYorkWallpaper")});
    private int mCurrentUser = ActivityManager.getCurrentUser();
    private final LockscreenWallpaper mLockscreenWallpaper;
    private final IWallpaperManager mWallpaperManager = Stub.asInterface(ServiceManager.getService("wallpaper"));

    public LiveWallpaperScrimController(ScrimView scrimView, ScrimView scrimView2, LockscreenWallpaper lockscreenWallpaper, TriConsumer<ScrimState, Float, GradientColors> triConsumer, Consumer<Integer> consumer, DozeParameters dozeParameters, AlarmManager alarmManager) {
        super(scrimView, scrimView2, triConsumer, consumer, dozeParameters, alarmManager);
        this.mLockscreenWallpaper = lockscreenWallpaper;
    }

    private boolean isReducedScrimWallpaperSet() {
        try {
            WallpaperInfo wallpaperInfo = this.mWallpaperManager.getWallpaperInfo(this.mCurrentUser);
            return wallpaperInfo != null && REDUCED_SCRIM_WALLPAPERS.contains(wallpaperInfo.getComponent()) && this.mLockscreenWallpaper.getBitmap() == null;
        } catch (RemoteException e) {
            return false;
        }
    }

    private void updateScrimValues() {
        if (isReducedScrimWallpaperSet()) {
            setScrimBehindValues(0.25f);
        } else {
            setScrimBehindValues(0.45f);
        }
    }

    public void setCurrentUser(int i) {
        this.mCurrentUser = i;
        updateScrimValues();
    }

    public void transitionTo(ScrimState scrimState) {
        if (scrimState == ScrimState.KEYGUARD) {
            updateScrimValues();
        }
        super.transitionTo(scrimState);
    }
}