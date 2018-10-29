/*
 * Copyright (C) 2018 The OmniROM Project
 *                    The PixelExperience Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.android.keyguard.weather;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WeatherClient {

    private static final String TAG = "SystemUI:WeatherClient";
    private static final boolean DEBUG = true;
    private static final String SERVICE_PACKAGE = "org.pixelexperience.weather.client";
    private static final Uri WEATHER_URI = Uri.parse("content://org.pixelexperience.weather.client.provider/weather");

    public static final int WEATHER_UPDATE_SUCCESS = 0; // Success
    public static final int WEATHER_UPDATE_RUNNING = 1; // Update running
    public static final int WEATHER_UPDATE_NO_DATA = 2; // On boot event
    public static final int WEATHER_UPDATE_ERROR = 3; // Error

    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CONDITIONS = "conditions";
    private static final String COLUMN_TEMPERATURE_METRIC = "temperatureMetric";
    private static final String COLUMN_TEMPERATURE_IMPERIAL = "temperatureImperial";
    private static final String[] PROJECTION_DEFAULT_WEATHER = new String[]{
            COLUMN_STATUS,
            COLUMN_CONDITIONS,
            COLUMN_TEMPERATURE_METRIC,
            COLUMN_TEMPERATURE_IMPERIAL
    };

    private Context mContext;
    private List<WeatherObserver> mObserver;

    public WeatherClient(Context context) {
        mContext = context;
        mObserver = new ArrayList<>();
        new WeatherContentObserver(new Handler()).observe();
    }

    public static boolean isAvailable(Context context) {
        final PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(SERVICE_PACKAGE, PackageManager.GET_ACTIVITIES);
            int enabled = pm.getApplicationEnabledSetting(SERVICE_PACKAGE);
            return enabled != PackageManager.COMPONENT_ENABLED_STATE_DISABLED &&
                    enabled != PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    WeatherInfo getWeatherData() {
        if (!isAvailable(mContext)) {
            return null;
        }
        WeatherInfo weatherInfo = new WeatherInfo();
        Cursor c = mContext.getContentResolver().query(WEATHER_URI, PROJECTION_DEFAULT_WEATHER,
                null, null, null);
        if (c != null) {
            try {
                int count = c.getCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        c.moveToPosition(i);
                        if (i == 0) {
                            weatherInfo.status = c.getInt(0);
                            weatherInfo.conditions = c.getString(1);
                            weatherInfo.temperatureMetric = c.getInt(2);
                            weatherInfo.temperatureImperial = c.getInt(3);
                        }
                    }
                }
            } finally {
                c.close();
            }
        }
        if (DEBUG) Log.d(TAG, weatherInfo.toString());
        return weatherInfo;
    }

    public void addObserver(WeatherObserver observer) {
        mObserver.add(observer);
    }

    public void removeObserver(WeatherObserver observer) {
        mObserver.remove(observer);
    }

    public interface WeatherObserver {
        void onWeatherUpdated(WeatherInfo info);
    }

    private class WeatherContentObserver extends ContentObserver {
        WeatherContentObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            mContext.getContentResolver().registerContentObserver(WEATHER_URI, false, this, UserHandle.USER_ALL);
        }

        @Override
        public void onChange(boolean selfChange) {
            WeatherInfo info = getWeatherData();
            for (WeatherObserver observer : mObserver) {
                observer.onWeatherUpdated(info);
            }
        }
    }

    public class WeatherInfo {

        int status = WEATHER_UPDATE_ERROR;
        String conditions = "";
        int temperatureMetric = 0;
        int temperatureImperial = 0;

        WeatherInfo() {
        }

        int getTemperature(boolean metric) {
            return metric ? this.temperatureMetric : this.temperatureImperial;
        }

        int getStatus() {
            return this.status;
        }

        String getConditions() {
            return this.conditions;
        }

        @NonNull
        @Override
        public String toString() {
            return "WeatherInfo: " +
                    "status=" + getStatus() + "," +
                    "conditions=" + getConditions() + "," +
                    "temperatureMetric=" + getTemperature(true) + "," +
                    "temperatureImperial=" + getTemperature(false);
        }
    }
}
