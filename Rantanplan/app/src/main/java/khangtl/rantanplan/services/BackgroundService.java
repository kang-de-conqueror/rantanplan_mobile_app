package khangtl.rantanplan.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;

import khangtl.rantanplan.R;
import khangtl.rantanplan.activities.MainActivity;
import khangtl.rantanplan.wifi.EmptyValueReceiver;
import khangtl.rantanplan.wifi.MaximumValueReceiver;
import khangtl.rantanplan.wifi.MinimumValueReceiver;
import khangtl.rantanplan.wifi.WifiScanReceiver;
import khangtl.rantanplan.wifi.WifiWaiting;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

public class BackgroundService extends Service implements SensorEventListener {
    private final float THRESHOLD = 12.0f;

    private boolean isStopScan = false;
    private boolean isHoldStation = false;
    // Seekbar value receiver
    private MinimumValueReceiver minimumValueReceiver;
    private MaximumValueReceiver maximumValueReceiver;
    private EmptyValueReceiver emptyValueReceiver;

    // Wifi variables
    private WifiManager wifiManager;
    private WifiWaiting wifiWaiting;
    private WifiScanReceiver wifiScanReceiver;
    private Timer timer;

    public boolean isStopScan() {
        return isStopScan;
    }

    public void setStopScan(boolean stopScan) {
        isStopScan = stopScan;
    }

    public WifiScanReceiver getWifiScanReceiver() {
        return wifiScanReceiver;
    }

    public void setWifiScanReceiver(WifiScanReceiver wifiScanReceiver) {
        this.wifiScanReceiver = wifiScanReceiver;
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public void scheduleTimer() {
        timer = new Timer();
        wifiScanReceiver = new WifiScanReceiver(this);
        wifiWaiting = new WifiWaiting(this);
        timer.schedule(wifiWaiting, MinimumValueReceiver.getMinimumIdleMotionPositionDuration() * 60 * 1000);
    }

    public void closeTimer() {
        if (wifiWaiting != null) {
            wifiWaiting.cancel();
            wifiWaiting = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void sendMessage(String message) {
        Intent intent = new Intent();
        intent.setAction("WIFI_DETECTION");
        intent.putExtra("DATA_RECEIVE", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Sensor variables
        SensorManager sensorMan = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMan.registerListener((SensorEventListener) this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        // Register Seekbar value receiver
        final IntentFilter minimumIntent = new IntentFilter();
        minimumIntent.addAction("MINIMUM_ACTION");
        minimumValueReceiver = new MinimumValueReceiver(this);
        registerReceiver(minimumValueReceiver, minimumIntent);

        final IntentFilter maximumIntent = new IntentFilter();
        maximumIntent.addAction("MAXIMUM_ACTION");
        maximumValueReceiver = new MaximumValueReceiver();
        registerReceiver(maximumValueReceiver, maximumIntent);

        final IntentFilter emptyIntent = new IntentFilter();
        emptyIntent.addAction("EMPTY_ACTION");
        emptyValueReceiver = new EmptyValueReceiver();
        registerReceiver(emptyValueReceiver, emptyIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (WifiWaiting.isRegisteredWifi()) {
            unregisterReceiver(wifiScanReceiver);
            WifiWaiting.setRegisteredWifi(false);
            wifiScanReceiver = null;
        }
        unregisterReceiver(minimumValueReceiver);
        unregisterReceiver(maximumValueReceiver);
        unregisterReceiver(emptyValueReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float delta = (float) Math.sqrt(x * x + y * y + z * z);

                if (delta <= THRESHOLD) {
                    wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (!isHoldStation) {
                        Toast.makeText(this, "Device is standing", Toast.LENGTH_LONG).show();
                        isHoldStation = true;
                    }
                    if (timer == null && !isStopScan) {
                        scheduleTimer();
                    }
                    return;
                }
                Toast.makeText(this, "Device is moving", Toast.LENGTH_LONG).show();
                if (WifiWaiting.isRegisteredWifi()) {
                    unregisterReceiver(wifiScanReceiver);
                    WifiWaiting.setRegisteredWifi(false);
                    wifiScanReceiver = null;
                }
                closeTimer();
                isHoldStation = false;
                isStopScan = false;
            }
        } catch (Exception e) {
            sendMessage(e.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public NotificationCompat.Builder createNotificationBuilder(String contentTitle, String contentText, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setAutoCancel(true);
    }
}
