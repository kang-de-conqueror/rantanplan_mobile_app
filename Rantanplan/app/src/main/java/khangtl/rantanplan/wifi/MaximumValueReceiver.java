package khangtl.rantanplan.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import khangtl.rantanplan.activities.MainActivity;

public class MaximumValueReceiver extends BroadcastReceiver {
    public static int MAXIMUM_SCAN_ROUND = MainActivity.MAXIMUM_LIST_VALUE.get(MainActivity.MAXIMUM_LIST_VALUE.size() - 1);

    public static int getMaximumScanRound() {
        return MAXIMUM_SCAN_ROUND;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("MAXIMUM_ACTION")) {
            MAXIMUM_SCAN_ROUND = intent.getIntExtra("MAXIMUM_VALUE", 10);
        }
    }
}
