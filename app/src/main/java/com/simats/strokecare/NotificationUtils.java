// NotificationUtils.java
package com.simats.strokecare;

public class NotificationUtils {

    private static final String TAG = "NotificationUtils";

    public static long calculateTriggerTime(int hour, int minute) {
        // Here you need to implement your logic to calculate the trigger time based on the provided hour and minute
        // For simplicity, you can use a fixed time in milliseconds for testing
        // Replace this with your actual logic to calculate the trigger time
        long currentTimeMillis = System.currentTimeMillis();
        long currentHourMillis = hour * 60 * 60 * 1000;
        long currentMinuteMillis = minute * 60 * 1000;
        long triggerTime = currentTimeMillis - (currentTimeMillis % (24 * 60 * 60 * 1000)) + currentHourMillis + currentMinuteMillis;
        if (triggerTime <= currentTimeMillis) {
            triggerTime += 24 * 60 * 60 * 1000; // Add 24 hours if the calculated trigger time is in the past
        }
        return triggerTime;
    }
}
