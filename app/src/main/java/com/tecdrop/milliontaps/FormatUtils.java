/*
 * Copyright (C) 2014-2016 AnAurelian. All rights reserved.
 * https://anaurelian.com
 */
package com.tecdrop.milliontaps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import java.util.concurrent.TimeUnit;

/**
 * Assorted utility methods related to text formatting integers and time values.
 */
final class FormatUtils {

    /**
     * Format a byte to binary and append a new line.
     */
    private static void appendBinaryByte(StringBuilder sb, int value, boolean newLine) {
        sb.append(String.format("%1$8s", Integer.toBinaryString(value)).replace(' ', '0'));
        if (newLine) {
            sb.append(System.getProperty("line.separator"));
        }
    }

    /**
     * Return a string representation of a counter integer value.
     *
     * @param context       A context for accessing string resources.
     * @param counter       The counter value.
     * @param numeralSystem The numeral system (radix).
     */
    static CharSequence formatCounter(Context context, int counter, String numeralSystem) {
        String counterString = "";
        switch (numeralSystem) {
            // Binary numeral system, on three lines
            case "2":
                final StringBuilder sb = new StringBuilder();
                appendBinaryByte(sb, Color.red(counter), true);
                appendBinaryByte(sb, Color.green(counter), true);
                appendBinaryByte(sb, Color.blue(counter), false);
                counterString = sb.toString();
                break;
            // Octal system
            case "8":
                counterString = Integer.toOctalString(counter);
                break;
            // Decimal system
            case "10":
                counterString = context.getString(R.string.format_comma_decimal, counter);
                break;
            // Hexadecimal system
            case "16":
                counterString = context.getString(R.string.format_hexadecimal, counter);
                break;
        }

        return counterString;
    }

    /**
     * Return a duration formatted string.
     * (Based on http://stackoverflow.com/a/9027379/220039)
     *
     * @param context A context for accessing string resources.
     * @param millis  The duration to format in milliseconds.
     */
    static CharSequence formatDuration(Context context, long millis) {
        final Resources res = context.getResources();
        final StringBuilder sb = new StringBuilder();

        // Add days
        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        if (days > 0L) {
            sb.append(res.getQuantityString(R.plurals.duration_days, (int) days, days));
        }

        // Add hours
        final long hours = TimeUnit.MILLISECONDS.toHours(millis) % TimeUnit.DAYS.toHours(1L);
        if (hours > 0L) {
            sb.append(res.getQuantityString(R.plurals.duration_hours, (int) hours, hours));
        }

        // Add minutes
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1L);
        if (minutes > 0L) {
            sb.append(res.getQuantityString(R.plurals.duration_minutes, (int) minutes, minutes));
        }

        // Add seconds
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1L);
        if (seconds > 0L) {
            sb.append(res.getQuantityString(R.plurals.duration_seconds, (int) seconds, seconds));
        }

        return sb.toString();
    }
}