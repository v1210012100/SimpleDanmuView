package com.example.diva.simpledanmuview.danmu;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Utils {
    private static DisplayMetrics mMetrics;

    public static void init(Resources res) {
        mMetrics = res.getDisplayMetrics();
    }

    public static float convertDpToPixel(float dp) {

        if (mMetrics == null) {
            return dp;
        }

        return dp * mMetrics.density;
    }
}
