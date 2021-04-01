package com.fuwafuwa.theme;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.fuwafuwa.utils.SPBase;
import com.fuwafuwa.utils.SPKey;

import java.util.List;

public class ThemeIconConf {
    public static final String[] colors = {"#FDDE4A", "#00CCFF",
            "#1E90FF", "#26A65B", "#2C3E50", "#84AF9B",
            "#AEDD81", "#D0D0D0", "#43CD80", "#C7EDCC",
            "#D24D57", "#8A2BE2", "#DCE2F1", "#E3EDCD",
            "#E9EBFE", "#EAEAEF", "#EB7347", "#FAF9DE",
            "#00CDCD", "#FC9D99", "#5D478B", "#FDE6E0",
            "#C0EBD7", "#CCA4E3", "#B0A4E3",
            "#19CAAD", "#8CC7B5", "#A0EEE1", "#BEE7E9", "#BEEDC7",
            "#F4606C", "#E6CEAC", "#D6D5B7", "#D1BA74", "#ECAD9E",
            "#6E7B8B", "#FFF2E2", "#000000", "#FFFFFF"};
    public static int ttColor = Color.BLACK;

    /**
     * same as {@code ColorUtils.calculateLuminance()} >=0.5 LUX ↑
     *
     * @param color
     * @return
     */
    public static boolean isLight(int color) {
        int r = (color & 0x0000000000ff0000) >> 4 * 4;
        int g = (color & 0x000000000000ff00) >> 4 * 2;
        int b = (color & 0x00000000000000ff);
        double y = r * 0.299 + g * 0.578 + b * 0.114;
        return y >= 192;
    }

    private static List<Integer> darkColors = null;

    public static List<Integer> getDarkColors() {
        if (darkColors != null) return darkColors;
        darkColors = Stream.of(ThemeIconConf.colors).map(Color::parseColor)
                .filter(item -> !isLight(item))
                .collect(Collectors.toList());
        return darkColors;
    }

    public static boolean isDarkMode(@NonNull Context context) {
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static enum Mode {
        NOT_SET,
        DEFAULT,
        LIGHT,
        DARK
    }

    public static Mode mode = Mode.LIGHT;

    public static void change(Context context, Mode mode) {
        if (mode == null) mode = Mode.NOT_SET;
        if (mode == Mode.NOT_SET) {
            if (ThemeIconConf.isDarkMode(context)) {
                ThemeIconConf.mode = Mode.DARK;
            } else {
                ThemeIconConf.mode = Mode.DEFAULT;
            }
        } else {
            ThemeIconConf.mode = mode;
        }
    }


    public static int getBackgroundColor(@ColorInt int originColor) {
        switch (mode) {
            case LIGHT:
                return Color.WHITE;
            case DARK:
                return 0xFF1b1c1e;
        }
        return originColor;
    }

    public static int getDockBackgroundColor(@ColorInt int originColor) {
        switch (mode) {
            case LIGHT:
                return 0xFFFFFFFF;
            case DARK:
                return 0xFF000000;
        }
        return originColor;
    }
}
