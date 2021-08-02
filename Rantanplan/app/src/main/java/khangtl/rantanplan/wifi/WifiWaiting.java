package khangtl.rantanplan.wifi;

import android.content.IntentFilter;
import android.net.wifi.WifiManager;

import java.util.Date;
import java.util.TimerTask;

import khangtl.rantanplan.services.BackgroundService;

public class WifiWaiting extends TimerTask {
    private static Date startTime;
    private static boolean isRegisteredWifi = false;
    private BackgroundService backgroundService;

    public static boolean isRegisteredWifi() {
        return isRegisteredWifi;
    }

    public static void setRegisteredWifi(boolean registeredWifi) {
        isRegisteredWifi = registeredWifi;
    }

    public WifiWaiting(BackgroundService backgroundService) {
        this.backgroundService = backgroundService;
    }

    public static Date getStartTime() {
        return startTime;
    }

    @Override
    public void run() {
        backgroundService.sendMessage("Wifi scanning...");
        backgroundService.registerReceiver(backgroundService.getWifiScanReceiver(), new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        isRegisteredWifi = true;
        backgroundService.getWifiManager().startScan();
        startTime = new Date();
    }
}
