package khangtl.rantanplan.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import khangtl.rantanplan.activities.MainActivity;

public class EmptyValueReceiver extends BroadcastReceiver {
    public static int EMPTY_HANDED_SCAN_ROUND = MainActivity.DEFAULT_EMPTY;

    public static int getEmptyHandedScanRound() {
        return EMPTY_HANDED_SCAN_ROUND;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("EMPTY_ACTION")) {
            EMPTY_HANDED_SCAN_ROUND = intent.getIntExtra("EMPTY_VALUE", 2);
        }
    }
}
