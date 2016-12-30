/*
 * Copyright (C) 2014-2016 AnAurelian. All rights reserved.
 * https://anaurelian.com
 */
package com.tecdrop.milliontaps;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Assorted utility methods.
 */

class Utils {

    /**
     * Set the alpha component to full and return the color.
     */
    static int fullAlpha(int color) {
        return 0xFF000000 | color;
    }

    /**
     * Converts an integer color value to a hexadecimal string.
     */
    static String colorToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    /**
     * Copies a text to the clipboard.
     */
    @SuppressWarnings("deprecation")
    static void copyText(Context context, CharSequence label, CharSequence text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            final android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            final android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            final android.content.ClipData clip = android.content.ClipData.newPlainText(label, text);
            clipboard.setPrimaryClip(clip);
        }
    }

    /**
     * Starts an activity to view an url.
     */
    static void viewUrl(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, context.getString(R.string.toast_bad_url, url), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Toggle between full brightness policy and normal brightness policy.
     *
     * @param window     the window whose brightness policy to set.
     * @param fullBright True to set full brightness, false otherwise.
     */
    static void setFullBrightness(Window window, boolean fullBright) {
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = fullBright ? WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL :
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        window.setAttributes(lp);
    }

    /**
     * Always show the overflow menu even if the phone has a menu button.
     * <p>
     * http://blog.vogella.com/2013/08/06/android-always-show-the-overflow-menu-even-if-the-phone-as-a-menu/
     */
    static void setOverflowMenuAlwaysOn(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }
}
