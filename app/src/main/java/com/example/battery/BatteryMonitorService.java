package com.example.battery;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BatteryMonitorService extends Service {
    private static final String CHANNEL_ID = "battery_channel";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isCharging = intent.getBooleanExtra("isCharging", false);
        int batteryPct = intent.getIntExtra("batteryPct", -1);

        createNotificationChannel();
        createNotification(isCharging, batteryPct);

        // Если уровень заряда <= 15%, открыть Activity с предупреждением
        if (!isCharging && batteryPct <= 15) {
            Intent warningIntent = new Intent(this, LowBatteryActivity.class);
            warningIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(warningIntent);
        }

        return START_NOT_STICKY;
    }

    private void createNotification(boolean isCharging, int batteryPct) {
        String message;
        if (isCharging) {
            if (batteryPct == 100) {
                message = "Полностью заряжена - 100%";
            } else {
                message = "Питание подключено - " + batteryPct + "%";
            }
        } else {
            if (batteryPct > 15) {
                message = "Уровень заряда - " + batteryPct + "%";
            } else {
                message = "Почти разряжена - " + batteryPct + "%";
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_battery)
                .setContentTitle("Статус батареи")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Battery Channel";
            String description = "Channel for battery notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}