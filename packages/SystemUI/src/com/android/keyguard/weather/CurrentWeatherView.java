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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settingslib.Utils;

public class CurrentWeatherView extends FrameLayout implements WeatherClient.WeatherObserver {

    static final String TAG = "SystemUI:CurrentWeatherView";
    static final boolean DEBUG = true;

    private ImageView mCurrentImage;
    private WeatherClient mWeatherClient;
    private TextView mRightText;
    private int mTextColor;
    private float mDarkAmount;

    public CurrentWeatherView(Context context) {
        this(context, null);
    }

    public CurrentWeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurrentWeatherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCurrentImage = findViewById(R.id.current_image);
        mRightText = findViewById(R.id.right_text);
        mTextColor = mRightText.getCurrentTextColor();
        if (WeatherClient.isAvailable(getContext())) {
            setVisibility(View.VISIBLE);
            mWeatherClient = new WeatherClient(getContext());
            mWeatherClient.addObserver(this);
            queryAndUpdateWeather(null);
        } else {
            setVisibility(View.GONE);
        }
    }

    private void updateWeatherData(WeatherClient.WeatherInfo weatherData) {
        if (DEBUG) Log.d(TAG, "updateWeatherData");

        if (!WeatherClient.isAvailable(getContext()) || weatherData == null) {
            setVisibility(View.GONE);
            return;
        }

        boolean useMetric = true; // TODO: Add entry on Settings > System > Language and input, to switch between metric and imperial
        int temperatureMetric = weatherData.getTemperature(true);
        int temperatureImperial = weatherData.getTemperature(false);
        String temperatureText = useMetric ? 
                                 Integer.toString(temperatureMetric) + " °C" : 
                                 Integer.toString(temperatureImperial) + " °F";

/*
        String conditions = weatherData.getConditions();
        Drawable d = getWeatherConditionImage(conditions);
        d = d.mutate();
        updateTint(d);
        mCurrentImage.setImageDrawable(d);*/

        mRightText.setText(temperatureText);
        setVisibility(View.VISIBLE);
    }

    private int getTintColor() {
        return Utils.getColorAttr(mContext, R.attr.wallpaperTextColor);
    }

    @Override
    public void onWeatherUpdated(WeatherClient.WeatherInfo weatherData) {
        if (DEBUG) Log.d(TAG, "weatherUpdated");
        queryAndUpdateWeather(weatherData);
    }

    private void queryAndUpdateWeather(WeatherClient.WeatherInfo weatherData) {
        if (mWeatherClient != null) {
            if (DEBUG) Log.d(TAG, "queryAndUpdateWeather");
            if (weatherData == null) {
                weatherData = mWeatherClient.getWeatherData();
            }
            updateWeatherData(weatherData);
        }
    }

    public void blendARGB(float darkAmount) {
        mDarkAmount = darkAmount;
        mRightText.setTextColor(ColorUtils.blendARGB(mTextColor, Color.WHITE, darkAmount));

        if (mWeatherClient != null) {
            // update image with correct tint
            updateWeatherData(mWeatherClient.getWeatherData());
        }
    }

    private void updateTint(Drawable d) {
        if (mDarkAmount == 1) {
            mCurrentImage.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        } else {
            mCurrentImage.setImageTintList((d instanceof VectorDrawable) ? ColorStateList.valueOf(getTintColor()) : null);
        }
    }

    public void onDensityOrFontScaleChanged() {
        mRightText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.widget_label_font_size));
        mCurrentImage.getLayoutParams().height =
                getResources().getDimensionPixelSize(R.dimen.current_weather_image_size);
        mCurrentImage.getLayoutParams().width =
                getResources().getDimensionPixelSize(R.dimen.current_weather_image_size);
    }
}